package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;

import java.io.Serializable;
import java.util.List;

/**
 * A wrapper for the MetadataCategory class.
 * @see org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataCategory
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CategoryWrapper implements Serializable{

	private static final long serialVersionUID = -1949961285656672831L;
	private String id;
	private String title;
	private String description;
	private List<MetadataFieldWrapper> fieldsForThisCategory;

	public CategoryWrapper() {
		super();
	}

	public CategoryWrapper(String id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MetadataFieldWrapper> getFieldsForThisCategory() {
		return fieldsForThisCategory;
	}

	public void setFieldsForThisCategory(
			List<MetadataFieldWrapper> fieldsForThisCategory) {
		this.fieldsForThisCategory = fieldsForThisCategory;
	}

	@Override
	public String toString() {
		return "CategoryWrapper ["
				+ (id != null ? "id=" + id + ", " : "")
				+ (title != null ? "title=" + title + ", " : "")
				+ (description != null ? "description=" + description + ", "
						: "")
				+ (fieldsForThisCategory != null ? "fieldsForThisCategory="
						+ fieldsForThisCategory.size() : "") + "]";
	}


}
