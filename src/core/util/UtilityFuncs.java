package core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class UtilityFuncs {
	public static void deleteDirectory(File dir) throws IOException {
		if ((dir == null) || !dir.isDirectory()) {
			throw new IllegalArgumentException("Argument " + dir + " is not a directory. ");
		}
		File[] entries = dir.listFiles();

		int sz = entries.length;
		for (int i = 0; i < sz; i++) {
			if (entries[i].isDirectory()) {
				deleteDirectory(entries[i]);
			}
			entries[i].delete();
		}
	}

	// for twcnb
	public static void serializeOneRow(File twcnbOutputDir, String prefix, int rowId, double[] row, boolean compress) throws IOException {
		File rowFile = new File(twcnbOutputDir, prefix + rowId);
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

	public static void deserializeOneRow(File twcnbOutputDir, String prefix, int rowId, double[] row, boolean compressed)
			throws IOException {
		File rowFile = new File(twcnbOutputDir, prefix + rowId);
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
}
