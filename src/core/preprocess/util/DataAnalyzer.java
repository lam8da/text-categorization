package core.preprocess.util;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.TreeMap;
import java.util.TreeSet;

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
	private File trainingFolder;
	private int gramSize; //the gram size of the feature. Note that stopping should not be used when gramSize>1
	private int docCnt; //equal to documentTries.size()
	private boolean finished;
	private Trie featureTrie; //one feature will be added as much times as its occurrence in each document
	private Trie featureTriePerDoc; //one feature will only be added once for each document containing that feature
	private Vector<Trie> documentTries;
	private Trie labelNameTrie;
	private Vector<Trie> labelTries; //added as much times as its occurrence in all document whose label is the given class
	private Vector<Trie> labelTriesPerDoc; //only added once for each document containing that feature

	/**
	 * each time we call function "add", only words that hadn't been added to
	 * the trie in current section will be added. Different section is separated
	 * by calling "startNewSection". The TrieNode member "id" is no longer its
	 * original meaning. Here it equals the section id, i.e. sectionCnt.
	 * 
	 * @author lambda
	 * 
	 */
	/*
	 * private class LimitedTrie extends Trie { private int sectionCnt;
	 * 
	 * public LimitedTrie() { super(); this.sectionCnt = 0; }
	 * 
	 * public void startNewSection() { this.sectionCnt++; }
	 * 
	 * @Override public int add(String word) { TrieNode tmp = root; for (int i =
	 * 0; i != word.length(); i++) { if (tmp.child == null) { tmp.child = new
	 * TrieNode(word.charAt(i), 0, null, null, tmp); tmp = tmp.child; } else {
	 * if (tmp.child.val > word.charAt(i)) { // Add To The Head Of The List
	 * TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.child, tmp);
	 * tmp.child = t; tmp = t; } else if (word.charAt(i) == tmp.child.val) { tmp
	 * = tmp.child; } else { TrieNode p = tmp; tmp = tmp.child; boolean flag =
	 * true; while (tmp.brother != null) { if (word.charAt(i) ==
	 * tmp.brother.val) { tmp = tmp.brother; flag = false; break; } else if
	 * (word.charAt(i) < tmp.brother.val) { TrieNode t = new
	 * TrieNode(word.charAt(i), 0, null, tmp.brother, p); tmp.brother = t; tmp =
	 * t; flag = false; break; } tmp = tmp.brother; } if (flag) { TrieNode t =
	 * new TrieNode(word.charAt(i), 0, null, null, p); tmp.brother = t; tmp = t;
	 * } } } } if (tmp.id != this.sectionCnt) { tmp.id = this.sectionCnt; if
	 * (tmp.occurrence == 0) strCnt++; tmp.occurrence++; } return
	 * tmp.occurrence; } }
	 */

	public DataAnalyzer(File trainingFolder, int gramSize) throws Exception {
		this.trainingFolder = trainingFolder;
		this.docCnt = 0;
		this.gramSize = gramSize;
		this.finished = false;

		File metaFile = new File(this.trainingFolder, Constant.EXTRACTION_METADATA_FILENAME);
		if (!metaFile.exists()) {
			throw new IOException("metadata file not exist!");
		}

		BufferedReader reader = new BufferedReader(new FileReader(metaFile));
		String line = reader.readLine();
		if (line.split(" ")[1].equals(Constant.YES) && this.gramSize > 1) {
			throw new Exception("gram size cannot be greater than 1 when stopper is used!");
		}

		this.featureTrie = new Trie();
		this.featureTriePerDoc = new Trie();
		this.labelTriesPerDoc = new Vector<Trie>(256);
		this.labelNameTrie = new Trie();
		this.documentTries = new Vector<Trie>(8192);
		this.labelTries = new Vector<Trie>(256);
	}

	public void addDocument(String[] labels, String title, String content) throws Exception {
		// we ignore the specialness of the title and treat it as normal document content at present
		if (this.finished) {
			throw new Exception("cannot add document after the statistical data be created!");
		}

		this.docCnt++;
		String[] titleFeature = title.split(Constant.WORD_SEPARATING_PATTERN);
		String[] contentFeature = content.split(Constant.WORD_SEPARATING_PATTERN);
		String[] ptr = titleFeature;
		int[] labelIds = new int[labels.length];

		for (int i = 0; i < labels.length; i++) {
			int labelId = this.labelNameTrie.add(labels[i]); //label id starts from 0
			labelIds[i] = labelId;

			if (labelId == this.labelNameTrie.size() - 1) { //a new label
				this.labelTriesPerDoc.add(new Trie());
				this.labelTries.add(new Trie());

				if (this.labelTriesPerDoc.size() != this.labelNameTrie.size()) {
					throw new Exception("fatal error occurs in program logic!");
				}
				if (this.labelTries.size() == this.labelNameTrie.size()) {
					throw new Exception("fatal error occurs in program logic!");
				}
			}
		}

		Trie docTrie = new Trie();
		this.documentTries.add(docTrie);
		//		this.featureTriePerDoc.startNewSection();

		TreeSet<String> wordMap = new TreeSet<String>();

		boolean swap = false;
		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i].length() > 0) {
				String fea = ptr[i];
				System.out.println(fea);

				this.featureTrie.add(fea);
				docTrie.add(fea);
				for (int j = 0; j < labels.length; j++) {
					this.labelTries.get(labelIds[j]).add(fea);
				}

				if (!wordMap.contains(fea)) {
					wordMap.add(fea);
					this.featureTriePerDoc.add(fea);
					for (int j = 0; j < labels.length; j++) {
						this.labelTriesPerDoc.get(labelIds[j]).add(fea);
					}
				}
			}

			if (!swap && i + 1 == ptr.length) {
				ptr = contentFeature;
				i = -1;
				swap = true;
			}
		}
	}

	public void createStatisticalData() {
		this.finished = true;
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
		return this.featureTriePerDoc.getOccurrence(feature);
	}

	public int getN_exclude_tk(int featureId) {
		return getN() - getN_tk(featureId);
	}

	public int getN_exclude_tk(String feature) {
		return getN() - getN_tk(feature);
	}

	public int getN_ci_tk(int labelId, int featureId) {
		Trie labelTrie = this.labelTriesPerDoc.get(labelId);
		String feature = this.featureTrie.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getN_ci_tk(String label, String feature) {
		int labelId = this.labelNameTrie.getId(label);
		Trie labelTrie = this.labelTriesPerDoc.get(labelId);
		return labelTrie.getOccurrence(feature);
	}

	public int getN_not_ci_tk(int labelId, int featureId) {
		return getN_tk(featureId) - getN_ci_tk(labelId, featureId);
	}

	public int getN_ci_exclude_tk(int labelId, int featureId) {
		return getN_ci(labelId) - getN_ci_tk(labelId, featureId);
	}

	public int getN_not_ci_exclude_tk(int labelId, int featureId) {
		return getN_exclude_tk(featureId) - getN_ci_exclude_tk(labelId, featureId);
	}

	/********************************** word counting **********************************/

	public int getW() {
		return this.featureTrie.getCounting();
	}

	public int getW_ci(int labelId) {
		return this.labelTries.get(labelId).getCounting();
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
		Trie labelTrie = this.labelTries.get(labelId);
		String feature = this.featureTrie.getWord(featureId);
		return labelTrie.getOccurrence(feature);
	}

	public int getW_ci_tk(String label, String feature) {
		Trie labelTrie = this.labelTries.get(this.labelNameTrie.getId(label));
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
		return this.labelTries.get(labelId).size();
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

	//--------------------------------------------------
	public int getV_not_ci(int labelId) {
		return -999999999;
	}

	public int getV_not_ci(String label) {
		int labelId = this.labelNameTrie.getId(label);
		return getV_not_ci(labelId);
	}

	public int getV_not_ci_exclude(int labelId) {
		return getV() - getV_ci_exclude(labelId);
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

	//--------------------------------------------------
	public int getV_not_dj(int docId) {
		return -999999999;
	}

	public int getV_not_dj_exclude(int docId) {
		return getV() - getV_not_dj(docId);
	}

	/********************************** label counting **********************************/

	public int getM() {
		return this.labelNameTrie.size();
	}

	//--------------------------------------------------
	public int getM_tk(int featureId) {
		return -999999999;
	}

	//--------------------------------------------------
	public int getM_tk(String feature) {
		return -999999999;
	}

	public int getM_exclude_tk(int featureId) {
		return getM() - getM_tk(featureId);
	}

	public int getM_exclude_tk(String feature) {
		return getM() - getM_tk(feature);
	}

	/************************************************************************************/

	public void writeToFile(File outputDir) throws Exception {

	}
}
