package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.Map;

public class OA {
	private Map<Integer, YearEntry> yearEntries;

	public OA() {
		this.yearEntries = new HashMap<Integer, YearEntry>();
	}

	public void InitYearEntries(int startYear, int endYear) {
		for (int i = startYear; i <= endYear; i++) {
			YearEntry yearEntry = new YearEntry();
			yearEntry.setYear(i);
			this.yearEntries.put(i, yearEntry);
		}
	}

	public Map<Integer, YearEntry> getYearEntries() {
		return yearEntries;
	}

	public void setYearEntries(Map<Integer, YearEntry> yearEntries) {
		this.yearEntries = yearEntries;
	}

	public class YearEntry {
		private int year;
		private double license;
		private double generalIndustrialExpenses;
		private double packagingCost;

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public double getLicense() {
			return license;
		}

		public void setLicense(double license) {
			this.license = license;
		}

		public double getGeneralIndustrialExpenses() {
			return generalIndustrialExpenses;
		}

		public void setGeneralIndustrialExpenses(double generalIndustrialExpenses) {
			this.generalIndustrialExpenses = generalIndustrialExpenses;
		}

		public double getPackagingCost() {
			return packagingCost;
		}

		public void setPackagingCost(double packagingCost) {
			this.packagingCost = packagingCost;
		}
	}
}
