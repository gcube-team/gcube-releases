package gr.cite.bluebridge.analytics.model;

import gr.cite.bluebridge.analytics.web.Parameters;

public class Economics {
	
	private int startYear;
	private int endYear;

	private Parameters parameters;
	private Values undepreciatedValues;
	private Values depreciatedValues;	
	
	public Economics() {}
	
	public Economics(int startYear, int endYear) {
		this.startYear = startYear;
		this.endYear = endYear;
		
		this.undepreciatedValues = new Values();
		this.depreciatedValues = new Values();
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
	
	public int getStartYear() {
		return startYear;
	}
	
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	
	public int getEndYear() {
		return endYear;
	}
	
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	
	public void printValues(Values values) {
		System.out.format("%26s", "Year");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16d", yearEntry.getYear());
		}
		System.out.println();
		System.out.format("%26s", "OA Cost");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getOACost());
		}
		System.out.println();
		System.out.format("%26s", "Total Shopping Cost");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getTotalShoppingCost());
		}
		System.out.println();
		System.out.format("%26s", "Expenses");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getExpenses());
		}
		System.out.println();
		System.out.format("%26s", "Income");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getIncome());
		}
		System.out.println();
		System.out.format("%26s", "Pre Tax Balance");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getPreTaxBalance());
		}
		System.out.println();
		System.out.format("%26s", "Cummulative Cost");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getCummulativeCost());
		}
		System.out.println();
		System.out.format("%26s", "Tax");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getTax());
		}
		System.out.println();
		System.out.format("%26s", "After Tax Balance");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getAfterTaxBalance());
		}
		System.out.println();
		System.out.format("%26s", "Cummulative GL");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getCummulativeGL());
		}
		System.out.println();
		System.out.format("%26s", "After Tax Cummulative GL");
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(i);
			System.out.format("%16.2f", yearEntry.getAfterTaxCummulativeGL());
		}
		System.out.println();
		System.out.println("NPV: " + values.getTargetIndicators().getNPV());
		System.out.println("IRR: " + values.getTargetIndicators().getIRR());
	}

	public void printAll(Model model) {	
		System.out.format("%90s", "Depreciated Shopping Cost");
		model.getDepreciatedShoppingCost().print(startYear, endYear);

		System.out.println("\n");

		System.out.format("%90s", "Shopping Cost");
		model.getShoppingCost().print(startYear, endYear);

		System.out.println("\n");

		System.out.format("%90s", "OA Cost");
		model.getOA().print(startYear, endYear);

		System.out.println("\n");

		System.out.format("%90s", "Item Cost");
		model.getItemCost().print(startYear, endYear);

		System.out.println("\n");

		System.out.format("%90s", "Product Price");
		model.getProductPrice().print(startYear, endYear);

		System.out.println("\n");

		System.out.format("%90s", "Revenue");
		model.getRevenue().print(startYear, endYear);
	}
}
