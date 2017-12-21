/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

/**
 * @author vfloros
 *
 */
public class ProjectUUIDAndPrincipalUtilityClass {
	private Principal principal = null;
	private UUID projectId = null;

	public ProjectUUIDAndPrincipalUtilityClass() {}

	public ProjectUUIDAndPrincipalUtilityClass(Principal principal, UUID projectId) {
		super();
		this.principal = principal;
		this.projectId = projectId;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}
}
