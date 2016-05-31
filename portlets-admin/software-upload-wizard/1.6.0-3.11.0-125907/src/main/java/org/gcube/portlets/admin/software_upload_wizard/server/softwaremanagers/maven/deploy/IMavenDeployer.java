/**
 * 
 */
package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy;

import java.io.File;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;

public interface IMavenDeployer {

	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile,
			File pomFile) throws Exception;

	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile,
			File pomFile, boolean generatePom) throws Exception;
	
	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile,
			File pomFile, boolean generatePom, String classifier) throws Exception;
	
	/**
	 * Deploys a primart artifact on a maven repository along with several attachments.
	 * 
	 * @param repositoryInfo Repository coordinates
	 * @param archiveFile primary artifact file
	 * @param pomFile primary artifact pom
	 * @param attachments list of primary artifact attachments
	 */
	public void deploy(IMavenRepositoryInfo repositoryInfo, File archiveFile,
			File pomFile, PrimaryArtifactAttachment... attachments) throws Exception;

}
