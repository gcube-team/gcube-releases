/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

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
	
}
