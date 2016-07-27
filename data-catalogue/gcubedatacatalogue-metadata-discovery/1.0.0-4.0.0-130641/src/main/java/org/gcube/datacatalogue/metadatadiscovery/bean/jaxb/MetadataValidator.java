/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Class MetadataValidator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
@XmlRootElement(name = "metadatavalidator")
@XmlAccessorType (XmlAccessType.FIELD)
public class MetadataValidator implements Serializable{

	private static final long serialVersionUID = -5394122378302593873L;
	private String regularExpression;

	/**
	 * Instantiates a new metadata validator.
	 */
	public MetadataValidator() {

	}

	/**
	 * Instantiates a new metadata validator.
	 *
	 * @param regularExpression the regular expression
	 */
	public MetadataValidator(String regularExpression) {

		super();
		this.regularExpression = regularExpression;
	}


	/**
	 * Gets the regular expression.
	 *
	 * @return the regularExpression
	 */
	public String getRegularExpression() {

		return regularExpression;
	}


	/**
	 * Sets the regular expression.
	 *
	 * @param regularExpression the regularExpression to set
	 */
	public void setRegularExpression(String regularExpression) {

		this.regularExpression = regularExpression;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataValidator [regularExpression=");
		builder.append(regularExpression);
		builder.append("]");
		return builder.toString();
	}


}
