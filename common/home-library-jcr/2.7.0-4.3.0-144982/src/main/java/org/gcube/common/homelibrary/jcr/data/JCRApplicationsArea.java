package org.gcube.common.homelibrary.jcr.data;

import org.gcube.common.homelibrary.home.data.ApplicationsArea;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;

public class JCRApplicationsArea implements ApplicationsArea {

	private static final String SHARE = "share";
	private static final String USERS = "users";

	private WorkspaceFolder applicationsArea;
	private JCRWorkspace worksapce;

	public JCRApplicationsArea(JCRWorkspace workspace) {
		this.worksapce = workspace;
		
		try {
			applicationsArea = workspace.getApplicationArea();
		} catch (InternalErrorException e) {

		}  
	}


	@Override
	public WorkspaceFolder getApplicationRoot(String applicationName)
			throws InternalErrorException {

		WorkspaceFolder folder =  null;
		try {
			folder = (WorkspaceFolder)applicationsArea.find(applicationName);
			if (folder == null)
				folder = applicationsArea.createFolder(applicationName, applicationName);

		} catch (InsufficientPrivilegesException e) {
			throw new InternalErrorException(e);
		} catch (ItemAlreadyExistException e) {
			throw new InternalErrorException(e);
		} 
		return folder;

	}



	@Override
	public WorkspaceFolder getApplicationUserRoot(String applicationName)
			throws InternalErrorException {

		WorkspaceFolder applicationsRoot = getApplicationRoot(applicationName);
		try {
			WorkspaceFolder usersFolder = (WorkspaceFolder)applicationsRoot.find(USERS);
			WorkspaceFolder userFolder = (WorkspaceFolder)usersFolder.find(
					worksapce.getOwner().getPortalLogin());
			if (userFolder == null)
				userFolder = usersFolder.createFolder(worksapce.getOwner().getPortalLogin(),
						applicationName);
			return userFolder;
		} catch (InsufficientPrivilegesException e) {
			throw new InternalErrorException(e);
		} catch (ItemAlreadyExistException e) {
			throw new InternalErrorException(e);
		} 

	}

	@Override
	public WorkspaceFolder getApplicationShareRoot(String applicationName)
			throws InternalErrorException {

		WorkspaceFolder applicationsRoot = getApplicationRoot(applicationName);
		WorkspaceFolder shareFolder = (WorkspaceFolder)applicationsRoot.find(SHARE);
		return shareFolder;

	}


}
