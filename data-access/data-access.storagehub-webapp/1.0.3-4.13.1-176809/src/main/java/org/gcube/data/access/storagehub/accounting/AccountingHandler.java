package org.gcube.data.access.storagehub.accounting;

import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountingEntryType;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AccountingHandler {

	private static final String USER = "hl:user";
	private static final String DATE = "hl:date";
	private static final String ITEM_NAME = "hl:itemName";
	private static final String ITEM_TYPE = "hl:itemType";
	private static final String MIME_TYPE = "hl:mimeType";
	private static final String MEMBERS = "hl:members";
	private static final String OLD_ITEM_NAME = "hl:oldItemName";
	private static final String NEW_ITEM_NAME = "hl:newItemName";
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(AccountingHandler.class);

	public void createReadObj(String title, Session ses, Node node, boolean saveHistory ) {
		try {
			Node directoryNode = node.getParent();
			
			if (!directoryNode.hasNode(NodeProperty.ACCOUNTING.toString())){
				directoryNode.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
				
			}
			
			Node accountingNodeParent = directoryNode.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.READ.getNodeTypeDefinition());
			accountingNode.setProperty(USER, AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty(DATE, Calendar.getInstance());
			accountingNode.setProperty(ITEM_NAME, title);
						
			try {
				VersionManager vManager = ses.getWorkspace().getVersionManager();
				VersionHistory history = vManager.getVersionHistory(node.getNode("jcr:content").getPath());
				VersionIterator versions = history.getAllVersions();
				Version version= null;
				while (versions.hasNext()) {
				  version = versions.nextVersion();
				}
				if (version!=null)
					accountingNode.setProperty("hl:version", version.getName());
			}catch(UnsupportedRepositoryOperationException uropex) {
				logger.warn("version cannot be retrieved", uropex);
			}
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}
	
	
	public void createFolderAddObj(String title, String itemType, String mimeType, Session ses, Node node, boolean saveHistory ) {
		try {
			Node directoryNode = node.getParent();
			
			if (!directoryNode.hasNode(NodeProperty.ACCOUNTING.toString())){
				directoryNode.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
			}
			
			Node accountingNodeParent = directoryNode.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.ADD.getNodeTypeDefinition());
			accountingNode.setProperty(USER, AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty(DATE, Calendar.getInstance());
			accountingNode.setProperty(ITEM_NAME, title);
			accountingNode.setProperty(ITEM_TYPE, itemType);
			if (mimeType!=null)
				accountingNode.setProperty(MIME_TYPE, mimeType);
			
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}
	
	public void createFolderRemoveObj(String title, String itemType, String mimeType, Session ses, Node parentNode, boolean saveHistory ) {
		try {
						
			if (!parentNode.hasNode(NodeProperty.ACCOUNTING.toString())){
				parentNode.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
			}
			
			Node accountingNodeParent = parentNode.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.REMOVAL.getNodeTypeDefinition());
			accountingNode.setProperty(USER, AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty(DATE, Calendar.getInstance());
			accountingNode.setProperty(ITEM_NAME, title);
			accountingNode.setProperty(ITEM_TYPE, itemType);
			if (mimeType!=null)
				accountingNode.setProperty(MIME_TYPE, mimeType);
			
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}
	
	public void createShareFolder(String title, Set<String> users, Session ses, Node sharedNode,  boolean saveHistory  ) {
		try {
						
			if (!sharedNode.hasNode(NodeProperty.ACCOUNTING.toString())){
				sharedNode.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
			}
			
			Node accountingNodeParent = sharedNode.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.SHARE.getNodeTypeDefinition());
			accountingNode.setProperty(USER, AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty(DATE, Calendar.getInstance());
			accountingNode.setProperty(ITEM_NAME, title);
			accountingNode.setProperty(MEMBERS, users.toArray(new String[users.size()]));
			
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}
	
	public void createUnshareFolder(String title, Session ses, Node sharedNode,  boolean saveHistory  ) {
		try {
						
			if (!sharedNode.hasNode(NodeProperty.ACCOUNTING.toString())){
				sharedNode.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
			}
			
			Node accountingNodeParent = sharedNode.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.SHARE.getNodeTypeDefinition());
			accountingNode.setProperty(USER, AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty(DATE, Calendar.getInstance());
			accountingNode.setProperty(ITEM_NAME, title);
			
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}
	
	public void createRename(String oldTitle, String newTitle, Node node, Session ses, boolean saveHistory  ) {
		try {
						
			if (!node.hasNode(NodeProperty.ACCOUNTING.toString())){
				node.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
			}
			
			Node accountingNodeParent = node.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.RENAMING.getNodeTypeDefinition());
			accountingNode.setProperty(USER, AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty(DATE, Calendar.getInstance());
			accountingNode.setProperty(OLD_ITEM_NAME, oldTitle);
			accountingNode.setProperty(NEW_ITEM_NAME, newTitle);
			
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}
	
}
