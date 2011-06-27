package core.preprocess.analyzation.generator;

import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.analyzation.interfaces.SimpleContainer;
import core.preprocess.analyzation.trie.SimpleTrie;
import core.preprocess.analyzation.trie.Trie;

public class TrieGenerator extends ContainerGenerator {

	@Override
	public FeatureContainer generateFeatureContainer() {
		return new Trie();
	}

	@Override
	public SimpleContainer generateSimpleContainer(FeatureContainer mapper) {
		return new SimpleTrie((Trie) mapper);
	}
}
