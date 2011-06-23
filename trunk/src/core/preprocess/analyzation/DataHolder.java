package core.preprocess.analyzation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.analyzation.interfaces.SimpleContainer;
import core.preprocess.util.Constant;

public class DataHolder {
	protected ContainerGenerator generator;
	protected int docCnt; //equal to documentTries.size()
	protected Vector<String[]> docLabels;
	protected FeatureContainer featureContainer; //features
	protected SimpleContainer featureAddedPerDoc; //one feature will only be added once for each document containing that feature
	protected Vector<SimpleContainer> documentContainers; //features per document
	protected FeatureContainer labelNameContainer; //labels
	protected Vector<SimpleContainer> labelFeatureContainers; //features (duplicated) per label
	protected Vector<SimpleContainer> labelFeatureContainersAddedPerDoc; //features (unduplicated per document) per label

	protected DataHolder(ContainerGenerator g) {
		this.generator = g;
		this.docCnt = 0;
		this.docLabels = new Vector<String[]>();
		this.featureContainer = g.generateFeatureContainer();
		this.featureAddedPerDoc = g.generateSimpleContainer();
		this.labelFeatureContainersAddedPerDoc = new Vector<SimpleContainer>(256);
		this.labelNameContainer = g.generateFeatureContainer();
		this.documentContainers = new Vector<SimpleContainer>(8192);
		this.labelFeatureContainers = new Vector<SimpleContainer>(256);
	}

	private static void loadTrieVector(//
			ContainerGenerator g,//
			Vector<SimpleContainer> vec,//
			File inputDir,//
			String folderName,//
			String metaFilename, FeatureContainer mapIdToString,//
			int[] eliminatedId//
	) throws Exception {

		File triesFolder = new File(inputDir, folderName);
		File sizeFile = new File(triesFolder, metaFilename);
		FileReader fr = new FileReader(sizeFile);
		BufferedReader br = new BufferedReader(fr);
		int size = Integer.parseInt(br.readLine());
		br.close();
		fr.close();

		for (int i = 0; i < size; i++) {
			//these are all SimpleTries, so "true"
			SimpleContainer st = g.generateSimpleContainer();
			st.deserializeFrom(new File(triesFolder, String.valueOf(i)), mapIdToString, eliminatedId);
			vec.add(st);
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

		res.featureContainer.deserializeFrom(new File(inputDir, Constant.FEATURE_TRIE_FILE), eliminatedId);
		res.featureAddedPerDoc.deserializeFrom(new File(inputDir, Constant.FEATURE_TRIE_ADDED_PER_DOC_FILE), res.featureContainer, eliminatedId);
		res.labelNameContainer.deserializeFrom(new File(inputDir, Constant.LABEL_NAME_TRIE_FILE), null);

		loadTrieVector(//
				res.generator,//
				res.documentContainers,//
				inputDir,//
				Constant.DOCUMENT_TRIES_FOLDER,//
				Constant.DOCUMENT_TRIES_FOLDER_SIZE_FILE,//
				res.featureContainer,//
				eliminatedId//
		);

		loadTrieVector(//
				res.generator,//
				res.labelFeatureContainers,//
				inputDir,//
				Constant.LABEL_FEATURE_TRIES_FOLDER,//
				Constant.LABEL_FEATURE_TRIES_FOLDER_SIZE_FILE,//
				res.featureContainer,//
				eliminatedId//
		);

		loadTrieVector(//
				res.generator,//
				res.labelFeatureContainersAddedPerDoc,//
				inputDir,//
				Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER,//
				Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER_SIZE_FILE,//
				res.featureContainer,//
				eliminatedId//
		);
	}

	/************************************ meta data ************************************/

	public String[] getDocLabels(int docId) {
		return this.docLabels.get(docId);
	}

	public int getFeatureId(String feature) {
		return this.featureContainer.getId(feature);
	}

	public String getFeature(int featureId) {
		return this.featureContainer.getWord(featureId);
	}

	public int getLabelId(String label) {
		return this.labelNameContainer.getId(label);
	}

	public String getLabel(int labelId) {
		return this.labelNameContainer.getWord(labelId);
	}

	/******************************** document counting ********************************/

	public int getN() {
		return this.docCnt;
	}

	public int getN_ci(int labelId) {
		String label = this.labelNameContainer.getWord(labelId);
		return getN_ci(label);
	}

	public int getN_ci(String label) {
		return this.labelNameContainer.getOccurrence(label);
	}

	public int getN_not_ci(int labelId) {
		return getN() - getN_ci(labelId);
	}

	public int getN_not_ci(String label) {
		return getN() - getN_ci(label);
	}

	public int getN_tk(int featureId) {
		String feature = this.featureContainer.getWord(featureId);
		return getN_tk(feature);
	}

	public int getN_tk(String feature) {
		return this.featureAddedPerDoc.getOccurrence(feature);
	}

	public int getN_exclude_tk(int featureId) {
		return getN() - getN_tk(featureId);
	}

	public int getN_exclude_tk(String feature) {
		return getN() - getN_tk(feature);
	}

	public int getN_ci_tk(int labelId, int featureId) {
		SimpleContainer labelTrie = this.labelFeatureContainersAddedPerDoc.get(labelId);
		String feature = this.featureContainer.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getN_ci_tk(String label, String feature) {
		int labelId = this.labelNameContainer.getId(label);
		SimpleContainer labelTrie = this.labelFeatureContainersAddedPerDoc.get(labelId);
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
		return this.featureContainer.getCounting();
	}

	public int getW_ci(int labelId) {
		return this.labelFeatureContainers.get(labelId).getCounting();
	}

	public int getW_ci(String label) {
		int labelId = this.labelNameContainer.getId(label);
		return getW_ci(labelId);
	}

	public int getW_not_ci(int labelId) {
		return getW() - getW_ci(labelId);
	}

	public int getW_not_ci(String label) {
		int labelId = this.labelNameContainer.getId(label);
		return getW() - getW_ci(labelId);
	}

	public int getW_dj(int docId) {
		return this.documentContainers.get(docId).getCounting();
	}

	public int getW_not_dj(int docId) {
		return getW() - getW_dj(docId);
	}

	/*************************** single feature word counting ***************************/

	public int getW_tk(int featureId) {
		String feature = this.featureContainer.getWord(featureId);
		return getW_tk(feature);
	}

	public int getW_tk(String feature) {
		return this.featureContainer.getOccurrence(feature);
	}

	public int getW_ci_tk(int labelId, int featureId) {
		SimpleContainer labelTrie = this.labelFeatureContainers.get(labelId);
		String feature = this.featureContainer.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getW_ci_tk(String label, String feature) {
		SimpleContainer labelTrie = this.labelFeatureContainers.get(this.labelNameContainer.getId(label));
		return labelTrie.getOccurrence(feature);
	}

	public int getW_not_ci_tk(int labelId, int featureId) {
		return getW_tk(featureId) - getW_ci_tk(labelId, featureId);
	}

	public int getW_not_ci_tk(String label, String feature) {
		return getW_tk(feature) - getW_ci_tk(label, feature);
	}

	public int getW_dj_tk(int docId, int featureId) {
		String feature = this.featureContainer.getWord(featureId);
		return getW_dj_tk(docId, feature);
	}

	public int getW_dj_tk(int docId, String feature) {
		SimpleContainer docTrie = this.documentContainers.get(docId);
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
		return this.featureContainer.size();
	}

	public int getV_ci(int labelId) {
		return this.labelFeatureContainers.get(labelId).size();
	}

	public int getV_ci(String label) {
		int labelId = this.labelNameContainer.getId(label);
		return getV_ci(labelId);
	}

	public int getV_ci_exclude(int labelId) {
		return getV() - getV_ci(labelId);
	}

	public int getV_ci_exclude(String label) {
		return getV() - getV_ci(label);
	}

	public int getV_not_ci(int labelId) {
		return this.featureContainer.difference(this.labelFeatureContainers.get(labelId));
	}

	public int getV_not_ci(String label) {
		int labelId = this.labelNameContainer.getId(label);
		return getV_not_ci(labelId);
	}

	public int getV_not_ci_exclude(int labelId) {
		return getV() - getV_not_ci(labelId);
	}

	public int getV_not_ci_exclude(String label) {
		int labelId = this.labelNameContainer.getId(label);
		return getV_not_ci_exclude(labelId);
	}

	public int getV_dj(int docId) {
		return this.documentContainers.get(docId).size();
	}

	public int getV_dj_exclude(int docId) {
		return getV() - getV_dj(docId);
	}

	public int getV_not_dj(int docId) {
		return this.featureContainer.difference(this.documentContainers.get(docId));
	}

	public int getV_not_dj_exclude(int docId) {
		return getV() - getV_not_dj(docId);
	}

	/********************************** label counting **********************************/

	public int getM() {
		return this.labelNameContainer.size();
	}

	public int getM_tk(int featureId) {
		String feature = this.featureContainer.getWord(featureId);
		int cnt = 0, labelSize = this.labelNameContainer.size();
		for (int i = 0; i < labelSize; i++) {
			if (this.labelFeatureContainers.get(i).contains(feature)) cnt++;
		}
		return cnt;
	}

	public int getM_tk(String feature) {
		int featureId = this.featureContainer.getId(feature);
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
