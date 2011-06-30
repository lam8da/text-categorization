package test.preprocess;

import java.io.File;

import core.preprocess.Preprocessor;
import core.util.Constant;

public class PreprocessorTest {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Invalid arguments.");
			return;
		}

		Preprocessor p = new Preprocessor(new File(args[0]));
		p.preprocess(Constant.STAGE_EXTRACTION, Constant.STAGE_FEATURE_SELECTION);
		p.preprocess(Constant.STAGE_SERIALIZATION, Constant.STAGE_SERIALIZATION);
	}
}
