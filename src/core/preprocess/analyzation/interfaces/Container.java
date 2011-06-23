package core.preprocess.analyzation.interfaces;

public interface Container {
	/**
	 * add word to the container
	 * 
	 * @param word
	 *            the word to be added
	 * @return return the id of the inserted word in the trie
	 * @throws Exception
	 */
	public int add(String word) throws Exception;
	
	public int getOccurrence(String word);

	public int size();

	public int getCounting();
	
	public int difference(Container other);
}
