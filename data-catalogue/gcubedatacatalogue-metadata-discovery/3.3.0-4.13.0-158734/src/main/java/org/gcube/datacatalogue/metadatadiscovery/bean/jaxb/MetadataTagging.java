/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.datacatalogue.metadatadiscovery.adapter.TaggingGroupingAdapter;


/**
 * The Class MetadataTagging.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 21, 2017
 */
@XmlRootElement(name = "tagging")
@XmlAccessorType (XmlAccessType.FIELD)
public class MetadataTagging implements Serializable{


	public static final String DEFAULT_SEPARATOR = "-";

	/**
	 *
	 */
	private static final long serialVersionUID = 641412896487091012L;

	@XmlAttribute
	private Boolean create = false;

	@XmlAttribute
	private String separator = DEFAULT_SEPARATOR;

    @XmlJavaTypeAdapter(TaggingGroupingAdapter.class)
    @XmlValue
	private TaggingGroupingValue taggingValue = null;



	/**
	 * Instantiates a new metadata tagging.
	 */
	public MetadataTagging() {
	}


	/**
	 * Instantiates a new metadata tagging.
	 *
	 * @param create the create
	 * @param separator the separator
	 * @param taggingValue the tagging value
	 */
	public MetadataTagging(Boolean create, String separator, TaggingGroupingValue taggingValue) {

		super();
		this.create = create;
		this.separator = separator;
		this.taggingValue = taggingValue;
	}


	/**
	 * Gets the creates the.
	 *
	 * @return the create
	 */
	public Boolean getCreate() {

		return create;
	}


	/**
	 * Gets the separator.
	 *
	 * @return the separator
	 */
	public String getSeparator() {

		return separator;
	}


	/**
	 * Gets the tagging value.
	 *
	 * @return the taggingValue
	 */
	public TaggingGroupingValue getTaggingValue() {

		return taggingValue;
	}


	/**
	 * Sets the creates the.
	 *
	 * @param create the create to set
	 */
	public void setCreate(Boolean create) {

		this.create = create;
	}


	/**
	 * Sets the separator.
	 *
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {

		this.separator = separator;
	}


	/**
	 * Sets the tagging value.
	 *
	 * @param taggingValue the taggingValue to set
	 */
	public void setTaggingValue(TaggingGroupingValue taggingValue) {

		this.taggingValue = taggingValue;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataTagging [create=");
		builder.append(create);
		builder.append(", separator=");
		builder.append(separator);
		builder.append(", taggingValue=");
		builder.append(taggingValue);
		builder.append("]");
		return builder.toString();
	}


}
