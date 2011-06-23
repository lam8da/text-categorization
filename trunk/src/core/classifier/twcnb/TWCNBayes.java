package core.classifier.twcnb;

import core.classifier.util.FinalDataHolder;
import core.classifier.util.Trainer;

public class TWCNBayes implements Trainer {
	private FinalDataHolder dataHolder;
	private double[][] dtWeight;
	private double[][] ctWeight;
	private int documentCnt;
	private int labelCnt;
	private int featureCnt;

	public TWCNBayes(FinalDataHolder holder) throws Exception {
		dataHolder = holder;
		this.documentCnt = dataHolder.getN();
		this.labelCnt = dataHolder.getLabelCnt();
		this.featureCnt = dataHolder.getFeatureCnt();
		this.dtWeight = new double[documentCnt][featureCnt];
		this.ctWeight = new double[labelCnt][featureCnt];
	}

	public void train() {
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
		for (int labelId = 0; labelId < labelCnt; labelId++) {

		}
	}

	public double calculateW_ci_tk(int labelId, int featureId) {
		double ans = 0;
		return ans;
	}
}
