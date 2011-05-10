package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;

public class CHIFeatrueSelector extends FeatureSelector {

	public CHIFeatrueSelector(DataAnalyzer data, int type) {
		super(data, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double A, B, C, D, L;
		double avg = 0;
		for (int i = 0; i != analyzer.getM(); i++) {
			A = analyzer.getN_ci_tk(i, featureId);
			B = analyzer.getN_not_ci_tk(i, featureId);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			D = analyzer.getN_not_ci_exclude_tk(i, featureId);
			L = analyzer.getN_ci(i);
			avg += L * (A * D - C * B) * (A * D - C * B) / ((A + C) * (B + D) * (A + B) * (C + D));
		}
		return avg;
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double A, B, C, D, L;
		double max = 0;
		double tmp;
		for (int i = 0; i != analyzer.getM(); i++) {
			A = analyzer.getN_ci_tk(i, featureId);
			B = analyzer.getN_not_ci_tk(i, featureId);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			D = analyzer.getN_not_ci_exclude_tk(i, featureId);
			L = analyzer.getN_ci(i);
			tmp = L * (A * D - C * B) * (A * D - C * B) / ((A + C) * (B + D) * (A + B) * (C + D));
			if (tmp > max) {
				max = tmp;
			}
		}
		return max;
	}

}
