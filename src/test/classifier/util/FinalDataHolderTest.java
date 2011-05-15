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
		//DataHolderTest.test(daTest);

		File outputDir = new File(args[0]);
		outputDir.mkdirs();
		((DataAnalyzer) (daTest.holder)).serialize(outputDir);

		FinalDataHolderTest fdhTest = new FinalDataHolderTest(outputDir);
		DataHolderTest.test(fdhTest);
	}

	public FinalDataHolderTest(File inputPath) throws Exception {
		super();
		this.holder = FinalDataHolder.deserialize(inputPath);
	}
}
