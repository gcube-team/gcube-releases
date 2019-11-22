
package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;

import java.io.Serializable;

/** 
 * To be used when a field must be used to create a tag.
 * @see org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataTagging
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class FieldAsTag implements Serializable{

	private static final long serialVersionUID = 5414077853964288094L;
	public static final String DEFAULT_SEPARATOR = "-";
	private boolean create;
	private String separator = DEFAULT_SEPARATOR;
	private TaggingGroupingValue taggingValue;

	public FieldAsTag() {
		super();
	}

	public FieldAsTag(Boolean create, String separator, TaggingGroupingValue taggingValue) {
		super();
		this.create = create;
		this.separator = separator;
		this.taggingValue = taggingValue;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public TaggingGroupingValue getTaggingValue() {
		return taggingValue;
	}

	public void setTaggingValue(TaggingGroupingValue taggingValue) {
		this.taggingValue = taggingValue;
	}

	@Override
	public String toString() {
		return "FieldAsTag [create=" + create + ", separator=" + separator
				+ ", taggingValue=" + taggingValue + "]";
	}

}
