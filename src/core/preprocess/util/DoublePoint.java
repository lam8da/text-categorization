package core.preprocess.util;

import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.math.stat.clustering.Clusterable;

public class DoublePoint implements Clusterable<DoublePoint>, Serializable{

	private static final long serialVersionUID = 12956237845231L;
	private final double[] point;
	public DoublePoint(double[] point){
		this.point = point;
	}
	
	public double[] getPoint(){
		return this.point;
	}
	
	@Override
	public DoublePoint centroidOf(Collection<DoublePoint> points) {
		// TODO Auto-generated method stub
		double[] centroid = new double[this.point.length];
        for (DoublePoint p : points) {
            for (int i = 0; i != centroid.length; i++) {
                centroid[i] += p.getPoint()[i];
            }
        }
        for (int i = 0; i != centroid.length; i++) {
            centroid[i] /= points.size();
        }
        return new DoublePoint(centroid);
	}

	@Override
	public double distanceFrom(DoublePoint p) {
		// TODO Auto-generated method stub
		double dist = 0;
		int len = this.point.length;
		for(int i = 0; i != len; i++){
			dist += (this.point[i]-p.point[i])*(this.point[i]-p.point[i]);
		}
		return Math.sqrt(dist);
	}

    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder("(");
        final double[] coordinates = getPoint();
        for (int i = 0; i != coordinates.length; i++) {
            buff.append(coordinates[i]);
            if (i != coordinates.length - 1) {
                buff.append(",");
            }
        }
        buff.append(")");
        return buff.toString();
    }
}
