package test.preprocess.util;

import core.preprocess.util.DataHolder;

public abstract class DataHolderTest {
	protected int docCnt;
	protected int featureCnt;
	protected int labelCnt;
	protected String[] features;
	protected String[] labels;
	protected String separatedLine;
	public DataHolder holder;//make it public for FinalDataHolderTest

	/******************************** basic information ********************************/
	public void testBasicInformation() {
		System.out.println("getDocLabels:");
		for (int i = 0; i < docCnt; i++) {
			String[] l = holder.getDocLabels(i);
			System.out.print("docId = " + i + ": ");
			for (int j = 0; j < l.length; j++) {
				System.out.print(l[j] + ",");
			}
			System.out.println();
		}
		System.out.println(separatedLine);

		System.out.println("getFeatureId:");
		for (int i = 0; i < featureCnt; i++) {
			int id = holder.getFeatureId(features[i]);
			System.out.println(features[i] + ": " + id);
		}
		System.out.println(separatedLine);

		System.out.println("getFeature:");
		for (int i = 0; i < featureCnt; i++) {
			String f = holder.getFeature(i);
			System.out.println(f + ": " + i);
		}
		System.out.println(separatedLine);

		System.out.println("getLabelId:");
		for (int i = 0; i < labelCnt; i++) {
			int id = holder.getLabelId(labels[i]);
			System.out.println(labels[i] + ": " + id);
		}
		System.out.println(separatedLine);

		System.out.println("getLabel:");
		for (int i = 0; i < labelCnt; i++) {
			String l = holder.getLabel(i);
			System.out.println(l + ": " + i);
		}
		System.out.println(separatedLine);
	}

	/******************************** document counting ********************************/
	public void testDocumentCounting() {
		System.out.println("getN:");
		System.out.println(holder.getN());
		System.out.println(separatedLine);

		System.out.println("getN_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getN_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getN_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getN_not_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getN_not_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getN_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getN_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getN_exclude_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getN_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getN_exclude_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_ci_exclude_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_ci_exclude_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_exclude_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getN_not_ci_exclude_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getN_not_ci_exclude_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);
	}

	/********************************** word counting **********************************/
	public void testWordCounting() {
		System.out.println("getW:");
		System.out.println(holder.getW());
		System.out.println(separatedLine);

		System.out.println("getW_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getW_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getW_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getW_not_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getW_not_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getW_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + holder.getW_dj(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + holder.getW_not_dj(i));
		}
		System.out.println(separatedLine);
	}

	/*************************** single feature word counting ***************************/
	public void testSingleFeatureWordCounting() {
		System.out.println("getW_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getW_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getW_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getW_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getW_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getW_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getW_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getW_not_ci_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_ci_tk:");
		for (int i = 0; i < labelCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println(labels[i] + ", " + features[j] + ": " + holder.getW_not_ci_tk(labels[i], features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + holder.getW_dj_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + holder.getW_dj_tk(i, features[j]));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + holder.getW_not_dj_tk(i, j));
			}
		}
		System.out.println(separatedLine);

		System.out.println("getW_not_dj_tk:");
		for (int i = 0; i < docCnt; i++) {
			for (int j = 0; j < featureCnt; j++) {
				System.out.println("doc " + i + ", " + features[j] + ": " + holder.getW_not_dj_tk(i, features[j]));
			}
		}
		System.out.println(separatedLine);
	}

	/********************************* feature counting *********************************/
	public void testFeatureCounting() throws Exception {
		System.out.println("getV:");
		System.out.println(holder.getV());
		System.out.println(separatedLine);

		System.out.println("getV_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_ci_exclude(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_ci_exclude(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_not_ci(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_not_ci(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_not_ci_exclude(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_ci_exclude:");
		for (int i = 0; i < labelCnt; i++) {
			System.out.println(labels[i] + ": " + holder.getV_not_ci_exclude(labels[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getV_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + holder.getV_dj(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_dj_exclude:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + holder.getV_dj_exclude(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_dj:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + holder.getV_not_dj(i));
		}
		System.out.println(separatedLine);

		System.out.println("getV_not_dj_exclude:");
		for (int i = 0; i < docCnt; i++) {
			System.out.println("doc " + i + ": " + holder.getV_not_dj_exclude(i));
		}
		System.out.println(separatedLine);
	}

	/********************************** label counting **********************************/
	public void testLabelCounting() throws Exception {
		System.out.println("getM:");
		System.out.println(holder.getM());
		System.out.println(separatedLine);

		System.out.println("getM_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getM_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getM_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getM_tk(features[i]));
		}
		System.out.println(separatedLine);

		System.out.println("getM_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getM_exclude_tk(i));
		}
		System.out.println(separatedLine);

		System.out.println("getM_exclude_tk:");
		for (int i = 0; i < featureCnt; i++) {
			System.out.println(features[i] + ": " + holder.getM_exclude_tk(features[i]));
		}
		System.out.println(separatedLine);
	}
}
