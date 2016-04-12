package org.gcube.portlets.user.searchportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchableFieldBean implements IsSerializable {
	
	private String id;
	
	private String name;
	
	private String value;
	
	private SearchableFieldBean() {}
	
	public SearchableFieldBean(String id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	 @Override
	   public int hashCode() {
	     return this.id.hashCode();
	   }
	  
	   /* (non-Javadoc)
	    * @see java.lang.Object#equals(java.lang.Object)
	    */
	   @Override
	   public boolean equals(Object other) {
		   if (other == null) return false;
		    if (other == this) return true;
		    if (this.getClass() != other.getClass())return false;
		    SearchableFieldBean sf = (SearchableFieldBean)other;
		    return (sf.id.equals(id) && sf.name.equals(name));

	   }
}
