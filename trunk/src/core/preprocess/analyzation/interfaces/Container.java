package core.preprocess.analyzation.interfaces;

public interface Container {
	/**
	 * get the number of times the given word occurs in the container
	 * 
	 * @param word
	 *            the given word
	 * @return the number of times
	 */
	public int getOccurrence(String word);

	/**
	 * get the number of different words in the container
	 * 
	 * @return the number of different words
	 */
	public int size();

	/**
	 * get the number of words (concerning duplication) in the container
	 * 
	 * @return the total number of words
	 */
	public int getCounting();
}
