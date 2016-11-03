package gr.cite.geoanalytics.execution;

import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import java.util.List;

import javax.inject.Inject;

public class AttributeFunction implements CostFunction<Double>{
	
	private ShapeManager shapeManager;
	
	@Inject
	public void setShapeManager(ShapeManager shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Override
	public Double compute(String attribute, Shape shape) throws Exception {
		AttributeInfo attributeInfo = this.shapeManager.retrieveShapeAttribute(shape, attribute);
		if (attributeInfo != null && !attributeInfo.getValue().trim().equals(":")){		
			return Double.valueOf(Double.parseDouble(attributeInfo.getValue().trim()));
		}else{
			return 0D;
		}
	}

}
