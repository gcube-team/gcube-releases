package org.gcube.data.access.storagehub;

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

		Item item  = ItemHandler.getItem(node, Arrays.asList("hl:accounting","jcr:content"));

		if (item.isShared()) {
			SharedFolder parentShared = retrieveSharedFolderParent(item, session);
			if (!parentShared.getUsers().getValues().containsKey(AuthorizationProvider.instance.get().getClient().getId()))
				throw new IllegalAccessException("Insufficent Provileges to read node with id "+id);
		} else if (!node.getProperty("hl:portalLogin").getString().equals(AuthorizationProvider.instance.get().getClient().getId()))
			throw new IllegalAccessException("Insufficent Provileges to read node with id "+id);

	}

	private SharedFolder retrieveSharedFolderParent(Item item, Session session) throws Exception{
		if (item instanceof SharedFolder) return (SharedFolder)item;
		else 
			return retrieveSharedFolderParent(ItemHandler.getItem(session.getNodeByIdentifier(item.getParentId()), Arrays.asList("hl:accounting","jcr:content")), session);

	}

	public void checkWriteAuthorizationControl(Session session, String id) throws Exception {

		Node node = session.getNodeByIdentifier(id);

		Item item  = ItemHandler.getItem(node, Arrays.asList("hl:accounting","jcr:content"));

		if (item.isShared()) {
			//put it in a different method
			JackrabbitAccessControlList accessControlList = AccessControlUtils.getAccessControlList(session, node.getPath());
			AccessControlEntry[] entries = accessControlList.getAccessControlEntries();
			for (AccessControlEntry entry: entries) {
				if (entry.getPrincipal().equals(AuthorizationProvider.instance.get().getClient().getId())) {
					for (Privilege privilege : entry.getPrivileges()){
						AccessType access = AccessType.valueOf(privilege.getName());
						if (access==AccessType.ADMINISTRATOR || access==AccessType.WRITE_ALL || (access==AccessType.WRITE_OWNER && item.getOwner().equals(AuthorizationProvider.instance.get().getClient().getId()))) 
							return;
						else throw new IllegalAccessException("Insufficent Provileges to write node with id "+id);
					}
				}
			}
			throw new IllegalAccessException("Insufficent Provileges to write node with id "+id);
		} else 
			if(!item.getOwner().equals(AuthorizationProvider.instance.get().getClient().getId()))
					throw new IllegalAccessException("Insufficent Provileges to write node with id "+id);
		
	}



}
