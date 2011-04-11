package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.util.ArrayList;

public class DFFeatureSelection extends FeatureSelection{

	@Override
	public String[] avgFeatureSelection(DataAnalyzer data, String[] featureSet, double thresh) {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		double tmp;
		for(int i = 0; i != featureSet.length; i++){
			try {
				tmp = data.getM_tk(featureSet[i]);
				if(tmp > thresh){
					result.add(featureSet[i]);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (String[]) result.toArray();
	}

	@Override
	public String[] maxFeatureSelection(DataAnalyzer data, String[] featureSet, double thresh) {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		double tmp;
		for(int i = 0; i != featureSet.length; i++){
			try {
				tmp = data.getM_tk(featureSet[i]);
				if(tmp > thresh){
					result.add(featureSet[i]);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (String[]) result.toArray();
	}
	
	@Override
	public double getAvgSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		try {
			return data.getM_tk(featureId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public double getMaxSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		try {
			return data.getM_tk(featureId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}
