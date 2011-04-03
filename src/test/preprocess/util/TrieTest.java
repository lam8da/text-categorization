package test.preprocess.util;

import java.util.Iterator;

import core.preprocess.util.Trie;

public class TrieTest {
	public static void main(String[] args) {
		String[] words = { "the", "they", "bahia", "cocoa", "zone", "alleviating", "the", "drought", "since", "early", "January", "and", "imporving",
				"prospects" };
		Trie trie = new Trie();
		for (int i = 0; i != words.length; i++) {
			trie.add(words[i]);
		}

		System.out.println(trie.size());
		trie.traverse();

		for (int i = 0; i < words.length; i++) {
			System.out.print(trie.getId(words[i]) + ",");
		}
		System.out.println();
		System.out.println();

		for (Iterator<String> it = trie.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
		System.out.println();
		
		System.out.println(trie.getWord(5));
	}
}
