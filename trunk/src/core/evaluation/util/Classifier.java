package core.evaluation.util;

import java.io.File;
import java.util.Vector;

import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.util.Configurator;
import core.util.Constant;

public abstract class Classifier {
	protected int documentCnt;
	protected int labelCnt;
	protected int featureCnt;
	protected Configurator config;
	protected FeatureContainer featureContainer;

	public Classifier() throws Exception {
		config = Configurator.getConfigurator();
		documentCnt = -1;
		labelCnt = -1;
		featureCnt = -1;
		featureContainer = config.getGenerator().generateFeatureContainer();
		featureContainer.deserializeFrom(new File(config.getStatisticalDir(), Constant.FEATURE_CONTAINER_FILE), null);
	}

	public abstract int classify(String[] titleFeatures, String[] contentFeatures) throws Exception;

	public abstract int[] classify(Vector<String[]> titleFeatures, Vector<String[]> contentFeatures) throws Exception;

	public abstract void deserializeFrom(File dir) throws Exception;
}
