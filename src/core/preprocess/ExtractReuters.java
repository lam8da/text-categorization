package core.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Split the Reuters SGML documents into Simple Text files containing: Title, Date, Dateline, Body
public class ExtractReuters {
	private File reutersDir;
	private File outputDir;
	private static final String LINE_SEPARATOR = "\r\n";

	public ExtractReuters(File reutersDir, File outputDir) {
		this.reutersDir = reutersDir;
		this.outputDir = outputDir;
		System.out.println("Deleting all files in " + outputDir);
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	public void extract() {
		File[] sgmFiles = reutersDir.listFiles(new FileFilter() {
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
			System.err.println("No .sgm files in " + reutersDir);
		}
	}

	Pattern EXTRACTION_PATTERN = Pattern
			.compile("<TITLE>(.*?)</TITLE>|<DATE>(.*?)</DATE>|<BODY>(.*?)</BODY>");

	private static String[] META_CHARS = { "&", "<", ">", "\"", "'" };

	private static String[] META_CHARS_SERIALIZATIONS = { "&amp;", "&lt;",
			"&gt;", "&quot;", "&apos;" };

	protected void extractFile(File sgmFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(sgmFile));

			StringBuffer buffer = new StringBuffer(1024);
			StringBuffer outBuffer = new StringBuffer(1024);

			String line = null;
			int index = -1;
			int docNumber = 0;
			while ((line = reader.readLine()) != null) {
				// when we see a closing reuters tag, flush the file

				if ((index = line.indexOf("</REUTERS")) == -1) {	//"</REUTERS" will always occur at index 0
					// Replace the SGM escape sequences

					buffer.append(line).append(' ');// accumulate the strings
													// for now, then apply
													// regular expression to get
													// the pieces,
				} else {
					// Extract the relevant pieces and write to a file in the
					// output dir
					Matcher matcher = EXTRACTION_PATTERN.matcher(buffer);
					while (matcher.find()) {
						for (int i = 1; i <= matcher.groupCount(); i++) {
							if (matcher.group(i) != null) {
								outBuffer.append(matcher.group(i));
							}
						}
						outBuffer.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
					}
					String out = outBuffer.toString();
					for (int i = 0; i < META_CHARS_SERIALIZATIONS.length; i++) {
						out = out.replaceAll(META_CHARS_SERIALIZATIONS[i],
								META_CHARS[i]);
					}
					File outFile = new File(outputDir, sgmFile.getName() + "-"
							+ (docNumber++) + ".txt");
					// System.out.println("Writing " + outFile);
					FileWriter writer = new FileWriter(outFile);
					writer.write(out);
					writer.close();
					outBuffer.setLength(0);
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