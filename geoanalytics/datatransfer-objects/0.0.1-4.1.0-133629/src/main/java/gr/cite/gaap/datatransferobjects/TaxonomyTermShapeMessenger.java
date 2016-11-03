package gr.cite.gaap.datatransferobjects;

public class TaxonomyTermShapeMessenger {

	private String id;
	private TaxonomyTermMessenger taxonomyTermMessenger;
	private ShapeMessenger shapeMessenger;
	private PrincipalMessenger principalMessenger;
	
	public TaxonomyTermShapeMessenger(){ }
	
	public TaxonomyTermShapeMessenger(String id, TaxonomyTermMessenger taxonomyTermMessenger, ShapeMessenger shapeMessenger, PrincipalMessenger principalMessenger){
		this.id = id;
		this.taxonomyTermMessenger = taxonomyTermMessenger;
		this.shapeMessenger = shapeMessenger;
		this.principalMessenger = principalMessenger;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TaxonomyTermMessenger getTaxonomyTermMessenger() {
		return taxonomyTermMessenger;
	}

	public void setTaxonomyTermMessenger(TaxonomyTermMessenger taxonomyTermMessenger) {
		this.taxonomyTermMessenger = taxonomyTermMessenger;
	}

	public ShapeMessenger getShapeMessenger() {
		return shapeMessenger;
	}

	public void setShapeMessenger(ShapeMessenger shapeMessenger) {
		this.shapeMessenger = shapeMessenger;
	}

	public PrincipalMessenger getPrincipalMessenger() {
		return principalMessenger;
	}

	public void setPrincipalMessenger(PrincipalMessenger principalMessenger) {
		this.principalMessenger = principalMessenger;
	}
}
