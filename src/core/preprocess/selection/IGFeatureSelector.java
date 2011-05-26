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
		double log2 = Math.log(2);
		N = analyzer.getN();
		double logN = Math.log(N);
		T_ = analyzer.getN_exclude_tk(featureId)+1;
		double logT_ = Math.log(T_);
		T = analyzer.getN_tk(featureId)+1;
		double logT = Math.log(T);
		int M = analyzer.getM();
		for (int i = 0; i != M; i++) {
			/*L = analyzer.getN_ci(i);
			tmp1 -= (L / N) * (Math.log(L)/log2-logN/log2);*/
			A = analyzer.getN_ci_tk(i, featureId)+1;
			tmp2 += (A / T) * (Math.log(A)-logT)/log2;
			C = analyzer.getN_ci_exclude_tk(i, featureId)+1;
			tmp3 += (C / T_) * (Math.log(C)-logT_)/log2;
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
