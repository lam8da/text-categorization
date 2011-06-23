package core.preprocess.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.generator.MapGenerator;
import core.preprocess.analyzation.generator.TrieGenerator;

public class Configurator {
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
	private int generatorId;
	private ContainerGenerator generator;

	private boolean isReady = false;

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

		this.toLower = toLower;
		this.timeToConst = timeToConst;
		this.numToConst = numToConst;

		serialize();
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
		//case Constant.TWENTY_NEWS_GTOUP:
		//	//not implemented yet
		//	break;
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

	private void serialize() throws Exception {
		//just for recording
		File iniFile = new File(this.outputDir, Constant.CONFIG_FILENAME);
		FileWriter fw = new FileWriter(iniFile);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("corpusId: ");
		switch (corpusId) {
		case Constant.REUTERS:
			bw.write("reuters");
			break;
		case Constant.TWENTY_NEWS_GTOUP:
			bw.write("20 news group");
			break;
		}
		bw.newLine();

		bw.write("splitting: ");
		switch (splitting) {
		case Constant.MOD_APTE:
			bw.write("mod apte");
			break;
		case Constant.MOD_HAYES:
			bw.write("mod hayes");
			break;
		case Constant.MOD_LEWIS:
			bw.write("mod lewis");
			break;
		}
		bw.newLine();

		bw.write("stopperId: ");
		switch (stopperId) {
		case Constant.USE_STOPPER:
			bw.write("use stopper");
			break;
		case Constant.NO_STOPPER:
			bw.write("no stopper");
			break;
		}
		bw.newLine();

		bw.write("stemmerId: ");
		switch (stemmerId) {
		case Constant.PORTER_STEMMER:
			bw.write("porter stemmer");
			break;
		case Constant.KROVETZ_STEMMER:
			bw.write("krovetz stemmer");
			break;
		case Constant.NO_STEMMER:
			bw.write("no stemmer");
			break;
		}
		bw.newLine();

		bw.write("toLower: ");
		if (toLower) {
			bw.write("true");
		}
		else bw.write("false");
		bw.newLine();

		bw.write("timeToConst: ");
		if (timeToConst) {
			bw.write("true");
		}
		else bw.write("false");
		bw.newLine();

		bw.write("numToConst: ");
		if (numToConst) {
			bw.write("true");
		}
		else bw.write("false");
		bw.newLine();

		bw.write("selectorId: ");
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

		bw.write("selectMethodId: ");
		switch (selectMethodId) {
		case Constant.FEATURE_SELECTION_MAXSELECTION:
			bw.write("max");
			break;
		case Constant.FEATURE_SELECTION_AVGSELECTION:
			bw.write("average");
			break;
		}
		bw.newLine();

		bw.write("generatorId: ");
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
		return inputDir; //where the original corpus data is placed
	}

	public File getOutputDir() throws Exception {
		checkReady();
		return outputDir; //where the output data should be placed
	}

	public File getTrainingDir() throws Exception {
		checkReady();
		return trainingDir;
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
