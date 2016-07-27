package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;

public class MonthlyFishingEffortCalculator {

	private static int monthsDifference(Date minDate, Date maxDate) {
		Calendar min = Calendar.getInstance();
		min.setTime(minDate);
		Calendar max = Calendar.getInstance();
		max.setTime(maxDate);
		
		int monthsDiff = (max.get(Calendar.YEAR) - min.get(Calendar.YEAR))*12 + (max.get(Calendar.MONTH) - min.get(Calendar.MONTH)) + ((max.get(Calendar.DAY_OF_MONTH) - min.get(Calendar.DAY_OF_MONTH)) / 27);

		return monthsDiff;
	}

	// positional Object: x,y,date,hours
	private double minEffort;
	private double maxEffort;

	public Map<String, Double> calculateMonthlyFishingEffor(List<Object> rows, Date minDate, Date maxDate) {

		HashMap<String, Double> csquare2month = new HashMap<String, Double>();
		HashMap<String, double[]> csquaremonths = new HashMap<String, double[]>();
		int numOfPoints = rows.size();
		int numOfMonths = monthsDifference(minDate, maxDate);
		 System.out.println("min:"+minDate+" max "+maxDate+" months diffs "+numOfMonths);

		for (int i = 0; i < numOfPoints; i++) {
			Object[] singleRow = (Object[]) rows.get(i);
			double x = Float.parseFloat(""+singleRow[0]);
			double y = Float.parseFloat(""+singleRow[1]);
			Date d = (Date) singleRow[2];
			double hours = (Float) singleRow[3];
			double speed = Double.parseDouble("" + singleRow[4]);

			if ((speed >= 2) && (speed <= 6)) {
				String csquare = CSquareCodesConverter.convertHalfDegree(x, y);
				double[] csquaremontheffort = csquaremonths.get(csquare);

				if (csquaremontheffort == null)
					csquaremontheffort = new double[numOfMonths + 1];

				int index = monthsDifference(minDate, d);
				csquaremontheffort[index] = csquaremontheffort[index] + hours;
				// update the map
				csquaremonths.put(csquare, csquaremontheffort);
			}
		}

		// calculate mean montly effort
		for (String keySquare : csquaremonths.keySet()) {

			double[] monthsEffort = csquaremonths.get(keySquare);

			double sum = 0;
			for (int i = 0; i < numOfMonths + 1; i++) {
				sum += monthsEffort[i];
			}
			sum = sum / (double) (numOfMonths + 1);
			sum = MathFunctions.roundDecimal(sum, 2);
			if (sum > maxEffort)
				maxEffort = sum;
			if (sum < minEffort)
				minEffort = sum;

			csquare2month.put(keySquare, sum);
		}

		return csquare2month;
	}

	public double getMinEffort() {
		return minEffort;
	}

	public void setMinEffort(double minEffort) {
		this.minEffort = minEffort;
	}

	public double getMaxEffort() {
		return maxEffort;
	}

	public void setMaxEffort(double maxEffort) {
		this.maxEffort = maxEffort;
	}

}
