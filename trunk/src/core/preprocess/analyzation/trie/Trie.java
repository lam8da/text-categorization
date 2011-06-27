package core.preprocess.analyzation.trie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import core.preprocess.analyzation.interfaces.Container;
import core.preprocess.analyzation.interfaces.FeatureContainer;

public class Trie extends AbstractTrie implements FeatureContainer {
	protected class TrieNode extends AbstractTrieNode {
		protected int id; //the globally unique identifier for words, start from 0
		protected TrieNode parent;

		/**
		 * constructor
		 */
		public TrieNode(char val, AbstractTrieNode child, AbstractTrieNode brother, AbstractTrieNode parent) {
			super(val, child, brother);
			this.id = -1;
			this.parent = (TrieNode) parent;
		}
	}

	protected static final int DO_NOT_RESET = -618618618;
	private TreeMap<Integer, TrieNode> nodeMap;

	/**
	 * constructor
	 */
	public Trie() {
		super();
		this.nodeMap = new TreeMap<Integer, TrieNode>();
	}

	@Override
	protected AbstractTrieNode createTrieNode(char val, AbstractTrieNode child, AbstractTrieNode brother, AbstractTrieNode parent) {
		return new TrieNode(val, child, brother, parent);
	}

	@Override
	public int add(String word) throws Exception {
		if (word == null) throw new Exception("word should not be null!");
		TrieNode res = (TrieNode) add(word, 1, DO_NOT_RESET);
		return res.id;
	}

	protected AbstractTrieNode add(String word, int times, int newId) throws Exception {
		TrieNode res = (TrieNode) super.add(word, times);
		if (res.occurrence == times) {
			if (newId == DO_NOT_RESET) {
				res.id = differentWordCnt - 1;
			}
			else res.id = newId;
			this.nodeMap.put(res.id, res);
		}
		return res;
	}

	/**
	 * find the corresponding word according to the given id
	 * 
	 * @param id
	 *            the id of the word we want to find
	 * @return return the word if it exist, or null
	 */
	@Override
	public String getWord(int id) {
		StringBuffer sb = new StringBuffer(32);
		TrieNode cur = (TrieNode) nodeMap.get(id);
		//should exception occur here when cur==null due to the missing string for given id ??
		if (cur == null) return null;
		
		while (cur.parent != null) {
			sb.append(cur.val);
			cur = cur.parent;
		}
		return sb.reverse().toString();
	}

	/**
	 * find the identifier of word
	 * 
	 * @param word
	 *            the id of which someone want to get
	 * @return if the word is not in trie, returns -1, otherwise return the id
	 *         of the word
	 */
	@Override
	public int getId(String word) {
		TrieNode tmp = (TrieNode) getNode(word);
		if (tmp == null) return -1;
		return tmp.id;
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
		dfs((TrieNode) root, sb);
	}

	/**
	 * 
	 * @param node
	 * @param sb
	 */
	private void dfs(TrieNode node, StringBuffer sb) {
		for (TrieNode nc = (TrieNode) node.child; nc != null; nc = (TrieNode) nc.brother) {
			sb.append(nc.val);
			//if (nc.occurrence != 0) System.out.println("id: " + nc.id + ", cnt: " + nc.occurrence + " - " + sb);
			System.out.println("id: " + (nc.id == -1 ? "" : " ") + nc.id + ", cnt: " + nc.occurrence + " - " + sb);
			dfs(nc, sb);
			sb.setLength(sb.length() - 1);
		}
	}

	/**
	 * delete a word from current trie
	 * 
	 * @param word
	 *            the word to be deleted
	 * @param rearrangeId
	 *            whether change the id of the last node to the one of the
	 *            deleted end node
	 * @return true if the word is in the trie (i.e. deleted successfully),
	 *         false otherwise
	 */
	public boolean delete(String word, boolean rearrangeId) {
		Vector<AbstractTrieNode> preBrother = new Vector<AbstractTrieNode>(16);
		TrieNode nc = (TrieNode) root;
		for (int i = 0; i < word.length(); i++) {
			TrieNode np = null, pre = null;
			for (np = (TrieNode) nc.child; np != null; np = (TrieNode) np.brother) {
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
		if (nc == null || nc.occurrence == 0) return false;

		if (rearrangeId) {
			TrieNode nLast = this.nodeMap.get(this.differentWordCnt - 1);
			this.nodeMap.remove(this.differentWordCnt - 1);
			if (nc != nLast) {//not the same node
				nLast.id = nc.id;
				this.nodeMap.put(nc.id, nLast);//modify the map relationship 
			}
		}
		else this.nodeMap.remove(nc.id);

		this.differentWordCnt--;
		this.wordCnt -= nc.occurrence;
		nc.id = -1;
		nc.occurrence = 0;

		for (int i = preBrother.size() - 1; i >= 0; i--) {
			if (nc.id == -1 && nc.child == null) {
				AbstractTrieNode pre = preBrother.get(i);
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

	@Override
	public void serialize(File outFile) throws Exception {
		StringBuffer sb = new StringBuffer(32);
		FileWriter fw = new FileWriter(outFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.differentWordCnt));
		bw.newLine();
		serializeDfs((TrieNode) root, sb, bw);
		bw.flush();
		bw.close();
		fw.close();
	}

	private void serializeDfs(TrieNode node, StringBuffer sb, BufferedWriter bw) throws Exception {
		for (TrieNode nc = (TrieNode) node.child; nc != null; nc = (TrieNode) nc.brother) {
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

	@Override
	public int[] rearrangeId() {
		this.nodeMap.clear();
		TrieIterator it = new TrieIterator(this.root);
		for (int i = 0; it.hasNext(); i++) {
			it.hasNextCalled = false;
			TrieNode nc = (TrieNode) it.currentPath.get(it.len);
			nc.id = i;
			this.nodeMap.put(i, nc);
		}
		//wordCnt and differentWordCnt will not changed
		return null;
	}

	/**
	 * deserialize the input file to obtain a new trie
	 * 
	 * @param inFile
	 *            the file to be deserialized
	 * @param mapIdToString
	 *            mapIdToString is null means that the file contains each string
	 *            itself and must contain an id for each string, otherwise the
	 *            file contains the id of each string without its id written and
	 *            we should provide a map
	 * @param eliminatedId
	 *            eliminatedId is null means that we should read all strings in
	 *            the file and add them to the trie, otherwise we should
	 *            eliminate those whose id (i.e., the id in 'id' field when
	 *            haveId is true, and the id in 'string' field otherwise) is in
	 *            eliminatedId
	 * @return the new trie
	 * @throws Exception
	 */
	@Override
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception {
		this.nodeMap.clear();
		this.clear();

		FileReader fr = new FileReader(inFile);
		BufferedReader br = new BufferedReader(fr);

		if (eliminatedId != null) Arrays.sort(eliminatedId);

		int n = Integer.parseInt(br.readLine());
		for (int i = 0; i < n; i++) {
			String str = br.readLine(); //'string' field
			int id = Integer.parseInt(br.readLine()); //'id' field
			if (eliminatedId != null && Arrays.binarySearch(eliminatedId, id) >= 0) {
				br.readLine();//read occurrence
				continue;
			}

			int occurrence = Integer.parseInt(br.readLine());
			add(str, occurrence, id);
		}

		br.close();
		fr.close();
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
	@Override
	public int difference(Container otherC) {
		AbstractTrie other = (AbstractTrie) otherC;
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
}
