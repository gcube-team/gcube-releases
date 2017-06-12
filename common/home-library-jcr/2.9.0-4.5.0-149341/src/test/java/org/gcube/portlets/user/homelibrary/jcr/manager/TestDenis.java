package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDenis {
	static Workspace ws = null;
	public static Logger logger = LoggerFactory.getLogger(TestDenis.class);
	public static void main(String[] args) throws Exception {
//		ScopeProvider.instance.set("/gcube/preprod/Dorne");
//		ScopeProvider.instance.set("/gcube/preprod/Dorne");
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
//		ScopeProvider.instance.set("/gcube");
		final String UPLOAD_FOLDER_NAME = "GrowthAnalysisUploads";
		
		ws = getWorkspace("mister.orange");
		System.out.println(ws.getRoot().getPath());
		
		WorkspaceSharedFolder dorne = (WorkspaceSharedFolder) ws.getVREFolderByScope("/gcube/preprod/Dorne");
//		System.out.println(dorne.getMembers());
//		dorne.addUserToVRE("panagiota.koltsida");
//		System.out.println(ws.getVREFolderByScope("/gcube/preprod/Dorne").getPath());
		
//		System.out.println(getUploadFolder(ws, "testShare").getPath());
//		ws = (JCRWorkspace) HomeLibrary
//				.getHomeManagerFactory()
//				.getHomeManager()
//				.getHome("kostashirikakis").getWorkspace();
//
//
//		String parentFolderId = ws.getRoot().getId();
//		WorkspaceFolder folder;
//		String name = "SharedFolder";
//		if (!ws.exists(name, parentFolderId)) {
//			String description = "TEST";sharedFolder.setACL(users, ACLType.READ_ONLY);
//			folder = ws.createSharedFolder(name, description, getUsers(), parentFolderId);
//		} else {
//			folder = (WorkspaceFolder) ws.find(name, parentFolderId);
//		}
//		
//		System.out.println(folder.getPath());


	}

	

	private static Workspace getWorkspace(String username) throws Exception {
		HomeManagerFactory factory;
		try {
			factory = HomeLibrary.getHomeManagerFactory();

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = factory.getHomeManager();

			// Then we retrieve the User home:
			User user = manager.createUser(username);
			Home home = manager.getHome(user);

			// At this point we can get the Workspace with his root:
			Workspace ws = home.getWorkspace();

			return ws;
		} catch (HomeNotFoundException | WorkspaceFolderNotFoundException | InternalErrorException e) {
			throw new Exception("Could not get workspace", e);
		}

	}

	static List<String> getUsers() {
		// TODO fill based on companyid? other way?
		List<String> users = new ArrayList<>();
		users.add("denispyr");
		users.add("gantzoulatos");
		users.add("kostashirikakis");
		return users;
	}

	private static WorkspaceFolder getUploadFolder(Workspace ws, String name) throws Exception {

		// create ws folder
		String description = "Uploaded files from Growth Analysis Setup";
		WorkspaceFolder parentFolder = ws.getRoot();
		String parentFolderId;
		WorkspaceFolder folder = null;
		try {
			parentFolderId = parentFolder.getId();

			if (logger.isTraceEnabled())
				logger.trace(String.format("folder [%s] exists in [%s]? [%s]", name, parentFolder.getName(),
						ws.exists(name, parentFolderId)));
			if (!ws.exists(name, parentFolderId)) {
				folder = ws.createSharedFolder(name, description, getUsers(), parentFolderId);
				folder.setACL(getUsers(), ACLType.WRITE_ALL);
			} else {
				folder = (WorkspaceFolder) ws.find(name, parentFolderId);
			}
			if (logger.isTraceEnabled())
				logger.trace(String.format("final folder [%s]", folder));
		} catch (InternalErrorException | ItemNotFoundException | WrongItemTypeException
				| InsufficientPrivilegesException | ItemAlreadyExistException | WrongDestinationException
				| WorkspaceFolderNotFoundException e) {
			// throw new Exception(String.format("Could not get or create upload
			// folder [%s]", name), e);
			logger.error(String.format("Could not get or create upload folder [%s]", name), e);
		} catch (Exception e) {
			// throw new Exception(String.format("Could not get or create upload
			// folder [%s]", name), e);
			logger.error(String.format("Could not get or create upload folder [%s]", name),
					new Exception("Undeclared exception catched", e));
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("final folder [%s]", folder));
		return folder;

	}
}
