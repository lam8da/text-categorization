package test.preprocess;

import java.util.*;

import core.preprocess.PorterStemmer;

public class PorterStemmerTest {

	public static void main(String[] args) {
		String word;
		String ret;
		PorterStemmer stemmer = new PorterStemmer();
		Scanner in = new Scanner(System.in);

		do {
			word = in.next();
			if (word.equals("exit"))
				break;
			ret = stemmer.stem(word);
			System.out.println(ret + "->" + ret.length());
		} while (true);
	}

}
