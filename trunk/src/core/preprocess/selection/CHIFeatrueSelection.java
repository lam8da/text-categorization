package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.util.ArrayList;

public  class CHIFeatrueSelection extends FeatureSelection{

	@Override
	public String[] maxFeatureSelection(DataAnalyzer data, double thresh) {
		// TODO Auto-generated method stub
		double max;
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i != data.getV(); i++){
			max = this.getMaxSelectionWeighting(data, i);
			if(max > thresh){
				result.add(data.getFeature(i));
			}
		}
		return (String[])result.toArray();
	}
	
	@Override
	public String[] avgFeatureSelection(DataAnalyzer data, double thresh) {
		// TODO Auto-generated method stub
		double avg;
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i != data.getV(); i++){
			avg = this.getAvgSelectionWeighting(data, i);
			if(avg > thresh){
				result.add(data.getFeature(i));
			}
		}
		return (String[])result.toArray();
	}

	@Override
	public double getAvgSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		double A,B,C,D,L;
		double avg = 0;
		for(int i = 0; i != data.getM(); i++){
			A = data.getN_ci_tk(i, featureId);
			B = data.getN_not_ci_tk(i, featureId);
			C = data.getN_ci_exclude_tk(i, featureId);
			D = data.getN_not_ci_exclude_tk(i, featureId);
			L = data.getN_ci(i);
			avg += L*(A*D-C*B)*(A*D-C*B)/((A+C)*(B+D)*(A+B)*(C+D));
		}
		return avg;
	}
	@Override
	public double getMaxSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		double A,B,C,D,L;
		double max = 0;
		double tmp;
		for(int i = 0; i != data.getM(); i++){
			A = data.getN_ci_tk(i, featureId);
			B = data.getN_not_ci_tk(i, featureId);
			C = data.getN_ci_exclude_tk(i, featureId);
			D = data.getN_not_ci_exclude_tk(i, featureId);
			L = data.getN_ci(i);
			tmp = L*(A*D-C*B)*(A*D-C*B)/((A+C)*(B+D)*(A+B)*(C+D));
			if(tmp > max){
				max = tmp;
			}
		}
		return max;
	}

	@Override
	public double determineThreshold(double[] data) {
		// TODO Auto-generated method stub
		return 0;
	}
}