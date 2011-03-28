package test.preprocess.corpus.reuters;

import java.io.File;

import core.preprocess.corpus.reuters.ExtractReuters;
import core.preprocess.util.Constant;

public class ExtractReutersTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			ExtractReuters.printUsage();
			return;
		}

		File reutersDir = new File(args[0]);

		if (reutersDir.exists()) {
			File outputDir = new File(args[1]);
			outputDir.mkdirs();
			ExtractReuters extractor = new ExtractReuters(reutersDir,
					outputDir, Constant.MODLEWIS);
			extractor.extract();
		} else {
			ExtractReuters.printUsage();
		}
	}
}