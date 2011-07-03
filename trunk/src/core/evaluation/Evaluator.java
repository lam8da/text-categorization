package core.evaluation;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import core.evaluation.twcnb.TWCNBayesClassifier;
import core.evaluation.util.Classifier;
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
			System.out.println("evaluating twcnb (bayes)...");
			trainer = new TWCNBayesClassifier(); // load parameters
			break;
		}

		File[] xmlFiles = config.getTestDir().listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		XmlDocument xml = new XmlDocument();
		int correctCnt = 0;
		int total = xmlFiles.length;

		Vector<String[]> titleFeatures = new Vector<String[]>();
		Vector<String[]> contentFeatures = new Vector<String[]>();
		Vector<String[]> actualLabels = new Vector<String[]>();

		for (int i = 0; i < xmlFiles.length; i++) {
			xml.parseDocument(xmlFiles[i]);
			titleFeatures.add(xml.getTitleFeatures());
			contentFeatures.add(xml.getContentFeatures());
			actualLabels.add(xml.getLabels());
		}
		int[] labelIds = trainer.classify(titleFeatures, contentFeatures);
		System.out.println();

		for (int i = 0; i < total; i++) {
			String[] l = actualLabels.get(i);
			boolean correct = false;
			for (int j = 0; j < l.length; j++) {
				int id = labelNameContainer.getId(l[j]);
				if (labelIds[i] == id) {
					correct = true;
					correctCnt++;
					break;
				}
			}
//			if (!correct) {
//				System.out.print("mismatch: ");
//				for (int j = 0; j < l.length; j++) {
//					if (j > 0) System.out.print(",");
//					System.out.print(l[j]);
//				}
//				System.out.print(" --> ");
//				System.out.print(labelNameContainer.getWord(labelIds[i]));
//				System.out.println(" (" + xmlFiles[i].getName() + ")");
//			}
		}
		System.out.println();

		//System.out.print("testing documents: ");
		//for (int i = 0; i < xmlFiles.length; i++) {
		//	if ((i & 1023) == 0) System.out.println();
		//	if ((i & 255) == 0) System.out.print(i + ",... ");

		//	xml.parseDocument(xmlFiles[i]);
		//	int ans = trainer.classify(xml.getTitleFeatures(), xml.getContentFeatures());

		//	String[] labels = xml.getLabels();
		//	for (int j = 0; j < labels.length; j++) {
		//		int actualLabel = labelNameContainer.getId(labels[j]);
		//		if (ans == actualLabel) {
		//			correctCnt++;
		//			break;
		//		}
		//	}
		//}
		//System.out.println();

		System.out.println("total test file: " + total);
		System.out.println("number of correct classification: " + correctCnt);
		System.out.println("accuracy: " + correctCnt * 100.0 / total + "%");
	}
}
