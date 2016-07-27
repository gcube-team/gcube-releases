/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



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
	private static final long serialVersionUID = 1740491173451633254L;

	private String fieldName;
	private Boolean mandatory = false;
	private Boolean isBoolean = false;
	private String defaulValue;
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
	 * Instantiates a new metadata field.
	 *
	 * @param fieldName the field name
	 * @param mandatory the mandatory
	 * @param isBoolean the is boolean
	 * @param defaulValue the defaul value
	 * @param note the note
	 * @param vocabulary the vocabulary
	 * @param validator the validator
	 */
	public MetadataField(
		String fieldName, Boolean mandatory, Boolean isBoolean,
		String defaulValue, String note, MetadataVocabulary vocabulary,
		MetadataValidator validator) {

		this.fieldName = fieldName;
		this.mandatory = mandatory;
		this.isBoolean = isBoolean;
		this.defaulValue = defaulValue;
		this.note = note;
		this.vocabulary = vocabulary;
		this.validator = validator;
	}



	/**
	 * Gets the field name.
	 *
	 * @return the fieldName
	 */
	public String getFieldName() {

		return fieldName;
	}



	/**
	 * Gets the mandatory.
	 *
	 * @return the mandatory
	 */
	public Boolean getMandatory() {

		return mandatory;
	}



	/**
	 * Gets the checks if is boolean.
	 *
	 * @return the isBoolean
	 */
	public Boolean getIsBoolean() {

		return isBoolean;
	}



	/**
	 * Gets the defaul value.
	 *
	 * @return the defaulValue
	 */
	public String getDefaulValue() {

		return defaulValue;
	}



	/**
	 * Gets the note.
	 *
	 * @return the note
	 */
	public String getNote() {

		return note;
	}



	/**
	 * Gets the vocabulary.
	 *
	 * @return the vocabulary
	 */
	public MetadataVocabulary getVocabulary() {

		return vocabulary;
	}



	/**
	 * Gets the validator.
	 *
	 * @return the validator
	 */
	public MetadataValidator getValidator() {

		return validator;
	}



	/**
	 * Sets the field name.
	 *
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {

		this.fieldName = fieldName;
	}



	/**
	 * Sets the mandatory.
	 *
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(Boolean mandatory) {

		this.mandatory = mandatory;
	}



	/**
	 * Sets the checks if is boolean.
	 *
	 * @param isBoolean the isBoolean to set
	 */
	public void setIsBoolean(Boolean isBoolean) {

		this.isBoolean = isBoolean;
	}



	/**
	 * Sets the defaul value.
	 *
	 * @param defaulValue the defaulValue to set
	 */
	public void setDefaulValue(String defaulValue) {

		this.defaulValue = defaulValue;
	}



	/**
	 * Sets the note.
	 *
	 * @param note the note to set
	 */
	public void setNote(String note) {

		this.note = note;
	}



	/**
	 * Sets the vocabulary.
	 *
	 * @param vocabulary the vocabulary to set
	 */
	public void setVocabulary(MetadataVocabulary vocabulary) {

		this.vocabulary = vocabulary;
	}



	/**
	 * Sets the validator.
	 *
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
		builder.append(", isBoolean=");
		builder.append(isBoolean);
		builder.append(", defaulValue=");
		builder.append(defaulValue);
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
