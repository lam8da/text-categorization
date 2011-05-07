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
	public static final String EMPTY_LABEL = "\\empty-1e3df054";
	public static final String NUMBER_FEATURE = "\\num-4y0xk482";
	public static final String TIME_FEATURE = "\\time-7p1uc189";

	//training, test and other folder name
	public static final String TRAINING_FOLDER = "training";
	public static final String TEST_FOLDER = "test";
	public static final String EXTRACTION_METADATA_FILENAME = "meta.txt";

	//for corpus selection
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

	//filenames for DataAnalyzer to output result
	/************************************ meta data ************************************/
	public static final String DocLabels = "DocLabels.dat";
	public static final String FeatureId = "FeatureId.dat";
	public static final String Feature = "Feature.dat";
	public static final String LabelId = "LabelId.dat";
	public static final String Label = "Label.dat";
	
	/******************************** document counting ********************************/
	public static final String N = "N.dat";
	public static final String N_ci = "N_ci.dat";
	public static final String N_not_ci = "N_not_ci.dat";
	public static final String N_tk = "N_tk.dat";
	public static final String N_exclude_tk = "N_exclude_tk.dat";
	public static final String N_ci_tk = "N_ci_tk.dat";
	public static final String N_not_ci_tk = "N_not_ci_tk.dat";
	public static final String N_ci_exclude_tk = "N_ci_exclude_tk.dat";
	public static final String N_not_ci_exclude_tk = "N_not_ci_exclude_tk.dat";
	
	/********************************** word counting **********************************/
	public static final String W = "W.dat";
	public static final String W_ci = "W_ci.dat";
	public static final String W_not_ci = "W_not_ci.dat";
	public static final String W_dj = "W_dj.dat";
	public static final String W_not_dj = "W_not_dj.dat";
	
	/*************************** single feature word counting ***************************/
	public static final String W_tk = "W_tk.dat";
	public static final String W_ci_tk = "W_ci_tk.dat";
	public static final String W_not_ci_tk = "W_not_ci_tk.dat";
	public static final String W_dj_tk = "W_dj_tk.dat";
	public static final String W_not_dj_tk = "W_not_dj_tk.dat";
	
	/********************************* feature counting *********************************/
	public static final String V = "V.dat";
	public static final String V_ci = "V_ci.dat";
	public static final String V_ci_exclude = "V_ci_exclude.dat";
	public static final String V_not_ci = "V_not_ci.dat";
	public static final String V_not_ci_exclude = "V_not_ci_exclude.dat";
	public static final String V_dj = "V_dj.dat";
	public static final String V_dj_exclude = "V_dj_exclude.dat";
	public static final String V_not_dj = "V_not_dj.dat";
	public static final String V_not_dj_exclude = "V_not_dj_exclude.dat";
	
	/********************************** label counting **********************************/
	public static final String M = "M.dat";
	public static final String M_tk = "M_tk.dat";
	public static final String M_exclude_tk = "M_exclude_tk.dat";
}
