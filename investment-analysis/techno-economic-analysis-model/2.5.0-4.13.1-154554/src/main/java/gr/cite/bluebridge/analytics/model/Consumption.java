package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Consumption {
	private List<Monthly> monthly;
	private List<Daily> daily;

	// Monthly Food Consumption

	public static class Monthly {
		private String month;
		private double food;

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public double getFood() {
			return food;
		}

		public void setFood(double food) {
			this.food = food;
		}
	}

	// Daily Food Consumption

	public static class Daily {
		private String day;
		private int bm;
		private int bmdead;
		private double fcre;
		private double fcrb;
		private double food;
		private double mortality;

		public double getMortality() {
			return mortality;
		}

		public void setMortality(double mortality) {
			this.mortality = mortality;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public int getBm() {
			return bm;
		}

		public void setBm(int bm) {
			this.bm = bm;
		}

		public double getFcre() {
			return fcre;
		}

		public void setFcre(double fcre) {
			this.fcre = fcre;
		}

		public double getFcrb() {
			return fcrb;
		}

		public void setFcrb(double fcrb) {
			this.fcrb = fcrb;
		}

		public double getFood() {
			return food;
		}

		public void setFood(double food) {
			this.food = food;
		}

		public int getBmdead() {
			return bmdead;
		}

		public void setBmdead(int bmdead) {
			this.bmdead = bmdead;
		}
	}

	public Map<Integer, Integer> getFeedNeedPerMonth() {
		Map<Integer, Integer> feedNeedPerMonth = new TreeMap<>();
		Map<Integer, Integer> years = new HashMap<>();

		for (Daily dailyConsumption : daily) {
			String[] date = dailyConsumption.getDay().split("/");

			int year = Integer.parseInt(date[2]);
			if (!years.containsKey(year)) {
				years.put(year, 12 * years.size());
			}
			int month = Integer.parseInt(date[1]) + years.get(year);

			int feedNeed = 0;
			if (feedNeedPerMonth.containsKey(month)) {
				feedNeed = feedNeedPerMonth.get(month);
			}
			feedNeed += dailyConsumption.getFood();
			feedNeedPerMonth.put(month, feedNeed);
		}

		return feedNeedPerMonth;
	}

	public Map<Integer, Double> getFeedNeedPerMonth1() {
		Map<Integer, Double> feedNeedPerMonth = new TreeMap<>();
		Map<Integer, Integer> years = new HashMap<>();

		for (Monthly monthlyConsumption : monthly) {
			String[] date = monthlyConsumption.getMonth().split("/");

			int year = Integer.parseInt(date[1]);
			if (!years.containsKey(year)) {
				years.put(year, 12 * years.size());
			}
			int month = Integer.parseInt(date[0]) + years.get(year);

			feedNeedPerMonth.put(month, monthlyConsumption.getFood());
		}

		return feedNeedPerMonth;
	}

	public int getTotalBiomassPerGeneration() {
		return daily.get(daily.size() - 1).getBm();
	}

	public List<Monthly> getMonthly() {
		return monthly;
	}

	public void setMonthly(List<Monthly> monthly) {
		this.monthly = monthly;
	}

	public List<Daily> getDaily() {
		return daily;
	}

	public void setDaily(List<Daily> daily) {
		this.daily = daily;
	}
}
