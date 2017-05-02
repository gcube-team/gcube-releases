package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateReportTemplate {
	
	
	static JCRWorkspace ws = null;
	public static final String REMOTE_STORAGE_PATH 		=  	"hl:remotePath";
	public static final String DATA 	  				= 	"jcr:data";
	
	
	static Logger logger = LoggerFactory.getLogger(CreateReportTemplate.class);
	
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		
	
		
		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

		
		WorkspaceItem folder = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/00000");
		System.out.println(folder.getId());
		System.out.println("Destination folder " + folder.getPath());
		String name = "report-template-" + UUID.randomUUID();
		String description = "descrition template";
		Calendar created = Calendar.getInstance();
		Calendar lastEdit = Calendar.getInstance();
		String author = "valentina.marioli";
		String destinationfolderId = folder.getId();
		String lastEditBy = "valentina.marioli";
		int numberOfSections = 2;
		String status = "ready";


		InputStream templateData = null;

		try {
			templateData = new FileInputStream("/home/valentina/Downloads/CastillloArena-1.jpg");
			ReportTemplate template = ws.createReportTemplate(name, description, created, lastEdit, author, lastEditBy, numberOfSections, status, templateData, destinationfolderId);
			System.out.println(template.getPath());

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (templateData!=null)
				templateData.close(); 
		}
	}

}
