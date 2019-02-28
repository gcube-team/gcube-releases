package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;

import java.io.Serializable;
import java.util.List;

/**
 * A MetaDataProfileBean with its children (MetaDataType, MetaDataFields, Categories)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class MetaDataProfileBean implements Serializable{

	private static final long serialVersionUID = -7377022025375553568L;

	private String type;
	private String title;
	private List<CategoryWrapper> categories;
	private List<MetadataFieldWrapper> metadataFields;

	public MetaDataProfileBean(){
		super();
	}
	public MetaDataProfileBean(String type,
			String title,
			List<MetadataFieldWrapper> metadataFields,
			List<CategoryWrapper> categories) {
		super();
		this.type = type;
		this.title = title;
		this.categories = categories;
		this.metadataFields = metadataFields;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the metadataFields
	 */
	public List<MetadataFieldWrapper> getMetadataFields() {
		return metadataFields;
	}
	/**
	 * @param metadataFields the metadataFields to set
	 */
	public void setMetadataFields(List<MetadataFieldWrapper> metadataFields) {
		this.metadataFields = metadataFields;
	}

	public List<CategoryWrapper> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryWrapper> categories) {
		this.categories = categories;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		return "MetaDataProfileBean [type="
				+ type
				+ ", title="
				+ title
				+ ", categories="
				+ (categories != null ? categories.subList(0,
						Math.min(categories.size(), maxLen)) : null)
				+ ", metadataFields="
				+ (metadataFields != null ? metadataFields.subList(0,
						Math.min(metadataFields.size(), maxLen)) : null) + "]";
	}
	
}
