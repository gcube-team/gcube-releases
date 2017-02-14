/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

/**
 * @author vfloros
 *
 */
public class ProjectGroupInfo {
	String projectGroupName = "";
	String creatorName = "";
	long numOfMembers = 0L;
	
	public ProjectGroupInfo(String projectGroupName, String creatorName) {
		super();
		this.projectGroupName = projectGroupName;
		this.creatorName = creatorName;
	}
	public ProjectGroupInfo(String projectGroupName, String creatorName, long numOfMembers) {
		super();
		this.projectGroupName = projectGroupName;
		this.creatorName = creatorName;
		this.numOfMembers = numOfMembers;
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
