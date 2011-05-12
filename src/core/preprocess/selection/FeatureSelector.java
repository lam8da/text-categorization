package core.preprocess.selection;

import java.util.Vector;

import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.Kmpp;
import core.preprocess.util.Constant;

public abstract class FeatureSelector {
	public static final int INTERACTION = 2000;
	public static final int CLUSTER = 10;

	protected DataAnalyzer analyzer;
	private int type;
	private double thresh;
	private double[][] weighting;

	public FeatureSelector(DataAnalyzer data, int type) {
		this.analyzer = data;
		this.type = type;
	}

	public Vector<String> getReductionList() throws Exception {
		this.thresh = determineThreshold();
		Vector<String> reductionList = new Vector<String>(1024);

		for (int i = 0; i != weighting.length; i++) {
			if (weighting[i][0] <= thresh) {
				reductionList.add(analyzer.getFeature(i));
			}
		}

//		for (int i = 0; i < reductionList.size(); i++) {
//			analyzer.reduce(reductionList.get(i));
//		}
		return reductionList;
	}

	public abstract double getAvgSelectionWeighting(int featureId);

	public abstract double getMaxSelectionWeighting(int featureId);

	private double determineThreshold() {
		int size = analyzer.getV();
		System.out.println("size = " + size);
		weighting = new double[size][1];
		if (type == Constant.FEATURE_SELECTION_MAXSELECTION) {
			for (int i = 0; i != size; i++) {
				weighting[i][0] = this.getMaxSelectionWeighting(i);
			}
		}
		else if (type == Constant.FEATURE_SELECTION_AVGSELECTION) {
			for (int i = 0; i != size; i++) {
				weighting[i][0] = this.getAvgSelectionWeighting(i);
			}
		}

		Kmpp k = new Kmpp();
		//k.cluster(weighting, 1, FeatureSelector.CLUSTER, FeatureSelector.INTERACTION);
		//return  k.getThresh();
		return 5;
	}

}