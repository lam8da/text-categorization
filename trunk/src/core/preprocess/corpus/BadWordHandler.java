package core.preprocess.corpus;

import java.util.Arrays;
import java.util.Vector;
import java.util.HashMap;

public class BadWordHandler {
	private static final String[] VERB_ABBR = {	//verb abbreviations
		"isn't",	//1
		"aren't",	//2
		"asn't",	//3
		"weren't",	//4
		"hasn't",	//5
		"haven't",	//6
		"won't",	//7
		"wouldn't",	//8
		"shan't",	//9
		"shouldn't",//10
		"don't",	//11
		"didn't",	//12
		"oughtn't",	//13
		"daren't",	//14
		"usedn't",	//15
		"can't",	//16
		"couldn't",	//17
		"mightn't",	//18
		"mustn't",	//19
	};
	private static final String[] VERB_ABBR_UNFOLDING = {	//expanded form of verb abbreviations
		"is not",		//1
		"are not",		//2
		"was not",		//3
		"were not",		//4
		"has not",		//5
		"have not",		//6
		"will not",		//7
		"would not",	//8
		"shall not",	//9
		"should not",	//10
		"do not",		//11
		"did not",		//12
		"ought not",	//13
		"dare not",		//14
		"used not to",	//15
		"cannot",		//16
		"could not",	//17
		"might not",	//18
		"must not",		//19
	};
	private static final char[] marks = { '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?',
			'@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~' };

	private boolean[] isMark;
	private HashMap<String,String[]> verbAbbrMap;

	public BadWordHandler() {		
		this.isMark = new boolean[256];
		Arrays.fill(isMark, false);
		for (int i = 0; i < marks.length; i++) {
			isMark[marks[i]] = true;
		}
		
		this.verbAbbrMap = new HashMap<String,String[]>(32);
		for(int i = 0; i < VERB_ABBR.length; i++) {
			String[] value = VERB_ABBR_UNFOLDING[i].split(" ");
			this.verbAbbrMap.put(VERB_ABBR[i], value);
		}
	}

	/**
	 * given a bad word, process it according to some syntax rule, and add the result to a vector
	 * @param word the given word
	 * @param vs the result vector
	 */
	public void process(String word, Vector<String> vs) {
		for (int i = 0; i < VERB_ABBR.length; i++) {
			String[] value = this.verbAbbrMap.get(word.toLowerCase());
			if (value != null) {
				for (int j = 0; j < value.length; j++) {
					if (value[j].length() > 0) vs.add(value[j]);
				}
				return;
			}
		}

		char[] str = word.toCharArray();
		int l, r, len = str.length;

		for (l = 0; l < len && isMark[str[l]]; l++)
			;
		for (r = len - 1; r >= 0 && isMark[str[r]]; r--)
			;
		if (l > r) return;

		vs.add(String.copyValueOf(str, l, r - l + 1));
	}
}
