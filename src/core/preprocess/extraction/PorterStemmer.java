package core.preprocess.extraction;

public class PorterStemmer extends Stemmer {

	private char[] b; /* buffer for word to be stemmed */
	private int k, k0, j; /* j is a general offset into the string */

	/* cons(i) is TRUE <=> b[i] is a consonant. */
	private boolean cons(int i) {
		switch (b[i]) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return false;
		case 'y':
			return (i == k0) ? true : !cons(i - 1);
		default:
			return true;
		}
	}

	/*
	 * m() measures the number of consonant sequences between k0 and j. if c is
	 * a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
	 * presence,
	 * 
	 * <c><v> gives 0 <c>vc<v> gives 1 <c>vcvc<v> gives 2 <c>vcvcvc<v> gives 3
	 * ....
	 */
	private int m() {
		int n = 0;
		int i = k0;
		while (true) {
			if (i > j) return n;
			if (!cons(i)) break;
			i++;
		}
		i++;
		while (true) {
			while (true) {
				if (i > j) return n;
				if (cons(i)) break;
				i++;
			}
			i++;
			n++;
			while (true) {
				if (i > j) return n;
				if (!cons(i)) break;
				i++;
			}
			i++;
		}
	}

	/* vowelinstem() is TRUE <=> k0,...j contains a vowel */
	private boolean vowelInStem() {
		int i;
		for (i = k0; i <= j; i++)
			if (!cons(i)) return true;
		return false;
	}

	/* doublec(j) is TRUE <=> j,(j-1) contain a double consonant. */
	private boolean doubleC(int j) {
		if (j < k0 + 1) return false;
		if (b[j] != b[j - 1]) return false;
		return cons(j);
	}

	/*
	 * cvc(i) is TRUE <=> i-2,i-1,i has the form consonant - vowel - consonant
	 * and also if the second c is not w,x or y. this is used when trying to
	 * restore an e at the end of a short word. e.g.
	 * 
	 * cav(e), lov(e), hop(e), crim(e), but snow, box, tray.
	 */
	private boolean cvc(int i) {
		if (i < k0 + 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) return false;
		int ch = b[i];
		if (ch == 'w' || ch == 'x' || ch == 'y') return false;
		return true;
	}

	/* compare b1[i1,...,i1+len-1] with b2[i2,...,i2+len-1] */
	private int memcmp(char[] b1, int i1, char[] b2, int i2, int len) {
		for (int i = 0; i < len; i++) {
			if (b1[i1 + i] != b2[i2 + i]) return ((int) b1[i1 + i]) - ((int) b2[i2 + i]);
		}
		return 0;
	}

	/* ends(s) is TRUE <=> k0,...k ends with the string s. */
	private boolean ends(String str) {
		char[] s = str.toCharArray();
		int length = s[0];
		if (s[length] != b[k]) return false; /* tiny speed-up */
		if (length > k - k0 + 1) return false;
		if (memcmp(b, k - length + 1, s, 1, length) != 0) return false;
		j = k - length;
		return true;
	}

	/* copy len chars from src[i2,...] to dest[i1,...] */
	private void memmove(char[] dest, int i1, char[] src, int i2, int len) {
		for (int i = 0; i < len; i++) {
			dest[i1 + i] = src[i2 + i];
		}
	}

	/*
	 * setto(s) sets (j+1),...k to the characters in the string s, readjusting
	 * k.
	 */
	private void setTo(String str) {
		char[] s = str.toCharArray();
		int length = s[0];
		memmove(b, j + 1, s, 1, length);
		k = j + length;
	}

	/* r(s) is used further down. */
	private void r(String s) {
		if (m() > 0) setTo(s);
	}

	/*
	 * step1ab() gets rid of plurals and -ed or -ing. e.g.
	 * 
	 * caresses -> caress ponies -> poni ties -> ti caress -> caress cats -> cat
	 * 
	 * feed -> feed agreed -> agree disabled -> disable
	 * 
	 * matting -> mat mating -> mate meeting -> meet milling -> mill messing ->
	 * mess
	 * 
	 * meetings -> meet
	 */
	private void step1ab() {
		if (b[k] == 's') {
			if (ends("\04" + "sses")) k -= 2;
			else if (ends("\03" + "ies")) setTo("\01" + "i");
			else if (b[k - 1] != 's') k--;
		}
		if (ends("\03" + "eed")) {
			if (m() > 0) k--;
		}
		else if ((ends("\02" + "ed") || ends("\03" + "ing")) && vowelInStem()) {
			k = j;
			if (ends("\02" + "at")) setTo("\03" + "ate");
			else if (ends("\02" + "bl")) setTo("\03" + "ble");
			else if (ends("\02" + "iz")) setTo("\03" + "ize");
			else if (doubleC(k)) {
				k--;
				int ch = b[k];
				if (ch == 'l' || ch == 's' || ch == 'z') k++;
			}
			else if (m() == 1 && cvc(k)) setTo("\01" + "e");
		}
	}

	/* step1c() turns terminal y to i when there is another vowel in the stem. */
	private void step1c() {
		if (ends("\01" + "y") && vowelInStem()) b[k] = 'i';
	}

	/*
	 * step2() maps double suffices to single ones. so -ization ( = -ize plus
	 * -ation) maps to -ize etc. note that the string before the suffix must
	 * give m() > 0.
	 */
	private void step2() {
		switch (b[k - 1]) {
		case 'a':
			if (ends("\07" + "ational")) {
				r("\03" + "ate");
				break;
			}
			if (ends("\06" + "tional")) {
				r("\04" + "tion");
				break;
			}
			break;
		case 'c':
			if (ends("\04" + "enci")) {
				r("\04" + "ence");
				break;
			}
			if (ends("\04" + "anci")) {
				r("\04" + "ance");
				break;
			}
			break;
		case 'e':
			if (ends("\04" + "izer")) {
				r("\03" + "ize");
				break;
			}
			break;
		case 'l':
			if (ends("\03" + "bli")) {
				r("\03" + "ble");
				break;
			} /*-DEPARTURE-*/

			/*
			 * To match the published algorithm, replace this line with case
			 * 'l': if (ends("\04" "abli")) { r("\04" "able"); break; }
			 */

			if (ends("\04" + "alli")) {
				r("\02" + "al");
				break;
			}
			if (ends("\05" + "entli")) {
				r("\03" + "ent");
				break;
			}
			if (ends("\03" + "eli")) {
				r("\01" + "e");
				break;
			}
			if (ends("\05" + "ousli")) {
				r("\03" + "ous");
				break;
			}
			break;
		case 'o':
			if (ends("\07" + "ization")) {
				r("\03" + "ize");
				break;
			}
			if (ends("\05" + "ation")) {
				r("\03" + "ate");
				break;
			}
			if (ends("\04" + "ator")) {
				r("\03" + "ate");
				break;
			}
			break;
		case 's':
			if (ends("\05" + "alism")) {
				r("\02" + "al");
				break;
			}
			if (ends("\07" + "iveness")) {
				r("\03" + "ive");
				break;
			}
			if (ends("\07" + "fulness")) {
				r("\03" + "ful");
				break;
			}
			if (ends("\07" + "ousness")) {
				r("\03" + "ous");
				break;
			}
			break;
		case 't':
			if (ends("\05" + "aliti")) {
				r("\02" + "al");
				break;
			}
			if (ends("\05" + "iviti")) {
				r("\03" + "ive");
				break;
			}
			if (ends("\06" + "biliti")) {
				r("\03" + "ble");
				break;
			}
			break;
		case 'g':
			if (ends("\04" + "logi")) {
				r("\03" + "log");
				break;
			} /*-DEPARTURE-*/

		}
	}

	/* step3() deals with -ic-, -full, -ness etc. similar strategy to step2. */
	private void step3() {
		switch (b[k]) {
		case 'e':
			if (ends("\05" + "icate")) {
				r("\02" + "ic");
				break;
			}
			if (ends("\05" + "ative")) {
				r("\00" + "");
				break;
			}
			if (ends("\05" + "alize")) {
				r("\02" + "al");
				break;
			}
			break;
		case 'i':
			if (ends("\05" + "iciti")) {
				r("\02" + "ic");
				break;
			}
			break;
		case 'l':
			if (ends("\04" + "ical")) {
				r("\02" + "ic");
				break;
			}
			if (ends("\03" + "ful")) {
				r("\00" + "");
				break;
			}
			break;
		case 's':
			if (ends("\04" + "ness")) {
				r("\00" + "");
				break;
			}
			break;
		}
	}

	/* step4() takes off -ant, -ence etc., in context <c>vcvc<v>. */
	private void step4() {
		switch (b[k - 1]) {
		case 'a':
			if (ends("\02" + "al")) break;
			return;
		case 'c':
			if (ends("\04" + "ance")) break;
			if (ends("\04" + "ence")) break;
			return;
		case 'e':
			if (ends("\02" + "er")) break;
			return;
		case 'i':
			if (ends("\02" + "ic")) break;
			return;
		case 'l':
			if (ends("\04" + "able")) break;
			if (ends("\04" + "ible")) break;
			return;
		case 'n':
			if (ends("\03" + "ant")) break;
			if (ends("\05" + "ement")) break;
			if (ends("\04" + "ment")) break;
			if (ends("\03" + "ent")) break;
			return;
		case 'o':
			if (ends("\03" + "ion") && (b[j] == 's' || b[j] == 't')) break;
			if (ends("\02" + "ou")) break;
			return;
			/* takes care of -ous */
		case 's':
			if (ends("\03" + "ism")) break;
			return;
		case 't':
			if (ends("\03" + "ate")) break;
			if (ends("\03" + "iti")) break;
			return;
		case 'u':
			if (ends("\03" + "ous")) break;
			return;
		case 'v':
			if (ends("\03" + "ive")) break;
			return;
		case 'z':
			if (ends("\03" + "ize")) break;
			return;
		default:
			return;
		}
		if (m() > 1) k = j;
	}

	/*
	 * step5() removes a final -e if m() > 1, and changes -ll to -l if m() > 1.
	 */
	private void step5() {
		j = k;
		if (b[k] == 'e') {
			int a = m();
			if (a > 1 || a == 1 && !cvc(k - 1)) k--;
		}
		if (b[k] == 'l' && doubleC(k) && m() > 1) k--;
	}

	/*
	 * In stem(p,i,j), p is a char pointer, and the string to be stemmed is from
	 * p[i] to p[j] inclusive. Typically i is zero and j is the offset to the
	 * last character of a string, (p[j+1] == '\0'). The stemmer adjusts the
	 * characters p[i] ... p[j] and returns the new end-point of the string, k.
	 * Stemming never increases word length, so i <= k <= j. To turn the stemmer
	 * into a module, declare 'stem' as extern, and delete the remainder of this
	 * file.
	 */
	/*
	 * ! \brief stem a term using the Porter algorithm. Performs case
	 * normalization on its input argument.
	 * 
	 * @param str the term to stem
	 * 
	 * @return the new end point of the string.
	 */
	public String stem(String str, boolean toLower) {
		k0 = 0; /* copy the parameters into statics */
		k = str.length() - 1;
		if (k <= k0 + 1) return str; /*-DEPARTURE-*/

		/*
		 * With this line, strings of length 1 or 2 don't go through the
		 * stemming process, although no mention is made of this in the
		 * published algorithm. Remove the line to match the published
		 * algorithm.
		 */
		b = new char[100];
		if (toLower) {
			for (int i = k0; i <= k; i++) {
				b[i] = Character.toLowerCase(str.charAt(i));
			}
		}
		else {
			for (int i = k0; i <= k; i++) {
				b[i] = str.charAt(i);
			}
		}
		// System.out.println(new String(b));

		step1ab();
		// System.out.println(new String(b));

		step1c();
		// System.out.println(new String(b));

		step2();
		// System.out.println(new String(b));

		step3();
		// System.out.println(new String(b));

		step4();
		// System.out.println(new String(b));

		step5();
		// System.out.println(new String(b));

		b[k + 1] = 0;
		return new String(b, 0, k + 1);
	}
}