package core.preprocess.selection;

import core.preprocess.analyzation.DataAnalyzer;

public class CHIFeatrueSelector extends FeatureSelector {

	public CHIFeatrueSelector(DataAnalyzer data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double A, B, C, D, L;
		double avg = 0;
		double M = analyzer.getM();
		for (int i = 0; i != M; i++) {
			A = analyzer.getN_ci_tk(i, featureId);
			B = analyzer.getN_not_ci_tk(i, featureId);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			D = analyzer.getN_not_ci_exclude_tk(i, featureId);
			L = analyzer.getN_ci(i);
			avg += (L * (A * D - C * B) * (A * D - C * B)+1) / ((A + C) * (B + D) * (A + B) * (C + D)+1);/*in case of 0 and does not affect precision*/
		}
		return avg;
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double A, B, C, D, N,M;
		double max = 0;
		double tmp;
		N = analyzer.getN();
		M = analyzer.getM();
		for (int i = 0; i != M; i++) {
			A = analyzer.getN_ci_tk(i, featureId);
			B = analyzer.getN_not_ci_tk(i, featureId);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			D = analyzer.getN_not_ci_exclude_tk(i, featureId);
			tmp = (N*(A * D - C * B) * (A * D - C * B)+1) / ((A + C) * (B + D) * (A + B) * (C + D)+1);/*in case of 0 and does not affect precision*/
			if (tmp > max) {
				max = tmp;
			}
		}
		return max;
	}

}
