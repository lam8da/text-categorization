package core.classifier.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import core.preprocess.util.Constant;
import core.preprocess.util.DataHolder;
import core.preprocess.util.Trie;

public final class FinalDataHolder extends DataHolder {
	private FinalDataHolder() {
		super();
	}

	private static void loadTrieVector(Vector<Trie> vec, File inputDir, String folderName, String metaFilename) throws Exception {
		File triesFolder = new File(inputDir, folderName);
		File sizeFile = new File(triesFolder, metaFilename);
		FileReader fr = new FileReader(sizeFile);
		BufferedReader br = new BufferedReader(fr);
		int size = Integer.parseInt(br.readLine());
		br.close();
		fr.close();

		for (int i = 0; i < size; i++) {
			vec.add(Trie.deserialize(new File(triesFolder, String.valueOf(i))));
		}
	}

	public static FinalDataHolder deserialize(File inputDir) throws Exception {
		FinalDataHolder res = new FinalDataHolder();

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

		res.featureTrie = Trie.deserialize(new File(inputDir, Constant.FEATURE_TRIE_FILE));
		res.featureTrieAddedPerDoc = Trie.deserialize(new File(inputDir, Constant.FEATURE_TRIE_ADDED_PER_DOC_FILE));
		res.labelNameTrie = Trie.deserialize(new File(inputDir, Constant.LABEL_NAME_TRIE_FILE));

		loadTrieVector(res.documentTries, inputDir, Constant.DOCUMENT_TRIES_FOLDER, Constant.DOCUMENT_TRIES_FOLDER_SIZE_FILE);
		loadTrieVector(res.labelFeatureTries, inputDir, Constant.LABEL_FEATURE_TRIES_FOLDER, Constant.LABEL_FEATURE_TRIES_FOLDER_SIZE_FILE);
		loadTrieVector(res.labelFeatureTriesAddedPerDoc, inputDir, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER, Constant.LABEL_FEATURE_TRIES_ADDED_PER_DOC_FOLDER_SIZE_FILE);

		return res;
	}
}
