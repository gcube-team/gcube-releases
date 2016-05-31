import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.cl.FHNManagerProxy;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClient;

public class ClientTest {

	public static void main(String[] args) throws Exception {
		
		ScopeProvider.instance.set("/gcube/devsec");
		
		FHNManager client = FHNManagerProxy.getService(new URL("http://fedcloud.res.eng.it:80/fhn-manager-service/rest")).build();
		//FHNManagerClient client = FHNManagerProxy.getService().build();
		System.out.println(client.createNode("4-1", "2-1", "http://fedcloud.egi.eu/occi/compute/flavour/1.0#large"));
		//client.stopNode("4-1@https://carach5.ics.muni.cz:11443/compute/70101");
		//client.deleteNode("4-1@https://carach5.ics.muni.cz:11443/compute/68935");
		//client.deleteNode("68102");
		//System.out.println(client.findNodes(null,null));
		//System.out.println(client.getNodeById("4-1@https://carach5.ics.muni.cz:11443/compute/68102"));
		//System.out.println(client.findResourceTemplate(null));
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.findVMProviders("2-2"));
		//System.out.println(client.getVMProviderbyId("4-1"));

	}
}