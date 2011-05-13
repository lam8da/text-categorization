package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.lang.Math;

public class IGFeatureSelector extends FeatureSelector {

	public IGFeatureSelector(DataAnalyzer data, int type) {
		super(data, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		double N, L, T, T_, A, C;
		double res, tmp1 = 0, tmp2 = 0, tmp3 = 0;
		N = analyzer.getN();
		T_ = analyzer.getN_exclude_tk(featureId);
		T = analyzer.getN_tk(featureId);
		
		for (int i = 0; i != analyzer.getM(); i++) {
			L = analyzer.getN_ci(i);
			tmp1 -= (L / N) * Math.log(L / N);
			A = analyzer.getN_ci_tk(i, featureId);
			tmp2 += (A / T) * Math.log(A / T);
			C = analyzer.getN_ci_exclude_tk(i, featureId);
			tmp3 += (C / T_) * Math.log(C / T_);
		}
		tmp2 = T / N * tmp2;
		tmp3 = T_ / N * tmp3;
		res = tmp2 + tmp3 + tmp1;
		return res;
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		return this.getAvgSelectionWeighting(featureId);
	}
}
