package gr.cite.geoanalytics.functions.techno.economic.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductPrice {
	private Map<Integer, Map<Fish, YearEntry>> yearEntries;

	public ProductPrice() {
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
		private double price;

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

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}	
	}
	
	public void print(int startYear, int endYear){
		System.out.println("\n");
		
		System.out.format("%16s", "Year");
		for (int year = startYear; year <= endYear; year++) {
			System.out.format("%15d", year);
		}			
		System.out.println();
		Set<Fish> allFish = yearEntries.get(startYear).keySet();

		for (Fish fish : allFish) {
			System.out.format("%16s", fish.getFish());
			for (int year = startYear; year <= endYear; year++) {
				ProductPrice.YearEntry yearEntry = yearEntries.get(year).get(fish);
				System.out.format("%15.2f", yearEntry.getPrice());
			}
			System.out.println();
		}	
	}
}
