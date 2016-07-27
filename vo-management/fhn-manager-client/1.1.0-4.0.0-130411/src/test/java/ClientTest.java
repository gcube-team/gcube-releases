import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.cl.FHNManagerProxy;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClient;

public class ClientTest {

	public static void main(String[] args) throws Exception {
		
		ScopeProvider.instance.set("/gcube/devsec");
		
		FHNManager client = FHNManagerProxy.getService(new URL("http://localhost:8080/fhn-manager-service-1.1.0-SNAPSHOT/rest")).build();
		//FHNManager client = FHNManagerProxy.getService(new URL("http://fedcloud.res.eng.it:80/fhn-manager-service/rest")).build();

		//FHNManagerClient client = FHNManagerProxy.getService().build();
		
		//System.out.println(client.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e", "30a2b7bd-2156-424d-ad40-0721f4e4888e",
		//		"http://fedcloud.egi.eu/occi/compute/flavour/1.0#large"));
		
		//client.stopNode("https://carach5.ics.muni.cz:11443/compute/73655");
		
		//client.startNode("https://carach5.ics.muni.cz:11443/compute/73655");

		//client.stopNode("4-1@https://carach5.ics.muni.cz:11443/compute/70101");
		
		//client.deleteNode("https://carach5.ics.muni.cz:11443/compute/73655");
		
		//System.out.println(client.allServiceProfiles());
		
		//client.deleteNode("68102");
		//System.out.println(client.allServiceProfiles());
		
		//System.out.println(client.findNodes(null, null));
		//System.out.println(client.findNodes(null,null));
		//System.out.println(client.getNodeById("https://carach5.ics.muni.cz:11443/compute/73608"));
		//System.out.println(client.findResourceTemplate(null));
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.findVMProviders("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
		System.out.println(client.getVMProviderbyId("58d494a2-505d-4550-8d48-83ade4c2b49e"));

	}
}