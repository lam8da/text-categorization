package test.preprocess.extraction;

import java.util.Scanner;

import core.preprocess.extraction.PorterStemmer;

public class PorterStemmerTest {

	public static void main(String[] args) {
		String word;
		String ret;
		PorterStemmer stemmer = new PorterStemmer();
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
