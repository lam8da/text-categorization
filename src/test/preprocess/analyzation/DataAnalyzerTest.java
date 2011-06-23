package test.preprocess.analyzation;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import core.preprocess.analyzation.DataAnalyzer;
import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.generator.TrieGenerator;
import core.preprocess.util.XmlDocument;

public class DataAnalyzerTest extends DataHolderTest {
	//the content of the being tested documents is as follows:
	//  --------------------------------------
	//  |   labels   |  docId  |   features  |
	//  --------------------------------------
	//  |     c1     |    3    | A B   D E E |
	//  |     c2     |    0    |   B     E E |
	//  |    c1,c2   |    4    |     C   E E |
	//  |     c2     |    7    |       D E E |
	//  |     c3     |    1    |     C   E E |
	//  |    c1,c3   |    2    |     C   E E |
	//  |    c2,c3   |    5    |       D E E |
	//  |  c1,c2,c3  |    6    |       D E E |
	//  --------------------------------------

	public int fileCnt = 0;

	public DataAnalyzerTest(String inputPath, ContainerGenerator generator) throws Exception {
		super();

		File inputDir = new File(inputPath);
		File[] xmlFiles = inputDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		//System.out.println("file cnt: " + xmlFiles.length);
		fileCnt = xmlFiles.length;

		Arrays.sort(xmlFiles);
		XmlDocument xml = new XmlDocument();
		this.holder = new DataAnalyzer(generator);

		for (int i = 0; i < xmlFiles.length; i++) {
			//System.out.println(xmlFiles[i].getName());
			xml.parseDocument(xmlFiles[i]);
			((DataAnalyzer) holder).addDocument(xml.getLabels(), xml.getTitleFeatures(), xml.getContentFeatures());
		}

		if (this.docCnt != xmlFiles.length) {
			throw new Exception("invalid xmlFiles.length!");
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}
		ContainerGenerator generator = new TrieGenerator();

		File stdout = new File("res/test/DataAnalyzerTest/standard output.txt");
		StringBuffer stdSb = new StringBuffer();
		readIntoSb(stdout, stdSb);

		File stdoutE = new File("res/test/DataAnalyzerTest/standard output (eliminated).txt");
		StringBuffer stdSbE = new StringBuffer();
		readIntoSb(stdoutE, stdSbE);

		StringBuffer holderSb = new StringBuffer();
		DataAnalyzerTest daTest = new DataAnalyzerTest("res/test/DataAnalyzerTest", generator);
		holderSb.append("file cnt: " + daTest.fileCnt).append("\r\n");
		DataHolderTest.test(daTest, holderSb);

		if (stdSb.toString().contentEquals(holderSb) == false) {
			System.out.println("Error in holder stringbuffer!");
			System.out.println(stdSb);
			System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			System.out.println(holderSb);
			return;
		}
		else System.out.println("holder stringbuffer is ok!");

		//------------------------------------------------------------------------------------------//

		File dir = new File(args[0]);
		dir.mkdirs();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}

		((DataAnalyzer) daTest.holder).serialize(dir);

		DataAnalyzer ana = DataAnalyzer.deserialize(new TrieGenerator(), dir, new int[] { 1 }, true);
		daTest.holder = ana;
		daTest.featureCnt = 4;
		daTest.features = new String[] { "A", "B", "C", "D" }; //their IDs are 0,1,2,3
		daTest.labels = new String[] { "c2", "c3", "c1" }; //their IDs are 0,1,2
		daTest.separatedLine = "---------------------------------------------";

		StringBuffer analyzerSb = new StringBuffer();
		analyzerSb.append("file cnt: " + daTest.fileCnt).append("\r\n");
		DataHolderTest.test(daTest, analyzerSb);

		if (stdSbE.toString().contentEquals(analyzerSb) == false) {
			System.out.println("Error in analyzer stringbuffer!");
			System.out.println(stdSbE);
			System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			System.out.println(analyzerSb);
			return;
		}
		else System.out.println("analyzer stringbuffer is ok!");
	}
}
