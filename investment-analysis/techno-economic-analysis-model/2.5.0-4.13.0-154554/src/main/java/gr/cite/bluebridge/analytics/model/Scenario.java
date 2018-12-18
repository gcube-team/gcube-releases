package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.Map;

public class Scenario {
	private Map<Integer, YearEntry> yearEntries;

	public Scenario() {
		this.yearEntries = new HashMap<Integer, YearEntry>();
	}

	public void InitYearEntries(int startYear, int endYear) {
		for (int i = startYear; i <= endYear; i++) {
			YearEntry yearEntry = new YearEntry();
			yearEntry.setYear(i);
			this.yearEntries.put(i, yearEntry);
		}
	}

	public void Calculate(Model model) {

	}

	public Map<Integer, YearEntry> getYearEntries() {
		return yearEntries;
	}

	public void setYearEntries(Map<Integer, YearEntry> yearEntries) {
		this.yearEntries = yearEntries;
	}

	public class YearEntry {
		private int year;
		private Double kg;
		private long fishCount;

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public double getKG() {
			return kg;
		}

		public long getFishCount() {
			return fishCount;
		}

		public void setFishCount(long fishCount) {
			this.fishCount = fishCount;
		}

		public Double getKg() {
			return kg;
		}

		public void setKg(Double kg) {
			this.kg = kg;
		}

		public void setKG(double kg) {
			this.kg = kg;
		}
	}
}
