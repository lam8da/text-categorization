package core.preprocess.util;

public class KmppOneDemension {
	private double[] weighting = null;
	private double[] cluster = null;
	private int[] pos = null;
	private int k;
	private int maxinterations;
	
	/**
	 * @param double[] data
	 * 	weighting to be clustered
	 * @param int k
	 * 	number of cluster
	 * @param int maxinteractions
	 * 	times of interactions
	 * */
	public KmppOneDemension(double[] data, int k, int maxinteractions) throws IndexOutOfBoundsException{
		int i;
		this.k = k;
		this.maxinterations = maxinteractions;
		this.weighting = new double[data.length];
		System.arraycopy((double[])data, 0, (double[])this.weighting, 0, data.length);
		Qsort(this.weighting,0,this.weighting.length-1);
		this.cluster = new double[this.k];
		this.pos = new int[k+1];
		this.pos[0] = -1;
		this.pos[this.k] = this.weighting.length-1;
		/*initialize the cluster point*/
		for(i = 0; i != this.k; i++){
			this.cluster[i] = this.weighting[(i+1)*(this.weighting.length)/(this.k+2)];
		}
	}
	
	/**
	 * @param void
	 * 	void
	 * @return int
	 * 	number of interactions
	 * */
	public int Cluster(){
		if(this.k > this.weighting.length){
			return 0;
		}
		int i;
		int j;
		int k;
		boolean changed;
		double[] tmp = new double[this.k];
		/*keep the mean value of clusters of the last time*/
		System.arraycopy((double[])this.cluster, 0, (double[])tmp, 0, this.k);
		k = 0;
		for(j= 1; ; j++){
			if(Math.abs(this.weighting[j]-this.cluster[k]) > Math.abs(this.cluster[k+1]-this.weighting[j])){
				this.pos[++k] = j-1;
				if(this.k-1 == k)
					break;
			}
		}
		for(i = 1; i != this.maxinterations; i++){
			/*calculate the new mean value*/
			for(j = 0; j !=  this.k; j++){
				this.cluster[j] = 0;
				for(k = this.pos[j]+1; k <= this.pos[j+1]; k++){
					this.cluster[j] += this.weighting[k];
				}
				this.cluster[j] /= (this.pos[j+1]-this.pos[j]);
			}
			changed = false;
			for(j = 0; j != this.k; j++){
				if(this.cluster[j] != tmp[j]){
					changed = true;
					break;
				}
			}
			if(!changed){
				return i;
			}
			System.arraycopy(this.cluster, 0, tmp, 0, this.k);
			/*calculate the new boundary*/
			k = 0;
			for(j= 1; ; j++){
				if(Math.abs(this.weighting[j]-this.cluster[k]) > Math.abs(this.cluster[k+1]-this.weighting[j])){
					this.pos[++k] = j-1;
					if(this.k-1 == k)
						break;
				}
			}
		}
		return this.maxinterations;
	}
	
	/**
	 * @param void
	 * 	void
	 * @return void
	 * 	void
	 * */
	public void Output(){
		for(int i = 0; i != this.k; i++){
			System.out.print(this.cluster[i]+"\t");
			for(int j = this.pos[i]+1; j <= this.pos[i+1]; j++){
				System.out.print(this.weighting[j]+"\t");
			}
			System.out.println();
		}
	}
	
	/**
	 * @param void
	 * 	void
	 * @return double
	 * 	the threshold 
	 *  call it after cluster has been called
	 * */
	public double GetThresh(){
		if(this.k > this.weighting.length){
			return this.weighting[0];
		}
		return this.weighting[this.pos[1]];
	}
	
	/**
	 * @param double[] data
	 * 	data to be partitioned
	 * @param int left
	 * 	the left boundary of data
	 * @param int right
	 * 	the right boundary of data
	 * @return int
	 * 	the divided index
	 * */
	public int Partition(double[] data, int left, int right){
		int part = left;
		double tmp;
		double t = data[part];
		for(int i = left+1; i <= right; i++){
			if(data[i] < t){
				tmp = data[i];
				data[i] = data[part+1];
				data[part] = tmp;
				part++;
			}
		}
		data[part] = t;
		return part;
	}
	/**
	 * @param double[] data
	 * 	data to be sorted
	 * @param int left
	 * 	left boundary of the array to be sorted
	 * @param int right
	 * 	right boundary of the array to be sorted
	 * @return void
	 * */
	public void Qsort(double[] data, int left, int right){
		int part;
		if(left < right){
			part = Partition(data,left,right);
			Qsort(data,left,part-1);
			Qsort(data,part+1,right);
		}
	}
}