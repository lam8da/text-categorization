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
	Configurator config;
	FeatureContainer labelNameContainer;

	/**
	 * constructor
	 * 
	 * @param inputDir
	 *            the output dir of preprocessor (or the folder containing the
	 *            configuration file)
	 * @throws Exception
	 */
	public Evaluator(File inputDir) throws Exception {
		File iniFile = new File(inputDir, Constant.CONFIG_FILENAME);
		config = Configurator.getConfigurator();
		config.deserializeFrom(iniFile);
		labelNameContainer = config.getGenerator().generateFeatureContainer();
		labelNameContainer.deserializeFrom(new File(config.getStatisticalDir(), Constant.LABEL_NAME_CONTAINER_FILE), null);
	}

	public void evaluate(int classifierId) throws Exception {
		Classifier trainer = null;
		switch (classifierId) {
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
