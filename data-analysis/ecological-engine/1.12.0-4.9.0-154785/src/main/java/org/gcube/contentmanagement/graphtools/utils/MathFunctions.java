package org.gcube.contentmanagement.graphtools.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;

public class MathFunctions {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		double[] a = logSubdivision(1,4874,5);
		for (int i =0;i<a.length;i++){
		System.out.print(a[i]+" ");
		}
		*/
//		System.out.println(" "+roundDecimal(300.23454,2));
		
//		System.out.println(cohensKappaForDichotomy(20, 5, 10, 15));
//		System.out.println(cohensKappaForDichotomy(45, 15, 25, 15));
		System.out.println(cohensKappaForDichotomy(25,35,5,35));
	}

	//rounds to the xth decimal position
	public static double roundDecimal(double number,int decimalposition){
		
		double n = (double)Math.round(number * Math.pow(10.00,decimalposition))/Math.pow(10.00,decimalposition);
		return n;
	}
	
	// increments a percentage o mean calculation when a lot of elements are present
	public static float incrementPerc(float perc, float quantity, int N) {

		if (N == 0)
			return quantity;

		float out = 0;
		int N_plus_1 = N + 1;
		out = (float) ((perc + ((double) quantity / (double) N)) * ((double) N / (double) N_plus_1));
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

		return randomsSet;
	}

	public static int[] generateSequence(int elements) {
		int[] sequence = new int[elements];
		for (int i = 0; i < elements; i++) {
			sequence[i] = i;
		}
		return sequence;
	}

	public static BigInteger chunk2Index(int chunkIndex, int chunkSize) {

		return BigInteger.valueOf(chunkIndex).multiply(BigInteger.valueOf(chunkSize));

	}

	// calculates mean
	public static double mean(double[] p) {
		double sum = 0; // sum of all the elements
		for (int i = 0; i < p.length; i++) {
			sum += p[i];
		}
		return sum / p.length;
	}// end method mean

	//calculates normalized derivative
	public static double[] derivative(double[] a) {
		double[] d = new double[a.length];
		double max = 1;
		if (a.length > 0) {
			for (int i = 0; i < a.length; i++) {
				double current = a[i];
				double previous = current;
				if (i > 0) {
					previous = a[i - 1];
				}
				d[i] = current - previous;
				if (Math.abs(d[i])>max)
					max = Math.abs(d[i]); 
				// System.out.println("point "+a[i]+" derivative "+d[i]);
			}
			
			//normalize
			for (int i = 0; i < a.length; i++) {
				d[i] = d[i]/max;
			}
		}

		return d;
	}

	// returns a list of spikes indexes
	public static boolean[] findMaxima(double[] derivative,double threshold) {
			boolean[] d = new boolean[derivative.length];

			if (d.length > 0) {
				d[0] = false;
				for (int i = 1; i < derivative.length - 1; i++) {
					if ((derivative[i] / derivative[i + 1] < 0) && derivative[i]>0){
//						double ratio = Math.abs((double) derivative[i]/ (double) derivative[i+1]);
//						System.out.println("RATIO "+i+" "+Math.abs(derivative[i]));
//						if ((threshold>0)&&(ratio<threshold))
						if ((threshold>0)&&(Math.abs(derivative[i])>threshold))
							d[i] = true;
					}
					else
						d[i] = false;
				}
				double max = Operations.getMax(derivative);
				if (max==derivative[derivative.length - 1])
					d[derivative.length - 1] = true;
				else
					d[derivative.length - 1] = false;
			}

			return d;
		}
		
	// returns a list of spikes indexes
	public static boolean[] findSpikes(double[] derivative,double threshold) {
		boolean[] d = new boolean[derivative.length];

		if (d.length > 0) {
			d[0] = false;
			for (int i = 1; i < derivative.length - 1; i++) {
				if (derivative[i] / derivative[i + 1] < 0){
//					double ratio = Math.abs((double) derivative[i]/ (double) derivative[i+1]);
//					System.out.println("RATIO "+i+" "+Math.abs(derivative[i]));
//					if ((threshold>0)&&(ratio<threshold))
					if ((threshold>0)&&(Math.abs(derivative[i])>threshold))
						d[i] = true;
				}
				else
					d[i] = false;
			}
			d[derivative.length - 1] = false;
		}

		return d;
	}

	// returns a list of spikes indexes
	public static boolean[] findSpikes(double[] derivative) {
		return findSpikes(derivative,-1);
	}
	
	// transforms a list of points for a series in a double vector of y values
	// it applies ONLY to transposed graphs not to extracted list of points (see GraphSamplesTable)
	public static double[] points2Double(List<Point<? extends Number, ? extends Number>> pointslist, int seriesIndex, int numbOfPoints) {

		double[] points = new double[numbOfPoints];
		// System.out.print("points: ");
		for (int y = 0; y < numbOfPoints; y++) {
			double value = pointslist.get(seriesIndex).getEntries().get(y).getValue().doubleValue();
			points[y] = value;
			// System.out.print(value+" ");
		}

		return points;
	}

	// searches for an index into an array
	public static boolean isIn(List<Integer> indexarray, int index) {
		
		int size = indexarray.size();
		
		for (int i = 0; i < size; i++) {
			if (index == indexarray.get(i).intValue())
				return true;
		}
		
		return false;
	}
	
	
	// finds the indexes of zero points
	public static List<Integer> findZeros(double[] points) {
		
		int size = points.length;
		ArrayList<Integer> zeros = new ArrayList<Integer>();
		
		for (int i = 0; i < size; i++) {
			if (points[i]==0){
				int start = i;
				int end = i;
				
				for (int j=i+1;j<size;j++)
				{
					if (points[j]!=0){
						end = j-1;
						break;
					}
				}
				int center = start+((end-start)/2); 
				zeros.add(center);
				i = end;
			}
		}
		
		return zeros;
		
	}
	
	
	public static double[] logSubdivision(double start,double end,int numberOfParts){
		
		
		if (end<=start)
			return null;
		
		if (start == 0){
			start = 0.01;
		}
		double logStart = Math.log(start);
		double logEnd = Math.log(end);
		double step =0 ;
		if (numberOfParts >0){
			
			double difference = logEnd-logStart;
			step = (difference/(double)numberOfParts);
			
		}
//		double [] points = new double[numberOfParts+1];
		double[] linearpoints = new double[numberOfParts+1];
		
		for (int i=0;i<numberOfParts+1;i++){
			
//			points[i] = logStart+(i*step);
			
			linearpoints[i]= Math.exp(logStart+(i*step));
			if (linearpoints[i]<0.011)
				linearpoints[i] = 0;
		}
		
		return linearpoints;
	}
	
	
	public static double cohensKappaForDichotomy(long NumOf_A1_B1, long NumOf_A1_B0, long NumOf_A0_B1, long NumOf_A0_B0){
		long  T = NumOf_A1_B1+NumOf_A1_B0+NumOf_A0_B1+NumOf_A0_B0;
		
		double Pra = (double)(NumOf_A1_B1+NumOf_A0_B0)/(double) T ;
		double Pre1 = (double) (NumOf_A1_B1+NumOf_A1_B0) * (double) (NumOf_A1_B1+NumOf_A0_B1)/(double) (T*T);
		double Pre2 = (double) (NumOf_A0_B0+NumOf_A0_B1) * (double) (NumOf_A0_B0+NumOf_A1_B0)/(double) (T*T);
		double Pre = Pre1+Pre2;
		double Kappa = (Pra-Pre)/(1d-Pre);
		return roundDecimal(Kappa,3);
	}
	
	public static String kappaClassificationLandisKoch(double kappa){
		if (kappa<0)
			return "Poor";
		else if ((kappa>=0)&&(kappa<=0.20))
			return "Slight";
		else if ((kappa>=0.20)&&(kappa<=0.40))
			return "Fair";
		else if ((kappa>0.40)&&(kappa<=0.60))
			return "Moderate";
		else if ((kappa>0.60)&&(kappa<=0.80))
			return "Substantial";
		else if (kappa>0.80)
			return "Almost Perfect";
		else
			return "Not Applicable";
	}
	
	public static String kappaClassificationFleiss(double kappa){
		if (kappa<0)
			return "Poor";
		else if ((kappa>=0)&&(kappa<=0.40))
			return "Marginal";
		else if ((kappa>0.4)&&(kappa<=0.75))
			return "Good";
		else if (kappa>0.75)
			return "Excellent";
		else
			return "Not Applicable";
	}
	
}
