package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public class ShapeMessenger {
	private String id = null;
	private String code = null;
	private int shapeClass = -1;
	private String name = null;
	private String geometry = null;
	private String extraData = null;
	private String importId = null;
	private String termName = null;
	private String termTaxonomy = null;

	public ShapeMessenger() {
	}

	public ShapeMessenger(Shape shape) {
		this.id = shape.getId().toString();
		this.code = shape.getCode();
		this.name = shape.getName();
		this.geometry = shape.getGeography().toText();
		this.extraData = shape.getExtraData();
		this.importId = shape.getShapeImport().getShapeImport().toString();

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

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getTermTaxonomy() {
		return termTaxonomy;
	}

	public void setTermTaxonomy(String termTaxonomy) {
		this.termTaxonomy = termTaxonomy;
	}

}
