package org.gcube.datacatalogue.catalogue.beans.resource;

import org.json.simple.JSONObject;

/**
 * A custom field bean. It also stores index of the category and of the metadata field associated.
 * These are used to sort them before pushing the content to CKAN.
 * If they are missing, indexes are set to Integer.MAX_VALUE.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CustomField implements Comparable<CustomField>{

	private String key;
	private String qualifiedKey;
	private String value;
	private int indexMetadataField = Integer.MAX_VALUE;
	private int indexCategory = Integer.MAX_VALUE;

	public CustomField(JSONObject object) {
		super();
		this.key = (String)object.get("key");
		this.qualifiedKey = key;
		this.value = (String)object.get("value");
		if(key == null || value  == null || key.isEmpty())
			throw new IllegalArgumentException("A custom field must have a key and a value! Provided object is " + object.toString());
	}

	/**
	 * @param key
	 * @param value
	 */
	public CustomField(String key, String value) {
		super();
		this.key = key;
		this.qualifiedKey = key;
		this.value = value;
	}

	/**
	 * @param key
	 * @param value
	 * @param indexMetadataField
	 * @param indexCategory
	 */
	public CustomField(String key, String value, int indexCategory, int indexMetadataField) {
		super();
		this.key = key;
		this.value = value;
		this.indexMetadataField = indexMetadataField;
		this.indexCategory = indexCategory;
		this.qualifiedKey = key;

		if(this.indexCategory < 0)
			this.indexCategory = Integer.MAX_VALUE;

		if(this.indexMetadataField < 0)
			this.indexMetadataField = Integer.MAX_VALUE;
	}

	public int getIndexMetadataField() {
		return indexMetadataField;
	}

	public String getQualifiedKey() {
		return qualifiedKey;
	}

	public void setQualifiedKey(String qualifiedKey) {
		this.qualifiedKey = qualifiedKey;
	}

	public void setIndexMetadataField(int indexMetadataField) {
		this.indexMetadataField = indexMetadataField;
		if(this.indexMetadataField < 0)
			this.indexMetadataField = Integer.MAX_VALUE;
	}

	public int getIndexCategory() {
		return indexCategory;
	}

	public void setIndexCategory(int indexCategory) {
		this.indexCategory = indexCategory;
		if(this.indexCategory < 0)
			this.indexCategory = Integer.MAX_VALUE;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CustomField [key=" + key + ", qualifiedKey=" + qualifiedKey
				+ ", value=" + value + ", indexMetadataField="
				+ indexMetadataField + ", indexCategory=" + indexCategory + "]";
	}

	@Override
	public int compareTo(CustomField o) {
		if(this.indexCategory == o.indexCategory){
			if(this.indexMetadataField == o.indexMetadataField)
				return 0;
			else
				return this.indexMetadataField > o.indexMetadataField ? 1 : -1;
		}
		else
			return this.indexCategory > o.indexCategory ? 1 : -1;
	}
}
