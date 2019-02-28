import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
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
		
		SecurityTokenProvider.instance.set("e3475dc7-935f-4625-9ed3-d12f6695bf3a-98187548");
		
		
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
		//System.out.println(service.findResourceTemplate(null).size());
		// System.out.println(service.getNodeById("4b901272-c8a3-4d18-a7f8-3c877470f3ed"));
		// service.startNode("https://carach5.ics.muni.cz:11443/compute/72965");
		// service.findNodes(null, null);
		// service.findNodes(null, null);

		// ISSynchronizer a = new ISSynchronizer(10000);
		// Thread b = new Thread(a);
		// b.start();
		// System.out.println(service.findNodes(null, null));
		// service.findNodes(null, null);
		//service.stopNode("http:$$cloud.recas.ba.infn.it:8787$occi$compute$72f649b9-181a-488b-8408-01f2143dfa5d@0b9f4509-0d45-4f28-aa59-50afd5557877");
		// service.startNode("https:$$cloud.cesga.es:3202$compute$63918@c6212b69-3981-455c-aeae-f88242fe9547");
		// service.deleteNode("https:$$cloud.cesga.es:3202$compute$63918@c6212b69-3981-455c-aeae-f88242fe9547");

		//System.out.println(service.findVMProviders("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
		//service.findResourceTemplate(null);
		//System.out.println(service.findNodes(null, null));
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
		//data miner - not published
		service.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "06ca2f9c-cb42-433a-9e91-864186043833", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");
		//executor
		//service.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");

		//Recas
		//executor - published with hostname = ip
		//service.createNode("0b9f4509-0d45-4f28-aa59-50afd5557877", "96660d8f-e495-4320-a030-b453014ebf9c", "http://schemas.openstack.org/template/resource#10");
		//service.startNode("http:$$cloud.recas.ba.infn.it:8787$occi$compute$672c0292-2bd8-49ad-941a-e1d7086cebb7@0b9f4509-0d45-4f28-aa59-50afd5557877");
		//service.startNode("http:$$cloud.recas.ba.infn.it:8787$occi$compute$1313cf25-28b5-46d0-9b7a-63ff0396334c@0b9f4509-0d45-4f28-aa59-50afd5557877");
		//data miner - not published
		//service.createNode("0b9f4509-0d45-4f28-aa59-50afd5557877", "db1d05ed-672e-4427-974d-f03483912e5a", "http://schemas.openstack.org/template/resource#10");
		//service.startNode("http:$$cloud.recas.ba.infn.it:8787$occi$compute$ccda6bd8-9177-4731-80b6-44da67f892fb@0b9f4509-0d45-4f28-aa59-50afd5557877");
		
		//IIAS
		//executor - published
		//service.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.openstack.org/template/resource#m1-xlarge");
		//data miner - not published
		//service.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "6357d7a5-da4f-4cc6-a0c9-d19e45c47967", "http://schemas.openstack.org/template/resource#m1-xlarge");

		//UPV-GRyCAP
		//executor - not published cause IP and hostname missing
		//service.createNode("2a6baa9b-eb0b-49bf-841e-e67f97511693", "96660d8f-e495-4320-a030-b453014ebf9c", "http://schemas.fedcloud.egi.eu/occi/infrastructure/resource_tpl#mem_extra_large");
		//data miner - not published cause IP and hostname missing
		//service.createNode("2a6baa9b-eb0b-49bf-841e-e67f97511693", "db1d05ed-672e-4427-974d-f03483912e5a", "http://fedcloud.egi.eu/occi/compute/flavour/1.0#large");

		//System.out.println(service.getNodeById("http:$$cloud.recas.ba.infn.it:8787$occi$compute$cdcb78b0-1f60-4642-9ac6-39725feeb3b8@0b9f4509-0d45-4f28-aa59-50afd5557877").getCores());
		
		//Node a = service.getNodeById("http:$$cloud.recas.ba.infn.it:8787$occi$compute$cdcb78b0-1f60-4642-9ac6-39725feeb3b8@0b9f4509-0d45-4f28-aa59-50afd5557877");
		
		/*******************************************************************/
	
		//service.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");
		//service.createInfrastructureByTemplate("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		//service.createInfrastructureByTemplate("occopusInfraTemplate11359e57-8a77-4dcc-9ce3-434841f6604e");
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

		//service.allServiceProfiles();
		// service.stopNode("https://carach5.ics.muni.cz:11443/compute/73218");

		// service.stopNode("https://carach5.ics.muni.cz:11443/compute/72965");
		// System.out.println(service.getAllServiceProfiles());
		// System.out.println();
		// for(Node n: service.findNodes("4-1", "2-2")){
		// System.out.println(n.getId());
		// }

		//service.findResourceTemplate(null);
		//System.out.println(SecurityTokenProvider.instance.get());
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
