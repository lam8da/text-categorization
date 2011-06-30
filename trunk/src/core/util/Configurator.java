package core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.generator.MapGenerator;
import core.preprocess.analyzation.generator.TrieGenerator;

public class Configurator {
	//for preprocessing
	private int corpusId;
	private int splitting;

	private File inputDir; // where the original corpus data is placed
	private File outputDir; // where the output data should be placed
	private File trainingDir;
	private File testDir;
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
	private int thresholdMethodId;
	private double chiThres;
	private double dfThres;
	private double igThres;
	private double miThres;
	private double wfThres;

	private int selectMethodId;
	private int generatorId;
	private ContainerGenerator generator;

	//for classifier
	private int classifierId;
	private File twcnbFolder;
	private File svmFolder;
	private File knnFolder;

	private boolean isReady;

	private Configurator() {
		isReady = false;
	}

	public void serialize(File outDir) throws Exception {
		// just for recording
		File iniFile = new File(outDir, Constant.CONFIG_FILENAME);
		FileWriter fw = new FileWriter(iniFile);
		BufferedWriter bw = new BufferedWriter(fw);

		/*-------------------------------------------------------------------------------*/

		bw.write("inputPath:");
		bw.newLine();
		bw.write(this.inputDir.getPath());
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("outputPath:");
		bw.newLine();
		bw.write(this.outputDir.getPath());
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("corpus:");
		bw.newLine();
		switch (corpusId) {
		case Constant.REUTERS:
			bw.write(Constant.REUTERS_STR);
			bw.newLine();
			bw.write("splitting:");
			bw.newLine();

			switch (splitting) {
			case Constant.MOD_APTE:
				bw.write(Constant.MOD_APTE_STR);
				break;
			case Constant.MOD_HAYES:
				bw.write(Constant.MOD_HAYES_STR);
				break;
			case Constant.MOD_LEWIS:
				bw.write(Constant.MOD_LEWIS_STR);
				break;
			}
			break;
		//case Constant.TWENTY_NEWS_GTOUP:
		//	bw.write(Constant.TWENTY_NEWS_GTOUP_STR);
		//	bw.newLine();
		//	bw.write("splitting:");
		//	bw.newLine();
		//	
		//	switch (splitting) {
		//	case Constant.???
		//		bw.write(???);
		//		break;
		//	}
		//	break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("stopper:");
		bw.newLine();
		switch (stopperId) {
		case Constant.USE_STOPPER:
			bw.write(Constant.USE_STOPPER_STR);
			break;
		case Constant.NO_STOPPER:
			bw.write(Constant.NO_STOPPER_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("stemmer:");
		bw.newLine();
		switch (stemmerId) {
		case Constant.PORTER_STEMMER:
			bw.write(Constant.PORTER_STEMMER_STR);
			break;
		case Constant.KROVETZ_STEMMER:
			bw.write(Constant.KROVETZ_STEMMER_STR);
			break;
		case Constant.NO_STEMMER:
			bw.write(Constant.NO_STEMMER_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("toLower:");
		bw.newLine();
		if (toLower) {
			bw.write(Constant.TRUE_STR);
		}
		else bw.write(Constant.FALSE_STR);
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("timeToConst:");
		bw.newLine();
		if (timeToConst) {
			bw.write(Constant.TRUE_STR);
		}
		else bw.write(Constant.FALSE_STR);
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("numToConst:");
		bw.newLine();
		if (numToConst) {
			bw.write(Constant.TRUE_STR);
		}
		else bw.write(Constant.FALSE_STR);
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("selector:");
		bw.newLine();
		switch (selectorId) {
		case Constant.CHI_SELECTOR:
			bw.write(Constant.CHI_SELECTOR_STR);
			break;
		case Constant.DF_SELECTOR:
			bw.write(Constant.DF_SELECTOR_STR);
			break;
		case Constant.MI_SELECTOR:
			bw.write(Constant.MI_SELECTOR_STR);
			break;
		case Constant.IG_SELECTOR:
			bw.write(Constant.IG_SELECTOR_STR);
			break;
		case Constant.WF_SELECTOR:
			bw.write(Constant.WF_SELECTOR_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("threshold-selection:");
		bw.newLine();
		switch (thresholdMethodId) {
		case Constant.THRESHOLD_K_MEANS:
			bw.write(Constant.THRESHOLD_K_MEANS_STR);
			break;
		case Constant.THRESHOLD_MANUAL:
			bw.write(Constant.THRESHOLD_MANUAL_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write(Constant.THRESHOLD_CHI_STR);
		bw.newLine();
		bw.write(String.valueOf(chiThres));
		bw.newLine();

		bw.write(Constant.THRESHOLD_DF_STR);
		bw.newLine();
		bw.write(String.valueOf(dfThres));
		bw.newLine();

		bw.write(Constant.THRESHOLD_IG_STR);
		bw.newLine();
		bw.write(String.valueOf(igThres));
		bw.newLine();

		bw.write(Constant.THRESHOLD_MI_STR);
		bw.newLine();
		bw.write(String.valueOf(miThres));
		bw.newLine();

		bw.write(Constant.THRESHOLD_WF_STR);
		bw.newLine();
		bw.write(String.valueOf(wfThres));
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("selectMethod:");
		bw.newLine();
		switch (selectMethodId) {
		case Constant.FEATURE_SELECTION_MAXSELECTION:
			bw.write(Constant.FEATURE_SELECTION_MAXSELECTION_STR);
			break;
		case Constant.FEATURE_SELECTION_AVGSELECTION:
			bw.write(Constant.FEATURE_SELECTION_AVGSELECTION_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("generator:");
		bw.newLine();
		switch (generatorId) {
		case Constant.TRIE_GENERATOR:
			bw.write(Constant.TRIE_GENERATOR_STR);
			break;
		case Constant.MAP_GENERATOR:
			bw.write(Constant.MAP_GENERATOR_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.write("classifier:");
		bw.newLine();
		switch (classifierId) {
		case Constant.TWCNB:
			bw.write(Constant.TWCNB_STR);
			break;
		case Constant.SVM:
			bw.write(Constant.SVM_STR);
			break;
		case Constant.KNN:
			bw.write(Constant.KNN_STR);
			break;
		}
		bw.newLine();

		/*-------------------------------------------------------------------------------*/

		bw.flush();
		bw.close();
		fw.close();
	}

	public void deserializeFrom(File iniFile) throws Exception {
		isReady = false;

		FileReader fr = new FileReader(iniFile);
		BufferedReader br = new BufferedReader(fr);
		String tmp1, tmp2;

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "inputPath:"
		String inputPath = br.readLine();

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "outputPath:"
		String outputPath = br.readLine();

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "corpus:"
		tmp1 = br.readLine();
		br.readLine(); // "splitting:"
		tmp2 = br.readLine();
		this.corpusId = -1;
		this.splitting = -1;

		if (tmp1.equals(Constant.REUTERS_STR)) {
			corpusId = Constant.REUTERS;
			if (tmp2.equals(Constant.MOD_APTE_STR)) {
				splitting = Constant.MOD_APTE;
			}
			else if (tmp2.equals(Constant.MOD_HAYES_STR)) {
				splitting = Constant.MOD_HAYES;
			}
			else if (tmp2.equals(Constant.MOD_LEWIS_STR)) {
				splitting = Constant.MOD_LEWIS;
			}
			else throw new Exception("splitting does not match corpus id!");
		}
		//else if (tmp1.equals(Constant.TWENTY_NEWS_GTOUP_STR)) {
		//	corpusId = Constant.TWENTY_NEWS_GTOUP; // not implemented yet
		//}
		else throw new Exception("invalid corpus id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "stopper:"
		tmp1 = br.readLine();
		this.stopperId = -1;
		if (tmp1.equals(Constant.USE_STOPPER_STR)) {
			stopperId = Constant.USE_STOPPER;
		}
		else if (tmp1.equals(Constant.NO_STOPPER_STR)) {
			stopperId = Constant.NO_STOPPER;
		}
		else throw new Exception("invalid stopper id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "stemmer:"
		tmp1 = br.readLine();
		this.stemmerId = -1;
		if (tmp1.equals(Constant.PORTER_STEMMER_STR)) {
			stemmerId = Constant.PORTER_STEMMER;
		}
		else if (tmp1.equals(Constant.KROVETZ_STEMMER_STR)) {
			stemmerId = Constant.KROVETZ_STEMMER;
		}
		else if (tmp1.equals(Constant.NO_STEMMER_STR)) {
			stemmerId = Constant.NO_STEMMER;
		}
		else throw new Exception("invalid stemmer id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "toLower:"
		tmp1 = br.readLine();
		if (tmp1.equals(Constant.TRUE_STR)) {
			this.toLower = true;
		}
		else if (tmp1.equals(Constant.FALSE_STR)) {
			this.toLower = false;
		}
		else throw new Exception("invalid value for toLower!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "timeToConst:"
		tmp1 = br.readLine();
		if (tmp1.equals(Constant.TRUE_STR)) {
			this.timeToConst = true;
		}
		else if (tmp1.equals(Constant.FALSE_STR)) {
			this.timeToConst = false;
		}
		else throw new Exception("invalid value for timeToConst!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "numToConst:"
		tmp1 = br.readLine();
		if (tmp1.equals(Constant.TRUE_STR)) {
			this.numToConst = true;
		}
		else if (tmp1.equals(Constant.FALSE_STR)) {
			this.numToConst = false;
		}
		else throw new Exception("invalid value for numToConst!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "selector:"
		tmp1 = br.readLine();
		this.selectorId = -1;
		if (tmp1.equals(Constant.CHI_SELECTOR_STR)) {
			selectorId = Constant.CHI_SELECTOR;
		}
		else if (tmp1.equals(Constant.DF_SELECTOR_STR)) {
			selectorId = Constant.DF_SELECTOR;
		}
		else if (tmp1.equals(Constant.MI_SELECTOR_STR)) {
			selectorId = Constant.MI_SELECTOR;
		}
		else if (tmp1.equals(Constant.IG_SELECTOR_STR)) {
			selectorId = Constant.IG_SELECTOR;
		}
		else if (tmp1.equals(Constant.WF_SELECTOR_STR)) {
			selectorId = Constant.WF_SELECTOR;
		}
		else throw new Exception("invalid selector id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "threshold-selection:"
		tmp1 = br.readLine();
		this.thresholdMethodId = -1;
		if (tmp1.equals(Constant.THRESHOLD_K_MEANS_STR)) {
			thresholdMethodId = Constant.THRESHOLD_K_MEANS;
		}
		else if (tmp1.equals(Constant.THRESHOLD_MANUAL_STR)) {
			thresholdMethodId = Constant.THRESHOLD_MANUAL;
		}
		else throw new Exception("invalid threshold method id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "chi-thres:"
		chiThres = Double.valueOf(br.readLine());

		br.readLine(); // "df-thres:"
		dfThres = Double.valueOf(br.readLine());

		br.readLine(); // "ig-thres:"
		igThres = Double.valueOf(br.readLine());

		br.readLine(); // "mi-thres:"
		miThres = Double.valueOf(br.readLine());

		br.readLine(); // "wf-thres:"
		wfThres = Double.valueOf(br.readLine());

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "selectMethod:"
		tmp1 = br.readLine();
		this.selectMethodId = -1;
		if (tmp1.equals(Constant.FEATURE_SELECTION_MAXSELECTION_STR)) {
			selectMethodId = Constant.FEATURE_SELECTION_MAXSELECTION;
		}
		else if (tmp1.equals(Constant.FEATURE_SELECTION_AVGSELECTION_STR)) {
			selectMethodId = Constant.FEATURE_SELECTION_AVGSELECTION;
		}
		else throw new Exception("invalid selection method id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "generator:"
		tmp1 = br.readLine();
		this.generatorId = -1;
		if (tmp1.equals(Constant.TRIE_GENERATOR_STR)) {
			generatorId = Constant.TRIE_GENERATOR;
			generator = new TrieGenerator();
		}
		else if (tmp1.equals(Constant.MAP_GENERATOR_STR)) {
			generatorId = Constant.MAP_GENERATOR;
			generator = new MapGenerator();
		}
		else throw new Exception("invalid generator id!");

		/*-------------------------------------------------------------------------------*/

		br.readLine(); // "classifier:"
		tmp1 = br.readLine();
		this.classifierId = -1;
		if (tmp1.equals(Constant.TWCNB_STR)) {
			classifierId = Constant.TWCNB;
		}
		else if (tmp1.equals(Constant.SVM_STR)) {
			classifierId = Constant.SVM;
		}
		else if (tmp1.equals(Constant.KNN_STR)) {
			classifierId = Constant.KNN;
		}
		else throw new Exception("invalid classifier id!");

		/*-------------------------------------------------------------------------------*/

		br.close();
		fr.close();

		this.inputDir = new File(inputPath);
		this.outputDir = new File(outputPath);

		this.xmlDir = new File(this.outputDir, Constant.XML_DATA_PATH);
		this.reductionListFile = new File(this.outputDir, Constant.REDUCTIONG_LIST_FILENAME);
		this.trainingDir = new File(this.xmlDir, Constant.TRAINING_FOLDER);
		this.testDir = new File(this.xmlDir, Constant.TEST_FOLDER);
		this.statisticalDir = new File(this.outputDir, Constant.STATISTICAL_DATA_PATH);
		this.orgStatisticalDir = new File(this.outputDir, Constant.ORG_STATISTICAL_DATA_PATH);

		this.twcnbFolder = new File(this.outputDir, Constant.TWCNB_FOLDER);
		this.svmFolder = new File(this.outputDir, Constant.SVM_FOLDER);
		this.knnFolder = new File(this.outputDir, Constant.KNN_FOLDER);

		isReady = true;
	}

	private void checkReady() throws Exception {
		if (isReady == false) {
			throw new Exception("the values haven't been set!");
		}
	}

	public int getCorpusId() throws Exception {
		checkReady();
		return corpusId;
	}

	public int getSplitting() throws Exception {
		checkReady();
		return splitting;
	}

	public File getInputDir() throws Exception {
		checkReady();
		return inputDir; // where the original corpus data is placed
	}

	public File getOutputDir() throws Exception {
		checkReady();
		return outputDir; // where the output data should be placed
	}

	public File getTrainingDir() throws Exception {
		checkReady();
		return trainingDir;
	}

	public File getTestDir() throws Exception {
		checkReady();
		return testDir;
	}

	public File getXmlDir() throws Exception {
		checkReady();
		return xmlDir;
	}

	public File getReductionListFile() throws Exception {
		checkReady();
		return reductionListFile;
	}

	public File getOrgStatisticalDir() throws Exception {
		checkReady();
		return orgStatisticalDir;
	}

	public File getStatisticalDir() throws Exception {
		checkReady();
		return statisticalDir;
	}

	public int getStopperId() throws Exception {
		checkReady();
		return stopperId;
	}

	public int getStemmerId() throws Exception {
		checkReady();
		return stemmerId;
	}

	public boolean getToLower() throws Exception {
		checkReady();
		return toLower;
	}

	public boolean getTimeToConst() throws Exception {
		checkReady();
		return timeToConst;
	}

	public boolean getNumToConst() throws Exception {
		checkReady();
		return numToConst;
	}

	public int getSelectorId() throws Exception {
		checkReady();
		return selectorId;
	}

	public int getThresholdMethodId() throws Exception {
		checkReady();
		return thresholdMethodId;
	}

	public double getChiThres() throws Exception {
		checkReady();
		return chiThres;
	}

	public double getDfThres() throws Exception {
		checkReady();
		return dfThres;
	}

	public double getIgThres() throws Exception {
		checkReady();
		return igThres;
	}

	public double getMiThres() throws Exception {
		checkReady();
		return miThres;
	}

	public double getWfThres() throws Exception {
		checkReady();
		return wfThres;
	}

	public int getSelectMethodId() throws Exception {
		checkReady();
		return selectMethodId;
	}

	public ContainerGenerator getGenerator() throws Exception {
		checkReady();
		return generator;
	}

	public int getClassifierId() throws Exception {
		checkReady();
		return classifierId;
	}

	public File getTwcnbFolder() throws Exception {
		checkReady();
		return twcnbFolder;
	}

	public File getSvmFolder() throws Exception {
		checkReady();
		return svmFolder;
	}

	public File getKnnFolder() throws Exception {
		checkReady();
		return knnFolder;
	}

	/*****************************************************************************/

	private static volatile Configurator config = null;

	public static Configurator getConfigurator() {
		if (config == null) {
			synchronized (Configurator.class) {
				if (config == null) {
					config = new Configurator();
				}
			}
		}
		return config;
	}
}
