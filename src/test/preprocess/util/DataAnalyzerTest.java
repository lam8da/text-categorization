package test.preprocess.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.XmlDocument;

public class DataAnalyzerTest extends DataHolderTest {
	//the content of the being tested documents is as follows:
	//  --------------------------------------
	//  |   labels   |  docId  |   features  |
	//  --------------------------------------
	//  |     c1     |    3    | A B   D E E |
	//  |     c2     |    0    |   B     E E |
	//  |    c1,c2   |    4    |     C   E E |
	//  |     c2     |    7    |       D E E |
	//  |     c3     |    1    |     C   E E |
	//  |    c1,c3   |    2    |     C   E E |
	//  |    c2,c3   |    5    |       D E E |
	//  |  c1,c2,c3  |    6    |       D E E |
	//  --------------------------------------

	public DataAnalyzerTest(String inputPath) throws Exception {
		super();

		File inputDir = new File(inputPath);
		File[] xmlFiles = inputDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		System.out.println("file cnt: " + xmlFiles.length);

		Arrays.sort(xmlFiles);
		XmlDocument xml = new XmlDocument();
		this.holder = new DataAnalyzer();

		for (int i = 0; i < xmlFiles.length; i++) {
			//System.out.println(xmlFiles[i].getName());
			xml.parseDocument(xmlFiles[i]);
			((DataAnalyzer) holder).addDocument(xml.getLabels(), xml.getTitleFeatures(), xml.getContentFeatures());
		}

		if (this.docCnt != xmlFiles.length) {
			throw new Exception("invalid xmlFiles.length!");
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}

		DataAnalyzerTest daTest = new DataAnalyzerTest("res/test/DataAnalyzerTest");
		DataHolderTest.test(daTest);

		File dir = new File(args[0]);
		dir.mkdirs();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}

		((DataAnalyzer) daTest.holder).serialize(dir);

		DataAnalyzer ana = DataAnalyzer.deserialize(dir, new int[] { 1 }, true);
		daTest.holder = ana;
		daTest.featureCnt = 4;
		daTest.features = new String[] { "A", "B", "C", "D" }; //their IDs are 0,1,2,3
		daTest.labels = new String[] { "c2", "c3", "c1" }; //their IDs are 0,1,2
		daTest.separatedLine = "---------------------------------------------";

		//DataHolderTest.test(daTest);
	}
}
