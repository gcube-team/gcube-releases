package gr.cite.gaap.datatransferobjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

public class ShapeImportInfo {
	private UUID importId = null;
	private Bounds boundingBox = null;
	private String layerName = null;

	private Map<String, Set<String>> valueMappingValues = new HashMap<String, Set<String>>();

	public ShapeImportInfo() {
	}

	public ShapeImportInfo(UUID importId, Bounds boundingBox) {
		this.importId = importId;
		this.boundingBox = boundingBox;
	}

	public ShapeImportInfo(UUID importId, Bounds boundingBox, Map<String, Set<String>> valueMappingValues) {
		this(importId, boundingBox);
		if (valueMappingValues != null)
			this.valueMappingValues = valueMappingValues;
	}

	public UUID getImportId() {
		return importId;
	}

	public void setImportId(UUID importId) {
		this.importId = importId;
	}

	public Bounds getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(Bounds boundingBox) {
		this.boundingBox = boundingBox;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public Map<String, Set<String>> getValueMappingValues() {
		return valueMappingValues;
	}

	public void setValueMappingValues(Map<String, Set<String>> autoValueMappingValues) {
		this.valueMappingValues = autoValueMappingValues;
	}

	public void addValueMappingValues(String attribute, Set<String> values) {
		this.valueMappingValues.put(attribute, values);
	}
}
