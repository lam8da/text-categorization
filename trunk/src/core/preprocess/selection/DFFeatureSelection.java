package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.util.ArrayList;

public class DFFeatureSelection extends FeatureSelection{

	@Override
	public String[] avgFeatureSelection(DataAnalyzer data, double thresh) {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		double tmp;
		for(int i = 0; i != data.getV(); i++){
			try {
				tmp = data.getM_tk(i);
				if(tmp > thresh){
					result.add(data.getFeature(i));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (String[]) result.toArray();
	}

	@Override
	public String[] maxFeatureSelection(DataAnalyzer data, double thresh) {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		double tmp;
		for(int i = 0; i != data.getV(); i++){
			try {
				tmp = data.getM_tk(i);
				if(tmp > thresh){
					result.add(data.getFeature(i));
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

	@Override
	public double determineThreshold(double[] data) {
		// TODO Auto-generated method stub
		return 0;
	}
}
