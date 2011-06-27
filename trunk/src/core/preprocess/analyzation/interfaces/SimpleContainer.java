package core.preprocess.analyzation.interfaces;

import java.io.File;

public interface SimpleContainer extends Container {
	/**
	 * add word to the container
	 * 
	 * @param word
	 *            the word to be added
	 * @return return the id of the inserted word in the container
	 * @throws Exception
	 */
	public void add(String word) throws Exception;

	/**
	 * whether current container contains word
	 * 
	 * @param word
	 *            the given word
	 * @return true if contains, false otherwise
	 */
	public boolean contains(String word);

	/**
	 * format:
	 * differentWordCnt
	 * ...
	 * word_i.id (in the corresponding FeatureContainer)
	 * word_i.occurrence
	 * ...
	 * 
	 * @param outFile
	 * @throws Exception
	 */
	public void serialize(File outFile) throws Exception;

	/**
	 * deserialize the container from the given file and eliminated id with the
	 * help of the FeatureContainer data member
	 * 
	 * @param inFile
	 * @param eliminatedId
	 * @throws Exception
	 */
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception;

	/**
	 * idConvertor[oldId]=newId if oldId is preserved, else
	 * idConvertor[oldId]=-1.
	 * 
	 * @param idConvertor
	 */
	public void rearrangeId(int[] idConvertor) throws Exception;
}
