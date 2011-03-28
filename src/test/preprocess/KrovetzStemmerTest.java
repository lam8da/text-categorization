package test.preprocess;

import java.util.Scanner;
import java.io.*;

import core.preprocess.KrovetzStemmer;

public class KrovetzStemmerTest {

	public static void main(String[] args) throws FileNotFoundException {
		String word;
		String ret;
		KrovetzStemmer stemmer = new KrovetzStemmer();
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