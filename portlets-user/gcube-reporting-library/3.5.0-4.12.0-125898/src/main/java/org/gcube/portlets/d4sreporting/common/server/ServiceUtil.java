package org.gcube.portlets.d4sreporting.common.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * <code> ServiceUtil </code> contains utility methods for the servlet SERVICES
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */

public class ServiceUtil {
	private static final Logger _log = LoggerFactory.getLogger(ServiceUtil.class);
	private final static String REPORT_FOLDER = "Sample Reports and Templates";
	private final static String REPORT_EXISTS = "reportfolderexisting";
	ASLSession session;
	/**
	 * 
	 */
	public ServiceUtil(ASLSession session) {
		_log.debug("Constructor ServiceUtil called");
		this.session = session;
		checkReportFolder(session);
	}
	/**
	 * 
	 * @param session
	 */
	private void checkReportFolder(ASLSession session) {
		//look first in the session
		if (session.getAttribute(REPORT_EXISTS) != null) {
			return;
		}
		else if (reportsFolderExists(session)) {
			session.setAttribute(REPORT_EXISTS, true);
		}
		else {
			createReportFolder();
			session.setAttribute(REPORT_EXISTS, true);
		}
	}
	/**
	 * 
	 * @param session
	 * @return
	 */
	private boolean reportsFolderExists(ASLSession session) {

		Workspace ws;
		boolean toReturn = false;
		try {
			ws = getWorkspaceArea(session);
			toReturn = ws.getRoot().exists(REPORT_FOLDER);
		} catch (Exception e) {	e.printStackTrace();
		} 
		return toReturn; 

	}
	private void createReportFolder() {
		Workspace ws = null;
		try {
			ws = getWorkspaceArea(session);
			WorkspaceFolder reportFolder = null;
			if (! ws.getRoot().exists(REPORT_FOLDER)) {
				reportFolder = ws.getRoot().createFolder(REPORT_FOLDER, "This folder hosts the set of predefined reports and templates");
				InputStream isZip = ServiceUtil.class.getResourceAsStream("/org/gcube/portlets/d4sreporting/common/server/resources/FishFinder-FactSheet.zip");
				Calendar dateCreated = Calendar.getInstance();
				dateCreated.setTime(new Date());
				reportFolder.createReportTemplateItem("FishFinder Species Fact Sheet", "no desc", dateCreated,
						dateCreated, "System", "no-one", 9, "no-status", isZip);

			} else {
				reportFolder =  (WorkspaceFolder) ws.getRoot().find(REPORT_FOLDER);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	/**
	 * 
	 * @param session the ASL session 
	 * @return the reports workspace folder
	 * @throws WorkspaceFolderNotFoundException . 
	 * @throws InternalErrorException .
	 * @throws HomeNotFoundException .
	 */
	public WorkspaceFolder getReportsFolder(ASLSession session) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {
		Workspace ws = getWorkspaceArea(session);
		WorkspaceFolder reportFolder = (WorkspaceFolder) ws.getRoot().find(REPORT_FOLDER);
		return reportFolder;
	}
	/**
	 * 
	 * @return an instance of the user WorkspaceArea
	 * @throws HomeNotFoundException 
	 * @throws InternalErrorException 
	 * @throws WorkspaceFolderNotFoundException 
	 * @throws WorkspaceNotFoundException
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 */
	protected Workspace getWorkspaceArea(ASLSession session) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException 	{
		return HomeLibrary.getUserWorkspace(session.getUsername());	
	}


	/**
	 * 
	 * Return the server path of the templatename passed
	 * 
	 * @param templateName .
	 * @param currentVRE . 
	 * @param currentUser .
	 * @return .
	 */
	public String getTemplatePath(String templateName, String currentVRE, String currentUser) {
		templateName = templateName.replaceAll(" ", "_");
		//platform indipendent slash
		String sl = File.separator;		
		
		StringBuilder sb = new StringBuilder(System.getenv("CATALINA_HOME"));
		sb.append(sl).append("webapps").append(sl).append("usersArea").append(sl).append(currentVRE).append(sl)
							.append("templates").append(sl).append(currentUser).append(sl).append("CURRENT_OPEN").append(sl);

		_log.debug("path to template: " + sb.toString());
		return  sb.toString();
	}

	/**
	 * 
	 * Return the server path of the templatename passed
	 * 
	 * @param templateName .
	 * @param currentVRE . 
	 * @param currentUser .
	 * @return .
	 */
	public String getReportsPath(String templateName, String currentVRE, String currentUser) {
		templateName = templateName.replaceAll(" ", "_");
		//platform indipendent slash
		String sl = File.separator;		
	
		StringBuilder sb = new StringBuilder(System.getenv("CATALINA_HOME"));
		sb.append(sl).append("webapps").append(sl).append("usersArea").append(sl).append(currentVRE).append(sl)
							.append("templates").append(sl).append(currentUser).append(sl).append("CURRENT_OPEN_REPORT").append(sl);

		_log.debug("path to report: " + sb.toString());
		return  sb.toString();
	}

	/**
	 * return the user template folder
	 * @param templateName
	 * @param currentDL . 
	 * @param currentUser .
	 * @return .
	 */
	public String getTemplateFolder(String currentDL, String currentUser) {
		_log.debug("getTemplateFolder Method called");
		//		platform indipendent slash
		String sl = File.separator;		
		//e.g. /home/massi/workspace/templategen/tomcat/webapps/ROOT
		String path = System.getenv("CATALINA_HOME");
		path +=	sl + "webapps" + sl +"usersArea" + sl + currentDL +
		sl + "templates" + sl + currentUser + sl ;
		_log.debug("Returning Path= " + path);
		return path;

	}


	

	
	/**
	 * 
	 * @param gwtModel .
	 * @param templateName .
	 * @param currentDL .
	 * @param currentUser .
	 * @return true if everything's fine
	 */
	public boolean writeModel(Model model, String templateName, String currentDL, String currentUser) {
		
		//reopen the saved templates/report in first page
		model.setCurrPage(1);
		
		String fileToWrite = getTemplatePath(templateName, currentDL, currentUser) + "CURRENT_OPEN" + ".d4st";
		//String fileVersion = getTemplatePath(templateName, currentDL, currentUser) + "gCube-label.d4s";
		//create directory 
		File dirToCreate = new File(getTemplatePath(templateName, currentDL, currentUser));
		if (! dirToCreate.exists())
			dirToCreate.mkdirs();

		//persists the template
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(fileToWrite);
			out = new ObjectOutputStream(fos);
			out.writeObject(model);
			out.close();
			System.out.println("Template D4ST Persisted.. writing version 3.0" + fileToWrite);
			//TODO: Postponed
			//			File vFile = new File(fileVersion);
			//			vFile.createNewFile();

			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}


	}



	/**
	 * 
	 * @param pathToModel e.g. /home/massi/templates/massimiliano.assante/PROVA1/PROVA1.d4st
	 * @return the Model Instance
	 */
	public Model readRawModel(String pathToModel) {
		Model toConvert = null;

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(pathToModel);
			in = new ObjectInputStream(fis);
			toConvert = (Model) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		_log.debug("Returning Model");
		return toConvert;
	}




	/**
	 * just copies files using buffered input stream
	 * @param from .
	 * @param to .
	 * @param folder the folder in which the image has to be copied, (it creates it if not exist)
	 * @throws Exception .
	 */
	public void copyFile(File from, File to, String folder) throws Exception {
		_log.debug("Folder:" + folder);
		File f  = new File(folder);
		//checking if dir exists
		if (! f.exists()) 
			try {
				f.mkdirs();
				_log.debug("Directory not exists creating it");
			} catch (SecurityException ex) {_log.error("Security Exception " + ex.getMessage());}

			BufferedInputStream in = new BufferedInputStream(new FileInputStream(from), 8192);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to), 8192);

			// Copy the input stream to the output stream 8k at a time.
			final byte[] buffer = new byte[8192];
			final long fileSize = from.length();

			for (long length = fileSize; length > 0 ;)
			{
				int bytes = (int) (length > buffer.length ? buffer.length : length);
				bytes = in.read(buffer, 0, bytes);
				if (bytes < 0)
					break;
				length -= bytes;
				out.write(buffer, 0, bytes);
			}
			in.close();
			out.close();
	}	
}
