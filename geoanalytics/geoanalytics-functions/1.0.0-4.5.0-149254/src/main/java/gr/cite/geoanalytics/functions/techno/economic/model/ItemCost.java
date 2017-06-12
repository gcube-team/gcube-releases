package gr.cite.geoanalytics.functions.techno.economic.model;

import java.util.HashMap;
import java.util.Map;

public class ItemCost {
	private Map<Integer, YearEntry> yearEntries;

	public ItemCost() {
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
		
		private double year;
		private double cage;
		private double nets;
		private double anchorsSystem;
		private double autofeedingMachine;
		private double supportEquipment;
		private double feed;
		private double fry;

		public double getYear() {
			return year;
		}

		public void setYear(double year) {
			this.year = year;
		}

		public double getCage() {
			return cage;
		}

		public void setCage(double cage) {
			this.cage = cage;
		}

		public double getNets() {
			return nets;
		}

		public void setNets(double nets) {
			this.nets = nets;
		}

		public double getAnchorsSystem() {
			return anchorsSystem;
		}

		public void setAnchorsSystem(double anchorsSystem) {
			this.anchorsSystem = anchorsSystem;
		}

		public double getAutofeedingMachine() {
			return autofeedingMachine;
		}

		public void setAutofeedingMachine(double autofeedingMachine) {
			this.autofeedingMachine = autofeedingMachine;
		}

		public double getSupportEquipment() {
			return supportEquipment;
		}

		public void setSupportEquipment(double supportEquipment) {
			this.supportEquipment = supportEquipment;
		}

		public double getFeed() {
			return feed;
		}

		public void setFeed(double feed) {			
			this.feed = feed;
		}

		public double getFry() {
			return fry;
		}

		public void setFry(double fry) {
			this.fry = fry;
		}		
	}
	
	public void print(int startYear, int endYear){		
		System.out.println("\n");
		
		System.out.format("%15s", "Year");
		for (int year = startYear; year <= endYear; year++) {
			System.out.format("%15d", year);
		}		
		
		System.out.println("\n");

		System.out.format("%15s", "Cages");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f", yearEntry.getCage());
		}
		
		System.out.println();

		System.out.format("%15s", "Nets");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f",  yearEntry.getNets());
		}
		
		System.out.println();

		System.out.format("%15s", "Anchors");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f", yearEntry.getAnchorsSystem());
		}
		
		System.out.println();

		System.out.format("%15s", "AFgMachine");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f", yearEntry.getAutofeedingMachine());
		}
		
		System.out.println();
		
		System.out.format("%15s", "SupEquipment");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.2f", yearEntry.getSupportEquipment());
		}		
		
		System.out.println();
		
		System.out.format("%15s", "Feed");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.4f", yearEntry.getFeed());
		}		
		
		System.out.println();
		
		System.out.format("%15s", "Fry");
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = yearEntries.get(year);
			System.out.format("%15.4f", yearEntry.getFry());
		}	
	}
}
