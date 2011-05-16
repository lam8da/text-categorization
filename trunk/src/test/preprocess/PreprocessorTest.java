package test.preprocess;

import core.preprocess.Preprocessor;
import core.preprocess.util.Constant;

public class PreprocessorTest {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Invalid arguments.");
			return;
		}

		Preprocessor p = new Preprocessor(//
				args[0], //inputPath
				args[1], //outputPath
				Constant.REUTERS, //corpusId
				Constant.MOD_LEWIS, //splitting
				Constant.USE_STOPPER, //stopperId
				Constant.KROVETZ_STEMMER, //stemmerId
				true, //toLower
				true, //timeToConst
				true, //numToConst
				Constant.WF_SELECTOR, //selectorId
				Constant.FEATURE_SELECTION_MAXSELECTION //selectMethodId
		);
		p.preprocess(Constant.STAGE_EXTRACTION, Constant.STAGE_FEATURE_SELECTION);
		p.preprocess(Constant.STAGE_SERIALIZATION, Constant.STAGE_SERIALIZATION);
	}
}
