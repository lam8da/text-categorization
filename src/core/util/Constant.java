package core.util;

/**
 * this class provide some constants which may be useful in the whole procedure
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
	public static final String ORG_STATISTICAL_DATA_PATH = "original-statistical-data";
	public static final String STATISTICAL_DATA_PATH = "statistical-data";

	//for feature selection
	public static final int FEATURE_SELECTION_MAXSELECTION = 1;
	public static final int FEATURE_SELECTION_AVGSELECTION = 2;
	public static final String REDUCTIONG_LIST_FILENAME = "reductionList.txt";

	//for selector choosing
	public static final int CHI_SELECTOR = 1;
	public static final int DF_SELECTOR = 2;
	public static final int IG_SELECTOR = 3;
	public static final int MI_SELECTOR = 4;
	public static final int WF_SELECTOR = 5;

	//filenames for DataHolder to serialize
	public static final String DOC_LABELS_FILE = "docLabels.dat";
	public static final String FEATURE_CONTAINER_FILE = "featureContainer.dat";
	public static final String FEATURE_CONTAINER_ADDED_PER_DOC_FILE = "featureContainerAddedPerDoc.dat";

	public static final String DOCUMENT_CONTAINERS_FOLDER = "documentContainers";
	public static final String DOCUMENT_CONTAINERS_FOLDER_SIZE_FILE = "documentContainers.meta";

	public static final String LABEL_NAME_CONTAINER_FILE = "labelNameContainer.dat";

	public static final String LABEL_FEATURE_CONTAINERS_FOLDER = "labelFeatureContainers";
	public static final String LABEL_FEATURE_CONTAINERS_FOLDER_SIZE_FILE = "labelFeatureContainers.meta";

	public static final String LABEL_FEATURE_CONTAINERS_ADDED_PER_DOC_FOLDER = "labelFeatureContainersAddedPerDoc";
	public static final String LABEL_FEATURE_CONTAINERS_ADDED_PER_DOC_FOLDER_SIZE_FILE = "labelFeatureContainersAddedPerDoc.meta";

	//preprosessing stage id
	public static final int STAGE_EXTRACTION = 0;
	public static final int STAGE_ANALYZATION = 1;
	public static final int STAGE_FEATURE_SELECTION = 2;
	public static final int STAGE_SERIALIZATION = 3;

	//for generator
	public static final int TRIE_GENERATOR = 1;
	public static final int MAP_GENERATOR = 2;

	//for configurator
	public static final String CONFIG_FILENAME = "config.txt";

	// for classifier selection
	public static final int TWCNB = 1;
	
	// for classifier output
	public static final String TWCNB_FOLDER = "twcnb";
	public static final String TWCNB_CLASS_TERM_WEIGHT_FILE = "ctWeight.dat";
}
