package test.preprocess.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.XmlDocument;

public class DataAnalyzerTest {
	private int docCnt;
	private int featureCnt;
	private int labelCnt;
	private String[] features;
	private String[] labels;
	private String separatedLine;
	private DataAnalyzer analyzer;

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

	public DataAnalyzerTest() throws Exception {
		File inputDir = new File("res/test/DataAnalyzerTest");
		File[] xmlFiles = inputDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		System.out.println("file cnt: " + xmlFiles.length);

		Arrays.sort(xmlFiles);
		XmlDocument xml = new XmlDocument();
		this.analyzer = new DataAnalyzer();

		for (int i = 0; i < xmlFiles.length; i++) {
			//System.out.println(xmlFiles[i].getName());
			xml.parseDocument(xmlFiles[i]);
			analyzer.addDocument(xml.getLabels(), xml.getTitleFeatures(), xml.getContentFeatures());
		}
		this.analyzer.accomplishAdding();

		this.docCnt = xmlFiles.length;
		this.featureCnt = 5;
		this.labelCnt = 3;
		this.features = new String[] { "B", "E", "C", "A", "D" }; //their IDs are 0,1,2,3,4
		this.labels = new String[] { "c2", "c3", "c1" }; //their IDs are 0,1,2
		this.separatedLine = "---------------------------------------------";
	}

	public static void main(String[] args) throws Exception {
		DataAnalyzerTest test = new DataAnalyzerTest();

		System.out.println("\r\n******************************** basic information ********************************");
		test.testBasicInformation();

		System.out.println("\r\n******************************** document counting ********************************");
		test.testDocumentCounting();

		System.out.println("\r\n********************************** word counting **********************************");
		test.testWordCounting();

		System.out.println("\r\n************************** single feature word counting ***************************");
		test.testSingleFeatureWordCounting();

		System.out.println("\r\n********************************* feature counting ********************************");
		test.testFeatureCounting();

		System.out.println("\r\n********************************** label counting *********************************");
		test.testLabelCounting();
	}

	/******************************** basic information ********************************/
	private void testBasicInformation() {
		System.out.println("getDocLabels:");
		for (int i = 0; i < docCnt; i++) {
			String[] l = analyzer.getDocLabels(i);
			System.out.print("docId = " + i + ": ");
			for (int j = 0; j < l.length; j++) {
				System.out.print(l[j] + ",");
			}
			System.out.println();
		}
		System.out.println(separatedLine);

		System.out.println("getFeatureId:");
		for (int i = 0; i < featureCnt; i++) {
			int id = analyzer.getFeatureId(features[i]);
			System.out.println(features[i] + ": " + id);
		}
		System.out.println(separatedLine);

		System.out.println("getFeature:");
		for (int i = 0; i < featureCnt; i++) {
			String f = analyzer.getFeature(i);
			System.out.println(f + ": " + i);
		}
		System.out.println(separatedLine);

		System.out.println("getLabelId:");
		for (int i = 0; i < labelCnt; i++) {
			int id = analyzer.getLabelId(labels[i]);
			System.out.println(labels[i] + ": " + id);
		}
		System.out.println(separatedLine);

		System.out.println("getLabel:");
		for (int i = 0; i < labelCnt; i++) {
			String l = analyzer.getLabel(i);
			System.out.println(l + ": " + i);
		}
		System.out.println(separatedLine);
	}

	/******************************** document counting ********************************/
	private void testDocumentCounting() {
		System.out.println("getN:");
		System.out.println(analyzer.getN());
		System.out.println(separatedLine);

		System.out.println("getN_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getN_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getN_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getN_not_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getN_not_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getN_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getN_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getN_exclude_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getN_exclude_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_not_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_not_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_ci_exclude_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_ci_exclude_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_not_ci_exclude_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getN_not_ci_exclude_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);
	}

	/********************************** word counting **********************************/
	private void testWordCounting() {
		System.out.println("getW:");
		System.out.println(analyzer.getW());
		System.out.println(separatedLine);

		System.out.println("getW_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getW_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getW_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getW_not_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getW_not_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getW_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + analyzer.getW_dj(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + analyzer.getW_not_dj(i));
		}
		System.out.println(separatedLine);
	}

	/*************************** single feature word counting ***************************/
	private void testSingleFeatureWordCounting() {
		System.out.println("getW_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getW_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getW_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getW_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getW_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getW_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getW_not_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + analyzer.getW_not_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + analyzer.getW_dj_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + analyzer.getW_dj_tk(i, features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + analyzer.getW_not_dj_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + analyzer.getW_not_dj_tk(i, features[j]));
			}
		}
		System.out.println(separatedLine);
	}

	/********************************* feature counting *********************************/
	private void testFeatureCounting() throws Exception {
		System.out.println("getV:");
		System.out.println(analyzer.getV());
		System.out.println(separatedLine);

		System.out.println("getV_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_ci_exclude(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_ci_exclude(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_not_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_not_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_not_ci_exclude(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + analyzer.getV_not_ci_exclude(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + analyzer.getV_dj(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_dj_exclude:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + analyzer.getV_dj_exclude(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + analyzer.getV_not_dj(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_dj_exclude:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + analyzer.getV_not_dj_exclude(i));
		}
		System.out.println(separatedLine);
	}

	/********************************** label counting **********************************/
	private void testLabelCounting() throws Exception {
		System.out.println("getM:");
		System.out.println(analyzer.getM());
		System.out.println(separatedLine);

		System.out.println("getM_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getM_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getM_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getM_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getM_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getM_exclude_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getM_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + analyzer.getM_exclude_tk(features[i]));
		}
		System.out.println(separatedLine);
	}
}
