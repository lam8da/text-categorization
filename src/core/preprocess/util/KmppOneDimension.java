package core.preprocess.util;

import java.util.Arrays;

public class KmppOneDimension {
        private double[] weighting = null;
        private double[] cluster = null;
        private int bestCluster;
        private int[] pos = null;
        private int k;
        private int maxinterations;
        
        /**
         * @param double[] data weighting to be clustered
         * @param int k number of cluster
         * @param int maxinteractions times of interactions
         * */
        public KmppOneDimension(double[] data, int k, int maxinteractions){
                int i;
                this.k = k;
                this.maxinterations = maxinteractions;
                this.weighting = new double[data.length];
                System.arraycopy((double[])data, 0, (double[])this.weighting, 0, data.length);
                Arrays.sort(this.weighting);
                if(k <= 1){
                	this.k = k;
                	return ;
                }
                if(this.k >= data.length){
                    return ;
                }
                this.cluster = new double[this.k];
                this.pos = new int[k+1];
                /*initialize the cluster point*/
                this.cluster[0] = this.weighting[0];
                for(i = 1; i != this.k-1; i++){
                        this.cluster[i] = this.weighting[i*(this.weighting.length)/(this.k-1)];
                }
                this.cluster[this.k-1] = this.weighting[this.weighting.length-1];
        }
        
        /**
         * @param void void
         * @return int number of interactions
         * */
        public int cluster(){
        		if(this.k <= 1){
        			return 0;
        		}
                if(this.k >= this.weighting.length){
                        return 0;
                }
                int i;
                int j;
                int k;
                double[] tmp = new double[this.k];
            	System.arraycopy((double[])this.cluster, 0, (double[])tmp, 0, this.k);
            	/*remove the duplicated clustering point and just once*/
            	k = 1;
            	for(j = 1; j != this.k; j++){
            		if(this.cluster[k-1] != tmp[j]){
            			this.cluster[k] = tmp[j];
            			k++;
            		}
            	}
            	/*record the estimated best number of clustering point*/
            	this.bestCluster = k;
            	if(this.bestCluster == 1){
            		return 0;
            	}
            	this.pos[0] = -1;
            	this.pos[this.bestCluster] = this.weighting.length-1;
                for(i = 1; i != this.maxinterations; i++){
                	System.arraycopy((double[])this.cluster, 0, (double[])tmp, 0, this.bestCluster);
                	/*compute the boundary*/
                	k = 0;
                	for(j = 1; ;){
                		if(Math.abs(this.cluster[k]-this.weighting[j]) > Math.abs(this.cluster[k+1]-this.weighting[j])){
                			this.pos[++k] = j-1;
                			if(k == this.bestCluster-1){
                				break;
                			}
                		}
                		j++;
                	}
                	/*compute the clustering points*/
                	for(j = 0; j != this.bestCluster; j++){
                		this.cluster[j] = 0;
                		for(k = this.pos[j]+1; k <= this.pos[j+1]; k++){
                			this.cluster[j] += this.weighting[k];
                		}
                		this.cluster[j] /= (this.pos[j+1]-this.pos[j]);
                	}
                	for(j = 0; j != this.bestCluster; j++){
                		if(this.cluster[j] != tmp[j]){
                			break;
                		}
                	}
                	if(j == this.bestCluster){
                		return i;
                	}
                }
                return this.maxinterations;
        }
        
        /**
         * @param void
         *      void
         * @return void
         *      void
         * */
        public void output(){
            if(this.k >= this.weighting.length || this.k <= 1 || this.bestCluster == 1 ){
            	for(int i = 0; i != this.weighting.length; i++){
                    System.out.print(this.weighting[i]+"\t");
            	}
                return ;
        }
            for(int i = 0; i != this.bestCluster; i++){
                    System.out.print(this.cluster[i]+":\t");
                    for(int j = this.pos[i]+1; j <= this.pos[i+1]; j++){
                            System.out.print(this.weighting[j]+"\t");
                    }
                    System.out.println();
            }
        }
        
        /**
         * @param void
         *      void
         * @return double
         *      the threshold 
         *  call it after cluster has been called
         * */
        public double getThresh(){
                if(this.k >= this.weighting.length || this.k <= 1 || this.bestCluster == 1 ){
                        return this.weighting[this.weighting.length-1];
                }
                return this.weighting[this.pos[1]];
        }
}