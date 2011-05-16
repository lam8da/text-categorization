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
import core.preprocess.util.Constant;
import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.XmlDocument;
import core.preprocess.corpus.Extractor;
import core.preprocess.corpus.reuters.ReutersExtractor;

/**
 * this class provide a tool for the whole procedure of propressing
 * 
 * @author lambda
 * 
 */
public class Preprocessor {
	private int corpusId;
	private int splitting;
	private File inputDir; //where the original corpus data is placed
	private File outputDir; //where the output data should be placed
	private File trainingDir;
	private File xmlDir;
	private File reductionListFile;
	private File orgStatisticalDir;
	private File statisticalDir;
	private int stopperId;
	private int stemmerId;
	private boolean toLower;
	private boolean timeToConst;
	private boolean numToConst;
	private int selectorId;
	private int selectMethodId;

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
			int selectMethodId //
	) throws Exception {
		validate(corpusId, splitting, selectorId, selectMethodId);

		this.inputDir = new File(inputPath);
		this.outputDir = new File(outputPath);
		this.outputDir.mkdirs();

		this.xmlDir = new File(this.outputDir, Constant.XML_DATA_PATH);
		this.xmlDir.mkdirs();
		this.reductionListFile = new File(this.outputDir, Constant.REDUCTIONG_LIST_FILENAME);
		this.trainingDir = new File(this.xmlDir, Constant.TRAINING_FOLDER);
		this.trainingDir.mkdirs();
		this.statisticalDir = new File(this.outputDir, Constant.STATISTICAL_DATA_PATH);
		this.statisticalDir.mkdirs();
		this.orgStatisticalDir = new File(this.outputDir, Constant.ORG_STATISTICAL_DATA_PATH);
		this.orgStatisticalDir.mkdirs();

		this.corpusId = corpusId;
		this.splitting = splitting;
		this.stopperId = stopperId;
		this.stemmerId = stemmerId;
		this.toLower = toLower;
		this.timeToConst = timeToConst;
		this.numToConst = numToConst;
		this.selectorId = selectorId;
		this.selectMethodId = selectMethodId;
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

	private void validate(int corpusId, int splitting, int selectorId, int selectMethodId) throws Exception {
		switch (corpusId) {
		case Constant.REUTERS:
			switch (splitting) {
			case Constant.MOD_APTE:
				break;
			case Constant.MOD_HAYES:
				break;
			case Constant.MOD_LEWIS:
				break;
			default:
				throw new Exception("splitting does not match corpus id!");
			}
			break;
		//case Constant.TWENTY_NEWS_GTOUP:
		//	//not implemented yet
		//	break;
		default:
			throw new Exception("invalid corpus id!");
		}

		switch (selectorId) {
		case Constant.CHI_SELECTOR:
			break;
		case Constant.DF_SELECTOR:
			break;
		case Constant.MI_SELECTOR:
			break;
		case Constant.IG_SELECTOR:
			break;
		default:
			throw new Exception("invalid selector id!");
		}

		switch (selectMethodId) {
		case Constant.FEATURE_SELECTION_MAXSELECTION:
			break;
		case Constant.FEATURE_SELECTION_AVGSELECTION:
			break;
		default:
			throw new Exception("invalid selection method id!");
		}
	}

	private void extraction() throws Exception {
		System.out.println("------------>start extraction!");
		System.out.println("Deleting all files in " + this.xmlDir);
		deleteDirectory(this.xmlDir);

		Extractor extractor = null;
		Stopper stopper = null;
		Stemmer stemmer = null;

		if (this.corpusId == Constant.REUTERS) {
			extractor = new ReutersExtractor(this.inputDir, this.xmlDir, this.splitting);
		}
		//else if (this.corpusId == Constant.TWENTY_NEWS_GTOUP) {
		//	//not implemented yet
		//}

		if (this.stopperId == Constant.USE_STOPPER) {
			stopper = new Stopper();
		}

		if (this.stemmerId == Constant.KROVETZ_STEMMER) {
			stemmer = new KrovetzStemmer();
		}
		else if (this.stemmerId == Constant.PORTER_STEMMER) {
			stemmer = new PorterStemmer();
		}

		extractor.extract(stopper, stemmer, this.toLower, this.timeToConst, this.numToConst);
		System.out.println("<------------extraction done!");
	}

	private void analyzation() throws Exception {
		System.out.println("------------>start analyzing!");
		File[] xmlFiles = this.trainingDir.listFiles(new FileFilter() {
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

		System.out.println("Deleting all files in " + this.orgStatisticalDir);
		deleteDirectory(this.orgStatisticalDir);

		System.out.println("serializing original data...");
		analyzer.serialize(this.orgStatisticalDir);

		System.out.println("<------------analyzing done!");
	}

	private void featureSelection() throws Exception {
		System.out.println("------------>start feature selection!");
		DataAnalyzer analyzer = DataAnalyzer.deserialize(this.orgStatisticalDir, null, false);
		FeatureSelector selector = null;

		switch (selectorId) {
		case Constant.CHI_SELECTOR:
			selector = new CHIFeatrueSelector(analyzer, this.selectMethodId);
			break;
		case Constant.DF_SELECTOR:
			selector = new DFFeatureSelector(analyzer, this.selectMethodId);
			break;
		case Constant.MI_SELECTOR:
			selector = new MIFeatureSelector(analyzer, this.selectMethodId);
			break;
		case Constant.IG_SELECTOR:
			selector = new IGFeatureSelector(analyzer, this.selectMethodId);
			break;
		case Constant.WF_SELECTOR:
			selector = new WFFeatureSelector(analyzer, this.selectMethodId);
			break;
		}

		System.out.println("getting reduction list...");
		int[] eliminatedId = selector.getReductionList();

		System.out.println("writting reduction list to " + this.reductionListFile);
		FileWriter fw = new FileWriter(this.reductionListFile);
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
		FileReader fr = new FileReader(this.reductionListFile);
		BufferedReader br = new BufferedReader(fr);
		int cnt = Integer.parseInt(br.readLine());
		int[] eliminatedId = new int[cnt];
		for (int i = 0; i < cnt; i++) {
			eliminatedId[i] = Integer.parseInt(br.readLine());
			br.readLine();//dummy
		}
		br.close();
		fr.close();
		DataAnalyzer analyzer = DataAnalyzer.deserialize(this.orgStatisticalDir, eliminatedId, true);

		analyzer.serialize(statisticalDir);
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
