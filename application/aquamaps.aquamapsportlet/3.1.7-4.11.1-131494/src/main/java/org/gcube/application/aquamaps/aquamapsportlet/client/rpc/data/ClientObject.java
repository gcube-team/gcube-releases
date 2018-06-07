package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.LocalObjectFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;


import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientObject implements IsSerializable {
	private String name="";
	private String author="";
	private Integer id;	
	private ClientObjectType type=ClientObjectType.Biodiversity;
	private ClientField selectedSpecies=new ClientField(LocalObjectFields.species+"","0",ClientFieldType.INTEGER);
	private Boolean gis=false;
	private Float threshold=0.5f;	
	private BoundingBox boundingBox=new BoundingBox();
	private Map<String,String> images=new HashMap<String, String>(); 
	
	//*************** Additional DETAILS for published objects
	private String layerUrl;
	private String layerName;
	private String localBasePath;
	
	private String algorithmType;
	
	public void setAlgorithmType(String algorithmType) {
		this.algorithmType = algorithmType;
	}
	
	public String getAlgorithmType() {
		return algorithmType;
	}
	
	public String getLocalBasePath() {
		return localBasePath;
	}
	public void setLocalBasePath(String localBasePath) {
		this.localBasePath = localBasePath;
	}
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	
	public void setLayerUrl(String layerUrl) {
		this.layerUrl = layerUrl;
	}
	
	public String getLayerName() {
		return layerName;
	}
	public String getLayerUrl() {
		return layerUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ClientObjectType getType() {
		return type;
	}
	public void setType(ClientObjectType type) {
		this.type = type;
	}
	public Boolean getGis() {
		return gis;
	}
	public void setGis(Boolean gis) {
		this.gis = gis;
	}
	public Float getThreshold() {
		return threshold;
	}
	public void setThreshold(Float threshold) {
		this.threshold = threshold;
	}
	

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	public void setImages(Map<String,String> images) {
		this.images = images;
	}
	public Map<String,String> getImages() {
		return images;
	}
	public void setSelectedSpecies(ClientField selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}
	public ClientField getSelectedSpecies() {
		return selectedSpecies;
	}
	
}
