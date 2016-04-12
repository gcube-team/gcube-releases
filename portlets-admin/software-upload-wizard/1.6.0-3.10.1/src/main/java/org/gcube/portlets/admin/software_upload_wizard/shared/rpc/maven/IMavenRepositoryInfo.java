package org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven;

import java.io.Serializable;

/**
 * Interface that defines a Maven repository by its ID and URL 
 * 
 * @author Luigi Fortunati
 *
 */
public interface IMavenRepositoryInfo extends Serializable{

	/**
	 * @return the url of the maven repository
	 */
	public abstract String getUrl();

	/**
	 * @return the id of the maven repository
	 */
	public abstract String getId();

}