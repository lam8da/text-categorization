package core.classifier.util;

import java.io.File;

import core.util.Configurator;

public abstract class Classifier {
	protected int documentCnt;
	//protected int actualDocCnt;
	protected int labelCnt;
	protected int featureCnt;
	protected FinalDataHolder dataHolder;
	protected Configurator config;

	public Classifier() throws Exception {
		initialize();
		dataHolder = null;
	}

	public Classifier(FinalDataHolder holder) throws Exception {
		initialize();
		dataHolder = holder;
	}

	private void initialize() {
		config = Configurator.getConfigurator();
		documentCnt = -1;
		//this.actualDocCnt = -1;
		labelCnt = -1;
		featureCnt = -1;
	}

	public abstract void train() throws Exception;

	public abstract int classify(String[] titleFeatures, String[] contentFeatures) throws Exception;

	public abstract void serialize() throws Exception;

	public abstract void deserializeFrom(File dir) throws Exception;
}
