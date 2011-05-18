package core.preprocess.util;

import java.util.Arrays;

public class KmppOneDimension {
        private double[] weighting = null;
        private double[] cluster = null;
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
         * @param void void
         * @return int number of interactions
         * */
        public int cluster(){
                if(this.k >= this.weighting.length){
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
                for(j= 1; j != this.weighting.length; j++){
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
                                if(this.pos[j+1] != this.pos[j])
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
                        for(j= 1; j!= this.weighting.length; j++){
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
         *      void
         * @return void
         *      void
         * */
        public void output(){
        	if(this.k >= this.weighting.length){
        		return ;
        	}
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
         *      void
         * @return double
         *      the threshold 
         *  call it after cluster has been called
         * */
        public double getThresh(){
                if(this.k >= this.weighting.length){
                        return this.weighting[0];
                }
                return this.weighting[this.pos[1]];
        }
}