package core.preprocess.selection;

import java.util.Vector;

import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.Kmpp;
import core.preprocess.util.Constant;
import core.preprocess.util.KmppOneDimension;

public abstract class FeatureSelector {
	public static final int INTERACTION = 2000;
	public static final int CLUSTER = 10;

	protected DataAnalyzer analyzer;
	private int type;
	private double thresh;
	private double[] weighting;

	public FeatureSelector(DataAnalyzer data, int type) {
		this.analyzer = data;
		this.type = type;
	}

	public void featureReduction()  throws Exception {
		this.thresh = determineThreshold();
		Vector<String> reduceList = new Vector<String>(1024);

		for (int i = 0; i != weighting.length; i++) {
			if (weighting[i] <= thresh) {
				reduceList.add(analyzer.getFeature(i));
			}
		}
		
		for(int i=0;i<reduceList.size();i++){
			analyzer.reduce(reduceList.get(i));
		}
	}

	public abstract double getAvgSelectionWeighting(int featureId);

	public abstract double getMaxSelectionWeighting(int featureId);

	private double determineThreshold() {
		int size = analyzer.getV();
		System.out.println("size = "+size);
		weighting = new double[size];
		if (type == Constant.FEATURE_SELECTION_MAXSELECTION) {
			for (int i = 0; i != size; i++) {
				weighting[i] = this.getMaxSelectionWeighting(i);
			}
		}
		else if (type == Constant.FEATURE_SELECTION_AVGSELECTION) {
			for (int i = 0; i != size; i++) {
				weighting[i] = this.getAvgSelectionWeighting(i);
			}
		}

		KmppOneDimension k = new KmppOneDimension(weighting,FeatureSelector.CLUSTER,FeatureSelector.INTERACTION);
		k.Cluster();
		return  k.GetThresh();
	}
	
}