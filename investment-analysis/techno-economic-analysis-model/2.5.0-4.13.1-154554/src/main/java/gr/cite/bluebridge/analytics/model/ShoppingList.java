package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.Map;

public class ShoppingList {
	private Map<Integer, YearEntry> yearEntries;

	public ShoppingList() {
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
		private int cage;
		private int nets;
		private int anchorsSystem;
		private int autofeedingMachine;
		private int supportEquipment;
		private double feed;
		private double fry;

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getCage() {
			return cage;
		}

		public void setCage(int cage) {
			this.cage = cage;
		}

		public int getNets() {
			return nets;
		}

		public void setNets(int nets) {
			this.nets = nets;
		}

		public int getAnchorsSystem() {
			return anchorsSystem;
		}

		public void setAnchorsSystem(int anchorsSystem) {
			this.anchorsSystem = anchorsSystem;
		}

		public int getAutofeedingMachine() {
			return autofeedingMachine;
		}

		public void setAutofeedingMachine(int autofeedingMachine) {
			this.autofeedingMachine = autofeedingMachine;
		}

		public int getSupportEquipment() {
			return supportEquipment;
		}

		public void setSupportEquipment(int supportEquipment) {
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
