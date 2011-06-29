package core.preprocess.extraction;

import core.util.Constant;

public abstract class Stemmer {
	public abstract String stem(String str, boolean toLower);

	public String stemTextBlock(String block, boolean toLower) {
		String[] words = block.split(Constant.WORD_SEPARATING_PATTERN);
		StringBuffer sb = new StringBuffer(block.length());

		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 0) {
				sb.append(this.stem(words[i], toLower)).append(Constant.WORD_SEPARATOR);
			}
		}
		return sb.toString();
	}
}
