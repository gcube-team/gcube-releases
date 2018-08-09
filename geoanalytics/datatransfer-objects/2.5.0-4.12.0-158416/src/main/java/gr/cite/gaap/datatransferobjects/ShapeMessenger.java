package gr.cite.gaap.datatransferobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.io.WKTReader;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public class ShapeMessenger implements Serializable {

	private static final long serialVersionUID = 8084839922895154241L;

	private static Logger logger = LoggerFactory.getLogger(ShapeMessenger.class);

	private String id = null;
	private String code = null;
	private int shapeClass = -1;
	private String name = null;
	private String geometry = null;
	private String extraData = null;
	private String importId = null;
	private String layerId = null;
	private String creatorID = null;
	private String layerGeocodeSystem = null;

	public ShapeMessenger() {
		logger.trace("Initialized default contructor for ShapeMessenger");

	}

	public ShapeMessenger(Shape shape) {
		logger.trace("Initializing ShapeMessenger...");
		
		this.id = (shape.getId()==null) ? null : shape.getId().toString();
		this.code = shape.getCode();
		this.name = shape.getName();
		this.layerId = (shape.getLayerID()==null) ? null : shape.getLayerID().toString();
		if(shape.getGeography()!=null)
			this.geometry = shape.getGeography().toText();
		this.extraData = shape.getExtraData();
		if(shape.getCreatorID()!=null && !shape.getCreatorID().toString().isEmpty())
			this.creatorID = shape.getCreatorID().toString();
		this.setShapeClass(shape.getShapeClass());
		logger.trace("Initialized ShapeMessenger");

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getShapeClass() {
		return shapeClass;
	}

	public void setShapeClass(int shapeClass) {
		this.shapeClass = shapeClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

	public String getLayerId() {
		return layerId;
	}

	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	public String getLayerGeocodeSystem() {
		return layerGeocodeSystem;
	}

	public void setLayerGeocodeSystem(String layerGeocodeSystem) {
		this.layerGeocodeSystem = layerGeocodeSystem;
	}
	
	public String getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(String creatorID) {
		this.creatorID = creatorID;
	}

	
	public Shape toShape() throws Exception{
		Shape shape = new Shape();
		if(getId()!=null && !getId().isEmpty())
			shape.setId(UUID.fromString(getId()));
		shape.setCode(getCode());
		shape.setName(getName());
		shape.setShapeClass(getShapeClass());
		if(getGeometry()!=null && !getGeometry().isEmpty())
			shape.setGeography(new WKTReader().read(getGeometry()));
		shape.setExtraData(getExtraData());
		if(getLayerId()!=null && !getLayerId().isEmpty())
			shape.setLayerID(UUID.fromString(getLayerId()));
		shape.setCreationDate(new Date());
		shape.setLastUpdate(new Date());
		if(getCreatorID()!=null && !getCreatorID().isEmpty())
			shape.setCreatorID(UUID.fromString(getCreatorID()));
		return shape;
	}
	
	
	
}
