package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;

public class CreateReport {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

		
//		ws.getRoot().getChildren();
		
//		WorkspaceItem csv = ws.getItem("a42fbe8e-99cd-4f76-9e7f-47c933df243d");
//		System.out.println(csv.getCreationTime().getTime());
//		System.out.println(csv.getRemotePath());
		
		
		WorkspaceItem folder = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/00000");
		System.out.println(folder.getId());
		System.out.println("Destination folder " + folder.getPath());
		String name = "report-" + UUID.randomUUID();
		String description = "descrition template";
		Calendar created = Calendar.getInstance();
		Calendar lastEdit = Calendar.getInstance();
		String author = "valentina.marioli";
		String destinationfolderId = folder.getId();
		String lastEditBy = "valentina.marioli";
		int numberOfSections = 2;
		String status = "ready";
		String templateName = "test";

		InputStream templateData = null;

		try {
			templateData = new FileInputStream("/home/valentina/Downloads/CastillloArena-1.jpg");
			
			Report template = ws.createReport(name, description, created, lastEdit, author, lastEditBy, templateName, numberOfSections, status, templateData, destinationfolderId);
			System.out.println(template.getPath());

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (templateData!=null)
				templateData.close(); 
		}
	}


}
