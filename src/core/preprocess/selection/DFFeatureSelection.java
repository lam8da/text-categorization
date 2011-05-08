package core.preprocess.selection;

import core.preprocess.util.DataAnalyzer;
import java.util.ArrayList;

public class DFFeatureSelection extends FeatureSelection{

	
	public DFFeatureSelection(DataAnalyzer data, int type) {
		super(data, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAvgSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		try {
			return data.getM_tk(featureId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public double getMaxSelectionWeighting(DataAnalyzer data, int featureId) {
		// TODO Auto-generated method stub
		try {
			return data.getM_tk(featureId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}


	@Override
	public void FeatureSelection(DataAnalyzer data, int type) throws Exception {
		// TODO Auto-generated method stub
		double max;
		if(type == this.AVGSELECTION){
			for(int i = 0; i != data.getV(); i++){
				max = this.getAvgSelectionWeighting(data, i);
				if(max <= thresh){
					data.reduce(data.getFeature(i));
				}
			}
		}
		else{
			for(int i = 0; i != data.getV(); i++){
				max = this.getMaxSelectionWeighting(data, i);
				if(max <= thresh){
					data.reduce(data.getFeature(i));
				}
			}			
		}
	}
}
