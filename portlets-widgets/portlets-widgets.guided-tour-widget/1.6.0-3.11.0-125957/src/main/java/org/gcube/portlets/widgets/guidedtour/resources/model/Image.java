/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
@XmlRootElement(name="image")
@XmlAccessorType(XmlAccessType.NONE)
public class Image {
	
	@XmlAttribute(name="url", required=true)
	protected String url;
	
	public Image(){}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Image [url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}
}
