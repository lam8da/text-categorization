package core.preprocess.analyzation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Vector;

import core.Configurator;
import core.Constant;
import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.interfaces.SimpleContainer;

/**
 * this class provide a tool for analyzing the document data and generating
 * corresponding statistical data
 * 
 * @author lambda
 * 
 */
public class DataAnalyzer extends DataHolder {
	ContainerGenerator cg;

	/**
	 * constructor
	 * 
	 * @throws Exception
	 */
	public DataAnalyzer() throws Exception {
		super();
		this.cg = Configurator.getConfigurator().getGenerator();
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
		int[] labelIds = new int[labels.length];

		// we ignore the specialness of the title and treat it as normal document content at present
		String[] ptr = new String[titleFeatures.length + contentFeatures.length];
		int copyIdx;
		for (copyIdx = 0; copyIdx < titleFeatures.length; ptr[copyIdx] = titleFeatures[copyIdx], copyIdx++);
		for (; copyIdx < titleFeatures.length + contentFeatures.length; ptr[copyIdx] = contentFeatures[copyIdx - titleFeatures.length], copyIdx++);

		int labelNameContainerSize = this.labelNameContainer.size();
		for (int i = 0; i < labels.length; i++) {
			int labelId = this.labelNameContainer.add(labels[i]); // label id starts from 0
			labelIds[i] = labelId;

			if (labelNameContainerSize == this.labelNameContainer.size() - 1) { // a new label
				labelNameContainerSize++;
				this.labelFeatureContainersAddedPerDoc.add(this.cg.generateSimpleContainer(featureContainer));
				this.labelFeatureContainers.add(this.cg.generateSimpleContainer(featureContainer));
				this.docIdsPerLabel.add(new Vector<Integer>());

				if (labelId != labelNameContainerSize - 1) {
					throw new Exception("fatal error occurs in program logic 1!");
				}
				if (this.labelFeatureContainersAddedPerDoc.size() != this.labelNameContainer.size()) {
					throw new Exception("fatal error occurs in program logic 2!");
				}
				if (this.labelFeatureContainers.size() != this.labelNameContainer.size()) {
					throw new Exception("fatal error occurs in program logic 3!");
				}
			}
			this.docIdsPerLabel.get(labelId).add(this.docCnt - 1);
		}

		SimpleContainer docContainer = this.cg.generateSimpleContainer(featureContainer);
		this.documentContainers.add(docContainer);

		HashSet<String> wordMap = new HashSet<String>(512);

		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i].length() > 0) {
				String fea = ptr[i];
				// System.out.println(fea);

				this.featureContainer.add(fea);
				docContainer.add(fea);
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

	public static DataAnalyzer deserialize(File inputDir, int[] eliminatedId, boolean rearrangeId) throws Exception {
		DataAnalyzer res = new DataAnalyzer();
		DataHolder.deserialize(res, inputDir, eliminatedId);
		if (rearrangeId) res.rearrangeId(); // do not forget to do this!
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
		int labelCnt = getM();
		bw.write(String.valueOf(labelCnt));
		bw.newLine();
		for (int i = 0; i < labelCnt; i++) {
			Vector<Integer> l = this.docIdsPerLabel.get(i);
			bw.write(String.valueOf(l.size()));
			bw.newLine();
			for (int j = 0; j < l.size(); j++) {
				bw.write(String.valueOf(l.get(j)));
				bw.newLine();
			}
		}
		bw.flush();
		bw.close();
		fw.close();

		this.featureContainer.serialize(new File(outputDir, Constant.FEATURE_CONTAINER_FILE));
		this.featureAddedPerDoc.serialize(new File(outputDir, Constant.FEATURE_CONTAINER_ADDED_PER_DOC_FILE));

		File documentContainersFolder = new File(outputDir, Constant.DOCUMENT_CONTAINERS_FOLDER);
		documentContainersFolder.mkdirs();
		sizeFile = new File(documentContainersFolder, Constant.DOCUMENT_CONTAINERS_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.documentContainers.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.documentContainers.size(); i++) {
			this.documentContainers.get(i).serialize(new File(documentContainersFolder, String.valueOf(i)));
		}

		this.labelNameContainer.serialize(new File(outputDir, Constant.LABEL_NAME_CONTAINER_FILE));

		File labelFeatureContainersFolder = new File(outputDir, Constant.LABEL_FEATURE_CONTAINERS_FOLDER);
		labelFeatureContainersFolder.mkdirs();
		sizeFile = new File(labelFeatureContainersFolder, Constant.LABEL_FEATURE_CONTAINERS_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.labelFeatureContainers.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.labelFeatureContainers.size(); i++) {
			this.labelFeatureContainers.get(i).serialize(new File(labelFeatureContainersFolder, String.valueOf(i)));
		}

		File labelFeatureContainersAddedPerDocFolder = new File(outputDir, Constant.LABEL_FEATURE_CONTAINERS_ADDED_PER_DOC_FOLDER);
		labelFeatureContainersAddedPerDocFolder.mkdirs();
		sizeFile = new File(labelFeatureContainersAddedPerDocFolder, Constant.LABEL_FEATURE_CONTAINERS_ADDED_PER_DOC_FOLDER_SIZE_FILE);
		fw = new FileWriter(sizeFile);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.labelFeatureContainersAddedPerDoc.size()));
		bw.flush();
		bw.close();
		fw.close();
		for (int i = 0; i < this.labelFeatureContainersAddedPerDoc.size(); i++) {
			this.labelFeatureContainersAddedPerDoc.get(i).serialize(new File(labelFeatureContainersAddedPerDocFolder, String.valueOf(i)));
		}
	}
}