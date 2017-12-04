package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.user.client.rpc.IsSerializable;


public class VMProvider implements Storable,IsSerializable{

	public static final ObjectType TYPE=ObjectType.VM_PROVIDER;

	public static final String prefix="provider.";

	public static final String NAME_FIELD=prefix+"NAME";
	public static final String URL_FIELD=prefix+"URL";
	public static final String ID_FIELD=prefix+"ID";



	private String name=null;
	private String url=null;
	private String id=null;

	public VMProvider() {
		// TODO Auto-generated constructor stub
	}

	public VMProvider(String name, String url,String id) {
		super();
		this.name = name;		
		this.url = url;
		this.id=id;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getNameValue() {
		return name;
	}

	

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VMProvider [name=");
		builder.append(name);
		builder.append(", url=");
		builder.append(url);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VMProvider other = (VMProvider) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String getKey() {		
		return getId();
	}

	@Override
	public Object getObjectField(String fieldName) {
		if(fieldName.equals(NAME_FIELD)) return getNameValue();
		if(fieldName.equals(URL_FIELD ))return getUrl();
		if(fieldName.equals(ID_FIELD)) return getId();
		return null;
	}

	@Override
	public ObjectType getType() {
		return TYPE;
	}
	
	@Override
	public String getName() {
		return name!=null?getNameValue():getId();
	}
}
