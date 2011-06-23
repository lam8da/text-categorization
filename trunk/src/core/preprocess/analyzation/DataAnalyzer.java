package core.preprocess.analyzation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;

import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.interfaces.SimpleContainer;
import core.preprocess.analyzation.trie.SimpleTrie;
import core.preprocess.util.Constant;

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
	public DataAnalyzer(ContainerGenerator g) {
		super(g);
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

		int labelNameTrieSize = this.labelNameContainer.size();
		for (int i = 0; i < labels.length; i++) {
			int labelId = this.labelNameContainer.add(labels[i]); //label id starts from 0
			labelIds[i] = labelId;

			if (labelNameTrieSize == this.labelNameContainer.size() - 1) { //a new label
				labelNameTrieSize++;
				this.labelFeatureContainersAddedPerDoc.add(new SimpleTrie());
				this.labelFeatureContainers.add(new SimpleTrie());

				if (this.labelFeatureContainersAddedPerDoc.size() != this.labelNameContainer.size()) {
					throw new Exception("fatal error occurs in program logic!");
				}
				if (this.labelFeatureContainers.size() != this.labelNameContainer.size()) {
					throw new Exception("fatal error occurs in program logic!");
				}
			}
		}

		SimpleContainer docTrie = new SimpleTrie();
		this.documentContainers.add(docTrie);

		HashSet<String> wordMap = new HashSet<String>(512);

		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i].length() > 0) {
				String fea = ptr[i];
				//System.out.println(fea);

				this.featureContainer.add(fea);
				docTrie.add(fea);
				for (int j = 0; j < labels.length; j++) {
					this.labelFeatureContainers.get(labelIds[j]).add(fea);
				}

				if (!wordMap.contains(fea)) {
					wordMap.add(fea);
					this.featureAddedPerDoc.add(fea);
					for (int j = 0; j < labels.length; j++) {
						this.labelFeatureContainersAddedPerDoc.get(labelIds[j]).add(fea);
					}
				}
			}
		}
	}

	public static DataAnalyzer deserialize(ContainerGenerator g, File inputDir, int[] eliminatedId, boolean rearrangeId) throws Exception {
		DataAnalyzer res = new DataAnalyzer(g);
		DataHolder.deserialize(res, inputDir, eliminatedId);
		if (rearrangeId) res.featureContainer.rearrangeId();//do not forget to do this!
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

		this.featureContainer.serialize(new File(outputDir, Constant.FEATURE_TRIE_FILE));
		this.featureAddedPerDoc.serialize(new File(outputDir, Constant.FEATURE_TRIE_ADDED_PER_DOC_FILE), this.featureContainer);

		File documentTriesFolder = new File(outputDir, Constant.DOCUMENT_TRIES_FOLDER);
		documentTriesFolder.mkdirs();
		sizeFile = new File(documentTriesFolder, Constant.DOCUMENT_TRIES_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.documentContainers.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.documentContainers.size(); i++) {
			this.documentContainers.get(i).serialize(new File(documentTriesFolder, String.valueOf(i)), this.featureContainer);
		}

		this.labelNameContainer.serialize(new File(outputDir, Constant.LABEL_NAME_TRIE_FILE));

		File labelFeatureTriesFolder = new File(outputDir, Constant.LABEL_FEATURE_TRIES_FOLDER);
		labelFeatureTriesFolder.mkdirs();
		sizeFile = new File(labelFeatureTriesFolder, Constant.LABEL_FEATURE_TRIES_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.labelFeatureContainers.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.labelFeatureContainers.size(); i++) {
			this.labelFeatureContainers.get(i).serialize(new File(labelFeatureTriesFolder, String.valueOf(i)), this.featureContainer);
		}

		File labelFeatureTriesAddedPerDocFolder = new File(outputDir, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER);
		labelFeatureTriesAddedPerDocFolder.mkdirs();
		sizeFile = new File(labelFeatureTriesAddedPerDocFolder, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.labelFeatureContainersAddedPerDoc.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.labelFeatureContainersAddedPerDoc.size(); i++) {
			this.labelFeatureContainersAddedPerDoc.get(i).serialize(new File(labelFeatureTriesAddedPerDocFolder, String.valueOf(i)),
					this.featureContainer);
		}
	}
}