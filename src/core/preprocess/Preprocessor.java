package core.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;

import core.preprocess.extraction.Stemmer;
import core.preprocess.extraction.KrovetzStemmer;
import core.preprocess.extraction.PorterStemmer;
import core.preprocess.selection.Stopper;
import core.preprocess.selection.FeatureSelector;
import core.preprocess.selection.CHIFeatrueSelector;
import core.preprocess.selection.DFFeatureSelector;
import core.preprocess.selection.IGFeatureSelector;
import core.preprocess.selection.MIFeatureSelector;
import core.preprocess.selection.WFFeatureSelector;
import core.preprocess.util.XmlDocument;
import core.preprocess.analyzation.DataAnalyzer;
import core.preprocess.corpus.Extractor;
import core.preprocess.corpus.reuters.ReutersExtractor;
import core.util.Configurator;
import core.util.Constant;
import core.util.UtilityFuncs;

/**
 * this class provide a tool for the whole procedure of propressing
 * 
 * @author lambda
 * 
 */
public class Preprocessor {
	private Configurator config;

	public Preprocessor() throws Exception {
		config = Configurator.getConfigurator();
		mkdirs();
	}

	public Preprocessor(File configFile) throws Exception {
		config = Configurator.getConfigurator();
		config.deserializeFrom(configFile);
		mkdirs();
	}

	private void mkdirs() throws Exception {
		// this method must be invoked after the configurator is initialized
		config.getOutputDir().mkdirs();
		config.serialize(config.getOutputDir());

		config.getXmlDir().mkdirs();
		config.getTrainingDir().mkdirs();
		config.getTestDir().mkdirs();
		config.getStatisticalDir().mkdirs();
		config.getOrgStatisticalDir().mkdirs();
	}

	private void extraction() throws Exception {
		System.out.println("------------>start extraction!");

		System.out.println("Deleting all files in " + config.getTrainingDir());
		UtilityFuncs.deleteDirectory(config.getTrainingDir());
		System.out.println("Deleting all files in " + config.getTestDir());
		UtilityFuncs.deleteDirectory(config.getTestDir());

		Extractor extractor = null;
		Stopper stopper = null;
		Stemmer stemmer = null;

		if (config.getCorpusId() == Constant.REUTERS) {
			extractor = new ReutersExtractor(config.getInputDir(), config.getXmlDir(), config.getSplitting());
		}
		//else if (config.getcorpusId() == Constant.TWENTY_NEWS_GTOUP) {
		//	//not implemented yet
		//}

		if (config.getStopperId() == Constant.USE_STOPPER) {
			stopper = new Stopper();
		}

		if (config.getStemmerId() == Constant.KROVETZ_STEMMER) {
			stemmer = new KrovetzStemmer();
		}
		else if (config.getStemmerId() == Constant.PORTER_STEMMER) {
			stemmer = new PorterStemmer();
		}

		System.out.println("extracting...");
		extractor.extract(stopper, stemmer, config.getToLower(), config.getTimeToConst(), config.getNumToConst());
		System.out.println("<------------extraction done!");
	}

	private void analyzation() throws Exception {
		System.out.println("------------>start analyzing!");
		File[] xmlFiles = config.getTrainingDir().listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		System.out.println("training file cnt: " + xmlFiles.length);

		XmlDocument xml = new XmlDocument();
		DataAnalyzer analyzer = new DataAnalyzer();

		System.out.print("passing documents: ");
		for (int i = 0; i < xmlFiles.length; i++) {
			if ((i & 2047) == 0) System.out.println();
			if ((i & 255) == 0) System.out.print(i + ",... ");
			xml.parseDocument(xmlFiles[i]);
			analyzer.addDocument(xml.getLabels(), xml.getTitleFeatures(), xml.getContentFeatures());
		}
		System.out.println();

		System.out.println("Deleting all files in " + config.getOrgStatisticalDir());
		UtilityFuncs.deleteDirectory(config.getOrgStatisticalDir());

		System.out.println("serializing original data...");
		analyzer.serialize(config.getOrgStatisticalDir());

		System.out.println("<------------analyzing done!");
	}

	private void featureSelection() throws Exception {
		System.out.println("------------>start feature selection!");
		DataAnalyzer analyzer = DataAnalyzer.deserialize(config.getOrgStatisticalDir(), null, false);
		FeatureSelector selector = null;

		switch (config.getSelectorId()) {
		case Constant.CHI_SELECTOR:
			System.out.println("using chi selector.");
			selector = new CHIFeatrueSelector(analyzer);
			break;
		case Constant.DF_SELECTOR:
			System.out.println("using df selector.");
			selector = new DFFeatureSelector(analyzer);
			break;
		case Constant.MI_SELECTOR:
			System.out.println("using mi selector.");
			selector = new MIFeatureSelector(analyzer);
			break;
		case Constant.IG_SELECTOR:
			System.out.println("using ig selector.");
			selector = new IGFeatureSelector(analyzer);
			break;
		case Constant.WF_SELECTOR:
			System.out.println("using wf selector.");
			selector = new WFFeatureSelector(analyzer);
			break;
		}

		System.out.println("getting reduction list...");
		int[] eliminatedId = selector.getReductionList();

		System.out.println("writting reduction list to " + config.getReductionListFile());
		FileWriter fw = new FileWriter(config.getReductionListFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(String.valueOf(eliminatedId.length));
		bw.newLine();
		for (int i = 0; i < eliminatedId.length; i++) {
			bw.write(String.valueOf(eliminatedId[i]));
			bw.newLine();

			String fea = analyzer.getFeature(eliminatedId[i]);
			bw.write(String.valueOf(analyzer.getW_tk(fea)) + "\t" + fea);
			bw.newLine();
		}
		bw.flush();
		bw.close();
		fw.close();

		//analyzer = null;
		//System.gc();
		System.out.println("<------------feature selection done!");
	}

	private void serialization() throws Exception {
		System.out.println("------------>start serialization!");

		System.out.println("deserializing and reducing data...");
		FileReader fr = new FileReader(config.getReductionListFile());
		BufferedReader br = new BufferedReader(fr);
		int cnt = Integer.parseInt(br.readLine());
		int[] eliminatedId = new int[cnt];
		for (int i = 0; i < cnt; i++) {
			eliminatedId[i] = Integer.parseInt(br.readLine());
			br.readLine();//dummy
		}
		br.close();
		fr.close();
		DataAnalyzer analyzer = DataAnalyzer.deserialize(config.getOrgStatisticalDir(), eliminatedId, true);

		System.out.println("Deleting all files in " + config.getStatisticalDir());
		UtilityFuncs.deleteDirectory(config.getStatisticalDir());

		analyzer.serialize(config.getStatisticalDir());
		System.out.println("<------------serialization done!");
	}

	/**
	 * 
	 * @param startingStageId
	 *            the stage id from which the preprocessing procedure should
	 *            start
	 * @return true if preprocess complete successfully, false otherwise
	 * @throws Exception
	 */
	public void preprocess(int startingStageId, int endingStageId) throws Exception {
		//----------------------------- start extracting -----------------------------
		if (startingStageId <= Constant.STAGE_EXTRACTION && endingStageId >= Constant.STAGE_EXTRACTION) {
			extraction();
			System.out.println();
		}

		//----------------------------- start analyzing -----------------------------
		if (startingStageId <= Constant.STAGE_ANALYZATION && endingStageId >= Constant.STAGE_ANALYZATION) {
			analyzation();
			System.out.println();
		}

		//------------------------- start feature selecting -------------------------
		if (startingStageId <= Constant.STAGE_FEATURE_SELECTION && endingStageId >= Constant.STAGE_FEATURE_SELECTION) {
			featureSelection();
			System.out.println();
		}

		//-------------------------- serialize the result  --------------------------
		if (startingStageId <= Constant.STAGE_SERIALIZATION && endingStageId >= Constant.STAGE_SERIALIZATION) {
			serialization();
			System.out.println();
		}
	}
}
