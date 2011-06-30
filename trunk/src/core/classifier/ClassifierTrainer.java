package core.classifier;

import java.io.File;

import core.classifier.twcnb.TWCNBayes;
import core.classifier.util.Classifier;
import core.classifier.util.FinalDataHolder;
import core.util.Configurator;
import core.util.Constant;

public class ClassifierTrainer {
	private FinalDataHolder dataHolder;
	private Configurator config;

	public ClassifierTrainer() throws Exception {
		config = Configurator.getConfigurator();
		deserializeDataHolder();
	}

	public ClassifierTrainer(File configFile) throws Exception {
		config = Configurator.getConfigurator();
		config.deserializeFrom(configFile);
		deserializeDataHolder();
	}

	private void deserializeDataHolder() throws Exception {
		// this method must be invoked after the configurator is initialized
		System.out.println("deserializing dataHolder...");
		dataHolder = FinalDataHolder.deserialize(config.getStatisticalDir());
	}

	public void train() throws Exception {
		Classifier trainer = null;
		switch (config.getClassifierId()) {
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