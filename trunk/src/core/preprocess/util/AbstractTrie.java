package core.preprocess.util;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public abstract class AbstractTrie {
	protected abstract class AbstractTrieNode {
		protected char val;
		protected int occurrence;
		protected AbstractTrieNode child;
		protected AbstractTrieNode brother;

		public AbstractTrieNode(char val, AbstractTrieNode child, AbstractTrieNode brother) {
			this.val = val;
			this.occurrence = 0;
			this.child = child;
			this.brother = brother;
		}
	}

	protected class TrieIterator implements Iterator<String> {
		protected Vector<AbstractTrieNode> currentPath;
		protected StringBuffer sb;
		protected int len; //the length of the string from root to the current node (i.e. currentPath[len])
		protected boolean ended;
		protected boolean hasNextCalled;

		public TrieIterator(AbstractTrieNode nc) {
			this.currentPath = new Vector<AbstractTrieNode>(32);
			this.sb = new StringBuffer(32);
			this.len = 0;
			this.currentPath.add(nc);
			this.ended = false;
			this.hasNextCalled = false;
		}

		protected boolean dfsNode(AbstractTrieNode cur) {
			while (cur.child != null) {
				cur = cur.child;
				this.currentPath.add(cur);
				this.sb.append(cur.val);
				this.len++;
				if (cur.occurrence > 0) return true;
			}
			do {
				if (cur.brother != null) {
					cur = cur.brother;
					this.sb.setCharAt(this.len - 1, cur.val);
					this.currentPath.set(this.len, cur);
					if (cur.occurrence > 0) return true;
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

			AbstractTrieNode cur = this.currentPath.get(this.len);
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

	protected AbstractTrieNode root;
	protected int wordCnt; //number of strings (concerning duplication) in trie
	protected int differentWordCnt; //number of different strings which have been added to the trie

	public AbstractTrie() {
		this.root = createTrieNode('\\', null, null, null);
		this.differentWordCnt = 0;
		this.wordCnt = 0;
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
		add(word, 1);
		return -1;
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
	protected AbstractTrieNode add(String word, int times) throws Exception {
		if (times <= 0) {
			throw new Exception("invalid parameter: times should be greater than 0!");
		}

		AbstractTrieNode tmp = root;
		for (int i = 0; i != word.length(); i++) {
			if (tmp.child == null) {
				tmp.child = createTrieNode(word.charAt(i), null, null, tmp);
				tmp = tmp.child;
			}
			else {
				if (tmp.child.val > word.charAt(i)) { // Add To The Head Of The List
					AbstractTrieNode t = createTrieNode(word.charAt(i), null, tmp.child, tmp);
					tmp.child = t;
					tmp = t;
				}
				else if (word.charAt(i) == tmp.child.val) {
					tmp = tmp.child;
				}
				else {
					AbstractTrieNode p = tmp;
					tmp = tmp.child;
					boolean flag = true;
					while (tmp.brother != null) {
						if (word.charAt(i) == tmp.brother.val) {
							tmp = tmp.brother;
							flag = false;
							break;
						}
						else if (word.charAt(i) < tmp.brother.val) {
							AbstractTrieNode t = createTrieNode(word.charAt(i), null, tmp.brother, p);
							tmp.brother = t;
							tmp = t;
							flag = false;
							break;
						}
						tmp = tmp.brother;
					}
					if (flag) {
						AbstractTrieNode t = createTrieNode(word.charAt(i), null, null, p);
						tmp.brother = t;
						tmp = t;
					}
				}
			}
		}
		if (tmp.occurrence == 0) differentWordCnt++;
		tmp.occurrence += times;
		this.wordCnt += times;
		return tmp;
	}

	/**
	 * find the corresponding node for the given word
	 * 
	 * @param word
	 *            the given word whose node we want to find
	 * @return the corresponding node
	 */
	protected AbstractTrieNode getNode(String word) {
		AbstractTrieNode tmp = root;
		for (int i = 0; i < word.length(); i++) {
			AbstractTrieNode nc = null;
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
	 * find whether the current trie contains the given word
	 * 
	 * @param word
	 *            the querying word
	 * @return true if the trie contains the word, false otherwise
	 */
	public boolean contains(String word) {
		return getOccurrence(word) > 0; //should not use getNode(word)!=null !!
	}

	/**
	 * find the occurrence of the given word
	 * 
	 * @param word
	 *            the given word
	 * @return the occurrence of word
	 */
	public int getOccurrence(String word) {
		AbstractTrieNode tmp = getNode(word);
		if (tmp == null) return 0;
		return tmp.occurrence;
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
	 * get the size (i.e. number of different words) of current trie
	 * 
	 * @return the size
	 */
	public int size() {
		return this.differentWordCnt;
	}

	/**
	 * find the number of words which is contained in current trie but not in
	 * "other", or contained in both but the occurrence in current trie is
	 * greater than that in "other"
	 * 
	 * @param other
	 *            the trie we want to compare to
	 * @return the difference defined as above
	 */
	public int difference(AbstractTrie other) {
		Iterator<String> it = this.iterator();
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

	protected abstract AbstractTrieNode createTrieNode(char val, AbstractTrieNode child, AbstractTrieNode brother, AbstractTrieNode parent);

	public abstract void serialize(File outFile, Trie mapStringToId) throws Exception;

	public abstract void serialize(File outFile) throws Exception;
}
