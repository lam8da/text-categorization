package core.preprocess.util;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.TreeMap;

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
	private int docCnt;
	private boolean finished;
	private Trie featureTrie;
	private Trie classTrie; //classTrie.we represent labels as integers, but features as strings
	private Vector<Trie> documentTries;
	private Vector<Integer> docFeatureCnt;

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
		this.classTrie = new Trie();
		this.documentTries = new Vector<Trie>(8192);
		this.docFeatureCnt = new Vector<Integer>(8192);
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

		for (int i = 0; i < labels.length; i++) {
			this.classTrie.add(labels[i]);
		}

		Trie docTrie = new Trie();
		this.documentTries.add(docTrie);

		boolean swap = false;
		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i].length() > 0) {
				String fea = ptr[i];
				System.out.println(fea);
				this.featureTrie.add(fea);
				docTrie.add(fea);
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

	public int getN() {
		return this.docCnt;
	}

	public int getNci(int labelId) {
		//return this.classTrie.findOccurrence(label);	//should create a new vector which uses id instead of label name
		return -999999999;
	}

	public void writeToFile(File outputDir) throws Exception {

	}
}
