package gr.cite.geoanalytics.execution;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;

import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

@Component
public class ModelLogicalLayer implements ModelLayer {

	private GeospatialBackend geospatialBackend;
	private TaxonomyManager taxonomyManager;
	private TaxonomyTerm layerTerm;
	
	public ModelLogicalLayer(TaxonomyTerm layerTerm) {
		this.layerTerm = layerTerm;
	}
	
	@Inject
	public void setLayerTerm(TaxonomyTerm layerTerm) {
		this.layerTerm = layerTerm;
	}
	
	public TaxonomyTerm getLayerTerm() {
		return layerTerm;
	}

	@Inject
	public void setGeospatialBackend(GeospatialBackend geospatialBackend) {
		this.geospatialBackend = geospatialBackend;
	}
	
	@Override
	public List<Shape> getGeometry() throws Exception {
		return geospatialBackend.getShapesOfLayer(layerTerm.getName(), layerTerm.getTaxonomy().getName());
	}

	@Override
	public Shape locate(TaxonomyTerm term) throws Exception {
		return geospatialBackend.getShapeFromLayerTermAndShapeTerm(layerTerm, term);
	}

	@Override
	public Shape locate(Geometry geometry) throws Exception {
		List<Shape> shapes = geospatialBackend.findShapesOfLayerEnclosingGeometry(geometry, layerTerm);
		if(shapes.size() > 1)
			throw new Exception("Found more than one shapes enclosing provided geometry for layer " + layerTerm.getName() + "(" + layerTerm.getId() + ")");
		if(shapes.isEmpty())
			return null;
		return shapes.get(0);
	}

}
