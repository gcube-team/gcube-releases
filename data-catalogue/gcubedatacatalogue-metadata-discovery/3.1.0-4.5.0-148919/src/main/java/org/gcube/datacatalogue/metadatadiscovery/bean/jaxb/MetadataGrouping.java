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
 * The Class MetadataGrouping.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 21, 2017
 */
@XmlRootElement(name = "grouping")
@XmlAccessorType (XmlAccessType.FIELD)
public class MetadataGrouping implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 4684835556193102991L;

	@XmlAttribute
	private Boolean create = false;

	@XmlAttribute
	private Boolean propagateUp = false;

    @XmlJavaTypeAdapter(TaggingGroupingAdapter.class)
    @XmlValue
	private TaggingGroupingValue groupingValue = null;

	/**
	 * Instantiates a new metadata grouping.
	 */
	public MetadataGrouping() {
	}


	/**
	 * Instantiates a new metadata grouping.
	 *
	 * @param create the create
	 * @param groupingValue the grouping value
	 */
	public MetadataGrouping(Boolean create, TaggingGroupingValue groupingValue) {

		this.create = create;
		this.groupingValue = groupingValue;
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
	 * Gets the grouping value.
	 *
	 * @return the groupingValue
	 */
	public TaggingGroupingValue getGroupingValue() {

		return groupingValue;
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
	 * Sets the grouping value.
	 *
	 * @param groupingValue the groupingValue to set
	 */
	public void setGroupingValue(TaggingGroupingValue groupingValue) {

		this.groupingValue = groupingValue;
	}



	/**
	 * @return the propagateUp
	 */
	public Boolean getPropagateUp() {

		return propagateUp;
	}



	/**
	 * @param propagateUp the propagateUp to set
	 */
	public void setPropagateUp(Boolean propagateUp) {

		this.propagateUp = propagateUp;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataGrouping [create=");
		builder.append(create);
		builder.append(", propagateUp=");
		builder.append(propagateUp);
		builder.append(", groupingValue=");
		builder.append(groupingValue);
		builder.append("]");
		return builder.toString();
	}


}
