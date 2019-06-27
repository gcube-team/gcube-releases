package org.gcube.common.authorizationservice.cl;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;
import org.gcube.common.authorization.library.policies.Users;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.junit.Ignore;
import org.junit.Test;
public class CallTest {

	@Test
	public void resolveNodeToken() throws Exception{
		System.out.println(resolveToken("d9431600-9fef-41a7-946d-a5b402de30d6-98187548")); //81caac0f-8a0d-4923-9312-7ff0eb3f2d5e|98187548"));
		System.out.println(resolveToken("d9431600-9fef-41a7-946d-a5b402de30d6-98187548").getClientInfo().getId());
	}
	
		
	@Test
	public void requestUserTokenViaUserNameAndScope()  throws Exception {
		System.out.println(authorizationService().resolveTokenByUserAndContext("valentina.marioli", "/gcube/devNext/NextNext"));
	}
	
	@Test
	public void removeUserinContext()  throws Exception {
		authorizationService().removeAllReleatedToken("lucio.lelii", "/gcube/devsec");
	}
	
	@Test
	public void requestExternalServiceToken()  throws Exception {
		SecurityTokenProvider.instance.set(requestTestToken("/pred4s"));
		System.out.println(authorizationService().generateExternalServiceToken("storagehubapp"));
	}
	
	@Test
	public void getExternalServiceToken()  throws Exception {
		SecurityTokenProvider.instance.set(requestTestToken("/gcube/devsec"));
		System.out.println(authorizationService().retrieveExternalServiceGenerated());
	}
	
	@Test
	public void requestNodeToken()  throws Exception {
		System.out.println(_requestNodeToken());
	}

	@Test
	public void addPolicy()  throws Exception {
		SecurityTokenProvider.instance.set(requestTestToken("/gcube/devNext/NextNext"));
		List<Policy> policies = new ArrayList<Policy>();
		policies.add(new User2ServicePolicy("/gcube/devNext/NextNext", new ServiceAccess(), Users.one("lucio.lelii"), Action.ACCESS ));
		authorizationService().addPolicies(policies);
	}

	@Test
	public void getPolicies()  throws Exception{
		SecurityTokenProvider.instance.set(requestTestToken("/gcube/devNext"));
		List<Policy> policies = authorizationService().getPolicies("/gcube/devsec");
		for (Policy policy: policies)
			System.out.println(policy);
	}

	@Test
	public void removePolicy()  throws Exception {
		authorizationService().removePolicies(2, 3, 4);
	}

	@Test
	public void generateToken() throws Exception{
		System.out.println(authorizationService().generateUserToken(new UserInfo("guest", new ArrayList<String>()), "/pred4s/preprod"));
	}
	@Test(expected=RuntimeException.class)
	public void createKeyWithError()  throws Exception {
		authorizationService().generateApiKey("TEST");
	}

	@Test
	public void getSymmKey() throws Exception{
		SecurityTokenProvider.instance.set(_requestNodeToken());
		authorizationService().getSymmKey("/tmp");		
	}


	@Test
	public void createKey()  throws Exception {
		String token = requestTestToken("/gcube");
		SecurityTokenProvider.instance.set(token);
		String key = authorizationService().generateApiKey("PIPPO");
		System.out.println("key : "+key);
		System.out.println(resolveToken(key));
	}

	@Test
	public void retrieveApiKeys()  throws Exception {
		String token = requestTestToken("/gcube/devNext");
		SecurityTokenProvider.instance.set(token);
		Map<String, String> keys = authorizationService().retrieveApiKeys();
		System.out.println("keys : "+keys);

	}

	public String _requestNodeToken()  throws Exception {
		SecurityTokenProvider.instance.set(requestTestToken("/gcube"));
		String token = authorizationService().requestActivation(new ContainerInfo("workspace-repository1-d.d4science.org",80), "/gcube/devsec");
		return token;
	}

	@Test
	public void createTestToken()  throws Exception {
		System.out.println(requestTestToken("/pred4s"));
	}

	private String requestTestToken(String context) throws Exception{
		return authorizationService().generateUserToken(new UserInfo("lucio.lelii", new ArrayList<String>()), context);
	}

	private AuthorizationEntry resolveToken(String token) throws Exception{
		AuthorizationEntry entry = authorizationService().get(token);
		return entry;
	}

	/*	List<String> scopes = Arrays.asList("/d4science.research-infrastructures.eu/gCubeApps/TabularDataLab",
"/d4science.research-infrastructures.eu/FARM/AquaMaps",
"/d4science.research-infrastructures.eu/FARM/WECAFC-FIRMS",
"/d4science.research-infrastructures.eu/gCubeApps/PGFA-UFMT", 
"/d4science.research-infrastructures.eu/FARM", 
"/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling", 
"/d4science.research-infrastructures.eu/gCubeApps/EuBrazilOpenBio", 
"/d4science.research-infrastructures.eu/gCubeApps/AlieiaVRE", 
 /d4science.research-infrastructures.eu/gCubeApps/ENVRIPlus", 
 /d4science.research-infrastructures.eu/gCubeApps/ENVRI", 
 /d4science.research-infrastructures.eu/gCubeApps/ICES_DASC", 
 /d4science.research-infrastructures.eu/gCubeApps/FAO_TunaAtlas", 
 /d4science.research-infrastructures.eu/SoBigData/ResourceCatalogue", 
 /d4science.research-infrastructures.eu/gCubeApps/StocksAndFisheriesKB", 
 /d4science.research-infrastructures.eu/SoBigData/TagMe", 
 /d4science.research-infrastructures.eu/gCubeApps/BlueCommons", 
 /d4science.research-infrastructures.eu/gCubeApps/ICES_TCSSM", 
 /d4science.research-infrastructures.eu/gCubeApps/OpenIt", 
 /d4science.research-infrastructures.eu/SmartArea/SmartApps", 
 /d4science.research-infrastructures.eu", 
 /d4science.research-infrastructures.eu/gCubeApps/Parthenos", 
 /d4science.research-infrastructures.eu/gCubeApps/AquacultureAtlasGeneration", 
 /d4science.research-infrastructures.eu/gCubeApps/IGDI", 
 /d4science.research-infrastructures.eu/gCubeApps/RStudioLab", 
 /d4science.research-infrastructures.eu/SoBigData", 
 /d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject", 
 /d4science.research-infrastructures.eu/gCubeApps/rScience", 
 /d4science.research-infrastructures.eu/FARM/VME-DB", 
 /d4science.research-infrastructures.eu/gCubeApps/EllinikaPsariaVRE", 
 /d4science.research-infrastructures.eu/gCubeApps/gCube", 
 /d4science.research-infrastructures.eu/FARM/TBTI_VRE", 
 /d4science.research-infrastructures.eu/FARM/GRSF",
 /d4science.research-infrastructures.eu/gCubeApps/DocumentsWorkflow", 
 /d4science.research-infrastructures.eu/gCubeApps/ICOS_ETC", 
 /d4science.research-infrastructures.eu/SoBigData/CityOfCitizens", 
 /d4science.research-infrastructures.eu/gCubeApps/SoBigData.eu", 
 /d4science.research-infrastructures.eu/gCubeApps/BiOnym", 
 /d4science.research-infrastructures.eu/gCubeApps/PerformanceEvaluationInAquaculture", 
 /d4science.research-infrastructures.eu/gCubeApps/SmartArea", 
 /d4science.research-infrastructures.eu/gCubeApps/ICES_TCRE", 
 /d4science.research-infrastructures.eu/gCubeApps/CNR_OpenScienceTF", 
 /d4science.research-infrastructures.eu/gCubeApps/BlueBRIDGE-PSC", 
 /d4science.research-infrastructures.eu/gCubeApps/BOBLME_HilsaAWG", 
 /d4science.research-infrastructures.eu/gCubeApps/ScalableDataMining", 
 /d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab", 
 /d4science.research-infrastructures.eu/gCubeApps/DESCRAMBLE", 
 /d4science.research-infrastructures.eu/gCubeApps/ICES_FIACO", 
 /d4science.research-infrastructures.eu/gCubeApps/BlueBRIDGE-EAB", 
 /d4science.research-infrastructures.eu/gCubeApps/ARIADNE", 
 /d4science.research-infrastructures.eu/SmartArea/SmartBuilding", 
 /d4science.research-infrastructures.eu/gCubeApps/ProtectedAreaImpactMaps", 
 /d4science.research-infrastructures.eu/gCubeApps/ForkysVRE", 
 /d4science.research-infrastructures.eu/gCubeApps/EGIEngage", 
 /d4science.research-infrastructures.eu/gCubeApps/ICES_StockAssessmentAdvanced", 
 /d4science.research-infrastructures.eu/FARM/GRSF", 
 /d4science.research-infrastructures.eu/SmartArea", 
 /d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab", 
 /d4science.research-infrastructures.eu/gCubeApps/TCom", 
 /d4science.research-infrastructures.eu/gCubeApps/ICCAT_BFT-E", 
 /d4science.research-infrastructures.eu/gCubeApps/ICES_DALSA", 
 /d4science.research-infrastructures.eu/gCubeApps/SoBigData.it", 
 /d4science.research-infrastructures.eu/gCubeApps/EGIP", 
 /d4science.research-infrastructures.eu/gCubeApps/BlueUptake", 
 /d4science.research-infrastructures.eu/FARM/iMarineBoardVRE", 
 /d4science.research-infrastructures.eu/gCubeApps/KnowledgeBridging", 
 /d4science.research-infrastructures.eu/gCubeApps/EFG", 
 /d4science.research-infrastructures.eu/gCubeApps/StockAssessment", 
 /d4science.research-infrastructures.eu/gCubeApps/iSearch", 
"/d4science.research-infrastructures.eu/gCubeApps",
"/d4science.research-infrastructures.eu/gCubeApps/StrategicInvestmentAnalysis")*/

	@Test
	public void requestListOfTokenForVREs() throws Exception{
		SecurityTokenProvider.instance.set(requestTestToken("/d4science.research-infrastructures.eu"));
		
		String jrNode ="tabulardata.d4science.org";
		int jrPort =8080;
		
		AuthorizationProxy proxy = authorizationService();
		
		try(BufferedReader isr = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/Scopes.txt"))); 
				BufferedWriter bw = new BufferedWriter(new FileWriter("./src/test/resources/tokens-"+jrNode+".txt"))){
			String line = null;
			
			
			while ( (line=isr.readLine())!=null){
				System.out.println(" retrieving token for scope "+line);
				String token = proxy.requestActivation(new ContainerInfo(jrNode,jrPort), line.trim());
				bw.write(String.format("<token>%s</token>", token));
				bw.newLine();
			}
		}
	}
	@Ignore @Test
	public void checkDiff() throws Exception{
	
		
		Set<String> scopes = new HashSet<String>();
		
		try(BufferedReader isr = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/Scopes.txt")))){
			String line = null;
						
			while ( (line=isr.readLine())!=null){
				System.out.println(scopes.size()+" "+line);
				if (scopes.contains(line.trim()))
					System.out.println("already contians "+line);
				else scopes.add(line.trim());
			}
		}
		
		System.out.println("initial scope count is "+scopes.size());
		
		
		try(BufferedReader isr = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/createdScope.txt")))){
			String line = null;
			
			
			while ( (line=isr.readLine())!=null){
				if (!scopes.remove(line.trim()))
					System.out.println("cannot remove scope "+line.trim());
			}
		}
		
		System.out.println("final scope count is "+scopes.size());
		
	}

}
