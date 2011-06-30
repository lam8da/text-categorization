package core.preprocess.selection;

import java.util.Vector;

import core.preprocess.analyzation.DataAnalyzer;
import core.preprocess.util.KmppOneDimension;
import core.util.Configurator;
import core.util.Constant;

public abstract class FeatureSelector {
	public static final int INTERACTION = 2000;
	public static final int CLUSTER = 10;

	protected DataAnalyzer analyzer;
	private double thresh;
	private double[] weighting;

	public FeatureSelector(DataAnalyzer data) {
		this.analyzer = data;
	}

	public int[] getReductionList() throws Exception {
		this.thresh = determineThreshold();
		System.out.println("threshold = " + this.thresh);

		Vector<Integer> reductionList = new Vector<Integer>(1024);
		for (int i = 0; i != weighting.length; i++) {
			if (weighting[i] <= thresh) {
				reductionList.add(i);
			}
		}

		int[] res = new int[reductionList.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = reductionList.get(i);
		}
		return res;
	}

	public abstract double getAvgSelectionWeighting(int featureId);

	public abstract double getMaxSelectionWeighting(int featureId);

	private double determineThreshold() throws Exception {
		int size = analyzer.getV();
		System.out.println("vocabulary size = " + size);

		weighting = new double[size];
		Configurator config = Configurator.getConfigurator();

		switch (config.getSelectMethodId()) {
		case Constant.FEATURE_SELECTION_MAXSELECTION:
			for (int i = 0; i != size; i++) {
				weighting[i] = this.getMaxSelectionWeighting(i);
			}
			break;
		case Constant.FEATURE_SELECTION_AVGSELECTION:
			for (int i = 0; i != size; i++) {
				weighting[i] = this.getAvgSelectionWeighting(i);
			}
			break;
		}

		if (config.getThresholdMethodId() == Constant.THRESHOLD_K_MEANS) {
			KmppOneDimension k = new KmppOneDimension(weighting, FeatureSelector.CLUSTER, FeatureSelector.INTERACTION);
			k.cluster();
			k.output();
			return k.getThresh();
		}
		else {
			double thres = -1;
			switch (config.getSelectorId()) {
			case Constant.CHI_SELECTOR:
				thres = config.getChiThres();
				break;
			case Constant.DF_SELECTOR:
				thres = config.getDfThres();
				break;
			case Constant.MI_SELECTOR:
				thres = config.getMiThres();
				break;
			case Constant.IG_SELECTOR:
				thres = config.getIgThres();
				break;
			case Constant.WF_SELECTOR:
				thres = config.getWfThres();
				break;
			}
			System.out.println("threshold = " + thres);
			return thres;
		}
	}
}