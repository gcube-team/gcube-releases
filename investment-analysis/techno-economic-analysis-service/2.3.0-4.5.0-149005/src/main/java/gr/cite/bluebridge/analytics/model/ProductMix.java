package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductMix {
	private Map<Integer, Map<Fish, YearEntry>> yearEntries;

	public ProductMix() {
		this.yearEntries = new HashMap<Integer, Map<Fish, YearEntry>>();
	}

	public void InitYearEntries(int startYear, int endYear, List<Fish> fishes) {
		for (int i = startYear; i <= endYear; i++) {
			Map<Fish, YearEntry> fishYearEntry = new HashMap<Fish, YearEntry>();
			for(Fish fish : fishes){
				YearEntry yearEntry = new YearEntry();
				yearEntry.setYear(i);
				yearEntry.setFish(fish);
				fishYearEntry.put(fish, yearEntry);
			}
			this.yearEntries.put(i, fishYearEntry);
		}
	}

	public Map<Integer, Map<Fish, YearEntry>> getYearEntries() {
		return yearEntries;
	}

	public void setYearEntries(Map<Integer, Map<Fish, YearEntry>> yearEntries) {
		this.yearEntries = yearEntries;
	}

	public class YearEntry {

		private double year;
		private Fish fish;
		private double kg;

		public double getYear() {
			return year;
		}

		public void setYear(double year) {
			this.year = year;
		}
		
		public Fish getFish() {
			return fish;
		}

		public void setFish(Fish fish) {
			this.fish = fish;
		}

		public double getKG() {
			return kg;
		}

		public void setKG(double kg) {
			this.kg = kg;
		}
	}
}
