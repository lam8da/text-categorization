package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;

public class DFFeatureSelection extends FeatureSelection{


	@Override
	public boolean selection(DataAnalyzer data, int featureId, double thresh) {
		// TODO Auto-generated method stub
		int tmp = data.getN_tk(featureId);
		if(tmp > thresh)
			return true;
		return false;
	}

	@Override
	public boolean selection(DataAnalyzer data, String featureLabel, double thresh) {
		// TODO Auto-generated method stub
		int tmp = data.getN_tk(featureLabel);
		if(tmp > thresh)
			return true;
		return false;
	}

	@Override
	public String[] featureSelection(DataAnalyzer data, String[] featureSet, double thresh) {
		// TODO Auto-generated method stub
		return null;
	}

}
