package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.apache.commons.io.FileUtils;
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
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;

import com.itextpdf.text.log.SysoCounter;

import lombok.patcher.Symbols;

public class CheckVRes {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		try {

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

			Home home = manager.getHome("massimiliano.assante");

			JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();

			//			WorkspaceSharedFolder vre = ws.getVREFolderByScope("/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject");
			//			System.out.println(vre.getName());
			//			System.out.println(vre.getOwner().getPortalLogin());

			JCRWorkspaceVREFolder vre = (JCRWorkspaceVREFolder) ws.getVREFolderByScope("/d4science.research-infrastructures.eu/gCubeApps/gCube");
			vre.changeOwner("valentina.marioli");
			System.out.println(vre.getName());
			System.out.println(vre.getOwner().getPortalLogin());

			WorkspaceItem folder = ws.getItem(vre.getId());
			System.out.println("** " + folder.getOwner().getPortalLogin());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}




}
