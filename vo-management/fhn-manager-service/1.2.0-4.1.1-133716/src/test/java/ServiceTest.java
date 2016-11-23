import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.impl.FHNManagerImpl;
import org.gcube.resources.federation.fhnmanager.is.ISProxyImpl;
import org.gcube.vomanagement.occi.OcciConnector;

public class ServiceTest {

	public static void main(String[] args) throws Exception {

		FHNManager service = new FHNManagerImpl();
		ScopeProvider.instance.set("/gcube/devsec");
		
		
		//service.getAllInfrastructures();
		//
		// ISProxyImpl isProxy = new ISProxyImpl();
		// ScopeProvider.instance.set("/gcube");
		// isProxy.updateIs();
		// ScopeProvider.instance.set("/gcube/devsec");
		// isProxy.updateIs();

		// System.out.println(service.findResourceTemplate(null));
		// service.stopNode("4-1@https://carach5.ics.muni.cz:11443/compute/68935");
		// service.createNode("4-1", "2-2",
		// "http://fedcloud.egi.eu/occi/compute/flavour/1.0#small");
		// System.out.println(service.findVMProviderbyId("4-1"));
		// System.out.println(service.getNodeById("https://carach5.ics.muni.cz:11443/compute/73991"));
		// System.out.println(service.findResourceTemplate(null));
		// service.findNodes("4-1", "2-2");
		// System.out.println(service.allServiceProfiles());
		// System.out.println(service.findNodes(null, null));
		// System.out.println(service.getNodeById("4-1:https://carach5.ics.muni.cz:11443/compute/68051"));

		// System.out.println(service.getNodeById("4-1@https://carach5.ics.muni.cz:11443/compute/68091"));
		// service.deleteNode("4-1@https://carach5.ics.muni.cz:11443/compute/68050");

		// System.out.println(service.getNodeById("4b901272-c8a3-4d18-a7f8-3c877470f3ed"));
		// service.startNode("https://carach5.ics.muni.cz:11443/compute/72965");
		// service.findNodes(null, null);
		// service.findNodes(null, null);

		// ISSynchronizer a = new ISSynchronizer(10000);
		// Thread b = new Thread(a);
		// b.start();
		// System.out.println(service.findNodes(null, null));
		// service.findNodes(null, null);

		// service.startNode("https:$$cloud.cesga.es:3202$compute$63918@c6212b69-3981-455c-aeae-f88242fe9547");
		// service.deleteNode("https:$$cloud.cesga.es:3202$compute$63918@c6212b69-3981-455c-aeae-f88242fe9547");

		//System.out.println(service.findVMProviders("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
		
		//System.out.println(service.findVMProviders(null));
	
		//System.out.println(service.findResourceTemplate("c6212b69-3981-455c-aeae-f88242fe9547"));
		
		// System.out.println(service.findResourceTemplate("91052d16-6466-432f-9315-dc0cfaf73af6"));

		
	/*******************************************************************/
		//fedcloud.egi.eu
		//Cesnet
		//dataminer
		//service.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e","6357d7a5-da4f-4cc6-a0c9-d19e45c47967","http://fedcloud.egi.eu/occi/compute/flavour/1.0#large");
		//executor
		//service.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e","30a2b7bd-2156-424d-ad40-0721f4e4888e","http://fedcloud.egi.eu/occi/compute/flavour/1.0#large");

	/*******************************************************************/

		//d4Science.org
		//Cesga
		//data miner
		//service.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "6357d7a5-da4f-4cc6-a0c9-d19e45c47967", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");
		//executor - osMixin not found
		//service.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");

		//Recas
		//executor - published with hostname = ip
		//service.createNode("7ff833e2-1e26-4ce4-b9ab-8af03abf86e9", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.openstack.org/template/resource#9");
		//data miner - not published
		//service.createNode("7ff833e2-1e26-4ce4-b9ab-8af03abf86e9", "6357d7a5-da4f-4cc6-a0c9-d19e45c47967", "http://schemas.openstack.org/template/resource#9");
	
		//IIAS
		//executor - published
		//service.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.openstack.org/template/resource#m1-xlarge");
		//data miner - not published
		//service.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "6357d7a5-da4f-4cc6-a0c9-d19e45c47967", "http://schemas.openstack.org/template/resource#m1-xlarge");

	/*******************************************************************/
	
		//service.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");
		//service.createInfrastructureByTemplate("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		service.createInfrastructureByTemplate("occopusInfraTemplate11359e57-8a77-4dcc-9ce3-434841f6604e");
		//service.startNode("https:$$carach5.ics.muni.cz:11443$compute$76522@58d494a2-505d-4550-8d48-83ade4c2b49e");

		//System.out.println(service.findResourceTemplate(null));
		//
		// service.createNode("91052d16-6466-432f-9315-dc0cfaf73af6",
		// "30a2b7bd-2156-424d-ad40-0721f4e4888e",
		// "http://schemas.openstack.org/template/resource#m1-xlarge");
		//
		// System.out.println(service.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e",
		// "30a2b7bd-2156-424d-ad40-0721f4e4888e",
		// "http://fedcloud.egi.eu/occi/compute/flavour/1.0#large"));

		// System.out.println(service.allServiceProfiles());
		// for(Node n: service.findNodes(null, null)){
		// System.out.println(n.getId());
		// }
		//
		// service.deleteNode("https:$$carach5.ics.muni.cz:11443$compute$75702@700b6cb7-1fc2-4334-9dd7-4ae759bf9298");

		// service.allServiceProfiles();
		// service.stopNode("https://carach5.ics.muni.cz:11443/compute/73218");

		// service.stopNode("https://carach5.ics.muni.cz:11443/compute/72965");
		// System.out.println(service.getAllServiceProfiles());
		// System.out.println();
		// for(Node n: service.findNodes("4-1", "2-2")){
		// System.out.println(n.getId());
		// }

		// System.out.println(service.findResourceTemplate("c6212b69-3981-455c-aeae-f88242fe9547"));
		// service.findVMProviders("30a2b7bd-2156-424d-ad40-0721f4e4888e");
		// System.out.println(service.createInfrastructureByTemplate("88a66f67-c831-4aed-ad0f-6f0e9036e17c"));

		// System.out.println(service.createNode("91052d16-6466-432f-9315-dc0cfaf73af6",
		// "30a2b7bd-2156-424d-ad40-0721f4e4888e",
		// "http://schemas.openstack.org/template/resource#m1-xlarge"));
		//
		//

		//service.createInfrastructureByTemplate("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		// service.getInfrastructureById("081c5af1-3ee5-4084-b97f-fec9ba0ee7b6");
		// service.destroyInfrastructure("ad9d5ba6-c987-407a-810a-d6947ad08b0d");
		// service.deleteNode("https://carach5.ics.muni.cz:11443/compute/73218");
	}

}
