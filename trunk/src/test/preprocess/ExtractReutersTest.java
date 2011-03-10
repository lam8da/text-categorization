package test.preprocess;

import java.io.File;

import core.preprocess.ExtractReuters;

public class ExtractReutersTest {

	public static void main(String[] args) {
		if (args.length != 2) {
			ExtractReuters.printUsage();
		}

		File reutersDir = new File(args[0]);

		if (reutersDir.exists()) {
			File outputDir = new File(args[1]);
			outputDir.mkdirs();
			ExtractReuters extractor = new ExtractReuters(reutersDir, outputDir);
			extractor.extract();
		} else {
			ExtractReuters.printUsage();
		}
	}

}
