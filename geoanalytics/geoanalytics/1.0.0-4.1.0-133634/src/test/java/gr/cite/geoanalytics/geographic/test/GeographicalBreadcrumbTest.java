package gr.cite.geoanalytics.geographic.test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.gaap.datatransferobjects.Coords;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.PrincipalManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml", 
"classpath:WEB-INF/geoanalytics-servlet.xml"})
@WebAppConfiguration
public class GeographicalBreadcrumbTest {
	
	private static enum CoordsTest {
		Athens(new Coords(23.72326, 37.95652)),
		Sparta(new Coords(22.42653, 37.05888));
		
		private Coords coords = null;
		
		private CoordsTest(Coords coords) {
			this.coords = coords;
		}
		
		public Coords coords() {
			return new Coords(coords.getLon(), coords.getLat());
		}
	}
	private static final Logger log = LoggerFactory.getLogger(GeographicalBreadcrumbTest.class);

	private MockMvc mockMvc;
	
	private TaxonomyManager taxonomyManager;
	private ShapeManager shapeManager;
	private ImportManager importManager;
	private PrincipalManager principalManager;
	
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
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
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
	public void testBreadcrumb() throws Exception {
		
		
		HttpSession session = mockMvc
				.perform(MockMvcRequestBuilders
					.post("/static/j_spring_security_check")
					.secure(true)
					.param("username", "EdElric")
					.param("password", "edel"))
				.andReturn()
				.getRequest()
				.getSession();
		
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders
					.post("/shapes/breadcrumbsByCoordinates")
					.contentType(MediaType.APPLICATION_JSON)
					.content(asJsonString(CoordsTest.Sparta.coords()))
					.session((MockHttpSession)session)
					.secure(true))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		System.out.println(result.getResponse().getStatus());
		result.getResponse().getContentAsString();
		Map<UUID, List<TaxonomyTermMessenger>> res = new ObjectMapper().readValue(result.getResponse().getContentAsString(), new TypeReference<Map<UUID, List<TaxonomyTermMessenger>>>(){});
		verifyBreadcrumb(CoordsTest.Sparta);
	}
	
	
	private void verifyBreadcrumb(CoordsTest coordsTest) {
		if(coordsTest == CoordsTest.Athens) {
			
		}
		
		if(coordsTest == CoordsTest.Sparta) {
			
		}
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
}
