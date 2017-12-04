package org.gcube.vremanagement.vremodeler;

import java.util.Calendar;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.VOID;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityItem;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityList;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityNodes;
import org.gcube.vremanagement.vremodeler.stubs.GHNArray;
import org.gcube.vremanagement.vremodeler.stubs.GHNType;
import org.gcube.vremanagement.vremodeler.stubs.GHNsPerFunctionality;
import org.gcube.vremanagement.vremodeler.stubs.ModelerFactoryPortType;
import org.gcube.vremanagement.vremodeler.stubs.ModelerServicePortType;
import org.gcube.vremanagement.vremodeler.stubs.Report;
import org.gcube.vremanagement.vremodeler.stubs.ReportList;
import org.gcube.vremanagement.vremodeler.stubs.ResourceDescriptionItem;
import org.gcube.vremanagement.vremodeler.stubs.ResourceItem;
import org.gcube.vremanagement.vremodeler.stubs.RunningInstanceMessage;
import org.gcube.vremanagement.vremodeler.stubs.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodeler.stubs.SetFunctionality;
import org.gcube.vremanagement.vremodeler.stubs.VREDescription;
import org.gcube.vremanagement.vremodeler.stubs.service.ModelerFactoryServiceAddressingLocator;
import org.gcube.vremanagement.vremodeler.stubs.service.ModelerServiceAddressingLocator;
import org.gcube.vremanagement.vremodeler.utils.Utils;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;


public class ModelerTest {
	
	public static void main(String[] args) throws Exception{
		
		//listVres();
		deploy();
		//undeploy("988e7860-072e-11e2-84a1-c0b7c6162ae8");
		//printStatus("c0591f50-07fe-11e2-83dc-b18abb79e665");
	}
	
	public static void printStatus(String id) throws Exception{
		//9ee98090-07ee-11e2-9ef9-eacae859b49f
		ModelerFactoryServiceAddressingLocator mfal =new ModelerFactoryServiceAddressingLocator();
		/*EndpointReferenceType epr= results.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/vremodeler/ModelerFactoryService");
	System.out.println(epr);*/
		EndpointReferenceType epr= new EndpointReferenceType(new URI("http://node24.d.d4science.research-infrastructures.eu:9000/wsrf/services/gcube/vremanagement/vremodeler/ModelerFactoryService"));
		ModelerFactoryPortType mfptp= mfal.getModelerFactoryPortTypePort(epr);


		GCUBEService service=GHNContext.getImplementation(GCUBEService.class);
		System.out.println("service is null?"+(service==null));
		mfptp = GCUBERemotePortTypeContext.getProxy(mfptp, GCUBEScope.getScope("/gcube/devsec"));
		

		EndpointReferenceType eprModelerRes= mfptp.getEPRbyId(id);
		
		ModelerServiceAddressingLocator msal= new ModelerServiceAddressingLocator();
		ModelerServicePortType msptp=msal.getModelerServicePortTypePort(eprModelerRes);
		msptp = GCUBERemotePortTypeContext.getProxy(msptp, GCUBEScope.getScope("/gcube/devsec"));
		
		
		FunctionalityNodes nodes = msptp.getFunctionalityNodes(new VOID());
		
		
		for (GHNsPerFunctionality func : nodes.getFunctionalities()){
			System.out.println("selected function"+func.getId()+" "+func.getMissingServices());
			
			if (func.getGhns()!=null && func.getGhns().getList()!=null)
				for (GHNType ghn :func.getGhns().getList()){
					System.out.println(ghn.getHost()+" "+ghn.isSelected());
					for (RunningInstanceMessage ri:ghn.getRelatedRIs()){
						System.out.println(ri.getServiceName()+" "+ri.getServiceName());
					}
				}
			
		}
		
		System.out.println("selectable GHN: ------------------------------");
		
		if (nodes.getSelectableGHNs()!=null && nodes.getSelectableGHNs().getList()!=null)
			for (GHNType ghn: nodes.getSelectableGHNs().getList())
				System.out.println(ghn.getId()+" "+ghn.getHost()+" "+ghn.isSelected());
		
		/*
		FunctionalityList list = msptp.getFunctionality(new VOID());
		for (FunctionalityItem item :list.getList()){
			System.out.println(item.getId()+" "+item.getName());
			System.out.println("children:");
			for(FunctionalityItem subItem: item.getChilds())
				System.out.println("-------------- "+subItem.getId()+" "+subItem.getName()+" "+subItem.isSelected());
		}*/
		
		
		
		DeployReport deployReport;
		do{
			System.out.println("checking report: ------------------------------");
			String report=msptp.checkStatus(new VOID());
			System.out.println("report is:");
			System.out.println(report);
			deployReport= Utils.fromXML(report);
			Thread.sleep(15000);
		}while (deployReport.getStatus()==Status.Running);
	}
	
	
	
	public static void listVres(){
		try{
			ModelerFactoryServiceAddressingLocator mfal =new ModelerFactoryServiceAddressingLocator();
		
		/*EndpointReferenceType epr= results.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/vremodeler/ModelerFactoryService");
		System.out.println(epr);*/
		EndpointReferenceType epr= new EndpointReferenceType(new URI("http://node24.d.d4science.research-infrastructures.eu:9000/wsrf/services/gcube/vremanagement/vremodeler/ModelerFactoryService"));
		ModelerFactoryPortType mfptp= mfal.getModelerFactoryPortTypePort(epr);
		
				
		GCUBEService service=GHNContext.getImplementation(GCUBEService.class);
		System.out.println("service is null?"+(service==null));
		mfptp = GCUBERemotePortTypeContext.getProxy(mfptp, GCUBEScope.getScope("/gcube/devsec"));
		
		ReportList listVres = mfptp.getAllVREs(new VOID());
		if (listVres.getList()!=null ) 
			for (Report  report: listVres.getList())
				System.out.println(report.getName()+" "+report.getState()+" --- "+report.getId());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void undeploy(String vreId){
		try{
			ModelerFactoryServiceAddressingLocator mfal =new ModelerFactoryServiceAddressingLocator();
			/*EndpointReferenceType epr= results.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/vremodeler/ModelerFactoryService");
		System.out.println(epr);*/
			EndpointReferenceType epr= new EndpointReferenceType(new URI("http://node24.d.d4science.research-infrastructures.eu:9000/wsrf/services/gcube/vremanagement/vremodeler/ModelerFactoryService"));
			ModelerFactoryPortType mfptp= mfal.getModelerFactoryPortTypePort(epr);


			GCUBEService service=GHNContext.getImplementation(GCUBEService.class);
			System.out.println("service is null?"+(service==null));
			mfptp = GCUBERemotePortTypeContext.getProxy(mfptp, GCUBEScope.getScope("/gcube/devsec"));
			

			EndpointReferenceType eprModelerRes= mfptp.getEPRbyId(vreId);
			System.out.println(eprModelerRes.toString());
			ModelerServiceAddressingLocator msal= new ModelerServiceAddressingLocator();
			ModelerServicePortType msptp=msal.getModelerServicePortTypePort(eprModelerRes);
			msptp = GCUBERemotePortTypeContext.getProxy(msptp, GCUBEScope.getScope("/gcube/devsec"));
			msptp.undeployVRE(new VOID());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void deploy(){
		try{
			/*
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBERIQuery riquery= client.getQuery(GCUBERIQuery.class);
			riquery.addAtomicConditions(new AtomicCondition("//ServiceName", "VREModeler"));
			List<GCUBERunningInstance> results=client.execute(riquery, GCUBEScope.getScope(args[0]));
			*/
			ModelerFactoryServiceAddressingLocator mfal =new ModelerFactoryServiceAddressingLocator();
			/*EndpointReferenceType epr= results.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/vremodeler/ModelerFactoryService");
			System.out.println(epr);*/
			EndpointReferenceType epr= new EndpointReferenceType(new URI("http://node24.d.d4science.research-infrastructures.eu:9000/wsrf/services/gcube/vremanagement/vremodeler/ModelerFactoryService"));
			ModelerFactoryPortType mfptp= mfal.getModelerFactoryPortTypePort(epr);
			
					
			GCUBEService service=GHNContext.getImplementation(GCUBEService.class);
			System.out.println("service is null?"+(service==null));
			mfptp = GCUBERemotePortTypeContext.getProxy(mfptp, GCUBEScope.getScope("/gcube/devsec"));
			
			ReportList listVres = mfptp.getAllVREs(new VOID());
			if (listVres.getList()!=null ) 
				for (Report  report: listVres.getList())
					System.out.println(report.getName()+" "+report.getState());
			
			EndpointReferenceType eprModelerRes= mfptp.createResource(new VOID());
			System.out.println(eprModelerRes.toString());
			ModelerServiceAddressingLocator msal= new ModelerServiceAddressingLocator();
			ModelerServicePortType msptp=msal.getModelerServicePortTypePort(eprModelerRes);
			
			/*
			EndpointReferenceType eprModelerRes= mfptp.getEPRbyId("c85ef4c0-f178-11df-9ffd-b8524462ccae");
			
			ModelerServiceAddressingLocator msal= new ModelerServiceAddressingLocator();
			ModelerServicePortType msptp=msal.getModelerServicePortTypePort(eprModelerRes);
			msptp = GCUBERemotePortTypeContext.getProxy(msptp, GCUBEScope.getScope(args[0]));
			
			System.out.println(msptp.isUseCloud(new VOID()));
			
			System.out.println(msptp.getCloudVMs(new VOID()));
			
			System.out.println(msptp.checkStatus(new VOID()));
			
			DeployReport dr=Utils.fromXML(msptp.checkStatus(new VOID()));
			
			System.out.println(dr.getState());
			
			//System.out.println(msptp.checkStatus(new VOID()));
			
			*/
			
			msptp = GCUBERemotePortTypeContext.getProxy(msptp, GCUBEScope.getScope("/gcube/devsec"));
			
			System.out.println("creation requested");
			
			
			
			VREDescription vreReq= new VREDescription();
			Calendar cal= Calendar.getInstance();
			System.out.println(cal.getTimeInMillis());
			vreReq.setStartTime(Calendar.getInstance());
			cal.add(Calendar.HOUR, 5);
			System.out.println(cal.getTimeInMillis());
			vreReq.setEndTime(cal);
			vreReq.setDescription("desc");
			vreReq.setDesigner("Lucio");
			vreReq.setManager("Lucio");
			vreReq.setName("testMulti6");
			msptp.setDescription(vreReq);
			System.out.println("description set");
			cal.add(Calendar.HOUR, 15);
			msptp.renewVRE(cal);
			
			/*GHNType[] ghns = msptp.getGHNs(new VOID()).getList();
			
			String id = null;
			for (GHNType ghn : ghns){
				System.out.println(ghn.getId()+" "+ghn.getHost()+" "+ghn.isSelectable()+" "+ghn.isSelected());
				id = ghn.getId();				
			}
				
			ghns = msptp.getGHNs(new VOID()).getList();
			for (GHNType ghn : ghns)
				System.out.println(ghn.getId()+" "+ghn.getHost()+" "+ghn.isSelectable()+" "+ghn.isSelected());
			msptp.setGHNs(new GHNArray(new String[]{id}));
			*/
			
			String resourcesId="";
			String resourceId= "";
			FunctionalityList list = msptp.getFunctionality(new VOID());
			for (FunctionalityItem item :list.getList()){
				System.out.println(item.getId()+" "+item.getName());
				System.out.println("children:");
				for(FunctionalityItem subItem: item.getChilds()){
					System.out.println("-------------- "+subItem.getId()+" "+subItem.getName());
					System.out.println("resource:");
					if (subItem.getSelectableResourcesDescription()!=null)
						for (ResourceDescriptionItem resources:  subItem.getSelectableResourcesDescription()){
							System.out.println("-----"+resources.getId()+" "+resources.getDescription());
							if (resources.getDescription().equals("biodiversity collections")) resourcesId=resources.getId();
							if (resources.getResource()!=null)
								for (ResourceItem resource : resources.getResource()){
									System.out.println(resource.getId()+" "+resource.getName()+" "+resource.getDescription()+" "+resource.isSelected());
									if (resource.getName().equals("WoRMS")) resourceId= resource.getId();
								}
						}
					System.out.println("-----------");
				}
			}
			
			
			System.out.println("--------------------------------------------------------");
			System.out.println("resourcesId selected is "+resourcesId+" and "+resourceId);
			
			//set functionalities
			
			SetFunctionality set = new SetFunctionality();
			
			set.setFunctionalityIds(new int[]{1,2,3,4,5,12});
			SelectedResourceDescriptionType rdt= new SelectedResourceDescriptionType();
			rdt.setDescriptionId(resourcesId);
			rdt.setResourceId(new String[]{"10418860-7ff9-11e1-b54b-fa68d406a964","15926900-aa39-11e1-a059-bc9609658eb2"});
			set.setResourcesDescription(new SelectedResourceDescriptionType[]{rdt});
			msptp.setFunctionality(set);
			
			
			msptp.setGHNs(new GHNArray(new String[]{"d92d1040-07f4-11e2-bcad-a28ea3c71d79"}));
			
			
			System.out.println();
			System.out.println("--------------------------------------------------------");
			
			list = msptp.getFunctionality(new VOID());
			for (FunctionalityItem item :list.getList()){
				System.out.println(item.getId()+" "+item.getName());
				System.out.println("children:");
				for(FunctionalityItem subItem: item.getChilds()){
					System.out.println("-------------- "+subItem.getId()+" "+subItem.getName());
					System.out.println("resource:");
					if (subItem.getSelectableResourcesDescription()!=null)
						for (ResourceDescriptionItem resources:  subItem.getSelectableResourcesDescription()){
							System.out.println("-----"+resources.getId()+" "+resources.getDescription());
							if (resources.getResource()!=null)
								for (ResourceItem resource : resources.getResource())
									System.out.println(resource.getName()+" "+resource.getDescription()+" "+resource.isSelected());
						}
					System.out.println("-----------");
				}
			}
			
			
			System.out.println("--------------------------------------------------------");
					
			
			
			FunctionalityNodes nodes = msptp.getFunctionalityNodes(new VOID());
			
			
			for (GHNsPerFunctionality func : nodes.getFunctionalities()){
				System.out.println(func.getId()+" "+func.getMissingServices());
				
				if (func.getGhns()!=null && func.getGhns().getList()!=null)
					for (GHNType ghn :func.getGhns().getList()){
						System.out.println(ghn.getHost()+" "+ghn.isSelected());
						for (RunningInstanceMessage ri:ghn.getRelatedRIs()){
							System.out.println(ri.getServiceName()+" "+ri.getServiceName());
						}
					}
				
			}
			
			System.out.println("selectable GHN: ------------------------------");
			
			if (nodes.getSelectableGHNs()!=null && nodes.getSelectableGHNs().getList()!=null)
				for (GHNType ghn: nodes.getSelectableGHNs().getList())
					System.out.println(ghn.getId()+" "+ghn.getHost()+" "+ghn.isSelected());
				
			
			/*GHNType[] ghns = msptp.getGHNs(new VOID()).getList();
			
			
			for (GHNType ghn : ghns){
				System.out.println(ghn.getId()+" "+ghn.getHost()+" "+ghn.isSelectable()+" "+ghn.isSelected()+" "+ghn.isRecomended());
				
			}
				
			
			
			/*CollectionList cl=msptp.getCollection(new VOID());
		
			System.out.println("collection list is null?"+(cl.getList()==null));
			
			for(int i=0; i< cl.getList().length; i++)
				System.out.println(i+" - "+cl.getList(i));
			
			msptp.setCollection(new CollectionArray(new String[]{cl.getList(0).getId()}));
		
			FunctionalityList flist=msptp.getFunctionality(new VOID());
			
			for (FunctionalityItem fitem: flist.getList()){
				for (FunctionalityItem fchild:fitem.getChilds())
					System.out.println(fchild.getName()+" "+fchild.getId());
			}
			
			System.out.println("collection set");
			
			
			FunctionalityIDArray fida= new FunctionalityIDArray();
			
			
			fida.setFunctionalityIDElement(new String[]{"4"});
			msptp.setFunctionality(fida);

			System.out.println("functionality set");
			
			msptp.setUseCloud(true);
			msptp.setCloudVMs(2);
			
			
			msptp.setUseCloud(false);
			msptp.setGHNs(new GHNArray(new String[]{"038bab20-eccd-11df-890f-bfe609d68cc0","046da6b0-eccd-11df-90ca-ecd7051b065f"},"038bab20-eccd-11df-890f-bfe609d68cc0"));
			
			*/
			msptp.setVREtoPendingState(new VOID());
			
			msptp.deployVRE(new VOID());
			DeployReport deployReport;
			do{
				String report=msptp.checkStatus(new VOID());
				System.out.println("report is:");
				System.out.println(report);
				deployReport= Utils.fromXML(report);
				Thread.sleep(15000);
			}while (deployReport.getStatus()==Status.Running);
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}
	
}
