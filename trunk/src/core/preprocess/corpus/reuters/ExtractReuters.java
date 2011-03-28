package core.preprocess.corpus.reuters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.CharSequence;

import core.preprocess.util.Constant;
import core.preprocess.util.XmlFormatter;

//Split the Reuters SGML documents into Simple Text files containing: Label, Title, Content
public class ExtractReuters {
	private static final int TRAINING = 0;
	private static final int TEST = 1;
	private static final int FIRED = 2;

	private int splitting;
	private File reutersDir;
	private File trainingDir;
	private File testDir;

	public ExtractReuters(File reutersDir, File outputDir, int splitting) {
		this.splitting = splitting;
		this.reutersDir = reutersDir;
		this.trainingDir = new File(outputDir, Constant.TRAINING_FOLDER);
		this.testDir = new File(outputDir, Constant.TEST_FOLDER);

		System.out.println("Deleting all files in " + outputDir);
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		this.trainingDir.mkdirs();
		this.testDir.mkdirs();
	}

	public void extract() throws Exception {
		File[] sgmFiles = this.reutersDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".sgm");
			}
		});
		if (sgmFiles != null && sgmFiles.length > 0) {
			for (int i = 0; i < sgmFiles.length; i++) {
				File sgmFile = sgmFiles[i];
				extractFile(sgmFile);
			}
		} else {
			System.err.println("No .sgm files in " + this.reutersDir);
		}
	}

	private Pattern HEAD_ATTRIBUTE_PATTERN = Pattern
			.compile("<REUTERS TOPICS=\"([^\"]*)\" LEWISSPLIT=\"([^\"]*)\" CGISPLIT=\"([^\"]*)\".*?>");
	private Pattern LABELS_PATTERN = Pattern.compile("<TOPICS>(.*?)</TOPICS>");
	private Pattern LABEL_PATTERN = Pattern.compile("<D>(.*?)</D>");
	private Pattern TITLE_PATTERN = Pattern.compile("<TITLE>(.*?)</TITLE>");
	private Pattern CONTENT_PATTERN = Pattern.compile("<BODY>(.*?)</BODY>");

	private static String[] META_CHARS = { "&", "<", ">", "\"", "'" };
	private static String[] META_CHARS_SERIALIZATIONS = { "&amp;", "&lt;",
			"&gt;", "&quot;", "&apos;" };

	private int checkModLewis(String splitTopics, String splitLewis,
			String splitCgi) {
		if (splitLewis.equals("NOT-USED") || splitTopics.equals("BYPASS")) {
			return FIRED;
		}
		if (splitLewis.equals("TRAIN")) {
			return TRAINING;
		} else {
			return TEST;
		}
	}

	private int checkModApte(String splitTopics, String splitLewis,
			String splitCgi) {
		if (splitTopics.equals("YES")) {
			if (splitLewis.equals("TRAIN")) {
				return TRAINING;
			} else if (splitLewis.equals("TEST")) {
				return TEST;
			} else {
				return FIRED;
			}
		} else {
			return FIRED;
		}
	}

	private int checkModHayes(String splitTopics, String splitLewis,
			String splitCgi) {
		if (splitCgi.equals("TRAINING-SET")) {
			return TRAINING;
		} else {
			return TEST;
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

		if (this.splitting == Constant.MODLEWIS) {
			return checkModLewis(splitTopics, splitLewis, splitCgi);
		} else if (this.splitting == Constant.MODAPTE) {
			return checkModApte(splitTopics, splitLewis, splitCgi);
		} else {
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

				if ((line.indexOf("</REUTERS")) == -1) {// if "</REUTERS"
														// occurs, it will
														// always occur at index
														// 0
					buffer.append(line).append(' ');
				} else {
					int usage = checkUsage(buffer);
					if (usage != FIRED) {
						String[] labels = findLabels(buffer);
						String title = findRelevantPiece(TITLE_PATTERN, buffer);
						String content = findRelevantPiece(CONTENT_PATTERN,
								buffer);

						for (int i = 0; i < META_CHARS_SERIALIZATIONS.length; i++) {
							for (int j = 0; j < labels.length; j++) {
								labels[j] = labels[j].replaceAll(
										META_CHARS_SERIALIZATIONS[i],
										META_CHARS[i]);
							}
							title = title
									.replaceAll(META_CHARS_SERIALIZATIONS[i],
											META_CHARS[i]);
							content = content
									.replaceAll(META_CHARS_SERIALIZATIONS[i],
											META_CHARS[i]);
						}

						// System.out.print("labels = ");
						// for (int i = 0; i < labels.length; i++) {
						// System.out.print(labels[i] + ", ");
						// }
						// System.out.println();
						// System.out.println("title = " + title);
						// System.out.println("usage = "
						// + (usage == TRAINING ? "training" : "test"));
						// System.out.println();

						String filename = sgmFile.getName() + "-"
								+ (docNumber++) + ".txt";
						// if (docNumber > 10) {
						// break;
						// }

						if (usage == TRAINING) {
							XmlFormatter.createXml(this.trainingDir, filename,
									labels, title, content);
						} else {
							XmlFormatter.createXml(this.testDir, filename,
									labels, title, content);
						}
					}
					buffer.setLength(0);
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void printUsage() {
		System.err
				.println("Usage: java -cp <...> org.apache.lucene.benchmark.utils.ExtractReuters <Path to Reuters SGM files> <Output Path>");
	}
}