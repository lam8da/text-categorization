package core.preprocess.selection;

import java.util.Scanner;

import res.ResourceProducer;

import core.preprocess.analyzation.trie.Trie;
import core.util.Constant;

public class Stopper {
	private Trie trie = new Trie();

	public Stopper() throws Exception {
		Scanner in = new Scanner(ResourceProducer.getResourceByName("stopwords-lemur"));
		while (in.hasNext()) {
			trie.add(in.next());
		}

		in = new Scanner(ResourceProducer.getResourceByName("stopwords-tcbook"));
		while (in.hasNext()) {
			trie.add(in.next());
		}

		in = new Scanner(ResourceProducer.getResourceByName("stopwords-web"));
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
