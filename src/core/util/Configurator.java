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
	private int selectMethodId;
	private int generatorId;
	private ContainerGenerator generator;

	private boolean isReady;

	private Configurator() {
		isReady = false;
	}

	public void setValues(//
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
		isReady = false;
		
		this.corpusId = corpusId;
		this.splitting = splitting;
		this.stopperId = stopperId;
		this.stemmerId = stemmerId;
		this.selectorId = selectorId;
		this.selectMethodId = selectMethodId;
		this.generatorId = generatorId;
		validate();

		switch (generatorId) {
		case Constant.TRIE_GENERATOR:
			generator = new TrieGenerator();
			break;
		case Constant.MAP_GENERATOR:
			generator = new MapGenerator();
			break;
		}

		this.inputDir = new File(inputPath);
		this.outputDir = new File(outputPath);

		this.xmlDir = new File(this.outputDir, Constant.XML_DATA_PATH);
		this.reductionListFile = new File(this.outputDir, Constant.REDUCTIONG_LIST_FILENAME);
		this.trainingDir = new File(this.xmlDir, Constant.TRAINING_FOLDER);
		this.testDir = new File(this.xmlDir, Constant.TEST_FOLDER);
		this.statisticalDir = new File(this.outputDir, Constant.STATISTICAL_DATA_PATH);
		this.orgStatisticalDir = new File(this.outputDir, Constant.ORG_STATISTICAL_DATA_PATH);

		this.toLower = toLower;
		this.timeToConst = timeToConst;
		this.numToConst = numToConst;

		//serialize(this.outputDir);
		isReady = true;
	}

	private void validate() throws Exception {
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
		// case Constant.TWENTY_NEWS_GTOUP:
		// //not implemented yet
		// break;
		default:
			throw new Exception("invalid corpus id!");
		}

		switch (stopperId) {
		case Constant.USE_STOPPER:
			break;
		case Constant.NO_STOPPER:
			break;
		default:
			throw new Exception("invalid stopper id!");
		}

		switch (stemmerId) {
		case Constant.PORTER_STEMMER:
			break;
		case Constant.KROVETZ_STEMMER:
			break;
		case Constant.NO_STEMMER:
			break;
		default:
			throw new Exception("invalid stemmer id!");
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
		case Constant.WF_SELECTOR:
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

		switch (generatorId) {
		case Constant.TRIE_GENERATOR:
			break;
		case Constant.MAP_GENERATOR:
			break;
		default:
			throw new Exception("invalid generator id!");
		}
	}

	public void serialize(File outDir) throws Exception {
		// just for recording
		File iniFile = new File(outDir, Constant.CONFIG_FILENAME);
		FileWriter fw = new FileWriter(iniFile);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("inputPath:");
		bw.newLine();
		bw.write(this.inputDir.getPath());
		bw.newLine();

		bw.write("outputPath:");
		bw.newLine();
		bw.write(this.outputDir.getPath());
		bw.newLine();

		bw.write("corpus:");
		bw.newLine();
		switch (corpusId) {
		case Constant.REUTERS:
			bw.write("reuters");
			break;
		case Constant.TWENTY_NEWS_GTOUP:
			bw.write("20-news-group");
			break;
		}
		bw.newLine();

		bw.write("splitting:");
		bw.newLine();
		switch (splitting) {
		case Constant.MOD_APTE:
			bw.write("mod-apte");
			break;
		case Constant.MOD_HAYES:
			bw.write("mod-hayes");
			break;
		case Constant.MOD_LEWIS:
			bw.write("mod-lewis");
			break;
		}
		bw.newLine();

		bw.write("stopper:");
		bw.newLine();
		switch (stopperId) {
		case Constant.USE_STOPPER:
			bw.write("yes");
			break;
		case Constant.NO_STOPPER:
			bw.write("no");
			break;
		}
		bw.newLine();

		bw.write("stemmer:");
		bw.newLine();
		switch (stemmerId) {
		case Constant.PORTER_STEMMER:
			bw.write("porter-stemmer");
			break;
		case Constant.KROVETZ_STEMMER:
			bw.write("krovetz-stemmer");
			break;
		case Constant.NO_STEMMER:
			bw.write("no-stemmer");
			break;
		}
		bw.newLine();

		bw.write("toLower:");
		bw.newLine();
		if (toLower) {
			bw.write("true");
		}
		else bw.write("false");
		bw.newLine();

		bw.write("timeToConst:");
		bw.newLine();
		if (timeToConst) {
			bw.write("true");
		}
		else bw.write("false");
		bw.newLine();

		bw.write("numToConst:");
		bw.newLine();
		if (numToConst) {
			bw.write("true");
		}
		else bw.write("false");
		bw.newLine();

		bw.write("selector:");
		bw.newLine();
		switch (selectorId) {
		case Constant.CHI_SELECTOR:
			bw.write("chi");
			break;
		case Constant.DF_SELECTOR:
			bw.write("df");
			break;
		case Constant.MI_SELECTOR:
			bw.write("mi");
			break;
		case Constant.IG_SELECTOR:
			bw.write("ig");
			break;
		case Constant.WF_SELECTOR:
			bw.write("wf");
			break;
		}
		bw.newLine();

		bw.write("selectMethod:");
		bw.newLine();
		switch (selectMethodId) {
		case Constant.FEATURE_SELECTION_MAXSELECTION:
			bw.write("max");
			break;
		case Constant.FEATURE_SELECTION_AVGSELECTION:
			bw.write("average");
			break;
		}
		bw.newLine();

		bw.write("generator:");
		bw.newLine();
		switch (generatorId) {
		case Constant.TRIE_GENERATOR:
			bw.write("trie");
			break;
		case Constant.MAP_GENERATOR:
			bw.write("map");
			break;
		}
		bw.newLine();

		bw.flush();
		bw.close();
		fw.close();
	}

	public void deserializeFrom(File iniFile) throws Exception {
		isReady = false;

		FileReader fr = new FileReader(iniFile);
		BufferedReader br = new BufferedReader(fr);
		String tmp;

		br.readLine(); // "inputPath:"
		String inputPath = br.readLine();

		br.readLine(); // "outputPath:"
		String outputPath = br.readLine();

		br.readLine(); // "corpus:"
		tmp = br.readLine();
		int corpusId = -1;
		if (tmp.equals("reuters")) {
			corpusId = Constant.REUTERS;
		}
		else if (tmp.equals("20-news-group")) {
			corpusId = Constant.TWENTY_NEWS_GTOUP;
		}

		br.readLine(); // "splitting:"
		tmp = br.readLine();
		int splitting = -1;
		if (tmp.equals("mod-apte")) {
			splitting = Constant.MOD_APTE;
		}
		else if (tmp.equals("mod-hayes")) {
			splitting = Constant.MOD_HAYES;
		}
		else if (tmp.equals("mod-lewis")) {
			splitting = Constant.MOD_LEWIS;
		}

		br.readLine(); // "stopper:"
		tmp = br.readLine();
		int stopperId = -1;
		if (tmp.equals("yes")) {
			stopperId = Constant.USE_STOPPER;
		}
		else if (tmp.equals("no")) {
			stopperId = Constant.NO_STOPPER;
		}

		br.readLine(); // "stemmer:"
		tmp = br.readLine();
		int stemmerId = -1;
		if (tmp.equals("porter-stemmer")) {
			stemmerId = Constant.PORTER_STEMMER;
		}
		else if (tmp.equals("krovetz-stemmer")) {
			stemmerId = Constant.KROVETZ_STEMMER;
		}
		else if (tmp.equals("no-stemmer")) {
			stemmerId = Constant.NO_STEMMER;
		}

		br.readLine(); // "toLower:"
		tmp = br.readLine();
		boolean toLower;
		if (tmp.equals("true")) {
			toLower = true;
		}
		else toLower = false;

		br.readLine(); // "timeToConst:"
		tmp = br.readLine();
		boolean timeToConst;
		if (tmp.equals("true")) {
			timeToConst = true;
		}
		else timeToConst = false;

		br.readLine(); // "numToConst:"
		tmp = br.readLine();
		boolean numToConst;
		if (tmp.equals("true")) {
			numToConst = true;
		}
		else numToConst = false;

		br.readLine(); // "selector:"
		tmp = br.readLine();
		int selectorId = -1;
		if (tmp.equals("chi")) {
			selectorId = Constant.CHI_SELECTOR;
		}
		else if (tmp.equals("df")) {
			selectorId = Constant.DF_SELECTOR;
		}
		else if (tmp.equals("mi")) {
			selectorId = Constant.MI_SELECTOR;
		}
		else if (tmp.equals("ig")) {
			selectorId = Constant.IG_SELECTOR;
		}
		else if (tmp.equals("wf")) {
			selectorId = Constant.WF_SELECTOR;
		}

		br.readLine(); // "selectMethod:"
		tmp = br.readLine();
		int selectMethodId = -1;
		if (tmp.equals("max")) {
			selectMethodId = Constant.FEATURE_SELECTION_MAXSELECTION;
		}
		else if (tmp.equals("average")) {
			selectMethodId = Constant.FEATURE_SELECTION_AVGSELECTION;
		}

		br.readLine(); // "generator:"
		tmp = br.readLine();
		int generatorId = -1;
		if (tmp.equals("trie")) {
			generatorId = Constant.TRIE_GENERATOR;
		}
		else if (tmp.equals("map")) {
			generatorId = Constant.MAP_GENERATOR;
		}

		br.close();
		fr.close();
		this.setValues(//
				inputPath,//
				outputPath,//
				corpusId,//
				splitting,//
				stopperId,//
				stemmerId,//
				toLower,//
				timeToConst,//
				numToConst,//
				selectorId,//
				selectMethodId,//
				generatorId//
		);
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

	public int getSelectMethodId() throws Exception {
		checkReady();
		return selectMethodId;
	}

	public ContainerGenerator getGenerator() throws Exception {
		checkReady();
		return generator;
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
