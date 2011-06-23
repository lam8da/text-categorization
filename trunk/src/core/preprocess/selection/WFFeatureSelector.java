package core.preprocess.selection;

import core.preprocess.analyzation.DataAnalyzer;

public class WFFeatureSelector extends FeatureSelector{

	public WFFeatureSelector(DataAnalyzer data, int type) {
		super(data, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		return this.analyzer.getW_tk(featureId);
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		// TODO Auto-generated method stub
		return this.analyzer.getW_tk(featureId);
	}

}
