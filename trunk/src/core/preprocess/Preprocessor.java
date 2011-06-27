package core.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
import core.preprocess.util.Configurator;
import core.preprocess.util.Constant;
import core.preprocess.util.XmlDocument;
import core.preprocess.analyzation.DataAnalyzer;
import core.preprocess.corpus.Extractor;
import core.preprocess.corpus.reuters.ReutersExtractor;

/**
 * this class provide a tool for the whole procedure of propressing
 * 
 * @author lambda
 * 
 */
public class Preprocessor {
	private Configurator config;

	/**
	 * 
	 * @param inputPath
	 *            the directory where the corpus lies
	 * @param outputPath
	 *            the root directory where the output data should be put
	 * @param corpusId
	 * @param splitting
	 * @param stopperId
	 * @param stemmerId
	 * @param toLower
	 *            whether all words in the text should be turned to lower case
	 * @param timeToConst
	 * @param numToConst
	 * @param selectorId
	 * @param selectMethodId
	 * @throws Exception
	 */
	public Preprocessor( //
			String inputPath, //
			String outputPath, //
			int corpusId, //
			int splitting, //
			int stopperId, //
			int stemmerId, //
			boolean toLower, //
			boolean timeToConst, //
			boolean numToConst, //
			int selectorId, //
			int selectMethodId, //
			int generatorId//
	) throws Exception {
		config = Configurator.getConfigurator();
		config.setValues( //
				inputPath, //
				outputPath, //
				corpusId, //
				splitting, //
				stopperId, //
				stemmerId, //
				toLower, //
				timeToConst, //
				numToConst, //
				selectorId, //
				selectMethodId, //
				generatorId //
		);
		config.getOutputDir().mkdirs();
		config.getXmlDir().mkdirs();
		config.getTrainingDir().mkdirs();
		config.getTestDir().mkdirs();
		config.getStatisticalDir().mkdirs();
		config.getOrgStatisticalDir().mkdirs();
	}

	private void deleteDirectory(File dir) throws IOException {
		if ((dir == null) || !dir.isDirectory()) {
			throw new IllegalArgumentException("Argument " + dir + " is not a directory. ");
		}
		File[] entries = dir.listFiles();

		int sz = entries.length;
		for (int i = 0; i < sz; i++) {
			if (entries[i].isDirectory()) {
				deleteDirectory(entries[i]);
			}
			entries[i].delete();
		}
	}

	private void extraction() throws Exception {
		System.out.println("------------>start extraction!");
		System.out.println("Deleting all files in " + config.getXmlDir());
		deleteDirectory(config.getXmlDir());

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
		deleteDirectory(config.getOrgStatisticalDir());

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
			selector = new CHIFeatrueSelector(analyzer, config.getSelectMethodId());
			break;
		case Constant.DF_SELECTOR:
			selector = new DFFeatureSelector(analyzer, config.getSelectMethodId());
			break;
		case Constant.MI_SELECTOR:
			selector = new MIFeatureSelector(analyzer, config.getSelectMethodId());
			break;
		case Constant.IG_SELECTOR:
			selector = new IGFeatureSelector(analyzer, config.getSelectMethodId());
			break;
		case Constant.WF_SELECTOR:
			selector = new WFFeatureSelector(analyzer, config.getSelectMethodId());
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

	public void printUsage() {

	}
}
