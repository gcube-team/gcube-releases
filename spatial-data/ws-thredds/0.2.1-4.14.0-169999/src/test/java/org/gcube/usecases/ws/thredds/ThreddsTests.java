package org.gcube.usecases.ws.thredds;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.usecases.ws.thredds.engine.impl.ThreddsController;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class ThreddsTests {

	public static void main(String[] args) throws InternalException, WorkspaceFolderNotFoundException, ItemNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException {
		TestCommons.setScope();
		
		SynchFolderConfiguration folderConfig=TestCommons.getSynchConfig();
		ThreddsController controller=TestCommons.getThreddsController();
		
		System.out.println("Getting thredds info...");
		
		ThreddsInfo info=controller.getThreddsInfo();
		
		System.out.println("INFO "+info);
		
		System.out.println(info.getCatalogByFittingLocation(info.getLocalBasePath()+"/"+folderConfig.getRemotePath()));
	}

}
