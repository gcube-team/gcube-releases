package org.gcube.datatransfer.portlets.user.server.workers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.obj.WorkspaceInitializeInfo;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WorkspaceWorker extends RemoteServiceServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final String USERNAME_ATTRIBUTE = "username";
	public int autoId;

	public WorkspaceWorker(){
		this.autoId=0;
	}

	public ASLSession getASLSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		//TODO we check for the older attribute name
		if (user == null) user = (String) httpSession.getAttribute("user");
		if(user==null){System.out.println("WorkspaceWorker - getASLSession: user not found in session");return null;}
		else System.out.println("WorkspaceWorker - getASLSession: user found in session "+user);

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/*
	 * getUser
	 * input: Local request
	 * returns: String with the name of the user in this session
	 */
	public String getUserAndScopeAndRole(HttpServletRequest localRequest){
		try{
			if(localRequest==null){System.out.println("WorkspaceWorker - getUserAndScope: localRequest==null");return null;}

			HttpSession httpSession = localRequest.getSession();
			if(httpSession==null){System.out.println("WorkspaceWorker - getUserAndScope: httpSession==null");return null;}

			ASLSession aslSession = getASLSession(httpSession);
			if(aslSession==null)return null;	
			
			List<String> roles = getUserRolesByGroup(aslSession);
			String res="";
			for(String tmp:roles)res=res+tmp+"\n";
			System.out.println("WorkspaceWorker - getUserAndScope - roles:\n"+res);
			boolean isAdmin = checkAdminCase(roles);
			
			String name = aslSession.getUsername();
			String scope = aslSession.getScopeName();

			if(name==null || scope==null){System.out.println("WorkspaceWorker - getUserAndScope: usern or scope is null");return null;}

			//returning value .. example: nick--/gcube/devsec--false
			return name+"--"+scope+"--"+isAdmin;
		}
		catch(Exception e){
			System.out.println("WorkspaceWorker - getUserAndScope: Exception ******");
			e.printStackTrace();
			return null;
		}

	}

	public List<String> getUserRolesByGroup(ASLSession aslSession){
		try {
			List<String> roles=new ArrayList<String>();
			RoleManager roleM=new LiferayRoleManager();
			GroupManager groupM=new LiferayGroupManager();
			UserManager userM=new LiferayUserManager();
			List<RoleModel> userRolesByGroup;

			String userId = userM.getUserId(aslSession.getUsername());
			String groupId=groupM.getGroupId(aslSession.getGroupName());			
			
			userRolesByGroup = roleM.listRolesByUserAndGroup(groupId, userId);
			
			for(RoleModel tmp:userRolesByGroup)roles.add(tmp.getRoleName());
			return roles;

		} catch (UserManagementSystemException e) {
			e.printStackTrace();
			return null;
		} catch (GroupRetrievalFault e) {
			e.printStackTrace();
			return null;
		} catch (UserRetrievalFault e) {
			e.printStackTrace();
			return null;
		}	
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean checkAdminCase(List<String> roles){
		for(String role:roles){
			if(role.compareTo("VO-Admin")==0 || role.compareTo("VRE-Manager")==0){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * getWorkspace
	 * input: HttpServletRequest 
	 * returns: String with the serialized object of workspace
	 */
	public String getWorkspace(HttpServletRequest localRequest){
		try{
			ASLSession session = getASLSession(localRequest.getSession());
			if(session==null)return null;

			Workspace w=null;
			try {
				ScopeProvider.instance.set(session.getScope());
				w = HomeLibrary.getUserWorkspace(session.getUsername());

			} catch (WorkspaceFolderNotFoundException e) {
				e.printStackTrace();
			} catch (InternalErrorException e) {
				e.printStackTrace();
			} catch (HomeNotFoundException e) {
				e.printStackTrace();
			}
			if(w==null){System.out.println("WorkspaceWorker - getWorkspace: workspace==null");return null;}

			WorkspaceInitializeInfo workspaceInfo=new WorkspaceInitializeInfo();
			workspaceInfo.setWorkspace(w);
			String serializedWorkspace=workspaceInfo.toXML();

			if(serializedWorkspace==null){System.out.println("WorkspaceWorker - getWorkspace: serializedWorkspace==null");return null;}
			else System.out.println("WorkspaceWorker - getWorkspace: serializedObj length="+serializedWorkspace.length());

			return serializedWorkspace;		
		}catch(Exception e){
			System.out.println("WorkspaceWorker - getWorkspace: Exception ***");
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * getWorkspaceWithoutSerialization
	 * input: HttpServletRequest 
	 * returns: Workspace obj
	 */
	public Workspace getWorkspaceWithoutSerialization(HttpServletRequest localRequest){
		try{
			ASLSession session = getASLSession(localRequest.getSession());
			if(session==null)return null;

			Workspace w=null;
			try {
				ScopeProvider.instance.set(session.getScope());
				w = HomeLibrary.getUserWorkspace(session.getUsername());
			} catch (WorkspaceFolderNotFoundException e) {
				e.printStackTrace();
			} catch (InternalErrorException e) {
				e.printStackTrace();
			} catch (HomeNotFoundException e) {
				e.printStackTrace();
			}

			if(w==null){System.out.println("WorkspaceWorker - getWorkspaceWithoutSerialization: workspace==null");return null;}
			else return w;

		}catch(Exception e){
			System.out.println("WorkspaceWorker - getWorkspaceWithoutSerialization: Exception ***");
			e.printStackTrace();
			return null;
		}

	}
	public String fixPath(String workspaceWebDavLink, String path){
		String tmpPath=path;
		if(path.startsWith("/"))tmpPath=tmpPath.replaceFirst("/", "");
		String[] partsOfPath=tmpPath.split("/");
		String[] partsOfwebdavUrl=workspaceWebDavLink.split("/");
		String fixedRootPath="";

		//if the workspaceWebDavLink ends to 'Workspace' for example and the path starts with the same name we 
		// omit it from the path because in other case it will be double
		if(partsOfwebdavUrl[partsOfwebdavUrl.length-1].compareTo(partsOfPath[0])==0){
			for(int i=1;i<=partsOfPath.length-1;i++){
				if(i<(partsOfPath.length-1)){
					fixedRootPath=fixedRootPath+(partsOfPath[i])+"/";
				}
				else fixedRootPath=fixedRootPath+(partsOfPath[i]);
			}
			return fixedRootPath;
		}
		else return tmpPath;

	}

	/*
	 * createTree
	 * input: WorkspaceFolder 
	 * returns: A FolderDto object which is the same tree represented from the input folder
	 */
	public FolderDto createTree(WorkspaceFolder root, String workspaceWebDavLink) throws InternalErrorException {
		if(root==null){System.out.println("GET WORKSPACE MANUALLY - createTree - root is null");return null;}
		FolderDto empty = makeFolder("",null);
		FolderDto folder=null;
		List<WorkspaceItem> list=null;

		String rootPath=root.getPath();
		String fixedRootPath= fixPath(workspaceWebDavLink, rootPath);
		if(fixedRootPath.compareTo("")!=0&&!fixedRootPath.endsWith("/"))fixedRootPath=fixedRootPath+"/";

		folder = makeFolder(workspaceWebDavLink+"/"+fixedRootPath,root.getId());
		list= root.getChildren();


		if(list==null){System.out.println("GET WORKSPACE MANUALLY - createTree - list is empty");return null;}
		if(list.size()<1){
			folder.addChild(empty);
			return folder;
		}
		for(WorkspaceItem tmp : list){
			if(tmp.getType().toString().compareTo("FOLDER")==0){
				String path=tmp.getPath();
				String fixedPath= fixPath(workspaceWebDavLink, path);

				if(fixedPath.compareTo("")!=0&&!fixedPath.endsWith("/"))fixedPath=fixedPath+"/";
				FolderDto subfolder = makeFolder(workspaceWebDavLink+"/"+fixedPath,tmp.getId());
				subfolder.addChild(empty);
				folder.addChild(subfolder);
			}
			else{
				String path=tmp.getPath();
				String fixedPath= fixPath(workspaceWebDavLink, path);

				FolderDto child = makeFolder(workspaceWebDavLink+"/"+fixedPath,null);
				folder.addChild(child);
			}
		}
		return folder;
	}

	/*
	 * makeFolder
	 * input: String with the name
	 * input: String with the id in workspace
	 * returns: The created FolderDto object
	 */
	public FolderDto makeFolder(String name, String idInWorkspace) {
		FolderDto theReturn = new FolderDto(++autoId, name);
		if(idInWorkspace!=null){theReturn.setIdInWorkspace(idInWorkspace);}
		theReturn.setChildren((List<FolderDto>) new ArrayList<FolderDto>());
		return theReturn;
	}

	/*
	 * printFolder
	 * input: FolderDto
	 * input: The depth
	 * It prints the tree represented from the input folder for debugging reasons
	 */
	public void printFolder(FolderDto folder, int indent){
		for(int i = 0; i < indent; i++) System.out.print("\t");
		System.out.println("fold : name="+folder.getName() +" - id="+folder.getId()+" - idInWorkspace="+folder.getIdInWorkspace());

		List<FolderDto> tmpListOfChildren = folder.getChildren();
		if(tmpListOfChildren!=null){
			for(FolderDto tmp : tmpListOfChildren){ //first the files
				if(tmp.getChildren().size() <= 0){
					if((tmp.getName().compareTo("")==0))continue;
					for(int i = 0; i < indent; i++) System.out.print("\t");
					String type= "";
					if((tmp.getName().substring(tmp.getName().length()-1,tmp.getName().length())).compareTo("/")==0)type="fold";
					else type="file";

					System.out.println(type+" : name="+tmp.getName()+" - id="+tmp.getId());
				}
			}		    	
			for(FolderDto tmp : tmpListOfChildren){ //then the folders
				if(tmp.getChildren().size() > 0){
					printFolder(tmp,indent+1);
				}
			}
		}		    
	}

	// for testing  ... 
	public void getAttributes(HttpServletRequest localRequest){
		if(localRequest==null){System.out.println("WorkspaceWorker - getAttributes: localRequest==null");return;}
		// RenderRequest renderRequest
		HttpSession httpSession = localRequest.getSession();
		if(httpSession==null){System.out.println("WorkspaceWorker - getAttributes: httpSession==null");return;}

		String sessionID = httpSession.getId();
		System.out.println("WorkspaceWorker - getAttributes: AttributeNames of session with id="+sessionID+":");
		Enumeration em= httpSession.getAttributeNames();
		while(em.hasMoreElements()){
			String value = (String) em.nextElement();			
			System.out.println("name="+value+" - stringValue="+httpSession.getAttribute(value).toString());
		}
	}
}
