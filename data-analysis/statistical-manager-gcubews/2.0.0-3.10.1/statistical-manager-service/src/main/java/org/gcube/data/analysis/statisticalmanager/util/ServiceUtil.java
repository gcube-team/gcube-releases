package org.gcube.data.analysis.statisticalmanager.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.data.analysis.statisticalmanager.Configuration;

public class ServiceUtil {


	private static SimpleDateFormat dateFormatter= new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss"); //format it as per your requirement
	
	public static String getDateTime(){
				
		return  format(Calendar.getInstance());
	}
	
	public static String format(Calendar cal){
		return dateFormatter.format(cal.getTime());
	}
	
	public static Home getWorkspaceHome(String user) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException{
		return HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(user);
	}

	public static WorkspaceFolder getWorkspaceSMFolder(Home home) throws InternalErrorException{
		return home.getDataArea().getApplicationRoot(Configuration.getProperty(Configuration.WS_APPLICATION_FOLDER));
	}


}
