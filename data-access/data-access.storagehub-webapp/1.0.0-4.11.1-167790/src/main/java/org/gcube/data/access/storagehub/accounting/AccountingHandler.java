package org.gcube.data.access.storagehub.accounting;

import java.util.Calendar;
import java.util.UUID;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountingEntryType;
import org.gcube.common.storagehub.model.types.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AccountingHandler {


	/*@Attribute("hl:user")
	@Attribute("hl:date")
	@Attribute("hl:version")*/

	private static final Logger logger = LoggerFactory.getLogger(AccountingHandler.class);

	public void createReadObj(String title, Session ses, Node node, boolean saveHistory ) {
		try {
			Node directoryNode = node.getParent();
			
			if (!directoryNode.hasNode(NodeProperty.ACCOUNTING.toString())){
				directoryNode.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
				
			}
			
			Node accountingNodeParent = directoryNode.getNode(NodeProperty.ACCOUNTING.toString());
			Node accountingNode = accountingNodeParent.addNode(UUID.randomUUID().toString(),AccountingEntryType.READ.getNodeTypeDefinition());
			accountingNode.setProperty("hl:user", AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty("hl:date", Calendar.getInstance());
			accountingNode.setProperty("hl:itemName", title);

			/*try {
				//VersionManager vManager = ses.getWorkspace().getVersionManager();
				//VersionManager versionManager = session.getWorkspace().getVersionManager();
				//Version version = versionManager.checkin(node.getPath());
				//Version version = vManager.getBaseVersion(node.getNode("jcr:content").getPath());
				//accountingNode.setProperty("hl:version", version.getName());
			}catch(UnsupportedRepositoryOperationException uropex) {
				logger.warn("version cannot be retrieved", uropex);
			}*/
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
			accountingNode.setProperty("hl:user", AuthorizationProvider.instance.get().getClient().getId());
			accountingNode.setProperty("hl:date", Calendar.getInstance());
			accountingNode.setProperty("hl:itemName", title);
			accountingNode.setProperty("hl:itemType", itemType);
			if (mimeType!=null)
				accountingNode.setProperty("hl:mimeType", mimeType);
			
			if (saveHistory) ses.save();
		} catch (RepositoryException e) {
			logger.warn("error trying to retrieve accountign node",e);
		}
	}

}
