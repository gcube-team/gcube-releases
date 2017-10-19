/**
 *
 */
package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;

import java.io.Serializable;

/**
 * To be used when a field must be used to create a group.
 * @see org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataGrouping
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class FieldAsGroup implements Serializable{

	private static final long serialVersionUID = 8096886403417944385L;
	private boolean create;
	private boolean isPropagateUp;
	private TaggingGroupingValue groupingValue;

	public FieldAsGroup() {
		super();
	}

	public FieldAsGroup(boolean create, TaggingGroupingValue groupingValue, boolean isPropagateUp) {
		this.isPropagateUp = isPropagateUp;
		this.create = create;
		this.groupingValue = groupingValue;
	}

	public boolean getCreate() {
		return create;
	}

	public void setCreate(Boolean create) {
		this.create = create;
	}

	public TaggingGroupingValue getGroupingValue() {
		return groupingValue;
	}

	public void setGroupingValue(TaggingGroupingValue groupingValue) {
		this.groupingValue = groupingValue;
	}

	public boolean isPropagateUp() {
		return isPropagateUp;
	}

	public void setPropagateUp(boolean isPropagateUp) {
		this.isPropagateUp = isPropagateUp;
	}

	@Override
	public String toString() {
		return "FieldAsGroup [create=" + create + ", isPropagateUp="
				+ isPropagateUp + ", groupingValue=" + groupingValue + "]";
	}

}
