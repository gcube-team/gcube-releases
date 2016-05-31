package org.gcube.common.homelibrary.jcr.workspace;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRGroup;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.jcr.workspace.util.Utils;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import com.thoughtworks.xstream.XStream;


public class JCRWorkspaceVREFolder extends JCRWorkspaceSharedFolder implements WorkspaceVREFolder {


	private JCRUserManager um;

	public JCRWorkspaceVREFolder(JCRWorkspace workspace, ItemDelegate node) throws RepositoryException, InternalErrorException {
		super(workspace,node);	
	}


	public JCRWorkspaceVREFolder(JCRWorkspace workspace, ItemDelegate delegate,
			String VREname, String description, String groupID, String displayName, String scope) throws RepositoryException, InternalErrorException, ItemNotFoundException {		

		super(workspace,delegate,VREname,description, workspace.getMySpecialFolders().getId(), asList(groupID, VREname + "-Manager"), null, null);

		Map<NodeProperty, String> properties = delegate.getProperties();
		properties.put(NodeProperty.IS_VRE_FOLDER, new XStream().toXML(true));
		properties.put(NodeProperty.DISPLAY_NAME, displayName);
		properties.put(NodeProperty.GROUP_ID, groupID);
		properties.put(NodeProperty.SCOPE, new XStream().toXML(scope));

		save();		
		share();

	}

	//getter methods

	@Override
	public String getDisplayName() {
		return delegate.getProperties().get(NodeProperty.DISPLAY_NAME);
	}

	@Override
	public GCubeGroup getGroup() throws InternalErrorException {
	
		if (delegate.getProperties().get(NodeProperty.GROUP_ID)!=null){
			String groupId = delegate.getProperties().get(NodeProperty.GROUP_ID);

			if (um==null)
				um = (JCRUserManager) HomeLibrary.getHomeManagerFactory().getUserManager();
			return um.getGroup(groupId);
		}

		logger.error("Group ID not found in " + delegate.getPath());
		return null;
	}

	@Override
	public String getScope() throws InternalErrorException {
		if (delegate.getProperties().get(NodeProperty.SCOPE)!=null)
			return (String) new XStream().fromXML(delegate.getProperties().get(NodeProperty.SCOPE));

		logger.error("Scope not found in " + delegate.getPath());
		return null;
	}

	@Override
	public void changeOwner(String  user) throws InternalErrorException, RepositoryException {
		delegate.setOwner(user);
		save();

	}

	//set admins

	@Override
	public void addUserToVRE(String user) throws InternalErrorException {

		if (um==null)
			um = (JCRUserManager) HomeLibrary.getHomeManagerFactory().getUserManager();
		try {
			um.associateUserToGroup(getScope(), user);
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void addUser(String userToAdd) throws InsufficientPrivilegesException,
	InternalErrorException {

		HomeManagerFactory factory = HomeLibrary
				.getHomeManagerFactory();
		UserManager gm = factory.getUserManager();

		GCubeGroup group = getGroup();
		String manager = getManager(group.getName());
		try{
			if (gm.createUser(userToAdd, null))
				logger.trace(userToAdd + " has been created");

			//add user to group
			if (group!=null){
				group.addMember(userToAdd);								
			}

			//add user to share
			Workspace ws = null;
			try {
				ws = factory.getHomeManager()
						.getHome(manager)
						.getWorkspace();
			} catch (WorkspaceFolderNotFoundException e) {
				throw new InternalErrorException(e);
			} catch (HomeNotFoundException e) {
				throw new InternalErrorException(e);
			} catch (UserNotFoundException e) {
				throw new InternalErrorException(e);
			}

			if (ws!=null){
				JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath(workspace.getMySpecialFolders().getPath() + "/"+ getName());
				logger.trace("VRE folder path: " + folder.getPath());

				//share folder with the new member
				ArrayList<String> userList = new ArrayList<String>();
				super.addUser(userToAdd);
				//				user.add(userToAssociate);

				try {
					folder.share(userList);
				} catch (InsufficientPrivilegesException
						| WrongDestinationException e) {
					throw new InternalErrorException(e);
				}
			}

		} catch (InternalErrorException | ItemNotFoundException e) {
			throw new RuntimeException(e);
		}

	}


	/**
	 * Get manager name
	 * @param scope
	 * @return manager
	 */
	private String getManager(String groupId) {
		String manager = groupId + "-Manager";
		return manager;
	}


	@Override
	public void removeUserFromVRE(String user) throws InternalErrorException {
		if (um==null)
			um = (JCRUserManager) HomeLibrary.getHomeManagerFactory().getUserManager();
		try {
			um.removeUserFromGroup(getScope(), user);
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	@Override
	public boolean isVreFolder() {
		return true;
	}





}
