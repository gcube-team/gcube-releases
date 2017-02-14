package gr.cite.geoanalytics.dataaccess.security.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.RequestResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.gaap.datatransferobjects.AnalyzeResponse;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.ImportRequest;
import gr.cite.gaap.datatransferobjects.ImportResponse;
import gr.cite.gaap.datatransferobjects.UserSearchSelection;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.mvc.AdminController;
import gr.cite.geoanalytics.util.test.TsvImportTest;
import gr.cite.geoanalytics.util.test.VerifyTaxonomyHeirarchy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml", 
"classpath:WEB-INF/geoanalytics-servlet.xml"})
@WebAppConfiguration
public class CookiePreAuthenticationTest {
	
	private static final Logger log = LoggerFactory.getLogger(CookiePreAuthenticationTest.class);

	private MockMvc mockMvc;
	private String taxonomyTerm = "";
	private String taxonomy = "";
	
	private TaxonomyManager taxonomyManager;
	private ShapeManager shapeManager;
	private ImportManager importManager;
	private UserManager userManager;
	
	@Inject
	public void setImportManager(ImportManager importManager) {
		this.importManager = importManager;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setShapeManager(ShapeManager shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Inject
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Inject
	private WebApplicationContext context;
	
	@Before
	public void testAnalyzeShapeFile() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).
				apply(SecurityMockMvcConfigurers.springSecurity()).
				build();	
	}
	
	@Test
	public void testImportHierarchicalTaxonomy() throws Exception {
		
		Cookie cookie = new Cookie("username", "EdElric");
		cookie.setPath("/");
		cookie.setMaxAge(3600);
		cookie.setVersion(0);
		
		UserSearchSelection uss = new UserSearchSelection();
		uss.setPrincipalNames(Collections.singletonList("AlElric"));
		
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders
					//.post("/admin/users/search")
					.post("/")
					.content(asJsonString(uss))
					.cookie(cookie))
				.andExpect(MockMvcResultMatchers.status().is(200))
				.andReturn();
		
		System.out.println(result.getResponse().getContentAsString());		
	
	}

	public static <T> String asJsonString(T object) {
	    try {
	        final ObjectMapper mapper = new ObjectMapper();
	        final String jsonContent = mapper.writeValueAsString(object);
	        return jsonContent;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	@After
	public void removeLayerAndCreatedTaxonomies() throws Exception {
		
	}
}
