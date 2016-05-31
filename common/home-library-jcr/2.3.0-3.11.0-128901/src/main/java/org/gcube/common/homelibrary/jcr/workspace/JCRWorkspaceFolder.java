package org.gcube.common.homelibrary.jcr.workspace;

import java.util.List;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRAccessManager;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import com.thoughtworks.xstream.XStream;


public class JCRWorkspaceFolder extends JCRAbstractWorkspaceFolder {

	public JCRWorkspaceFolder(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {	
		super(workspace,delegate);	
	}

	public JCRWorkspaceFolder(JCRWorkspace workspace, ItemDelegate node,
			String name, String description) throws RepositoryException  {		
		super(workspace,node,name,description);
	}

	public JCRWorkspaceFolder(JCRWorkspace workspace, ItemDelegate node,
			String name, String description, Map<String, String> properties) throws RepositoryException  {		
		super(workspace,node,name,description, properties);
	}

	@Override
	public WorkspaceItemType getType() {
		return WorkspaceItemType.FOLDER;
	}

	

	@Override	
	public WorkspaceSharedFolder share(List<String> users) throws InsufficientPrivilegesException,
	WrongDestinationException, InternalErrorException {

		try {
			return workspace.shareFolder(users, getId());
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (InternalErrorException e) {
			throw new InternalErrorException(e);
		}

	}

	@Override
	public void setACL(List<String> users,  ACLType privilege) throws InternalErrorException {

		if (!isShared())
			throw new InternalErrorException("ACL cannot be set if the folder is not shared");

		String absPath = null;

		try{
			absPath = getAbsolutePath();
		}catch (Exception e) {
			logger.error("Error retrieving absolute path");
		}

		if (absPath == null)
			throw new InternalErrorException("Absolute path cannot be null setting ACL");

		boolean flag = false;
		JCRAccessManager accessManager = new JCRAccessManager();

		int i = 0;
		while ((flag==false) && (i<3)){
			i++;
			try{
				switch(privilege){

				case NONE:
					flag = accessManager.setAccessDenied(users, absPath);		
					break;
				case READ_ONLY:
					flag = accessManager.setReadOnlyACL(users, absPath);		
					break;
				case WRITE_OWNER:	
					flag = accessManager.setWriteOwnerACL(users, absPath);		
					break;
				case WRITE_ALL:
					flag = accessManager.setWriteAllACL(users, absPath);	
					break;
				case ADMINISTRATOR:
					flag = accessManager.setAdminACL(users, absPath);	
					break;
				default:
					break;
				}

				if (flag==false)
					Thread.sleep(1000);

			}catch (Exception e) {
				logger.error("an error occurred setting ACL on: " + absPath);
			}
		}

		logger.info("Has ACL been modified correctly for users " + users.toString() + "in path " + absPath + "? " + flag);	
	}







}
