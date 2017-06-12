package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class Utils {
	
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	
	private static final String VRE_PATH = "/Workspace/MySpecialFolders/";
	private static final String HOME = "Home";
	private static final String SEPARATOR = "/";
	private static final Object MY_SPECIAL_FOLDER = "MySpecialFolders";
	private static final int BYTES_DOWNLOAD = 4096;
	
	
	public static String cleanPath(Workspace workspace, String absPath) throws ItemNotFoundException, InternalErrorException {
		logger.info("Clean path " + absPath);
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

	
	public static void downloadByPath(HttpServletRequest request,
			HttpServletResponse response, XStream xstream,
			Session session, String absPath, String login) throws Exception{

		logger.info("Servlet Download called with parameters: [absPath: "+ absPath + "]");

		Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		absPath = Utils.cleanPath(workspace, absPath);
		WorkspaceItem item = workspace.getItemByPath(absPath);
		String name = item.getName();
		File file = null; 
		InputStream inStream = null;
		OutputStream outStream = null;

		String mimeType;
		int lenght;
		try{
			if (item.isFolder()){

				WorkspaceFolder folder = (WorkspaceFolder) item;
				name = name + ".zip";

				logger.info("Download zip folder " + name);

				file = ZipUtil.zipFolder(folder);
				inStream = new FileInputStream(file);

				mimeType = "application/zip";
				lenght = (int) file.length();

			}else{
				logger.info("Download file " + name);

				ExternalFile myfile = (ExternalFile) item;
				inStream = myfile.getData();

				if (inStream.available()==0 )
					logger.error("No inpustream for item " + item.getName());

				mimeType = myfile.getMimeType();
				lenght = (int) myfile.getLength();

			}

			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"", name));
			response.setContentType(mimeType);
			response.setContentLength(lenght);

			int read= -1;
			byte[] buffer = new byte[BYTES_DOWNLOAD];
			outStream = response.getOutputStream();

			while((read = inStream.read(buffer))!= -1){
				outStream.write(buffer, 0, read);
			}
			outStream.flush();


		}finally
		{
			if (outStream != null)
				outStream.close();
			if (inStream != null)
				inStream.close();
		}
	}

	
	
}
