/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;



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

	public static final String LOCAL_NAME_METADATA_TYPE = "type";

	public static final String LOCAL_NAME_METADATA_FORMAT = "metadataformat";


	/**
	 * Instantiates a new metadata format.
	 */
	public MetadataFormat() {
	}

	@XmlElement(name = "metadatafield")
	private List<MetadataField> metadataFields;

	@XmlAttribute(name = LOCAL_NAME_METADATA_TYPE, required=true)
	private String type = "";

	@XmlTransient
	private String metadataSource = null;


	/**
	 * Gets the metadata fields.
	 *
	 * @return the metadataFields
	 */
	public List<MetadataField> getMetadataFields() {

		return metadataFields;
	}



	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {

		return type;
	}


	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {

		this.type = type;
	}

	/**
	 * Adds the metadata.
	 *
	 * @param Field the field
	 */
	public void addMetadata(MetadataField Field){
		if(this.metadataFields==null)
			this.metadataFields = new ArrayList<MetadataField>();

		this.metadataFields.add(Field);
	}


	/**
	 * Gets the metadata source.
	 *
	 * @return the metadata source
	 */
	public String getMetadataSource() {

		return metadataSource;
	}


	/**
	 * Sets the metadata source.
	 *
	 * @param metadataSource the new metadata source
	 */
	public void setMetadataSource(String metadataSource) {

		this.metadataSource = metadataSource;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataFormat [metadataFields=");
		builder.append(metadataFields);
		builder.append(", type=");
		builder.append(type);
		builder.append(", metadataSource=");
		builder.append(metadataSource);
		builder.append("]");
		return builder.toString();
	}


}
