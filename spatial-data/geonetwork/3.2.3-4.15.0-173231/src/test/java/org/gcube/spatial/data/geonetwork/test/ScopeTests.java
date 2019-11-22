package org.gcube.spatial.data.geonetwork.test;

import java.util.ArrayList;

import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.junit.Test;

import it.geosolutions.geonetwork.util.GNSearchRequest;

public class ScopeTests {

	//	String[] scopes=new String[]{
	////			"/gcube",
	////			"/gcube/devsec",
	////			"/gcube/devsec/devVRE",
	////			"/gcube/devNext/NextNext"
	//			
	//			"/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling",
	//			"/d4science.research-infrastructures.eu/gCubeApps"
	//			
	//	};

	private static ArrayList<String> scopes=new ArrayList<String>();
	static{
	// DEV
		
		scopes.add("/gcube");
		scopes.add("/gcube/devNext");
		scopes.add("/gcube/devsec");
		
		scopes.add("/gcube/devNext/NextNext");
        
	      scopes.add("/gcube/devsec/StaTabTest");
	        
	        
	      scopes.add("/gcube/devsec/RMinerDev");
	        
	        
	        
	      scopes.add("/gcube/devsec/TabProcessing");
	        
	      scopes.add("/gcube/devsec/devVRE");
	      
	      
	      
//		scopes.add("/gcube/preprod/preVRE");
		
		
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/SIASPA");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/InfraScience");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICES_TCRE");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/TabularDataLab");
//		scopes.add("/d4science.research-infrastructures.eu/FARM/AquaMaps");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/PGFA-UFMT");
//		scopes.add("/d4science.research-infrastructures.eu/FARM");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EuBrazilOpenBio");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBRIDGE-PSC");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ENVRIPlus");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ENVRI");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BOBLME_HilsaAWG");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ScalableDataMining");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/DESCRAMBLE");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/FAO_TunaAtlas");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/StocksAndFisheriesKB");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueCommons");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICES_TCSSM");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBRIDGE-EAB");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ARIADNE");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ProtectedAreaImpactMaps");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/OpenIt");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/AquacultureAtlasGeneration");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/Parthenos");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/IGDI");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EGIEngage");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/RStudioLab");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/TimeSeries");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/TCom");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICCAT_BFT-E");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/SoBigData.it");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueUptake");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/gCube");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/KnowledgeBridging");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EFG");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/StockAssessment");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/iSearch");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICOS_ETC");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/VesselActivitiesAnalyzer");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BiOnym");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/SoBigData.eu");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/PerformanceEvaluationInAquaculture");
//		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/StrategicInvestmentAnalysis");
	}

	
	
	

	@Test
	public void testUtils(){

		for(String scope:scopes){		
			TokenSetter.set(scope);
			System.out.println("Setted scope "+scope);
			System.out.println("Scope name : "+ScopeUtils.getCurrentScopeName());
			System.out.println("Parents"+ScopeUtils.getParentScopes());
		}
	}

	@Test
	public void testConfigs() throws MissingConfigurationException, MissingServiceEndpointException, Exception{
		for(String scope:scopes){
			System.out.println("SCOPE : "+scope);
			TokenSetter.set(scope);
			Configuration config=TestConfiguration.getClient().getConfiguration();
			System.out.println(config.getScopeConfiguration());
			Account account=config.getScopeConfiguration().getAccounts().get(Type.CKAN);
			System.out.println("CKAN : "+account.getUser()+" "+account.getPassword());;

		}

		Configuration config=TestConfiguration.getClient().getConfiguration();
		System.out.println(config.getAdminAccount().getUser()+" "+config.getAdminAccount().getPassword());
	}

	@Test
	public void testGNUsersAndGroups() throws Exception{
		TokenSetter.set(scopes.get(0));
		GeoNetworkAdministration admin=TestConfiguration.getClient();
		admin.login(LoginLevel.ADMIN);
		System.out.println(admin.getGroups());
		System.out.println(admin.getUsers());
	}

	@Test
	public void getCount() throws Exception{
		for(String scope:scopes){
			TokenSetter.set(scope);
			GeoNetworkReader reader=TestConfiguration.getClient();
			final GNSearchRequest req=new GNSearchRequest();
			req.addParam(GNSearchRequest.Param.any,"");
			int publicCount=reader.query(req).getCount();

			reader.login(LoginLevel.CKAN);
			int totalCount=reader.query(req).getCount();

			reader.login(LoginLevel.ADMIN);
			int existingCount=reader.query(req).getCount();
			System.out.println("SCOPE "+scope+" found "+totalCount+" (public access : "+publicCount+", local +"+(totalCount-publicCount)+", existing in instance : "+existingCount+")");
		}
	}
}
