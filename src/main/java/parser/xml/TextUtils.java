package parser.xml;

import com.google.common.base.Strings;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacter.UnicodeBlock;
import parser.xml.TextDiffParser.Diff;
import parser.xml.TextDiffParser.Operation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {

	public static final Pattern MAIL_REGEX = Pattern.compile("[-_.0-9A-Za-z]{1,64}@[-_0-9A-Za-z]{1,255}[-_.0-9A-Za-z]{1,255}");
	public static final Pattern MENTION_OR_HASHTAG_PATTERN = Pattern.compile("(?:^|\\s)[#@][\\w]+");
	public static final Pattern RETWEET_PATTERN = Pattern.compile("RT:");
	public static final Pattern REPLY_MSG_PATTERN = Pattern.compile("\\b[rR][eE]\\b:");
	public static final Pattern LOWER_CASE_WORD_PATTERN = Pattern.compile("\\b[A-Z]{2,}\\b");

	public static final String LATIN_LETTERS = "a-zA-Z";
	public static final String ARABIC_CHARACTERS = "\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF";
	public static final String CJK_CHARACTERS = "\\u4E00-\\u9FFF\\u3400-\\u4DFF";
	public static final String THAI_CHARACTERS = "\\u0E00-\\u0E7F";
	public static final String CYRYLIC_CHARACTERS = "\\u0400-\\u052F\\u2DE0-\\u2DFF\\uA640-\\uA69F\\u1D2B\\u1D78";

	public static final String LETTERS = LATIN_LETTERS + ARABIC_CHARACTERS + CJK_CHARACTERS + THAI_CHARACTERS + CYRYLIC_CHARACTERS;
	public static final Pattern STRIP_PATTERN = Pattern.compile("^[^" + LETTERS + "]+|[^" + LETTERS + "]+$");

	/**
	 * changes words written in capital letters to lower case.
	 * @param text text to convert
	 * @return converted text
	 */
	public static String capitalWordsToLowerCase(String text) {
		Matcher m = LOWER_CASE_WORD_PATTERN.matcher(text);

		StringBuilder sb = new StringBuilder(text.length());
		int last = 0;
		while (m.find()) {
			sb.append(text.substring(last, m.start()));
			sb.append(m.group(0).toLowerCase());
			last = m.end();
		}
		sb.append(text.substring(last));

		return sb.toString();
	}

	public static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([\\da-fA-F]{4})");

	public static String escapeUnicodes(String text) {
		String result = text;
		Matcher m = UNICODE_PATTERN.matcher(text);
		while (m.find()) {
			final String ch = Character.toString((char) Integer.parseInt(m.group(1), 16));
			result = result.replace(m.group(), ch);
		}

		return result;
	}

	private static final int LAST_CONTROL_UNICODE = 0x001f;
	private static final int MYSQL_LAST_SUPPORTED_UNICODE = 0xffff;

	public static String removeInvalidChars(String text) {
		if (null != text) {
			final int length = text.length();
			int codepoint = 0;
			StringBuilder sb = null;
			for (int offset = 0;offset < length;) {
				codepoint = text.codePointAt(offset);
				if (codepoint <= LAST_CONTROL_UNICODE || codepoint > MYSQL_LAST_SUPPORTED_UNICODE) {
					if (sb == null) {
						sb = new StringBuilder(length);
						sb.append(text, 0, offset);
					}
				} else if (sb != null) {
					sb.appendCodePoint(codepoint);
				}
				offset += Character.charCount(codepoint);
			}
			String result = text;
			if (sb != null) {
				result = sb.toString();
			}
			return result.trim();
		}
		return null;
	}

	private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

	public static String removeDiacritics(CharSequence text) {
		String normText = Normalizer.normalize(text, Normalizer.Form.NFD);
		return DIACRITICS_PATTERN.matcher(normText).replaceAll("");
	}

	private static final Pattern NORMALIZE_SPACES_PATTERN = Pattern.compile("\\s{2,}");

	/**
	 * trims string and changes all other spaces to only one
	 */
	public static String normalizeSpaces(String text) {
		return NORMALIZE_SPACES_PATTERN.matcher(text.trim()).replaceAll(" ");
	}

	public static InputStream skipBOM(InputStream inputStream) throws IOException {
		PushbackInputStream pushbackInputStream =
				new PushbackInputStream(new BufferedInputStream(inputStream), 3);

		byte[] bom = new byte[3];
		if (pushbackInputStream.read(bom) != -1) {
			if (!((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)))
				pushbackInputStream.unread(bom);
		}

		return pushbackInputStream;
	}

	public static <T extends Reader> T skipBOM(T reader) throws IOException {
		reader.mark(1);
		char[] possibleBOM = new char[1];
		reader.read(possibleBOM);

		if (possibleBOM[0] != '\ufeff')
			reader.reset();

		return reader;
	}

	private static List<Character> DELIMS = Arrays.asList('!', '?', ',', '.', ';', ':');
	private static List<Character> IN_DELIMS = Arrays.asList('!', '?', '.', ';');

	public static String extractTitle(String text) {
		String[] words = text.split("\\s");
		StringBuilder buff = new StringBuilder(text.length());
		for (String word : words) {
			if ((buff.length() > 40) && (word.length() > 0) &&
					DELIMS.contains(word.charAt(word.length() - 1))) {
				if (IN_DELIMS.contains(word.charAt(word.length() - 1)))
					buff.append(word);
				else
					buff.append(word.substring(0, word.length() - 1));

				break;
			} else if ((buff.length() > 60) && (word.toLowerCase().startsWith("http"))) {
				break;
			} else{
				buff.append(word);
			}

			buff.append(" ");
		}

		return buff.toString().trim();
	}

	/*
	 * wrapper method for diff_main method from the diff_match_patch class
	 *
	 * It is better to introduce this wrapper method instead of copying that method with
	 * quite many helper methods and variables directly from the diff_match_patch class
	 */
	public static List<Diff> findDifferences(String text1, String text2) {

		TextDiffParser dmp = new TextDiffParser();

		return dmp.diff_main(text1, text2);

	}

	/*
	 * Concatenates the diffs into a new one Diff for the 'deleted' case and a new one for 'added' case
	 * when our original list of diffs comes with many having '1-char' changes
	 */
	public static List<Diff> combineDiffs(List<Diff> originalDiffs) {

		StringBuilder deletedAsWhole = new StringBuilder();
		StringBuilder insertedAsWhole = new StringBuilder();

		LinkedList<Diff> transformedDiffs = new LinkedList<Diff>();

		ListIterator<Diff> diffsIt = originalDiffs.listIterator();

		while (diffsIt.hasNext()) {
			Diff diff = diffsIt.next();

			switch (diff.operation) {

				case INSERT:
					if (Strings.isNullOrEmpty(diff.text)) {
						insertedAsWhole.append(diff.text);
					}
					break;
				case DELETE:
					if (Strings.isNullOrEmpty(diff.text)) {
						deletedAsWhole.append(diff.text);
					}
					break;
				case EQUAL:
					int currentIndex = diffsIt.nextIndex() - 1;

					if (currentIndex == 0) {
						transformedDiffs.add(diff);
					}

					if (currentIndex > 0 && currentIndex < originalDiffs.size()) {
					/*
					 * we need to decide somehow when to add the current 'equal' text to the existing 'deleted'/'added' phrases
					 * and when to create a completely new 'equal'
					 */
						if (diff.text.length() == 1) {
							int previousIndex = currentIndex - 1;
							int nextIndex = currentIndex + 1;
							Diff previousDiff = originalDiffs.get(previousIndex);

							Diff nextDiff = null;
							if (nextIndex < originalDiffs.size()) {
								nextDiff = originalDiffs.get(nextIndex);
							}

							if (previousDiff.operation.equals(Operation.INSERT) && nextDiff != null) {
								deletedAsWhole.append(diff.text);
								insertedAsWhole.append(diff.text);
							}
						} else{
							transformedDiffs.add(new Diff(Operation.DELETE, deletedAsWhole.toString()));
							transformedDiffs.add(new Diff(Operation.INSERT, insertedAsWhole.toString()));
							deletedAsWhole.delete(0, deletedAsWhole.length());
							insertedAsWhole.delete(0, insertedAsWhole.length());
							transformedDiffs.add(new Diff(Operation.EQUAL, diff.text));
						}
					}

					break;
			}
		}

		return transformedDiffs;

	}

	/*
	 * iterates through the list of diffs trying to locate the first occurrence of the diff with the length 1 (being not blank at the same time)
	 */
	public static Boolean isCharModification(List<Diff> originalDiffs) {

		boolean charModificationsAvailable = false;

		for (Diff diff : originalDiffs) {
			if (!diff.operation.equals(Operation.EQUAL) && !Strings.isNullOrEmpty(diff.text) && diff.text.length() == 1) {
				charModificationsAvailable = true;
				break;
			}
		}

		return charModificationsAvailable;
	}

	private static final Pattern timelinePhotosPattern = Pattern.compile("((re|shared): )*timeline photos", Pattern.CASE_INSENSITIVE);

	protected static final String _XML_BODY_PREFIX = "<?xml ";

	/**
	 * Abbreviates a text, adding ellipsis and up to the last whole word with which the maximum length condition is met.
	 *
	 * @param text The text to abbreviate.
	 * @param maxLen The maximum allowed length for the resulting text, including ellipsis. Must be at least 4.
	 * @return Original text if its length is less than the maxLen, null if the text is null, abbreviated text with ellipsis otherwise.
	 * @throws IllegalArgumentException If the maxLen is too small
	 */
	public static String abbreviate(String text, int maxLen) {
		if (text == null || text.length() <= maxLen) {
			return text;
		}

		if (maxLen < 4) {
			throw new IllegalArgumentException("The maximum length must be at least 4");
		}

		//Replace all the \t, \n and \r with whitespaces.
		text = text.replaceAll("[\\t\\n\\r]+", " ");

		int lastSpaceIndex = text.lastIndexOf(" ", maxLen - 3);
		if (lastSpaceIndex != -1) {
			text = text.substring(0, lastSpaceIndex);
		} else{
			text = text.substring(0, maxLen - 3);
		}

		return text + "...";
	}

	private static final Pattern HASHTAG_PATTERN = Pattern.compile("(?<!&)#(\\w+)(\\s+|$)");

	public static Set<String> extractHashtags(CharSequence body) {
		Set<String> hashtags = new LinkedHashSet<String>();
		Matcher hashtagsMatcher = HASHTAG_PATTERN.matcher(body);
		String ht = null;
		while (hashtagsMatcher.find()) {
			ht = hashtagsMatcher.group(1);
			hashtags.add(ht.toLowerCase());
		}

		return hashtags;
	}

	public static boolean isMark(char c) {
		int type = Character.getType(c);
		return Character.COMBINING_SPACING_MARK == type || Character.NON_SPACING_MARK == type;
	}

	public static boolean isNotSpaceSeparated(int codePoint) {
		UnicodeBlock ub = UnicodeBlock.of(codePoint);
		return ub == UCharacter.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == UCharacter.UnicodeBlock.KATAKANA
				|| ub == UCharacter.UnicodeBlock.THAI
				|| ub == UCharacter.UnicodeBlock.HIRAGANA;

	}
}
