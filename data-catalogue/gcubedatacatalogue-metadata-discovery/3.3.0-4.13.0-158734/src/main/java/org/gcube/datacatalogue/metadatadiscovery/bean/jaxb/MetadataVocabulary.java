/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Class MetadataVocabulary.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 12, 2016
 */
@XmlRootElement(name = "metadatavocabulary")
@XmlAccessorType (XmlAccessType.FIELD)
public class MetadataVocabulary implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 506451021936766592L;

	private List<String> vocabularyField;

	@XmlAttribute
	private Boolean isMultiSelection = false;

	/**
	 * Instantiates a new metadata vocabulary.
	 */
	public MetadataVocabulary() {
	}


	/**
	 * Instantiates a new metadata vocabulary.
	 *
	 * @param vocabularyFields the vocabulary fields
	 */
	public MetadataVocabulary(List<String> vocabularyFields) {

		super();
		this.vocabularyField = vocabularyFields;
	}

	/**
	 * Instantiates a new metadata vocabulary.
	 *
	 * @param vocabularyField the vocabulary field
	 * @param isMultiSelection the is multi selection
	 */
	public MetadataVocabulary(List<String> vocabularyField, Boolean isMultiSelection) {

		super();
		this.vocabularyField = vocabularyField;
		this.isMultiSelection = isMultiSelection;
	}


	/**
	 * Gets the vocabulary fields.
	 *
	 * @return the vocabularyField
	 */
	public List<String> getVocabularyFields() {

		return vocabularyField;
	}



	/**
	 * Sets the vocabulary field.
	 *
	 * @param vocabularyField the vocabularyField to set
	 */
	public void setVocabularyField(List<String> vocabularyField) {

		this.vocabularyField = vocabularyField;
	}



	/**
	 * Gets the vocabulary field.
	 *
	 * @return the vocabularyField
	 */
	public List<String> getVocabularyField() {

		return vocabularyField;
	}



	/**
	 * Checks if is multi selection.
	 *
	 * @return the isMultiSelection
	 */
	public Boolean isMultiSelection() {

		return isMultiSelection;
	}



	/**
	 * Sets the checks if is multi selection.
	 *
	 * @param isMultiSelection the isMultiSelection to set
	 */
	public void setIsMultiSelection(Boolean isMultiSelection) {

		this.isMultiSelection = isMultiSelection;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataVocabulary [vocabularyField=");
		builder.append(vocabularyField);
		builder.append(", isMultiSelection=");
		builder.append(isMultiSelection);
		builder.append("]");
		return builder.toString();
	}

}


