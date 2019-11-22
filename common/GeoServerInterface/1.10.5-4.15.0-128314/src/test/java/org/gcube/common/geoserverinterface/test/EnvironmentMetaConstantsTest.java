package org.gcube.common.geoserverinterface.test;

import java.util.Date;

import org.gcube.common.geoserverinterface.bean.iso.EnvironmentConfiguration;
import org.gcube.common.geoserverinterface.bean.iso.GcubeISOMetadata;
import org.gcube.common.geoserverinterface.bean.iso.Thesaurus;
import org.gcube.common.scope.api.ScopeProvider;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.xml.XML;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;

public class EnvironmentMetaConstantsTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//*************** Environment Configuration creation + Publishing
		
		EnvironmentConfiguration config=generateiMarine();
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		String id=config.publish().id();
		System.out.println("Published configuration [res ID = "+id+"] under scope "+ScopeProvider.instance.get());
		
		//*************** Environemnt Configuration Check
//		ScopeProvider.instance.set("/gcube/devsec");
//		
//		XML.marshal(fillMeta().getMetadata(),System.out);
	}

	
	private static GcubeISOMetadata fillMeta() throws Exception{
		GcubeISOMetadata meta=new GcubeISOMetadata();
		meta.setAbstractField("This metadata is just a test");
		meta.setCreationDate(new Date(System.currentTimeMillis()));
		meta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		meta.setGeometricObjectType(GeometricObjectType.SURFACE);
		meta.setPresentationForm(PresentationForm.MAP_DIGITAL);
		meta.setPurpose("Purpose of this layer is to test the library");
		meta.setResolution(0.5d);
		meta.setTitle("My Test Layer");
		meta.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
		meta.setUser("fabio.sinibaldi");		
		
		
		meta.addCredits("Thanks to me");
		meta.addGraphicOverview("http://www.d4science.org/D4ScienceOrg-Social-theme/images/custom/D4ScienceInfrastructure.png");
		
		Thesaurus generalThesaurus=meta.getConfig().getThesauri().get("General");		
		meta.addKeyword("TEST", generalThesaurus);
		meta.addKeyword("Geoserverinterface", generalThesaurus);
		
		meta.addTopicCategory(TopicCategory.BIOTA);
		return meta;
	}
	
	
	

	
	
	private static EnvironmentConfiguration generateiMarine(){
		EnvironmentConfiguration config=new EnvironmentConfiguration();
		//Protocol declarations
		config.setWmsProtocolDeclaration("OGC:WMS-1.3.0-http-get-map");
		config.setWfsProtocolDeclaration("OGC:WFS-1.0.0-http-get-feature");
		config.setWcsProtocolDeclaration("WWW:LINK-1.0-http--link");
		config.setHttpProtocolDeclaration("WWW:LINK-1.0-http--link");
		config.setDefaultCRS("EPSG:4326");

		//Project
		config.setProjectName("iMarine Consortium");
		config.setProjectCitation("This layer has been produced by iMarine (www.i-marine.eu). " +
				"iMarine (283644) is funded by the European Commission under Framework Programme 7 ");
		
		//Distributor
		
		config.setDistributorEMail("info@i-marine.eu");
		config.setDistributorIndividualName("iMarine.eu");
		config.setDistributorOrganisationName(config.getProjectName());
		config.setDistributorSite("http://www.i-marine.eu");
		
		//Provider
		
		config.setProviderEMail("support@i-marine.eu");
		config.setProviderIndividualName("iMarine Consortium Technical Support");
		config.setProviderOrganisationName(config.getProjectName());
		config.setProviderSite("http://www.i-marine.eu");
		
		config.setLicense("CC-BY-SA");
		
		//Thesauri
		Thesaurus OBIS=new Thesaurus(KeywordType.THEME, "OBIS", new Date(System.currentTimeMillis()),
				"Intergovernmental Oceanographic Commission (IOC) of UNESCO. The Ocean Biogeographic Information System. Web. http://www.iobis.org. (Consulted on DATE)",
				"http://www.iobis.org", "UNESCO");
		Thesaurus WORMS=new Thesaurus(KeywordType.THEME, "WORMS", new Date(System.currentTimeMillis()), "Appeltans W, Bouchet P, Boxshall GA, De Broyer C, de Voogd NJ, " +
				"Gordon DP, Hoeksema BW, Horton T, Kennedy M, Mees J, Poore GCB, Read G, Stöhr S, Walter TC, Costello MJ. (eds) (2012). " +
				"World Register of Marine Species. Accessed at http://www.marinespecies.org on 2013-05-06.", "http://www.marinespecies.org", "World Register of Marine Species");
		Thesaurus IRMNG=new Thesaurus(KeywordType.THEME,"IRMNG",new Date(System.currentTimeMillis()),"OBIS - Australia","http://www.obis.org.au/irmng/","Interim Register of Marine and Nonmarine Genera");
		Thesaurus CATALOG_OF_LIFE=new Thesaurus(KeywordType.THEME, "CATALOG OF LIFE",new Date(System.currentTimeMillis()),"Species 2000 & ITIS Catalogue of Life: 2013 Annual Checklist",
				"http://www.catalogueoflife.org/","Integrated Taxonomic Information System");
		Thesaurus ITIS=new Thesaurus(KeywordType.THEME, "ITIS", new Date(System.currentTimeMillis()), 
				"Retrieved on DATE, from the Integrated Taxonomic Information System on-line database", "http://www.itis.gov/", "Integrated Taxonomic Information System");
		Thesaurus FISHBASE=new Thesaurus(KeywordType.THEME, "FISHBASE", new Date(System.currentTimeMillis()), "FishBase is a global species database of fish species (specifically finfish)."
				, "http://www.fishbase.org/search.php", "IFM-GEOMAR");
		Thesaurus THREE_A_CODE=new Thesaurus(KeywordType.THEME,"3A Code",new Date(System.currentTimeMillis()));
		
		Thesaurus GENERAL=new Thesaurus(KeywordType.THEME,"General",new Date(System.currentTimeMillis()));
		
		
		Thesaurus[] thesauri=new Thesaurus[]{
				OBIS,
				WORMS,
				IRMNG,
				CATALOG_OF_LIFE,
				ITIS,
				FISHBASE,
				THREE_A_CODE,
				GENERAL
		};
		for(Thesaurus t : thesauri)
			config.getThesauri().put(t.getTitle(), t);
		
		return config;
	}
}
