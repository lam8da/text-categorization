package core.classifier.util;

import java.io.File;
import java.util.Vector;

import core.preprocess.util.DataHolder;

public final class FinalDataHolder extends DataHolder {
	private FinalDataHolder() {
		super();
		this.V_not_ci = new Vector<Integer>(256);
		this.V_not_dj = new Vector<Integer>(16384);
		this.M_tk = new Vector<Integer>(8192);
	}

	public static FinalDataHolder deserialize(File inputDir) throws Exception {
		FinalDataHolder res = new FinalDataHolder();
		deserialize(res, inputDir, null);
		res.finalizeData();
		return res;
	}

	//statistical data
	private Vector<Integer> V_not_ci;
	private Vector<Integer> V_not_dj;
	private Vector<Integer> M_tk;

	private void finalizeData() {
		int labelSize = this.labelNameTrie.size();
		for (int i = 0; i < labelSize; i++) {
			this.V_not_ci.add(this.featureTrie.difference(this.labelFeatureTries.get(i)));
		}

		for (int i = 0; i < docCnt; i++) {
			this.V_not_dj.add(this.featureTrie.difference(this.documentTries.get(i)));
		}

		int featureSize = this.featureTrie.size();
		for (int i = 0; i < featureSize; i++) {
			String feature = this.featureTrie.getWord(i);
			int cnt = 0;
			for (int j = 0; j < labelSize; j++) {
				if (this.labelFeatureTries.get(j).contains(feature)) cnt++;
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
