package core.classifier.twcnb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import core.classifier.util.FinalDataHolder;
import core.classifier.util.Trainer;
import core.util.Constant;
import core.util.UtilityFuncs;

public class TWCNBayesTrainer extends Trainer {
	private static final String badDocMarkInfoFile = "badDocMark";
	private File twcnbOutputDir;

	public TWCNBayesTrainer(FinalDataHolder holder) throws Exception {
		super(holder);
		this.twcnbOutputDir = config.getTwcnbFolder();
		this.twcnbOutputDir.mkdirs();
	}

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
			UtilityFuncs.serializeOneRow(twcnbOutputDir, Constant.TWCNB_DT_ROW_FILE_PREFIX, docId, dtWeight, true);
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
					UtilityFuncs.deserializeOneRow(twcnbOutputDir, Constant.TWCNB_DT_ROW_FILE_PREFIX, docId, dtWeight, true);
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
			UtilityFuncs.serializeOneRow(twcnbOutputDir, Constant.TWCNB_CT_ROW_FILE_PREFIX, labelId, ctWeight, false);
		}
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
}
