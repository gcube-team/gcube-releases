package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class ThProcessDescriptor.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 8, 2018
 */
public class ThProcessDescriptor implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3357273914885205346L;
	
	/** The folder id. */
	private String folderId;
	
	/** The folder path. */
	private String folderPath;
	
	/** The launch time. */
	private long launchTime;
	
	/** The process id. */
	private String processId;
	
	/** The synch configuration. */
	private ThSynchFolderConfiguration synchConfiguration;
	
	
	public ThProcessDescriptor() {
		// TODO Auto-generated constructor stub
	}


	public ThProcessDescriptor(String folderId, String folderPath, long launchTime, String processId,
			ThSynchFolderConfiguration synchConfiguration) {
		super();
		this.folderId = folderId;
		this.folderPath = folderPath;
		this.launchTime = launchTime;
		this.processId = processId;
		this.synchConfiguration = synchConfiguration;
	}


	public String getFolderId() {
		return folderId;
	}


	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}


	public String getFolderPath() {
		return folderPath;
	}


	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}


	public long getLaunchTime() {
		return launchTime;
	}


	public void setLaunchTime(long launchTime) {
		this.launchTime = launchTime;
	}


	public String getProcessId() {
		return processId;
	}


	public void setProcessId(String processId) {
		this.processId = processId;
	}


	public ThSynchFolderConfiguration getSynchConfiguration() {
		return synchConfiguration;
	}


	public void setSynchConfiguration(ThSynchFolderConfiguration synchConfiguration) {
		this.synchConfiguration = synchConfiguration;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThProcessDescriptor [folderId=");
		builder.append(folderId);
		builder.append(", folderPath=");
		builder.append(folderPath);
		builder.append(", launchTime=");
		builder.append(launchTime);
		builder.append(", processId=");
		builder.append(processId);
		builder.append(", synchConfiguration=");
		builder.append(synchConfiguration);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
