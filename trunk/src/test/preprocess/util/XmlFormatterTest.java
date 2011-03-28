package test.preprocess.util;

import java.io.File;

import core.preprocess.util.XmlFormatter;

public class XmlFormatterTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Invalid arguments.");
			return;
		}

		File inputDir = new File(args[0]);
		File outputDir = new File(args[1]);
		XmlFormatter
				.createXml(outputDir, "test.xml", new String[] { "sport",
						"othertopic" }, "something about basketball",
						"Basketball is famous in China and America, but do you know where it was born?");

		XmlFormatter t = new XmlFormatter();
		t.parseXml(inputDir, "test.xml");

		String[] labels = t.getLabels();
		for (int i = 0; i < labels.length; i++) {
			System.out.println("label " + (i + 1) + ": " + labels[i]);
		}
		System.out.println("title: " + t.getTitle());
		System.out.println("content: " + t.getContent());
	}
}
