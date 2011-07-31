package test.preprocess.corpus.reuters;

import java.io.File;

import core.preprocess.corpus.Extractor;
import core.preprocess.corpus.reuters.ReutersExtractor;
import core.util.Constant;

public class ExtractReutersTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("invalid parameters!");
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
			System.out.println("fatal error: reuter directory does not exist!");
		}
	}
}