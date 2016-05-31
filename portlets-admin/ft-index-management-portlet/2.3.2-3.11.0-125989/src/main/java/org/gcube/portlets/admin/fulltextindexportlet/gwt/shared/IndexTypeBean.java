/**
 * 
 */
package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class IndexTypeBean implements IsSerializable {
	
	private String resourceID;
	private String indexTypeName;
	private String indexTypeDesc;
	private FieldBean[] indexTypeFields;
	
	public IndexTypeBean() { }
	
	public void setIndexTypeName(String indexTypeName) {
		this.indexTypeName = indexTypeName;
	}
	
	public String getIndexTypeName() {
		return this.indexTypeName;
	}
	
	public void setIndexTypeFields(FieldBean[] fields) {
		this.indexTypeFields = fields;
	}
	
	public FieldBean[] getIndexTypeFields() {
		return this.indexTypeFields;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	
	public String getResourceID() {
		return this.resourceID;
	}
	
	public void setIndexTypeDesc(String indexTypeDesc) {
		this.indexTypeDesc = indexTypeDesc;
	}
	
	public String getIndexTypeDesc() {
		return this.indexTypeDesc;
	}
}
