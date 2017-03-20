package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Workspace;

import org.apache.commons.io.FileUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;


public class Test {

	public static void main(String[] args) throws IOException {
		SecurityTokenProvider.instance.set("aac13fb7-d074-45ae-aa47-61718956a5e6-98187548");
		
		org.gcube.common.homelibrary.home.workspace.Workspace ws;
		try {
			ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome().getWorkspace();
			

			

			String absPath = "/Share/042efb85-240b-4a04-93d3-bca3924b7503/";
			list (ws.getItemByPath(absPath));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	

	}
	
	
	private static void list(WorkspaceItem node) throws Exception {
//		System.out.println(node.getPath());
		System.out.println(node.getPath() +" - " + node.getOwner().getPortalLogin());
		List<? extends WorkspaceItem> children;
		if (node.isFolder()){
			
				
			children = node.getChildren();
			for (WorkspaceItem child: children)
				list(child);
		}

	}

}
