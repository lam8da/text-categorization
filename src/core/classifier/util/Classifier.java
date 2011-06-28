package core.classifier.util;

import java.io.File;

public interface Classifier {
	public void train();

	public int classify(String[] titleFeatures, String[] contentFeatures);

	public void serialize(File outputDir) throws Exception;
}
