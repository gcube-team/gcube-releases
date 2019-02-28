/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Class NamespaceCategories.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 19, 2017
 */
@XmlRootElement(name="namespaces")
@XmlAccessorType(XmlAccessType.FIELD)
public class NamespaceCategories {

	@XmlElement(name = "namespace")
	private List<NamespaceCategory> namespaceCategories;

	/**
	 * Instantiates a new namespace categories.
	 */
	public NamespaceCategories() {

	}

	/**
	 * Instantiates a new namespace categories.
	 *
	 * @param namespaceCategories the namespace categories
	 */
	public NamespaceCategories(List<NamespaceCategory> namespaceCategories) {
		this.namespaceCategories = namespaceCategories;
	}




	/**
	 * Gets the namespace categories.
	 *
	 * @return the namespaceCategories
	 */
	public List<NamespaceCategory> getNamespaceCategories() {

		return namespaceCategories;
	}




	/**
	 * Sets the namespace categories.
	 *
	 * @param namespaceCategories the namespaceCategories to set
	 */
	public void setNamespaceCategories(List<NamespaceCategory> namespaceCategories) {

		this.namespaceCategories = namespaceCategories;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("NamespaceCategories [namespaceCategories=");
		builder.append(namespaceCategories);
		builder.append("]");
		return builder.toString();
	}


}
