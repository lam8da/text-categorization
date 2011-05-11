package core.preprocess.corpus;

import java.util.Arrays;
import java.util.Vector;
import java.util.HashMap;

import core.preprocess.util.Constant;

public class BadWordHandler {
	private static final String[] VERB_ABBR = {	//verb abbreviations
		"isn't",	//1
		"aren't",	//2
		"wasn't",	//3 (the former was "asn't")
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
		"can not",		//16
		"could not",	//17
		"might not",	//18
		"must not",		//19
	};
	
	/*
	private static final char[] marks = { '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?',
			'@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~' };
	
	//excluding &'-.:@_ from "marks"
	private static final char[] badMarks = { '!', '"', '#', '$', '%', '(', ')', '*', '+', ',', '/', ';', '<', '=', '>', '?', '[', '\\', ']', '^',
			'`', '{', '|', '}', '~' };
	*/

	private boolean timeToConst;
	private boolean numToConst;
	private boolean[] isMark;
	private boolean[] isBadMark;
	private HashMap<String, String[]> verbAbbrMap;

	/**
	 * 
	 * @param timeToConst
	 *            whether turn all time format to Constant.TIME_FEATURE
	 * @param numToConst
	 *            whether turn all number format to Constant.NUM_FEATURE
	 */
	public BadWordHandler(boolean timeToConst, boolean numToConst) {
		this.isMark = new boolean[256];
		Arrays.fill(isMark, true);
		for (int i = '0'; i <= '9'; i++) {
			isMark[i] = false;
		}
		for (int i = 'a'; i <= 'z'; i++) {
			isMark[i] = false;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			isMark[i] = false;
		}

		this.isBadMark = Arrays.copyOf(isMark, 256);
		isBadMark['&'] = false;
		isBadMark['\''] = false;
		isBadMark['-'] = false;
		isBadMark['.'] = false;
		isBadMark[':'] = false;
		isBadMark['@'] = false;
		isBadMark['_'] = false;

		this.verbAbbrMap = new HashMap<String, String[]>(32);
		for (int i = 0; i < VERB_ABBR.length; i++) {
			String[] value = VERB_ABBR_UNFOLDING[i].split(" ");
			this.verbAbbrMap.put(VERB_ABBR[i], value);
		}

		this.timeToConst = timeToConst;
		this.numToConst = numToConst;
	}

	/**
	 * invoked by "process" to process the word in "str" recursively.
	 * after the processing, email-address-like string will be reserved,
	 * while urls will be separated into multiple parts according to '\'
	 * and other punctuation.
	 * time-like format extraction is still a problem.
	 * 
	 * @param str
	 *            the char sequence containing the word to be processed
	 * @param l
	 *            the start index of the word in "str"
	 * @param r
	 *            the end index (inclusive) of the word in "str"
	 * @param vs
	 *            the result vector to which the result should be added
	 */
	private void dfsProcess(char[] str, int l, int r, Vector<String> vs) {
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
			dfsProcess(str, l, id - 1, vs);
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

		//eliminate abbreviation suffix such as 's
		int len = r - l + 1;
		while (len > 2 && (str[r - 1] == '\'' || str[r - 1] == '"') && (str[r] == 's' || str[r] == 'S' || str[r] == 't' || str[r] == 'd')) {
			r -= 2;
			len -= 2;
		}
		while (len > 3 && (str[r - 2] == '\'' || str[r - 2] == '"') && str[r - 1] == 'l' && str[r] == 'l') {
			r -= 3;
			len -= 3;
		}

		//eliminate connective "-" when: at least one side contains 3(default) or more letters or digits
		int letterOrDigitRequired = 3;
		id = l + 1;
		while (true) {
			for (; id < r && str[id] != '-'; id++);
			if (id >= r) break;

			int isLetter = 2;
			for (int i = id - 1; i >= id - letterOrDigitRequired; i--) {//3 or more consecutive letters or digits on left side
				if (i < l || !Character.isLetterOrDigit(str[i])) {
					isLetter--;
					break;
				}
			}
			for (int i = id + 1; i <= id + letterOrDigitRequired; i++) {//3 or more consecutive letters or digits on right side
				if (i > r || !Character.isLetterOrDigit(str[i])) {
					isLetter--;
					break;
				}
			}
			if (isLetter > 0) {
				dfsProcess(str, l, id - 1, vs);
				l = id + 1;
			}
			id += 1;
		}

		//eliminate connective ".:'" when: both sides contain 3(default) or more letters or digits
		letterOrDigitRequired = 3;
		id = l + 1;
		while (true) {
			for (; id < r && str[id] != '.' && str[id] != ':' && str[id] != '\''; id++);
			if (id >= r) break;

			boolean isLetter = true;
			for (int i = id - 1; i >= id - letterOrDigitRequired; i--) {//3 or more consecutive letters or digits on left side
				if (i < l || !Character.isLetterOrDigit(str[i])) {
					isLetter = false;
					break;
				}
			}
			for (int i = id + 1; i <= id + letterOrDigitRequired; i++) {//3 or more consecutive letters or digits on right side
				if (i > r || !Character.isLetterOrDigit(str[i])) {
					isLetter = false;
					break;
				}
			}
			if (isLetter) {
				dfsProcess(str, l, id - 1, vs);
				l = id + 1;
			}
			id += 1;
		}

		//extract the simplest time format: HH:mm
		if (this.timeToConst) {
			if (r - l + 1 == 5) {
				if (str[l + 2] == ':' && Character.isDigit(str[l]) && Character.isDigit(str[l + 1]) && Character.isDigit(str[l + 3])
						&& Character.isDigit(str[l + 4])) {
					boolean valid = true;
					if (str[l] > '2') valid = false;
					if (str[l] == '2' && str[l + 1] > '4') valid = false;
					if (str[l + 3] > '5') valid = false;
					if (valid) {
						vs.add(Constant.TIME_FEATURE);
						return;
					}
				}
			}
		}

		//eliminate bad marks. floating point numbers such as 12/35 may be separated into two parts,
		//but this may not be so crucial.
		len = r - l + 1;
		id = l;
		while (true) {
			for (; id <= r && !this.isBadMark[str[id]]; id++);
			if (id > r) break;
			//the following "if" considers some meaningful abbreviation
			//such as I/O and short divition such as 1/2
			if (str[id] == '/' && len <= 4) {
				id++;
				continue;
			}
			dfsProcess(str, l, id - 1, vs);
			id = l = id + 1;
			len = r - l + 1;
		}

		//whether turn all number format to Constant.NUMBER_FEATURE
		if (this.numToConst) {
			for (id = l; id <= r; id++) {
				if (!Character.isDigit(str[id]) && !this.isMark[str[id]]) break;
			}
			if (id > r) {
				vs.add(Constant.NUMBER_FEATURE);
				return;
			}
		}
		vs.add(String.copyValueOf(str, l, r - l + 1));
	}

	/**
	 * given a word (may be a bad word), process it according to some syntax
	 * rule, and add the result to a vector
	 * 
	 * @param word
	 *            the given word
	 * @param vs
	 *            the result vector
	 */
	public void process(String word, Vector<String> vs) {
		char[] str = word.toCharArray();
		int l, r, len = str.length;

		//trim punctuation on both sides
		for (l = 0; l < len && isMark[str[l]]; l++);
		for (r = len - 1; r >= 0 && isMark[str[r]]; r--);
		if (l > r) return;//all character are punctuation

		dfsProcess(str, l, r, vs);
	}
}
