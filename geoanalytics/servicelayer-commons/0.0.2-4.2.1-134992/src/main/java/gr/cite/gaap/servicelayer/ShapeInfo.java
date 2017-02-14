package gr.cite.gaap.servicelayer;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public class ShapeInfo {
	
	private Shape shape = null;
	private TaxonomyTerm term = null;

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public TaxonomyTerm getTerm() {
		return term;
	}

	public void setTerm(TaxonomyTerm term) {
		this.term = term;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof ShapeInfo))
			return false;
		
		ShapeInfo other = (ShapeInfo)obj;
		return this.getShape().equals(other.getShape()) &&
				this.getTerm().equals(other.getTerm());
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result += 37 * result + this.getShape().hashCode();
		result += 37 * result + this.getTerm().hashCode();
		return result;
	}
	
	public static class ShapeInfoMessenger{
		
		private ShapeMessenger shapeMessenger = new ShapeMessenger();
		private TaxonomyTermMessenger taxonomyTermMessenger = new TaxonomyTermMessenger();
		
		public ShapeInfoMessenger(){}
		
		public ShapeInfoMessenger(Shape shape, TaxonomyTerm taxonomyTerm) throws Exception{
			
			this.shapeMessenger.setCode(shape.getCode());
			this.shapeMessenger.setExtraData(shape.getExtraData());
			this.shapeMessenger.setGeometry(shape.getGeography().toText());
			this.shapeMessenger.setImportId(shape.getShapeImport().getShapeImport().toString());
			this.shapeMessenger.setName(shape.getName());
			this.shapeMessenger.setId(shape.getId().toString());
			
			this.taxonomyTermMessenger = new TaxonomyTermMessenger(taxonomyTerm);
			
		}
		
		public ShapeInfoMessenger(ShapeMessenger shapeMessenger, TaxonomyTermMessenger taxonomyTermMessenger){
			this.shapeMessenger = shapeMessenger;
			this.taxonomyTermMessenger = taxonomyTermMessenger;
		}
		
		public ShapeMessenger getShapeMessenger() {
			return shapeMessenger;
		}
		public void setShapeMessenger(ShapeMessenger shapeMessenger) {
			this.shapeMessenger = shapeMessenger;
		}

		public TaxonomyTermMessenger getTaxonomyTermMessenger() {
			return taxonomyTermMessenger;
		}

		public void setTaxonomyTermMessenger(TaxonomyTermMessenger taxonomyTermMessenger) {
			this.taxonomyTermMessenger = taxonomyTermMessenger;
		}
	}
}