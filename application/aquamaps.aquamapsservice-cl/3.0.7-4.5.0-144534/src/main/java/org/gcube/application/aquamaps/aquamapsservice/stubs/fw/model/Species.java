package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;

@XmlRootElement(namespace=aquamapsTypesNS, name="specie")
public class Species {
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String id;
	@XmlElement(namespace=aquamapsTypesNS)
	private FieldArray additionalField;
	
	public Species() {
		// TODO Auto-generated constructor stub
	}

	public Species(String id, FieldArray additionalField) {
		super();
		this.id = id;
		this.additionalField = additionalField;
	}

	/**
	 * @return the id
	 */
	public String id() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void id(String id) {
		this.id = id;
	}

	/**
	 * @return the additionalField
	 */
	public FieldArray additionalField() {
		return additionalField;
	}

	/**
	 * @param additionalField the additionalField to set
	 */
	public void additionalField(FieldArray additionalField) {
		this.additionalField = additionalField;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Species [id=");
		builder.append(id);
		builder.append(", additionalField=");
		builder.append(additionalField);
		builder.append("]");
		return builder.toString();
	}
	
	
}
