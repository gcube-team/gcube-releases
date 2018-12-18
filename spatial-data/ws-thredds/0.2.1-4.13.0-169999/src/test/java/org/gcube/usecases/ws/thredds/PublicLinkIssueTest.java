package org.gcube.usecases.ws.thredds;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.gcube.spatial.data.sdi.plugins.SDIAbstractPlugin;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class PublicLinkIssueTest {

	
	public static void main (String[] args) throws ItemNotFoundException, InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException, UserNotFoundException, InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException {
		TokenSetter.set("/d4science.research-infrastructures.eu");
		
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();	
		System.out.println(ws.getItemByPath("/Workspace/ArgoNetCDF/Practical_salinity/Practical_salinity_code_30_date_2000_6.nc").getPublicLink(false));
		
		String threddsHostName = Commons.getThreddsHost();

		DataTransferClient client=Commons.getDTClient(threddsHostName);
		Destination dest=new Destination("thredds", "public/netcdf/someWhere", "myTest.txt", true, DestinationClashPolicy.REWRITE, DestinationClashPolicy.APPEND);
		
		
		
		
		
		client.httpSource("http://data.d4science.org/V2drR2gxSFRTQlpLVC9nakozL29QcDdPR2U5UEVHYWRHbWJQNStIS0N6Yz0", dest);
		
		
		scanForPrint((WorkspaceFolder) ws.getItem("a8cd78d3-69e8-4d02-ac90-681b2d16d84d"));
		System.out.println("OK FIRST ...");
		try {
		Metadata meta=SDIAbstractPlugin.metadata().build();

		

		MetadataPublishOptions opts=new MetadataPublishOptions(new TemplateInvocationBuilder().threddsOnlineResources(threddsHostName, "myMeta", "testCatalog").get());
		opts.setGeonetworkCategory("Datasets");
		MetadataReport report=meta.pushMetadata(new File("/home/fabio/Desktop/meta.xml"), opts);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("CHECKING AGAIN");
		scanForPrint((WorkspaceFolder) ws.getItem("a8cd78d3-69e8-4d02-ac90-681b2d16d84d"));
		
		
	}
	
	public static void scanForPrint(WorkspaceFolder folder) throws InternalErrorException {
		System.out.println("Folder "+folder.getPath());
		printProperties(folder.getProperties());
		SynchFolderConfiguration config=new SynchFolderConfiguration("", "", "", "","");
		for(WorkspaceItem item:folder.getChildren())
			if(!item.isFolder()&&config.matchesFilter(item.getName())) {
//				System.out.println("ITEM "+item.getPath());
				printProperties(item.getProperties());
			}
		for(WorkspaceItem item:folder.getChildren())
			if(item.isFolder())scanForPrint((WorkspaceFolder) item);
	}
	
	
	public static void printProperties(Properties prop) throws InternalErrorException {
		Map<String,String> map=prop.getProperties();
//		System.out.print("Properties : ..");
		for(Entry<String,String> entry:map.entrySet()) {
			if(entry.getKey().equals(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS)||
					entry.getKey().equals(Constants.WorkspaceProperties.LAST_UPDATE_STATUS)||
					entry.getKey().equals(Constants.WorkspaceProperties.LAST_UPDATE_TIME)) {
//			if(true) {
				if(entry.getValue()==null) System.out.print(entry.getKey()+" is null;");
				else System.out.print(entry.getKey()+" = "+entry.getValue()+";");
			}
		}
//		System.out.println();
	}
	
	
}
