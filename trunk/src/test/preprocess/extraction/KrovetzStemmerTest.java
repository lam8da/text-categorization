package test.preprocess.extraction;

import java.util.Scanner;

import core.preprocess.extraction.KrovetzStemmer;

public class KrovetzStemmerTest {

	public static void main(String[] args) throws Exception {
		String word;
		String ret;
		KrovetzStemmer stemmer = new KrovetzStemmer();
		Scanner in = new Scanner(System.in);

		do {
			word = in.next();
			if (word.equals("exit")) break;
			ret = stemmer.stem(word, true);
			System.out.println(ret + "->" + ret.length());
		}
		while (true);
	}
}