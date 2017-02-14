/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * The Class MetadataFormat.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
@XmlRootElement(name="metadataformat")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetadataFormat implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -5881074882343424963L;

	/**
	 *
	 */
	public MetadataFormat() {
	}

	@XmlElement(name = "metadatafield")
	private List<MetadataField> metadataFields;


	/**
	 * Gets the metadata fields.
	 *
	 * @return the metadataFields
	 */
	public List<MetadataField> getMetadataFields() {

		return metadataFields;
	}


	/**
	 * Sets the metadata fields.
	 *
	 * @param metadataFields the metadataFields to set
	 */
	public void setMetadataFields(List<MetadataField> metadataFields) {

		this.metadataFields = metadataFields;
	}

	/**
	 * Sets the metadatas.
	 *
	 * @param metadatas the metadatas to set
	 */
	public void setMetadatas(List<MetadataField> metadatas) {

		this.metadataFields = metadatas;
	}

	/**
	 * Adds the metadata.
	 *
	 * @param metadata the metadata
	 */
	public void addMetadata(MetadataField metadata){
		if(this.metadataFields==null)
			this.metadataFields = new ArrayList<MetadataField>();

		this.metadataFields.add(metadata);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataFormat [metadataFields=");
		builder.append(metadataFields);
		builder.append("]");
		return builder.toString();
	}
}
