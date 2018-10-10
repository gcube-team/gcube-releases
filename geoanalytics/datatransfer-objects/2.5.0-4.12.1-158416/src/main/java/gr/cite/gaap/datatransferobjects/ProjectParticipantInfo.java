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
public class ProjectParticipantInfo {
	private static Logger logger = LoggerFactory.getLogger(ProjectParticipantInfo.class);

	private String individualName = "";
	private String projectGroupName = "";
	private UUID id = null;
	private Rights rights = null;
	
	public ProjectParticipantInfo() {
		super();
		logger.trace("Initialized default contructor for ProjectParticipantInfo");
	}
	public Rights getRights() {
		return rights;
	}
	public void setRights(Rights rights) {
		this.rights = rights;
	}
	public String getIndividualName() {
		return individualName;
	}
	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}
	public String getProjectGroupName() {
		return projectGroupName;
	}
	public void setProjectGroupName(String projectGroupName) {
		this.projectGroupName = projectGroupName;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
}
