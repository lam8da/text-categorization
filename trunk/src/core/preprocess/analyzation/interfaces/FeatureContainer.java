package core.preprocess.analyzation.interfaces;

import java.io.File;

public interface FeatureContainer extends Container {
	public int getId(String word);

	public String getWord(int id);

	public void serialize(File outFile) throws Exception;
	
	public void rearrangeId();
	
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception;
}
