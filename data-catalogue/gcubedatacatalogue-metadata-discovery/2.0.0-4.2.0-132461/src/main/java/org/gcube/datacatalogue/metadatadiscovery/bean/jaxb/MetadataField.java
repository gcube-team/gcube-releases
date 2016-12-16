/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.datacatalogue.metadatadiscovery.adapter.DataTypeAdapter;



/**
 * The Class MetadataField.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
@XmlRootElement(name="metadatafield")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetadataField implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5935573474465015727L;

	@XmlElement(required = true)
	private String fieldName;
    @XmlElement(required = true)
	private Boolean mandatory = false;
//	private Boolean isBoolean = false;
    @XmlJavaTypeAdapter(DataTypeAdapter.class)
	private DataType dataType = DataType.String;
	private String defaultValue;
	private String note;

	//It's the list of eligible values;
	@XmlElement(name = "vocabulary")
	private MetadataVocabulary vocabulary;

	@XmlElement(name = "validator")
	private MetadataValidator validator;

	/**
	 * Instantiates a new metadata field.
	 */
	public MetadataField() {
	}

	/**
	 * @param fieldName
	 * @param mandatory
	 */
	public MetadataField(String fieldName, Boolean mandatory) {

		super();
		this.fieldName = fieldName;
		this.mandatory = mandatory;
	}


	/**
	 * @return the fieldName
	 */
	public String getFieldName() {

		return fieldName;
	}


	/**
	 * @return the mandatory
	 */
	public Boolean getMandatory() {

		return mandatory;
	}


	/**
	 * @return the dataType
	 */
	public DataType getDataType() {

		return dataType;
	}


	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {

		return defaultValue;
	}


	/**
	 * @return the note
	 */
	public String getNote() {

		return note;
	}


	/**
	 * @return the vocabulary
	 */
	public MetadataVocabulary getVocabulary() {

		return vocabulary;
	}


	/**
	 * @return the validator
	 */
	public MetadataValidator getValidator() {

		return validator;
	}


	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {

		this.fieldName = fieldName;
	}


	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(Boolean mandatory) {

		this.mandatory = mandatory;
	}


	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {

		this.dataType = dataType;
	}


	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {

		this.defaultValue = defaultValue;
	}


	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {

		this.note = note;
	}


	/**
	 * @param vocabulary the vocabulary to set
	 */
	public void setVocabulary(MetadataVocabulary vocabulary) {

		this.vocabulary = vocabulary;
	}


	/**
	 * @param validator the validator to set
	 */
	public void setValidator(MetadataValidator validator) {

		this.validator = validator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataField [fieldName=");
		builder.append(fieldName);
		builder.append(", mandatory=");
		builder.append(mandatory);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", note=");
		builder.append(note);
		builder.append(", vocabulary=");
		builder.append(vocabulary);
		builder.append(", validator=");
		builder.append(validator);
		builder.append("]");
		return builder.toString();
	}

}
