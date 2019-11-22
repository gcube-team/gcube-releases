package org.gcube.data.simulfishgrowthdata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.MoreObjects;

public class UnivariateOutlierDetector {

	static public interface IValue extends Comparator, Comparable {
		public Double getValue();
	}

	static public class SimpleValue implements IValue {
		protected Double value;

		public SimpleValue(Double value) {
			this.value = value;
		}

		public Double getValue() {
			return value;
		}

		@Override
		public int compare(Object o1, Object o2) {
			if (o2 == null)
				return 1;
			if (o1 == null)
				return -1;
			return ((SimpleValue) o1).getValue().compareTo(((SimpleValue) o2).getValue());
		}

		@Override
		public String toString() {
			return String.valueOf(getValue());
		}

		@Override
		public int compareTo(Object o) {
			return compare(this, o);
		}

	}

	final List<IValue> values;
	List<IValue> outliers;

	Double lowerPercentage;
	Double upperPercentage;

	public UnivariateOutlierDetector() {
		values = new ArrayList<>();
	}

	public List<IValue> getOutliers() {
		return outliers;
	}

	public List<IValue> getValues() {
		return values;
	}

	public UnivariateOutlierDetector cleanFromOutliers() {
		values.removeAll(outliers);
		return this;
	}

	public UnivariateOutlierDetector addValues(final List<IValue> moreValues) {
		this.values.addAll(moreValues);
		return this;
	}

	public UnivariateOutlierDetector cleanOutliers() {
		outliers.clear();
		return this;
	}

	public UnivariateOutlierDetector cleanValues() {
		values.clear();
		return this;
	}

	public UnivariateOutlierDetector defineLowerPercentage(final Double lowerPercentage) {
		this.lowerPercentage = lowerPercentage;
		return this;
	}

	public UnivariateOutlierDetector defineUpperPercentage(final Double upperPercentage) {
		this.upperPercentage = upperPercentage;
		return this;
	}

	// Calculate the Quartiles
	protected Double quartile(final IValue[] v, final Double Percent) {
		int n = (int) Math.round((float) (v.length) * Percent / 100.0);

		return v[n].getValue();

	}

	/**
	 * detect outliers in values
	 * 
	 * @param values
	 * @param lowBound
	 * @param upperBound
	 * @return
	 */
	protected List<IValue> detectOutliers(final IValue[] values, final Double lowBound, final Double upperBound) {
		/*
		 * Detect Outliers: for each value of each variable in the data, check
		 * it whether it is laid outside the bounds
		 */
		int vlen = values.length;
		// ArrayList<double[]> outliers_list = new ArrayList<double[]>();
		List<IValue> toRet = new ArrayList<IValue>();

		for (int i = 0; i < vlen; i++) {
			if (values[i].getValue() < lowBound || values[i].getValue() > upperBound) {
				toRet.add(values[i]);
			}
		}
		return (toRet);
	}

	public UnivariateOutlierDetector execute() throws Exception {

		if (values == null || values.isEmpty()) {
			throw new Exception("The data array either is null or does not contain any data.");
		}

		IValue sortedValues[] = new IValue[values.size()];
		sortedValues = values.toArray(sortedValues);
		Arrays.sort(sortedValues);
		System.out.println("sorted " + Arrays.toString(sortedValues));

		// call function quartile() to calculate Q1 and Q3
		Double Q1 = quartile(sortedValues, lowerPercentage);
		Double Q3 = quartile(sortedValues, upperPercentage);

		System.out.println("Q1 = " + Q1 + " , " + "Q3 = " + Q3);

		// calculate the IQR = Q3-Q1
		Double IQR = Q3 - Q1;

		// calculate the lower and upper bound
		Double lowOutlierBound = Q1 - 3.0 * IQR;
		Double upOutlierBound = Q3 + 3.0 * IQR;

		System.out.println("lowOutlierBound = " + lowOutlierBound + "," + "upOutlierBound = " + upOutlierBound);

		// Detect outliers of an array v
		outliers = detectOutliers(sortedValues, lowOutlierBound, upOutlierBound);

		return this;
	}

	// ******************************
	// Main program

	public static void main(String[] args) {

		Double lowerperc = 25.0;
		Double upperperc = 75.0;

		// Read data from file
		ArrayList<IValue> values_list = new ArrayList<IValue>(Arrays.asList(new SimpleValue(4.0), new SimpleValue(17.0),
				new SimpleValue(7.0), new SimpleValue(7.5), new SimpleValue(14.0), new SimpleValue(18.0),
				new SimpleValue(100.5), new SimpleValue(12.0), new SimpleValue(3.0), new SimpleValue(16.0),
				new SimpleValue(0.0), new SimpleValue(-250.45), new SimpleValue(10.0), new SimpleValue(4.0),
				new SimpleValue(4.0), new SimpleValue(11.0), new SimpleValue(1000.0), new SimpleValue(-240.0),
				new SimpleValue(650.0), new SimpleValue(750.0), new SimpleValue(5.4), new SimpleValue(600.75),
				new SimpleValue(-290.0)));

		try {
			UnivariateOutlierDetector detector = new UnivariateOutlierDetector().addValues(values_list)
					.defineLowerPercentage(lowerperc).defineUpperPercentage(upperperc).execute();
			detector.cleanFromOutliers();

			// Print results
			System.out.println("outliers:");
			List<IValue> outliers = detector.getOutliers();
			for (int i = 0; i < outliers.size(); i++) {
				System.out.print(outliers.get(i) + " ");
			}

			System.out.println();

			List<IValue> values = detector.getValues();
			for (int i = 0; i < values.size(); i++) {
				System.out.print(values.get(i) + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end main
}
