package core.preprocess;

import java.io.File;

import core.preprocess.extraction.Stemmer;
import core.preprocess.extraction.KrovetzStemmer;
import core.preprocess.extraction.PorterStemmer;
import core.preprocess.selection.Stopper;
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
	private File inputDir;
	private File outputDir;
	private File trainingDir;
	private File xmlDir;
	private File statisticalDir;
	private Stopper stopper;
	private Stemmer stemmer;
	private boolean toLower;
	private boolean timeToConst;
	private boolean numToConst;
	private Extractor extractor;

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
	 * @throws Exception
	 */
	public Preprocessor(
			String inputPath,
			String outputPath,
			int corpusId,
			int splitting,
			int stopperId,
			int stemmerId,
			boolean toLower,
			boolean timeToConst,
			boolean numToConst
		) throws Exception {
		this.corpusId = corpusId;
		this.splitting = splitting;
		
		if (this.corpusId == Constant.REUTERS) {
			if (this.splitting != Constant.MOD_APTE && this.splitting != Constant.MOD_HAYES && this.splitting != Constant.MOD_LEWIS) {
				throw new Exception("splitting does not match corpus id!");
			}
		}
		else if (this.corpusId == Constant.TWENTY_NEWS_GTOUP) {
			// not implemented yet
		}
		else {
			throw new Exception("invalid corpus id!");
		}

		this.inputDir = new File(inputPath);
		this.outputDir = new File(outputPath);
		if (!this.outputDir.exists()) this.outputDir.mkdirs();

		System.out.println("Deleting all files in " + outputDir);
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}

		this.trainingDir = new File(this.outputDir, Constant.TRAINING_FOLDER);
		this.xmlDir = new File(this.outputDir, Constant.XML_DATA_PATH);
		this.statisticalDir = new File(this.outputDir, Constant.STATISTICAL_DATA_PATH);
		this.xmlDir.mkdirs();
		this.statisticalDir.mkdirs();

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
	}

	/**
	 * 
	 * @return true if preprocess complete successfully, false otherwise
	 * @throws Exception
	 */
	public boolean preprocess() throws Exception {
		if (this.corpusId == Constant.REUTERS) {
			this.extractor = new ReutersExtractor(this.inputDir, this.xmlDir, this.splitting);
		}
		else {
			//not implemented yet
			throw new Exception("invalid corpus id!");
		}

		extractor.extract(this.stopper, this.stemmer, this.toLower, this.timeToConst, this.numToConst);
		return true;
	}

	public void printUsage() {

	}
}
