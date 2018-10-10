package gr.cite.gaap.datatransferobjects;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapeSearchSelection {
	private static Logger logger = LoggerFactory.getLogger(ShapeSearchSelection.class);

	public enum GeoSearchType {
		None, BoundingBox, Proximity, Overlap
	}

	private List<String> terms = null;
	private List<String> importInstances = null;
	private UUID id = null;
	private String geometry = null;
	private GeoSearchType geoSearchType = GeoSearchType.None;
	
	

	public ShapeSearchSelection() {
		super();
		logger.trace("Initialized default contructor for ShapeSearchSelection");

	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	public List<String> getImportInstances() {
		return importInstances;
	}

	public void setImportInstances(List<String> importInstances) {
		this.importInstances = importInstances;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public GeoSearchType getGeoSearchType() {
		return geoSearchType;
	}

	public void setGeoSearchType(GeoSearchType geoSearchType) {
		this.geoSearchType = geoSearchType;
	}
}
