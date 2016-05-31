package org.gcube.data.oai.tmplugin.requests;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.oai.tmplugin.utils.Constants;

/**
 * A {@link Request} to bind the OAI Plugin to a single data source formed of all the records in one ore more sets of an OAI repository.
 *  
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(namespace = Constants.NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class WrapRepositoryRequest extends Request implements Serializable{


	private static final long serialVersionUID = 1L;


	public WrapRepositoryRequest() {
	}

	/**
	 * Creates an instance with a given source identifier and the URL of a given OAI repository.
	 * @param id the source identifier
	 * @param url the URL
	 * @throws IllegalArgumentException if the identifier is null or empty
	 */
	public WrapRepositoryRequest(String url) throws IllegalArgumentException {

		super(url);

	}




	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WrapRepositoryRequest [name=");
		builder.append(super.getName());
		builder.append(", description=");
		builder.append(super.getDescription());
		builder.append(", url=");
		builder.append(super.getRepositoryUrl());
		builder.append(", alternativesXPath=");
		builder.append(super.getAlternativesXPath());
		builder.append(", contentXPath=");
		builder.append(super.getContentXPath());
		builder.append(", titleXPath=");
		builder.append(super.getTitleXPath());
		builder.append("]");
		return builder.toString();
	}


}
