package test.classifier.util;

import java.io.File;

import core.Configurator;
import core.Constant;
import core.preprocess.analyzation.DataAnalyzer;
import core.classifier.util.FinalDataHolder;

import test.preprocess.analyzation.DataAnalyzerTest;
import test.preprocess.analyzation.DataHolderTest;

public class FinalDataHolderTest extends DataHolderTest {
	public FinalDataHolderTest(File inputPath) throws Exception {
		super();
		this.holder = FinalDataHolder.deserialize(inputPath);
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

		Configurator config = Configurator.getConfigurator();
		config.setValues( //
				"", //
				args[0], //
				1, //
				1, //
				1, //
				1, //
				true, //
				true, //
				true, //
				1, //
				1, //
				Constant.MAP_GENERATOR //
		);

		File stdout = new File("res/test/DataAnalyzerTest/standard output.txt");
		StringBuffer stdSb = new StringBuffer();
		readIntoSb(stdout, stdSb);

		StringBuffer holderSb = new StringBuffer();
		DataAnalyzerTest daTest = new DataAnalyzerTest("res/test/DataAnalyzerTest");
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
		FinalDataHolderTest fdhTest = new FinalDataHolderTest(outputDir);
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
