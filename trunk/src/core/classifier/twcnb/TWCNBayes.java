package core.classifier.twcnb;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import core.classifier.util.FinalDataHolder;
import core.classifier.util.Classifier;

public class TWCNBayes implements Classifier {
	private FinalDataHolder dataHolder;
	private double[][] ctWeight;
	private int documentCnt;
	private int labelCnt;
	private int featureCnt;

	public TWCNBayes(FinalDataHolder holder) throws Exception {
		dataHolder = holder;
		this.documentCnt = dataHolder.getN();
		this.labelCnt = dataHolder.getLabelCnt();
		this.featureCnt = dataHolder.getFeatureCnt();
		this.ctWeight = new double[labelCnt][featureCnt];
	}

	public void train() {
		double[][] dtWeight = new double[documentCnt][featureCnt]; // the document-term matrix
		for (int docId = 0; docId < documentCnt; docId++) {
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				double dkj = dataHolder.getW_dj_tk(docId, featureId);
				dkj = Math.log1p(dkj);
				dkj = dkj * Math.log(documentCnt / (double) dataHolder.getN_tk(featureId));
				dtWeight[docId][featureId] = dkj;
			}
		}
		for (int docId = 0; docId < documentCnt; docId++) {
			double sum = 0;
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				sum += dtWeight[docId][featureId] * dtWeight[docId][featureId];
			}
			sum = Math.sqrt(sum);
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				dtWeight[docId][featureId] /= sum;
			}
		}

		double[] rowSum = new double[documentCnt];
		double[] columnSum = new double[featureCnt];
		double dtWeightSum = 0;
		Arrays.fill(rowSum, 0);
		Arrays.fill(columnSum, 0);
		for (int docId = 0; docId < documentCnt; docId++) {
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				double tmp = dtWeight[docId][featureId];
				rowSum[docId] += tmp;
				columnSum[featureId] += tmp;
				dtWeightSum += tmp;
			}
		}

		double alpha_i = 1;
		double alpha = featureCnt;
		for (int labelId = 0; labelId < labelCnt; labelId++) {
			Vector<Integer> l = dataHolder.getDocIdsByLabel(labelId);
			int lsize = l.size();
			double denominator = dtWeightSum + alpha;
			for (int i = 0; i < lsize; i++) {
				denominator -= rowSum[l.get(i)];
			}
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				double numerator = columnSum[featureId] + alpha_i;
				for (int i = 0; i < lsize; i++) {
					numerator -= dtWeight[l.get(i)][featureId];
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
	public int classify(String[] titleFeatures, String[] contentFeatures) {
		// we regard the features in the title and the content the same
		double mins = Double.MAX_VALUE;
		int ans = -1;
		for (int i = 0; i < labelCnt; i++) {
			double s = 0;
			for (int j = 0; j < titleFeatures.length; j++) {
				int id = dataHolder.getFeatureId(titleFeatures[i]);
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
	public void serialize(File outputDir) throws Exception {}
}
