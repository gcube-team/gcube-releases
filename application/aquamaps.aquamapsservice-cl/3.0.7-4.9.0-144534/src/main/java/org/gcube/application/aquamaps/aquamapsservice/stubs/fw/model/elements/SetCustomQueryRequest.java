package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=DM_target_namespace)
public class SetCustomQueryRequest {
	
	@XmlElement(namespace=DM_target_namespace)
	private String user;
	@XmlElement(namespace=DM_target_namespace, name="queryString")
	private String query;
	
	
	public SetCustomQueryRequest() {
		// TODO Auto-generated constructor stub
	}


	public SetCustomQueryRequest(String user, String query) {
		super();
		this.user = user;
		this.query = query;
	}


	/**
	 * @return the user
	 */
	public String user() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public void user(String user) {
		this.user = user;
	}


	/**
	 * @return the query
	 */
	public String query() {
		return query;
	}


	/**
	 * @param query the query to set
	 */
	public void query(String query) {
		this.query = query;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SetCustomQueryRequest [user=");
		builder.append(user);
		builder.append(", query=");
		builder.append(query);
		builder.append("]");
		return builder.toString();
	}
	
	
}
