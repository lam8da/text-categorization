package core.preprocess.analyzation.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import core.preprocess.analyzation.interfaces.Container;
import core.preprocess.analyzation.interfaces.FeatureContainer;

public class FeatureMap implements FeatureContainer {
	private class WordInfo {
		private int id;
		private int cnt;

		private WordInfo() {
			id = -1;
			cnt = 0;
		}
	}

	private HashMap<String, WordInfo> hmap; //<word, (id, cnt)>
	private TreeMap<Integer, String> idMap; //<id, word>
	private int wordCnt;
	private int differentWordCnt;

	public FeatureMap() {
		hmap = new HashMap<String, WordInfo>(1024);
		idMap = new TreeMap<Integer, String>();
		wordCnt = 0;
		differentWordCnt = 0;
	}

	private WordInfo add(String word, int times, boolean addToIdMap) throws Exception {
		if (times <= 0) throw new Exception("times should be greater than 0!");
		WordInfo info;
		if (hmap.containsKey(word)) {
			info = hmap.get(word);
			info.cnt += times;
		}
		else {
			info = new WordInfo();
			info.id = differentWordCnt++;
			info.cnt = times;
			hmap.put(word, info);
		}
		wordCnt += times;
		if (addToIdMap) idMap.put(info.id, word);
		return info;
	}

	private void add(String word, int times, int newId) throws Exception {
		WordInfo info = add(word, times, false);
		idMap.put(newId, word);
		info.id = newId;
	}

	@Override
	public int add(String word) throws Exception {
		if (word == null) throw new Exception("word should not be null!");
		return add(word, 1, true).id;
	}

	@Override
	public int getOccurrence(String word) {
		if (hmap.containsKey(word)) {
			return hmap.get(word).cnt;
		}
		return 0;
	}

	@Override
	public int size() {
		return this.differentWordCnt;
	}

	@Override
	public int getCounting() {
		return this.wordCnt;
	}

	@Override
	public int difference(Container other) {
		int ans = 0;
		Set<String> set = hmap.keySet();
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String str = it.next();
			WordInfo info = hmap.get(str);
			int otherCnt = other.getOccurrence(str);
			if (info.cnt > otherCnt) ans++;
		}
		return ans;
	}

	@Override
	public int getId(String word) {
		if (hmap.containsKey(word)) {
			return hmap.get(word).id;
		}
		return -1;
	}

	@Override
	public String getWord(int id) {
		if (idMap.containsKey(id)) {
			return idMap.get(id);
		}
		return null;
	}

	@Override
	public void serialize(File outFile) throws Exception {
		FileWriter fw = new FileWriter(outFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.differentWordCnt));
		bw.newLine();

		Set<String> set = hmap.keySet();
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String str = it.next();
			WordInfo info = hmap.get(str);
			bw.write(str);
			bw.newLine();
			bw.write(String.valueOf(info.id));
			bw.newLine();
			bw.write(String.valueOf(info.cnt));
			bw.newLine();
		}

		bw.flush();
		bw.close();
		fw.close();
	}

	@Override
	public int[] rearrangeId() {
		int[] idConvertor = new int[idMap.lastKey() + 1];
		Arrays.fill(idConvertor, -1);
		this.idMap.clear();

		String[] strForSort = hmap.keySet().toArray(new String[0]);
		Arrays.sort(strForSort);

		for (int i = 0; i < strForSort.length; i++) {
			WordInfo info = hmap.get(strForSort[i]);
			idConvertor[info.id] = i;
			info.id = i;
			this.idMap.put(i, strForSort[i]);
		}
		return idConvertor;
	}

	@Override
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception {
		this.hmap.clear();
		this.idMap.clear();
		this.wordCnt = 0;
		this.differentWordCnt = 0;

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
}
