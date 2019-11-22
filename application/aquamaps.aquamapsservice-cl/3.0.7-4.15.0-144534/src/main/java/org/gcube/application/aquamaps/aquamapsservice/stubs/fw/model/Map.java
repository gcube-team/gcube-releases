package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FileArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerInfoType;

@XmlRootElement(namespace=aquamapsTypesNS)
public class Map {

	@XmlElement(namespace=aquamapsTypesNS)
	private LayerInfoType gisLayer;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean gis;
	@XmlElement(namespace=aquamapsTypesNS)
	private String title;
	@XmlElement(namespace=aquamapsTypesNS)
	private String mapType;
	@XmlElement(namespace=aquamapsTypesNS)
	private FileArray staticImages;
	@XmlElement(namespace=aquamapsTypesNS)
	private Resource resource;
	@XmlElement(namespace=aquamapsTypesNS)
	private String coverage;
	@XmlElement(namespace=aquamapsTypesNS)
	private long creationDate;
	@XmlElement(namespace=aquamapsTypesNS)
	private String author;
	@XmlElement(namespace=aquamapsTypesNS)
	private String fileSetId;
	@XmlElement(namespace=aquamapsTypesNS)
	private String layerId;
	@XmlElement(namespace=aquamapsTypesNS)
	private String speciesListCSV;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean custom;
	
	
	public Map() {
		// TODO Auto-generated constructor stub
	}


	public Map(LayerInfoType gisLayer, boolean gis, String title,
			String mapType, FileArray staticImages, Resource resource,
			String coverage, long creationDate, String author,
			String fileSetId, String layerId, String speciesListCSV,
			boolean custom) {
		super();
		this.gisLayer = gisLayer;
		this.gis = gis;
		this.title = title;
		this.mapType = mapType;
		this.staticImages = staticImages;
		this.resource = resource;
		this.coverage = coverage;
		this.creationDate = creationDate;
		this.author = author;
		this.fileSetId = fileSetId;
		this.layerId = layerId;
		this.speciesListCSV = speciesListCSV;
		this.custom = custom;
	}


	/**
	 * @return the gisLayer
	 */
	public LayerInfoType gisLayer() {
		return gisLayer;
	}


	/**
	 * @param gisLayer the gisLayer to set
	 */
	public void gisLayer(LayerInfoType gisLayer) {
		this.gisLayer = gisLayer;
	}


	/**
	 * @return the gis
	 */
	public boolean gis() {
		return gis;
	}


	/**
	 * @param gis the gis to set
	 */
	public void gis(boolean gis) {
		this.gis = gis;
	}


	/**
	 * @return the title
	 */
	public String title() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void title(String title) {
		this.title = title;
	}


	/**
	 * @return the mapType
	 */
	public String mapType() {
		return mapType;
	}


	/**
	 * @param mapType the mapType to set
	 */
	public void mapType(String mapType) {
		this.mapType = mapType;
	}


	/**
	 * @return the staticImages
	 */
	public FileArray staticImages() {
		return staticImages;
	}


	/**
	 * @param staticImages the staticImages to set
	 */
	public void staticImages(FileArray staticImages) {
		this.staticImages = staticImages;
	}


	/**
	 * @return the resource
	 */
	public Resource resource() {
		return resource;
	}


	/**
	 * @param resource the resource to set
	 */
	public void resource(Resource resource) {
		this.resource = resource;
	}


	/**
	 * @return the coverage
	 */
	public String coverage() {
		return coverage;
	}


	/**
	 * @param coverage the coverage to set
	 */
	public void coverage(String coverage) {
		this.coverage = coverage;
	}


	/**
	 * @return the creationDate
	 */
	public long creationDate() {
		return creationDate;
	}


	/**
	 * @param creationDate the creationDate to set
	 */
	public void creationDate(long creationDate) {
		this.creationDate = creationDate;
	}


	/**
	 * @return the author
	 */
	public String author() {
		return author;
	}


	/**
	 * @param author the author to set
	 */
	public void author(String author) {
		this.author = author;
	}


	/**
	 * @return the fileSetId
	 */
	public String fileSetId() {
		return fileSetId;
	}


	/**
	 * @param fileSetId the fileSetId to set
	 */
	public void fileSetId(String fileSetId) {
		this.fileSetId = fileSetId;
	}


	/**
	 * @return the layerId
	 */
	public String layerId() {
		return layerId;
	}


	/**
	 * @param layerId the layerId to set
	 */
	public void layerId(String layerId) {
		this.layerId = layerId;
	}


	/**
	 * @return the speciesListCSV
	 */
	public String speciesListCSV() {
		return speciesListCSV;
	}


	/**
	 * @param speciesListCSV the speciesListCSV to set
	 */
	public void speciesListCSV(String speciesListCSV) {
		this.speciesListCSV = speciesListCSV;
	}


	/**
	 * @return the custom
	 */
	public boolean custom() {
		return custom;
	}


	/**
	 * @param custom the custom to set
	 */
	public void custom(boolean custom) {
		this.custom = custom;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Map [gisLayer=");
		builder.append(gisLayer);
		builder.append(", gis=");
		builder.append(gis);
		builder.append(", title=");
		builder.append(title);
		builder.append(", mapType=");
		builder.append(mapType);
		builder.append(", staticImages=");
		builder.append(staticImages);
		builder.append(", resource=");
		builder.append(resource);
		builder.append(", coverage=");
		builder.append(coverage);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", author=");
		builder.append(author);
		builder.append(", fileSetId=");
		builder.append(fileSetId);
		builder.append(", layerId=");
		builder.append(layerId);
		builder.append(", speciesListCSV=");
		builder.append(speciesListCSV);
		builder.append(", custom=");
		builder.append(custom);
		builder.append("]");
		return builder.toString();
	}
	
	
}
