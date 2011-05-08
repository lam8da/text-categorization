package core.preprocess.util;

import java.util.Iterator;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Trie {
	protected class TrieNode {
		protected int id; //the globally unique identifier for words, start from 0
		protected char val;
		protected int occurrence;
		protected TrieNode child;
		protected TrieNode brother;
		protected TrieNode parent;

		/**
		 * constructor
		 */
		public TrieNode(char val, int occurrence, TrieNode child, TrieNode brother, TrieNode parent) {
			this.id = -1;
			this.val = val;
			this.occurrence = occurrence;
			this.child = child;
			this.brother = brother;
			this.parent = parent;
		}
	}

	private class TrieIterator implements Iterator<String> {
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
			if (this.hasNextCalled) return !ended;

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

	protected TrieNode root;
	private int differentWordCnt; //number of different strings which have been added to the trie
	private int wordCnt; // number of strings (concerning duplication) in trie
	private TreeMap<Integer, TrieNode> nodeMap;

	/**
	 * constructor
	 */
	public Trie() {
		this.root = new TrieNode('\\', 0, null, null, null);
		this.differentWordCnt = 0;
		this.wordCnt = 0;
		this.nodeMap = new TreeMap<Integer, TrieNode>();
	}

	/**
	 * get the size (i.e. number of different words) of current trie
	 * 
	 * @return the size
	 */
	public int size() {
		return this.differentWordCnt;
	}

	/**
	 * get the number of words (concerning duplication) in the trie
	 * 
	 * @return the number of words concerning duplication
	 */
	public int getCounting() {
		return this.wordCnt;
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
	 * @return return the id of the inserted word in the trie
	 * @throws Exception
	 */
	public int add(String word) throws Exception {
		return add(word, 1, false, -2);
	}

	/**
	 * 
	 * add word to the trie with number of occurrence as "times"
	 * 
	 * @param word
	 *            the word to be added
	 * @param times
	 *            number of occurrence we want to add to "word", should be
	 *            positive
	 * @param resetId
	 *            this parameter is used in function deserialize
	 * @param newId
	 *            the new id to be set
	 * @return the id of the inserted word in the trie
	 * @throws Exception
	 */
	protected int add(String word, int times, boolean resetId, int newId) throws Exception {
		if (times <= 0) {
			throw new Exception("invalid parameter: times should be greater than 0!");
		}

		TrieNode tmp = root;
		for (int i = 0; i != word.length(); i++) {
			if (tmp.child == null) {
				tmp.child = new TrieNode(word.charAt(i), 0, null, null, tmp);
				tmp = tmp.child;
			}
			else {
				if (tmp.child.val > word.charAt(i)) { // Add To The Head Of The List
					TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.child, tmp);
					tmp.child = t;
					tmp = t;
				}
				else if (word.charAt(i) == tmp.child.val) {
					tmp = tmp.child;
				}
				else {
					TrieNode p = tmp;
					tmp = tmp.child;
					boolean flag = true;
					while (tmp.brother != null) {
						if (word.charAt(i) == tmp.brother.val) {
							tmp = tmp.brother;
							flag = false;
							break;
						}
						else if (word.charAt(i) < tmp.brother.val) {
							TrieNode t = new TrieNode(word.charAt(i), 0, null, tmp.brother, p);
							tmp.brother = t;
							tmp = t;
							flag = false;
							break;
						}
						tmp = tmp.brother;
					}
					if (flag) {
						TrieNode t = new TrieNode(word.charAt(i), 0, null, null, p);
						tmp.brother = t;
						tmp = t;
					}
				}
			}
		}
		if (tmp.occurrence == 0) {
			if (resetId) {
				tmp.id = newId;
			}
			else tmp.id = differentWordCnt;
			this.nodeMap.put(tmp.id, tmp);
			differentWordCnt++;
		}
		tmp.occurrence += times;
		this.wordCnt += times;
		return tmp.id;
	}

	/**
	 * find whether the current trie contains the given word
	 * 
	 * @param word
	 *            the querying word
	 * @return true if the trie contains the word, false otherwise
	 */
	public boolean contains(String word) {
		return getId(word) != -1; //should not use getNode(word)!=null !!
	}

	/**
	 * find the corresponding node for the given word
	 * 
	 * @param word
	 *            the given word whose node we want to find
	 * @return the corresponding node
	 */
	private TrieNode getNode(String word) {
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
	public int getId(String word) {
		TrieNode tmp = getNode(word);
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
	public int getOccurrence(String word) {
		TrieNode tmp = getNode(word);
		if (tmp == null) return 0;
		return tmp.occurrence;
	}

	/**
	 * find the corresponding word according to the given id
	 * 
	 * @param id
	 *            the id of the word we want to find
	 * @return return the word if it exist, or null
	 */
	public String getWord(int id) {
		StringBuffer sb = new StringBuffer(32);
		for (TrieNode cur = nodeMap.get(id); cur.parent != null; cur = cur.parent) {
			sb.append(cur.val);
		}
		return sb.reverse().toString();
	}

	/**
	 * traverse the trie in lexicographic order. this routine is just for test
	 * at present.
	 */
	public void traverse() {
		System.out.println("differentWordCnt = " + this.differentWordCnt);
		System.out.println("wordCnt = " + this.wordCnt);
		for (int i = 0; i < this.differentWordCnt; i++) {
			System.out.println(i + ": " + this.getWord(i));
		}

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
			//if (nc.occurrence != 0) System.out.println("id: " + nc.id + ", cnt: " + nc.occurrence + " - " + sb);
			System.out.println("id: " + (nc.id == -1 ? "" : " ") + nc.id + ", cnt: " + nc.occurrence + " - " + sb);
			dfs(nc, sb);
			sb.setLength(sb.length() - 1);
		}
	}

	/**
	 * find the number of words which is contained in current trie but not in
	 * "other", or contained in both but the occurrence in current trie is
	 * greater than in "other"
	 * 
	 * @param other
	 *            the trie we want to compare to
	 * @return the difference defined as above
	 */
	public int difference(Trie other) {
		Iterator<String> it = iterator();
		if (!it.hasNext()) return 0;

		int diff = 0;
		Iterator<String> otherIt = other.iterator();
		String itNext = it.next();
		String otherItNext = "\0"; //need to be tested

		while (true) {
			boolean bk = false;

			while (otherItNext.compareTo(itNext) < 0) {
				if (otherIt.hasNext()) otherItNext = otherIt.next();
				else {
					diff++;
					while (it.hasNext()) {
						itNext = it.next();
						diff++;
					}
					bk = true;
					break;
				}
			}
			if (bk) break;

			if (otherItNext.equals(itNext)) {
				int occurrence = getOccurrence(itNext) - other.getOccurrence(itNext);
				if (occurrence > 0) diff++;
			}
			else diff++;

			if (it.hasNext()) {
				itNext = it.next();
			}
			else break;

			while (itNext.compareTo(otherItNext) < 0) {
				diff++;
				if (it.hasNext()) itNext = it.next();
				else {
					bk = true;
					break;
				}
			}
			if (bk) break;
		}
		return diff;
	}

	/**
	 * delete a word from current trie
	 * 
	 * @param word
	 *            the word to be deleted
	 * @return true if the word is in the trie (i.e. deleted successfully),
	 *         false otherwise
	 */
	public boolean delete(String word) {
		Vector<TrieNode> preBrother = new Vector<TrieNode>(16);
		TrieNode nc = root;
		for (int i = 0; i < word.length(); i++) {
			TrieNode np = null, pre = null;
			for (np = nc.child; np != null; np = np.brother) {
				if (np.val >= word.charAt(i)) break;
				pre = np;
			}
			if (np == null || np.val > word.charAt(i)) {
				nc = null;
				break;
			}
			nc = np;
			preBrother.add(pre);
		}
		if (nc == null || nc.id == -1) return false;

		TrieNode nLast = this.nodeMap.get(this.differentWordCnt - 1);
		this.nodeMap.remove(this.differentWordCnt - 1);
		if (nc != nLast) {//not the same node
			nLast.id = nc.id;
			this.nodeMap.put(nc.id, nLast);//modify the map relationship 
		}

		this.differentWordCnt--;
		this.wordCnt -= nc.occurrence;
		nc.id = -1;
		nc.occurrence = 0;

		for (int i = preBrother.size() - 1; i >= 0; i--) {
			if (nc.id == -1 && nc.child == null) {
				TrieNode pre = preBrother.get(i);
				if (pre == null) {
					if (nc.brother == null) {
						nc = nc.parent;
						nc.child = null;
					}
					else {
						nc.parent.child = nc.brother;
						break;
					}
				}
				else {//nc's parent has at least one child, break.
					pre.brother = nc.brother;
					break;
				}
			}
			else break;
		}
		return true;
	}

	public void serialize(File outFile) throws IOException {
		StringBuffer sb = new StringBuffer(32);
		FileWriter fw = new FileWriter(outFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.differentWordCnt));
		bw.newLine();
		serializeDfs(root, sb, bw);
		bw.flush();
		bw.close();
		fw.close();
	}

	private void serializeDfs(TrieNode node, StringBuffer sb, BufferedWriter bw) throws IOException {
		for (TrieNode nc = node.child; nc != null; nc = nc.brother) {
			sb.append(nc.val);
			if (nc.occurrence != 0) {
				bw.write(sb.toString());
				bw.newLine();
				bw.write(String.valueOf(nc.id));
				bw.newLine();
				bw.write(String.valueOf(nc.occurrence));
				bw.newLine();
			}
			serializeDfs(nc, sb, bw);
			sb.setLength(sb.length() - 1);
		}
	}

	public static Trie deserialize(File inFile) throws Exception {
		Trie t = new Trie();
		FileReader fr = new FileReader(inFile);
		BufferedReader br = new BufferedReader(fr);

		int n = Integer.parseInt(br.readLine());
		for (int i = 0; i < n; i++) {
			String str = br.readLine();
			int id = Integer.parseInt(br.readLine());
			int occurrence = Integer.parseInt(br.readLine());
			t.add(str, occurrence, true, id);
		}

		br.close();
		fr.close();
		return t;
	}
}
