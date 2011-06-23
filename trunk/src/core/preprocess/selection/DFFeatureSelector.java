package core.preprocess.selection;

import core.preprocess.analyzation.DataAnalyzer;

public class DFFeatureSelector extends FeatureSelector {

	public DFFeatureSelector(DataAnalyzer data, int type) {
		super(data, type);
	}

	@Override
	public double getAvgSelectionWeighting(int featureId) {
		return analyzer.getN_tk(featureId);
	}

	@Override
	public double getMaxSelectionWeighting(int featureId) {
		return analyzer.getN_tk(featureId);
	}
}
