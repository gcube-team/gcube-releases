package gr.cite.geoanalytics.functions.techno.economic.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import gr.cite.geoanalytics.functions.techno.economic.model.*;

public class Evaluator {

	public static final int startYear = 2017;
	public static final int endYear = 2026;
	public static final int deduction = 5;

	public static final double irrGuess1 = 0.01d;
	public static final double irrGuess2 = 0.00d;

	public static final double PACKAGING_COST_PER_FISH = 0.22d;
	public static final double WEIGHT_PER_FISH = 419.38; // grams

	private Map<Integer, Integer> harvestedGenerationsPerYear = new HashMap<>();

	public static void main(String[] args) {
//		ModelInput input = new ModelInput();
//		Fish giltheadSeaBream = new Fish();
//		giltheadSeaBream.setFish("Sea Bream");
//		giltheadSeaBream.setMixPercent(100d);
//		giltheadSeaBream.setInitialPrice(5.20d);
//
//		input.setOffShoreAquaFarm(true);
//		input.getFishes().add(giltheadSeaBream);
//		input.setFeedPrice(1.15);
//		input.setFryPrice(0.20);
//		input.setTaxRate(29d);
//		input.setDiscountRate(3.75d);
//		input.setMaturity(18);
//
//		TreeMap<Integer, Double> inflationRate = new TreeMap<>();
//		inflationRate.put(2018, 0.65d);
//		input.setCustomInflationRateOnYear(inflationRate);
//
//		Consumption consumption = Consumption.getFixedData();
//		input.setConsumption(consumption);
//
//		Map<Integer, FryGeneration> generationsPerYear = new HashMap<>();
//		generationsPerYear.put(1, new FryGeneration(750000, 2.17));
//		generationsPerYear.put(4, new FryGeneration(750000, 2.17));
//		generationsPerYear.put(7, new FryGeneration(750000, 2.17));
//		generationsPerYear.put(10, new FryGeneration(750000, 2.17));
//
//		input.setGenerationsPerYear(generationsPerYear);
//
//		Economics economics = new Evaluator().calculate(input);
//		printValues(economics.getDepreciatedValues());
//		printValues(economics.getUndepreciatedValues());
	}

	public Economics calculate(ModelInput input) {
		Model model = new Model();
		model.InitYearEntries(startYear, endYear, input.getFishes());
		this.setUpProductionParameters(model, input);
		this.setUpEOL(model);
		this.calculateShoppingList(model, input);
		this.calculateItemCost(model, input);
		this.calculateShoppingCost(model);
		this.calcuateDepreciatedShoppingCost(model);
		this.calculateOACost(model, input);
		this.calculateProductPrice(input, model);
		this.calculateProductMix(input, model);
		this.calculateRevenue(input, model);
		this.calculateUndepreciatedValues(input, model);
		this.calculateDepreciatedValues(input, model);
		// printAll(model);
		return model.getEconomics();
	}

	public void setUpProductionParameters(Model model, ModelInput input) {
		model.getProductionParameters().setLargeCage(30);
		model.getProductionParameters().setSmallCage(20);
		model.getProductionParameters().setAnchorsSystem(3);
		model.getProductionParameters().setIsOffShoreAquaFarm(input.isOffShoreAquaFarm());
		model.getProductionParameters().setFeedNeedPerMonth(input.getFeedNeedPerMonth());
		model.getProductionParameters().setGenerationsPerYear(input.getGenerationsPerYear());
		model.getProductionParameters().setSupportEquipment(1);
		model.getProductionParameters().setPackagingPerFish(PACKAGING_COST_PER_FISH);
		model.getProductionParameters().setConsumption(input.getConsumption());
	}

	public void setUpEOL(Model model) {
		model.getEOL().setCage(20);
		model.getEOL().setNets(8);
		model.getEOL().setAnchorsSystem(15);
		model.getEOL().setAutofeedingMachine(Integer.MAX_VALUE);
		model.getEOL().setSupportEquipment(20);
	}

	public void calculateShoppingList(Model model, ModelInput input) {
		Map<Integer, Integer> feedNeedPerYear = this.calculateFeedNeedPerYear(model, input);
		Double fryNeedPerYear = this.calculateFryNeedPerYear(model);

		for (int year = startYear; year <= endYear; year++) {
			ShoppingList.YearEntry yearEntry = model.getShoppingList().getYearEntries().get(year);

			// Cage
			if ((year - startYear) % model.getEOL().getCage() == 0) {
				yearEntry.setCage(model.getProductionParameters().getLargeCage() + model.getProductionParameters().getSmallCage());
			} else {
				yearEntry.setCage(0);
			}

			// Nets
			if ((year - startYear) % model.getEOL().getNets() == 0) {
				yearEntry.setNets(model.getProductionParameters().getLargeCage() + model.getProductionParameters().getSmallCage());
			} else {
				yearEntry.setNets(0);
			}

			// Anchors System
			if ((year - startYear) % model.getEOL().getAnchorsSystem() == 0) {
				yearEntry.setAnchorsSystem(model.getProductionParameters().getAnchorsSystem());
			} else {
				yearEntry.setAnchorsSystem(0);
			}

			// Autofeeding Machine
			if ((year - startYear) % model.getEOL().getAutofeedingMachine() == 0 && model.getProductionParameters().getIsOffShoreAquaFarm()) {
				yearEntry.setAutofeedingMachine(1);
			} else {
				yearEntry.setAutofeedingMachine(0);
			}

			// Support Equipment
			if ((year - startYear) % model.getEOL().getSupportEquipment() == 0) {
				yearEntry.setSupportEquipment(model.getProductionParameters().getSupportEquipment());
			} else {
				yearEntry.setSupportEquipment(0);
			}

			yearEntry.setFeed(feedNeedPerYear.get(year)); // Feed Requirement
			yearEntry.setFry(fryNeedPerYear); // Fry Requirement
		}
	}

	public void calculateItemCost(Model model, ModelInput input) {
		for (int year = startYear; year <= endYear; year++) {
			ItemCost.YearEntry yearEntry = model.getItemCost().getYearEntries().get(year);

			if (year == startYear) {
				yearEntry.setCage(6000d);
				yearEntry.setNets(7200d);
				yearEntry.setAnchorsSystem(76667d);
				yearEntry.setAutofeedingMachine(1000000d);
				yearEntry.setSupportEquipment(500000d);
				yearEntry.setFeed(input.getFeedPrice());
				yearEntry.setFry(input.getFryPrice());
			} else {
				ItemCost.YearEntry previousYearEntry = model.getItemCost().getYearEntries().get(year - 1);
				double inflationRate = 1 + input.getInflationRateOnYear(year);
				yearEntry.setCage(previousYearEntry.getCage() * inflationRate);
				yearEntry.setNets(previousYearEntry.getNets() * inflationRate);
				yearEntry.setAnchorsSystem(previousYearEntry.getAnchorsSystem() * inflationRate);
				yearEntry.setAutofeedingMachine(previousYearEntry.getAutofeedingMachine() * inflationRate);
				yearEntry.setSupportEquipment(previousYearEntry.getSupportEquipment() * inflationRate);
				yearEntry.setFeed(previousYearEntry.getFeed() * inflationRate);
				yearEntry.setFry(previousYearEntry.getFry() * inflationRate);
			}
		}
	}

	public void calculateShoppingCost(Model model) {
		for (int year = startYear; year <= endYear; year++) {
			ShoppingCost.YearEntry yearEntry = model.getShoppingCost().getYearEntries().get(year);
			ItemCost.YearEntry itemCostYearEntry = model.getItemCost().getYearEntries().get(year);
			ShoppingList.YearEntry shoppingListYearEntry = model.getShoppingList().getYearEntries().get(year);

			yearEntry.setCage(itemCostYearEntry.getCage() * shoppingListYearEntry.getCage());
			yearEntry.setNets(itemCostYearEntry.getNets() * shoppingListYearEntry.getNets());
			yearEntry.setAnchorsSystem(itemCostYearEntry.getAnchorsSystem() * shoppingListYearEntry.getAnchorsSystem());
			yearEntry.setAutofeedingMachine(itemCostYearEntry.getAutofeedingMachine() * shoppingListYearEntry.getAutofeedingMachine());
			yearEntry.setSupportEquipment(itemCostYearEntry.getSupportEquipment() * shoppingListYearEntry.getSupportEquipment());
			yearEntry.setFeed(itemCostYearEntry.getFeed() * shoppingListYearEntry.getFeed());
			yearEntry.setFry(itemCostYearEntry.getFry() * shoppingListYearEntry.getFry());
		}
	}

	public void calcuateDepreciatedShoppingCost(Model model) {
		for (int year = startYear; year <= endYear; year++) {
			ShoppingCost.YearEntry shoppingCostYearEntry = model.getShoppingCost().getYearEntries().get(year);

			for (int j = 0; j < deduction; j++) {
				if (year + j > endYear) {
					continue;
				}

				ShoppingCost.YearEntry depreciatedYearEntry = model.getDepreciatedShoppingCost().getYearEntries().get(year + j);

				double cageCost = depreciatedYearEntry.getCage() + shoppingCostYearEntry.getCage() / deduction;
				double netsCost = depreciatedYearEntry.getNets() + shoppingCostYearEntry.getNets() / deduction;
				double anchorsSystemCost = depreciatedYearEntry.getAnchorsSystem() + shoppingCostYearEntry.getAnchorsSystem() / deduction;
				double autofeedingMachineCost = depreciatedYearEntry.getAutofeedingMachine() + shoppingCostYearEntry.getAutofeedingMachine() / deduction;
				double supportEquipmentCost = depreciatedYearEntry.getSupportEquipment() + shoppingCostYearEntry.getSupportEquipment() / deduction;

				depreciatedYearEntry.setCage(cageCost);
				depreciatedYearEntry.setNets(netsCost);
				depreciatedYearEntry.setAnchorsSystem(anchorsSystemCost);
				depreciatedYearEntry.setAutofeedingMachine(autofeedingMachineCost);
				depreciatedYearEntry.setSupportEquipment(supportEquipmentCost);
			}

			// We do not deduct the feed and fry expenses
			ShoppingCost.YearEntry depreciatedYearEntry = model.getDepreciatedShoppingCost().getYearEntries().get(year);
			depreciatedYearEntry.setFeed(shoppingCostYearEntry.getFeed());
			depreciatedYearEntry.setFry(shoppingCostYearEntry.getFry());
		}
	}

	public void calculateOACost(Model model, ModelInput input) {
		for (int year = startYear; year <= endYear; year++) {
			OA.YearEntry yearEntry = model.getOA().getYearEntries().get(year);
			if (year == startYear) {
				yearEntry.setGeneralIndustrialExpenses(600000d);
				yearEntry.setLicense(20000d);
			} else {
				OA.YearEntry previousYearEntry = model.getOA().getYearEntries().get(year - 1);
				double inflationRateThisYear = 1 + input.getInflationRateOnYear(year);
				double generalIndustrialExpenses = previousYearEntry.getGeneralIndustrialExpenses();
				yearEntry.setGeneralIndustrialExpenses(generalIndustrialExpenses * inflationRateThisYear);
			}
			// System.out.println("GeneralIndustrialExpenses for Year " + year + " = " + yearEntry.getGeneralIndustrialExpenses());
		}

		// Packaging Cost

		calculateFishWeightPerYear(model);
		double packagingCostPerFish = model.getProductionParameters().getPackagingPerFish();

		for (int year = startYear + 1; year <= endYear; year++) {
			OA.YearEntry yearEntry = model.getOA().getYearEntries().get(year);

			double inflationRate = 1 + input.getInflationRateOnYear(year);
			packagingCostPerFish = packagingCostPerFish * inflationRate;
			long fishCount = model.getScenario().getYearEntries().get(year).getFishCount();

			double packagingCostThisYear = packagingCostPerFish * fishCount;
			yearEntry.setPackagingCost(packagingCostThisYear);
			// System.out.println("Year " + year + " Packaging Cost = " + yearEntry.getPackagingCost());
		}
	}

	public void calculateProductPrice(ModelInput input, Model model) {
		for (int year = startYear; year <= endYear; year++) {
			Map<Fish, ProductPrice.YearEntry> yearEntry = model.getProductPrice().getYearEntries().get(year);

			for (Fish fish : input.getFishes()) {
				if (year == startYear) {
					yearEntry.get(fish).setPrice(fish.getInitialPrice());
				} else {
					Map<Fish, ProductPrice.YearEntry> previousYearEntry = model.getProductPrice().getYearEntries().get(year - 1);
					double previousYearPrice = previousYearEntry.get(fish).getPrice();
					double fishPrice = previousYearPrice * (1 + input.getInflationRateOnYear(year));
					yearEntry.get(fish).setPrice(fishPrice);
				}
			}
		}

		// Rounding of Prices after calculation

		for (int year = startYear; year <= endYear; year++) {
			Map<Fish, ProductPrice.YearEntry> yearEntry = model.getProductPrice().getYearEntries().get(year);
			for (Fish fish : input.getFishes()) {
				yearEntry.get(fish).setPrice(yearEntry.get(fish).getPrice());
				// System.out.println("Year " + year + " Fish Price = " + yearEntry.get(fish).getPrice());
			}
		}
	}

	public void calculateProductMix(ModelInput input, Model model) {
		for (int year = startYear; year <= endYear; year++) {
			Map<Fish, ProductMix.YearEntry> yearEntry = model.getProductMix().getYearEntries().get(year);

			for (Fish fish : input.getFishes()) {
				long kg = roundToLong(fish.getMixPercent() / 100 * model.getScenario().getYearEntries().get(year).getKG());
				yearEntry.get(fish).setKG(kg);
				// System.out.println("Year " + year + " Product Mix = " + kg);
			}
		}
	}

	public void calculateRevenue(ModelInput input, Model model) {
		for (int year = startYear; year <= endYear; year++) {
			Map<Fish, Revenue.YearEntry> yearEntry = model.getRevenue().getYearEntries().get(year);
			Map<Fish, ProductMix.YearEntry> productMixYearEntry = model.getProductMix().getYearEntries().get(year);
			Map<Fish, ProductPrice.YearEntry> productPriceYearEntry = model.getProductPrice().getYearEntries().get(year);

			for (Fish fish : input.getFishes()) {
				double revenue = productMixYearEntry.get(fish).getKG() * productPriceYearEntry.get(fish).getPrice();
				yearEntry.get(fish).setRevenue(revenue);
				// System.out.println("Year " + year + " Revenue = " + yearEntry.get(fish).getRevenue());
			}
		}
	}

	public void calculateUndepreciatedValues(ModelInput input, Model model) {
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = model.getEconomics().getUndepreciatedValues().getYearEntries().get(i);
			Values.YearEntry previousYearEntry = i == startYear ? null : model.getEconomics().getUndepreciatedValues().getYearEntries().get(i - 1);

			// OA Cost
			OA.YearEntry oaYearEntry = model.getOA().getYearEntries().get(i);
			yearEntry.setOACost(-(oaYearEntry.getLicense() + oaYearEntry.getGeneralIndustrialExpenses() + oaYearEntry.getPackagingCost()));

			// Total Shopping Cost
			ShoppingCost.YearEntry shoppingCostYearEntry = model.getShoppingCost().getYearEntries().get(i);
			yearEntry.setTotalShoppingCost(-(shoppingCostYearEntry.getCage() + shoppingCostYearEntry.getNets() + shoppingCostYearEntry.getAnchorsSystem()
					+ shoppingCostYearEntry.getAutofeedingMachine() + shoppingCostYearEntry.getSupportEquipment() + shoppingCostYearEntry.getFeed() + shoppingCostYearEntry.getFry()));

			// Expenses
			yearEntry.setExpenses(yearEntry.getOACost() + yearEntry.getTotalShoppingCost());

			// Income
			Map<Fish, Revenue.YearEntry> revenueYearEntry = model.getRevenue().getYearEntries().get(i);
			double income = 0;
			for (Fish fish : input.getFishes()) {
				income += revenueYearEntry.get(fish).getRevenue();
			}
			yearEntry.setIncome(income);

			// Pre Tax Balance
			yearEntry.setPreTaxBalance(yearEntry.getExpenses() + yearEntry.getIncome());

			// Cummulative Cost
			if (i == startYear) {
				yearEntry.setCummulativeCost(yearEntry.getOACost() + yearEntry.getTotalShoppingCost());
			} else {
				yearEntry.setCummulativeCost(previousYearEntry.getCummulativeCost() + yearEntry.getOACost() + yearEntry.getTotalShoppingCost());
			}

			// Tax
			yearEntry.setTax(yearEntry.getPreTaxBalance() > 0 ? -yearEntry.getPreTaxBalance() * input.getTaxRate() / 100 : 0);

			// After Tax Balance
			yearEntry.setAfterTaxBalance(yearEntry.getPreTaxBalance() + yearEntry.getTax());

			// Cummulative GL
			if (i == startYear) {
				yearEntry.setCummulativeGL(yearEntry.getPreTaxBalance());
			} else {
				yearEntry.setCummulativeGL(previousYearEntry.getCummulativeGL() + yearEntry.getPreTaxBalance());
			}

			// After Tax Cummulative GL
			if (i == startYear) {
				yearEntry.setAfterTaxCummulativeGL(yearEntry.getAfterTaxBalance());
			} else {
				yearEntry.setAfterTaxCummulativeGL(previousYearEntry.getAfterTaxCummulativeGL() + yearEntry.getAfterTaxBalance());
			}

			// Yearly Net Profit Margin
			yearEntry.setNetProfitMargin((yearEntry.getIncome() + yearEntry.getExpenses()) / (-yearEntry.getExpenses()));
		}

		// ############################## NPV - IRR ##############################
		{
			double npv = calculateNPV(model.getEconomics().getUndepreciatedValues(), input.getDiscountRate() / 100);
			model.getEconomics().getUndepreciatedValues().getTargetIndicators().setNPV(npv);

			// IRR = R1 + ((NPV1 x (R2 - R1)) / (NPV1 - NPV2)

			double irr = 0;
			double previousIrr;
			double guess1 = irrGuess1;
			double guess2 = irrGuess2;

			boolean firstIteration = true;
			// System.out.format("%10s %10s %10s\n", "guess1", "guess2", "IRR");
			do {
				previousIrr = irr;
				irr = calculateIRR(model.getEconomics().getUndepreciatedValues(), guess1, guess2);

				// System.out.format("%10.4f %10.4f %10.4f\n", guess1, guess2, irr);
				if (firstIteration) {
					firstIteration = false;
					guess2 = irr;
					continue;
				}

				double distanceGuess1Irr = Math.abs(guess1 - irr);
				double distanceGuess2Irr = Math.abs(guess2 - irr);

				int winner = Double.compare(distanceGuess1Irr, distanceGuess2Irr) < 0 ? 1 : 2;
				boolean lessThanRangeGuess1Guess2 = Double.compare(irr, guess1) < 0 && Double.compare(irr, guess2) < 0;
				boolean moreThanRangeGuess1Guess2 = Double.compare(irr, guess1) > 0 && Double.compare(irr, guess2) > 0;

				if (lessThanRangeGuess1Guess2 || moreThanRangeGuess1Guess2) {
					if (winner == 1) {
						guess2 = irr;
					} else {
						guess1 = irr;
					}
				} else {
					if (winner == 1) {
						guess2 = guess2 / 2;
					} else {
						guess1 = guess1 / 2;
					}
				}
			} while (Math.abs(previousIrr - irr) > 0.000009);
			model.getEconomics().getUndepreciatedValues().getTargetIndicators().setIRR(roundWithPrecision(irr, 4));
		}
	}

	public void calculateDepreciatedValues(ModelInput input, Model model) {
		for (int i = startYear; i <= endYear; i++) {
			Values.YearEntry yearEntry = model.getEconomics().getDepreciatedValues().getYearEntries().get(i);
			Values.YearEntry previousYearEntry = i == startYear ? null : model.getEconomics().getDepreciatedValues().getYearEntries().get(i - 1);

			// OA Cost
			OA.YearEntry oaYearEntry = model.getOA().getYearEntries().get(i);
			yearEntry.setOACost(-(oaYearEntry.getLicense() + oaYearEntry.getGeneralIndustrialExpenses() + oaYearEntry.getPackagingCost()));

			// Total Shopping Cost
			ShoppingCost.YearEntry depreciatedShoppingCostYearEntry = model.getDepreciatedShoppingCost().getYearEntries().get(i);
			yearEntry.setTotalShoppingCost(-(depreciatedShoppingCostYearEntry.getCage() + depreciatedShoppingCostYearEntry.getNets() + depreciatedShoppingCostYearEntry.getAnchorsSystem()
					+ depreciatedShoppingCostYearEntry.getAutofeedingMachine() + depreciatedShoppingCostYearEntry.getSupportEquipment() + depreciatedShoppingCostYearEntry.getFeed()
					+ depreciatedShoppingCostYearEntry.getFry()));

			// Expenses
			yearEntry.setExpenses(yearEntry.getOACost() + yearEntry.getTotalShoppingCost());

			// Total Revenue
			Map<Fish, Revenue.YearEntry> revenueYearEntry = model.getRevenue().getYearEntries().get(i);
			double income = 0;
			for (Fish fish : input.getFishes()) {
				income += revenueYearEntry.get(fish).getRevenue();
			}
			yearEntry.setIncome(income);

			// Pre Tax Balance
			yearEntry.setPreTaxBalance(yearEntry.getExpenses() + yearEntry.getIncome());

			// Cummulative Cost
			if (i == startYear) {
				yearEntry.setCummulativeCost(yearEntry.getOACost() + yearEntry.getTotalShoppingCost());
			} else {
				yearEntry.setCummulativeCost(previousYearEntry.getCummulativeCost() + yearEntry.getOACost() + yearEntry.getTotalShoppingCost());
			}

			// Tax
			yearEntry.setTax(yearEntry.getPreTaxBalance() > 0 ? -yearEntry.getPreTaxBalance() * input.getTaxRate() / 100 : 0);

			// After Tax Balance
			yearEntry.setAfterTaxBalance(yearEntry.getPreTaxBalance() + yearEntry.getTax());

			// Cummulative GL
			if (i == startYear) {
				yearEntry.setCummulativeGL(yearEntry.getPreTaxBalance());
			} else {
				yearEntry.setCummulativeGL(previousYearEntry.getCummulativeGL() + yearEntry.getPreTaxBalance());
			}

			// After Tax Cummulative GL
			if (i == startYear) {
				yearEntry.setAfterTaxCummulativeGL(yearEntry.getAfterTaxBalance());
			} else {
				yearEntry.setAfterTaxCummulativeGL(previousYearEntry.getAfterTaxCummulativeGL() + yearEntry.getAfterTaxBalance());
			}

			// Yearly Net Profit Margin
			yearEntry.setNetProfitMargin((yearEntry.getIncome() + yearEntry.getExpenses()) / (-yearEntry.getExpenses()));
		}

		// ############################## NPV - IRR ##############################
		{
			double npv = calculateNPV(model.getEconomics().getDepreciatedValues(), input.getDiscountRate() / 100);
			model.getEconomics().getDepreciatedValues().getTargetIndicators().setNPV(npv);

			// IRR = R1 + ((NPV1 x (R2 - R1)) / (NPV1 - NPV2)

			double irr = 0;
			double previousIrr;
			double guess1 = irrGuess1;
			double guess2 = irrGuess2;

			boolean firstIteration = true;
			// System.out.format("%10s %10s %10s\n", "guess1", "guess2", "IRR");
			do {
				previousIrr = irr;
				irr = calculateIRR(model.getEconomics().getDepreciatedValues(), guess1, guess2);

				// System.out.format("%10.4f %10.4f %10.4f\n", guess1, guess2, irr);
				if (firstIteration) {
					firstIteration = false;
					guess2 = irr;
					continue;
				}

				double distanceGuess1Irr = Math.abs(guess1 - irr);
				double distanceGuess2Irr = Math.abs(guess2 - irr);

				int winner = Double.compare(distanceGuess1Irr, distanceGuess2Irr) < 0 ? 1 : 2;
				boolean lessThanRangeGuess1Guess2 = Double.compare(irr, guess1) < 0 && Double.compare(irr, guess2) < 0;
				boolean moreThanRangeGuess1Guess2 = Double.compare(irr, guess1) > 0 && Double.compare(irr, guess2) > 0;

				if (lessThanRangeGuess1Guess2 || moreThanRangeGuess1Guess2) {
					if (winner == 1) {
						guess2 = irr;
					} else {
						guess1 = irr;
					}
				} else {
					if (winner == 1) {
						guess2 = guess2 / 2;
					} else {
						guess1 = guess1 / 2;
					}
				}
			} while (Math.abs(previousIrr - irr) > 0.000009);
			model.getEconomics().getDepreciatedValues().getTargetIndicators().setIRR(roundWithPrecision(irr, 4));
		}
	}

	private double calculateIRR(Values values, double guess1, double guess2) {
		double npv1 = calculateNPV(values, guess1);
		double npv2 = calculateNPV(values, guess2);
		return guess1 + ((npv1 * (guess2 - guess1)) / (npv1 - npv2));
	}

	private double calculateNPV(Values values, double rate) {
		double npv = values.getYearEntries().get(startYear).getAfterTaxBalance();
		int pow = 1;
		// System.out.println("Guess " + rate );
		for (int year = startYear + 1; year <= endYear; year++) {
			Values.YearEntry yearEntry = values.getYearEntries().get(year);

			double yearRate = 1 / Math.pow(1 + rate, pow);

			npv = npv + yearEntry.getAfterTaxBalance() * yearRate;

			pow++;
			// System.out.println( " Year " + year + " value = " + yearEntry.getAfterTaxBalance() * yearRate);
		}
		return npv;
	}

	private Map<Integer, Integer> calculateFeedNeedPerYear(Model model, ModelInput input) {
		Map<Integer, Integer> feedNeedPerYear = new HashMap<>();
		Map<Integer, Integer> feedNeedPerMonth = model.getProductionParameters().getFeedNeedPerMonth();
		Set<Integer> generationsPerYear = model.getProductionParameters().getGenerationsPerYear().keySet();

		int monthsToMaturity = input.getMaturity();
		List<Integer> generationsAlive = new ArrayList<>();

		for (int year = startYear; year <= endYear; year++) {
			// create N fish generations per year

			for (int generationMonth : generationsPerYear) {
				generationsAlive.add(generationMonth + monthsToMaturity - 1);
			}

			// simulate year passage

			int feedNeedThisYear = 0;
			int harvestedGenerationsThisYear = 0;

			for (ListIterator<Integer> iterator = generationsAlive.listIterator(); iterator.hasNext();) {
				int listIndex = iterator.nextIndex();
				Integer fishGenerationMonth = iterator.next();

				for (int month = 1; month <= 12; month++) {
					if (fishGenerationMonth > 0) {
						if (monthsToMaturity + 1 - fishGenerationMonth > 0) {
							feedNeedThisYear += feedNeedPerMonth.get(monthsToMaturity + 1 - fishGenerationMonth);
						}

						fishGenerationMonth--;

						if (fishGenerationMonth > 0) {
							generationsAlive.set(listIndex, fishGenerationMonth);
						} else {
							harvestedGenerationsThisYear++;
							iterator.remove();
							break;
						}
					}
				}
			}

			feedNeedPerYear.put(year, feedNeedThisYear);
			harvestedGenerationsPerYear.put(year, harvestedGenerationsThisYear);
		}

		return feedNeedPerYear;
	}

	private Double calculateFryNeedPerYear(Model model) {
		Map<Integer, FryGeneration> generationsPerYear = model.getProductionParameters().getGenerationsPerYear();
		double fryNeedPerYear = 0;

		for (Map.Entry<Integer, FryGeneration> entry : generationsPerYear.entrySet()) {
			FryGeneration fryGeneration = entry.getValue();
			fryNeedPerYear += fryGeneration.getCount();
		}

		// System.out.println("Fry Need per Year = " + fryNeedPerYear);

		return fryNeedPerYear;
	}

	public void calculateFishWeightPerYear(Model model) {
		int totalBiomass = model.getProductionParameters().getConsumption().getTotalBiomassPerGeneration();
		for (int year = startYear; year <= endYear; year++) {
			double totalFishWeight = (totalBiomass * harvestedGenerationsPerYear.get(year)) / 1000d;
			long fishCount = Math.round(totalBiomass * harvestedGenerationsPerYear.get(year) / WEIGHT_PER_FISH);

			model.getScenario().getYearEntries().get(year).setKG(totalFishWeight);
			model.getScenario().getYearEntries().get(year).setFishCount(fishCount);
			// System.out.println("year = " + year + " totalFishWeight = " + totalFishWeight);
		}
	}

	public static void printValues(Values values) {
		// Print
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

	private void printAll(Model model) {
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

	private double roundWithPrecision(double value, int roundPrecision) {
		int pow = 10;
		for (int i = 1; i < roundPrecision; i++) {
			pow *= 10;
		}
		double tmp = value * pow;
		return (double) (int) ((tmp - (int) tmp) >= 0.5d ? tmp + 1 : tmp) / pow;
	}

	private long roundToLong(double value) {
		return (long) Math.round(value);
	}
}
