package org.gcube.data.analysis.statisticalmanager.util;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.exception.HLManagementException;
import org.gcube.data.analysis.statisticalmanager.persistence.RemoteStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ServiceUtil {


	private static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);
	
	private static SimpleDateFormat dateFormatter= new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss"); //format it as per your requirement

	public static String getDateTime(){

		return  format(Calendar.getInstance());
	}

	public static String format(Calendar cal){
		return dateFormatter.format(cal.getTime());
	}

	public static Home getWorkspaceHome(String user) throws HLManagementException {
		try{
			return HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(user);
		}catch(Exception e){
			throw new HLManagementException("Unable to get Home for user "+user, e);
		}
	}

	public static WorkspaceFolder getWorkspaceSMFolder(Home home) throws HLManagementException {
		String folder=null;
		try{
			folder=Configuration.getProperty(Configuration.WS_APPLICATION_FOLDER);
			return home.getDataArea().getApplicationRoot(folder);
		}catch(Exception e){
			throw new HLManagementException("Unable to get Application folder "+(folder!=null?folder:""), e);
		}
	}


	public static String formatDetailedErrorMessage(Throwable t){
		try{
			//Create temp file
			File out=File.createTempFile("errLog", ".txt");
			PrintWriter pw = new PrintWriter(out);
			pw.println("Error Message : "+t.getMessage());
			pw.println("-------------");
			pw.println("Stack Trace :");		
			t.printStackTrace(pw);		
			pw.flush();
			pw.close();
			//Store remotely
			RemoteStorage stg=new RemoteStorage();
			return stg.getUrlById(stg.putFile(out, true));
		}catch(Throwable t1){
			logger.error("Unable to store error description",t1);
			return "Unable to get description (cause : "+t1.getMessage()+"), see service log.";
		}


	}
}
