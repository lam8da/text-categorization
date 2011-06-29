package core.classifier;

import java.io.File;

import core.classifier.twcnb.TWCNBayes;
import core.classifier.util.Classifier;
import core.classifier.util.FinalDataHolder;
import core.util.Configurator;
import core.util.Constant;

public class ClassifierTrainer {
	private FinalDataHolder dataHolder;

	/**
	 * constructor
	 * 
	 * @param inputDir
	 *            the output dir of preprocessor (or the folder containing the
	 *            configuration file)
	 * @throws Exception
	 */
	public ClassifierTrainer(File inputDir) throws Exception {
		File iniFile = new File(inputDir, Constant.CONFIG_FILENAME);
		Configurator config = Configurator.getConfigurator();
		config.deserializeFrom(iniFile);

		System.out.println("deserializing dataHolder...");
		dataHolder = FinalDataHolder.deserialize(config.getStatisticalDir()); //must be done after the configurator is initialized
	}

	public void train(int classifierId) throws Exception {
		Classifier trainer = null;
		switch (classifierId) {
		case Constant.TWCNB:
			trainer = new TWCNBayes(dataHolder);
			break;
		}

		System.out.println("training...");
		trainer.train();
		System.out.println("serializing the result...");
		trainer.serialize();
	}
}