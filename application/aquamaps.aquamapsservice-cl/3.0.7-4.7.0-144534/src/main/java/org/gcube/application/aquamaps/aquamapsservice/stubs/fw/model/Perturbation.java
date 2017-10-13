package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=aquamapsTypesNS)
public class Perturbation {

	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private String field;
	@XmlElement(namespace=aquamapsTypesNS)
	private String toPerturbId;
	@XmlElement(namespace=aquamapsTypesNS)
	private String value;
	
	public Perturbation() {
		// TODO Auto-generated constructor stub
	}

	public Perturbation(String type, String field, String toPerturbId,
			String value) {
		super();
		this.type = type;
		this.field = field;
		this.toPerturbId = toPerturbId;
		this.value = value;
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
	 * @return the field
	 */
	public String field() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void field(String field) {
		this.field = field;
	}

	/**
	 * @return the toPerturbId
	 */
	public String toPerturbId() {
		return toPerturbId;
	}

	/**
	 * @param toPerturbId the toPerturbId to set
	 */
	public void toPerturbId(String toPerturbId) {
		this.toPerturbId = toPerturbId;
	}

	/**
	 * @return the value
	 */
	public String value() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void value(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Perturbation [type=");
		builder.append(type);
		builder.append(", field=");
		builder.append(field);
		builder.append(", toPerturbId=");
		builder.append(toPerturbId);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
