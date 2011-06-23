package test.classifier.util;

import java.io.File;

import core.preprocess.analyzation.DataAnalyzer;
import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.generator.TrieGenerator;
import core.classifier.util.FinalDataHolder;

import test.preprocess.analyzation.DataAnalyzerTest;
import test.preprocess.analyzation.DataHolderTest;

public class FinalDataHolderTest extends DataHolderTest {
	public FinalDataHolderTest(File inputPath, ContainerGenerator g) throws Exception {
		super();
		this.holder = FinalDataHolder.deserialize(g, inputPath);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}
		ContainerGenerator generator = new TrieGenerator();

		File stdout = new File("res/test/DataAnalyzerTest/standard output.txt");
		StringBuffer stdSb = new StringBuffer();
		readIntoSb(stdout, stdSb);

		StringBuffer holderSb = new StringBuffer();
		DataAnalyzerTest daTest = new DataAnalyzerTest("res/test/DataAnalyzerTest", generator);
		holderSb.append("file cnt: " + daTest.fileCnt).append("\r\n");
		DataHolderTest.test(daTest, holderSb);

		if (stdSb.toString().contentEquals(holderSb) == false) {
			System.out.println("Error in holder stringbuffer!");
			System.out.println(stdSb);
			System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			System.out.println(holderSb);
			return;
		}
		else System.out.println("holder stringbuffer is ok!");

		//------------------------------------------------------------------------------------------//

		File outputDir = new File(args[0]);
		outputDir.mkdirs();
		((DataAnalyzer) (daTest.holder)).serialize(outputDir);

		StringBuffer finalHolderSb = new StringBuffer();
		FinalDataHolderTest fdhTest = new FinalDataHolderTest(outputDir, generator);
		finalHolderSb.append("file cnt: " + daTest.fileCnt).append("\r\n");
		DataHolderTest.test(fdhTest, finalHolderSb);

		if (stdSb.toString().contentEquals(finalHolderSb) == false) {
			System.out.println("Error in final holder stringbuffer!");
			System.out.println(stdSb);
			System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			System.out.println(holderSb);
			return;
		}
		else System.out.println("final holder stringbuffer is ok!");
	}
}
