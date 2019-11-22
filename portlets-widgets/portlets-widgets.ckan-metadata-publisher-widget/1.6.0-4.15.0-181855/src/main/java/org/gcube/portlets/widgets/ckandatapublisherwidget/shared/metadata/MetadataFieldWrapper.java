package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;


import java.io.Serializable;
import java.util.List;

/**
 * The Class MetadataFieldWrapper.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 * 
 * @author francesco-mangiacrapa at ISTI-CNR (costantino.perciante@isti.cnr.it)
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
	
	private Integer maxOccurs = 1;

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
	 * @param type the type
	 * @param defaultValue the default value
	 * @param note the note
	 * @param vocabulary the vocabulary
	 * @param validator the validator
	 * @param category the category
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
	 * Gets the max occurs.
	 *
	 * @return the max occurs
	 */
	public Integer getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * Sets the max occurs.
	 *
	 * @param maxOccurs the new max occurs
	 */
	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
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
	 * @param defaultValue the new default value
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

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public DataTypeWrapper getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(DataTypeWrapper type) {
		this.type = type;
	}

	/**
	 * Checks if is multi selection.
	 *
	 * @return true, if is multi selection
	 */
	public boolean isMultiSelection() {
		return multiSelection;
	}

	/**
	 * Sets the multi selection.
	 *
	 * @param multiSelection the new multi selection
	 */
	public void setMultiSelection(boolean multiSelection) {
		this.multiSelection = multiSelection;
	}

	/**
	 * Gets the owner category.
	 *
	 * @return the owner category
	 */
	public CategoryWrapper getOwnerCategory() {
		return ownerCategory;
	}

	/**
	 * Sets the owner category.
	 *
	 * @param ownerCategory the new owner category
	 */
	public void setOwnerCategory(CategoryWrapper ownerCategory) {
		this.ownerCategory = ownerCategory;
	}

	/**
	 * Gets the field name from category.
	 *
	 * @return the field name from category
	 */
	public String getFieldNameFromCategory() {
		return fieldNameFromCategory;
	}

	/**
	 * Sets the field name from category.
	 *
	 * @param fieldNameFromCategory the new field name from category
	 */
	public void setFieldNameFromCategory(String fieldNameFromCategory) {
		this.fieldNameFromCategory = fieldNameFromCategory;
	}

	/**
	 * Gets the as group.
	 *
	 * @return the as group
	 */
	public FieldAsGroup getAsGroup() {
		return asGroup;
	}

	/**
	 * Sets the as group.
	 *
	 * @param asGroup the new as group
	 */
	public void setAsGroup(FieldAsGroup asGroup) {
		this.asGroup = asGroup;
	}

	/**
	 * Gets the as tag.
	 *
	 * @return the as tag
	 */
	public FieldAsTag getAsTag() {
		return asTag;
	}

	/**
	 * Sets the as tag.
	 *
	 * @param asTag the new as tag
	 */
	public void setAsTag(FieldAsTag asTag) {
		this.asTag = asTag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetadataFieldWrapper ["
				+ (fieldName != null ? "fieldName=" + fieldName + ", " : "")
				+ (fieldNameFromCategory != null ? "fieldNameFromCategory="
						+ fieldNameFromCategory + ", " : "")
				+ (mandatory != null ? "mandatory=" + mandatory + ", " : "")
				+ (maxOccurs != null ? "maxOccurs=" + maxOccurs + ", " : "")
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
