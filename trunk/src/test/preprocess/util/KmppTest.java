package test.preprocess.util;

import core.preprocess.util.Kmpp;

public class KmppTest {

	public static void main(String[] args){
		double ori[][] = {{2.3},{3.5},{5.6},{7.8},{0.1},{6.1},{6.9},{0.2},{4},{7},{0.3},{1.1},{5.2},{2.1}};
		Kmpp k = new Kmpp();
		k.cluster(ori, 1, 4, 100);
		k.outputClusters();
		double thresh = k.getThresh();
		System.out.println(thresh);
	}
}
