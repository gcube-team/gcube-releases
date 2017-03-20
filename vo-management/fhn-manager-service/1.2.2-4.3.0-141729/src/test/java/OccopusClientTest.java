import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructureTemplate;
import org.gcube.resources.federation.fhnmanager.is.ISProxyImpl;
import org.gcube.resources.federation.fhnmanager.occopus.OccopusClient;

public class OccopusClientTest {

	public static void main(String[] args) {
		
		ScopeProvider.instance.set("/gcube");

		String occopusServerUrl = "http://127.0.0.1:5000/";
		
		OccopusClient oc = new OccopusClient(occopusServerUrl);

		
		ISProxyImpl is = new ISProxyImpl();
		OccopusInfrastructureTemplate t = is.returnInfraTemplate("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		
		
		
		//oc.createInfrastructure(t.getOccopusDescription());
		
		
		
		

		


	}

}
