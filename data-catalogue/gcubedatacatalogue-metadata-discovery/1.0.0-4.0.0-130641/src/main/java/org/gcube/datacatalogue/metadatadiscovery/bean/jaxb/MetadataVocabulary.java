/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * The Class MetadataVocabulary.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
@XmlRootElement(name = "metadatavocabulary")
@XmlAccessorType (XmlAccessType.FIELD)
public class MetadataVocabulary implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 6697274733224694581L;

	private List<String> vocabularyField;

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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataVocabulary [vocabularyField=");
		builder.append(vocabularyField);
		builder.append("]");
		return builder.toString();
	}

}
