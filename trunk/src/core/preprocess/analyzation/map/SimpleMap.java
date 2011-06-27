package core.preprocess.analyzation.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import core.preprocess.analyzation.interfaces.SimpleContainer;

public class SimpleMap implements SimpleContainer {
	private FeatureMap idMapper;
	private TreeMap<Integer, Integer> tmap; //<id, occurrence>
	private int wordCnt;
	private int differentWordCnt;

	public SimpleMap(FeatureMap mapper) {
		this.idMapper = mapper;
		tmap = new TreeMap<Integer, Integer>();
		wordCnt = 0;
		differentWordCnt = 0;
	}

	private void add(String word, int times) throws Exception {
		if (times <= 0) throw new Exception("times should be greater than 0!");
		int id = idMapper.getId(word);
		if (id == -1) throw new Exception("the word is not in the corresponding FeatureContainer!");
		if (tmap.containsKey(id)) {
			tmap.put(id, tmap.get(id) + times);
		}
		else {
			tmap.put(id, times);
			differentWordCnt++;
		}
		wordCnt += times;
	}

	@Override
	public void add(String word) throws Exception {
		if (word == null) throw new Exception("word should not be null!");
		add(word, 1);
	}

	@Override
	public int getOccurrence(String word) {
		int id = idMapper.getId(word);
		if (tmap.containsKey(id)) return tmap.get(id);
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
	public boolean contains(String word) {
		int id = idMapper.getId(word);
		return tmap.containsKey(id);
	}

	@Override
	public void serialize(File outFile) throws Exception {
		FileWriter fw = new FileWriter(outFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(String.valueOf(this.differentWordCnt));
		bw.newLine();

		Set<Integer> set = tmap.keySet();
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			Integer i = it.next();
			bw.write(String.valueOf(i));
			bw.newLine();
			bw.write(String.valueOf(tmap.get(i)));
			bw.newLine();
		}

		bw.flush();
		bw.close();
		fw.close();
	}

	@Override
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception {
		this.tmap.clear();
		this.wordCnt = 0;
		this.differentWordCnt = 0;

		FileReader fr = new FileReader(inFile);
		BufferedReader br = new BufferedReader(fr);
		if (eliminatedId != null) Arrays.sort(eliminatedId);

		int n = Integer.parseInt(br.readLine());
		for (int i = 0; i < n; i++) {
			int givenId = Integer.parseInt(br.readLine()); // id field
			if (eliminatedId != null && Arrays.binarySearch(eliminatedId, givenId) >= 0) {
				br.readLine();// read occurrence
				continue;
			}
			String str = this.idMapper.getWord(givenId); // in order to validate

			int occurrence = Integer.parseInt(br.readLine());
			add(str, occurrence);
		}

		br.close();
		fr.close();
	}

	@Override
	public void rearrangeId(int[] idConvertor) throws Exception {
		TreeMap<Integer, Integer> newTmap = new TreeMap<Integer, Integer>();
		Set<Integer> set = tmap.keySet();
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			Integer oldId=it.next();
			int newId=idConvertor[oldId];
			if(newId==-1) throw new Exception("bad old id is preserved!");
			newTmap.put(newId, tmap.get(oldId));
		}
		tmap.clear();
		tmap=newTmap;
	}
}
