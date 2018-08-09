package org.gcube.data.access.storagehub;

import static org.gcube.common.storagehub.model.NodeConstants.*;
import java.util.Arrays;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.data.access.storagehub.handlers.ItemHandler;

@Singleton
public class AuthorizationChecker {

	public void checkReadAuthorizationControl(Session session, String id) throws Exception{
		Node node = session.getNodeByIdentifier(id);

		Item item  = ItemHandler.getItem(node, Arrays.asList(ACCOUNTING_NAME,CONTENT_NAME));

		if (item.isShared()) {
			SharedFolder parentShared = retrieveSharedFolderParent(item, session);
			if (!parentShared.getUsers().getValues().containsKey(AuthorizationProvider.instance.get().getClient().getId()))
				throw new IllegalAccessException("Insufficent Provileges to read node with id "+id);
		} else if (!item.getOwner().equals(AuthorizationProvider.instance.get().getClient().getId()))
			throw new IllegalAccessException("Insufficent Provileges to read node with id "+id);

	}

	private SharedFolder retrieveSharedFolderParent(Item item, Session session) throws Exception{
		if (item instanceof SharedFolder) return (SharedFolder)item;
		else 
			return retrieveSharedFolderParent(ItemHandler.getItem(session.getNodeByIdentifier(item.getParentId()), Arrays.asList(ACCOUNTING_NAME,CONTENT_NAME)), session);

	}

	public void checkWriteAuthorizationControl(Session session, String id, boolean isNewItem) throws Exception {
		//in case of newItem the id is the parent otherwise the old node to replace
		Node node = session.getNodeByIdentifier(id);

		Item item  = ItemHandler.getItem(node, Arrays.asList(ACCOUNTING_NAME,CONTENT_NAME, METADATA_NAME));

		if (item.isShared()) {
			SharedFolder parentShared = retrieveSharedFolderParent(item, session);
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(session, parentShared.getPath());
			AccessControlEntry[] entries = accessControlList.getAccessControlEntries();
				//put it in a different method
						
			for (AccessControlEntry entry: entries) {
				if (entry.getPrincipal().getName().equals(AuthorizationProvider.instance.get().getClient().getId()) || (parentShared.isVreFolder() && entry.getPrincipal().getName().equals(parentShared.getTitle()))) {
					for (Privilege privilege : entry.getPrivileges()){
						AccessType access = AccessType.fromValue(privilege.getName());
						if (isNewItem && access!=AccessType.READ_ONLY)
							return;
						else 
							if (!isNewItem && 
								(access==AccessType.ADMINISTRATOR || access==AccessType.WRITE_ALL || (access==AccessType.WRITE_OWNER && item.getOwner().equals(AuthorizationProvider.instance.get().getClient().getId())))) 
								return;
						
					}
					throw new IllegalAccessException("Insufficent Provileges to write node with id "+id);
				}
			}
			
		} else 
			if(item.getOwner().equals(AuthorizationProvider.instance.get().getClient().getId()))
				return;
		
		throw new IllegalAccessException("Insufficent Provileges to write node with id "+id);
		
	}
	/*
	private String retrieveOwner(Node node) {
		Node nodeOwner;
		//get Owner
		try{
			return node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}catch (Exception e) {
			try {
				nodeOwner = node.getNode(NodeProperty.OWNER.toString());
				return nodeOwner.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
				//				this.userId = nodeOwner.getProperty(USER_ID).getString();
				//				this.portalLogin = nodeOwner.getProperty(PORTAL_LOGIN).getString();
				//				node.getSession().save();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}

		}	
	}
	 */
}
