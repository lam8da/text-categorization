package core.preprocess.util;

import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.util.NoSuchElementException;

public class Trie {
	private class TrieNode {
		private int id; //the globally unique identifier for words, start from 0
		private char val;
		private int occurrence;
		private TrieNode child;
		private TrieNode brother;

		//		public TrieNode() {
		//			this.id = -1;
		//			this.val = 0;
		//			this.occurrence = 0;
		//			this.Child = null;
		//			this.Brother = null;
		//		}

		/**
		 * 
		 */
		public TrieNode(char val, int occurrence, TrieNode Child, TrieNode Brother) {
			this.id = -1;
			this.val = val;
			this.occurrence = occurrence;
			this.child = Child;
			this.brother = Brother;
		}
	}

	public class TrieIterator implements Iterator<String> {
		private Vector<TrieNode> currentPath;
		private StringBuffer sb;
		private int len; //the length of the string from root to the current node(i.e. currentPath[len])
		private boolean ended;
		private boolean hasNextCalled;

		public TrieIterator(TrieNode nc) {
			this.currentPath = new Vector<TrieNode>(32);
			this.sb = new StringBuffer(32);
			this.len = 0;
			this.currentPath.add(nc);
			this.ended = false;
			this.hasNextCalled = false;
		}

		private boolean dfsNode(TrieNode cur) {
			while (cur.child != null) {
				cur = cur.child;
				this.currentPath.add(cur);
				this.sb.append(cur.val);
				this.len++;
				if (cur.id != -1) return true;
			}
			do {
				if (cur.brother != null) {
					cur = cur.brother;
					this.sb.setCharAt(this.len - 1, cur.val);
					this.currentPath.set(this.len, cur);
					if (cur.id != -1) return true;
					return dfsNode(cur);
				}
				this.len--;
				if (this.len == 0) break;

				this.currentPath.setSize(this.len + 1);
				cur = this.currentPath.get(this.len);
				this.sb.setLength(this.len);
				//				System.out.println("-->" + this.sb.toString());
			}
			while (true);
			this.ended = true;
			return false;
		}

		@Override
		public boolean hasNext() {
			this.hasNextCalled = true;
			if (this.ended) return false;

			TrieNode cur = this.currentPath.get(this.len);
			return dfsNode(cur);
		}

		@Override
		public String next() {
			if (!this.hasNextCalled) hasNext();
			if (this.ended) {
				throw new NoSuchElementException("the iterator has reached the end, no more element!");
			}
			else {
				this.hasNextCalled = false;
				return this.sb.toString();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("removing node on trie is not supported!");
		}

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

	/**
	 * create an iterator for the current trie
	 * 
	 * @return the iterator created
	 */
	public Iterator<String> iterator() {
		return new TrieIterator(this.root);
	}

	/**
	 * add word to the trie
	 * 
	 * @param word
	 *            the word to be added
	 * @return return the ID of the inserted word
	 */
	public int add(String word) {
		TrieNode tmp = root;
		for (int i = 0; i != word.length(); i++) {
			if (tmp.child == null) {
				tmp.child = new TrieNode(word.charAt(i), 0, null, null);
				tmp = tmp.child;
			}
			else {
				if (tmp.child.val > word.charAt(i)) { // Add To The Head Of The List
					TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.child);
					tmp.child = t;
					tmp = t;
				}
				else if (word.charAt(i) == tmp.child.val) {
					tmp = tmp.child;
				}
				else {
					tmp = tmp.child;
					boolean flag = true;
					while (tmp.brother != null) {
						if (word.charAt(i) == tmp.brother.val) {
							tmp = tmp.brother;
							flag = false;
							break;
						}
						else if (word.charAt(i) < tmp.brother.val) {
							TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.brother);
							tmp.brother = t;
							tmp = t;
							flag = false;
							break;
						}
						tmp = tmp.brother;
					}
					if (flag) {
						TrieNode t = new TrieNode(word.charAt(i), 0, null, null);
						tmp.brother = t;
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

	/**
	 * find whether the current trie contains the given word
	 * 
	 * @param word
	 *            the querying word
	 * @return true if the trie contains the word, false otherwise
	 */
	public boolean contains(String word) {
		return findId(word) != -1;
	}

	/**
	 * find the corresponding node for the given word
	 * 
	 * @param word
	 *            the given word whose node we want to find
	 * @return the corresponding node
	 */
	private TrieNode findNode(String word) {
		TrieNode tmp = root;
		for (int i = 0; i < word.length(); i++) {
			TrieNode nc = null;
			for (nc = tmp.child; nc != null; nc = nc.brother) {
				if (nc.val == word.charAt(i)) break;
				if (nc.val > word.charAt(i)) return null;
			}
			if (nc == null) return null;
			tmp = nc;
		}
		return tmp;
	}

	/**
	 * find the identifier of word
	 * 
	 * @param word
	 *            the id of which someone want to get
	 * @return if the word is not in trie, returns -1, otherwise return the id
	 *         of the word
	 */
	public int findId(String word) {
		TrieNode tmp = findNode(word);
		if (tmp == null) return -1;
		return tmp.id;
	}

	/**
	 * find the occurrence of the given word
	 * 
	 * @param word
	 *            the given word
	 * @return the occurrence of word
	 */
	public int findOccurrence(String word) {
		TrieNode tmp = findNode(word);
		if (tmp == null) return 0;
		return tmp.occurrence;
	}

	/**
	 * 
	 * @return
	 */
	public int serialize(File outputFile) {
		return 0;
	}

	/**
	 * 
	 */
	public void traverse() {
		StringBuffer sb = new StringBuffer(32);
		dfs(root, sb);
	}

	/**
	 * 
	 * @param node
	 * @param sb
	 */
	private void dfs(TrieNode node, StringBuffer sb) {
		for (TrieNode nc = node.child; nc != null; nc = nc.brother) {
			sb.append(nc.val);
			dfs(nc, sb);
			if (nc.occurrence != 0) System.out.println(nc.occurrence + ": " + sb);
			sb.setLength(sb.length() - 1);
		}
	}

	/**
	 * if a word occurs both in this and "other", subtract its occurrence in the
	 * current trie by the occurrence in "other". Otherwise nothing will be
	 * done.
	 * 
	 * @param other
	 *            the trie whose words we want to subtract from the current trie
	 * @return return a new trie as a result, no changes will be made to the
	 *         current trie
	 */
	public Trie pseudoSubtract(Trie other) {
		Trie res = new Trie();
		return res;
	}
}
