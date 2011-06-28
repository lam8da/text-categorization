package core.classifier;

import java.io.File;

import core.Configurator;
import core.Constant;
import core.classifier.twcnb.TWCNBayes;
import core.classifier.util.Classifier;
import core.classifier.util.FinalDataHolder;

public class ClassifierTrainer {
	private FinalDataHolder dataHolder;

	public ClassifierTrainer(File inputDir) throws Exception {
		File iniFile = new File(inputDir, Constant.CONFIG_FILENAME);
		Configurator config = Configurator.getConfigurator();
		config.deserializeFrom(iniFile);

		dataHolder = FinalDataHolder.deserialize(inputDir); //must be done after the configurator is initialized
	}

	public void train(int classifierId) throws Exception {
		Classifier trainer = null;
		switch (classifierId) {
		case Constant.TWCNB:
			trainer = new TWCNBayes(dataHolder);
			break;
		}
		
		trainer.train();
	}
}