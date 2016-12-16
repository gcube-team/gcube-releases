package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;

public class Utils {
	private static final String VRE_PATH = "/Workspace/MySpecialFolders/";
	private static final String HOME = "Home";
	private static final String SEPARATOR = "/";
	private static final Object MY_SPECIAL_FOLDER = "MySpecialFolders";
	
	public static String cleanPath(Workspace workspace, String absPath) throws ItemNotFoundException, InternalErrorException {
		String myVRE = null;
		String longVRE = null;
		
		String [] splitPath = absPath.split(SEPARATOR);
		if(absPath.contains(VRE_PATH) && (!splitPath[splitPath.length-1].equals(MY_SPECIAL_FOLDER))){
					
			if (splitPath[1].equals(HOME))
				myVRE = splitPath[5];
			else
				myVRE = splitPath[3];

			java.util.List<WorkspaceItem> vres = workspace.getMySpecialFolders().getChildren();
			for (WorkspaceItem vre: vres){
				if (vre.getName().endsWith(myVRE)){
					longVRE = vre.getName();
					break;
				} 
			}

			if (longVRE!=null)
				absPath = absPath.replace(myVRE, longVRE);
		}
		
//		System.out.println("CLEAN PATH " + absPath);
		return absPath;

	}

}
