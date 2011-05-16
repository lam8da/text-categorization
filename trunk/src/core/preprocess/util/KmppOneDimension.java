package core.preprocess.util;

import java.util.Arrays;

public class KmppOneDimension {
	private double[] weighting;
	private double[] cluster;
	private double[] sum;
	private int[] pos;
	private int k;
	private int maxIterations;

	/**
	 * @param double[] data weighting to be clustered
	 * @param int k number of cluster
	 * @param int maxinteractions times of interactions
	 * @throws Exception
	 */
	public KmppOneDimension(double[] data, int k, int maxIterations) throws Exception {
		if (data.length < k) {
			throw new Exception("data.length < k !!!");
		}

		this.k = k;
		this.maxIterations = maxIterations;
		this.weighting = Arrays.copyOf(data, data.length);
		Arrays.sort(this.weighting);

		this.sum = Arrays.copyOf(this.weighting, this.weighting.length);
		for (int i = 1; i < sum.length; i++) {
			sum[i] += sum[i - 1];
		}

		/* initialize the cluster point */
		this.cluster = new double[this.k];
		for (int i = 0; i < this.k; i++) {
			this.cluster[i] = this.weighting[i];
		}

		this.pos = new int[k];
		for (int i = 0; i < this.k - 1; i++) {
			this.pos[i] = i;
		}
		this.pos[this.k - 1] = this.weighting.length - 1;
	}

	/**
	 * @param void void
	 * @return int number of interactions
	 */
	public int cluster() {
		/* keep the mean value of clusters of the last time */
		double[] tmp = Arrays.copyOf(this.cluster, this.k);
		for (int i = 1; i <= this.maxIterations; i++) {
			/* calculate the new mean value */
			cluster[0] = sum[pos[0]];
			for (int j = 1; j < this.k; j++) {
				cluster[j] = (sum[pos[j]] - sum[pos[j - 1]]) / (pos[j] - pos[j - 1]);
			}
			boolean changed = false;
			for (int j = 0; j != this.k; j++) {
				if (this.cluster[j] != tmp[j]) {
					changed = true;
					break;
				}
			}
			if (!changed) return i;
			for (int j = 0; j < this.k; j++) {
				tmp[j] = cluster[j];
			}

			/* calculate the new boundary */
			for (int j = 1, x = 0; x < k - 1; j++) {
				if (Math.abs(weighting[j] - cluster[x]) >= Math.abs(weighting[j] - cluster[x + 1])) {
					this.pos[x++] = j - 1;
				}
			}
		}
		return this.maxIterations;
	}

	/**
	 * @param void void
	 * @return void void
	 */
	public void output() {
		for (int i = 0; i < k; i++) {
			System.out.println(i + " - " + cluster[i] + ":");
			for (int j = (i == 0 ? 0 : (pos[i - 1] + 1)); j <= pos[i]; j++) {
				System.out.print("\t" + this.weighting[j] + "\t");
				int cnt = 1, k;
				for (k = j + 1; k <= pos[i] && weighting[k] == weighting[j]; k++, cnt++);
				j = k - 1;
				System.out.println("(" + cnt + ")");
			}
		}
	}

	/**
	 * @param void void
	 * @return double the threshold call it after cluster has been called
	 */
	public double getThresh() {
		return weighting[pos[0]];
	}
}