package core.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import core.evaluation.twcnb.TWCNBayesClassifier;
import core.evaluation.util.Classifier;
import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.util.XmlDocument;
import core.util.Configurator;
import core.util.Constant;

public class Evaluator {
	protected class LabelResultInfo {
		int labelId = -1;
		int tp = 0; // true positive
		int fp = 0; // false positive
		int fn = 0; // false negative
		float precision = 0;
		float recall = 0;

		void calPrecision() {
			precision = tp / (float) (tp + fp);
		}

		void calRecall() {
			recall = tp / (float) (tp + fn);
		}
	}

	private Configurator config;
	private FeatureContainer labelNameContainer;
	private LabelResultInfo[] resultInfo;

	public Evaluator() throws Exception {
		config = Configurator.getConfigurator();
		init();
	}

	public Evaluator(File configFile) throws Exception {
		config = Configurator.getConfigurator();
		config.deserializeFrom(configFile);
		init();
	}

	private void init() throws Exception {
		// this method must be invoked after the configurator is initialized
		labelNameContainer = config.getGenerator().generateFeatureContainer();
		labelNameContainer.deserializeFrom(new File(config.getStatisticalDir(), Constant.LABEL_NAME_CONTAINER_FILE), null);

		resultInfo = new LabelResultInfo[labelNameContainer.size()];
		for (int i = 0; i < resultInfo.length; i++) {
			resultInfo[i] = new LabelResultInfo();
		}

		config.getEvaluationFolder().mkdirs();
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
		int total = xmlFiles.length;

		Vector<String[]> titleFeatures = new Vector<String[]>();
		Vector<String[]> contentFeatures = new Vector<String[]>();
		Vector<String[]> actualStrLabels = new Vector<String[]>();

		System.out.println("reading testing set...");
		for (int i = 0; i < xmlFiles.length; i++) {
			xml.parseDocument(xmlFiles[i]);
			titleFeatures.add(xml.getTitleFeatures());
			contentFeatures.add(xml.getContentFeatures());
			actualStrLabels.add(xml.getLabels());
		}
		Vector<int[]> labelIds = trainer.classify(titleFeatures, contentFeatures);
		System.out.println();

		for (int i = 0; i < total; i++) {
			String[] lstr = actualStrLabels.get(i);
			int[] actualLabels = new int[lstr.length];
			for (int j = 0; j < actualLabels.length; j++) {
				actualLabels[j] = labelNameContainer.getId(lstr[j]);
			}

			int[] resultLabels = labelIds.get(i);
			for (int j = 0; j < resultLabels.length; j++) {
				int lbl = resultLabels[j];
				boolean found = false;
				for (int k = 0; k < actualLabels.length; k++) {
					if (actualLabels[k] == lbl) {
						found = true;
						actualLabels[k] = -1; // -1 is not possible to be a label id
						this.resultInfo[lbl].tp++;
						break;
					}
				}
				if (!found) this.resultInfo[lbl].fp++;
			}
			for (int j = 0; j < actualLabels.length; j++) {
				if (actualLabels[j] != -1) this.resultInfo[actualLabels[j]].fn++;
			}
		}

		for (int i = 0; i < resultInfo.length; i++) {
			resultInfo[i].labelId = i;
			resultInfo[i].calPrecision();
			resultInfo[i].calRecall();
		}
		Arrays.sort(resultInfo, new Comparator<LabelResultInfo>() {
			@Override
			public int compare(LabelResultInfo arg0, LabelResultInfo arg1) {
				return arg1.tp + arg1.fn - arg0.tp - arg0.fn;
			}
		});
		serializeResult();

		System.out.println("total test file: " + total);
		int labelCnt = labelNameContainer.size();
		int showCnt = (15 > labelCnt ? labelCnt : 15);
		System.out.println("precision and recall of the top " + showCnt + " most frequently classes (total " + labelCnt + " ) are:");
		for (int i = 0; i < showCnt; i++) {
			String labelStr = labelNameContainer.getWord(resultInfo[i].labelId);
			System.out.print(String.format("label: %-15s   ", labelStr.equals(Constant.EMPTY_LABEL) ? "<EMPTY>" : labelStr));
			System.out.print(String.format("precision: %5.2f%%   ", resultInfo[i].precision * 100));
			System.out.println(String.format("recall: %5.2f%%", resultInfo[i].recall * 100));
		}
	}

	private void serializeResult() throws Exception {
		File resultFile = new File(config.getEvaluationFolder(), Constant.EVALUATION_RESULT_FILE);
		FileWriter fw = new FileWriter(resultFile);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("number of labels: " + labelNameContainer.size());
		bw.newLine();
		for (int i = 0; i < resultInfo.length; i++) {
			bw.write(String.format("id: %-4d   ", resultInfo[i].labelId));
			String labelStr = labelNameContainer.getWord(resultInfo[i].labelId);
			bw.write(String.format("label: %-20s   ", labelStr.equals(Constant.EMPTY_LABEL) ? "<EMPTY>" : labelStr));
			bw.write(String.format("tp: %-4d   ", resultInfo[i].tp));
			bw.write(String.format("fp: %-4d   ", resultInfo[i].fp));
			bw.write(String.format("fn: %-4d   ", resultInfo[i].fn));
			bw.write(String.format("precision: %5.2f%%   ", resultInfo[i].precision * 100));
			bw.write(String.format("recall: %5.2f%%", resultInfo[i].recall * 100));
			bw.newLine();
		}

		bw.flush();
		bw.close();
		fw.close();
	}
}
