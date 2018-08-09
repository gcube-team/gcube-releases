/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.datacatalogue.metadatadiscovery.Namespace;
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
	private static final String LOCAL_NAME_CATEGORYREF = "categoryref";

	/**
	 *
	 */
	private static final long serialVersionUID = 5935573474465015727L;

	@XmlAttribute(name=LOCAL_NAME_CATEGORYREF)
	private String categoryRef = null; //ITS VALUE IS A CATEGORY-ID
	@XmlElement(required = true)
	private String fieldName;
    @XmlElement(required = true)
	private Boolean mandatory = false;

    @XmlElement(name = "maxOccurs")
	private String maxOccurs;
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

	@XmlElement(name = "tagging")
	private MetadataTagging tagging;

	@XmlElement(name = "grouping")
	private MetadataGrouping grouping;

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
	 */
	public MetadataField(String fieldName, Boolean mandatory) {

		super();
		this.fieldName = fieldName;
		this.mandatory = mandatory;
	}


	/**
	 * Instantiates a new metadata field.
	 *
	 * @param fieldName the field name
	 * @param mandatory the mandatory
	 * @param categoryID the category id
	 */
	public MetadataField(String fieldName, Boolean mandatory, String categoryID) {

		super();
		this.fieldName = fieldName;
		this.mandatory = mandatory;
		setCategoryRefToCategoryId(categoryID);
	}

	/**
	 * Gets the category ref. If exists its value is a Category-ID
	 *
	 * @return the categoryRef
	 */
	public String getCategoryRef() {

		return categoryRef;
	}


	/**
	 * Sets the category ref to category id.
	 *
	 * @param categoryID the new category ref to category id
	 */
	public void setCategoryRefToCategoryId(String categoryID) {

		this.categoryRef = categoryID;
	}

	/**
	 * Gets the category field qualified name.
	 *
	 * If the Metadata Field belongs to a category, returns the qualified name: {@link MetadataField#categoryRef} {@link Namespace#Separator}} fieldName;
	 * Otherwise returns the fieldName
	 *
	 * @return the category q name
	 */
	public String getCategoryFieldQName(){

		return categoryRef==null?fieldName:categoryRef+Namespace.Separator+fieldName;

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
	 * Gets the data type.
	 *
	 * @return the dataType
	 */
	public DataType getDataType() {

		return dataType;
	}


	/**
	 * Gets the max occurs.
	 *
	 * @return the maxOccurs
	 */
	public String getMaxOccurs() {

		return maxOccurs;
	}


	/**
	 * @param maxOccurs the maxOccurs to set
	 */
	public void setMaxOccurs(String maxOccurs) {

		this.maxOccurs = maxOccurs;
	}

	/**
	 * Gets the default value.
	 *
	 * @return the defaultValue
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
	 * Gets the tagging.
	 *
	 * @return the tagging
	 */
	public MetadataTagging getTagging() {

		return tagging;
	}


	/**
	 * Gets the grouping.
	 *
	 * @return the grouping
	 */
	public MetadataGrouping getGrouping() {

		return grouping;
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
	 * Sets the data type.
	 *
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {

		this.dataType = dataType;
	}


	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the defaultValue to set
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


	/**
	 * Sets the tagging.
	 *
	 * @param tagging the tagging to set
	 */
	public void setTagging(MetadataTagging tagging) {

		this.tagging = tagging;
	}


	/**
	 * Sets the grouping.
	 *
	 * @param grouping the grouping to set
	 */
	public void setGrouping(MetadataGrouping grouping) {

		this.grouping = grouping;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataField [categoryRef=");
		builder.append(categoryRef);
		builder.append(", fieldName=");
		builder.append(fieldName);
		builder.append(", mandatory=");
		builder.append(mandatory);
		builder.append(", maxOccurs=");
		builder.append(maxOccurs);
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
		builder.append(", tagging=");
		builder.append(tagging);
		builder.append(", grouping=");
		builder.append(grouping);
		builder.append("]");
		return builder.toString();
	}


}
