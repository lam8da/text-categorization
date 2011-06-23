package core.preprocess.analyzation.map;

import java.io.File;

import core.preprocess.analyzation.interfaces.Container;
import core.preprocess.analyzation.interfaces.FeatureContainer;

public class FeatureMap implements FeatureContainer {

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
	public int getId(String word) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getWord(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serialize(File outFile) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rearrangeId() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
