package gr.cite.geoanalytics.mvc.test;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.DocumentManager;
//import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.manager.AuditingManager;
import gr.cite.geoanalytics.manager.ProjectManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.manager.admin.AdministrationManager;
import gr.cite.geoanalytics.mvc.HomeController;
import gr.cite.geoanalytics.notifications.NotificationManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.mail.mailer.Mailer;

@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml", 
		"classpath:WEB-INF/geoanalytics-servlet.xml"})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class HomeControllerTest {
	
	@Inject
	private UserManager userManager;
//	@Inject
//	private ShapeManager shapeManager;
	@Inject
	private DocumentManager documentManager;
	@Inject
	private GeocodeManager taxonomyManager;
	@Inject
	private ProjectManager projectManager;
	@Inject
	private ConfigurationManager configurationManager;
	@Inject
	private AuditingManager auditingManager;
	@Inject
	private AdministrationManager administrationManager;
	@Inject
	private NotificationManager notificationManager;
	@Inject
	private SecurityContextAccessor securityContextAccessor;
	@Inject
	private Mailer mailer;
	
	@Inject
	private WebApplicationContext context;
	  
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).
				apply(SecurityMockMvcConfigurers.springSecurity()).
				build();
	}
/*	@Test
	public void testHomeView() throws Exception {
		HomeController hc = new HomeController(userManager, shapeManager, taxonomyManager, 
				documentManager, projectManager, configurationManager, auditingManager, 
				administrationManager, notificationManager, securityContextAccessor, mailer);
		
	//	MockMvc mockMvc = MockMvcBuilders.standaloneSetup(hc).build();
		
		mockMvc.perform(MockMvcRequestBuilders.get("/")).
			andExpect(MockMvcResultMatchers.status().is(200)).
			andExpect(MockMvcResultMatchers.view().name("home"));
	}*/
}
