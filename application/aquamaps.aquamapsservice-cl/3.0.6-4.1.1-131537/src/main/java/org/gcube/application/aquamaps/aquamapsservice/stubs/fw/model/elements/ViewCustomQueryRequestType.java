package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;

@XmlRootElement
public class ViewCustomQueryRequestType {
	
	@XmlElement
	private String user;
	@XmlElement
	private PagedRequestSettings pagedRequestSettings;
	
	public ViewCustomQueryRequestType() {
		// TODO Auto-generated constructor stub
	}

	public ViewCustomQueryRequestType(String user, PagedRequestSettings settings) {
		super();
		this.user = user;
		this.pagedRequestSettings = settings;
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
	 * @return the pagedRequestSettings
	 */
	public PagedRequestSettings settings() {
		return pagedRequestSettings;
	}

	/**
	 * @param pagedRequestSettings the pagedRequestSettings to set
	 */
	public void settings(PagedRequestSettings settings) {
		this.pagedRequestSettings = settings;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ViewCustomQueryRequestType [user=");
		builder.append(user);
		builder.append(", pagedRequestSettings=");
		builder.append(pagedRequestSettings);
		builder.append("]");
		return builder.toString();
	}
	
	
}
