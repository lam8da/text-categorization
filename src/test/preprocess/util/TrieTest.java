package test.preprocess.util;

import core.preprocess.util.Trie;

public class TrieTest {
	public static void main(String[] args) {
		String[] words = { "the", "the", "bahia", "cocoa", "zone", "alleviating", "the", "drought", "since", "early", "January", "and", "imporving", "prospects" };
		Trie dic = new Trie();
		for (int i = 0; i != words.length; i++) {
			dic.add(words[i]);
		}
		dic.traverse();

		for (int i = 0; i < words.length; i++) {
			System.out.println(dic.findId(words[i]));
		}
	}
}
