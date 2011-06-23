package core.preprocess.analyzation.generator;

import core.preprocess.analyzation.interfaces.FeatureContainer;
import core.preprocess.analyzation.interfaces.SimpleContainer;

public abstract class ContainerGenerator {
	public abstract FeatureContainer generateFeatureContainer();

	public abstract SimpleContainer generateSimpleContainer();
}
