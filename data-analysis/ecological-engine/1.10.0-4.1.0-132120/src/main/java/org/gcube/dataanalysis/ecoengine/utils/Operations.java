package org.gcube.dataanalysis.ecoengine.utils;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;

public class Operations {

	public static double scalarProduct(double[] a, double[] b) {

		double sum = 0;

		for (int i = 0; i < a.length; i++) {
			if (i < b.length)
				sum = sum + a[i] * b[i];
		}

		return sum;
	}

	public static double sumVector(double[] a) {

		double sum = 0;

		for (int i = 0; i < a.length; i++) {
			sum = sum + a[i];
		}

		return sum;
	}

	public static double[] vectorialDifference(double[] a, double[] b) {

		double[] diff = new double[a.length];

		for (int i = 0; i < a.length; i++) {
			if (i < b.length)
				diff[i] = a[i] - b[i];
			else
				diff[i] = a[i];
		}

		return diff;
	}

	public static double[] vectorialAbsoluteDifference(double[] a, double[] b) {

		double[] diff = new double[a.length];

		for (int i = 0; i < a.length; i++) {
			if (i < b.length)
				diff[i] = Math.abs(a[i] - b[i]);
			else
				diff[i] = Math.abs(a[i]);
		}

		return diff;
	}

	public static double getMax(double[] points) {
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			if (max < points[i])
				max = points[i];
		}
		return max;
	}

	public static int getMax(int[] points) {
		int max = -Integer.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			if (max < points[i])
				max = points[i];
		}
		return max;
	}

	public static int getMin(int[] points) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			if (min > points[i])
				min = points[i];
		}
		return min;
	}

	public static double getMin(double[] points) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			if (min > points[i])
				min = points[i];
		}
		return min;
	}

	// calculates the frequency distribution for a set of points respect to a set of intervals
	public static double[] calcFrequencies(double[] interval, double[] points) {
		int intervs = interval.length;
		int npoints = points.length;
		double[] frequencies = new double[intervs];
		for (int i = 0; i < intervs; i++) {

			for (int j = 0; j < npoints; j++) {

				if (((i == 0) && (points[j] < interval[i])) || ((i == intervs - 1) && (points[j] >= interval[i - 1]) && (points[j] <= interval[i])) || ((i > 0) && (points[j] >= interval[i - 1]) && (points[j] < interval[i]))) {
					// System.out.println("(" + (i == 0 ? "" : interval[i - 1]) + "," + interval[i] + ")" + " - " + points[j]);
					frequencies[i] = frequencies[i] + 1;
				}
			}
		}

		return frequencies;
	}

	public static double[] normalizeFrequencies(double[] frequencies, int numberOfPoints) {
		int intervs = frequencies.length;
		for (int i = 0; i < intervs; i++) {
			frequencies[i] = frequencies[i] / (double) numberOfPoints;
		}

		return frequencies;

	}

	// checks if an interval contains at least one element from a sequence of points
	public static boolean intervalContainsPoints(double min, double max, double[] points) {
		// System.out.println(min+"-"+max);
		boolean contains = false;
		for (int i = 0; i < points.length; i++) {
			if ((points[i] >= min) && (points[i] < max)) {
				// System.out.println("---->"+points[i]);
				contains = true;
				break;
			}
		}
		return contains;
	}

	// finds the best subdivision for a sequence of numbers
	public static double[] uniformDivide(double max, double min, double[] points) {
		int maxintervals = 10;
		int n = maxintervals;

		boolean subdivisionOK = false;
		double gap = (max - min) / n;

		// search for the best subdivision: find the best n
		while (!subdivisionOK) {
			// System.out.println("*************************");
			boolean notcontains = false;
			// take the gap interval to test
			for (int i = 0; i < n; i++) {
				double rightmost = 0;
				// for the last border take a bit more than max
				if (i == n - 1)
					rightmost = max + 0.01;
				else
					rightmost = min + gap * (i + 1);
				// if the interval doesn't contain any point discard the subdivision
				if (!intervalContainsPoints(min + gap * i, rightmost, points)) {
					notcontains = true;
					break;
				}
			}

			// if there are empty intervals and there is space for another subdivision proceed
			if (notcontains && n > 0) {
				n--;
				gap = (max - min) / n;
			}
			// otherwise take the default subdivision
			else if (n == 0) {
				n = maxintervals;
				subdivisionOK = true;
			}
			// if all the intervals are non empty then exit
			else
				subdivisionOK = true;
		}

		// once the best n is found build the intervals
		double[] intervals = new double[n];
		for (int i = 0; i < n; i++) {
			if (i < n - 1)
				intervals[i] = min + gap * (i + 1);
			else
				intervals[i] = Double.POSITIVE_INFINITY;
		}

		return intervals;
	}

	public double[][] standardize(double[][] matrix) {
		return standardize(matrix, null, null);
	}

	public double[] means;
	public double[] variances;

	// standardizes a matrix: each row represents a vector: outputs columns means and variances
	public double[][] standardize(double[][] matrix, double[] meansVec, double[] variancesVec) {

		if (matrix.length > 0) {
			int ncols = matrix[0].length;
			int mrows = matrix.length;

			if ((means == null) && (variances == null)) {
				means = new double[ncols];
				variances = new double[ncols];
			}

			double[][] matrixT = Transformations.traspose(matrix);

			for (int i = 0; i < ncols; i++) {
				double[] icolumn = matrixT[i];

				double mean = 0;

				if (meansVec == null) {
					mean = MathFunctions.mean(icolumn);
					means[i] = mean;
				} else
					mean = meansVec[i];

				double variance = 0;
				if (variancesVec == null) {
					variance = com.rapidminer.tools.math.MathFunctions.variance(icolumn, Double.NEGATIVE_INFINITY);
					variances[i] = variance;
				} else
					variance = variancesVec[i];

				for (int j = 0; j < mrows; j++) {
					// standardization
					double numerator = (icolumn[j] - mean);
					if ((numerator == 0) && (variance == 0))
						icolumn[j] = 0;
					else if (variance == 0)
						icolumn[j] = Double.MAX_VALUE;
					else
						icolumn[j] = numerator / variance;
				}
			}

			matrix = Transformations.traspose(matrixT);

		}
		return matrix;
	}

	// calculates the number of elements to take from a set with inverse weight respect to the number of elements
	public static int calcNumOfRepresentativeElements(int numberOfElements, int minimumNumberToTake) {
		return (int) Math.max(minimumNumberToTake, numberOfElements / Math.log10(numberOfElements));
	}

	public static double[] linearInterpolation(double el1, double el2, int intervals) {

		double step = (el2 - el1) / (double) intervals;

		double[] intervalsd = new double[intervals];
		intervalsd[0] = el1;
		for (int i = 1; i < intervals - 1; i++) {
			intervalsd[i] = el1 + step * i;
		}
		intervalsd[intervals - 1] = el2;

		return intervalsd;
	}

	private static double parabol(double a, double b, double c, double x, double shift) {
		return a * (x - shift) * (x - shift) + b * (x - shift) + c;
	}

	public static double[] inverseParabol(double a, double b, double c, double y) {

		double[] ret = { (-1d * b + Math.sqrt(b * b + 4 * a * (Math.abs(y) - c))) / (2 * a), (-1d * b - Math.sqrt(b * b + 4 * a * (Math.abs(y) - c))) / (2 * a) };
		return ret;
	}

	public static double logaritmicTransformation(double y) {
		y = Math.abs(y);
		if (y == 0)
			return -Double.MAX_VALUE;
		else
			return Math.log10(y);
	}

	// the parabol is centered on the start Point
	public static double[] parabolicInterpolation(double startP, double endP, int intervals) {

		double start = startP;
		double end = endP;
		double shift = start;

		double a = 1000d;
		double b = 0d;
		double c = 0d;
		double parabolStart = parabol(a, b, c, start, shift);
		if (start < 0)
			parabolStart = -1 * parabolStart;

		double parabolEnd = parabol(a, b, c, end, start);
		if (end < 0)
			parabolEnd = -1 * parabolEnd;

		double step = 0;
		if (intervals > 0) {
			double difference = Math.abs(parabolEnd - parabolStart);
			step = (difference / (double) intervals);
		}

		double[] linearpoints = new double[intervals];

		linearpoints[0] = startP;
		// System.out.println("Y0: "+parabolStart);
		for (int i = 1; i < intervals - 1; i++) {
			double ypoint = 0;
			if (end > start)
				ypoint = parabolStart + (i * step);
			else
				ypoint = parabolStart - (i * step);
			// System.out.println("Y: "+ypoint);
			double res[] = inverseParabol(a, b, c, Math.abs(ypoint));
			// System.out.println("X: "+linearpoints[i]);
			if (ypoint < 0)
				linearpoints[i] = res[1] + shift;
			else
				linearpoints[i] = res[0] + shift;
		}

		linearpoints[intervals - 1] = endP;
		return linearpoints;
	}

	public static void main1(String[] args) {
		// double [] points = {1,1.2,1.3,2,5};
		double[] points = new double[20];
		for (int i = 0; i < 20; i++)
			points[i] = 10 * Math.random();

		double max = getMax(points);
		double min = getMin(points);
		System.out.println("<" + min + "," + max + ">");

		double[] interval = uniformDivide(max, min, points);

		double[] frequencies = calcFrequencies(interval, points);
		for (int i = 0; i < interval.length; i++) {
			System.out.print(interval[i] + " ");
			System.out.println("->" + frequencies[i] + " ");
		}
	}

	public static void main2(String[] args) {
		/*
		 * System.out.println("numbers to take: " + calcNumOfRepresentativeElements(100, 100)); double[] interp = linearInterpolation(27.27, 28.28, 3); double[] parabinterp = parabolicInterpolation(1, 10, 9); System.out.println("");
		 */
		int[] ii = takeChunks(11549, 11549/100);
		System.out.println("OK");
	}

	//distributes uniformly elements in parts
	public static int[] takeChunks(int numberOfElements, int partitionFactor) {
		int[] partitions = new int[1];
		if (partitionFactor <= 0) {
			return partitions;
		} else if (partitionFactor == 1) {
			partitions[0] = numberOfElements;
			return partitions;
		}

		int chunksize = numberOfElements / (partitionFactor);
		int rest = numberOfElements % (partitionFactor);
		if (chunksize == 0) {
			partitions = new int[numberOfElements];
			for (int i = 0; i < numberOfElements; i++) {
				partitions[i] = 1;
			}
		} else {
			partitions = new int[partitionFactor];
			for (int i = 0; i < partitionFactor; i++) {
				partitions[i] = chunksize;
			}

			for (int i = 0; i < rest; i++) {
				partitions[i]++;
			}

		}

		return partitions;
	}

	public static int chunkize(int numberOfElements, int partitionFactor) {
		int chunksize = numberOfElements / partitionFactor;
		int rest = numberOfElements % partitionFactor;
		if (chunksize == 0)
			chunksize = 1;
		else if (rest != 0)
			chunksize++;
		/*
		 * int numOfChunks = numberOfElements / chunksize; if ((numberOfElements % chunksize) != 0) numOfChunks += 1;
		 */

		return chunksize;
	}

	
	public static double[] uniformSampling(double min, double max, int maxElementsToTake){
		double step = (max-min)/(double)(maxElementsToTake-1);
		double [] samples = new double [maxElementsToTake];
		
		for (int i=0;i<samples.length;i++){
			double value = min+i*step;
			if (value>max)
				value=max;
			samples [i] = value;
		}
		
		return samples;
	}
	
	public static int[] uniformIntegerSampling(double min, double max, int maxElementsToTake){
		double step = (max-min)/(double)(maxElementsToTake-1);
		int [] samples = new int [maxElementsToTake];
		
		for (int i=0;i<samples.length;i++){
			double value = min+i*step;
			if (value>max)
				value=max;
			samples [i] = (int)value;
		}
		
		return samples;
	}
	
	public static void main(String[] args) {
		double [] samples = uniformSampling(0, 9, 10);
		System.out.println("OK");
	}
}
