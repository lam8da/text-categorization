package core.preprocess.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.TreeSet;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import core.preprocess.util.Constant;
import core.preprocess.util.Trie;

/**
 * this class provide a tool for analyzing the document data and generating
 * corresponding statistical data
 * 
 * @author lambda
 * 
 */
public class DataAnalyzer {
	private boolean addingOK; //whether function "accomplishAdding" has been invoked before
	private boolean reducingOK; //whether function "accomplishReducing" has been invoked before
	private int docCnt; //equal to documentTries.size()
	private Vector<String[]> docLabels;
	private Trie featureTrie; //features
	private Trie featureTrieAddedPerDoc; //one feature will only be added once for each document containing that feature
	private Vector<Trie> documentTries; //features per document
	private Trie labelNameTrie; //labels
	private Vector<Trie> labelFeatureTries; //features (duplicated) per label
	private Vector<Trie> labelFeatureTriesAddedPerDoc; //features (unduplicated per document) per label

	/**
	 * constructor
	 */
	public DataAnalyzer() {
		this.docCnt = 0;
		this.addingOK = false;
		this.reducingOK = false;

		this.docLabels = new Vector<String[]>();
		this.featureTrie = new Trie();
		this.featureTrieAddedPerDoc = new Trie();
		this.labelFeatureTriesAddedPerDoc = new Vector<Trie>(256);
		this.labelNameTrie = new Trie();
		this.documentTries = new Vector<Trie>(8192);
		this.labelFeatureTries = new Vector<Trie>(256);
	}

	/**
	 * add a document to the analyzer who will do some calculation to record the
	 * information of the document
	 * 
	 * @param labels
	 *            document labels
	 * @param titleFeatures
	 *            features in document title
	 * @param contentFeatures
	 *            features in document body
	 * @throws Exception
	 *             if we have invoked finish() before, no more documents can be
	 *             added, or there will be an exception
	 */
	public void addDocument(String[] labels, String[] titleFeatures, String[] contentFeatures) throws Exception {
		if (this.addingOK) {
			throw new Exception("cannot add document after the statistical data was created!");
		}

		this.docCnt++;
		this.docLabels.add(labels);
		int[] labelIds = new int[labels.length];

		// we ignore the specialness of the title and treat it as normal document content at present
		String[] ptr = new String[titleFeatures.length + contentFeatures.length];
		int copyIdx;
		for (copyIdx = 0; copyIdx < titleFeatures.length; ptr[copyIdx] = titleFeatures[copyIdx], copyIdx++);
		for (; copyIdx < titleFeatures.length + contentFeatures.length; ptr[copyIdx] = contentFeatures[copyIdx - titleFeatures.length], copyIdx++);

		int labelNameTrieSize = this.labelNameTrie.size();
		for (int i = 0; i < labels.length; i++) {
			int labelId = this.labelNameTrie.add(labels[i]); //label id starts from 0
			labelIds[i] = labelId;

			if (labelNameTrieSize == this.labelNameTrie.size() - 1) { //a new label
				labelNameTrieSize++;
				this.labelFeatureTriesAddedPerDoc.add(new Trie());
				this.labelFeatureTries.add(new Trie());

				if (this.labelFeatureTriesAddedPerDoc.size() != this.labelNameTrie.size()) {
					throw new Exception("fatal error occurs in program logic!");
				}
				if (this.labelFeatureTries.size() != this.labelNameTrie.size()) {
					throw new Exception("fatal error occurs in program logic!");
				}
			}
		}

		Trie docTrie = new Trie();
		this.documentTries.add(docTrie);

		TreeSet<String> wordMap = new TreeSet<String>();

		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i].length() > 0) {
				String fea = ptr[i];
				//System.out.println(fea);

				this.featureTrie.add(fea);
				docTrie.add(fea);
				for (int j = 0; j < labels.length; j++) {
					this.labelFeatureTries.get(labelIds[j]).add(fea);
				}

				if (!wordMap.contains(fea)) {
					wordMap.add(fea);
					this.featureTrieAddedPerDoc.add(fea);
					for (int j = 0; j < labels.length; j++) {
						this.labelFeatureTriesAddedPerDoc.get(labelIds[j]).add(fea);
					}
				}
			}
		}
	}

	public void accomplishAdding() {
		this.addingOK = true;
	}

	public void accomplishReducing() {
		this.reducingOK = true;
	}

	/**
	 * eliminate a feature given by "feature", mainly invoked in feature
	 * selection phase
	 * 
	 * @param feature
	 *            the feature to be eliminated
	 * @throws Exception
	 *             if this function is invoked after "finish", an exception will
	 *             occur
	 */
	public void reduce(String feature) throws Exception {
		if (!this.addingOK) throw new Exception("feature selection is not allowed before the process of adding documents is finished!");
		if (this.reducingOK) throw new Exception("feature selection is not allowed after \"accomplishReducing\" was invoked!");

		this.featureTrie.delete(feature);
		this.featureTrieAddedPerDoc.delete(feature);

		for (int i = 0; i < this.documentTries.size(); i++) {
			this.documentTries.get(i).delete(feature);
		}

		for (int i = 0; i < this.labelFeatureTries.size(); i++) {
			this.labelFeatureTries.get(i).delete(feature);
		}

		for (int i = 0; i < this.labelFeatureTriesAddedPerDoc.size(); i++) {
			this.labelFeatureTriesAddedPerDoc.get(i).delete(feature);
		}
	}

	/********************************* output routines *********************************/

	//the following variables are just for convenience
	private int featureCnt;
	private int labelCnt;
	private String[] features;
	private String[] labels;

	/**
	 * output the result of analysis
	 * 
	 * @param outputDir
	 *            the directory where the output files should be placed
	 * @throws Exception
	 *             if this function is invoked before "finish", an exception
	 *             will occur
	 */
	public void writeToFile(File outputDir) throws Exception {
		if (!this.reducingOK) throw new Exception("file writing is not allowed before the process of feature selection is finished!");
		featureCnt = getV();
		labelCnt = getM();
		features = new String[featureCnt];
		labels = new String[labelCnt];

		for (int i = 0; i < featureCnt; i++) {
			features[i] = getFeature(i);
		}
		for (int i = 0; i < labelCnt; i++) {
			labels[i] = getLabel(i);
		}

		outputDir.mkdirs();

		writeMetaData(outputDir);
		writeDocumentCounting(outputDir);
		writeWordCounting(outputDir);
		writeSingleFeatureWordCounting(outputDir);
		writeFeatureCounting(outputDir);
		writeLabelCounting(outputDir);
	}

	private void writeMetaData(File outputDir) throws IOException {
		File currentDir = new File(outputDir, Constant.META_DATA_FOLDER);
		currentDir.mkdirs();

		FileWriter fw;
		BufferedWriter bw;

		File featureFile = new File(currentDir, Constant.FEATURE_FILE);
		fw = new FileWriter(featureFile);
		bw = new BufferedWriter(fw);
		bw.write(featureCnt);
		bw.newLine();
		for (int i = 0; i < featureCnt; i++) {
			bw.write(i + " " + features[i]); //format: featureId feature
			bw.newLine();
		}
		bw.flush();
		bw.close();
		fw.close();

		File labelFile = new File(currentDir, Constant.LABEL_FILE);
		fw = new FileWriter(labelFile);
		bw = new BufferedWriter(fw);
		bw.write(labelCnt);
		bw.newLine();
		for (int i = 0; i < labelCnt; i++) {
			bw.write(i + " " + labels[i]); //format: labelId label
			bw.newLine();
		}
		bw.flush();
		bw.close();
		fw.close();

		File docLabelFile = new File(currentDir, Constant.DOC_LABEL_FILE);
		fw = new FileWriter(docLabelFile);
		bw = new BufferedWriter(fw);
		bw.write(docCnt);
		bw.newLine();
		for (int i = 0; i < docCnt; i++) {
			String[] l = getDocLabels(i);
			bw.write(l.length + " ");
			for (int j = 0; j < l.length; j++) {
				bw.write(l[j] + " ");
			}
			bw.newLine();
		}
		bw.flush();
		bw.close();
		fw.close();
	}

	private void writeDocumentCounting(File outputDir) throws IOException {
		File currentDir = new File(outputDir, Constant.DOCUMENT_COUNTING_FOLDER);
		currentDir.mkdirs();

		FileWriter fw;
		BufferedWriter bw;

		File fileN_ci = new File(currentDir, Constant.N_ci);
		fw = new FileWriter(fileN_ci);
		bw = new BufferedWriter(fw);
		bw.write(labelCnt);
		bw.newLine();
		for (int i = 0; i < labelCnt; i++) {
			bw.write(getN_ci(labels[i]) + " ");
		}
		bw.flush();
		bw.close();
		fw.close();

		File fileN_not_ci = new File(currentDir, Constant.N_not_ci);
		fw = new FileWriter(fileN_not_ci);
		bw = new BufferedWriter(fw);
		bw.write(labelCnt);
		bw.newLine();
		for (int i = 0; i < labelCnt; i++) {
			bw.write(getN_not_ci(labels[i]) + " ");
		}
		bw.flush();
		bw.close();
		fw.close();

		File fileN_tk = new File(currentDir, Constant.N_tk);
		fw = new FileWriter(fileN_tk);
		bw = new BufferedWriter(fw);
		bw.write(featureCnt);
		bw.newLine();
		for (int i = 0; i < featureCnt; i++) {
			bw.write(getN_tk(features[i]) + " ");
		}
		bw.flush();
		bw.close();
		fw.close();

		File fileN_exclude_tk = new File(currentDir, Constant.N_exclude_tk);
		fw = new FileWriter(fileN_exclude_tk);
		bw = new BufferedWriter(fw);
		bw.write(featureCnt);
		bw.newLine();
		for (int i = 0; i < featureCnt; i++) {
			bw.write(getN_exclude_tk(features[i]) + " ");
		}
		bw.flush();
		bw.close();
		fw.close();

		File fileN_ci_tk = new File(currentDir, Constant.N_ci_tk);
		fw = new FileWriter(fileN_ci_tk);
		bw = new BufferedWriter(fw);
		bw.write(labelCnt);
		bw.newLine();
		for (int i = 0; i < labelCnt; i++) {
			Trie labelTrie = this.labelFeatureTriesAddedPerDoc.get(i);
			Trie.NodeData[] data = labelTrie.serialize();
			for (int j = 0; j < data.length; j++) {
				data[j].id = this.featureTrie.getId(data[j].str);
			}
			Arrays.sort(data, 0, data.length, new Comparator<Trie.NodeData>() {
				public int compare(Trie.NodeData o1, Trie.NodeData o2) {
					return o1.id - o2.id;
				}
			});
			//backup the current procedure 
		}
		bw.flush();
		bw.close();
		fw.close();

		File fileN_not_ci_tk = new File(currentDir, Constant.N_not_ci_tk);
		fw = new FileWriter(fileN_not_ci_tk);
		bw = new BufferedWriter(fw);
		bw.flush();
		bw.close();
		fw.close();

		File fileN_ci_exclude_tk = new File(currentDir, Constant.N_ci_exclude_tk);
		fw = new FileWriter(fileN_ci_exclude_tk);
		bw = new BufferedWriter(fw);
		bw.flush();
		bw.close();
		fw.close();

		File fileN_not_ci_exclude_tk = new File(currentDir, Constant.N_not_ci_exclude_tk);
		fw = new FileWriter(fileN_not_ci_exclude_tk);
		bw = new BufferedWriter(fw);
		bw.flush();
		bw.close();
		fw.close();
	}

	private void writeWordCounting(File outputDir) throws IOException {
		File currentDir = new File(outputDir, Constant.WORD_COUNTING_FOLDER);
		currentDir.mkdirs();
	}

	private void writeSingleFeatureWordCounting(File outputDir) throws IOException {
		File currentDir = new File(outputDir, Constant.SINGLE_FEATURE_WORD_COUNTING_FOLDER);
		currentDir.mkdirs();
	}

	private void writeFeatureCounting(File outputDir) throws IOException {
		File currentDir = new File(outputDir, Constant.FEATURE_COUNTING_FOLDER);
		currentDir.mkdirs();
	}

	private void writeLabelCounting(File outputDir) throws IOException {
		File currentDir = new File(outputDir, Constant.LABEL_COUNTING_FOLDER);
		currentDir.mkdirs();
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