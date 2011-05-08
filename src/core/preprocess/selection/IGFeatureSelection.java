package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.lang.Math;
import java.util.ArrayList;

public class IGFeatureSelection extends FeatureSelection {


	public IGFeatureSelection(DataAnalyzer data, int type) {
		super(data, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		double N,L,T,T_,A,C;
		double res, tmp1 = 0, tmp2 = 0, tmp3 = 0;
		N = data.getN();
		try {
			T_ = data.getM_exclude_tk(featureId);
			T = data.getM_tk(featureId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			T = 0;
			T_ = 0;
		}
		for(int i = 0; i != data.getM(); i++){
			L = data.getN_ci(i);
			tmp1 += (L/N)*Math.log(L/N);
			A = data.getN_ci_tk(i, featureId);
			tmp2 += (A/T)*Math.log(A/T);
			C = data.getN_ci_exclude_tk(i, featureId);
			tmp3 += (C/T_)*Math.log(C/T_);
		}
		tmp2 = T/N*tmp2;
		tmp3 = T_/N*tmp3;
		res = tmp2+tmp3-tmp1;
		return res;
	}

	@Override
	public double getMaxSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		double N,L,T,T_,A,C;
		double res, tmp1 = 0, tmp2 = 0, tmp3 = 0;
		N = data.getN();
		try {
			T_ = data.getM_exclude_tk(featureId);
			T = data.getM_tk(featureId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			T = 0;
			T_ = 0;
		}
		for(int i = 0; i != data.getM(); i++){
			L = data.getN_ci(i);
			tmp1 += (L/N)*Math.log(L/N);
			A = data.getN_ci_tk(i, featureId);
			tmp2 += (A/T)*Math.log(A/T);
			C = data.getN_ci_exclude_tk(i, featureId);
			tmp3 += (C/T_)*Math.log(C/T_);
		}
		tmp2 = T/N*tmp2;
		tmp3 = T_/N*tmp3;
		res = tmp2+tmp3-tmp1;
		return res;
	}
}
