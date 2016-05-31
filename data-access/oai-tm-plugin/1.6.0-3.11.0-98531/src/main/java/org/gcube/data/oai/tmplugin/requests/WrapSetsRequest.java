package org.gcube.data.oai.tmplugin.requests;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.oai.tmplugin.utils.Constants;

/**
 * A {@link Request} to bind the OAI Plugin to one or more sets of an OAI
 * repository.
 * 
 * @author Fabio Simeoni
 * 
 */
@XmlRootElement(namespace = Constants.NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class WrapSetsRequest extends Request implements Serializable {


	private static final long serialVersionUID = 1L;

	protected WrapSetsRequest() {
	}

	/**
	 * Creates an instance with the URL of an OAI repository and a prefix for
	 * the names of the sets to use as the names of the corresponding sources.
	 */
	public WrapSetsRequest(String url)
			throws IllegalArgumentException {

		super(url);
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WrapSetsRequest [url=");
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
	

	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {		
		
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;		
		
		return true;
	}


	
}
