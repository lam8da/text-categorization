package test.preprocess.util;

import java.util.Random;

import core.preprocess.util.KmppOneDimension;

public class KmppOneDimensionTest {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		double[] data = new double[10];
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			data[i] = 1;
		}
		KmppOneDimension kmpp = new KmppOneDimension(data, 4, 100);
		System.out.println("number of interactions: " + kmpp.cluster());
		System.out.println("Threshold: " + kmpp.getThresh());
		System.out.println();
		kmpp.output();
	}
}
