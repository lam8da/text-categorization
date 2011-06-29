package test.evaluation;

import java.io.File;

import core.evaluation.Evaluator;
import core.util.Constant;

public class EvaluatorTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}

		Evaluator evaluator = new Evaluator(new File(args[0]));
		evaluator.evaluate(Constant.TWCNB);
	}
}
