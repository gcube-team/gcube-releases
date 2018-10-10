package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.dmService;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import java.nio.charset.Charset;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ExportTableRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ExportTableStatusType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;
import org.gcube.common.scope.api.ScopeProvider;


public class DMTestsFW {

	/**
	 * @param args
	 * @throws Exception 
	 */
	

	
	
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set(TestCommon.SCOPE);
		
		DataManagementStub stub=stubFor(dmService).at(TestCommon.getServiceURI("gcube/application/aquamaps/aquamapsservice/DataManagement"));
	
//		//VIEW TABLE
//		System.out.println(stub.viewTable(new ViewTableRequest("speciesoccursum",new PagedRequestSettings(1, 0, SpeciesOccursumFields.speciesid+"", OrderDirection.ASC),null,ViewTableFormat.JSON)));
//
//		
//		//EDIT GET RESOURCE
//		System.out.println("Default Sources");
//		int hspecId=0;
//		for(Field f : stub.GetDefaultSources(new Empty()).theList()){
//			try{
//				int resId=f.getValueAsInteger();
//				System.out.println(f);
//				Resource res=stub.getResourceInfo(new Resource(ResourceType.HCAF+"",resId));				
//				System.out.println(res);
//				System.out.println("Editing resource...");
//				res.description("Edited via fws @ "+TestCommon.dateFormatter.format(new Date(System.currentTimeMillis())));
//				System.out.println(stub.EditResource(res));
//				if(f.name().equals(ResourceType.HSPEC+""))hspecId=resId;
//			}catch(Throwable t){t.printStackTrace();}
//		}
		
		//CUSTOM QUERY
//		String queryDescriptorId=stub.SetCustomQuery(new SetCustomQueryRequest("fabio.sinibaldi", "Select status,count(*) as tot from submitted group by status order by tot DESC"));
//		CustomQueryDescriptorStubs desc=null;
//		do{
//			try{Thread.sleep(2000);}catch(InterruptedException e){}
//			desc=stub.GetCustomQueryDescriptor(queryDescriptorId);
//			System.out.println(desc);
//		}while (!desc.status().equals(ExportStatus.COMPLETED)&&!desc.status().equals(ExportStatus.ERROR));
//		System.out.println(stub.ViewCustomQuery(new ViewCustomQueryRequestType("fabio.sinibaldi", new PagedRequestSettings(10,0,"status",OrderDirection.ASC))));
//		
		
		
		//EXPORT TABLE
		String reqId=stub.ExportTableAsCSV(new ExportTableRequest("speciesoccursum", ExportOperation.TRANSFER, "fabio.sinibaldi", null, null, new ExportCSVSettings(Charset.defaultCharset()+"", ",", true, null)));
		ExportTableStatusType stat=null;
		do{
			try{Thread.sleep(2000);}catch(InterruptedException e){}
			stat=stub.GetExportStatus(reqId);
			System.out.println(stat);
		}while (!stat.status().equals(ExportStatus.COMPLETED)&&!stat.status().equals(ExportStatus.ERROR));
		
		
		
		
		
		
		
//		System.out.println(stub.generateMaps(new GenerateMapsRequest("fabio.sinibaldi", 325, true, new FieldArray(Arrays.asList(new Field[]{
//				new Field(SpeciesOccursumFields.familycolumn+"","Gadilidae",FieldType.STRING)
//		})), true)));
		
		
	}

}
