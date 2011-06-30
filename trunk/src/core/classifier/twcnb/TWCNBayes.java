package core.classifier.twcnb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import core.classifier.util.FinalDataHolder;
import core.classifier.util.Classifier;
import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.util.Constant;
import core.util.UtilityFuncs;

public class TWCNBayes extends Classifier {
	private File twcnbOutputDir;

	public TWCNBayes() throws Exception {
		super();
		this.twcnbOutputDir = config.getTwcnbFolder();
		deserializeFrom(new File(config.getOutputDir(), Constant.TWCNB_FOLDER));
	}

	public TWCNBayes(FinalDataHolder holder) throws Exception {
		super(holder);
		this.documentCnt = dataHolder.getN();
		this.labelCnt = dataHolder.getLabelCnt();
		this.featureCnt = dataHolder.getFeatureCnt();

		this.twcnbOutputDir = config.getTwcnbFolder();
		this.twcnbOutputDir.mkdirs();
	}

	private static final String dtRow = "dtRow";
	private static final String ctRow = "ctRow";
	private static final String badDocMarkInfoFile = "badDocMark";

	private void serializeBadDocMarkInfo(boolean[] badDocMark) throws IOException {
		File dtRowFile = new File(twcnbOutputDir, badDocMarkInfoFile);
		FileWriter fw = new FileWriter(dtRowFile);
		BufferedWriter bw = new BufferedWriter(fw);

		int cnt = 0;
		for (int i = 0; i < badDocMark.length; i++) {
			if (badDocMark[i]) cnt++;
		}
		bw.write(String.valueOf(cnt));
		bw.newLine();
		for (int i = 0; i < badDocMark.length; i++) {
			if (badDocMark[i]) {
				bw.write(String.valueOf(i));
				bw.newLine();
			}
		}

		bw.flush();
		bw.close();
		fw.close();
	}

	/*
	 * private void deserializeBadDocMarkInfo(boolean[] badDocMark) throws
	 * IOException {
	 * File dtRowFile = new File(twcnbOutputDir, badDocMarkInfoFile);
	 * FileReader fr = new FileReader(dtRowFile);
	 * BufferedReader br = new BufferedReader(fr);
	 * int cnt = Integer.parseInt(br.readLine());
	 * Arrays.fill(badDocMark, false);
	 * for (int i = 0; i < cnt; i++) {
	 * badDocMark[Integer.parseInt(br.readLine())] = true;
	 * }
	 * br.close();
	 * fr.close();
	 * }
	 */

	private void serializeOneRow(String type, int rowId, double[] row, boolean compress) throws IOException {
		File rowFile = new File(twcnbOutputDir, type + rowId);
		FileWriter fw = new FileWriter(rowFile);
		BufferedWriter bw = new BufferedWriter(fw);

		if (compress) {
			int nonZeroCnt = 0;
			for (int i = 0; i < row.length; i++) {
				if (row[i] != 0) nonZeroCnt++;
			}
			bw.write(String.valueOf(nonZeroCnt));
			bw.newLine();
			for (int i = 0; i < row.length; i++) {
				if (row[i] != 0) {
					bw.write(String.valueOf(i));
					bw.newLine();
					bw.write(String.valueOf(row[i]));
					bw.newLine();
				}
			}
		}
		else {
			for (int i = 0; i < row.length; i++) {
				bw.write(String.valueOf(row[i]));
				bw.newLine();
			}
		}

		bw.flush();
		bw.close();
		fw.close();
	}

	private void deserializeOneRow(String type, int rowId, double[] row, boolean compressed) throws IOException {
		File rowFile = new File(twcnbOutputDir, type + rowId);
		FileReader fr = new FileReader(rowFile);
		BufferedReader br = new BufferedReader(fr);

		if (compressed) {
			int nonZeroCnt = Integer.parseInt(br.readLine());
			Arrays.fill(row, 0);
			for (int i = 0; i < nonZeroCnt; i++) {
				int idx = Integer.parseInt(br.readLine());
				row[idx] = Double.parseDouble(br.readLine());
			}
		}
		else {
			for (int i = 0; i < row.length; i++) {
				row[i] = Double.parseDouble(br.readLine());
			}
		}

		br.close();
		fr.close();
	}

	public void train() throws Exception {
		if (dataHolder == null) throw new Exception("dataHolder is null, can not train!");
		System.out.println("Deleting all files in " + twcnbOutputDir);
		UtilityFuncs.deleteDirectory(twcnbOutputDir);

		System.out.print("calculating the document-term matrix...");
		double[] dtWeight = new double[featureCnt]; // one row of the document-term matrix
		double[] ctWeight = new double[featureCnt]; // one row of the class-term matrix

		boolean[] badDocMark = new boolean[documentCnt];
		double[] rowSum = new double[documentCnt];
		double[] columnSum = new double[featureCnt];
		Arrays.fill(badDocMark, false);
		Arrays.fill(rowSum, 0);
		Arrays.fill(columnSum, 0);
		double dtWeightSum = 0;

		for (int docId = 0; docId < documentCnt; docId++) {
			if ((docId & 1023) == 0) System.out.println();
			if ((docId & 255) == 0) System.out.print(docId + ",... ");

			double sum = 0;
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				double dkj = dataHolder.getW_dj_tk(docId, featureId);
				dkj = Math.log1p(dkj);
				dkj = dkj * Math.log(documentCnt / (double) dataHolder.getN_tk(featureId));
				dtWeight[featureId] = dkj;
				sum += dkj * dkj;
			}
			if (sum == 0) {
				badDocMark[docId] = true;
				continue;
			}
			sum = Math.sqrt(sum);
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				dtWeight[featureId] /= sum;
				double tmp = dtWeight[featureId];
				rowSum[docId] += tmp;
				columnSum[featureId] += tmp;
				dtWeightSum += tmp;
			}
			serializeOneRow(dtRow, docId, dtWeight, true);
		}
		System.out.println();
		serializeBadDocMarkInfo(badDocMark);

		//System.out.println();
		//for (int i = 0; i < documentCnt; i++) {
		//	System.out.println(rowSum[i]);
		//}
		//System.out.println();
		//for (int i = 0; i < featureCnt; i++) {
		//	System.out.println(columnSum[i]);
		//}
		//System.out.println();

		System.out.println("calculating the class-term matrix (the classifying parameters)...");
		double alpha_i = 1;
		double alpha = featureCnt;
		for (int labelId = 0; labelId < labelCnt; labelId++) {
			Vector<Integer> l = dataHolder.getDocIdsByLabel(labelId);
			int lsize = l.size();
			double denominator = dtWeightSum + alpha;
			for (int i = 0; i < lsize; i++) {
				if (!badDocMark[l.get(i)]) {
					denominator -= rowSum[l.get(i)];
				}
			}
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				ctWeight[featureId] = columnSum[featureId] + alpha_i;
			}
			for (int i = 0; i < lsize; i++) {
				int docId = l.get(i);
				if (!badDocMark[docId]) {
					deserializeOneRow(dtRow, docId, dtWeight, true);
					for (int featureId = 0; featureId < featureCnt; featureId++) {
						ctWeight[featureId] -= dtWeight[featureId];
					}
				}
			}
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				ctWeight[featureId] = Math.log(ctWeight[featureId] / denominator);
			}

			//for (int featureId = 0; featureId < featureCnt; featureId++) {
			//	double numerator = columnSum[featureId] + alpha_i;
			//	for (int i = 0; i < lsize; i++) {
			//		if (!badDocMark[l.get(i)]) {
			//			numerator -= dtWeight[l.get(i)][featureId];
			//		}
			//	}
			//	ctWeight[labelId][featureId] = Math.log(numerator / denominator);
			//}

			double s = 0;
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				s += ctWeight[featureId];
			}
			for (int featureId = 0; featureId < featureCnt; featureId++) {
				ctWeight[featureId] /= s;
			}
			serializeOneRow(ctRow, labelId, ctWeight, false);
		}
	}

	@Override
	public int classify(String[] titleFeatures, String[] contentFeatures) throws Exception {
		FeatureContainer featureContainer = config.getGenerator().generateFeatureContainer();
		featureContainer.deserializeFrom(new File(config.getStatisticalDir(), Constant.FEATURE_CONTAINER_FILE), null);

		// we regard the features in the title and the content the same
		double mins = Double.MAX_VALUE;
		int ans = -1;
		double[] ctWeight = new double[featureCnt]; // one row of the class-term matrix

		for (int labelId = 0; labelId < labelCnt; labelId++) {
			deserializeOneRow(ctRow, labelId, ctWeight, false);
			double s = 0;
			for (int j = 0; j < titleFeatures.length; j++) {
				int id = featureContainer.getId(titleFeatures[j]);
				if (id != -1) s += ctWeight[id];
			}
			for (int j = 0; j < contentFeatures.length; j++) {
				int id = featureContainer.getId(contentFeatures[j]);
				if (id != -1) s += ctWeight[id];
			}
			if (s < mins) {
				mins = s;
				ans = labelId;
			}
		}
		return ans;
	}

	@Override
	public void serialize() throws Exception {
		// only write meta data because the weightings have been written out in the training phase
		File dtRowFile = new File(twcnbOutputDir, Constant.TWCNB_META_FILE);
		FileWriter fw = new FileWriter(dtRowFile);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write(String.valueOf(this.documentCnt));
		bw.newLine();
		bw.write(String.valueOf(this.labelCnt));
		bw.newLine();
		bw.write(String.valueOf(this.featureCnt));
		bw.newLine();

		bw.flush();
		bw.close();
		fw.close();
	}

	@Override
	public void deserializeFrom(File dir) throws Exception {
		this.dataHolder = null;

		File metaFile = new File(dir, Constant.TWCNB_META_FILE);
		FileReader fr = new FileReader(metaFile);
		BufferedReader br = new BufferedReader(fr);

		this.documentCnt = Integer.parseInt(br.readLine());
		this.labelCnt = Integer.parseInt(br.readLine());
		this.featureCnt = Integer.parseInt(br.readLine());

		br.close();
		fr.close();
	}
}
