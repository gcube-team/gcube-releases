package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
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
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRAccessManager;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.jcr.workspace.util.Utils;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CreateVREFolder {


	protected static Logger logger = LoggerFactory.getLogger(CreateVREFolder.class);
	public static final String MEMBERS 		=	"hl:members";
	public static final String USERS 			=	"hl:users";
	public static final String ATTACHMENT_FOLDER ="Shared attachments";
	
	static String vreScope = "/gcube/devNext/NextNext";
	
	
	public static void main(String[] args) {

		try {
			//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			
		
			ScopeProvider.instance.set(vreScope);

			
//			String manager = "d4science.research-infrastructures.eu-SmartArea-SmartApps-Manager";

//			UserManager um = HomeLibrary
//					.getHomeManagerFactory().getUserManager();
//			um.deleteAuthorizable(designer);

//			String designer = "luca.frosini";
			String designer = "andrea.rossi";
			createVRESharedGroupFolder(designer);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createVRESharedGroupFolder(String designer) throws Exception {

		String currScope = ScopeProvider.instance.get();

//		String vreName = "SmartApps";
//		String vreScope = "/d4science.research-infrastructures.eu/SmartArea/SmartApps";
		String vreName = "NextNext";
	

		//		List<UserModel> users = um.listUsersByGroup(group.getGroupId());
		String vreDesignerUserName = designer;		
		if (vreDesignerUserName != null) {
			JCRWorkspace ws = (JCRWorkspace) HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(vreDesignerUserName)
					.getWorkspace();
			
			System.out.println(ws.getRoot().getPath());

			List<String> users = new ArrayList<String>();
			
			users.add("statistical.wps");
			users.add("roberto.cirillo");
			users.add("ngalante");
			users.add("gabriele.giammatteo");
			users.add("lucio.lelii");
			users.add("paul.taconet");
			users.add("gianpaolo.coro");
			users.add("ciro.formisano");
			users.add("valentina.marioli");
			users.add("andrea.dellamico");
			users.add("panagiota.koltsida");
			users.add("tommaso.piccioli");
			users.add("emmanuel.blondel");
			users.add("francesco.mangiacrapa");
			users.add("anton.ellenbroek");
			users.add("pasquale.pagano");
			users.add("leonardo.candela");
			users.add("costantino.perciante");
			users.add("giancarlo.panichi");
			users.add("luca.frosini");
			users.add("fabio.sinibaldi");
			users.add("yannis.marketakis");
			users.add("paolo.fabriani");
			users.add("nikolas.laskaris");
			users.add("massimiliano.assante");
			users.add("alessandro.pieve");
			users.add("denispyr");
			users.add("kostas.kakaletris");
			users.add("scarponi");
			users.add("pino.vaccaro");
			users.add("leviwesterveld");
			users.add("andrea.rossi");
			users.add("costa46");
			users.add("massi.testino");
			users.add("miles");
			users.add("kgiannakelos");
			users.add("grsf.publisher");
			users.add("ay");
			users.add("salvam");
			users.add("naialeoque");
			users.add("nikos");
			users.add("loredana.versienti");
			users.add("giorgosalex1521");
			users.add("a.test.toignore");
			
			
			GCubeGroup gGroup = createGroup(vreScope, users);


			//						GCubeGroup gGroup =um.getGroup(groupId);
//			UserManager um = HomeLibrary
//					.getHomeManagerFactory().getUserManager();
//			String groupId = "d4science.research-infrastructures.eu-SmartArea-SmartApps";



			String groupid = (gGroup == null) ? vreScope :  gGroup.getName();


			WorkspaceSharedFolder wSharedFolder = createVREFolder(vreScope, vreName, groupid, ws);

			List<String> groups = new ArrayList<String>();
			groups.add(gGroup.getName());
			wSharedFolder.setACL(groups, ACLType.WRITE_OWNER);



		} else {
			logger.error("NO VRE-MANAGER FOUND IN THIS VRE");			
		}
		ScopeProvider.instance.set(currScope);
	}

	private static GCubeGroup createGroup(String vreScope, List<String> usersToAdd) throws InternalErrorException {
		org.gcube.common.homelibrary.home.workspace.usermanager.UserManager gm = HomeLibrary
				.getHomeManagerFactory().getUserManager();
		GCubeGroup group = gm.createGroup(vreScope);
		for (String user : usersToAdd) {
			group.addMember(user);
		}
		return group;
	}

	private static WorkspaceSharedFolder createVREFolder(String vreScope, String vreName, String groupId, JCRWorkspace ws) throws Exception {		

		WorkspaceSharedFolder folder = ws.createSharedFolder(vreScope, "Special Shared folder for VRE " + vreName, groupId, ws.getMySpecialFolders().getId(), vreName, true);
		System.out.println(folder.getPath());
		return folder;

	}

}



