import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.cl.FHNManagerProxy;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClient;

public class ClientTest {

	public static void main(String[] args) throws Exception {
		
		//ScopeProvider.instance.set("/gcube/devSec");

		//ScopeProvider.instance.set("/gcube/devsec");
		SecurityTokenProvider.instance.set("e3475dc7-935f-4625-9ed3-d12f6695bf3a-98187548");

		//FHNManager client = FHNManagerProxy.getService(new URL("http://node75.d4science.org:80/fhn-manager-service/rest")).build();
		//FHNManager client = FHNManagerProxy.getService(new URL("http://fedcloud.res.eng.it:80/fhn-manager-service-1.2.0-SNAPSHOT/rest")).build();
		FHNManager client = FHNManagerProxy.getService(new URL("http://node2-d-d4s.d4science.org:8080/fhn-manager-service-1.2.1-SNAPSHOT/rest")).build();

		
		//client.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#large");
		//client.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "6357d7a5-da4f-4cc6-a0c9-d19e45c47967", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");
		//client.createNode("2a6baa9b-eb0b-49bf-841e-e67f97511693", "96660d8f-e495-4320-a030-b453014ebf9c", "http://fedcloud.egi.eu/occi/compute/flavour/1.0#mem_small");
		//System.out.println(client.findResourceTemplate(null).size());
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.getAllInfrastructures());
		
		//System.out.println(client.findVMProviders(null));
		//System.out.println(client.findResourceTemplate(null));
		//client.deleteNode("https:$$fc-one.i3m.upv.es:11443$compute$24356@2a6baa9b-eb0b-49bf-841e-e67f97511693");
		//client.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.openstack.org/template/resource#m1-xlarge");
		
		//client.deleteNode("http:$$cloud.recas.ba.infn.it:8787$occi$compute$9cefca38-5cef-443c-aad3-6d8ae7760f94@87608a6f-45c3-4897-94ae-b42cc05106d9");
		//client.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.openstack.org/template/resource#m1-xlarge");
		//client.createInfrastructureByTemplate("occopusInfraTemplate11359e57-8a77-4dcc-9ce3-434841f6604e");
		//System.out.println(client.findResourceTemplate(null));
		
		//client.destroyInfrastructure("d63bd6d9-e6fe-4020-b60f-bb7d22c63541");
		
		//System.out.println(client.getAllInfrastructures());
		//System.out.println(client.getVMProviderbyId(null));
		//System.out.println(client.allServiceProfiles());
		
		//client.destroyInfrastructure("66d6c063-d07d-4571-ac8b-721d24492ff1");
		//System.out.println(client.getAllInfrastructures());
		//System.out.println(client.getInfrastructureById("866ac4f0-2943-4712-9b2e-c8989e6182a5").getInstanceSets().get("node1").getInstances().get("5c3274d1-92d3-4491-8d1d-b43494a523a2").getResource_address());
		
		//System.out.println(client.findResourceTemplate(null));
		//client.createNode("2a6baa9b-eb0b-49bf-841e-e67f97511693", "06ca2f9c-cb42-433a-9e91-864186043833", "");
		//client.createNode("0b9f4509-0d45-4f28-aa59-50afd5557877","06ca2f9c-cb42-433a-9e91-864186043833","http://schemas.openstack.org/template/resource#4");

		//client.createNode("91052d16-6466-432f-9315-dc0cfaf73af6", "30a2b7bd-2156-424d-ad40-0721f4e4888e", "http://schemas.openstack.org/template/resource#m1-xlarge");
		//
		
		//client.destroyInfrastructure("866ac4f0-2943-4712-9b2e-c8989e6182a5");
		//client.destroyInfrastructure("0fb403f5-f703-430b-9780-45c1b3edf6f7");
		//client.createInfrastructureByTemplate("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		//client.destroyInfrastructure("6a471fde-7036-4261-8f44-09cb36dbeb6d");
		//client.destroyInfrastructure(infrastructureId);
		//System.out.println(client.getAllInfrastructures());
		//System.out.println(client.getInfrastructureById("081c5af1-3ee5-4084-b97f-fec9ba0ee7b6"));
		//client.destroyInfrastructure("081c5af1-3ee5-4084-b97f-fec9ba0ee7b6");
		
		//System.out.println(client.getAllInfrastructures());
		//client.getAllInfrastructures();
		//System.out.println(client.getInfrastructureById("cda7e9d5-a495-4a69-a82a-93c857454a9c"));
		//client.destroyInfrastructure("cda7e9d5-a495-4a69-a82a-93c857454a9c");
		
		//client.destroyInfrastructure(infrastructureId);
		//client.destroyInfrastructure("da0c1ea1-e30b-4c4a-8fa4-02a78119bf02");
		//System.out.println(client.allServiceProfiles());
		//FHNManager client2 = FHNManagerProxy.getService(new URL("http://ngalante-ThinkPad-X250:8080/fhn-manager-service/rest")).build();
		//System.out.println(client.findNodes(null, null));
		//client2.allServiceProfiles();
		//System.out.println(client2.findNodes(null, null));
		//System.out.println(client.findVMProviders(null));
		//System.out.println(client.findResourceTemplate(null));
		//System.out.println(client.findNodes(null, null));
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.getNodeById("https://carach5.ics.muni.cz:11443/compute/73991"));
		//System.out.println(client2.getNodeById("d3492201-0294-434d-a9ad-a5eb522b16c9"));
		//client.startNode("https:$$carach5.ics.muni.cz:11443$compute$74087@58d494a2-505d-4550-8d48-83ade4c2b49e");
		//FHNManager client = FHNManagerProxy.getService(new URL("http://fedcloud.res.eng.it:80/fhn-manager-service/rest")).build();
		
		//cesga
		//client.createNode("c6212b69-3981-455c-aeae-f88242fe9547", "06ca2f9c-cb42-433a-9e91-864186043833", "http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#extra_large");
		//bari
		client.createNode("0b9f4509-0d45-4f28-aa59-50afd5557877", "06ca2f9c-cb42-433a-9e91-864186043833", "http://schemas.openstack.org/template/resource#9");
		//iisas
		
		//upv
		
		
		
		//FHNManagerClient client = FHNManagerProxy.getService().build();
		
		//		String q = "https://carach5.ics.muni.cz:11443/compute/73607";
		//		System.out.println(q);
		//		String g1 = URLEncoder.encode(q,"UTF-8");
		//		System.out.println(g1);
		//client.stopNode("https://carach5.ics.muni.cz:11443/compute/73991");
		//System.out.println(client.getNodeById("https:$$carach5.ics.muni.cz:11443$compute$74087@58d494a2-505d-4550-8d48-83ade4c2b49e"));
		//System.out.println(client.getVMProviderbyId("58d494a2-505d-4550-8d48-83ade4c2b49e"));
		
		//System.out.println(client.getNodeById("https:$$carach5.ics.muni.cz:11443$compute$74070@58d494a2-505d-4550-8d48-83ade4c2b49e"));
		//client.stopNode("f3ea77ad-a22a-4ff8-a48c-ed1f19b0481b");
		
		//System.out.println(client.getNodeById("https:$$carach5.ics.muni.cz:11443$compute$74070@58d494a2-505d-4550-8d48-83ade4c2b49e"));
		
		//client.deleteNode("https:$$carach5.ics.muni.cz:11443$compute$74098@58d494a2-505d-4550-8d48-83ade4c2b49e");
		//client.startNode("f3ea77ad-a22a-4ff8-a48c-ed1f19b0481b");
		//client.deleteNode("https:$$carach5.ics.muni.cz:11443$compute$74070@58d494a2-505d-4550-8d48-83ade4c2b49e");
		//client.deleteNode("https://carach5.ics.muni.cz:11443/compute/73918");
		
		//client.deleteNode("https:$$carach5.ics.muni.cz:11443$compute$74100@58d494a2-505d-4550-8d48-83ade4c2b49e");
//		System.out.println(client.createNode("700b6cb7-1fc2-4334-9dd7-4ae759bf9298", "6b59119c-e9f1-42d8-a26d-c5c0cac88951",
//				"http://fedcloud.egi.eu/occi/compute/flavour/1.0#large"));
//		
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.findNodes(null, null));
		//client.deleteNode("https:$$carach5.ics.muni.cz:11443$compute$74102@58d494a2-505d-4550-8d48-83ade4c2b49e");
		
		//client.deleteNode("https:$$carach5.ics.muni.cz:11443$compute$74097@58d494a2-505d-4550-8d48-83ade4c2b49e<");
		//client.stopNode("https://carach5.ics.muni.cz:11443/compute/73655");
		//client.allServiceProfiles();
		//System.out.println(client.findResourceTemplate("58d494a2-505d-4550-8d48-83ade4c2b49e"));
		
		//System.out.println(client.findNodes(null, null));
		//client.getVMProviderbyId("58d494a2-505d-4550-8d48-83ade4c2b49e");
		//client.stopNode("0f65955f-e492-42bd-89b5-e16469810cec");
		//client2.startNode("d3492201-0294-434d-a9ad-a5eb522b16c9");
		//client2.deleteNode("d3492201-0294-434d-a9ad-a5eb522b16c9");


		//client.stopNode("4-1@https://carach5.ics.muni.cz:11443/compute/70101");
		
		//client.deleteNode("https://carach5.ics.muni.cz:11443/compute/73655");
		
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.findResourceTemplate(null));
		
		//client.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e", "30a2b7bd-2156-424d-ad40-0721f4e4888e",
		//				"http://fedcloud.egi.eu/occi/compute/flavour/1.0#large");
		//System.out.println(client.findNodes(null, null));
		//client.deleteNode("68102");
		//System.out.println(client.allServiceProfiles());
		
		//System.out.println(client.findNodes(null, null));
		//System.out.println(client.findNodes(null,null));
		//System.out.println(client.getNodeById("https://carach5.ics.muni.cz:11443/compute/73608"));
		//System.out.println(client.findResourceTemplate(null));
		//System.out.println(client.allServiceProfiles());
		//System.out.println(client.findVMProviders("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
		//System.out.println(client.getVMProviderbyId("58d494a2-505d-4550-8d48-83ade4c2b49e"));

	}
}