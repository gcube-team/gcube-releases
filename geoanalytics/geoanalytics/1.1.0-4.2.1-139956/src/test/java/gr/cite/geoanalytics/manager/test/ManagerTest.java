package gr.cite.geoanalytics.manager.test;

import java.util.ArrayList;
import java.util.List;

import gr.cite.gaap.datatransferobjects.ProjectInfoMessenger;
import gr.cite.gaap.datatransferobjects.ProjectMessenger;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.manager.ProjectManager;
import gr.cite.geoanalytics.manager.UserManager;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml", 
"classpath:WEB-INF/geoanalytics-servlet.xml"})
@WebAppConfiguration
public class ManagerTest {
	
	private static final Logger log = LoggerFactory.getLogger(ManagerTest.class);
	
	private static final String TAXONOMY_1 = "taxonomy1";
	private static final String TAXONOMY_2 = "taxonomy2";
	private static final String TAXONOMY_TERM = "taxonomyTerm";

	private TaxonomyManager taxonomyManager;
	private UserManager userManager;
	private PrincipalDao principalDao;
	private ProjectManager projectManager;
	
	@Inject
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	
	@Inject
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Before
	public void preTest(){
		List<String> taxonomies = createStringList();
		try {
			this.taxonomyManager.deleteTaxonomies(taxonomies);
			log.debug("The Taxonomies removes succesfully.");
		} catch (Exception e) {
			log.error("Error while removing taxonomies", e);
		}
	}
	
	@Test
	public void test() throws Exception {
		
		Principal creator = principalDao.systemPrincipal();
		
		Taxonomy taxonomy1 = new Taxonomy();
		Taxonomy taxonomy2 = new Taxonomy();
		
		taxonomy1.setIsActive(true);
		taxonomy1.setCreator(creator);
		taxonomy1.setIsUserTaxonomy(false);
		taxonomy1.setName(TAXONOMY_1);
		taxonomy1.setTaxonomyClass(null);
		
		taxonomy2.setIsActive(true);
		taxonomy2.setCreator(creator);
		taxonomy2.setIsUserTaxonomy(false);
		taxonomy2.setName(TAXONOMY_2);
		taxonomy2.setTaxonomyClass(null);
		
		this.taxonomyManager.updateTaxonomy(taxonomy1, null, true);
		this.taxonomyManager.updateTaxonomy(taxonomy2, null, true);
		
		TaxonomyTerm taxonomyTerm = new TaxonomyTerm();
		
		Taxonomy fetchedTaxonomy = this.taxonomyManager.findTaxonomyByName(TAXONOMY_1, false); 
		
		taxonomyTerm.setIsActive(false);
		taxonomyTerm.setCreator(creator);
		taxonomyTerm.setName(TAXONOMY_TERM);
		taxonomyTerm.setOrder(0);
		taxonomyTerm.setTaxonomy(fetchedTaxonomy);
		
		this.taxonomyManager.updateTerm(taxonomyTerm, null, null, true);
		
		this.taxonomyManager.findTermByNameAndTaxonomies(TAXONOMY_TERM, createStringList(), false);
	}
	
	@After
	public void postTest(){
		List<String> taxonomies = createStringList();
		try {
			this.taxonomyManager.deleteTaxonomies(taxonomies);
			log.debug("The Taxonomies removes succesfully.");
		} catch (Exception e) {
			log.error("Error while removing taxonomies", e);
		}
	}

	/**
	 * @return
	 */
	private List<String> createStringList() {
		List<String> taxonomies = new ArrayList<String>();
		taxonomies.add(TAXONOMY_1);
		taxonomies.add(TAXONOMY_2);
		return taxonomies;
	}
	
}
