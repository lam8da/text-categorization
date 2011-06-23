package core.classifier.util;

import java.io.File;
import java.util.Vector;

import core.preprocess.analyzation.DataHolder;
import core.preprocess.analyzation.generator.ContainerGenerator;

public final class FinalDataHolder extends DataHolder {
	private FinalDataHolder(ContainerGenerator g) {
		super(g);
		this.V_not_ci = new Vector<Integer>(256);
		this.V_not_dj = new Vector<Integer>(16384);
		this.M_tk = new Vector<Integer>(8192);
	}

	public static FinalDataHolder deserialize(ContainerGenerator g, File inputDir) throws Exception {
		FinalDataHolder res = new FinalDataHolder(g);
		deserialize(res, inputDir, null);
		res.finalizeData();
		return res;
	}

	//statistical data
	private Vector<Integer> V_not_ci;
	private Vector<Integer> V_not_dj;
	private Vector<Integer> M_tk;

	private void finalizeData() {
		int labelSize = this.labelNameContainer.size();
		for (int i = 0; i < labelSize; i++) {
			this.V_not_ci.add(this.featureContainer.difference(this.labelFeatureContainers.get(i)));
		}

		for (int i = 0; i < docCnt; i++) {
			this.V_not_dj.add(this.featureContainer.difference(this.documentContainers.get(i)));
		}

		int featureSize = this.featureContainer.size();
		for (int i = 0; i < featureSize; i++) {
			String feature = this.featureContainer.getWord(i);
			int cnt = 0;
			for (int j = 0; j < labelSize; j++) {
				if (this.labelFeatureContainers.get(j).contains(feature)) cnt++;
			}
			this.M_tk.add(cnt);
		}
	}

	@Override
	public int getV_not_ci(int labelId) {
		return this.V_not_ci.get(labelId);
	}

	@Override
	public int getV_not_dj(int docId) {
		return this.V_not_dj.get(docId);
	}

	@Override
	public int getM_tk(int featureId) {
		return this.M_tk.get(featureId);
	}

	public int getFeatureCnt() {
		return getV();
	}

	public int getLabelCnt() {
		return getM();
	}
}
