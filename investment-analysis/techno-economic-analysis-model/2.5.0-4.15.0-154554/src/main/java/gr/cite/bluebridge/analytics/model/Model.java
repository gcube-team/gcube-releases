package gr.cite.bluebridge.analytics.model;

import java.util.List;

public class Model {

	private int startYear;
	private int endYear;

	private ProductionParameters productionParameters;
	private Scenario scenario;
	private OA OA;
	private ShoppingList shoppingList;
	private ItemCost itemCost;
	private ShoppingCost shoppingCost;
	private ShoppingCost depreciatedShoppingCost;
	private EOL EOL;
	private ProductPrice productPrice;
	private ProductMix productMix;
	private Revenue revenue;
	private Economics economics;

	public Model(int startYear, int endYear) {
		this.productionParameters = new ProductionParameters();
		this.scenario = new Scenario();
		this.OA = new OA();
		this.shoppingList = new ShoppingList();
		this.itemCost = new ItemCost();
		this.shoppingCost = new ShoppingCost();
		this.depreciatedShoppingCost = new ShoppingCost();
		this.EOL = new EOL();
		this.productPrice = new ProductPrice();
		this.productMix = new ProductMix();
		this.revenue = new Revenue();
		this.startYear = startYear;
		this.endYear = endYear;
		this.economics = new Economics(startYear, endYear);
	}

	public void InitYearEntries(int startYear, int endYear, List<Fish> fishes) {
		this.getScenario().InitYearEntries(startYear, endYear);
		this.getOA().InitYearEntries(startYear, endYear);
		this.getShoppingList().InitYearEntries(startYear, endYear);
		this.getItemCost().InitYearEntries(startYear, endYear);
		this.getShoppingCost().InitYearEntries(startYear, endYear);
		this.getDepreciatedShoppingCost().InitYearEntries(startYear, endYear);
		this.getProductPrice().InitYearEntries(startYear, endYear, fishes);
		this.getProductMix().InitYearEntries(startYear, endYear, fishes);
		this.getRevenue().InitYearEntries(startYear, endYear, fishes);
		this.getEconomics().InitYearEntries(startYear, endYear);
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

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public OA getOA() {
		return OA;
	}

	public void setOA(OA oA) {
		OA = oA;
	}

	public ShoppingList getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(ShoppingList shoppingList) {
		this.shoppingList = shoppingList;
	}

	public ItemCost getItemCost() {
		return itemCost;
	}

	public void setItemCost(ItemCost itemCost) {
		this.itemCost = itemCost;
	}

	public ShoppingCost getShoppingCost() {
		return shoppingCost;
	}

	public void setShoppingCost(ShoppingCost shoppingCost) {
		this.shoppingCost = shoppingCost;
	}

	public ShoppingCost getDepreciatedShoppingCost() {
		return depreciatedShoppingCost;
	}

	public void setDepreciatedShoppingCost(ShoppingCost depreciatedShoppingCost) {
		this.depreciatedShoppingCost = depreciatedShoppingCost;
	}

	public EOL getEOL() {
		return EOL;
	}

	public void setEOL(EOL eOL) {
		EOL = eOL;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public ProductPrice getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(ProductPrice productPrice) {
		this.productPrice = productPrice;
	}

	public ProductMix getProductMix() {
		return productMix;
	}

	public void setProductMix(ProductMix productMix) {
		this.productMix = productMix;
	}

	public Revenue getRevenue() {
		return revenue;
	}

	public void setRevenue(Revenue revenue) {
		this.revenue = revenue;
	}

	public Economics getEconomics() {
		return economics;
	}

	public void setEconomics(Economics economics) {
		this.economics = economics;
	}
}
