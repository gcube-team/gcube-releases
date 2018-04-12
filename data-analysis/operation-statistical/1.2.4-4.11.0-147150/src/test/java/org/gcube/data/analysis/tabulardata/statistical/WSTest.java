package org.gcube.data.analysis.tabulardata.statistical;

import java.net.URI;
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Test;


import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

public class WSTest {

	static{
		Handler.activateProtocol();
	}
	
	
	
	@Test
	public void getFilesID() throws Exception{
		/**
		 * 12:55:35.502 [Thread-7] WARN  Common: 
		 * Unable to get resource SMObject [url=/Workspace/.applications/StatisticalManager/images_14_12_2015_12_55_26_CMP200758, 
		 * toString()=SMResource [resourceId=ff71f61f-68e3-4582-9b9f-eb8d5fdd8424, 
		 * resourceType=2, portalLogin=fabio.sinibaldi, operationId=200758, 
		 * description=Charts, name=IMAGES, provenance=1, 
		 * creationDate=java.util.GregorianCalendar[time=1450094132652,areFieldsSet=true,
		 * areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="Europe/Rome",
		 * offset=3600000,dstSavings=3600000,useDaylight=true,transitions=169,lastRule=java.util.SimpleTimeZone[id=Europe/Rome,
		 * offset=3600000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=2,startMonth=2,startDay=-1,startDayOfWeek=1,
		 * startTime=3600000,startTimeMode=2,endMode=2,endMonth=9,endDay=-1,endDayOfWeek=1,endTime=3600000,endTimeMode=2]],
		 * firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2015,MONTH=11,WEEK_OF_YEAR=51,WEEK_OF_MONTH=3,DAY_OF_MONTH=14,DAY_OF_YEAR=348,
		 * DAY_OF_WEEK=2,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=0,HOUR_OF_DAY=12,MINUTE=55,SECOND=32,MILLISECOND=652,ZONE_OFFSET=3600000,DST_OFFSET=0], 
		 * algorithm=null]]
		 */
		
		ScopeProvider.instance.set("/gcube/devsec");
		String folderUrl="/Workspace/.applications/StatisticalManager/images18 03 2015 18_22_10";
		String user="fabio.sinibaldi";
		
//		String SMPackage="org.gcube.data.analysis.statisticalmanager";
//		String SMServiceName="StatisticalManager";
		
		
		String HLServiceName=null;
		String HLPackage="org.gcube.portlets.user";
		String HLResourceName="HomeLibraryRepository";
		
		
		String callerScope=ScopeProvider.instance.get();
		ScopeBean scope=new ScopeBean(callerScope);
		while(!scope.is(Type.INFRASTRUCTURE))
			scope=scope.enclosingScope();
		
		
		// SET ROOT
		ScopeProvider.instance.set(scope.toString());
		
		
		// DIscover HL params for storage
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ HLResourceName + "' ");
		 
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		 
		for (AccessPoint ap:client.submit(query).get(0).profile().accessPoints()) {
			 
			if (ap.name().equals("ServiceName")) {	
				HLServiceName = ap.address();
				break;
			}
		}
		
		
		
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager()
				.getHome(user).getWorkspace();
		WorkspaceItem folderItem = ws.getItemByPath(folderUrl);

		WorkspaceFolder folder = (WorkspaceFolder) folderItem;
		List<WorkspaceItem> childrenList = folder.getChildren();
		IClient storage= new StorageClient(HLPackage, HLServiceName,
				user, AccessType.SHARED, scope.toString(), true).getClient();
		for (WorkspaceItem item : childrenList) {
			ExternalImage file = (ExternalImage) item;
			String name = item.getName();
			String mimeType=file.getMimeType();
			MyFile storageFile=storage.getMetaFile().RFile(file.getRemotePath());
			// switched name and description
			System.out.println(new ImmutableURIResult(
					new InternalURI(new URI(storageFile.getId()),mimeType), name,item.getDescription(),ResourceType.GENERIC_FILE));
			
		}
	}
	
	
}
