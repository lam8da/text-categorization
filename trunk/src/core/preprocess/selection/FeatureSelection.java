package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;

public abstract class FeatureSelection {
	public abstract boolean selection (DataAnalyzer data,  int featureId, double thresh);
	public abstract boolean selection (DataAnalyzer data,  String featureId, double thresh);
	public abstract String[] featureSelection(DataAnalyzer data, String[] featureSet, double thresh);
}
