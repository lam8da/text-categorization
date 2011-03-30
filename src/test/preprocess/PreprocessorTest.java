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

		Preprocessor p = new Preprocessor(args[0], args[1], Constant.REUTERS, Constant.MOD_LEWIS, Constant.USE_STOPPER, Constant.KROVETZ_STEMMER,
				true);
		p.preprocess();
	}
}
