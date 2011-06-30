package core.evaluation;

import java.io.File;
import java.io.FileFilter;

import core.classifier.twcnb.TWCNBayes;
import core.classifier.util.Classifier;
import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.util.XmlDocument;
import core.util.Configurator;
import core.util.Constant;

public class Evaluator {
	private Configurator config;
	private FeatureContainer labelNameContainer;

	public Evaluator() throws Exception {
		config = Configurator.getConfigurator();
		deserializeLabelNameContainer();
	}

	public Evaluator(File configFile) throws Exception {
		config = Configurator.getConfigurator();
		config.deserializeFrom(configFile);
		deserializeLabelNameContainer();
	}

	private void deserializeLabelNameContainer() throws Exception {
		// this method must be invoked after the configurator is initialized
		labelNameContainer = config.getGenerator().generateFeatureContainer();
		labelNameContainer.deserializeFrom(new File(config.getStatisticalDir(), Constant.LABEL_NAME_CONTAINER_FILE), null);
	}

	public void evaluate() throws Exception {
		Classifier trainer = null;
		switch (config.getClassifierId()) {
		case Constant.TWCNB:
			trainer = new TWCNBayes(); // load parameters
			break;
		}

		File[] xmlFiles = config.getTestDir().listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		XmlDocument xml = new XmlDocument();

		System.out.print("testing documents: ");
		int correctCnt = 0;
		int total = xmlFiles.length;
		for (int i = 0; i < xmlFiles.length; i++) {
			if ((i & 2047) == 0) System.out.println();
			if ((i & 255) == 0) System.out.print(i + ",... ");

			xml.parseDocument(xmlFiles[i]);
			int ans = trainer.classify(xml.getTitleFeatures(), xml.getContentFeatures());

			String[] labels = xml.getLabels();
			boolean correct = false;
			for (int j = 0; j < labels.length; j++) {
				int actualLabel = labelNameContainer.getId(labels[j]);
				if (ans == actualLabel) {
					correct = true;
					break;
				}
			}

			if (correct) correctCnt++;
		}
		System.out.println();

		System.out.println("total test file: " + total);
		System.out.println("number of correct classification: " + correctCnt);
		System.out.println("accuracy: " + correctCnt * 100.0 / total + "%");
	}
}
