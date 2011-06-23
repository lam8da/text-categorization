package core.preprocess.analyzation.map;

import java.io.File;

import core.preprocess.analyzation.interfaces.Container;
import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.analyzation.interfaces.SimpleContainer;

public class SimpleMap implements SimpleContainer {

	@Override
	public int add(String word) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOccurrence(String word) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCounting() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int difference(Container other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean contains(String word) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void serialize(File outFile, FeatureContainer mapStringToId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deserializeFrom(File inFile, FeatureContainer mapIdToString, int[] eliminatedId) throws Exception {
		// TODO Auto-generated method stub

	}

}
