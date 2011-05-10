package test.preprocess.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.XmlDocument;

public class DataAnalyzerTest extends DataHolderTest{
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
		((DataAnalyzer) this.holder).accomplishAdding();

		this.docCnt = xmlFiles.length;
		this.featureCnt = 5;
		this.labelCnt = 3;
		this.features = new String[] { "B", "E", "C", "A", "D" }; //their IDs are 0,1,2,3,4
		this.labels = new String[] { "c2", "c3", "c1" }; //their IDs are 0,1,2
		this.separatedLine = "---------------------------------------------";
	}

	public static void main(String[] args) throws Exception {
		DataAnalyzerTest test = new DataAnalyzerTest("res/test/DataAnalyzerTest");

		System.out.println("\r\n******************************** basic information ********************************");
		test.testBasicInformation();

		System.out.println("\r\n******************************** document counting ********************************");
		test.testDocumentCounting();

		System.out.println("\r\n********************************** word counting **********************************");
		test.testWordCounting();

		System.out.println("\r\n************************** single feature word counting ***************************");
		test.testSingleFeatureWordCounting();

		System.out.println("\r\n********************************* feature counting ********************************");
		test.testFeatureCounting();

		System.out.println("\r\n********************************** label counting *********************************");
		test.testLabelCounting();
	}
}
