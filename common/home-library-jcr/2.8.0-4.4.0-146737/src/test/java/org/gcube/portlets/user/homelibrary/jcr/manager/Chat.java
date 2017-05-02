package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;
import org.gcube.common.scope.api.ScopeProvider;

import com.itextpdf.text.log.SysoCounter;

import lombok.patcher.Symbols;

public class Chat {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {

		WorkspaceFolder folder = null;
		try {
			//			String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
			ScopeProvider.instance.set("/gcube/devNext/NextNext");

			//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

			Home home = manager.getHome("valentina.marioli");

			JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();

			UUID chatID = UUID.randomUUID();
			String attach01 = ws.getItemByPath("/Workspace/BBB/jennifer.jpg").getId();
			System.out.println("ID " +  attach01);

			URL attachURL01 = ws.attachToConversation(chatID, attach01);
			System.out.println(attachURL01.toString());

			String attach02 = ws.getItemByPath("/Workspace/BBB/susan.jpg").getId();
			System.out.println("ID " +  attach02);

			URL attachURL02 = ws.attachToConversation(chatID, attach02);
			System.out.println(attachURL02.toString());

			//			List<String> list = new ArrayList<String>();
			//			list.add("8689eed3-be9a-44bd-a46d-3741f4e372d0");
			//
			//			list.add("5d125931-2e18-46b6-8f69-a54358529de0");
			//			list.add("75d38281-93a2-4d6f-9045-e7579b9ee916");
			//			list.add("f69ae066-6be0-41d7-a78f-19a704bc880e");
			//			list.add("48e08f6a-e7c9-4cfc-856a-f42346c6360c");
			//			list.add("ec8a9916-8aac-4853-a28e-610422480943");
			//			list.add("f2f5f061-eb4b-4440-9e13-afe267b5174a");
			//			list.add("400d1ab5-8d22-43c0-819f-5cb6456c5255");
			//			list.add("8d1f8adf-0c0e-465b-a9c9-eae88e946c74");
			//			list.add("5b2d3f17-b341-4f00-8372-03b268332c36");
			//			list.add("08405094-a743-41f4-bb0f-9a16c9cd2ede");
			//			list.add("59f4acc4-4fe6-43fe-92e1-9f3829840a4e");
			//			list.add("72572d95-fcc6-449d-9ae0-4da161cde9b5");
			//			list.add("6ceb2af6-3d50-4ee8-a053-cbcf69d757ba");
			//			list.add("e72ea0b5-af36-4b5b-849c-4be06322f83e");
			//			list.add("dd94d424-e00b-4a57-a276-c52a9e74f3cf");
			//			list.add("47d28b7b-5791-4041-a6b4-8f302e89ccb5");
			//
			//			for (String id: list){
			//				//	String ConversationId = "219adf11-ae8b-4864-954f-baf9cca03bd0";
			//				System.out.println(ws.deleteAllConversationAttachments(UUID.fromString(id)));
			//			}



		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
