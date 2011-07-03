package test.preprocess.analyzation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import core.preprocess.analyzation.DataHolder;

public abstract class DataHolderTest {
	protected int docCnt;
	protected int featureCnt;
	protected int labelCnt;
	protected String[] features;
	protected String[] labels;
	protected String separatedLine;
	public DataHolder holder;//make it public for FinalDataHolderTest

	public DataHolderTest() {
		this.docCnt = 8;
		this.featureCnt = 5;
		this.labelCnt = 3;
		this.features = new String[] { "B", "E", "C", "A", "D" }; //their IDs are 0,1,2,3,4
		this.labels = new String[] { "c2", "c3", "c1" }; //their IDs are 0,1,2
		this.separatedLine = "---------------------------------------------";
	}

	protected static void readIntoSb(File file, StringBuffer sb) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		while (true) {
			String text = br.readLine().trim();
			if (text == null) break;
			sb.append(text);
			sb.append("\r\n");
		}
		br.close();
		fr.close();
	}

	protected static void test(DataHolderTest dhTest, StringBuffer sb) throws Exception {
		sb.append("\r\n******************************** basic information ********************************");
		sb.append("\r\n");
		dhTest.testBasicInformation(sb);

		sb.append("\r\n******************************** document counting ********************************");
		sb.append("\r\n");
		dhTest.testDocumentCounting(sb);

		sb.append("\r\n********************************** word counting **********************************");
		sb.append("\r\n");
		dhTest.testWordCounting(sb);

		sb.append("\r\n************************** single feature word counting ***************************");
		sb.append("\r\n");
		dhTest.testSingleFeatureWordCounting(sb);

		sb.append("\r\n********************************* feature counting ********************************");
		sb.append("\r\n");
		dhTest.testFeatureCounting(sb);

		sb.append("\r\n********************************** label counting *********************************");
		sb.append("\r\n");
		dhTest.testLabelCounting(sb);
	}

	/******************************** basic information ********************************/
	public void testBasicInformation(StringBuffer sb) {
		sb.append("getDocLabels:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			Vector<Integer> l = holder.getDocIdsByLabel(i);
			sb.append("labelId = " + i + " (" + holder.getLabel(i) + "): ");
			for (int j = 0; j < l.size(); j++) {
				sb.append("doc" + l.get(j) + ",");
			}
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getFeatureId:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			int id = holder.getFeatureId(features[i]);
			sb.append(features[i] + ": " + id);
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getFeature:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			String f = holder.getFeature(i);
			sb.append(f + ": " + i);
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getLabelId:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			int id = holder.getLabelId(labels[i]);
			sb.append(labels[i] + ": " + id);
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getLabel:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			String l = holder.getLabel(i);
			sb.append(l + ": " + i);
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");
	}

	/******************************** document counting ********************************/
	public void testDocumentCounting(StringBuffer sb) {
		sb.append("getN:");
		sb.append("\r\n");
		sb.append(holder.getN());
		sb.append("\r\n");
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getN_ci(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getN_ci(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_not_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getN_not_ci(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_not_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getN_not_ci(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getN_tk(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getN_tk(features[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getN_exclude_tk(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getN_exclude_tk(features[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_ci_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_ci_tk(labels[i], features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_not_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_not_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_tk(labels[i], features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_ci_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_ci_exclude_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_ci_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_ci_exclude_tk(labels[i], features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_not_ci_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_exclude_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getN_not_ci_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_exclude_tk(labels[i], features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");
	}

	/********************************** word counting **********************************/
	public void testWordCounting(StringBuffer sb) {
		sb.append("getW:");
		sb.append("\r\n");
		sb.append(holder.getW());
		sb.append("\r\n");
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getW_ci(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getW_ci(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getW_not_ci(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getW_not_ci(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_dj:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			sb.append("doc " + i + ": " + holder.getW_dj(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_dj:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			sb.append("doc " + i + ": " + holder.getW_not_dj(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");
	}

	/*************************** single feature word counting ***************************/
	public void testSingleFeatureWordCounting(StringBuffer sb) {
		sb.append("getW_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getW_tk(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getW_tk(features[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getW_ci_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getW_ci_tk(labels[i], features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getW_not_ci_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_ci_tk:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append(labels[i] + ", " + features[j] + ": " + holder.getW_not_ci_tk(labels[i], features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_dj_tk:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append("doc " + i + ", " + features[j] + ": " + holder.getW_dj_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_dj_tk:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append("doc " + i + ", " + features[j] + ": " + holder.getW_dj_tk(i, features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_dj_tk:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append("doc " + i + ", " + features[j] + ": " + holder.getW_not_dj_tk(i, j));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getW_not_dj_tk:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				sb.append("doc " + i + ", " + features[j] + ": " + holder.getW_not_dj_tk(i, features[j]));
				sb.append("\r\n");
			}
		}
		sb.append(separatedLine);
		sb.append("\r\n");
	}

	/********************************* feature counting *********************************/
	public void testFeatureCounting(StringBuffer sb) throws Exception {
		sb.append("getV:");
		sb.append("\r\n");
		sb.append(holder.getV());
		sb.append("\r\n");
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_ci(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_ci(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_ci_exclude:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_ci_exclude(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_ci_exclude:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_ci_exclude(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_not_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_not_ci(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_not_ci:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_not_ci(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_not_ci_exclude:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_not_ci_exclude(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_not_ci_exclude:");
		sb.append("\r\n");
		for (int i = 0; i < labelCnt; i++) {
			sb.append(labels[i] + ": " + holder.getV_not_ci_exclude(labels[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_dj:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			sb.append("doc " + i + ": " + holder.getV_dj(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_dj_exclude:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			sb.append("doc " + i + ": " + holder.getV_dj_exclude(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_not_dj:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			sb.append("doc " + i + ": " + holder.getV_not_dj(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getV_not_dj_exclude:");
		sb.append("\r\n");
		for (int i = 0; i < docCnt; i++) {
			sb.append("doc " + i + ": " + holder.getV_not_dj_exclude(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");
	}

	/********************************** label counting **********************************/
	public void testLabelCounting(StringBuffer sb) throws Exception {
		sb.append("getM:");
		sb.append("\r\n");
		sb.append(holder.getM());
		sb.append("\r\n");
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getM_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getM_tk(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getM_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getM_tk(features[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getM_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getM_exclude_tk(i));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");

		sb.append("getM_exclude_tk:");
		sb.append("\r\n");
		for (int i = 0; i < featureCnt; i++) {
			sb.append(features[i] + ": " + holder.getM_exclude_tk(features[i]));
			sb.append("\r\n");
		}
		sb.append(separatedLine);
		sb.append("\r\n");
	}
}
