package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCost {
	private Map<Integer, YearEntry> yearEntries;

	public ShoppingCost() {
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
		private double cage = 0d;
		private double nets = 0d;
		private double anchorsSystem = 0d;
		private double autofeedingMachine = 0d;
		private double supportEquipment = 0d;
		private double feed = 0d;
		private double fry = 0d;

		public double getYear() {
			return year;
		}

		public void setYear(int year) {
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
}
