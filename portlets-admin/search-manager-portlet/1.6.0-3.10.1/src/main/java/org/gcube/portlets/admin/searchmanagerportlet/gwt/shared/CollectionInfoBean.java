package org.gcube.portlets.admin.searchmanagerportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionInfoBean implements IsSerializable{
	
	private String ID;
	
	private String name;
	
	public CollectionInfoBean() {}
	
	public CollectionInfoBean(String id, String name) {
		this.ID = id;
		this.name = name;
	}

	public String getID() {
		return ID;
	}

	public String getName() {
		return name;
	}
	
	 @Override
	   public int hashCode() {
	     return this.ID.hashCode();
	   }
	  
	   /* (non-Javadoc)
	    * @see java.lang.Object#equals(java.lang.Object)
	    */
	   @Override
	   public boolean equals(Object other) {
		   if (other == null) return false;
		    if (other == this) return true;
		    if (this.getClass() != other.getClass())return false;
		    CollectionInfoBean col = (CollectionInfoBean)other;
		    if (col.ID == null || col.name == null)
		    	return false;
		    return (col.ID.equals(ID) && col.name.equals(name));

	   }

}
