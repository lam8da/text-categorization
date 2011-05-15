package test.preprocess.util;

import java.util.Random;

import core.preprocess.util.KmppOneDimension;

public class KmppOneDimensionTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] data = new double[10];
		Random r = new Random();
		for(int i = 0; i != 10; i++){
			data[i] = r.nextDouble();
		}
		KmppOneDimension kmpp = new KmppOneDimension(data,3,100);
		System.out.println("number of interactions: "+kmpp.cluster());
		System.out.println("Threshold: "+kmpp.getThresh());
		kmpp.output();
	}
}
