package org.gcube.common.homelibrary.jcr.data;

import org.gcube.common.homelibrary.home.data.ApplicationsArea;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;

public class JCRApplicationsArea implements ApplicationsArea {

	private static final String SHARE = "share";
	private static final String USERS = "users";

	private WorkspaceFolder applicationsArea;
	private JCRWorkspace worksapce;

	public JCRApplicationsArea(JCRWorkspace workspace, JCRRepository repository) {
		this.worksapce = workspace;

//		Session session = null;
		try {

//			session =  JCRRepository.getSession();

			applicationsArea = workspace.getApplicationArea();

		} catch (InternalErrorException e) {

		}  
//		finally {
//			if (session!=null)
//				session.logout();
//		}
	}


	@Override
	public WorkspaceFolder getApplicationRoot(String applicationName)
			throws InternalErrorException {

//		Session session = null;
		WorkspaceFolder folder =  null;
		try {
//			session = JCRRepository.getSession();
			folder = (WorkspaceFolder)applicationsArea.find(applicationName);
			if (folder == null)
				folder = applicationsArea.createFolder(applicationName, applicationName);
			
		} catch (InsufficientPrivilegesException e) {
			throw new InternalErrorException(e);
		} catch (ItemAlreadyExistException e) {
			throw new InternalErrorException(e);
		} 
//		finally {
//			if (session!=null)
//			session.logout();
//		}
		return folder;

	}



	@Override
	public WorkspaceFolder getApplicationUserRoot(String applicationName)
			throws InternalErrorException {

//		Session session = JCRRepository.getSession();
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
//		finally {
//			if (session!=null)
//			session.logout();
//		}
	}

	@Override
	public WorkspaceFolder getApplicationShareRoot(String applicationName)
			throws InternalErrorException {
//		Session session = null;
		WorkspaceFolder applicationsRoot = getApplicationRoot(applicationName);
//		try {
//			session = JCRRepository.getSession();
			WorkspaceFolder shareFolder = (WorkspaceFolder)applicationsRoot.find(SHARE);
			return shareFolder;
//		} 
//		finally {
//			if (session!=null)
//			session.logout();
//		}
	}


}
