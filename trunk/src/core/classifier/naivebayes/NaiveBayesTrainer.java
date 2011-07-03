package core.classifier.naivebayes;

import java.io.File;

import core.classifier.util.FinalDataHolder;
import core.classifier.util.Trainer;

public class NaiveBayesTrainer extends Trainer {
	private static final String badDocMarkInfoFile = "badDocMark";
	private File naiveBayesOutputDir;

	public NaiveBayesTrainer(FinalDataHolder holder) throws Exception {
		super(holder);
		this.naiveBayesOutputDir = config.getNaiveBayesFolder();
		this.naiveBayesOutputDir.mkdirs();
	}

	@Override
	public void train() throws Exception {
	}

	@Override
	public void serialize() throws Exception {
	}
}
