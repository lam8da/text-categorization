package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.lang.Math;
import java.util.ArrayList;
public class MIFeatureSelection extends FeatureSelection{

	@Override
	public String[] avgFeatureSelection(DataAnalyzer data, String[] featureSet,
			double thresh) {
		// TODO Auto-generated method stub
		double tmp;
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i != featureSet.length; i++){
			tmp = this.getAvgSelectionWeighting(data, data.getFeatureId(featureSet[i]));
			if(tmp > thresh){
				result.add(featureSet[i]);
			}
		}
		return (String[])result.toArray();
	}

	@Override
	public double getAvgSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		double res = 0;
		double A,B,C,L;
		for(int i = 0; i != data.getM(); i++){
			L = data.getN_ci(i);
			A = data.getN_ci_tk(i, featureId);
			B = data.getN_not_ci_tk(i, featureId);
			C = data.getN_ci_exclude_tk(i, featureId);
			res += (A*L/(A+C)/(A+B));
		}
		return res;
	}

	@Override
	public double getMaxSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		double res = 0;
		double tmp;
		double A,B,C,L;
		for(int i = 0; i != data.getM(); i++){
			L = data.getN_ci(i);
			A = data.getN_ci_tk(i, featureId);
			B = data.getN_not_ci_tk(i, featureId);
			C = data.getN_ci_exclude_tk(i, featureId);
			tmp = (A*L/(A+C)/(A+B));
			if(tmp > res){
				res = tmp;
			}
		}
		return res;
	}

	@Override
	public String[] maxFeatureSelection(DataAnalyzer data, String[] featureSet,
			double thresh) {
		// TODO Auto-generated method stub
		double tmp;
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i != featureSet.length; i++){
			tmp = this.getMaxSelectionWeighting(data, data.getFeatureId(featureSet[i]));
			if(tmp > thresh){
				result.add(featureSet[i]);
			}
		}
		return (String[])result.toArray();
	}


}
