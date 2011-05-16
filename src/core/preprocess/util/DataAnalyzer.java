package core.preprocess.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	/**
	 * constructor
	 */
	public DataAnalyzer() {
		super();
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

	public static DataAnalyzer deserialize(File inputDir, int[] eliminatedId, boolean rearrangeId) throws Exception {
		DataAnalyzer res = new DataAnalyzer();
		DataHolder.deserialize(res, inputDir, eliminatedId);
		if (rearrangeId) res.featureTrie.rearrangeId();//do not forget to do this!
		return res;
	}

	public void serialize(File outputDir) throws Exception {
		outputDir.mkdirs();
		FileWriter fw;
		BufferedWriter bw;
		File sizeFile;

		File docLabelFile = new File(outputDir, Constant.DOC_LABELS_FILE);
		fw = new FileWriter(docLabelFile);
		bw = new BufferedWriter(fw);
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

		this.featureTrie.serialize(new File(outputDir, Constant.FEATURE_TRIE_FILE), true, null);
		this.featureTrieAddedPerDoc.serialize(new File(outputDir, Constant.FEATURE_TRIE_ADDED_PER_DOC_FILE), false, this.featureTrie);

		File documentTriesFolder = new File(outputDir, Constant.DOCUMENT_TRIES_FOLDER);
		documentTriesFolder.mkdirs();
		sizeFile = new File(documentTriesFolder, Constant.DOCUMENT_TRIES_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.documentTries.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.documentTries.size(); i++) {
			this.documentTries.get(i).serialize(new File(documentTriesFolder, String.valueOf(i)), false, this.featureTrie);
		}

		this.labelNameTrie.serialize(new File(outputDir, Constant.LABEL_NAME_TRIE_FILE), true, null);

		File labelFeatureTriesFolder = new File(outputDir, Constant.LABEL_FEATURE_TRIES_FOLDER);
		labelFeatureTriesFolder.mkdirs();
		sizeFile = new File(labelFeatureTriesFolder, Constant.LABEL_FEATURE_TRIES_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.labelFeatureTries.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.labelFeatureTries.size(); i++) {
			this.labelFeatureTries.get(i).serialize(new File(labelFeatureTriesFolder, String.valueOf(i)), false, this.featureTrie);
		}

		File labelFeatureTriesAddedPerDocFolder = new File(outputDir, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER);
		labelFeatureTriesAddedPerDocFolder.mkdirs();
		sizeFile = new File(labelFeatureTriesAddedPerDocFolder, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.labelFeatureTriesAddedPerDoc.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.labelFeatureTriesAddedPerDoc.size(); i++) {
			this.labelFeatureTriesAddedPerDoc.get(i).serialize(new File(labelFeatureTriesAddedPerDocFolder, String.valueOf(i)), false,
					this.featureTrie);
		}
	}
}