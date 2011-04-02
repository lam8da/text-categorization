package core.preprocess.corpus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import core.preprocess.extraction.Stemmer;
import core.preprocess.selection.Stopper;
import core.preprocess.util.Constant;

public abstract class Extractor {
	protected int splitting;
	protected File inputDir;
	protected File outputDir;
	protected File trainingDir;
	protected File testDir;
	protected Stemmer stemmer;
	protected Stopper stopper;
	protected boolean toLower;

	public Extractor(File inputDir, File outputDir, int splitting) {
		this.splitting = splitting;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.trainingDir = new File(outputDir, Constant.TRAINING_FOLDER);
		this.testDir = new File(outputDir, Constant.TEST_FOLDER);

		System.out.println("Deleting all files in " + outputDir);
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}

		this.trainingDir.mkdirs();
		this.testDir.mkdirs();
		this.stemmer = null;
		this.stopper = null;
	}

	public void writeMetadata() throws IOException {
		File metaFile = new File(this.outputDir, Constant.EXTRACTION_METADATA_FILENAME);
		FileWriter writer = new FileWriter(metaFile);

		writer.write("stopper:" + Constant.WORD_SEPARATOR);
		if (this.stopper == null) {
			writer.write(Constant.NO);
		}
		else writer.write(Constant.YES);
		writer.write(Constant.LINE_SEPARATOR);

		writer.write("stemmer:" + Constant.WORD_SEPARATOR);
		if (this.stemmer == null) {
			writer.write(Constant.NO);
		}
		else writer.write(this.stemmer.getClass().toString().split(" ")[1]);
		writer.write(Constant.LINE_SEPARATOR);

		writer.write("lower:" + Constant.WORD_SEPARATOR);
		if (this.toLower) {
			writer.write(Constant.YES);
		}
		else writer.write(Constant.NO);
		writer.write(Constant.LINE_SEPARATOR);

		writer.close();
	}

	/**
	 * extract the text data from original files, do stopping and stemming
	 * according to the parameters, and write metadata file at the end of the
	 * processing
	 * 
	 * @param stopper
	 *            if stopper==null, no stopping will be done
	 * @param stemmer
	 *            if stemmer==null, no stemming will be done
	 * @param toLower
	 *            whether the title and the content should be turned to lower
	 *            case
	 * @throws Exception
	 *             such as FileNotFoundException
	 */
	public void extract(Stopper stopper, Stemmer stemmer, boolean toLower) throws Exception {
		this.stemmer = stemmer;
		this.stopper = stopper;
		this.toLower = toLower;

		extractFiles();
		writeMetadata();
	}

	protected abstract void extractFiles() throws Exception;
}
