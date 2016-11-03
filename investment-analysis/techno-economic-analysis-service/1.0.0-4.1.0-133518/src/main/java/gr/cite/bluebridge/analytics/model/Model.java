package gr.cite.bluebridge.analytics.model;

import java.util.List;

public class Model {

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
	
	public Model() {
		productionParameters = new ProductionParameters();
		scenario = new Scenario();
		OA = new OA();
		shoppingList = new ShoppingList();
		itemCost = new ItemCost();
		shoppingCost = new ShoppingCost();
		depreciatedShoppingCost = new ShoppingCost();
		EOL = new EOL();
		productPrice = new ProductPrice();
		productMix = new ProductMix();
		revenue = new Revenue();
		economics = new Economics();
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
	
	public void Calculate(Model model) {

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
