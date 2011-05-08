package core.preprocess.util;

import java.util.Vector;
import java.util.HashSet;

import core.preprocess.util.Trie;

/**
 * this class provide a tool for analyzing the document data and generating
 * corresponding statistical data
 * 
 * @author lambda
 * 
 */
public class DataAnalyzer extends DataHolder {
	private boolean addingOK; //whether function "accomplishAdding" has been invoked before

	/**
	 * constructor
	 */
	public DataAnalyzer() {
		this.docCnt = 0;
		this.addingOK = false;

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

		HashSet<String> wordMap = new HashSet<String>(512);

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
}