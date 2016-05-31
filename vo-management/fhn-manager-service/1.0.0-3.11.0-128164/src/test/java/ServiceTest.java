import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.impl.FHNManagerImpl;

public class ServiceTest {

	public static void main(String[] args) throws Exception {
		
		FHNManager service = new FHNManagerImpl();
		System.out.println(service.findResourceTemplate(null));
		service.stopNode("4-1@https://carach5.ics.muni.cz:11443/compute/68935");
		//service.createNode("4-1", "2-2", "http://fedcloud.egi.eu/occi/compute/flavour/1.0#small");
		//System.out.println(service.findVMProviderbyId("4-1"));
		
		//service.findNodes("4-1", "2-2");
		//System.out.println(service.allServiceProfiles());
		//System.out.println(service.findNodes("2-2", "4-1"));
		//System.out.println(service.getNodeById("4-1:https://carach5.ics.muni.cz:11443/compute/68051"));

		//System.out.println(service.getNodeById("4-1@https://carach5.ics.muni.cz:11443/compute/68091"));
		//service.deleteNode("4-1@https://carach5.ics.muni.cz:11443/compute/68050");
		
//		System.out.println(service.getAllServiceProfiles());
//		System.out.println();
//		for(Node n: service.findNodes("4-1", "2-2")){
//			System.out.println(n.getId());
//		}
	}

}
