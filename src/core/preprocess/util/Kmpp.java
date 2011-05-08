package core.preprocess.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math.stat.clustering.Cluster;
import org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer;

public class Kmpp {
	private List<Cluster<DoublePoint>> list = null;
	private int dimension = 0;
	/**
	 * @param final double[][] points
	 * 	d-dimension points
	 * @param final int d
	 * 	dimension of points
	 * @param final int k
	 * 	number of clusters to be divided into
	 * @param final int maxinteractions
	 * 	number of maximum of interaction during computation of kmpp algorithm
	 * @return List<Cluster<DoublePoint>>
	 * 	clusters
	 * */
	public List<Cluster<DoublePoint>> cluster(final double[][] points, final int d, final int k, final int maxinterations){
		KMeansPlusPlusClusterer<DoublePoint> km = new KMeansPlusPlusClusterer<DoublePoint>(new Random());
		Collection<DoublePoint> col = new ArrayList<DoublePoint>();
		this.dimension = d;
		for(int i = 0; i != points.length; i++){
			col.add(new DoublePoint(points[i]));
		}
		list = new ArrayList<Cluster<DoublePoint>>();
		list = km.cluster(col, k, maxinterations);
		return list;
	}
	
	/**
	 * @param void
	 * call it after cluster has been called
	 * */
	public void outputClusters(){
		int ind = 1;
		Iterator<Cluster<DoublePoint>> it = list.iterator();
		while(it.hasNext()){
			Cluster<DoublePoint> cl = it.next();
			System.out.print("Cluster"+(ind++)+":");
			List<DoublePoint> li = cl.getPoints();
			Iterator<DoublePoint> ii = li.iterator();
			while(ii.hasNext()){
				DoublePoint eip = ii.next();
				System.out.print(eip+" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * @param void
	 * void
	 * @return double
	 * 	thresh for one-dimension
	 * */
	public double getThresh(){
		if(this.dimension != 1){
			return Integer.MAX_VALUE;
		}
		double thresh = Integer.MAX_VALUE;
		double tmp;
		Iterator<Cluster<DoublePoint>> it = list.iterator();
		while(it.hasNext()){
			Cluster<DoublePoint> cl = it.next();
			List<DoublePoint> li = cl.getPoints();
			Iterator<DoublePoint> ii = li.iterator();
			tmp = Integer.MAX_VALUE;
			if(ii.hasNext()){
				DoublePoint eip = ii.next();
				tmp = eip.getPoint()[0];
			}
			while(ii.hasNext()){
				DoublePoint eip = ii.next();
				if(tmp < eip.getPoint()[0]){
					tmp = eip.getPoint()[0];
				}
			}
			if(tmp < thresh){
				thresh = tmp;
			}
		}
		return thresh;
	}
}
