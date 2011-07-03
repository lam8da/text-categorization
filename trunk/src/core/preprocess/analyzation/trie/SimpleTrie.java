package core.preprocess.analyzation.trie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import core.preprocess.analyzation.interfaces.SimpleContainer;

public class SimpleTrie extends AbstractTrie implements SimpleContainer {
	protected class SimpleTrieNode extends AbstractTrieNode {
		public SimpleTrieNode(char val, AbstractTrieNode child, AbstractTrieNode brother) {
			super(val, child, brother);
		}
	}

	private Trie idMapper;

	public SimpleTrie(Trie mapper) {
		this.idMapper = mapper;
	}

	@Override
	protected AbstractTrieNode createTrieNode(char val, AbstractTrieNode child, AbstractTrieNode brother, AbstractTrieNode parent) {
		return new SimpleTrieNode(val, child, brother);
	}

	@Override
	public void add(String word) throws Exception {
		if (word == null) throw new Exception("word should not be null!");
		add(word, 1);
	}

	/**
	 * serialize the current trie to a file
	 * 
	 * @param outFile
	 *            the file where the serialization data should be written
	 * @param writeId
	 *            whether write id of each string to outFile
	 * @param mapStringToId
	 *            mapStringToId is null means each string itself will be written
	 *            to outFile, otherwise the id of each string will be written
	 * @throws Exception
	 */
	@Override
	public void serialize(File outFile) throws Exception {
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

	private void serializeDfs(AbstractTrieNode node, StringBuffer sb, BufferedWriter bw) throws Exception {
		for (AbstractTrieNode nc = node.child; nc != null; nc = nc.brother) {
			sb.append(nc.val);
			if (nc.occurrence != 0) {
				int id = idMapper.getId(sb.toString());
				if (id == -1) {
					throw new Exception("serializeDfs: invalid word or bad map!");
				}
				bw.write(String.valueOf(id));
				bw.newLine();
				bw.write(String.valueOf(nc.occurrence));
				bw.newLine();
			}
			serializeDfs(nc, sb, bw);
			sb.setLength(sb.length() - 1);
		}
	}

	@Override
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception {
		this.clear();
		FileReader fr = new FileReader(inFile);
		BufferedReader br = new BufferedReader(fr);

		if (eliminatedId != null) Arrays.sort(eliminatedId);

		int n = Integer.parseInt(br.readLine().trim());
		for (int i = 0; i < n; i++) {
			String str = br.readLine().trim(); // 'string' field

			int givenId = Integer.parseInt(str); // 'string' field
			if (eliminatedId != null && Arrays.binarySearch(eliminatedId, givenId) >= 0) {
				br.readLine().trim();// read occurrence
				continue;
			}
			str = this.idMapper.getWord(givenId); // exception may occur if the trie has no string associate with strId

			int occurrence = Integer.parseInt(br.readLine().trim());
			add(str, occurrence);
		}

		br.close();
		fr.close();
	}

	@Override
	public void rearrangeId(int[] idConvertor) {}
}
