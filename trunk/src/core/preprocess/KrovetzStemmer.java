package core.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class KrovetzStemmer {

	private class CharBuffer {
		private char[] bf = new char[MAX_WORD_LENGTH << 2];
		private int len = 0;

		private CharBuffer(String str) {
			len = str.length();
			for (int i = 0; i < len; i++)
				bf[i] = str.charAt(i);
		}

		public String toString() {
			return new String(bf, 0, len);
		}

		private int length() {
			if (bf[len] == '\0')
				return len;
			for (len = 0; bf[len] != '\0'; len++)
				;
			return len;
		}

		private void setLength(int l) {
			len = l;
			bf[len] = '\0';
		}

		private void setCharAt(int id, char c) {
			bf[id] = c;
		}

		private char charAt(int id) {
			return bf[id];
		}

		private String substring(int off) {
			return new String(bf, off, length() - off);
		}

		private void replace(int s, String str) {
			int sl = str.length();
			for (int i = 0; i < sl; i++) {
				bf[s + i] = str.charAt(i);
			}
			setLength(s + sl);
		}
	}

	public KrovetzStemmer() throws FileNotFoundException {
		stemhtsize = 30013;
		k = j = 0;
		word = null;
		stemCache = new CacheEntry[stemhtsize];
		for (int i = 0; i < stemhtsize; i++) {
			/* Set things up so that the first slot is used first */
			stemCache[i] = new CacheEntry();
			stemCache[i].flag = 2;
		}
		loadTables();
	}

	// / maximum number of characters in a word to be stemmed.
	private static final int MAX_WORD_LENGTH = 25;

	/*
	 * ! \brief stem a term using the Krovetz algorithm. The stem returned may
	 * be longer than the input term. Performs case normalization on its input
	 * argument.
	 */

	private boolean myIsalpha(char ch) {
		if (ch >= 'a' && ch <= 'z')
			return true;
		if (ch >= 'A' && ch <= 'Z')
			return true;
		return false;
	}

	private char myToLower(char ch) {
		if (ch >= 'A' && ch <= 'Z')
			return (char) (ch + 'a' - 'A');
		return ch;
	}

	/*
	 * ! \brief stem a term using the Krovetz algorithm into the specified
	 * buffer. The stem returned may be longer than the input term. Performs
	 * case normalization on its input argument.
	 * 
	 * @param term the term to stem
	 * 
	 * @param buffer the buffer to hold the stemmed term. The buffer should be
	 * at MAX_WORD_LENGTH or larger.
	 * 
	 * @return the number of characters written to the buffer, including the
	 * terminating '\\0'. If 0, the caller should use the value in term.
	 */
	public String stem(String termS) {
		int i;
		boolean stem_it = true;

		CharBuffer term = new CharBuffer(termS);
		k = term.length() - 1;

		/*
		 * if the word is too long or too short, or not entirely alphabetic,
		 * just lowercase copy it into stem and return
		 */
		if ((k <= 2 - 1) || (k >= MAX_WORD_LENGTH - 1))
			stem_it = false;
		else {
			for (i = 0; i <= k; i++) {
				// 8 bit characters can be a problem on windows
				if (!myIsalpha(term.charAt(i))) {
					stem_it = false;
					break;
				}
			}
		}

		if (!stem_it) {
			for (i = 0; i <= k; i++)
				term.setCharAt(i, myToLower(term.charAt(i)));
			term.setLength(k + 1);
			return term.toString();
		}

		/* Check to see if it's in the cache. */
		/* If it's found, mark the slot in which it is found */
		/* Note that there is no need to lowercase the term in this case */
		DictEntry dep = null;

		/* --- Hashing in a fixed sized table. */
		// int[] ptr=new int[6];
		// strncpy((char *)ptr, term, 12);
		// int hval = ((ptr[0]<<4)^ptr[1]^ptr[2]^ptr[3]^ptr[4]^ptr[5]) %
		// stemhtsize;
		int hval = termS.hashCode() % stemhtsize;
		if (hval < 0) {
			hval = hval + stemhtsize;
		}

		if (termS.equals(stemCache[hval].word1)) {
			stemCache[hval].flag = 1;
			return stemCache[hval].stem1;
		} else if (termS.equals(stemCache[hval].word2)) {
			stemCache[hval].flag = 2;
			return stemCache[hval].stem2;
		}

		/*
		 * 'word' is a pointer, global to this file, for manipulating the word
		 * in the buffer provided through the passed in pointer 'stem'.
		 */
		word = new CharBuffer(termS.toLowerCase());/* lowercase the local copy */
		word.setLength(k + 1);

		/*
		 * the basic algorithm is to check the dictionary, and leave the word as
		 * it is if the word is found. Otherwise, recognize plurals, tense, etc.
		 * and normalize according to the rules for those affixes. Check against
		 * the dictionary after each stage, so `longings' -> `longing' rather
		 * than `long'. Finally, deal with some derivational endings. The -ion,
		 * -er, and -ly endings must be checked before -ize. The -ity ending
		 * must come before -al, and -ness must come before -ly and -ive.
		 * Finally, -ncy must come before -nce (because -ncy is converted to
		 * -nce for some instances).
		 */

		/*
		 * This while loop will never repeat; it is only here to allow the break
		 * statement to be used to escape as soon as a word is recognized.
		 */
		// int lambdacnt = -1;
		while (true) {
			if ((dep = getDep(word)) != null)
				break;
			plural();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			pastTense();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			aspect();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ity_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ness_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ion_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			er_and_or_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ly_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			al_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ive_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ize_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ment_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ble_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ism_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ic_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			ncy_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			if ((dep = getDep(word)) != null)
				break;
			nce_endings();
			// System.out.println((lambdacnt += 1) + " " + word);

			dep = getDep(word);
			break;
		}
		// System.out.println(word);

		/*
		 * try for a direct mapping (allows for cases like `Italian'->`Italy'
		 * and `Italians'->`Italy')
		 */
		if (dep != null && dep.root.length() != 0) {
			word = new CharBuffer(dep.root);
		} else {
			// word = new CharBuffer(termS);// ????
		}
		/* Enter into cache, at the place not used by the last cache hit */
		if (stemCache[hval].flag == 2) {
			stemCache[hval].word1 = termS;
			stemCache[hval].stem1 = word.toString();
			stemCache[hval].flag = 1;
		} else {
			stemCache[hval].word2 = termS;
			stemCache[hval].stem2 = word.toString();
			stemCache[hval].flag = 2;
		}
		return word.toString();
	}

	/*
	 * ! \brief Add an entry to the stemmer's dictionary table.
	 * 
	 * @param variant the spelling for the entry.
	 * 
	 * @param word the stem to use for the variant. If "", the variant stems to
	 * itself.
	 * 
	 * @param exc Is the word an exception to the spelling rules.
	 */
	/*
	 * Adds a stem entry into the hash table; forces the stemmer to stem
	 * <variant> to <word>. If <word> == "", <variant> is stemmed to itself.
	 */
	private void addTableEntry(String variant, String word, boolean exc) {
		if (dictEntries.containsKey(variant)) {
			// duplicate.
			System.out.println("kstem_add_table_entry: Duplicate word "
					+ variant + " will be ignored.");
			return;
		}
		DictEntry entry = new DictEntry(exc, word);
		// should test for duplicates here.
		dictEntries.put(variant, entry);
	}

	private void addTableEntry(String variant, String word) {
		addTableEntry(variant, word, false);
	}

	// / Dictionary table entry
	private class DictEntry {
		private DictEntry(boolean exc, String r) {
			this.exception = exc;
			this.root = r;
		}

		// / is the word an exception to stemming rules?
		private boolean exception;
		// / stem to use for this entry.
		private String root;
	}

	// / Two term hashtable entry for caching across calls
	private class CacheEntry {
		// / flag for first or second entry most recently used.
		char flag;
		// / first entry variant
		String word1 = new String();
		// / first entry stem
		String stem1 = new String();
		// / second entry variant
		String word2 = new String();
		// / second entry stem
		String stem2 = new String();
	}

	// operates on atribute word.
	private boolean endsIn(String str) {
		int sufflength = str.length();
		int r = (k + 1) - sufflength; /* length of word before this suffix */
		boolean match;

		if (sufflength > k)
			return (false);

		match = (word.substring(r).equals(str));
		j = (match ? r - 1 : k); /*
								 * use r-1 since j is an index rather than
								 * length
								 */
		return (match);
	}

	/* replace old suffix with str */
	private void setSuffix(String str) {
		int length = str.length();
		word.replace(j + 1, str);
		k = j + length;
		word.setLength(k + 1);
	}

	/*
	 * getdep(word) returns NULL if word is not found in the dictionary, and
	 * returns a pointer to a dictentry if found
	 */
	private DictEntry getDep(CharBuffer w) {
		/* don't bother to check for words that are short */
		if (w.length() <= 1) {
			return null;
		} else {
			return dictEntries.get(w.toString());
		}
	}

	/*
	 * lookup(word) returns false if word is not found in the dictionary, and
	 * true if it is
	 */
	private boolean lookup(CharBuffer w) {
		return getDep(w) != null;
	}

	/* cons() returns TRUE if word[i] is a consonant. */
	private boolean cons(int i) {
		char ch = word.charAt(i);
		if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u')
			return (false);

		if (ch != 'y' || i == 0)
			return (true);
		else {
			/*
			 * ch == y, test previous char. If vowel, y is consonant the case of
			 * yy (previously handled via recursion) is ignored.
			 */
			ch = word.charAt(i - 1);
			return (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u');
		}
	}

	/* This routine is useful for ensuring that we don't stem acronyms */
	private boolean vowelInStem() {
		for (int i = 0; i < (j + 1); i++)
			if (vowel(i))
				return (true);
		return (false);
	}

	private boolean vowel(int i) {
		return !cons(i);
	}

	/* return TRUE if word ends with a double consonant */
	private boolean doubleC(int i) {
		if (i < 1)
			return (false);

		if (word.charAt(i) != word.charAt(i - 1))
			return (false);

		return (cons(i));
	}

	/* convert plurals to singular form, and `-ies' to `y' */
	private void plural() {
		if ((word.charAt(k)) == 's') {
			if (endsIn("ies")) {
				word.setLength(j + 3);
				k--;
				if (lookup(word)) /* ensure calories -> calorie */
					return;
				k++;
				word.setCharAt(j + 3, 's');
				setSuffix("y");
			} else if (endsIn("es")) {
				/* try just removing the "s" */
				word.setLength(j + 2);
				k--;

				/*
				 * note: don't check for exceptions here. So, `aides' -> `aide',
				 * but `aided' -> `aid'. The exception for double s is used to
				 * prevent crosses -> crosse. This is actually correct if
				 * crosses is a plural noun (a type of racket used in lacrosse),
				 * but the verb is much more common
				 */

				if ((lookup(word))
						&& j > 0
						&& !((word.charAt(j) == 's') && (word.charAt(j - 1) == 's')))
					return;

				/* try removing the "es" */

				word.setLength(j + 1);
				k--;
				if (lookup(word))
					return;

				/* the default is to retain the "e" */
				word.setCharAt(j + 1, 'e');
				word.setLength(j + 2);
				k++;
				return;
			} else {
				if ((k + 1) > 3 && (word.charAt(k - 1)) != 's'
						&& !endsIn("ous")) {
					/*
					 * unless the word ends in "ous" or a double "s", remove the
					 * final "s"
					 */
					word.setLength(k);
					k--;
				}
			}
		}
	}

	/* convert past tense (-ed) to present, and `-ied' to `y' */
	private void pastTense() {
		/*
		 * Handle words less than 5 letters with a direct mapping This prevents
		 * (fled -> fl).
		 */

		if ((k + 1) <= 4)
			return;

		DictEntry dep = null;

		if (endsIn("ied")) {
			word.setLength(j + 3);
			k--;
			if (lookup(word)) /* we almost always want to convert -ied to -y, but */
				return; /* this isn't true for short words (died->die) */
			k++; /* I don't know any long words that this applies to, */
			// word.setLength(j + 4);
			word.setCharAt(j + 3, 'd'); /* but just in case... */
			setSuffix("y");
			return;
		}

		/* the vowelinstem() is necessary so we don't stem acronyms */
		if (endsIn("ed") && vowelInStem()) {
			/* see if the root ends in `e' */
			word.setLength(j + 2);
			k = j + 1;

			if ((dep = getDep(word)) != null)
				if (!(dep.exception)) /*
									 * if it's in the dictionary and not an
									 * exception
									 */
					return;

			/* try removing the "ed" */
			word.setLength(j + 1);
			k = j;
			if (lookup(word))
				return;

			/*
			 * try removing a doubled consonant. if the root isn't found in the
			 * dictionary, the default is to leave it doubled. This will
			 * correctly capture `backfilled' -> `backfill' instead of
			 * `backfill' -> `backfille', and seems correct most of the time
			 */

			if (doubleC(k)) {
				word.setLength(k);
				k--;
				if (lookup(word))
					return;
				word.setCharAt(k + 1, word.charAt(k));
				k++;
				return;
			}

			/* if we have a `un-' prefix, then leave the word alone */
			/* (this will sometimes screw up with `under-', but we */
			/* will take care of that later) */

			if ((word.charAt(0) == 'u') && (word.charAt(1) == 'n')) {
				word.setCharAt(k + 1, 'e');
				word.setCharAt(k + 2, 'd');
				k = k + 2;
				return;
			}

			/*
			 * it wasn't found by just removing the `d' or the `ed', so prefer
			 * to end with an `e' (e.g., `microcoded' -> `microcode').
			 */

			word.setCharAt(j + 1, 'e');
			word.setLength(j + 2);
			k = j + 1;
			return;
		}
	}

	/* handle `-ing' endings */
	private void aspect() {
		/*
		 * handle short words (aging -> age) via a direct mapping. This prevents
		 * (thing -> the) in the version of this routine that ignores
		 * inflectional variants that are mentioned in the dictionary (when the
		 * root is also present)
		 */

		if ((k + 1) <= 5)
			return;

		DictEntry dep = null;
		/* the vowelinstem() is necessary so we don't stem acronyms */
		if (endsIn("ing") && vowelInStem()) {

			/* try adding an `e' to the stem and check against the dictionary */
			word.setCharAt(j + 1, 'e');
			word.setLength(j + 2);
			k = j + 1;

			if ((dep = getDep(word)) != null)
				if (!(dep.exception)) /*
									 * if it's in the dictionary and not an
									 * exception
									 */
					return;

			/* adding on the `e' didn't work, so remove it */
			word.setLength(k);
			k--; /* note that `ing' has also been removed */

			if (lookup(word))
				return;

			/* if I can remove a doubled consonant and get a word, then do so */
			if (doubleC(k)) {
				k--;
				word.setLength(k + 1);
				if (lookup(word))
					return;
				word.setCharAt(k + 1, word.charAt(k)); /*
														 * restore the doubled
														 * consonant
														 */

				/* the default is to leave the consonant doubled */
				/* (e.g.,`fingerspelling' -> `fingerspell'). Unfortunately */
				/*
				 * `bookselling' -> `booksell' and `mislabelling' ->
				 * `mislabell').
				 */
				/*
				 * Without making the algorithm significantly more complicated,
				 * this
				 */
				/* is the best I can do */
				k++;
				return;
			}

			/*
			 * the word wasn't in the dictionary after removing the stem, and
			 * then checking with and without a final `e'. The default is to add
			 * an `e' unless the word ends in two consonants, so `microcoding'
			 * -> `microcode'. The two consonants restriction wouldn't normally
			 * be necessary, but is needed because we don't try to deal with
			 * prefixes and compounds, and most of the time it is correct (e.g.,
			 * footstamping -> footstamp, not footstampe; however, decoupled ->
			 * decoupl). We can prevent almost all of the incorrect stems if we
			 * try to do some prefix analysis first
			 */

			if (j > 0 && cons(j) && cons(j - 1)) {
				k = j;
				word.setLength(k + 1);
				return;
			}

			word.setCharAt(j + 1, 'e');
			word.setLength(j + 2);
			k = j + 1;
			return;
		}
	}

	/* handle some derivational endings */

	/*
	 * this routine deals with -ion, -ition, -ation, -ization, and -ication. The
	 * -ization ending is always converted to -ize
	 */
	private void ion_endings() {
		int old_k = k;

		if (endsIn("ization")) { /*
								 * the -ize ending is very productive, so simply
								 * accept it as the root
								 */
			word.setCharAt(j + 3, 'e');
			word.setLength(j + 4);
			k = j + 3;
			return;
		}

		if (endsIn("ition")) {
			word.setCharAt(j + 1, 'e');
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word)) /*
							 * remove -ition and add `e', and check against the
							 * dictionary
							 */
				return; /* (e.g., definition->define, opposition->oppose) */

			/* restore original values */
			word.setCharAt(j + 1, 'i');
			word.setCharAt(j + 2, 't');
			k = old_k;
		}

		if (endsIn("ation")) {
			word.setCharAt(j + 3, 'e');
			word.setLength(j + 4);
			k = j + 3;
			if (lookup(word)) /*
							 * remove -ion and add `e', and check against the
							 * dictionary
							 */
				return; /* (elmination -> eliminate) */

			word.setCharAt(j + 1, 'e'); /*
										 * remove -ation and add `e', and check
										 * against the dictionary
										 */
			word.setLength(j + 2); /* (allegation -> allege) */
			k = j + 1;
			if (lookup(word))
				return;

			word.setLength(j + 1); /*
									 * just remove -ation (resignation->resign)
									 * and check dictionary
									 */
			k = j;
			if (lookup(word))
				return;

			/* restore original values */
			word.setCharAt(j + 1, 'a');
			word.setCharAt(j + 2, 't');
			word.setCharAt(j + 3, 'i');
			word.setCharAt(j + 4, 'o'); /*
										 * no need to restore word.charAt(j+5]
										 * (n); it was never changed
										 */
			k = old_k;
		}

		/*
		 * test -ication after -ation is attempted (e.g.,
		 * `complication->complicate' rather than `complication->comply')
		 */

		if (endsIn("ication")) {
			word.setCharAt(j + 1, 'y');
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word)) /*
							 * remove -ication and add `y', and check against
							 * the dictionary
							 */
				return; /* (e.g., amplification -> amplify) */

			/* restore original values */
			word.setCharAt(j + 1, 'i');
			word.setCharAt(j + 2, 'c');
			k = old_k;
		}

		if (endsIn("ion")) {
			word.setCharAt(j + 1, 'e');
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word)) /*
							 * remove -ion and add `e', and check against the
							 * dictionary
							 */
				return;

			word.setLength(j + 1);
			k = j;
			if (lookup(word)) /*
							 * remove -ion, and if it's found, treat that as the
							 * root
							 */
				return;

			/* restore original values */
			word.setCharAt(j + 1, 'i');
			word.setCharAt(j + 2, 'o');
			k = old_k;
		}

		return;
	}

	/*
	 * this routine deals with -er, -or, -ier, and -eer. The -izer ending is
	 * always converted to -ize
	 */
	private void er_and_or_endings() {
		int old_k = k;

		char word_char; /* so we can remember if it was -er or -or */

		if (endsIn("izer")) { /*
							 * -ize is very productive, so accept it as the root
							 */
			word.setLength(j + 4);
			k = j + 3;
			return;
		}

		if (endsIn("er") || endsIn("or")) {
			word_char = word.charAt(j + 1);
			if (doubleC(j)) {
				word.setLength(j);
				k = j - 1;
				if (lookup(word))
					return;
				word.setCharAt(j, word.charAt(j - 1)); /*
														 * restore the doubled
														 * consonant
														 */
			}

			if (word.charAt(j) == 'i') { /* do we have a -ier ending? */
				word.setCharAt(j, 'y');
				word.setLength(j + 1);
				k = j;
				if (lookup(word)) /* yes, so check against the dictionary */
					return;
				word.setCharAt(j, 'i'); /* restore the endings */
				word.setCharAt(j + 1, 'e');
			}

			if (word.charAt(j) == 'e') { /* handle -eer */
				word.setLength(j);
				k = j - 1;
				if (lookup(word))
					return;
				word.setCharAt(j, 'e');
			}

			word.setLength(j + 2); /* remove the -r ending */
			k = j + 1;
			if (lookup(word))
				return;
			word.setLength(j + 1); /* try removing -er/-or */
			k = j;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'e'); /* try removing -or and adding -e */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, word_char); /*
											 * restore the word to the way it
											 * was
											 */
			word.setCharAt(j + 2, 'r');
			k = old_k;
		}
	}

	/*
	 * this routine deals with -ly endings. The -ally ending is always converted
	 * to -al Sometimes this will temporarily leave us with a non-word (e.g.,
	 * heuristically maps to heuristical), but then the -al is removed in the
	 * next step.
	 */
	private void ly_endings() {
		int old_k = k;

		if (endsIn("ly")) {
			word.setCharAt(j + 2, 'e'); /* try converting -ly to -le */
			if (lookup(word))
				return;
			word.setCharAt(j + 2, 'y');

			word.setLength(j + 1); /* try just removing the -ly */
			k = j;
			if (lookup(word))
				return;
			if (j > 0 && (word.charAt(j - 1) == 'a') && (word.charAt(j) == 'l'))
				/*
				 * always convert -ally to -al
				 */
				return;
			word.setCharAt(j + 1, 'l');
			k = old_k;

			if (j > 0 && (word.charAt(j - 1) == 'a') && (word.charAt(j) == 'b')) {
				/*
				 * always convert - ably to - able
				 */
				word.setCharAt(j + 2, 'e');
				k = j + 2;
				return;
			}

			if (word.charAt(j) == 'i') { /* e.g., militarily -> military */
				word.setCharAt(j, 'y');
				word.setLength(j + 1);
				k = j;
				if (lookup(word))
					return;
				word.setCharAt(j, 'i');
				word.setCharAt(j + 1, 'l');
				k = old_k;
			}

			word.setLength(j + 1); /* the default is to remove -ly */
			k = j;
		}
		return;
	}

	/*
	 * this routine deals with -al endings. Some of the endings from the
	 * previous routine are finished up here.
	 */
	private void al_endings() {
		int old_k = k;

		if (endsIn("al")) {
			word.setLength(j + 1);
			k = j;
			if (lookup(word)) /* try just removing the -al */
				return;

			if (doubleC(j)) { /* allow for a doubled consonant */
				word.setLength(j);
				k = j - 1;
				if (lookup(word))
					return;
				word.setCharAt(j, word.charAt(j - 1));
			}

			word.setCharAt(j + 1, 'e'); /* try removing the -al and adding -e */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, 'u'); /* try converting -al to -um */
			word.setCharAt(j + 2, 'm'); /* (e.g., optimal - > optimum ) */
			k = j + 2;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, 'a'); /* restore the ending to the way it was */
			word.setCharAt(j + 2, 'l');
			word.setLength(j + 3);
			k = old_k;

			if (j > 0 && (word.charAt(j - 1) == 'i') && (word.charAt(j) == 'c')) {
				word.setLength(j - 1); /* try removing -ical */
				k = j - 2;
				if (lookup(word))
					return;

				word.setCharAt(j - 1, 'y'); /*
											 * try turning -ical to -y (e.g.,
											 * bibliographical)
											 */
				word.setLength(j);
				k = j - 1;
				if (lookup(word))
					return;

				word.setCharAt(j - 1, 'i');
				word.setCharAt(j, 'c');
				word.setLength(j + 1); /*
										 * the default is to convert -ical to
										 * -ic
										 */
				k = j;
				return;
			}

			if (word.charAt(j) == 'i') { /*
										 * sometimes -ial endings should be
										 * removed
										 */
				word.setLength(j); /* (sometimes it gets turned into -y, but we */
				k = j - 1; /* aren't dealing with that case for now) */
				if (lookup(word))
					return;
				word.setCharAt(j, 'i');
				k = old_k;
			}

		}
		return;
	}

	/*
	 * this routine deals with -ive endings. It normalizes some of the -ative
	 * endings directly, and also maps some -ive endings to -ion.
	 */
	private void ive_endings() {
		int old_k = k;

		if (endsIn("ive")) {
			word.setLength(j + 1); /* try removing -ive entirely */
			k = j;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, 'e'); /* try removing -ive and adding -e */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'i');
			word.setCharAt(j + 2, 'v');

			if (j > 0 && (word.charAt(j - 1) == 'a') && (word.charAt(j) == 't')) {
				word.setCharAt(j - 1, 'e'); /* try removing -ative and adding -e */
				word.setLength(j); /* (e.g., determinative -> determine) */
				k = j - 1;
				if (lookup(word))
					return;
				word.setLength(j - 1); /* try just removing -ative */
				if (lookup(word))
					return;
				word.setCharAt(j - 1, 'a');
				word.setCharAt(j, 't');
				k = old_k;
			}

			/* try mapping -ive to -ion (e.g., injunctive/injunction) */
			word.setCharAt(j + 2, 'o');
			word.setCharAt(j + 3, 'n');
			if (lookup(word))
				return;

			word.setCharAt(j + 2, 'v'); /* restore the original values */
			word.setCharAt(j + 3, 'e');
			k = old_k;
		}
		return;
	}

	/* this routine deals with -ize endings. */
	private void ize_endings() {
		int old_k = k;

		if (endsIn("ize")) {
			word.setLength(j + 1); /* try removing -ize entirely */
			k = j;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'i');

			if (doubleC(j)) { /* allow for a doubled consonant */
				word.setLength(j);
				k = j - 1;
				if (lookup(word))
					return;
				word.setCharAt(j, word.charAt(j - 1));
			}

			word.setCharAt(j + 1, 'e'); /* try removing -ize and adding -e */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'i');
			word.setCharAt(j + 2, 'z');
			k = old_k;
		}
		return;
	}

	/* this routine deals with -ment endings. */
	private void ment_endings() {
		int old_k = k;

		if (endsIn("ment")) {
			word.setLength(j + 1);
			k = j;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'm');
			k = old_k;
		}
		return;
	}

	/*
	 * this routine deals with -ity endings. It accepts -ability, -ibility, and
	 * -ality, even without checking the dictionary because they are so
	 * productive. The first two are mapped to -ble, and the -ity is remove for
	 * the latter
	 */
	private void ity_endings() {
		int old_k = k;

		if (endsIn("ity")) {
			word.setLength(j + 1); /* try just removing -ity */
			k = j;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'e'); /* try removing -ity and adding -e */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word))
				return;
			word.setCharAt(j + 1, 'i');
			word.setCharAt(j + 2, 't');
			k = old_k;

			/*
			 * the -ability and -ibility endings are highly productive, so just
			 * accept them
			 */
			if (j > 0 && (word.charAt(j - 1) == 'i') && (word.charAt(j) == 'l')) {
				word.setCharAt(j - 1, 'l'); /* convert to -ble */
				word.setCharAt(j, 'e');
				word.setLength(j + 1);
				k = j;
				return;
			}

			/* ditto for -ivity */
			if (j > 0 && (word.charAt(j - 1) == 'i') && (word.charAt(j) == 'v')) {
				word.setCharAt(j + 1, 'e'); /* convert to -ive */
				word.setLength(j + 2);
				k = j + 1;
				return;
			}

			/* ditto for -ality */
			if (j > 0 && (word.charAt(j - 1) == 'a') && (word.charAt(j) == 'l')) {
				word.setLength(j + 1);
				k = j;
				return;
			}

			/*
			 * if the root isn't in the dictionary, and the variant *is* there,
			 * then use the variant. This allows `immunity'->`immune', but
			 * prevents `capacity'->`capac'. If neither the variant nor the root
			 * form are in the dictionary, then remove the ending as a default
			 */

			if (lookup(word))
				return;

			/* the default is to remove -ity altogether */
			word.setLength(j + 1);
			k = j;
			return;
		}
	}

	/* handle -able and -ible */
	private void ble_endings() {
		int old_k = k;
		char word_char;

		if (endsIn("ble")) {
			if (!((word.charAt(j) == 'a') || (word.charAt(j) == 'i')))
				return;
			word_char = word.charAt(j);
			word.setLength(j); /* try just removing the ending */
			k = j - 1;
			if (lookup(word))
				return;
			if (doubleC(k)) { /* allow for a doubled consonant */
				word.setLength(k);
				k--;
				if (lookup(word))
					return;
				k++;
				word.setCharAt(k, word.charAt(k - 1));
			}
			word.setCharAt(j, 'e'); /* try removing -a/ible and adding -e */
			word.setLength(j + 1);
			k = j;
			if (lookup(word))
				return;

			word.setCharAt(j, 'a'); /* try removing -able and adding -ate */
			word.setCharAt(j + 1, 't'); /* (e.g., compensable/compensate) */
			word.setCharAt(j + 2, 'e');
			word.setLength(j + 3);
			k = j + 2;
			if (lookup(word))
				return;

			word.setCharAt(j, word_char); /* restore the original values */
			word.setCharAt(j + 1, 'b');
			word.setCharAt(j + 2, 'l');
			word.setCharAt(j + 3, 'e');
			k = old_k;
		}
		return;
	}

	/* handle -ness */
	private void ness_endings() {
		if (endsIn("ness")) { /*
							 * this is a very productive endings, so just accept
							 * it
							 */
			word.setLength(j + 1);
			k = j;
			if (word.charAt(j) == 'i')
				word.setCharAt(j, 'y');
		}
		return;
	}

	/* handle -ism */
	private void ism_endings() {
		if (endsIn("ism")) { /*
							 * this is a very productive ending, so just accept
							 * it
							 */
			word.setLength(j + 1);
			k = j;
		}
		return;
	}

	/*
	 * handle -ic endings. This is fairly straightforward, but this is also the
	 * only place we try *expanding* an ending, -ic -> -ical. This is to handle
	 * cases like `canonic' -> `canonical'
	 */
	private void ic_endings() {
		if (endsIn("ic")) {
			word.setCharAt(j + 3, 'a'); /* try converting -ic to -ical */
			word.setCharAt(j + 4, 'l');
			word.setLength(j + 5);
			k = j + 4;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, 'y'); /* try converting -ic to -y */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, 'e'); /* try converting -ic to -e */
			if (lookup(word))
				return;

			word.setLength(j + 1); /* try removing -ic altogether */
			k = j;
			if (lookup(word))
				return;

			word.setCharAt(j + 1, 'i'); /* restore the original ending */
			word.setCharAt(j + 2, 'c');
			word.setLength(j + 3);
			k = j + 2;
		}
		return;
	}

	/* handle -ency and -ancy */
	private void ncy_endings() {
		if (endsIn("ncy")) {
			if (!((word.charAt(j) == 'e') || (word.charAt(j) == 'a')))
				return;
			word.setCharAt(j + 2, 't'); /* try converting -ncy to -nt */
			word.setLength(j + 3); /* (e.g., constituency -> constituent) */
			k = j + 2;

			if (lookup(word))
				return;

			word.setCharAt(j + 2, 'c'); /* the default is to convert it to -nce */
			word.setCharAt(j + 3, 'e');
			k = j + 3;
		}
		return;
	}

	/* handle -ence and -ance */
	private void nce_endings() {
		int old_k = k;

		char word_char;

		if (endsIn("nce")) {
			if (!((word.charAt(j) == 'e') || (word.charAt(j) == 'a')))
				return;
			word_char = word.charAt(j);
			word.setCharAt(j, 'e'); /*
									 * try converting -e/ance to -e
									 * (adherance/adhere)
									 */
			word.setLength(j + 1);
			k = j;
			if (lookup(word))
				return;
			word.setLength(j); /*
								 * try removing -e/ance altogether
								 * (disappearance/disappear)
								 */
			k = j - 1;
			if (lookup(word))
				return;
			word.setCharAt(j, word_char); /* restore the original ending */
			word.setCharAt(j + 1, 'n');
			k = old_k;
		}
		return;
	}

	// maint.
	void loadTables() throws FileNotFoundException {
		/* Initialize hash table */
		Scanner in;

		try {
			in = new Scanner(new File("res/KrovetzStemmer-exceptions"));
			while (in.hasNext()) {
				addTableEntry(in.next(), "", true);
			}

			in = new Scanner(new File("res/KrovetzStemmer-headwords"));
			while (in.hasNext()) {
				addTableEntry(in.next(), "");
			}

			in = new Scanner(new File("res/KrovetzStemmer-conflation_pair"));
			while (in.hasNext()) {
				String v = in.next();
				String w = in.next();
				addTableEntry(v, w);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	private HashMap<String, DictEntry> dictEntries = new HashMap<String, DictEntry>();

	// this needs to be a bounded size cache.
	// kstem.cpp uses size 30013 entries.
	CacheEntry[] stemCache;
	// size
	int stemhtsize;
	// state
	int k;
	int j;
	// pointer to the output buffer
	CharBuffer word;
}
