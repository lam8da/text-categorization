package core.classifier;

import java.io.File;

import core.classifier.twcnb.TWCNBayesTrainer;
import core.classifier.util.Trainer;
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
		Trainer trainer = null;
		switch (config.getClassifierId()) {
		case Constant.TWCNB:
			System.out.println("using twcnb (bayes) classifier.");
			trainer = new TWCNBayesTrainer(dataHolder);
			break;
		}

		System.out.println("start training...");
		trainer.train();
		System.out.println("serializing the result...");
		trainer.serialize();
		System.out.println("training finished!");
		System.out.println();
	}
}