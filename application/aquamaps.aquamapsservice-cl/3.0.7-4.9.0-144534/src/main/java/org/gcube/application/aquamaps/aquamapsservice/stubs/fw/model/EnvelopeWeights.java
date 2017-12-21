package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;


@XmlRootElement(namespace=aquamapsTypesNS)
public class EnvelopeWeights {

	@XmlElement(namespace=aquamapsTypesNS)
	private String speciesId;
	@XmlElement(namespace=aquamapsTypesNS)
	private FieldArray weights;
	
	public EnvelopeWeights() {
		// TODO Auto-generated constructor stub
	}

	public EnvelopeWeights(String speciesId, FieldArray weights) {
		super();
		this.speciesId = speciesId;
		this.weights = weights;
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
	 * @return the weights
	 */
	public FieldArray weights() {
		return weights;
	}

	/**
	 * @param weights the weights to set
	 */
	public void weights(FieldArray weights) {
		this.weights = weights;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnvelopeWeights [speciesId=");
		builder.append(speciesId);
		builder.append(", weights=");
		builder.append(weights);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
