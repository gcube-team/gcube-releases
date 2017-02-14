package gr.cite.geoanalytics.util.test;

import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml" })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class VerifyTaxonomyHeirarchy {

	
	private TaxonomyManager taxonomyManager;
	private ShapeManager shapeManager;
	
	@Inject
	public void setShapManager(ShapeManager shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	/**
	 * @throws Exception
	 */
	public Boolean verifyTaxonomyTermHeirarchyOperation(String geographicalTaxonomyString) throws Exception {
		Taxonomy geographicalTaxonomy = this.taxonomyManager.findTaxonomyByName(geographicalTaxonomyString, false);
		List<Taxonomy> hierarchy = this.shapeManager.getGeographyHierarchy(geographicalTaxonomy).getMainHierarchy();
		
		for (Taxonomy taxonomy : hierarchy){
			List<TaxonomyTerm> taxonomyTerms = this.taxonomyManager.getTermsOfTaxonomy(taxonomy.getId().toString(), true, false);
			for (TaxonomyTerm taxonomyTerm : taxonomyTerms){
				
				String numericalPart = getNumericalPart(taxonomyTerm);
				
				if (numericalPart.length() != 1){
					checkParent(numericalPart.substring(0, numericalPart.length() - 1), taxonomyTerm.getParent());
				}else{
					break;
				}
			}
		}
		return true;
	}
	
	private static void checkParent(String substring, TaxonomyTerm parent) {
		
		String numerical = getNumericalPart(parent);
		
		if (!numerical.equals(substring)){
			throw new RuntimeException();
		}else{
			if (substring.length() - 1 != 0){
				checkParent(numerical.substring(0, numerical.length() - 1), parent.getParent());
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
	
}
