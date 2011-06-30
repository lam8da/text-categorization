package core.preprocess.corpus;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import core.preprocess.extraction.Stemmer;
import core.preprocess.selection.Stopper;
import core.preprocess.corpus.BadWordHandler;
import core.util.Constant;

public abstract class Extractor {
	protected int splitting;
	protected File inputDir;
	protected File outputDir;
	protected File trainingDir;
	protected File testDir;
	protected Stemmer stemmer;
	protected Stopper stopper;
	protected boolean toLower;
	protected BadWordHandler wordHandler;

	public Extractor(File inputDir, File outputDir, int splitting) {
		this.splitting = splitting;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.trainingDir = new File(outputDir, Constant.TRAINING_FOLDER);
		this.testDir = new File(outputDir, Constant.TEST_FOLDER);

//		System.out.println("Deleting all files in " + outputDir);
//		File[] files = outputDir.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			files[i].delete();
//		}

		this.trainingDir.mkdirs();
		this.testDir.mkdirs();
		this.stemmer = null;
		this.stopper = null;
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
	public void extract(Stopper stopper, Stemmer stemmer, boolean toLower, boolean timeToConst, boolean numToConst) throws Exception {
		this.stemmer = stemmer;
		this.stopper = stopper;
		this.toLower = toLower;
		this.wordHandler = new BadWordHandler(timeToConst, numToConst);

		extractFiles();
	}

	protected String processWords(String str) {
		String[] words = str.split(Constant.WORD_SEPARATING_PATTERN);
		Vector<String> vs = new Vector<String>(512);
		for (int i = 0; i < words.length; i++) {
			//we may use a hash table to reduce the times of invoking "process"
			this.wordHandler.process(words[i], vs);
		}

		StringBuffer sb = new StringBuffer(2048);
		for (Iterator<String> it = vs.iterator(); it.hasNext();) {
			String word = it.next();
			if (this.stopper != null) {
				if (this.stopper.stop(word)) continue;
			}
			if (this.stemmer != null) {
				word = this.stemmer.stem(word, this.toLower);
			}
			else if (this.toLower) {
				word = word.toLowerCase();
			}
			sb.append(word).append(Constant.WORD_SEPARATOR);
		}
		return sb.toString();
	}

	protected abstract void extractFiles() throws Exception; //should invoke function "processWords"
}
