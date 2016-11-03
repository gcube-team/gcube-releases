package gr.cite.geoanalytics.layerimport.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.gaap.datatransferobjects.AnalyzeResponse;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.ImportRequest;
import gr.cite.gaap.datatransferobjects.ImportResponse;
import gr.cite.gaap.datatransferobjects.TsvImportProperties;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.manager.DataManager;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.mvc.AdminController;
import gr.cite.geoanalytics.util.test.TsvImportTest;
import gr.cite.geoanalytics.util.test.VerifyTaxonomyHeirarchy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml", 
"classpath:WEB-INF/geoanalytics-servlet.xml"})
@WebAppConfiguration
public class HierarchicalTermLayerImportTest {
	
	final static String TSV_WITH_UNIT = "unit,geo\\time	2014 	2013 	2012 	2011 	2010 	2009 	2008 	2007 	2006 	2005 	2004 	2003 	2002 	2001 	2000" + '\n' 
	+ "EUR_HAB,AT	38500 	38100 	37600 	36800 	35200 	34300 	35100 	34000 	32200 	30800 	29600 	28500 	28000 	27400 	26600" + '\n' 
	+ "EUR_HAB,AT1	38700 	38500 	38100 	37600 	36300 	35600 	36200 	35100 	33500 	32000 	31100 	30100 	29900 	29200 	28500\n" + '\n' 
	+ "EUR_HAB,AT11	26500 	26100 	25500 	24300 	23400 	22500 	22500 	22100 	21000 	20300 	20200 	19200 	18700 	17800 	17300\n" + '\n'
	+ "EUR_HAB,AT12	31400 	31200 	30700 	30100 	28700 	28000 	28900 	27900 	26100 	24900 	24300 	23000 	22500 	22100 	21800\n" + '\n'
	+ "EUR_HAB,AT13	47300 	47300 	47100 	46800 	45700 	45000 	45500 	44100 	42700 	40800 	39600 	38900 	39100 	38300 	37100\n"
	+ "MIO_EUR,AT	38500 	38100 	37600 	36800 	35200 	34300 	35100 	34000 	32200 	30800 	29600 	28500 	28000 	27400 	26600\n" + '\n' 
	+ "MIO_EUR,AT1	38700 	38500 	38100 	37600 	36300 	35600 	36200 	35100 	33500 	32000 	31100 	30100 	29900 	29200 	28500\n" + '\n' 
	+ "MIO_EUR,AT11	26500 	26100 	25500 	24300 	23400 	22500 	22500 	22100 	21000 	20300 	20200 	19200 	18700 	17800 	17300\n" + '\n'
	+ "MIO_EUR,AT12	31400 	31200 	30700 	30100 	28700 	28000 	28900 	27900 	26100 	24900 	24300 	23000 	22500 	22100 	21800\n" + '\n'
	+ "MIO_EUR,AT13	47300 	47300 	47100 	46800 	45700 	45000 	45500 	44100 	42700 	40800 	39600 	38900 	39100 	38300 	37100\n";
	
	private static final String GEOGRAPHY_TAXONOMY = "NUTS Geography Test";
	private static final String TEMPLATE_LAYER_TAXONOMY_TERM = "NUTS_Layer_Test";
	private static final String TAXONOMY_TERM_OF_LAYER = "New_NUTS_taxonomy_term2";
	private static final String TAXONOMY_OF_TAXONOMY_TERM_LAYER = "NUTS";

	private static final Logger log = LoggerFactory.getLogger(HierarchicalTermLayerImportTest.class);

	private MockMvc mockMvc;
	private String taxonomyTerm = "";
	private String taxonomy = "";
	
	private TaxonomyManager taxonomyManager;
	private ShapeManager shapeManager;
	private ImportManager importManager;
	private DataManager dataManager;
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
	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	@Inject
	private WebApplicationContext context;
	
	@Before
	public void testAnalyzeShapeFile() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).
				apply(SecurityMockMvcConfigurers.springSecurity()).
				build();
		
		
		Taxonomy geogTaxonomy = taxonomyManager.findTaxonomyByName(GEOGRAPHY_TAXONOMY, false);
		if(geogTaxonomy == null) {
			geogTaxonomy = new Taxonomy();
			geogTaxonomy.setCreator(principalManager.getSystemPrincipal());
			geogTaxonomy.setExtraData("<extraData geographic = \"true\" />");
			geogTaxonomy.setIsActive(true);
			geogTaxonomy.setIsUserTaxonomy(false);
			geogTaxonomy.setName(GEOGRAPHY_TAXONOMY);
			taxonomyManager.updateTaxonomy(geogTaxonomy, null, true);
		}
//		TaxonomyTerm taxonomyTermLayer = this.taxonomyManager.findTermByNameAndTaxonomy(TEMPLATE_LAYER_TAXONOMY_TERM, TAXONOMY_OF_TAXONOMY_TERM_LAYER, false);
//		if(taxonomyTermLayer != null) {
//			List<Taxonomy> hier = 
//					shapeManager.getGeographyHierarchy(
//							shapeManager.getTermFromLayerTermAndShape(taxonomyTermLayer, 
//									this.shapeManager.getShapesOfLayer(taxonomyTermLayer).get(0), true)
//									.getTaxonomy())
//							.getMainHierarchy();
			
//			this.importManager.removeLayer(taxonomyTermLayer);
//			this.taxonomyManager.deleteTerm(taxonomyTermLayer);
//			taxonomyManager.deleteTaxonomies(hier.stream().map(t -> t.getName()).skip(1).collect(Collectors.toList()));
//		}
				
	}
	
	public void testImportHierarchicalTaxonomy() throws Exception {
		
		createTemplateLayerTaxonomyTerm();
		
		MockMultipartFile dbfFile = new MockMultipartFile("dbfFile", "NUTS_RG_01M_2013.dbf", "application/octet-stream", 
				new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.dbf")));
		
		MockMultipartFile prjFile = new MockMultipartFile("prjFile", "NUTS_RG_01M_2013.prj", "application/octet-stream", 
				new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.prj")));
		
		MockMultipartFile shpFile = new MockMultipartFile("shpFile", "NUTS_RG_01M_2013.shp", "application/octet-stream", 
				new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.shp")));
		
		MockMultipartFile shxFile = new MockMultipartFile("shxFile", "NUTS_RG_01M_2013.shx", "application/octet-stream", 
				new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.shx")));
		
		MockMultipartFile sbnFile = new MockMultipartFile("sbnFile", "NUTS_RG_01M_2013.sbn", "application/octet-stream", 
				new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.sbn")));
		
		MockMultipartFile sbxFile = new MockMultipartFile("sbxFile", "NUTS_RG_01M_2013.sbx", "application/octet-stream", 
				new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.sbx")));
		
//		MockMultipartFile dbfFile = new MockMultipartFile("dbfFile", "NUTS_RG_01M_2013.dbf", "application/octet-stream", 
//				new FileInputStream(new File("C:\\Users\\g.farantatos\\Documents\\NUTS_RG_01M_2013_200_features\\NUTS_RG_01M_2013.dbf")));
//		
//		MockMultipartFile prjFile = new MockMultipartFile("prjFile", "NUTS_RG_01M_2013.prj", "application/octet-stream", 
//				new FileInputStream(new File("C:\\Users\\g.farantatos\\Documents\\NUTS_RG_01M_2013_200_features\\NUTS_RG_01M_2013.prj")));
//		
//		MockMultipartFile shpFile = new MockMultipartFile("shpFile", "NUTS_RG_01M_2013.shp", "application/octet-stream", 
//				new FileInputStream(new File("C:\\Users\\g.farantatos\\Documents\\NUTS_RG_01M_2013_200_features\\NUTS_RG_01M_2013.shp")));
//		
//		MockMultipartFile shxFile = new MockMultipartFile("shxFile", "NUTS_RG_01M_2013.shx", "application/octet-stream", 
//				new FileInputStream(new File("C:\\Users\\g.farantatos\\Documents\\NUTS_RG_01M_2013_200_features\\NUTS_RG_01M_2013.shx")));
//		
		
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
					.fileUpload("/admin/import/analyze")
					.file(shpFile)
					.file(sbnFile)
					.file(sbxFile)
					.file(shxFile)
					.file(dbfFile)
					.file(prjFile)
					.param("dbfCharset", "UTF-8")
					.session((MockHttpSession)session)
					.secure(true))
				.andReturn();
		
		System.out.println(result.getResponse().getStatus());
		result.getResponse().getContentAsString();
		AnalyzeResponse res = new ObjectMapper().readValue(result.getResponse().getContentAsString(), AnalyzeResponse.class);
		System.out.println(res.getToken());
		System.out.println(res.getAttrs());
		
		ImportRequest importRequest = createImportRequestForNuts(res);
		
		long importStart = System.currentTimeMillis();
		result = mockMvc
					.perform(MockMvcRequestBuilders
						.post("/admin/import/importData")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.session((MockHttpSession)session)
						.content(asJsonString(importRequest))
						.secure(true))
					.andReturn();
		long importEnd = System.currentTimeMillis();
		long importTimeInMinutes = TimeUnit.MINUTES.convert((importEnd-importStart), TimeUnit.MILLISECONDS);
		
		ObjectMapper objectMapper = new ObjectMapper();
		ImportResponse importResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ImportResponse.class);
		if(importResponse.getStatus() != ImportResponse.Status.Success)
			Assert.fail("Import not successful. Status: " + importResponse.getStatus() + " message: " + importResponse.getMessage());
		
		verifyTaxonomyTermHeirarchyOperation(GEOGRAPHY_TAXONOMY, false);
		
		UUID templateLayerTaxonomyTermId  = this.taxonomyManager.findTermByNameAndTaxonomy(TEMPLATE_LAYER_TAXONOMY_TERM, TAXONOMY_OF_TAXONOMY_TERM_LAYER, false).getId();
		UUID geographycalTaxonomyId  = this.taxonomyManager.findTaxonomyByName(GEOGRAPHY_TAXONOMY, false).getId();
		UUID taxonomyOfLayerTaxonomyTerm  = this.taxonomyManager.findTaxonomyByName(TAXONOMY_OF_TAXONOMY_TERM_LAYER, false).getId();
		
		importDataFromTsv(templateLayerTaxonomyTermId, geographycalTaxonomyId, TSV_WITH_UNIT, TAXONOMY_TERM_OF_LAYER, taxonomyOfLayerTaxonomyTerm);
	}
	
	@WithUserDetails("EdElric")
	private void createTemplateLayerTaxonomyTerm() throws Exception {
		TaxonomyTerm taxonomyTermLayer = new TaxonomyTerm();
		taxonomyTermLayer.setCreator(principalManager.getSystemPrincipal());
		taxonomyTermLayer.setExtraData("<extraData geographic = \"true\" />");
		taxonomyTermLayer.setIsActive(true);
		taxonomyTermLayer.setName(TEMPLATE_LAYER_TAXONOMY_TERM);
		taxonomyTermLayer.setTaxonomy(taxonomyManager.findTaxonomyByName(TAXONOMY_OF_TAXONOMY_TERM_LAYER, false));
		taxonomyManager.updateTerm(taxonomyTermLayer, null, null, true);
	}
	
	public void verify() throws Exception {
		verifyTaxonomyTermHeirarchyOperation(GEOGRAPHY_TAXONOMY, false);
	}
	
	@Test
	@WithUserDetails("EdElric")
	public void tsvTest() throws Exception {
		UUID templateLayerTaxonomyTermId  = this.taxonomyManager.findTermByNameAndTaxonomy(TEMPLATE_LAYER_TAXONOMY_TERM, TAXONOMY_OF_TAXONOMY_TERM_LAYER, false).getId();
		UUID geographycalTaxonomyId  = this.taxonomyManager.findTaxonomyByName(GEOGRAPHY_TAXONOMY, false).getId();
		UUID taxonomyOfLayerTaxonomyTerm  = this.taxonomyManager.findTaxonomyByName(TAXONOMY_OF_TAXONOMY_TERM_LAYER, false).getId();
		
		HttpSession session = mockMvc
				.perform(MockMvcRequestBuilders
					.post("/static/j_spring_security_check")
					.secure(true)
					.param("username", "EdElric")
					.param("password", "edel"))
				.andReturn()
				.getRequest()
				.getSession();
		
		TsvImportProperties props = new TsvImportProperties();
		props.setNewLayerName(TAXONOMY_TERM_OF_LAYER);
		props.setTemplateLayerName(TEMPLATE_LAYER_TAXONOMY_TERM);
		
		//MockMultipartFile tsvFile  = new MockMultipartFile("tsvImportFile", "test.tsv", "application/octet-stream", 
		//		new FileInputStream(new File("C:\\Users\\g.farantatos\\Downloads\\Nuts shapefile\\NUTS_RG_01M_2013.prj")));
		MockMultipartFile tsvFile  = new MockMultipartFile("tsvImportFile", "test.tsv", "application/octet-stream", 
				TSV_WITH_UNIT.getBytes(StandardCharsets.UTF_8));
		MockMultipartFile propPart = new MockMultipartFile("tsvImportProperties", "", "application/json", asJsonString(props).getBytes(StandardCharsets.UTF_8));

		
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders
					.fileUpload("/admin/import/importTsv")
					.file(tsvFile)
					.file(propPart)
					.session((MockHttpSession)session)
					.secure(true))
				.andExpect(MockMvcResultMatchers.status().is(200))
				.andReturn();
		
		System.out.println(result.getResponse().getContentAsString());
		
		
		//importDataFromTsv(templateLayerTaxonomyTermId, geographycalTaxonomyId, TSV_WITH_UNIT, TAXONOMY_TERM_OF_LAYER, taxonomyOfLayerTaxonomyTerm);

	}
	
	@WithUserDetails("EdElric")
	public void removeLayerAndCreatedTaxonomies() throws Exception {
		log.info("Taxonomy: " + this.taxonomy + ", TaxonomyTerm " + this.taxonomyTerm);
		
		if (taxonomy != "" && taxonomyTerm != ""){
			TaxonomyTerm taxonomyTermLayer = this.taxonomyManager.findTermByNameAndTaxonomy(taxonomyTerm, taxonomy, false);
			List<Taxonomy> hier = 
					shapeManager.getGeographyHierarchy(
							shapeManager.getTermFromLayerTermAndShape(taxonomyTermLayer, 
									this.shapeManager.getShapesOfLayer(taxonomyTermLayer).get(0))
									.getTaxonomy())
							.getMainHierarchy();
			
			this.importManager.removeLayer(taxonomyTermLayer);
			this.taxonomyManager.deleteTerm(taxonomyTermLayer);
			taxonomyManager.deleteTaxonomies(hier.stream().map(t -> t.getName()).skip(1).collect(Collectors.toList()));
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
	
	private ImportRequest createImportRequestForNuts(AnalyzeResponse res) {
		
		ImportRequest importRequest = new ImportRequest();
		List<AttributeInfo> attributeInfos = new ArrayList<AttributeInfo>();
		AttributeInfo shapeLen = new AttributeInfo("SHAPE_LEN", "double", null, null, false, true);
		
		shapeLen.setAutoValueMapping(false);
		shapeLen.setMapValue(false);
		shapeLen.setAutoDocumentMapping(false);
		
		attributeInfos.add(shapeLen);
		
		AttributeInfo statLvl = new AttributeInfo("STAT_LEVL_", "integer", null, null, false, true);
		
		statLvl.setAutoValueMapping(false);
		statLvl.setMapValue(false);
		statLvl.setAutoDocumentMapping(false);
		
		attributeInfos.add(statLvl);
		
		AttributeInfo shapeArea = new AttributeInfo("SHAPE_AREA", "double", null, null, false, true);
		
		shapeArea.setAutoValueMapping(false);
		shapeArea.setMapValue(false);
		shapeArea.setAutoDocumentMapping(false);
		
		attributeInfos.add(shapeArea);
		
		AttributeInfo shapeId = new AttributeInfo("NUTS_ID", "string", null, null, true, true);
		
		shapeId.setTaxonomy(GEOGRAPHY_TAXONOMY);
		shapeId.setTermParentTaxonomy(GEOGRAPHY_TAXONOMY);
		shapeId.setAutoValueMapping(true);
		shapeId.setMapValue(false);
		shapeId.setAutoDocumentMapping(false);
		
		attributeInfos.add(shapeId);
		
		importRequest.setToken(res.getToken());
		importRequest.setAttributeConfig(attributeInfos);
		importRequest.setCrs("");
		importRequest.setDbfCharset("UTF-8");
		importRequest.setForceLonLat(true);
		importRequest.setMerge(false);
		importRequest.setReplace(false);
		importRequest.setTaxonomyTerm(TEMPLATE_LAYER_TAXONOMY_TERM);
		importRequest.setTaxonomyTermTaxonomy(TAXONOMY_OF_TAXONOMY_TERM_LAYER);
		importRequest.setGeographyTaxonomy(GEOGRAPHY_TAXONOMY);
		
		taxonomy = "NUTS";
		taxonomyTerm = "NUTS Layer";
		
		return importRequest;
	}
	
	@WithUserDetails("EdElric")
	public Boolean verifyTaxonomyTermHeirarchyOperation(String geographicalTaxonomyString, boolean strict) throws Exception {
		Taxonomy geographicalTaxonomy = this.taxonomyManager.findTaxonomyByName(geographicalTaxonomyString, false);
		List<Taxonomy> hierarchy = this.shapeManager.getGeographyHierarchy(geographicalTaxonomy).getMainHierarchy();
		
		for (Taxonomy taxonomy : hierarchy){
			List<TaxonomyTerm> taxonomyTerms = this.taxonomyManager.getTermsOfTaxonomy(taxonomy.getId().toString(), true, true);
			for (TaxonomyTerm taxonomyTerm : taxonomyTerms){
				
				String numericalPart = getNumericalPart(taxonomyTerm);
				
				if (numericalPart.length() >= 1){
					if(taxonomyTerm.getParent() != null) {
						TaxonomyTerm parent = taxonomyManager.findTermById(taxonomyTerm.getParent().getId().toString(), true);
						checkParent(numericalPart.substring(0, numericalPart.length() - 1), parent, strict);
					}
				}else{
					break;
				}
			}
		}
		return true;
	}
	
	private static void checkParent(String substring, TaxonomyTerm parent, boolean strict) {
		
		String numerical = getNumericalPart(parent);
		
		if (!(strict ? numerical.equals(substring) : substring.contains(numerical))){
			Assert.fail(substring + " is not parent of " + numerical);
		}else{
			if (substring.length() - 1 > 0){
				if(parent.getParent() != null)
					checkParent(numerical.substring(0, numerical.length() - 1), parent.getParent(), strict);
			}
		}
	}

	/**
	 * @param parent
	 * @return
	 */
	private static String getNumericalPart(TaxonomyTerm parent) {
		String taxonomyTermName = parent.getName();
		String numerical = taxonomyTermName.replaceAll("\\D+","");
		return numerical;
	}
	
	@WithUserDetails("EdElric")
	public void importDataFromTsv(UUID templateLayerTaxonomyTermId, UUID geographycalTaxonomyId, String tsv, String newLayerNameForTaxonomyTerm, UUID taxonomyOfLayerTaxonomyTerm) throws Exception {
		this.dataManager.importDataToShapesOfLayerUsingTsvAndUpdate(templateLayerTaxonomyTermId, tsv, newLayerNameForTaxonomyTerm, taxonomyOfLayerTaxonomyTerm);
	}
}
