package test.preprocess.util;

import java.io.File;

import core.preprocess.util.XmlDocument;

/**
 * this class provide a test for the class XmlDocument
 * 
 * @author lambda
 * 
 */
public class XmlDocumentTest {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Invalid arguments.");
			return;
		}

		File dir = new File(args[0]);
		XmlDocument.createDocument(dir, "test.xml", new String[] { "sport", "othertopic" }, "something about basketball",
				"Basketball is famous in China and America, but do you know where it was born?");

		XmlDocument t = new XmlDocument();
		t.parseDocument(new File(dir, "test.xml"));

		String[] labels = t.getLabels();
		for (int i = 0; i < labels.length; i++) {
			System.out.println("label " + (i + 1) + ": " + labels[i]);
		}
		System.out.println("title: " + t.getTitle());
		System.out.println("content: " + t.getContent());
	}
}
