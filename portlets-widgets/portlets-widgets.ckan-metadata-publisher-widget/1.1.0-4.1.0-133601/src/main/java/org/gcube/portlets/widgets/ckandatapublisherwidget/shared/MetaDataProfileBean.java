package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;
import java.util.List;

/**
 * A MetaDataProfileBean with its children (MetaDataType and MetaDataFields)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class MetaDataProfileBean implements Serializable{

	private static final long serialVersionUID = -7377022025375553568L;

	private MetaDataTypeWrapper type; // the type
	private List<MetadataFieldWrapper> metadataFields; // the fields of this type
	
	public MetaDataProfileBean(){
		super();
	}
	
	/**
	 * @param type
	 * @param metadataFields
	 */
	public MetaDataProfileBean(MetaDataTypeWrapper type, List<MetadataFieldWrapper> metadataFields) {
		super();
		this.type = type;
		this.metadataFields = metadataFields;
	}
	/**
	 * @return the type
	 */
	public MetaDataTypeWrapper getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(MetaDataTypeWrapper type) {
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaDataBean [type=" + type + ", metadataFields="
				+ metadataFields + "]";
	}
}
