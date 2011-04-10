package core.preprocess.util;

/**
 * this class provide some constants which may be useful in the procedure of
 * preprocessing
 * 
 * @author lambda
 * 
 */
public final class Constant {
	//constant strings
	public static final String LINE_SEPARATOR = "\r\n";
	public static final String WORD_SEPARATOR = " ";
	public static final String WORD_SEPARATING_PATTERN = "\\s+";
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String EMPTY_LABEL = "EMPTY_LABEL-1e3df054";

	//training, test and other folder name
	public static final String TRAINING_FOLDER = "training";
	public static final String TEST_FOLDER = "test";
	public static final String EXTRACTION_METADATA_FILENAME = "meta.txt";

	//for corpus choosing
	public static final int REUTERS = 1;
	public static final int TWENTY_NEWS_GTOUP = 2;

	//for determining the usage of each document
	public static final int TRAINING = 0;
	public static final int TEST = 1;
	public static final int FIRED = 2;

	//for choosing training and test set of reuters corpus
	public static final int MOD_LEWIS = 1;
	public static final int MOD_APTE = 2;
	public static final int MOD_HAYES = 3;

	//for choosing training and test set of 20newsgroup corpus
	//each of these value should be different with all the above values of reuters splitting!
	//public static final int ???

	//for stemmer choosing
	public static final int PORTER_STEMMER = 1;
	public static final int KROVETZ_STEMMER = 2;
	public static final int NO_STEMMER = 3;

	//for stopper choosing
	public static final int USE_STOPPER = 1;
	public static final int NO_STOPPER = 2;

	//output path names
	public static final String XML_DATA_PATH = "xml-data";
	public static final String STATISTICAL_DATA_PATH = "statistical-data";
}