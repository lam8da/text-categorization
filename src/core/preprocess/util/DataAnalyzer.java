package core.preprocess.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		super();
		this.addingOK = false;
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
}