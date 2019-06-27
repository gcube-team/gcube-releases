/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vfloros
 *
 */
public class ProjectGroupInfo {
	private static Logger logger = LoggerFactory.getLogger(ProjectGroupInfo.class);

	String projectGroupName = "";
	String creatorName = "";
	long numOfMembers = 0L;
	UUID projectGroupID = null;
	
	public ProjectGroupInfo(String projectGroupName, String creatorName) {
		super();
		logger.trace("Initializing ProjectGroupInfo...");

		this.projectGroupName = projectGroupName;
		this.creatorName = creatorName;
		logger.trace("Initialized ProjectGroupInfo");
	}
	
	public ProjectGroupInfo(String projectGroupName, String creatorName, long numOfMembers) {
		super();
		logger.trace("Initializing ProjectGroupInfo...");
		this.projectGroupName = projectGroupName;
		this.creatorName = creatorName;
		this.numOfMembers = numOfMembers;
		logger.trace("Initialized ProjectGroupInfo");
	}
	
	public ProjectGroupInfo(String projectGroupName, String creatorName, long numOfMembers, UUID projectGroupID) {
		super();
		logger.trace("Initializing ProjectGroupInfo...");
		this.projectGroupName = projectGroupName;
		this.creatorName = creatorName;
		this.numOfMembers = numOfMembers;
		this.projectGroupID = projectGroupID;
		logger.trace("Initialized ProjectGroupInfo");
	}
	
	public String getProjectGroupName() {
		return projectGroupName;
	}
	public void setProjectGroupName(String projectGroupName) {
		this.projectGroupName = projectGroupName;
	}
	
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
	public long getNumOfMembers() {
		return numOfMembers;
	}
	public void setNumOfMembers(long numOfMembers) {
		this.numOfMembers = numOfMembers;
	}

	public UUID getProjectGroupID() {
		return projectGroupID;
	}

	public void setProjectGroupID(UUID projectGroupID) {
		this.projectGroupID = projectGroupID;
	}
	
}
