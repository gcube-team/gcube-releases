package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.Map;

public class Values {

	private TargetIndicators targetIndicators;
	
	private Map<Integer, YearEntry> yearEntries;

	public Values() {
		targetIndicators = new TargetIndicators();
		yearEntries = new HashMap<Integer, YearEntry>();
	}
	
	public void InitYearEntries(int startYear, int endYear) {
		for (int i = startYear; i <= endYear; i++) {
			YearEntry yearEntry = new YearEntry();
			yearEntry.setYear(i);
			this.yearEntries.put(i, yearEntry);
		}
	}
	
	public TargetIndicators getTargetIndicators() {
		return targetIndicators;
	}

	public void setTargetIndicators(TargetIndicators targetIndicators) {
		this.targetIndicators = targetIndicators;
	}
	
	public Map<Integer, YearEntry> getYearEntries() {
		return yearEntries;
	}

	public void setYearEntries(Map<Integer, YearEntry> yearEntries) {
		this.yearEntries = yearEntries;
	}

	public class YearEntry {

		private int year;
		private double OACost;//Organizational & Administration Cost
		private double totalShoppingCost;
		private double expenses;
		private double income;
		
		private double preTaxBalance;
		private double cummulativeCost;
		private double tax;
		private double afterTaxBalance;
		private double cummulativeGL;//Cummulative Gain Loss
		private double afterTaxCummulativeGL;
		private double netProfitMargin;
		
		public void Calculate() {
			
		}
		
		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public double getOACost() {
			return OACost;
		}

		public void setOACost(double OACost) {
			this.OACost = OACost;
		}

		public double getTotalShoppingCost() {
			return totalShoppingCost;
		}

		public void setTotalShoppingCost(double totalShoppingCost) {
			this.totalShoppingCost = totalShoppingCost;
		}
		
		public double getExpenses() {
			return expenses;
		}

		public void setExpenses(double expenses) {
			this.expenses = expenses;
		}

		public double getIncome() {
			return income;
		}

		public void setIncome(double income) {
			this.income = income;
		}

		public double getPreTaxBalance() {
			return preTaxBalance;
		}

		public void setPreTaxBalance(double preTaxBalance) {
			this.preTaxBalance = preTaxBalance;
		}

		public double getCummulativeCost() {
			return cummulativeCost;
		}

		public void setCummulativeCost(double cummulativeCost) {
			this.cummulativeCost = cummulativeCost;
		}

		public double getTax() {
			return tax;
		}

		public void setTax(double tax) {
			this.tax = tax;
		}

		public double getAfterTaxBalance() {
			return afterTaxBalance;
		}

		public void setAfterTaxBalance(double afterTaxBalance) {
			this.afterTaxBalance = afterTaxBalance;
		}

		public double getCummulativeGL() {
			return cummulativeGL;
		}

		public void setCummulativeGL(double cummulativeGL) {
			this.cummulativeGL = cummulativeGL;
		}

		public double getAfterTaxCummulativeGL() {
			return afterTaxCummulativeGL;
		}

		public void setAfterTaxCummulativeGL(double afterTaxCummulativeGL) {
			this.afterTaxCummulativeGL = afterTaxCummulativeGL;
		}
		
		public double getNetProfitMargin() {
			return netProfitMargin;
		}

		public void setNetProfitMargin(double netProfitMargin) {
			this.netProfitMargin = netProfitMargin;
		}
	}
}
