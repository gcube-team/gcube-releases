package org.gcube.contentmanagement.lexicalmatcher.utils;

import java.math.BigInteger;
import java.util.ArrayList;

public class MathFunctions {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	//increments a percentage o mean calculation when a lot of elements are present 
	public static float incrementPerc(float perc, float quantity, int N){
		
		if (N==0)
			return quantity;
		
		float out = 0;
		int N_plus_1 = N+1;
		out = (float)((perc + ((double)quantity / (double)N )) * ((double)N/(double)N_plus_1));
		return out;
		
	}
	
	
	public static ArrayList<Integer> generateRandoms(int numberOfRandoms, int min, int max) {

		ArrayList<Integer> randomsSet = new ArrayList<Integer>();
		// if number of randoms is equal to -1 generate all numbers
		if (numberOfRandoms == -1) {
			for (int i = min; i < max; i++) {
				randomsSet.add(i);
			}
		} else {
			int numofrandstogenerate = 0;
			if (numberOfRandoms <= max) {
				numofrandstogenerate = numberOfRandoms;
			} else {
				numofrandstogenerate = max;
			}

			if (numofrandstogenerate == 0) {
				randomsSet.add(0);
			} else {
				for (int i = 0; i < numofrandstogenerate; i++) {

					int RNum = -1;
					RNum = (int) ((max) * Math.random()) + min;

					// generate random number
					while (randomsSet.contains(RNum)) {
						RNum = (int) ((max) * Math.random()) + min;
						// AnalysisLogger.getLogger().debug("generated " + RNum);
					}

					// AnalysisLogger.getLogger().debug("generated " + RNum);

					if (RNum >= 0)
						randomsSet.add(RNum);
				}

			}
		}

		AnalysisLogger.getLogger().trace("MathFunctions-> generateRandoms " + randomsSet.toString());

		return randomsSet;
	}
	
	
	public static int[] generateSequence(int elements) {
		int [] sequence = new int[elements];
		for (int i=0;i<elements;i++){
			sequence[i]=i;
		}
		return sequence;
	}
	
	public static BigInteger chunk2Index(int chunkIndex,int chunkSize){
		
		return BigInteger.valueOf(chunkIndex).multiply(BigInteger.valueOf(chunkSize));
		
	}
	
	//calculates mean
	public static double mean(double[] p) {
	    double sum = 0;  // sum of all the elements
	    for (int i=0; i<p.length; i++) {
	        sum += p[i];
	    }
	    return sum / p.length;
	}//end method mean
	
	
}
