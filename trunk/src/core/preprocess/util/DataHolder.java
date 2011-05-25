package core.preprocess.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

public class DataHolder {
	protected int docCnt; //equal to documentTries.size()
	protected Vector<String[]> docLabels;
	protected Trie featureTrie; //features
	protected SimpleTrie featureTrieAddedPerDoc; //one feature will only be added once for each document containing that feature
	protected Vector<SimpleTrie> documentTries; //features per document
	protected Trie labelNameTrie; //labels
	protected Vector<SimpleTrie> labelFeatureTries; //features (duplicated) per label
	protected Vector<SimpleTrie> labelFeatureTriesAddedPerDoc; //features (unduplicated per document) per label

	protected DataHolder() {
		this.docCnt = 0;

		this.docLabels = new Vector<String[]>();
		this.featureTrie = new Trie();
		this.featureTrieAddedPerDoc = new SimpleTrie();
		this.labelFeatureTriesAddedPerDoc = new Vector<SimpleTrie>(256);
		this.labelNameTrie = new Trie();
		this.documentTries = new Vector<SimpleTrie>(8192);
		this.labelFeatureTries = new Vector<SimpleTrie>(256);
	}

	private static void loadTrieVector(Vector<SimpleTrie> vec, File inputDir, String folderName, String metaFilename, Trie mapIdToString,
			int[] eliminatedId) throws Exception {
		File triesFolder = new File(inputDir, folderName);
		File sizeFile = new File(triesFolder, metaFilename);
		FileReader fr = new FileReader(sizeFile);
		BufferedReader br = new BufferedReader(fr);
		int size = Integer.parseInt(br.readLine());
		br.close();
		fr.close();

		for (int i = 0; i < size; i++) {
			//these are all SimpleTries, so "true"
			vec.add(Trie.deserialize(true, new File(triesFolder, String.valueOf(i)), mapIdToString, eliminatedId));
		}
	}

	protected static void deserialize(DataHolder res, File inputDir, int[] eliminatedId) throws Exception {
		File docLabelFile = new File(inputDir, Constant.DOC_LABELS_FILE);
		FileReader fr = new FileReader(docLabelFile);
		BufferedReader br = new BufferedReader(fr);
		res.docCnt = Integer.parseInt(br.readLine());
		for (int i = 0; i < res.docCnt; i++) {
			int lLength = Integer.parseInt(br.readLine());
			String[] l = new String[lLength];
			for (int j = 0; j < lLength; j++) {
				l[j] = br.readLine();
			}
			res.docLabels.add(l);
		}
		br.close();
		fr.close();

		res.featureTrie = (Trie) Trie.deserialize(false, new File(inputDir, Constant.FEATURE_TRIE_FILE), null, eliminatedId);
		res.featureTrieAddedPerDoc = Trie.deserialize(true, new File(inputDir, Constant.FEATURE_TRIE_ADDED_PER_DOC_FILE), res.featureTrie,
				eliminatedId);
		res.labelNameTrie = (Trie) Trie.deserialize(false, new File(inputDir, Constant.LABEL_NAME_TRIE_FILE), null, null);

		loadTrieVector(res.documentTries, inputDir, Constant.DOCUMENT_TRIES_FOLDER, Constant.DOCUMENT_TRIES_FOLDER_SIZE_FILE, res.featureTrie,
				eliminatedId);
		loadTrieVector(res.labelFeatureTries, inputDir, Constant.LABEL_FEATURE_TRIES_FOLDER, Constant.LABEL_FEATURE_TRIES_FOLDER_SIZE_FILE,
				res.featureTrie, eliminatedId);
		loadTrieVector(res.labelFeatureTriesAddedPerDoc, inputDir, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER,
				Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER_SIZE_FILE, res.featureTrie, eliminatedId);
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
		SimpleTrie labelTrie = this.labelFeatureTriesAddedPerDoc.get(labelId);
		String feature = this.featureTrie.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getN_ci_tk(String label, String feature) {
		int labelId = this.labelNameTrie.getId(label);
		SimpleTrie labelTrie = this.labelFeatureTriesAddedPerDoc.get(labelId);
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
		SimpleTrie labelTrie = this.labelFeatureTries.get(labelId);
		String feature = this.featureTrie.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getW_ci_tk(String label, String feature) {
		SimpleTrie labelTrie = this.labelFeatureTries.get(this.labelNameTrie.getId(label));
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
		SimpleTrie docTrie = this.documentTries.get(docId);
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
