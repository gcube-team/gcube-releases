import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.impl.FHNManagerImpl;

public class ServiceTest {

	public static void main(String[] args) throws Exception {

		FHNManager service = new FHNManagerImpl();
		ScopeProvider.instance.set("/gcube/devsec");

		// System.out.println(service.findResourceTemplate(null));
		// service.stopNode("4-1@https://carach5.ics.muni.cz:11443/compute/68935");
		// service.createNode("4-1", "2-2",
		// "http://fedcloud.egi.eu/occi/compute/flavour/1.0#small");
		// System.out.println(service.findVMProviderbyId("4-1"));

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

//		ISSynchronizer a = new ISSynchronizer(10000);
//		Thread b = new Thread(a);
//		b.start();

		//service.findNodes(null, null);
	service.createNode("58d494a2-505d-4550-8d48-83ade4c2b49e", "30a2b7bd-2156-424d-ad40-0721f4e4888e",
				"http://fedcloud.egi.eu/occi/compute/flavour/1.0#large");
		// service.allServiceProfiles();
		// service.stopNode("https://carach5.ics.muni.cz:11443/compute/73218");

		// service.stopNode("https://carach5.ics.muni.cz:11443/compute/72965");
		// System.out.println(service.getAllServiceProfiles());
		// System.out.println();
		// for(Node n: service.findNodes("4-1", "2-2")){
		// System.out.println(n.getId());
		// }
		 
		 //service.deleteNode("https://carach5.ics.muni.cz:11443/compute/73218");
	}

}
