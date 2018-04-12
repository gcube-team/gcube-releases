package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.Map;

import gr.cite.bluebridge.analytics.web.Parameters;

public class Economics {
	
	@Override
	public String toString() {
		return "Economics [parameters=" + parameters + ", undepreciatedValues=" + undepreciatedValues + ", depreciatedValues=" + depreciatedValues + "]";
	}

	private Parameters parameters;
	private Values undepreciatedValues;
	private Values depreciatedValues;	

	public Economics() {
		undepreciatedValues = new Values();
		depreciatedValues = new Values();
	}
	
	public void InitYearEntries(int startYear, int endYear) {
		this.getUndepreciatedValues().InitYearEntries(startYear, endYear);
		this.getDepreciatedValues().InitYearEntries(startYear, endYear);
	}
	
	public Values getUndepreciatedValues() {
		return undepreciatedValues;
	}

	public void setUndepreciatedValues(Values undepreciatedValues) {
		this.undepreciatedValues = undepreciatedValues;
	}

	public Values getDepreciatedValues() {
		return depreciatedValues;
	}

	public void setDepreciatedValues(Values depreciatedValues) {
		this.depreciatedValues = depreciatedValues;
	}
	
	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
	public static class Values {

		@Override
		public String toString() {
			return "Values [targetIndicators=" + targetIndicators + ", yearEntries=" + yearEntries + "]";
		}

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
		
		public static class TargetIndicators {

			@Override
			public String toString() {
				return "TargetIndicators [irr=" + irr + ", npv=" + npv + "]";
			}

			private double irr;
			
			private double npv;//Net present value: https://support.d4science.org/projects/bluebridge/wiki/VRE_6_2_Specification#Net-Present-Value-NPV
			
			public double getIRR() {
				return this.irr;
			}

			public void setIRR(double irr) {
				this.irr = irr;
			}

			public double getNPV() {
				return this.npv;
			}

			public void setNPV(double npv) {
				this.npv = npv;
			}
		}
		
		public static class YearEntry {

			@Override
			public String toString() {
				return "YearEntry [year=" + year + ", OACost=" + OACost + ", totalShoppingCost=" + totalShoppingCost + ", expenses=" + expenses + ", income=" + income + ", preTaxBalance="
						+ preTaxBalance + ", cummulativeCost=" + cummulativeCost + ", tax=" + tax + ", afterTaxBalance=" + afterTaxBalance + ", cummulativeGL=" + cummulativeGL
						+ ", afterTaxCummulativeGL=" + afterTaxCummulativeGL + ", netProfitMargin=" + netProfitMargin + "]";
			}

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
}
