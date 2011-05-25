package core.preprocess.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Vector;

public class Trie extends SimpleTrie {
	private TreeMap<Integer, TrieNode> nodeMap;

	/**
	 * constructor
	 */
	public Trie() {
		super();
		this.nodeMap = new TreeMap<Integer, TrieNode>();
	}

	@Override
	protected TrieNode add(String word, int times, int newId) throws Exception {
		TrieNode res = super.add(word, times, newId);
		if (res.occurrence == times) this.nodeMap.put(res.id, res);
		return res;
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
		//exception may occur here when cur==null due to the missing string for given id!!!!!!
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

	/**
	 * deserialize the input file to obtain a new trie
	 * 
	 * @param isSimple
	 *            whether the result would be a SimpleTrie
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
	public static SimpleTrie deserialize(boolean isSimple, File inFile, Trie mapIdToString, int[] eliminatedId) throws Exception {
		SimpleTrie t;
		if (isSimple) t = new SimpleTrie();
		else t = new Trie();
		
		FileReader fr = new FileReader(inFile);
		BufferedReader br = new BufferedReader(fr);

		if (eliminatedId != null) {
			Arrays.sort(eliminatedId);
		}

		int n = Integer.parseInt(br.readLine());
		for (int i = 0; i < n; i++) {
			String str = br.readLine(); //'string' field
			int id = DO_NOT_RESET; //'id' field

			if (mapIdToString != null) {
				int givenId = Integer.parseInt(str); //'string' field
				if (eliminatedId != null && Arrays.binarySearch(eliminatedId, givenId) >= 0) {
					br.readLine();//read occurrence
					continue;
				}
				str = mapIdToString.getWord(givenId); //exception may occur if the trie has no string associate with strId
			}
			else {
				id = Integer.parseInt(br.readLine());
				if (eliminatedId != null && Arrays.binarySearch(eliminatedId, id) >= 0) {
					br.readLine();//read occurrence
					continue;
				}
			}

			int occurrence = Integer.parseInt(br.readLine());
			t.add(str, occurrence, id);
		}

		br.close();
		fr.close();
		return t;
	}

	public void rearrangeId() {
		this.nodeMap.clear();
		TrieIterator it = new TrieIterator(this.root);
		for (int i = 0; it.hasNext(); i++) {
			it.hasNextCalled = false;
			TrieNode nc = it.currentPath.get(it.len);
			nc.id = i;
			this.nodeMap.put(i, nc);
		}
		//wordCnt and differentWordCnt will not changed
	}
}
