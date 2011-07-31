package core.preprocess.corpus.reuters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.CharSequence;

import core.preprocess.util.XmlDocument;
import core.preprocess.corpus.Extractor;
import core.util.Constant;

//Split the Reuters SGML documents into Simple Text files containing: Label, Title, Content
public class ReutersExtractor extends Extractor {
	public ReutersExtractor(File reutersDir, File outputDir, int splitting) {
		super(reutersDir, outputDir, splitting);
	}

	@Override
	protected void extractFiles() throws Exception {
		File[] sgmFiles = this.inputDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".sgm");
			}
		});
		if (sgmFiles != null && sgmFiles.length > 0) {
			for (int i = 0; i < sgmFiles.length; i++) {
				File sgmFile = sgmFiles[i];
				extractFile(sgmFile);
			}
		}
		else {
			//System.err.println("No .sgm files in " + this.reutersDir);
			throw new FileNotFoundException("No .sgm files in " + this.inputDir);
		}
	}

	private Pattern HEAD_ATTRIBUTE_PATTERN = Pattern.compile("<REUTERS TOPICS=\"([^\"]*)\" LEWISSPLIT=\"([^\"]*)\" CGISPLIT=\"([^\"]*)\".*?>");
	private Pattern LABELS_PATTERN = Pattern.compile("<TOPICS>(.*?)</TOPICS>");
	private Pattern LABEL_PATTERN = Pattern.compile("<D>(.*?)</D>");
	private Pattern TITLE_PATTERN = Pattern.compile("<TITLE>(.*?)</TITLE>");
	private Pattern CONTENT_PATTERN = Pattern.compile("<BODY>(.*?)</BODY>");
	
	//the following members are corpus specified bad word patterns
	private static final String[] META_CHARS_SERIALIZATIONS = { "&amp;", "&lt;", "&gt;", "&quot;", "&apos;" };
	private static final String[] META_CHARS = { "&", "<", ">", "\"", "'" };

	private int checkModLewis(String splitTopics, String splitLewis, String splitCgi) {
		if (splitLewis.equals("NOT-USED") || splitTopics.equals("BYPASS")) {
			return Constant.FIRED;
		}
		if (splitLewis.equals("TRAIN")) {
			return Constant.TRAINING;
		}
		else {
			return Constant.TEST;
		}
	}

	private int checkModApte(String splitTopics, String splitLewis, String splitCgi) {
		if (splitTopics.equals("YES")) {
			if (splitLewis.equals("TRAIN")) {
				return Constant.TRAINING;
			}
			else if (splitLewis.equals("TEST")) {
				return Constant.TEST;
			}
			else {
				return Constant.FIRED;
			}
		}
		else {
			return Constant.FIRED;
		}
	}

	private int checkModHayes(String splitTopics, String splitLewis, String splitCgi) {
		if (splitCgi.equals("TRAINING-SET")) {
			return Constant.TRAINING;
		}
		else {
			return Constant.TEST;
		}
	}

	private int checkUsage(CharSequence buffer) {
		String splitTopics = null;
		String splitLewis = null;
		String splitCgi = null;

		Matcher matcher = HEAD_ATTRIBUTE_PATTERN.matcher(buffer);
		if (matcher.find()) {
			splitTopics = matcher.group(1);
			splitLewis = matcher.group(2);
			splitCgi = matcher.group(3);
		}
		// System.out.println("TOPICS = " + splitTopics);
		// System.out.println("LEWISSPLIT = " + splitLewis);
		// System.out.println("CGISPLIT = " + splitCgi);

		if (this.splitting == Constant.MOD_LEWIS) {
			return checkModLewis(splitTopics, splitLewis, splitCgi);
		}
		else if (this.splitting == Constant.MOD_APTE) {
			return checkModApte(splitTopics, splitLewis, splitCgi);
		}
		else {
			return checkModHayes(splitTopics, splitLewis, splitCgi);
		}
	}

	private String[] findLabels(CharSequence buffer) {
		Matcher matcher = LABELS_PATTERN.matcher(buffer);
		Vector<String> vec = new Vector<String>();
		if (matcher.find()) {
			Matcher m = LABEL_PATTERN.matcher(matcher.group(1));
			while (m.find()) {
				vec.add(m.group(1));
			}
		}
		return vec.toArray(new String[0]);
	}

	// use to find title or content
	private String findRelevantPiece(Pattern pattern, CharSequence buffer) {
		Matcher matcher = pattern.matcher(buffer);
		StringBuffer res = new StringBuffer(1024);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				if (matcher.group(i) != null) {
					// System.out.println(matcher.group(i));
					res.append(matcher.group(i)).append(' ');
				}
			}
		}
		return res.toString();
	}

	private void extractFile(File sgmFile) throws Exception {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(sgmFile));
			StringBuffer buffer = new StringBuffer(1024);

			String line = null;
			int docNumber = 0;
			while ((line = reader.readLine()) != null) {
				// when we see a closing reuters tag, flush the file

				if ((line.indexOf("</REUTERS")) == -1) {//if "</REUTERS" occurs, it will always occur at index 0
					buffer.append(line).append(' ');
				}
				else {
					int usage = checkUsage(buffer);
					if (usage != Constant.FIRED) {
						String[] labels = findLabels(buffer);
						String title = findRelevantPiece(TITLE_PATTERN, buffer);
						String content = findRelevantPiece(CONTENT_PATTERN, buffer);

						for (int i = 0; i < META_CHARS_SERIALIZATIONS.length; i++) {
							title = title.replaceAll(META_CHARS_SERIALIZATIONS[i], META_CHARS[i]);
							content = content.replaceAll(META_CHARS_SERIALIZATIONS[i], META_CHARS[i]);
						}
						title = processWords(title);
						content = processWords(content);

						String filename = sgmFile.getName() + "-" + (docNumber++) + ".xml";

						if (usage == Constant.TRAINING) {
							XmlDocument.createDocument(this.trainingDir, filename, labels, title, content);
						}
						else {
							XmlDocument.createDocument(this.testDir, filename, labels, title, content);
						}
					}
					buffer.setLength(0);
				}
			}
			reader.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}