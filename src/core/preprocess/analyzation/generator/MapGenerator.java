package core.preprocess.analyzation.generator;

import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.analyzation.interfaces.SimpleContainer;
import core.preprocess.analyzation.map.FeatureMap;
import core.preprocess.analyzation.map.SimpleMap;

public class MapGenerator extends ContainerGenerator {

	@Override
	public FeatureContainer generateFeatureContainer() {
		return new FeatureMap();
	}

	@Override
	public SimpleContainer generateSimpleContainer(FeatureContainer mapper) {
		return new SimpleMap((FeatureMap) mapper);
	}
}
