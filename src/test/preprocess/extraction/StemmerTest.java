package test.preprocess.extraction;

import core.preprocess.extraction.KrovetzStemmer;
import core.preprocess.extraction.PorterStemmer;
import core.preprocess.extraction.Stemmer;

public class StemmerTest {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String text = "Document will describe marketing strategies carried out by U.S. companies for their agricultural chemicals report predictions for market share of such chemicals or report market statistics for agrochemicals";
		Stemmer kStemmer = new KrovetzStemmer();
		Stemmer pStemmer = new PorterStemmer();

		System.out.println(kStemmer.stemTextBlock(text, true));
		System.out.println(pStemmer.stemTextBlock(text, true));
	}
}
