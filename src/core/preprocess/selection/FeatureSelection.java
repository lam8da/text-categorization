package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;

public abstract class FeatureSelection {
	public abstract String[] maxFeatureSelection(DataAnalyzer data, double thresh);
	public abstract String[] avgFeatureSelection(DataAnalyzer data, double thresh);
	public abstract double getAvgSelectionWeighting(DataAnalyzer data, int featureId);
	public abstract double getMaxSelectionWeighting(DataAnalyzer data, int featureId);
	public abstract double determineThreshold(double[] data);
}
