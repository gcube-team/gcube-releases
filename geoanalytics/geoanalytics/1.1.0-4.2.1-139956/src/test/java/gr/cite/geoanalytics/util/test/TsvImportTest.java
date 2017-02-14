package gr.cite.geoanalytics.util.test;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.manager.DataManager;
import gr.cite.geoanalytics.manager.ImportManager;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.junit.After;
import org.junit.Test;

@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml" })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class TsvImportTest {
	
	final static String TSV_WITH_UNIT = "unit,geo\\time	2014 	2013 	2012 	2011 	2010 	2009 	2008 	2007 	2006 	2005 	2004 	2003 	2002 	2001 	2000" + '\n' 
	+ "EUR_HAB,AT	38500 	38100 	37600 	36800 	35200 	34300 	35100 	34000 	32200 	30800 	29600 	28500 	28000 	27400 	26600" + '\n' 
	+ "EUR_HAB,AT1	38700 	38500 	38100 	37600 	36300 	35600 	36200 	35100 	33500 	32000 	31100 	30100 	29900 	29200 	28500\n" + '\n' 
	+ "EUR_HAB,AT11	26500 	26100 	25500 	24300 	23400 	22500 	22500 	22100 	21000 	20300 	20200 	19200 	18700 	17800 	17300\n" + '\n'
	+ "EUR_HAB,AT12	31400 	31200 	30700 	30100 	28700 	28000 	28900 	27900 	26100 	24900 	24300 	23000 	22500 	22100 	21800\n" + '\n'
	+ "EUR_HAB,AT13	47300 	47300 	47100 	46800 	45700 	45000 	45500 	44100 	42700 	40800 	39600 	38900 	39100 	38300 	37100\n";
	
	final static String TSV_WITHOUT_UNIT = "geo\time	2003 	2004 	2005 	2006 	2007 	2008 	2009 	2010 	2011 	2012 	2013 	2014\n" +
			"AL	: 	: 	51.2 	51.4 	51.9 	53.8 	51.4 	52.7 	51.7 	48.2 	48.5 	47.8\n" +
			"AT	103.5 	103.4 	102.6 b	102.0 	102.7 	105.2 	107.9 	105.1 	105.9 	105.2 	106.1 	105.8\n" +
			"BA	: 	: 	51.3 	52.6 	53.1 	57.3 	57.5 	55.7 	55.4 	53.3 	52.9 	51.8\n" + 
			"BE	106.7 	106.9 	106.5 b	107.8 	107.5 	110.4 	112.4 	110.2 	110.3 	109.5 	109.9 	108.7\n" +
			"BG	40.8 	42.0 	43.3 b	44.9 	45.6 	49.4 	51.3 	50.0 	50.2 	50.2 	49.2 	47.9\n" + 
			"CH	144.1 	141.0 	137.7 b	134.7 	125.2 	128.6 	137.6 	148.1 	160.3 	153.5 	148.2 	148.9\n"; 
	
	private ImportManager importManager;
	private TaxonomyManager taxonomyManager;
	private ShapeManager shapeManager;
	private DataManager dataManager;
	
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
	
	@Test 
	public void tsvWithUnitImport() throws Exception{
		//importDataFromTsv();
	}

	/**
	 * @throws Exception
	 */
	public void importDataFromTsv(UUID templateLayerTaxonomyTermId, UUID geographycalTaxonomyId, String tsv, String newLayerNameForTaxonomyTerm, UUID taxonomyOfLayerTaxonomyTerm) throws Exception {
		this.dataManager.importDataToShapesOfLayerUsingTsvAndUpdate(templateLayerTaxonomyTermId, tsv, newLayerNameForTaxonomyTerm, taxonomyOfLayerTaxonomyTerm);
	}
	
/*	@Test
	public void tsvWIthoutUnitImport() throws Exception{
		this.importManager.tsvParsing(new TaxonomyTerm(), new TaxonomyTerm(), new Taxonomy(), TSV_WITHOUT_UNIT);
	}*/
	
/*	@Test
	public void testNewDaoQuerry() throws Exception{
		TaxonomyTerm termForShape = this.taxonomyManager.findTermByName("SettlementGytheion 1", false);
		TaxonomyTerm layerTerm = this.taxonomyManager.findTermByName("Taxon1Term6", false);
		this.shapeManager.getShapeFromLayerTermAndShapeTerm(layerTerm, termForShape);
	}*/
	
	@After
	public void afterTest(){
		
	}
}
