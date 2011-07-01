package core.classifier.util;

import core.util.Configurator;

public abstract class Trainer {
	//protected int actualDocCnt;
	protected int documentCnt;
	protected int labelCnt;
	protected int featureCnt;
	protected FinalDataHolder dataHolder;
	protected Configurator config;

	public Trainer(FinalDataHolder holder) {
		//this.actualDocCnt = -1;
		this.config = Configurator.getConfigurator();
		this.dataHolder = holder;
		this.documentCnt = dataHolder.getN();
		this.labelCnt = dataHolder.getLabelCnt();
		this.featureCnt = dataHolder.getFeatureCnt();
	}

	public abstract void train() throws Exception;

	public abstract void serialize() throws Exception;
}
