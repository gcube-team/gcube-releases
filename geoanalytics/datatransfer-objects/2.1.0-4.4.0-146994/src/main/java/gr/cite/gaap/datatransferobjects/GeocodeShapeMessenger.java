package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeocodeShapeMessenger {
	private static Logger logger = LoggerFactory.getLogger(GeocodeShapeMessenger.class);

	private String id;
	private GeocodeMessenger geocodeMessenger;
	private ShapeMessenger shapeMessenger;
	private PrincipalMessenger principalMessenger;
	
	public GeocodeShapeMessenger(){ 
		logger.trace("Initialized default contructor for GeocodeShapeMessenger");
	}
	
	public GeocodeShapeMessenger(String id, GeocodeMessenger taxonomyTermMessenger, ShapeMessenger shapeMessenger, PrincipalMessenger principalMessenger){
		logger.trace("Initializing GeocodeShapeMessenger...");
		this.id = id;
		this.geocodeMessenger = taxonomyTermMessenger;
		this.shapeMessenger = shapeMessenger;
		this.principalMessenger = principalMessenger;
		logger.trace("Initialized GeocodeShapeMessenger");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GeocodeMessenger getGeocodeMessenger() {
		return geocodeMessenger;
	}

	public void setGeocodeMessenger(GeocodeMessenger geocodeMessenger) {
		this.geocodeMessenger = geocodeMessenger;
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
