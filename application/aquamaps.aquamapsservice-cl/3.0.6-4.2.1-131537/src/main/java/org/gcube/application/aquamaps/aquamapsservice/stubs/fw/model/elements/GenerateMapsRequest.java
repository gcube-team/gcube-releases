package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;

@XmlRootElement(namespace=DM_target_namespace,name="generateMapsRequestType")
public class GenerateMapsRequest {

	@XmlElement(namespace=DM_target_namespace)
	private String author;
	@XmlElement(namespace=DM_target_namespace)
	private int HSPECId;
	@XmlElement(namespace=DM_target_namespace)
	private boolean generateLayers;
	@XmlElement(namespace=DM_target_namespace)
	private FieldArray speciesFilter;
	@XmlElement(namespace=DM_target_namespace)
	private boolean forceRegeneration;
	
	
	public GenerateMapsRequest() {
		// TODO Auto-generated constructor stub
	}


	public GenerateMapsRequest(String author, int hSPECId,
			boolean generateLayers, FieldArray speciesFilter,
			boolean forceRegeneration) {
		super();
		this.author = author;
		HSPECId = hSPECId;
		this.generateLayers = generateLayers;
		this.speciesFilter = speciesFilter;
		this.forceRegeneration = forceRegeneration;
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
	 * @return the hSPECId
	 */
	public int HSPECId() {
		return HSPECId;
	}


	/**
	 * @param hSPECId the hSPECId to set
	 */
	public void HSPECId(int hSPECId) {
		HSPECId = hSPECId;
	}


	/**
	 * @return the generateLayers
	 */
	public boolean generateLayers() {
		return generateLayers;
	}


	/**
	 * @param generateLayers the generateLayers to set
	 */
	public void generateLayers(boolean generateLayers) {
		this.generateLayers = generateLayers;
	}


	/**
	 * @return the speciesFilter
	 */
	public FieldArray speciesFilter() {
		return speciesFilter;
	}


	/**
	 * @param speciesFilter the speciesFilter to set
	 */
	public void speciesFilter(FieldArray speciesFilter) {
		this.speciesFilter = speciesFilter;
	}


	/**
	 * @return the forceRegeneration
	 */
	public boolean forceRegeneration() {
		return forceRegeneration;
	}


	/**
	 * @param forceRegeneration the forceRegeneration to set
	 */
	public void forceRegeneration(boolean forceRegeneration) {
		this.forceRegeneration = forceRegeneration;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GenerateMapsRequest [author=");
		builder.append(author);
		builder.append(", HSPECId=");
		builder.append(HSPECId);
		builder.append(", generateLayers=");
		builder.append(generateLayers);
		builder.append(", speciesFilter=");
		builder.append(speciesFilter);
		builder.append(", forceRegeneration=");
		builder.append(forceRegeneration);
		builder.append("]");
		return builder.toString();
	}
	
	
}
