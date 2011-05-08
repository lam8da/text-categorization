package core.preprocess.util;

import java.util.Vector;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;

import core.preprocess.util.Constant;

public class DataHolder {
	protected int docCnt; //equal to documentTries.size()
	protected Vector<String[]> docLabels;
	protected Trie featureTrie; //features
	protected Trie featureTrieAddedPerDoc; //one feature will only be added once for each document containing that feature
	protected Vector<Trie> documentTries; //features per document
	protected Trie labelNameTrie; //labels
	protected Vector<Trie> labelFeatureTries; //features (duplicated) per label
	protected Vector<Trie> labelFeatureTriesAddedPerDoc; //features (unduplicated per document) per label

	public void serialize(File outputDir) throws IOException {
		outputDir.mkdirs();

		File docLabelFile = new File(outputDir, Constant.DOC_LABELS_FILE);
		FileWriter fw = new FileWriter(docLabelFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(String.valueOf(docCnt));
		bw.newLine();
		for (int i = 0; i < docCnt; i++) {
			String[] l = this.docLabels.get(i);
			bw.write(String.valueOf(l.length));
			bw.newLine();
			for (int j = 0; j < l.length; j++) {
				bw.write(l[j]);
				bw.newLine();
			}
		}
		bw.flush();
		bw.close();
		fw.close();

		this.featureTrie.serialize(new File(outputDir, Constant.FEATURE_TRIE_FILE));
		this.featureTrieAddedPerDoc.serialize(new File(outputDir, Constant.FEATURE_TRIE_ADDED_PER_DOC_FILE));

		File documentTriesFolder = new File(outputDir, Constant.DOCUMENT_TRIES_FOLDER);
		for (int i = 0; i < this.documentTries.size(); i++) {
			this.documentTries.get(i).serialize(new File(documentTriesFolder, String.valueOf(i)));
		}

		this.labelNameTrie.serialize(new File(outputDir, Constant.LABEL_NAME_TRIE_FILE));

		File labelFeatureTriesFolder = new File(outputDir, Constant.LABEL_FEATURE_TRIES_FOLDER);
		for (int i = 0; i < this.labelFeatureTries.size(); i++) {
			this.labelFeatureTries.get(i).serialize(new File(labelFeatureTriesFolder, String.valueOf(i)));
		}

		File labelFeatureTriesAddedPerDocFolder = new File(outputDir, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER);
		for (int i = 0; i < this.labelFeatureTriesAddedPerDoc.size(); i++) {
			this.labelFeatureTriesAddedPerDoc.get(i).serialize(new File(labelFeatureTriesAddedPerDocFolder, String.valueOf(i)));
		}
	}

	public void deserialize() {

	}

	/************************************ meta data ************************************/

	public String[] getDocLabels(int docId) {
		return this.docLabels.get(docId);
	}

	public int getFeatureId(String feature) {
		return this.featureTrie.getId(feature);
	}

	public String getFeature(int featureId) {
		return this.featureTrie.getWord(featureId);
	}

	public int getLabelId(String label) {
		return this.labelNameTrie.getId(label);
	}

	public String getLabel(int labelId) {
		return this.labelNameTrie.getWord(labelId);
	}

	/******************************** document counting ********************************/

	public int getN() {
		return this.docCnt;
	}

	public int getN_ci(int labelId) {
		String label = this.labelNameTrie.getWord(labelId);
		return getN_ci(label);
	}

	public int getN_ci(String label) {
		return this.labelNameTrie.getOccurrence(label);
	}

	public int getN_not_ci(int labelId) {
		return getN() - getN_ci(labelId);
	}

	public int getN_not_ci(String label) {
		return getN() - getN_ci(label);
	}

	public int getN_tk(int featureId) {
		String feature = this.featureTrie.getWord(featureId);
		return getN_tk(feature);
	}

	public int getN_tk(String feature) {
		return this.featureTrieAddedPerDoc.getOccurrence(feature);
	}

	public int getN_exclude_tk(int featureId) {
		return getN() - getN_tk(featureId);
	}

	public int getN_exclude_tk(String feature) {
		return getN() - getN_tk(feature);
	}

	public int getN_ci_tk(int labelId, int featureId) {
		Trie labelTrie = this.labelFeatureTriesAddedPerDoc.get(labelId);
		String feature = this.featureTrie.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getN_ci_tk(String label, String feature) {
		int labelId = this.labelNameTrie.getId(label);
		Trie labelTrie = this.labelFeatureTriesAddedPerDoc.get(labelId);
		return labelTrie.getOccurrence(feature);
	}

	public int getN_not_ci_tk(int labelId, int featureId) {
		return getN_tk(featureId) - getN_ci_tk(labelId, featureId);
	}

	public int getN_not_ci_tk(String label, String feature) {
		return getN_tk(feature) - getN_ci_tk(label, feature);
	}

	public int getN_ci_exclude_tk(int labelId, int featureId) {
		return getN_ci(labelId) - getN_ci_tk(labelId, featureId);
	}

	public int getN_ci_exclude_tk(String label, String feature) {
		return getN_ci(label) - getN_ci_tk(label, feature);
	}

	public int getN_not_ci_exclude_tk(int labelId, int featureId) {
		return getN_exclude_tk(featureId) - getN_ci_exclude_tk(labelId, featureId);
	}

	public int getN_not_ci_exclude_tk(String label, String feature) {
		return getN_not_ci(label) - getN_not_ci_tk(label, feature);
	}

	/********************************** word counting **********************************/

	public int getW() {
		return this.featureTrie.getCounting();
	}

	public int getW_ci(int labelId) {
		return this.labelFeatureTries.get(labelId).getCounting();
	}

	public int getW_ci(String label) {
		int labelId = this.labelNameTrie.getId(label);
		return getW_ci(labelId);
	}

	public int getW_not_ci(int labelId) {
		return getW() - getW_ci(labelId);
	}

	public int getW_not_ci(String label) {
		int labelId = this.labelNameTrie.getId(label);
		return getW() - getW_ci(labelId);
	}

	public int getW_dj(int docId) {
		return this.documentTries.get(docId).getCounting();
	}

	public int getW_not_dj(int docId) {
		return getW() - getW_dj(docId);
	}

	/*************************** single feature word counting ***************************/

	public int getW_tk(int featureId) {
		String feature = this.featureTrie.getWord(featureId);
		return getW_tk(feature);
	}

	public int getW_tk(String feature) {
		return this.featureTrie.getOccurrence(feature);
	}

	public int getW_ci_tk(int labelId, int featureId) {
		Trie labelTrie = this.labelFeatureTries.get(labelId);
		String feature = this.featureTrie.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getW_ci_tk(String label, String feature) {
		Trie labelTrie = this.labelFeatureTries.get(this.labelNameTrie.getId(label));
		return labelTrie.getOccurrence(feature);
	}

	public int getW_not_ci_tk(int labelId, int featureId) {
		return getW_tk(featureId) - getW_ci_tk(labelId, featureId);
	}

	public int getW_not_ci_tk(String label, String feature) {
		return getW_tk(feature) - getW_ci_tk(label, feature);
	}

	public int getW_dj_tk(int docId, int featureId) {
		String feature = this.featureTrie.getWord(featureId);
		return getW_dj_tk(docId, feature);
	}

	public int getW_dj_tk(int docId, String feature) {
		Trie docTrie = this.documentTries.get(docId);
		return docTrie.getOccurrence(feature);
	}

	public int getW_not_dj_tk(int docId, int featureId) {
		return getW_tk(featureId) - getW_dj_tk(docId, featureId);
	}

	public int getW_not_dj_tk(int docId, String feature) {
		return getW_tk(feature) - getW_dj_tk(docId, feature);
	}

	/********************************* feature counting *********************************/

	public int getV() {
		return this.featureTrie.size();
	}

	public int getV_ci(int labelId) {
		return this.labelFeatureTries.get(labelId).size();
	}

	public int getV_ci(String label) {
		int labelId = this.labelNameTrie.getId(label);
		return getV_ci(labelId);
	}

	public int getV_ci_exclude(int labelId) {
		return getV() - getV_ci(labelId);
	}

	public int getV_ci_exclude(String label) {
		return getV() - getV_ci(label);
	}

	public int getV_not_ci(int labelId) {
		return this.featureTrie.difference(this.labelFeatureTries.get(labelId));
	}

	public int getV_not_ci(String label) {
		int labelId = this.labelNameTrie.getId(label);
		return getV_not_ci(labelId);
	}

	public int getV_not_ci_exclude(int labelId) {
		return getV() - getV_not_ci(labelId);
	}

	public int getV_not_ci_exclude(String label) {
		int labelId = this.labelNameTrie.getId(label);
		return getV_not_ci_exclude(labelId);
	}

	public int getV_dj(int docId) {
		return this.documentTries.get(docId).size();
	}

	public int getV_dj_exclude(int docId) {
		return getV() - getV_dj(docId);
	}

	public int getV_not_dj(int docId) {
		return this.featureTrie.difference(this.documentTries.get(docId));
	}

	public int getV_not_dj_exclude(int docId) {
		return getV() - getV_not_dj(docId);
	}

	/********************************** label counting **********************************/

	public int getM() {
		return this.labelNameTrie.size();
	}

	public int getM_tk(int featureId) {
		String feature = this.featureTrie.getWord(featureId);
		int cnt = 0, labelSize = this.labelNameTrie.size();
		for (int i = 0; i < labelSize; i++) {
			if (this.labelFeatureTries.get(i).contains(feature)) cnt++;
		}
		return cnt;
	}

	public int getM_tk(String feature) {
		int featureId = this.featureTrie.getId(feature);
		return getM_tk(featureId);
	}

	public int getM_exclude_tk(int featureId) {
		return getM() - getM_tk(featureId);
	}

	public int getM_exclude_tk(String feature) {
		return getM() - getM_tk(feature);
	}

	/************************************************************************************/
}
