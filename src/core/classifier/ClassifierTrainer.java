package core.classifier;

import java.io.File;

import core.classifier.twcnb.TWCNBayes;
import core.classifier.util.FinalDataHolder;
import core.preprocess.analyzation.generator.ContainerGenerator;
import core.preprocess.analyzation.generator.MapGenerator;
import core.preprocess.analyzation.generator.TrieGenerator;
import core.preprocess.util.Constant;

public class ClassifierTrainer {
	private ContainerGenerator generator;
	private FinalDataHolder dataHolder;

	public ClassifierTrainer(File inputDir, int generatorId) throws Exception {
		switch (generatorId) {
		case Constant.TRIE_GENERATOR:
			generator = new TrieGenerator();
			break;
		case Constant.MAP_GENERATOR:
			generator = new MapGenerator();
			break;
		}

		dataHolder = FinalDataHolder.deserialize(generator, inputDir);
	}

	public void train() throws Exception {
		TWCNBayes trainer = new TWCNBayes(dataHolder);
		trainer.train();
	}

	public static void main() {

	}
}
