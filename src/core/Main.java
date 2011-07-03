package core;

import java.io.File;

import core.classifier.ClassifierTrainer;
import core.evaluation.Evaluator;
import core.preprocess.Preprocessor;
import core.util.Configurator;
import core.util.Constant;

public class Main {
	public static void printUsage() {
		String usage = "tc config_file [-p stage1 stage2] [-t] [-e]";
		String[] _p = new String[] {//
		"-p stage1 stage2",//
				"Preprocess the text.",//
				"stage1(2) must be one of \"ext\", \"ana\", \"fea\" and \"ser\";",//
				"\"ext\" represents document extraction;",//
				"\"ana\" represents document analyzation;",//
				"\"fea\" represents feature selection;",//
				"\"ser\" represents serialization the result;",//
				"\"stage1\" must be prior to \"stage2\";",//
				"If -t is specified, \"stage2\" must be \"ser\"."//
		};
		String[] _t = new String[] {//
		"-t",//
				"Train the classifier specified by config_file.",//
				"If -p and -e are both specified, -t must be specified."//
		};
		String[] _e = new String[] { "-e", "Do evaluation on the trained classifier." };
		String[][] options = new String[][] { _p, _t, _e };

		final int indent = 20;
		String alignmentStr = "   ";
		String indentStr = alignmentStr;
		for (int i = 0; i < indent; i++) {
			indentStr += " ";
		}

		System.out.println();
		System.out.println("Usage: " + usage);
		System.out.println();
		System.out.println("Options:");
		for (int i = 0; i < options.length; i++) {
			int len = options[i][0].length();
			System.out.print(alignmentStr + options[i][0]);

			for (int j = len; j < indent; j++) {
				System.out.print(" ");
			}
			System.out.println(options[i][1]);

			for (int j = 2; j < options[i].length; j++) {
				System.out.println(indentStr + options[i][j]);
			}
		}
		//System.out.println();
	}

	public static int parseStageId(String[] args, int i) {
		if (args.length < i + 1) return -1;
		if (args[i].equals("ext")) {
			return Constant.STAGE_EXTRACTION;
		}
		if (args[i].equals("ana")) {
			return Constant.STAGE_ANALYZATION;
		}
		if (args[i].equals("fea")) {
			return Constant.STAGE_FEATURE_SELECTION;
		}
		if (args[i].equals("ser")) {
			return Constant.STAGE_SERIALIZATION;
		}
		return -1;
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			printUsage();
			return;
		}

		boolean doPreprocess = false;
		boolean doTraining = false;
		boolean doEvaluation = false;
		int stage1 = -1;
		int stage2 = -1;

		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("-p")) {
				i++;
				stage1 = parseStageId(args, i);
				i++;
				stage2 = parseStageId(args, i);
				if (stage1 == -1 || stage2 == -1 || stage1 > stage2) {
					printUsage();
					return;
				}
				doPreprocess = true;
			}
			else if (args[i].equals("-t")) {
				doTraining = true;
			}
			else if (args[i].equals("-e")) {
				doEvaluation = true;
			}
			else {
				printUsage();
				return;
			}
		}
		if (doPreprocess && doEvaluation && !doTraining) {
			printUsage();
			return;
		}
		if (doPreprocess && doTraining && stage2 != Constant.STAGE_SERIALIZATION) {
			printUsage();
			return;
		}
		if (!doPreprocess && !doEvaluation && !doTraining) {
			printUsage();
			return;
		}

		System.out.println();
		Configurator config = Configurator.getConfigurator();
		config.deserializeFrom(new File(args[0]));

		if (doPreprocess) {
			System.out.println("------------------------------- preprocessing -------------------------------");
			System.out.println();
			Preprocessor preprocessor = new Preprocessor();
			preprocessor.preprocess(stage1, stage2);
		}
		if (doTraining) {
			System.out.println("---------------------------------- training ---------------------------------");
			System.out.println();
			ClassifierTrainer trainer = new ClassifierTrainer();
			trainer.train();
		}
		if (doEvaluation) {
			System.out.println("--------------------------------- evaluating --------------------------------");
			System.out.println();
			Evaluator evaluator = new Evaluator();
			evaluator.evaluate();
		}
	}
}
