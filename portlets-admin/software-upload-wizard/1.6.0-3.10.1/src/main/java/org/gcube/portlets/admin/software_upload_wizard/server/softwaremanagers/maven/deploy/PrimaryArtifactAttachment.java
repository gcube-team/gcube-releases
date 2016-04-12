package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy;

import java.io.File;

/**
 * Identifies an attachment of a maven primary artifact.
 * 
 * @author Luigi Fortunati
 *
 */
public class PrimaryArtifactAttachment {

	private File file;
	private String classifier;
	private String type;
	
	/**
	 * Construct a maven primary artifact attachment.
	 * 
	 * @param file the artifact of the primary artifact attachment
	 * @param classifier the classifier of the primary artifact attachment
	 * @param type the type (extension) of the primary artifact attachment
	 */
	public PrimaryArtifactAttachment(File file, String classifier, String type) {
		super();
		this.file = file;
		this.classifier = classifier;
		this.type = type;
	}

	/**
	 * 
	 * @return the artifact of the primary artifact attachment
	 */
	public File getFile() {
		return file;
	}

	/**
	 * 
	 * @return the classifier of the primary artifact attachment
	 */
	public String getClassifier() {
		return classifier;
	}

	/**
	 * 
	 * @return the type (extension) of the primary artifact attachment
	 */
	public String getType() {
		return type;
	}

}
