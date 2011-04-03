package test.preprocess.util;

import java.util.Iterator;

import core.preprocess.util.Trie;

public class TrieTest {
	public static void main(String[] args) throws Exception {
		String[] words = { "the", "they", "bahia", "cocoa", "zone", "alleviating", "the", "drought", "since", "early", "January", "and", "imporving",
				"prospects" };
		Trie trie = new Trie();
		for (int i = 0; i != words.length; i++) {
			trie.add(words[i]);
		}
		System.out.println(trie.size());
		trie.traverse();
		System.out.println();

		//		for (int i = 0; i < words.length; i++) {
		//			System.out.print(trie.getId(words[i]) + ",");
		//		}
		//		System.out.println();
		//		System.out.println();

		//		for (Iterator<String> it = trie.iterator(); it.hasNext();) {
		//			it.hasNext();
		//			System.out.println(it.next());
		//		}
		//		System.out.println();
		//		System.out.println("trie.getWord(5) = " + trie.getWord(5));
		//		System.out.println();

		String[] wordsNew = { "the", "they", "cocoa", "alleviating", "drought", "since", "and", "imporving" };
		Trie trieNew = new Trie();
		for (int i = 0; i != wordsNew.length; i++) {
			trieNew.add(wordsNew[i]);
		}
		Trie res = trie.pseudoSubtract(trieNew);
		res.traverse();
	}
}
