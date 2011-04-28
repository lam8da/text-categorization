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
		char[] str = word.toCharArray();
		int l, r, len = str.length;

		//trim punctuation on both sides
		for (l = 0; l < len && isMark[str[l]]; l++);
		for (r = len - 1; r >= 0 && isMark[str[r]]; r--);
		if (l > r) return;//all character are punctuation

		//check verb abbreviation
		String[] value = this.verbAbbrMap.get(String.copyValueOf(str, l, r - l + 1).toLowerCase());
		if (value != null) {
			for (int i = 0; i < value.length; i++) {
				if (value[i].length() > 0) vs.add(value[i]);
			}
			return;
		}

		//eliminate continuous punctuation
		int id = l;
		while (true) {
			while (id <= r && !isMark[str[id]])
				id++;
			if (id > r) {
				break;
			}
			if (!isMark[str[id + 1]]) {//check continuous punctuation
				id++;
				continue;
			}
			//str[id + 1] is punctuation, and id!=r because ending punctuation have been eliminated before
			process(String.copyValueOf(str, l, id - l), vs);
			while (isMark[str[id]])
				id++;
			l = id;//the next first non-punctuation character
		}
		//till now, str[l,...,r] contains at most 1 continuous punctuation

		//eliminate full stop mark and comma between numbers
		for (int i = r - 1; i > l; i--) {
			if (str[i] != '.' && str[i] != ',') continue;
			if (Character.isDigit(str[i - 1]) && Character.isDigit(str[i + 1])) {
				for (int j = i; j < r; j++)
					str[j] = str[j + 1];
				r--;
			}
		}
		len = r - l + 1;
		
		//eliminate abbreviation suffix such as 's
		while (len > 2 && (str[r - 1] == '\'' || str[r - 1] == '"') && (str[r] == 's' || str[r] == 'S' || str[r] == 't' || str[r] == 'd')) {
			r -= 2;
			len -= 2;
		}
		while (len > 3 && (str[r - 2] == '\'' || str[r - 2] == '"') && str[r - 1] == 'l' && str[r] == 'l') {
			r -= 3;
			len -= 3;
		}
		
		//eliminate connective "-" according to special case
		int letterOrDigitRequired = 3;
		id = l + letterOrDigitRequired;
//		boolean printed = false;
		while (true) {
			for (; id < r && str[id] != '-'; id++);
			if (id >= r) break;

			boolean isLetter = true;
			for (int i = id - 1; i >= id - letterOrDigitRequired; i--) {//3 or more consecutive letter on left side
				if (i < l || !Character.isLetterOrDigit(str[i])) {
					isLetter = false;
					break;
				}
			}
			for (int i = id + 1; i <= id + letterOrDigitRequired; i++) {//3 or more consecutive letter on right side
				if (i > r || !Character.isLetterOrDigit(str[i])) {
					isLetter = false;
					break;
				}
			}
			if (isLetter) {
//				if (!printed) {
//					System.out.println(String.copyValueOf(str, l, r - l + 1));
//					printed = true;
//				}
				process(String.copyValueOf(str, l, id - l), vs);
				l = id + 1;
			}
			id += letterOrDigitRequired + 1;
		}
		
		vs.add(String.copyValueOf(str, l, r - l + 1));
	}
}
