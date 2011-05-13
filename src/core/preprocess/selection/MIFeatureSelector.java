package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;

public class MIFeatureSelector extends FeatureSelector {

	public MIFeatureSelector(DataAnalyzer data, int type) {
		super(data, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double res = 0;
		double A, B, C,N,L;
		N = analyzer.getN();
		for (int i = 0; i != analyzer.getM(); i++) {
			L = analyzer.getN_ci(i);
			A = analyzer.getN_ci_tk(i, featureId);
			B = analyzer.getN_not_ci_tk(i, featureId);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			res += L/N*Math.log( (A * N / (A + C) / (A + B)) );
		}
		return res;
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double res = 0;
		double tmp;
		double A, B, C, N;
		N = analyzer.getN();
		for (int i = 0; i != analyzer.getM(); i++) {
			A = analyzer.getN_ci_tk(i, featureId);
			B = analyzer.getN_not_ci_tk(i, featureId);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			tmp = (A *N / (A + C) / (A + B));
			if (tmp > res) {
				res = tmp;
			}
		}
		return res;
	}
}
