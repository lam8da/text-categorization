package test.classifier.twcnb;

import java.io.File;

import core.classifier.ClassifierTrainer;

public class TWCNBayesTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}

		ClassifierTrainer trainer = new ClassifierTrainer(new File(args[0]));
		trainer.train();
	}
}
