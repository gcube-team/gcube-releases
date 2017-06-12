package gr.cite.geoanalytics.functions.techno.economic.model;

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
	
	public void print(int startYear, int endYear){
		System.out.println("\n");
		
		System.out.format("%15s", "Year");
		for (int year = startYear; year <= endYear; year++) {
			System.out.format("%15d", year);
		}		
		
		System.out.println("\n");

		System.out.format("%15s", "Licence");
		for (int year = startYear; year <= endYear; year++) {
			OA.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f", yearEntry.getLicense());
		}
		
		System.out.println();

		System.out.format("%15s", "Gen.Industrial");
		for (int year = startYear; year <= endYear; year++) {
			OA.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f",  yearEntry.getGeneralIndustrialExpenses());
		}
		
		System.out.println();

		System.out.format("%15s", "Packaging Cost");
		for (int year = startYear; year <= endYear; year++) {
			OA.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f",  yearEntry.getPackagingCost());
		}
		
		
		System.out.println("\n");
		
		System.out.format("%15s", "Total");
		for (int year = startYear; year <= endYear; year++) {
			OA.YearEntry yearEntry = yearEntries.get(year);
			double sum = 0;
			sum += yearEntry.getPackagingCost();
			sum += yearEntry.getLicense();
			sum += yearEntry.getGeneralIndustrialExpenses();
			System.out.format("%15.2f", sum);
		}
		
		double sum = 0;
		System.out.println();
		System.out.format("%15s", "Cumulative");
		for (int year = startYear; year <= endYear; year++) {
			OA.YearEntry yearEntry = yearEntries.get(year);
			sum += yearEntry.getPackagingCost();
			sum += yearEntry.getLicense();
			sum += yearEntry.getGeneralIndustrialExpenses();
			System.out.format("%15.2f", sum);
		}
	}
}
