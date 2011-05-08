package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import core.preprocess.util.Kmpp;

public abstract class FeatureSelection {
	
	public static int MAXSELECTION = 1;
	public static int AVGSELECTION = 2;
	public static int INTERACTION = 2000;
	public static int CLUSTER = 10;
	private  double thresh;
	private double weighting[][];
	
	public FeatureSelection(DataAnalyzer data, int type){
		this.thresh = this.determineThreshold(data, type);
	}
	
	public void FeatureReduction(DataAnalyzer data)throws Exception{
		for(int i = 0; i != weighting.length; i++){
			if(weighting[i][0] <= thresh){
				data.reduce(data.getFeature(i));
			}
		}
	}

	public abstract double getAvgSelectionWeighting(DataAnalyzer data, int featureId);

	public abstract double getMaxSelectionWeighting(DataAnalyzer data, int featureId);

	public  double determineThreshold(DataAnalyzer data, int type){
		int size = data.getV();
		weighting = new double[size][1];
		if(type == this.MAXSELECTION){
			for(int i = 0; i != size; i++){
				weighting[i][0] = this.getMaxSelectionWeighting(data, i);
			}
		}
		else if(type == this.AVGSELECTION){
			for(int i = 0; i != size; i++){
				weighting[i][0] = this.getAvgSelectionWeighting(data, i);
			}			
		}
		Kmpp k = new Kmpp();
		k.cluster(weighting, 1, this.CLUSTER, this.INTERACTION);
		this.thresh = k.getThresh();
		return this.thresh;
	}
}