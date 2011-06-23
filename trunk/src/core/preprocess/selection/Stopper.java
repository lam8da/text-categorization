package core.preprocess.selection;

import java.io.File;
import java.util.Scanner;

import core.preprocess.analyzation.trie.Trie;
import core.preprocess.util.Constant;

public class Stopper {
	private Trie trie = new Trie();

	public Stopper() throws Exception {
		Scanner in = new Scanner(new File("res/stopwords-lemur"));
		while (in.hasNext()) {
			trie.add(in.next());
		}

		in = new Scanner(new File("res/stopwords-tcbook"));
		while (in.hasNext()) {
			trie.add(in.next());
		}

		in = new Scanner(new File("res/stopwords-web"));
		while (in.hasNext()) {
			trie.add(in.next());
		}
	}

	public boolean stop(String str) {
		return trie.contains(str);
	}

	public String stopTextBlock(String block) {
		String[] words = block.split("\\s+");
		StringBuffer sb = new StringBuffer(block.length());

		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 0 && !stop(words[i])) {
				sb.append(words[i]).append(Constant.WORD_SEPARATOR);
			}
		}
		return sb.toString();
	}
}
