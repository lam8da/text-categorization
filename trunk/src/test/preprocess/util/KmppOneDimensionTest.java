package test.preprocess.util;

//import java.util.Random;

import core.preprocess.util.KmppOneDimension;

public class KmppOneDimensionTest {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int len = 1000;
		double[] data = new double[len];
		//Random r = new Random();
		for (int i = 0; i < len; i++) {
			data[i] = i;
		}
		KmppOneDimension kmpp = new KmppOneDimension(data, 10, len * 10);
		System.out.println("number of interactions: " + kmpp.cluster());
		System.out.println("Threshold: " + kmpp.getThresh());
		System.out.println();
		kmpp.output();
	}
}
