import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructure;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructureTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.Software;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.is.ISProxyImpl;

public class IsTests {

	public static void main(String[] args) throws MalformedURLException{
		ISProxyImpl isProxy = new ISProxyImpl();
        ScopeProvider.instance.set("/gcube/devsec");

		
	
// 1. to create a new NodeTemplate	

//		NodeTemplate nt = new NodeTemplate();
//		nt.setName("IISAS");
//		nt.setDescription("NodeTemplate for gCubeSmartExecutor");
//		nt.setId("IISAS");
//		nt.setScript(new URL("https://goo.gl/WqVSes"));
//		nt.setOsTemplateId("http://schemas.openstack.org/template/os#498dd867-2e0a-49f3-a1a7-7f5f4a0b8660");
//		nt.setServiceProfile(new ResourceReference<ServiceProfile>("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
//		isProxy.addNodeTemplate(nt);
//	
        
        
// 2. to create a new ServiceEndpoint        
    
      //TBD
        
        
// 3. to create a new ServiceProviles
        
//		ServiceProfile sp = new ServiceProfile();
//		
//		Set<ResourceReference<Software>> ds = new HashSet<ResourceReference<Software>>();
//		ResourceReference<Software> rd = new ResourceReference<Software>();
//		rd.setRefId("1234");
//		ds.add(rd);
//		
//		sp.setCreationDate("test");
//		sp.setDeployedSoftware(ds);
//		sp.setDescription("test");
//		sp.setId("test");
//		sp.setMinCores(100);
//		sp.setMinRam(Long.valueOf(2));
//		sp.setSuggestedCores(2);
//		sp.setSuggestedRam(Long.valueOf(100));
//		sp.setVersion("test");
//		
//		isProxy.addServiceProfile(sp);

        
        
        
        
        
        
        
        
// 4. to create a new VMProviders
  //     VMProvider vmp = new VMProvider();
        
//        vmp.setId("d4Science Bari");
//    	Set<ResourceReference<NodeTemplate>> ds = new HashSet<ResourceReference<NodeTemplate>>();
//    	ResourceReference<NodeTemplate> rd = new ResourceReference<NodeTemplate>();
//		rd.setRefId("8a5ee109-83ad-4427-8253-7aebf834cfaa");
//        ds.add(rd);
//		ResourceReference<NodeTemplate> rd2 = new ResourceReference<NodeTemplate>();
//		rd2.setRefId("d0dd4071-eac8-46bd-8c3a-d3d95f249eca");
//		ds.add(rd2);
//        vmp.setEndpoint("eae73b0c-f060-4398-bb6c-31046b9d9950");
//        vmp.setName("d4Science-RescaBari");
//        vmp.setNodeTemplates(ds);
//        vmp.setResourceTemplates(null);
////        
//        isProxy.addVMProvider(vmp);
		
    
        
		
		
		
		
 
        
// 5. to create a new InfraTemplate (2nodes)
//		
//		OccopusInfrastructureTemplate it = new OccopusInfrastructureTemplate();
//		
//		it.setId("some id");
//		it.setOccopusDescription("infra_name: occi_infra_test" +"\n"
//				+ "user_id: somebody@somewhere.com"+"\n"
//				+ "nodes:"+"\n"
//				+ " -"+"\n"
//				+ "  name: node1"+"\n"
//				+ "  type: occi_Flavor: m1.large_7e396a11-5ff5-44e4-a87a-dc509e8e508e"+"\n"
//				+ "  scaling:"+"\n"
//				+ "   min: 1"+"\n"
//				+ "   max: 3"+"\n"
//				+ "  variables:"+"\n"
//				+ "   message: Hello World! I am a node created by Occopus."+"\n"
//				+ " -"+"\n"
//				+ "  name: node2"+"\n"
//				+ "  type: occi_Flavor: m1.xlarge_7e396a11-5ff5-44e4-a87a-dc509e8e508e"+"\n"
//				+ "  scaling:"+"\n"
//				+ "   min: 1"+"\n"
//				+ "   max: 3"+"\n"
//				+ "  variables:"+"\n"
//				+ "   message: Hello World! I am a node created by Occopus."+"\n");
//		
//		isProxy.addInfraTemplate(it);
//
//	}
	
	
	
	// 5. to create a new InfraTemplate (1nodes)
	
//			OccopusInfrastructureTemplate it = new OccopusInfrastructureTemplate();
//			
//			it.setId("some id");
//			it.setOccopusDescription("infra_name: occi_infra_test" +"\n"
//					+ "user_id: somebody@somewhere.com"+"\n"
//					+ "nodes:"+"\n"
//					+ " -"+"\n"
//					+ "  name: occi_smartexecutor_node"+"\n"
//					+ "  type: occi_Large Instance - 4 cores and 4 GB RAM_05f9d5fe-e4be-4b66-ba2b-dc83b9ec9484"+"\n"
//					+ "  scaling:"+"\n"
//					+ "   min: 1"+"\n"
//					+ "   max: 5"+"\n"
//					+ "  variables:"+"\n"
//					+ "   message: Hello World! I am a node created by Occopus."+"\n");
//			
//			isProxy.addInfraTemplate(it);

		OccopusInfrastructure aaa = new OccopusInfrastructure();
		aaa.setInfrastructureTemplate("occopusInfraTemplate3ee7cda3-0171-42a7-8a42-38c89b6912c2");
		isProxy.addInfra(aaa);
		
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
