package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;

public class DFFeatureSelector extends FeatureSelector {

	public DFFeatureSelector(DataAnalyzer data, int type) {
		super(data, type);
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		return analyzer.getW_tk(featureId);
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		return analyzer.getW_tk(featureId);
	}
}
