package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;


import java.io.Serializable;
import java.util.List;

/**
 * The Class MetadataFieldWrapper.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class MetadataFieldWrapper implements Serializable{

	private static final long serialVersionUID = -8476731365884466698L;
	private String fieldName;
	private String fieldNameFromCategory;
	private Boolean mandatory = false;
	private DataTypeWrapper type;
	private String defaultValue;
	private String note;
	private List<String> vocabulary;
	private boolean multiSelection;
	private String validator;
	private CategoryWrapper ownerCategory;
	private FieldAsGroup asGroup;
	private FieldAsTag asTag;

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
	 * @param DataType the type
	 * @param defaulValue the defaul value
	 * @param note the note
	 * @param vocabulary the vocabulary
	 * @param validator the validator
	 */
	public MetadataFieldWrapper(
			String fieldName, Boolean mandatory, DataTypeWrapper type,
			String defaultValue, String note, List<String> vocabulary,
			String validator, CategoryWrapper category) {
		super();
		this.fieldName = fieldName;
		this.mandatory = mandatory;
		this.type = type;
		this.defaultValue = defaultValue;
		this.note = note;
		this.vocabulary = vocabulary;
		this.validator = validator;
		this.ownerCategory = category;
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
	 * Gets the defaul value.
	 *
	 * @return the defaulValue
	 */
	public String getDefaultValue() {

		return defaultValue;
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
	 * Sets the defaul value.
	 *
	 * @param defaulValue the defaulValue to set
	 */
	public void setDefaultValue(String defaultValue) {

		this.defaultValue = defaultValue;
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

	public DataTypeWrapper getType() {
		return type;
	}

	public void setType(DataTypeWrapper type) {
		this.type = type;
	}

	public boolean isMultiSelection() {
		return multiSelection;
	}

	public void setMultiSelection(boolean multiSelection) {
		this.multiSelection = multiSelection;
	}

	public CategoryWrapper getOwnerCategory() {
		return ownerCategory;
	}

	public void setOwnerCategory(CategoryWrapper ownerCategory) {
		this.ownerCategory = ownerCategory;
	}

	public String getFieldNameFromCategory() {
		return fieldNameFromCategory;
	}

	public void setFieldNameFromCategory(String fieldNameFromCategory) {
		this.fieldNameFromCategory = fieldNameFromCategory;
	}

	public FieldAsGroup getAsGroup() {
		return asGroup;
	}

	public void setAsGroup(FieldAsGroup asGroup) {
		this.asGroup = asGroup;
	}

	public FieldAsTag getAsTag() {
		return asTag;
	}

	public void setAsTag(FieldAsTag asTag) {
		this.asTag = asTag;
	}

	@Override
	public String toString() {
		return "MetadataFieldWrapper ["
				+ (fieldName != null ? "fieldName=" + fieldName + ", " : "")
				+ (fieldNameFromCategory != null ? "fieldNameFromCategory="
						+ fieldNameFromCategory + ", " : "")
				+ (mandatory != null ? "mandatory=" + mandatory + ", " : "")
				+ (type != null ? "type=" + type + ", " : "")
				+ (defaultValue != null ? "defaultValue=" + defaultValue + ", "
						: "")
				+ (note != null ? "note=" + note + ", " : "")
				+ (vocabulary != null ? "vocabulary=" + vocabulary + ", " : "")
				+ "multiSelection="
				+ multiSelection
				+ ", "
				+ (validator != null ? "validator=" + validator + ", " : "")
				+ (ownerCategory != null ? "ownerCategory=" + ownerCategory.getId()
						+ ", " : "")
				+ (asGroup != null ? "asGroup=" + asGroup + ", " : "")
				+ (asTag != null ? "asTag=" + asTag : "") + "]";
	}

}
