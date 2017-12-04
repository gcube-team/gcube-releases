package org.gcube.spatial.data.gis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;
import org.junit.Test;

public class Environment {


	@Test
	public void test() throws FileNotFoundException {
		ConcurrentSkipListMap<String, String> errors=new ConcurrentSkipListMap<String,String>();
		ConcurrentSkipListSet<String> scopes=new ConcurrentSkipListSet<String>(getScopes());
		
		PrintWriter out = new PrintWriter("report.txt");
		for(String scope:scopes){
			TokenSetter.set(scope);
			try{
				GISInterface gis=GISInterface.get();
				out.println(printInfo(gis));
			}catch(Throwable t){
//				System.err.println(t);
				errors.put(scope, t.toString());
			}
		}
		
		out.println("Problematic scopes: ");
		for(Entry<String,String> err:errors.entrySet())
			out.println(err.getKey() +" --> "+err.getValue());
		
		out.flush();
		out.close();
	}



	private String printInfo(GISInterface gis)throws Exception{
		StringBuilder builder=new StringBuilder("*********************************");
		builder.append(ScopeUtils.getCurrentScope()+"\n");
		for(AbstractGeoServerDescriptor desc: gis.getCurrentCacheElements(false)){
			builder.append(desc+"\n");
			builder.append("Styles : "+desc.getStyles()+" \n");
			for(String ws:desc.getWorkspaces())
				builder.append("Datastores in "+ws+" : "+desc.getDatastores(ws)+" \n");
		}
		builder.append("Selected : "+gis.getCurrentGeoServer());
		return builder.toString();
	}


	
	
	
	private static ArrayList<String> getScopes(){
		ArrayList<String> scopes=new ArrayList<String>();

		
		//*************************** PRODUCTION
		scopes.add("/d4science.research-infrastructures.eu");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/InfraScience");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICES_TCRE");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/TabularDataLab");
		scopes.add("/d4science.research-infrastructures.eu/FARM/AquaMaps");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/PGFA-UFMT");
		scopes.add("/d4science.research-infrastructures.eu/FARM");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EuBrazilOpenBio");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBRIDGE-PSC");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ENVRIPlus");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ENVRI");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BOBLME_HilsaAWG");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ScalableDataMining");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/DESCRAMBLE");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/FAO_TunaAtlas");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/StocksAndFisheriesKB");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueCommons");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICES_TCSSM");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBRIDGE-EAB");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ARIADNE");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ProtectedAreaImpactMaps");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/OpenIt");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/AquacultureAtlasGeneration");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/Parthenos");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/IGDI");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EGIEngage");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/RStudioLab");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/TimeSeries");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/TCom");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICCAT_BFT-E");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/SoBigData.it");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BlueUptake");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/gCube");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/KnowledgeBridging");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/EFG");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/StockAssessment");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/iSearch");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/ICOS_ETC");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/VesselActivitiesAnalyzer");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/BiOnym");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/SoBigData.eu");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/PerformanceEvaluationInAquaculture");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps/StrategicInvestmentAnalysis");

		//******************** DEVELOPMENT 

		scopes.add("/gcube");
		scopes.add("/gcube/devsec");
		scopes.add("/gcube/devsec/BasicVRETest");
		scopes.add("/gcube/devsec/GSTProcessingTest");
		scopes.add("/gcube/devsec/StaTabTest");
		scopes.add("/gcube/devsec/USTORE_VRE");
		scopes.add("/gcube/devsec/TestTue10May_1822");
		scopes.add("/gcube/devsec/OpenAireDevVRE");
		scopes.add("/gcube/devsec/StaTabTest");
		scopes.add("/gcube/devsec/TabProcessing");
		scopes.add("/gcube/devsec/devVRE");
		scopes.add("/gcube/devsec/TestFri26Feb2016");
		scopes.add("/gcube/devsec/USTORE_VRE");
		scopes.add("/gcube/devsec/RMinerDev");
		scopes.add("/gcube/devsec/TabProcessing");
		scopes.add("/gcube/devsec/devVRE");
		scopes.add("/gcube/devsec/BlueVRE");
		scopes.add("/gcube/devsec/TestFri26Feb2016");
		scopes.add("/gcube/devsec/LucioVRE");

		scopes.add("/gcube/preprod");
		scopes.add("/gcube/preprod/Dorne");
		scopes.add("/gcube/preprod/preVRE");

		scopes.add("/gcube/devNext");
		scopes.add("/gcube/devNext/NextNext");

		return scopes;
	}
}
