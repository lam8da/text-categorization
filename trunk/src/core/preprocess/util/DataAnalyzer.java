package core.preprocess.util;

import java.io.File;

/**
 * this class provide a tool for analyzing the document data and generating corresponding statistical data
 * @author lambda
 *
 */
public class DataAnalyzer {
	int docCnt;

	public DataAnalyzer() {
		docCnt = 0;
	}

	public void addDocument(String[] labels, String title, String content) {
		// we ignore the specialness of the title and treat it as normal document content at present
	}

	public void writeToFile(File outputDir) throws Exception {

	}
}
