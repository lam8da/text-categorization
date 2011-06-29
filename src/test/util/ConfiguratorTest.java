package test.util;

import java.io.File;

import core.util.Configurator;
import core.util.Constant;

public class ConfiguratorTest {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("invalid parameters!");
			return;
		}

		Configurator config = Configurator.getConfigurator();
		//config.setValues("123456", args[0], 1, 1, 1, 1, true, true, true, 1, 1, Constant.TRIE_GENERATOR);
		config.deserializeFrom(new File(new File(args[0]), Constant.CONFIG_FILENAME));
	}
}
