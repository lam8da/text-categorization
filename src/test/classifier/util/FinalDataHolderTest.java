package test.classifier.util;

import java.io.File;

import core.preprocess.util.DataAnalyzer;
import core.classifier.util.FinalDataHolder;

import test.preprocess.util.DataAnalyzerTest;
import test.preprocess.util.DataHolderTest;

public class FinalDataHolderTest extends DataHolderTest {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}
		DataAnalyzerTest daTest = new DataAnalyzerTest("res/test/DataAnalyzerTest");
		//FinalDataHolderTest.test(daTest);

		File outputDir = new File(args[0]);
		outputDir.mkdirs();
		((DataAnalyzer) (daTest.holder)).serialize(outputDir);

		FinalDataHolderTest fdhTest = new FinalDataHolderTest(outputDir);
		FinalDataHolderTest.test(fdhTest);
	}

	public FinalDataHolderTest(File inputPath) throws Exception {
		this.holder = FinalDataHolder.deserialize(inputPath);
		
		this.docCnt = 8;
		this.featureCnt = 5;
		this.labelCnt = 3;
		this.features = new String[] { "B", "E", "C", "A", "D" }; //their IDs are 0,1,2,3,4
		this.labels = new String[] { "c2", "c3", "c1" }; //their IDs are 0,1,2
		this.separatedLine = "---------------------------------------------";
	}

	private static void test(DataHolderTest dhTest) throws Exception {
		System.out.println("\r\n******************************** basic information ********************************");
		dhTest.testBasicInformation();

		System.out.println("\r\n******************************** document counting ********************************");
		dhTest.testDocumentCounting();

		System.out.println("\r\n********************************** word counting **********************************");
		dhTest.testWordCounting();

		System.out.println("\r\n************************** single feature word counting ***************************");
		dhTest.testSingleFeatureWordCounting();

		System.out.println("\r\n********************************* feature counting ********************************");
		dhTest.testFeatureCounting();

		System.out.println("\r\n********************************** label counting *********************************");
		dhTest.testLabelCounting();
	}
}
