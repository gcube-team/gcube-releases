package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;


import java.io.Serializable;
import java.util.List;

/**
 * The Class MetadataFieldWrapper.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class MetadataFieldWrapper implements Serializable{

	private static final long serialVersionUID = -8476731365884466698L;
	private String fieldName;
	private Boolean mandatory = false;
	private Boolean isBoolean = false;
	private String defaulValue;
	private String note;
	private List<String> vocabulary;
	private String validator;

	/**
	 * Instantiates a new metadata field.
	 */
	public MetadataFieldWrapper() {
		super();
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
	public MetadataFieldWrapper(
		String fieldName, Boolean mandatory, Boolean isBoolean,
		String defaulValue, String note, List<String> vocabulary,
		String validator) {
		super();
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
	public List<String> getVocabulary() {

		return vocabulary;
	}

	/**
	 * Gets the validator.
	 *
	 * @return the validator
	 */
	public String getValidator() {

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
	public void setVocabulary(List<String> vocabulary) {

		this.vocabulary = vocabulary;
	}

	/**
	 * Sets the validator.
	 *
	 * @param validator the validator to set
	 */
	public void setValidator(String validator) {

		this.validator = validator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetadataFieldWrapper [fieldName=" + fieldName + ", mandatory="
				+ mandatory + ", isBoolean=" + isBoolean + ", defaulValue="
				+ defaulValue + ", note=" + note + ", vocabulary=" + vocabulary
				+ ", validator=" + validator + "]";
	}

}
