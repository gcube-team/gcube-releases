package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GetSpeciesEnvelopeRequestType {

	@XmlElement
	private String speciesId;
	@XmlElement
	private int hspenId;
	
	
	public GetSpeciesEnvelopeRequestType() {
		// TODO Auto-generated constructor stub
	}


	public GetSpeciesEnvelopeRequestType(String speciesId, int hspenId) {
		super();
		this.speciesId = speciesId;
		this.hspenId = hspenId;
	}


	/**
	 * @return the speciesId
	 */
	public String speciesId() {
		return speciesId;
	}


	/**
	 * @param speciesId the speciesId to set
	 */
	public void speciesId(String speciesId) {
		this.speciesId = speciesId;
	}


	/**
	 * @return the hspenId
	 */
	public int hspenId() {
		return hspenId;
	}


	/**
	 * @param hspenId the hspenId to set
	 */
	public void hspenId(int hspenId) {
		this.hspenId = hspenId;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetSpeciesEnvelopeRequestType [speciesId=");
		builder.append(speciesId);
		builder.append(", hspenId=");
		builder.append(hspenId);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
