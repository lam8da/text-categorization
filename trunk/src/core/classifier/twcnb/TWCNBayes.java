package core.classifier.twcnb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Vector;

import core.classifier.util.FinalDataHolder;
import core.classifier.util.Classifier;
import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.util.Constant;

public class TWCNBayes extends Classifier {
	private double[][] ctWeight;

	public TWCNBayes() throws Exception {
		super();
		deserializeFrom(new File(config.getOutputDir(), Constant.TWCNB_FOLDER));
	}

	public TWCNBayes(FinalDataHolder holder) throws Exception {
		super(holder);
		this.documentCnt = dataHolder.getN();
		this.labelCnt = dataHolder.getLabelCnt();
		this.featureCnt = dataHolder.getFeatureCnt();
		this.ctWeight = new double[labelCnt][featureCnt];
	}

	public void train() throws Exception {
		if (dataHolder == null) throw new Exception("dataHolder is null, can not train!");

		System.out.println("calculating the document-term matrix...");
		double[][] dtWeight = new double[documentCnt][featureCnt]; // the document-term matrix
		for (int docId = 0; docId < documentCnt; docId++) {
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				double dkj = dataHolder.getW_dj_tk(docId, featureId);
				dkj = Math.log1p(dkj);
				dkj = dkj * Math.log(documentCnt / (double) dataHolder.getN_tk(featureId));
				dtWeight[docId][featureId] = dkj;
				//if (dkj > 0) System.out.println(dkj);
			}
		}

		boolean[] badDocMark = new boolean[documentCnt];
		Arrays.fill(badDocMark, false);
		//actualDocCnt = documentCnt;

		for (int docId = 0; docId < documentCnt; docId++) {
			double sum = 0;
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				sum += dtWeight[docId][featureId] * dtWeight[docId][featureId];
			}
			if (sum == 0) {
				badDocMark[docId] = true;
				//actualDocCnt--;
				continue;
			}
			sum = Math.sqrt(sum);
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				dtWeight[docId][featureId] /= sum;
				//if (dtWeight[docId][featureId] > 0 && Math.random() > 0.8) System.out.println(dtWeight[docId][featureId]);
			}
		}

		double[] rowSum = new double[documentCnt];
		double[] columnSum = new double[featureCnt];
		double dtWeightSum = 0;
		Arrays.fill(rowSum, 0);
		Arrays.fill(columnSum, 0);
		for (int docId = 0; docId < documentCnt; docId++) {
			if (!badDocMark[docId]) {
				for (int featureId = 0; featureId < featureCnt; featureId++) {
					double tmp = dtWeight[docId][featureId];
					rowSum[docId] += tmp;
					columnSum[featureId] += tmp;
					dtWeightSum += tmp;
					//System.out.println(docId + ": " + dtWeightSum);
					//if (docId == 97) {
					//	System.out.println();
					//}
				}
			}
		}
		//System.out.println();
		//for (int i = 0; i < documentCnt; i++) {
		//	System.out.println(rowSum[i]);
		//}
		//System.out.println();
		//for (int i = 0; i < featureCnt; i++) {
		//	System.out.println(columnSum[i]);
		//}
		//System.out.println();

		System.out.println("calculating the class-term matrix (the classifying parameters)...");
		double alpha_i = 1;
		double alpha = featureCnt;
		for (int labelId = 0; labelId < labelCnt; labelId++) {
			Vector<Integer> l = dataHolder.getDocIdsByLabel(labelId);
			int lsize = l.size();
			double denominator = dtWeightSum + alpha;
			for (int i = 0; i < lsize; i++) {
				if (!badDocMark[l.get(i)]) {
					denominator -= rowSum[l.get(i)];
				}
			}
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				double numerator = columnSum[featureId] + alpha_i;
				for (int i = 0; i < lsize; i++) {
					if (!badDocMark[l.get(i)]) {
						numerator -= dtWeight[l.get(i)][featureId];
					}
				}
				ctWeight[labelId][featureId] = Math.log(numerator / denominator);
			}
		}

		for (int labelId = 0; labelId < labelCnt; labelId++) {
			double s = 0;
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				s += ctWeight[labelId][featureId];
			}
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				ctWeight[labelId][featureId] /= s;
			}
		}
	}

	@Override
	public int classify(String[] titleFeatures, String[] contentFeatures) throws Exception {
		FeatureContainer featureContainer = config.getGenerator().generateFeatureContainer();
		featureContainer.deserializeFrom(new File(config.getStatisticalDir(), Constant.FEATURE_CONTAINER_FILE), null);

		// we regard the features in the title and the content the same
		double mins = Double.MAX_VALUE;
		int ans = -1;
		for (int i = 0; i < labelCnt; i++) {
			double s = 0;
			for (int j = 0; j < titleFeatures.length; j++) {
				int id = featureContainer.getId(titleFeatures[j]);
				if (id != -1) s += ctWeight[i][id];
			}
			for (int j = 0; j < contentFeatures.length; j++) {
				int id = featureContainer.getId(contentFeatures[j]);
				if (id != -1) s += ctWeight[i][id];
			}
			if (s < mins) {
				mins = s;
				ans = i;
			}
		}
		return ans;
	}

	@Override
	public void serialize() throws Exception {
		File outputDir = new File(config.getOutputDir(), Constant.TWCNB_FOLDER);
		outputDir.mkdirs();

		File weightFile = new File(outputDir, Constant.TWCNB_CLASS_TERM_WEIGHT_FILE);
		FileWriter fw = new FileWriter(weightFile);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write(String.valueOf(labelCnt));
		bw.newLine();
		bw.write(String.valueOf(featureCnt));
		bw.newLine();
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				bw.write(String.valueOf(ctWeight[i][j]));
				bw.newLine();
			}
		}

		bw.flush();
		bw.close();
		fw.close();
	}

	@Override
	public void deserializeFrom(File dir) throws Exception {
		this.documentCnt = -1;
		this.dataHolder = null;

		File ctWeightFile = new File(dir, Constant.TWCNB_CLASS_TERM_WEIGHT_FILE);
		FileReader fr = new FileReader(ctWeightFile);
		BufferedReader br = new BufferedReader(fr);

		this.labelCnt = Integer.parseInt(br.readLine());
		this.featureCnt = Integer.parseInt(br.readLine());
		this.ctWeight = new double[labelCnt][featureCnt];

		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				ctWeight[i][j] = Double.parseDouble(br.readLine());
			}
		}

		br.close();
		fr.close();
	}
}
