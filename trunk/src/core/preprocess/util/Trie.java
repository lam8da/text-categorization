package core.preprocess.util;

public class Trie {
	private class TrieNode {
		private int id; //the globally unique identifier for words, start from 0
		private char val;
		private int occurrence;
		private TrieNode Child;
		private TrieNode Brother;

		//		/*
		//		 * Constructor without parameters
		//		 */
		//		public TrieNode() {
		//			this.id = -1;
		//			this.val = 0;
		//			this.occurrence = 0;
		//			this.Child = null;
		//			this.Brother = null;
		//		}

		/*
		 * Constructor with occurrence
		 */
		public TrieNode(char val, int occurrence, TrieNode Child, TrieNode Brother) {
			this.id = -1;
			this.val = val;
			this.occurrence = occurrence;
			this.Child = Child;
			this.Brother = Brother;
		}

		//		/*
		//		 * 
		//		 */
		//		private void setAttribute(char val) {
		//			this.val = val;
		//		}
		//
		//		/*
		//		 * 
		//		 */
		//		private void setOccurrence(int occurrence) {
		//			this.occurrence = occurrence;
		//		}
		//
		//		/*
		//		 *
		//		 */
		//		private void setFirstChild(TrieNode Child) {
		//			this.Child = Child;
		//		}
		//
		//		/*
		//		 *
		//		 */
		//		private void setFirstBrother(TrieNode Brother) {
		//			this.Brother = Brother;
		//		}
		//
		//		/*
		//		 *
		//		 */
		//		private char getAttribute() {
		//			return this.val;
		//		}
		//
		//		/*
		//		 * Get Occurrence of the current node
		//		 */
		//		private int getOccurrence() {
		//			return this.occurrence;
		//		}
		//
		//		/*
		//		 * Get The Firt Child
		//		 */
		//		private TrieNode getFirstChild() {
		//			return this.Child;
		//		}
		//
		//		/*
		//		 * Get the first brother
		//		 */
		//		private TrieNode getFirstBrother() {
		//			return this.Brother;
		//		}
	}

	private TrieNode root;
	private int strCnt; //number of unduplicated strings which have been added to the trie

	public Trie() {
		this.root = new TrieNode('\\', 0, null, null);
		this.strCnt = 0;
	}

	public int size() {
		return strCnt;
	}

	/*
	 * add word to the trie, return the ID of the inserted word
	 */
	public int add(String word) {
		TrieNode tmp = root;
		for (int i = 0; i != word.length(); i++) {
			if (tmp.Child == null) {
				tmp.Child = new TrieNode(word.charAt(i), 0, null, null);
				tmp = tmp.Child;
			}
			else {
				if (tmp.Child.val > word.charAt(i)) { // Add To The Head Of The List
					TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.Child);
					tmp.Child = t;
					tmp = t;
				}
				else if (word.charAt(i) == tmp.Child.val) {
					tmp = tmp.Child;
				}
				else {
					tmp = tmp.Child;
					boolean flag = true;
					while (tmp.Brother != null) {
						if (word.charAt(i) == tmp.Brother.val) {
							tmp = tmp.Brother;
							flag = false;
							break;
						}
						else if (word.charAt(i) < tmp.Brother.val) {
							TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.Brother);
							tmp.Brother = t;
							tmp = t;
							flag = false;
							break;
						}
						tmp = tmp.Brother;
					}
					if (flag) {
						TrieNode t = new TrieNode(word.charAt(i), 0, null, null);
						tmp.Brother = t;
						tmp = t;
					}
				}
			}
		}
		if (tmp.occurrence == 0) {
			tmp.id = strCnt;
			strCnt++;
		}
		tmp.occurrence++;
		return tmp.occurrence;
	}

	public boolean contains(String word) {
		return findId(word) != -1;
	}

	/*
	 * find the identifier of word, if the word is not in trie, returns -1.
	 */
	public int findId(String word) {
		TrieNode tmp = root;
		for (int i = 0; i < word.length(); i++) {
			TrieNode nc = null;
			for (nc = tmp.Child; nc != null; nc = nc.Brother) {
				if (nc.val == word.charAt(i)) break;
				if (nc.val > word.charAt(i)) return -1;
			}
			if (nc == null) return -1;
			tmp = nc;
		}
		return tmp.id;
	}

	/*
	 * Write DicTree
	 */
	public int writeDicTree() {
		return 0;
	}

	public void traverse() {
		StringBuffer sb = new StringBuffer(32);
		dfs(root, sb);
	}

	private void dfs(TrieNode node, StringBuffer sb) {
		for (TrieNode nc = node.Child; nc != null; nc = nc.Brother) {
			sb.append(nc.val);
			dfs(nc, sb);
			if (nc.occurrence != 0) System.out.println(nc.occurrence + ": " + sb);
			sb.setLength(sb.length() - 1);
		}
	}
}
