package org.gcube.portlets.user.homelibrary.jcr.test;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class TestToken {

	private static final String PDF = "application/pdf";
	
	private static final String DOC01 = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final String DOC02 = "application/msword";
	private static final String DOC03 = "text/plain";
	private static final String DOC04 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private static final String DOC05 = "application/xml";
	private static final String DOC06 = "application/vnd.openxmlformats-officedocument.wordprocessingml.template";
	private static final String DOC07 = "application/rdf+xml";
	private static final String DOC08 = "message/rfc822";
	private static final String DOC09 = "text/html";
	private static final String DOC10 = "application/rtf";
	private static final String DOC11 = "application/octet-stream";
	private static final String DOC12 = "application/json";
	private static final String DOC13 = "application/vnd.ms-excel";
	
	private static final String IMG01 = "image/jpeg";
	private static final String IMG02 = "image/png";

	private static final String PRESENTATION01 = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
	private static final String PRESENTATION02 = "application/vnd.ms-powerpoint";
	
	private static final String ZIP01 = "application/x-compressed";
	private static final String ZIP02 = "application/x-zip-compressed";
	private static final String ZIP03 = "application/zip";
	private static final String ZIP04 = "application/x-rar-compressed";
	
	private static final String VIDEO = "video/mp4";
		
	private static int count = 0;
	private static int pdf =0;
	private static int doc = 0;
	private static int image = 0;
	private static int zip = 0;
	private static int presentation = 0;
	private static int video = 0;
	private static int other = 0;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {

		try {

			ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/Parthenos");
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

			String user = "valentina.marioli";

			Home home = manager.getHome(user);
			Workspace ws = home.getWorkspace();

			WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-Parthenos/");

			explore(item);
		
			System.out.println("PDF " + pdf);
			System.out.println("image " + image);
			System.out.println("doc " + doc);
			System.out.println("presentations " + presentation);
			System.out.println("zip " + zip);
			System.out.println("video " + video);
			System.out.println("Other " + other);
			
			System.out.println("Total: " + count);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void explore(WorkspaceItem root) throws InternalErrorException {
		List<? extends WorkspaceItem> children;
		if (root.isFolder()){
			children = root.getChildren();
			for (WorkspaceItem child: children)
				explore(child);
		}else {
			count++;

			try{
				ExternalFile file = (ExternalFile) root;
				String mimetype = file.getMimeType();
				
				switch(mimetype){
				
				case PDF:
					pdf++;
					break;
					
				case DOC01: case DOC02: case DOC03: case DOC04: case DOC05: case DOC06: 
					case DOC07: case DOC08: case DOC09: case DOC10: case DOC11: case DOC12:	case DOC13:
					doc++;
					break;
					
				case IMG01: case IMG02:
					image++;
					break;
					
				case PRESENTATION01: case PRESENTATION02:
					presentation++;
					break;
					
				case ZIP01: case ZIP02: case ZIP03: case ZIP04:
					zip++;
					break;
					
				case VIDEO:
					video++;
					break;
					
				default:
					other++;
					System.out.println("Please consider to add this mimetype: " +  file.getMimeType());
					break;
				}
			}catch (Exception e){
				other++;
				System.out.println("ERROR  " +  root.getPath());
			}
		}

	}


}
