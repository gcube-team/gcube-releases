package org.gcube.usecases.ws.thredds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class DTTests {

	public static void main(String[] args) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException, MalformedURLException, FileNotFoundException, IOException, WorkspaceInteractionException, InternalException, WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException {
//		TestCommons.setScope();
//		String threddsHostname="thredds-d-d4s.d4science.org";
//		DataTransferClient client=DataTransferClient.getInstanceByEndpoint("http://"+threddsHostname+":80");
//		Destination toSetDestination=new Destination();
//		toSetDestination.setCreateSubfolders(true);
//		toSetDestination.setDestinationFileName("transferTest.tst");
//		toSetDestination.setOnExistingFileName(DestinationClashPolicy.REWRITE);
//		toSetDestination.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
//		toSetDestination.setPersistenceId("thredds");
//
//		//NB ITEM IS SUPPOSED TO HAVE REMOTE PATH 
//		String fileLocation="WS-Tests/mySub";
//		toSetDestination.setSubFolder(fileLocation);
//
//		File temp=File.createTempFile("testTransfer", "tmp");
//		IOUtils.copy(new URL("http://data-d.d4science.org/SUlDWjIxamdaUTdHcmpvdEFmcFFPOUcvbjF5VyswbXlHbWJQNStIS0N6Yz0").openStream(), new FileOutputStream(temp));
//		
//		System.out.println(client.localFile(temp, 
//				toSetDestination,Collections.singleton(new PluginInvocation(Constants.SIS_PLUGIN_ID))));
//		
		
		TokenSetter.set("/gcube/devNext");
		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		String folderId=ws.getItemByPath("/Workspace/Accounting").getId();
		SyncEngine.get().setSynchronizedFolder(new SynchFolderConfiguration("another", "", TokenSetter.getCurrentToken(), "dummy",folderId), folderId);
		System.out.println("Done");
		
	}

}
