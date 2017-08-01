/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.datacatalogue.metadatadiscovery.Namespace;



/**
 * The Class NamespaceCategory.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 19, 2017
 */
@XmlRootElement(name = "namespace")
@XmlAccessorType (XmlAccessType.FIELD)
public class NamespaceCategory implements Namespace, Serializable{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1632300992065668875L;

	@XmlAttribute(required = true)
	@XmlID
	private String id = null;

	@XmlElement(required = true)
	private String name = null;

	@XmlElement(required = true)
	private String title = null;

	private String description = "";

	/**
	 * Instantiates a new namespace category.
	 */
	public NamespaceCategory() {
	}



	/**
	 * Instantiates a new namespace category.
	 *
	 * @param id the id
	 * @param name the name
	 * @param title the title
	 */
	public NamespaceCategory(String id, String name, String title) {

		super();
		this.id = id;
		this.name = name;
		this.title = title;
	}


	/**
	 * Instantiates a new namespace category.
	 *
	 * @param id the id
	 * @param name the name
	 * @param title the title
	 * @param description the description
	 */
	public NamespaceCategory(String id, String name, String title, String description) {

		super();
		this.id = id;
		this.name = name;
		this.title = title;
		this.description = description;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {

		return id;
	}


	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {

		return title;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {

		return description;
	}


	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}


	/**
	 * Gets the namespace category qualifier name. The QName is a unique name to identify the category. It corresponds to {@link NamespaceCategory#id}
	 * @return the namespace category q name
	 */
	public String getNamespaceCategoryQName(){

		return id;
	}


	/**
	 * Sets the title.
	 *
	 * @param title the title to set
	 */
	public void setTitle(String title) {

		this.title = title;
	}


	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("NamespaceCategory [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}
