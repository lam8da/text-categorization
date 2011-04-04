package test.preprocess.util;

import core.preprocess.util.DataAnalyzer;

public class DataAnalyzerTest {
	public static void main(String[] args) throws Exception {
		DataAnalyzer analyzer = new DataAnalyzer();
		analyzer.addDocument(new String[] { "sport", "news" }, new String[] { "basketball", "other" }, new String[] { "hello", "basketball", "ball" });
		analyzer.addDocument(new String[] { "news" }, new String[] { "test", "god" }, new String[] { "other", "basketball", "test" });
		analyzer.finish();

		System.out.println(analyzer.getN());
		System.out.println(analyzer.getW());
		System.out.println(analyzer.getV());
		System.out.println(analyzer.getM());
		
		//need more tests
	}
}
