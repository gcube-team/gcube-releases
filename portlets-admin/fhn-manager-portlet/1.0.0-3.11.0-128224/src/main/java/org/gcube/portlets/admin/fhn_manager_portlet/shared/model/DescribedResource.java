package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DescribedResource implements IsSerializable{

	private Storable theObject;
	private String xmlDescription;
	
	private List<DescribedResource> related=null;
	
	public DescribedResource() {
		// TODO Auto-generated constructor stub
	}
	public DescribedResource(Storable theObject, String xmlDescription) {
		super();
		this.theObject = theObject;
		this.xmlDescription = xmlDescription;
	}
	
	public void setRelated(List<DescribedResource> related) {
		this.related = related;
	}
	
	public List<DescribedResource> getRelated() {
		return related;
	}
	
	public void add(DescribedResource toAdd){
		if(related==null) related=new ArrayList<DescribedResource>();
		related.add(toAdd);
	}
	
	/**
	 * @return the theObject
	 */
	public Storable getTheObject() {
		return theObject;
	}
	/**
	 * @param theObject the theObject to set
	 */
	public void setTheObject(Storable theObject) {
		this.theObject = theObject;
	}
	/**
	 * @return the xmlDescription
	 */
	public String getXmlDescription() {
		return xmlDescription;
	}
	/**
	 * @param xmlDescription the xmlDescription to set
	 */
	public void setXmlDescription(String xmlDescription) {
		this.xmlDescription = xmlDescription;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((theObject == null) ? 0 : theObject.hashCode());
		result = prime * result
				+ ((xmlDescription == null) ? 0 : xmlDescription.hashCode());
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
		DescribedResource other = (DescribedResource) obj;
		if (theObject == null) {
			if (other.theObject != null)
				return false;
		} else if (!theObject.equals(other.theObject))
			return false;
		if (xmlDescription == null) {
			if (other.xmlDescription != null)
				return false;
		} else if (!xmlDescription.equals(other.xmlDescription))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DescribedResource [theObject=");
		builder.append(theObject);
		builder.append(", xmlDescription=");
		builder.append(xmlDescription);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
