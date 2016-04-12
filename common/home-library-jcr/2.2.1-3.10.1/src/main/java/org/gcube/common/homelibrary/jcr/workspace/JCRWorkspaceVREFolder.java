package org.gcube.common.homelibrary.jcr.workspace;


import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;


public class JCRWorkspaceVREFolder extends JCRWorkspaceSharedFolder implements WorkspaceVREFolder {

//	private String displayName;
	

	public JCRWorkspaceVREFolder(JCRWorkspace workspace, ItemDelegate node) throws RepositoryException, InternalErrorException {
		super(workspace,node);	

//		try{
//			this.displayName = node.getProperty(DISPLAY_NAME).getString();
//		}catch (Exception e) {}


	}

	@Override
	public List<String> getGroups() throws InternalErrorException {

		List<String> groups = new ArrayList<String>();
		try {	
//				System.out.println(this.getUsers().toString());
//				System.out.println(this.getMembers().toString());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return groups;
	}


//	/**
//	 * @return the displayName
//	 */
//	public String getDisplayName() {
//		return displayName;
//	}




}
