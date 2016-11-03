package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FileArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.SpeciesArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerArray;

@XmlRootElement(namespace=aquamapsTypesNS)
public class AquaMap {
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String boundingBox;
	@XmlElement(namespace=aquamapsTypesNS)
	private String name;
	@XmlElement(namespace=aquamapsTypesNS)
	private String author;
	@XmlElement(namespace=aquamapsTypesNS)
	private long date;
	@XmlElement(namespace=aquamapsTypesNS)
	private int id;
	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private SpeciesArray selectedSpecies;
	@XmlElement(namespace=aquamapsTypesNS)
	private LayerArray layers;
	@XmlElement(namespace=aquamapsTypesNS)
	private FileArray images;
	@XmlElement(namespace=aquamapsTypesNS)
	private FileArray additionalFiles;
	@XmlElement(namespace=aquamapsTypesNS)	
	private float threshold;
	@XmlElement(namespace=aquamapsTypesNS)
	private String status;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean gis;
	@XmlElement(namespace=aquamapsTypesNS)
	private String algorithmType;
	
	
	public AquaMap() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the boundingBox
	 */
	public String boundingBox() {
		return boundingBox;
	}


	/**
	 * @param boundingBox the boundingBox to set
	 */
	public void boundingBox(String boundingBox) {
		this.boundingBox = boundingBox;
	}


	/**
	 * @return the name
	 */
	public String name() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void name(String name) {
		this.name = name;
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
	 * @return the date
	 */
	public long date() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void date(long date) {
		this.date = date;
	}


	/**
	 * @return the id
	 */
	public int id() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void id(int id) {
		this.id = id;
	}


	/**
	 * @return the type
	 */
	public String type() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void type(String type) {
		this.type = type;
	}


	/**
	 * @return the selectedSpecies
	 */
	public SpeciesArray selectedSpecies() {
		return selectedSpecies;
	}


	/**
	 * @param selectedSpecies the selectedSpecies to set
	 */
	public void selectedSpecies(SpeciesArray selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}


	/**
	 * @return the layers
	 */
	public LayerArray layers() {
		return layers;
	}


	/**
	 * @param layers the layers to set
	 */
	public void layers(LayerArray layers) {
		this.layers = layers;
	}


	/**
	 * @return the images
	 */
	public FileArray images() {
		return images;
	}


	/**
	 * @param images the images to set
	 */
	public void images(FileArray images) {
		this.images = images;
	}


	/**
	 * @return the additionalFiles
	 */
	public FileArray additionalFiles() {
		return additionalFiles;
	}


	/**
	 * @param additionalFiles the additionalFiles to set
	 */
	public void additionalFiles(FileArray additionalFiles) {
		this.additionalFiles = additionalFiles;
	}


	/**
	 * @return the threshold
	 */
	public float threshold() {
		return threshold;
	}


	/**
	 * @param threshold the threshold to set
	 */
	public void threshold(float threshold) {
		this.threshold = threshold;
	}


	/**
	 * @return the status
	 */
	public String status() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void status(String status) {
		this.status = status;
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
	 * @return the algorithmType
	 */
	public String algorithmType() {
		return algorithmType;
	}


	/**
	 * @param algorithmType the algorithmType to set
	 */
	public void algorithmType(String algorithmType) {
		this.algorithmType = algorithmType;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AquaMap [boundingBox=");
		builder.append(boundingBox);
		builder.append(", name=");
		builder.append(name);
		builder.append(", author=");
		builder.append(author);
		builder.append(", date=");
		builder.append(date);
		builder.append(", id=");
		builder.append(id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", selectedSpecies=");
		builder.append(selectedSpecies);
		builder.append(", layers=");
		builder.append(layers);
		builder.append(", images=");
		builder.append(images);
		builder.append(", additionalFiles=");
		builder.append(additionalFiles);
		builder.append(", threshold=");
		builder.append(threshold);
		builder.append(", status=");
		builder.append(status);
		builder.append(", gis=");
		builder.append(gis);
		builder.append(", algorithmType=");
		builder.append(algorithmType);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
