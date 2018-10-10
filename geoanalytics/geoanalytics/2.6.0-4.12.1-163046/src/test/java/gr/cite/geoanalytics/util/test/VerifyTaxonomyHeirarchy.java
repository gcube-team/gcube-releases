//package gr.cite.geoanalytics.util.test;
//
////import gr.cite.gaap.servicelayer.ShapeManager;
//import gr.cite.gaap.servicelayer.GeocodeManager;
////import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeSystem;
////import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Geocode;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml" })
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//public class VerifyTaxonomyHeirarchy {
//
//	
//	private GeocodeManager taxonomyManager;
////	private ShapeManager shapeManager;
//	
////	@Inject
////	public void setShapManager(ShapeManager shapeManager) {
////		this.shapeManager = shapeManager;
////	}
//	
//	@Inject
//	public void setTaxonomyManager(GeocodeManager taxonomyManager) {
//		this.taxonomyManager = taxonomyManager;
//	}
//	
//	/**
//	 * @throws Exception
//	 */
//	public Boolean verifyTaxonomyTermHeirarchyOperation(String geographicalTaxonomyString) throws Exception {
//		GeocodeSystem geographicalTaxonomy = this.taxonomyManager.findGeocodeSystemByName(geographicalTaxonomyString, false);
//		List<GeocodeSystem> hierarchy = this.shapeManager.getGeographyHierarchy(geographicalTaxonomy).getMainHierarchy();
//		
//		for (GeocodeSystem taxonomy : hierarchy){
//			List<Geocode> taxonomyTerms = this.taxonomyManager.getGeocodesOfGeocodeSystem(taxonomy.getId().toString(), true, false);
//			for (Geocode taxonomyTerm : taxonomyTerms){
//				
//				String numericalPart = getNumericalPart(taxonomyTerm);
//				
//				if (numericalPart.length() != 1){
//					checkParent(numericalPart.substring(0, numericalPart.length() - 1), taxonomyTerm.getParent());
//				}else{
//					break;
//				}
//			}
//		}
//		return true;
//	}
//	
//	private static void checkParent(String substring, Geocode parent) {
//		
//		String numerical = getNumericalPart(parent);
//		
//		if (!numerical.equals(substring)){
//			throw new RuntimeException();
//		}else{
//			if (substring.length() - 1 != 0){
//				checkParent(numerical.substring(0, numerical.length() - 1), parent.getParent());
//			}
//		}
//	}
//
//	/**
//	 * @param parent
//	 * @return
//	 */
//	private static String getNumericalPart(Geocode parent) {
//		String taxonomyTermName = parent.getName();
//		String numerical = taxonomyTermName.replaceAll("\\D+","");
//		return numerical;
//	}
//	
//}
