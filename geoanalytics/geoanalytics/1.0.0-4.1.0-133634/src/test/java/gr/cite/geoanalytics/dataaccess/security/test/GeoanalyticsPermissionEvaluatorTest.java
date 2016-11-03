package gr.cite.geoanalytics.dataaccess.security.test;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import gr.cite.gaap.datatransferobjects.ProjectInfoMessenger;
import gr.cite.gaap.datatransferobjects.ProjectMessenger;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.manager.ProjectManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml", 
"classpath:WEB-INF/geoanalytics-servlet.xml"})
@WebAppConfiguration
public class GeoanalyticsPermissionEvaluatorTest {

	private ProjectManager projectManager;
	
	@Inject
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	
	@Test
	@WithUserDetails("EdElric")
	public void testEvaluator() throws Exception{
		ProjectInfoMessenger projectInfoMessenger = new ProjectInfoMessenger();
		ProjectMessenger projectMessenger = new ProjectMessenger();
		projectMessenger.setId("02612919-d6e9-403d-a55e-b35cd91871fb");
		projectInfoMessenger.setProjectMessenger(projectMessenger);

		Project p = new Project();
		p.setId(UUID.fromString("02612919-d6e9-403d-a55e-b35cd91871fb"));
//		this.projectManager.update(p, new Workflow(), false, false, "");
//		this.projectManager.update(projectInfoMessenger);
//		this.projectManager.deleteTask(null);
		
		this.projectManager.addWorkflowTaskDocument("02612919-d6e9-403d-a55e-b35cd91871fb", "02612919-d6e9-403d-a55e-b35cd91871fb");
		
	}
	
}
