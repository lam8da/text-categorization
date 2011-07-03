package core.evaluation.twcnb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Vector;

import core.evaluation.util.Classifier;
import core.util.Constant;
import core.util.UtilityFuncs;

public class TWCNBayesClassifier extends Classifier {
	private File twcnbOutputDir;
	private double[] ctWeight; // one row of the class-term matrix

	public TWCNBayesClassifier() throws Exception {
		super();
		this.twcnbOutputDir = config.getTwcnbFolder();
		deserializeFrom(new File(config.getOutputDir(), Constant.TWCNB_FOLDER));
		this.ctWeight = new double[featureCnt]; // one row of the class-term matrix
	}

	private double calculateScore(String[] titleFeatures, String[] contentFeatures) {
		double s = 0;
		for (int j = 0; j < titleFeatures.length; j++) {
			int id = featureContainer.getId(titleFeatures[j]);
			if (id != -1) s += ctWeight[id];
		}
		for (int j = 0; j < contentFeatures.length; j++) {
			int id = featureContainer.getId(contentFeatures[j]);
			if (id != -1) s += ctWeight[id];
		}
		return s;
	}

	@Override
	public Vector<Integer> classify(String[] titleFeatures, String[] contentFeatures) throws Exception {
		// we regard the features in the title and the content the same
		double mins = Double.MAX_VALUE;
		int ans = -1;

		for (int labelId = 0; labelId < labelCnt; labelId++) {
			UtilityFuncs.deserializeOneRow(twcnbOutputDir, Constant.TWCNB_CT_ROW_FILE_PREFIX, labelId, ctWeight, false);
			double s = calculateScore(titleFeatures, contentFeatures);
			if (s < mins) {
				mins = s;
				ans = labelId;
			}
		}
		Vector<Integer> res = new Vector<Integer>(1);
		res.add(ans);
		return res;
	}

	@Override
	public Vector<int[]> classify(Vector<String[]> titleFeatures, Vector<String[]> contentFeatures) throws Exception {
		if (titleFeatures.size() != contentFeatures.size()) throw new Exception("the size of title and content mismatch!");
		int testDocCnt = contentFeatures.size();
		int[] ans = new int[testDocCnt];
		double[] mins = new double[testDocCnt];
		Arrays.fill(ans, -1);
		Arrays.fill(mins, Double.MAX_VALUE);

		System.out.print("processing label: ");
		for (int labelId = 0; labelId < labelCnt; labelId++) {
			if ((labelId & 15) == 0) System.out.println();
			System.out.print(labelId + ", ");
			UtilityFuncs.deserializeOneRow(twcnbOutputDir, Constant.TWCNB_CT_ROW_FILE_PREFIX, labelId, ctWeight, false);
			for (int i = 0; i < testDocCnt; i++) {
				double s = calculateScore(titleFeatures.get(i), contentFeatures.get(i));
				if (s < mins[i]) {
					mins[i] = s;
					ans[i] = labelId;
				}
			}
		}
		System.out.println();

		Vector<int[]> res = new Vector<int[]>(testDocCnt);
		for (int i = 0; i < testDocCnt; i++) {
			int[] ansi = new int[1];
			ansi[0] = ans[i];
			res.add(ansi);
		}
		return res;
	}

	@Override
	public void deserializeFrom(File dir) throws Exception {
		File metaFile = new File(dir, Constant.TWCNB_META_FILE);
		System.out.println("deserializing meta data (twcnb) from: " + metaFile.getPath());

		FileReader fr = new FileReader(metaFile);
		BufferedReader br = new BufferedReader(fr);

		this.documentCnt = Integer.parseInt(br.readLine().trim());
		this.labelCnt = Integer.parseInt(br.readLine().trim());
		this.featureCnt = Integer.parseInt(br.readLine().trim());

		br.close();
		fr.close();
	}
}
