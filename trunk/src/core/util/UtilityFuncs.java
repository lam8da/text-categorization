package core.util;

import java.io.File;
import java.io.IOException;

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
}
