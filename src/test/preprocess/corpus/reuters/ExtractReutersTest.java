package test.preprocess.corpus.reuters;

import java.io.File;

import core.preprocess.corpus.Extractor;
import core.preprocess.corpus.reuters.ReutersExtractor;
import core.preprocess.util.Constant;

public class ExtractReutersTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			ReutersExtractor.printUsage();
			return;
		}

		File reutersDir = new File(args[0]);

		if (reutersDir.exists()) {
			File outputDir = new File(args[1]);
			outputDir.mkdirs();
			Extractor extractor = new ReutersExtractor(reutersDir, outputDir, Constant.MOD_LEWIS);
			extractor.extract(null, new core.preprocess.extraction.KrovetzStemmer(), false, true, true);
		}
		else {
			ReutersExtractor.printUsage();
		}
	}
}