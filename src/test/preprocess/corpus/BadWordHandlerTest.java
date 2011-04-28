package test.preprocess.corpus;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Vector;
import java.io.FileWriter;
import java.lang.Character;

import core.preprocess.corpus.BadWordHandler;

public class BadWordHandlerTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}
		BadWordHandler handler = new BadWordHandler();
		Scanner reader = new Scanner(new File("res/test/reuters-badword.txt"));
		//Scanner reader = new Scanner(new File("res/test/manualbadword.txt"));
		Vector<String> vs = new Vector<String>(32768);

		//handler.process("lambd'a,,h(ello,%,sh-ut,*", vs);
		while (reader.hasNext()) {
			handler.process(reader.next(), vs);
		}

		HashMap<String, Integer> badwset = new HashMap<String, Integer>(32768);
		for (Iterator<String> it = vs.iterator(); it.hasNext();) {
			String word = it.next();
			for (int i = 0; i < word.length(); i++) {
				if (!Character.isLetterOrDigit(word.charAt(i))) {
					int value = 0;
					if (badwset.containsKey(word)) value = badwset.get(word);
					badwset.put(word, value + 1);
					break;
				}
			}
		}

		File outFile = new File(args[0]);
		FileWriter writer = new FileWriter(outFile);
		for (Iterator<String> it = badwset.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			writer.write(badwset.get(key) + ": " + key + "\r\n");
		}
		writer.close();
	}
}