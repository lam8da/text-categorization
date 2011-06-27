package core.preprocess.analyzation.interfaces;

import java.io.File;

public interface FeatureContainer extends Container {
	/**
	 * add word to the container
	 * 
	 * @param word
	 *            the word to be added
	 * @return return the id of the inserted word in the container
	 * @throws Exception
	 */
	public int add(String word) throws Exception;
	
	/**
	 * get the id of the given word
	 * 
	 * @param word
	 *            the given word
	 * @return the id
	 */
	public int getId(String word);

	/**
	 * get the corresponding word via its id
	 * 
	 * @param id
	 *            the given id
	 * @return the word
	 */
	public String getWord(int id);

	/**
	 * rearrange the id of the words in the container according to their
	 * lexicographic order (other order is also valid)
	 */
	public int[] rearrangeId();

	/**
	 * find the number of words which is contained in current container but not
	 * in "other", or contained in both but the occurrence in current container
	 * is greater than that in "other"
	 * 
	 * @param other
	 *            the container to be compared with
	 * @return the expected answer
	 */
	public int difference(Container other);

	/**
	 * format:
	 *   differentWordCnt
	 *   ...
	 *   word_i
	 *   word_i.id
	 *   word_i.occurrence
	 *   ...
	 * 
	 * @param outFile
	 *            the file for output
	 * @throws Exception
	 */
	public void serialize(File outFile) throws Exception;

	/**
	 * deserialize the container from the given file and eliminated id
	 * 
	 * @param inFile
	 *            the input file
	 * @param eliminatedId
	 *            the array of eliminated ids
	 * @throws Exception
	 */
	public void deserializeFrom(File inFile, int[] eliminatedId) throws Exception;
}
