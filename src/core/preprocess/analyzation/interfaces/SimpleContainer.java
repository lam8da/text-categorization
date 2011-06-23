package core.preprocess.analyzation.interfaces;

import java.io.File;

public interface SimpleContainer extends Container {
	public boolean contains(String word);
	
	public void serialize(File outFile, FeatureContainer mapStringToId) throws Exception;
	
	public void deserializeFrom(File inFile, FeatureContainer mapIdToString, int[] eliminatedId) throws Exception;
}
