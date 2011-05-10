package core.preprocess;

import java.io.File;
import java.io.FileFilter;

import core.preprocess.extraction.Stemmer;
import core.preprocess.extraction.KrovetzStemmer;
import core.preprocess.extraction.PorterStemmer;
import core.preprocess.selection.Stopper;
import core.preprocess.selection.FeatureSelector;
import core.preprocess.selection.CHIFeatrueSelector;
import core.preprocess.selection.DFFeatureSelector;
import core.preprocess.selection.IGFeatureSelector;
import core.preprocess.selection.MIFeatureSelector;
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
	private File statisticalDir;
	private Stopper stopper;
	private Stemmer stemmer;
	private boolean toLower;
	private boolean timeToConst;
	private boolean numToConst;
	private int selectorId;
	private int selectMethodId;
	private Extractor extractor;
	private DataAnalyzer analyzer;
	private FeatureSelector selector;

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
	 *            TODO
	 * @param selectMethodId
	 *            TODO
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
		this.trainingDir = new File(this.outputDir, Constant.TRAINING_FOLDER);
		this.trainingDir.mkdirs();
		this.xmlDir = new File(this.outputDir, Constant.XML_DATA_PATH);
		this.xmlDir.mkdirs();
		this.statisticalDir = new File(this.outputDir, Constant.STATISTICAL_DATA_PATH);
		this.statisticalDir.mkdirs();

		System.out.println("Deleting all files in " + outputDir);
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}

		this.corpusId = corpusId;
		this.splitting = splitting;

		if (stopperId == Constant.USE_STOPPER) {
			stopper = new Stopper();
		}
		else stopper = null;

		if (stemmerId == Constant.KROVETZ_STEMMER) {
			stemmer = new KrovetzStemmer();
		}
		else if (stemmerId == Constant.PORTER_STEMMER) {
			stemmer = new PorterStemmer();
		}
		else stemmer = null;

		this.toLower = toLower;
		this.timeToConst = timeToConst;
		this.numToConst = numToConst;
		this.selectorId = selectorId;
		this.selectMethodId = selectMethodId;
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

	/**
	 * 
	 * @return true if preprocess complete successfully, false otherwise
	 * @throws Exception
	 */
	public void preprocess() throws Exception {
		//----------------------------- start extracting -----------------------------
		if (this.corpusId == Constant.REUTERS) {
			this.extractor = new ReutersExtractor(this.inputDir, this.xmlDir, this.splitting);
		}
		//else if (this.corpusId == Constant.TWENTY_NEWS_GTOUP) {
		//	//not implemented yet
		//}
		extractor.extract(this.stopper, this.stemmer, this.toLower, this.timeToConst, this.numToConst);

		//----------------------------- start analyzing -----------------------------
		File[] xmlFiles = this.trainingDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		System.out.println("training file cnt: " + xmlFiles.length);

		XmlDocument xml = new XmlDocument();
		this.analyzer = new DataAnalyzer();
		for (int i = 0; i < xmlFiles.length; i++) {
			//System.out.println(xmlFiles[i].getName());
			xml.parseDocument(xmlFiles[i]);
			this.analyzer.addDocument(xml.getLabels(), xml.getTitleFeatures(), xml.getContentFeatures());
		}
		this.analyzer.accomplishAdding();

		//------------------------- start feature selecting -------------------------		
		switch (selectorId) {
		case Constant.CHI_SELECTOR:
			this.selector = new CHIFeatrueSelector(this.analyzer, this.selectMethodId);
			break;
		case Constant.DF_SELECTOR:
			this.selector = new DFFeatureSelector(this.analyzer, this.selectMethodId);
			break;
		case Constant.MI_SELECTOR:
			this.selector = new MIFeatureSelector(this.analyzer, this.selectMethodId);
			break;
		case Constant.IG_SELECTOR:
			this.selector = new IGFeatureSelector(this.analyzer, this.selectMethodId);
			break;
		}
		this.selector.featureReduction();

		//-------------------------- serialize the result  --------------------------
		this.analyzer.serialize(statisticalDir);
	}

	public void printUsage() {

	}
}
