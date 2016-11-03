/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.shared;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 30, 2013
 *
 */
@Entity
public class GeoResourceParameters implements FetchingElement{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId;
	
	protected String scope;
	protected String url;
	protected String user;
	protected String password;
	
	protected String resourceType; //GEOSERVER OR GEONETWORK
	
	@Temporal(TemporalType.DATE)
	protected Date creationDate;
	
	public static enum RESOURCETYPE{GEOSERVER, GEONETWORK};
	
	public GeoResourceParameters(){}
	

	/**
	 * 
	 * @param scope
	 * @param url
	 * @param user
	 * @param password
	 * @param type
	 */
	public GeoResourceParameters(String scope, String url, String user, String password, RESOURCETYPE type) {
		this.scope = scope;
		this.url = url;
		this.user = user;
		this.password = password;
		this.resourceType = type.toString();
		this.creationDate = Calendar.getInstance().getTime();
	}
	
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.shared.FetchingElement#getId()
	 */
	@Override
	public String getId() {
		return internalId+"";
	}


	public String getScope() {
		return scope;
	}


	public void setScope(String scope) {
		this.scope = scope;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getResourceType() {
		return resourceType;
	}


	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	protected Date getCreationDate() {
		return creationDate;
	}


	protected void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoResourceParameters [internalId=");
		builder.append(internalId);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", url=");
		builder.append(url);
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append(password);
		builder.append(", resourceType=");
		builder.append(resourceType);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append("]");
		return builder.toString();
	}
	
	
}
