package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import java.util.Map;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class AccountingDelegateWrapper {

	private static Logger logger = LoggerFactory.getLogger(AccountingDelegateWrapper.class);

	String portalLogin;
	AccountingDelegate item;
	XStream xstream;

	public AccountingDelegateWrapper(AccountingDelegate item, String portalLogin) throws Exception {
		this.item = item;
		this.portalLogin = portalLogin;
		xstream = new XStream();
	}


	/**
	 * If the item already exists, update it otherwise add it
	 * @return
	 * @throws NoSuchNodeTypeException
	 * @throws VersionException
	 * @throws ConstraintViolationException
	 * @throws LockException
	 * @throws RepositoryException
	 */
	public AccountingDelegate save(Session session) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {

//		System.out.println("save");
		try{
			Node node = session.getNodeByIdentifier(item.getId());

			logger.info("Adding " + item.getEntryType()+ " to node: " + node.getPath());
//			System.out.println("Adding " + item.getEntryType()+ " to node: " + node.getPath());

			if (!node.hasNode(NodeProperty.ACCOUNTING.toString())){
				node.addNode(NodeProperty.ACCOUNTING.toString(), NodeProperty.NT_ACCOUNTING.toString());
				session.save();
			}
			Node accountingNode = node.getNode(NodeProperty.ACCOUNTING.toString());
			AccountingEntryType nodeType = AccountingEntryType.valueOf(item.getEntryType().toString());

//			System.out.println(("Accountin node type " + nodeType.getNodeTypeDefinition()));
//			logger.debug("Accountin node type " + nodeType.getNodeTypeDefinition());
			Node entryNode = accountingNode.addNode(UUID.randomUUID().toString(),
					nodeType.getNodeTypeDefinition());

			entryNode.setProperty(AccountingProperty.USER.toString(), item.getUser());
			entryNode.setProperty(AccountingProperty.DATE.toString(), item.getDate());

			setCustomProperties(entryNode, item);

			session.save();

		}catch (Exception e) {
			logger.error("impossible to save AccountingDelegate", e);
		}
		return item;
	}


	private void setCustomProperties(Node entryNode, AccountingDelegate item) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		AccountingEntryType type = item.getEntryType();

		Map<AccountingProperty, String> properties = item.getAccountingProperties();

		switch (type) {
		case SET_ACL:
		case SHARE:
			entryNode.setProperty(AccountingProperty.MEMBERS.toString(), (String[]) new XStream().fromXML(properties.get(AccountingProperty.MEMBERS)));
		case UPDATE:
		case UNSHARE:
		case RESTORE:
		case ENABLED_PUBLIC_ACCESS:
		case DISABLED_PUBLIC_ACCESS:
		case READ:
			entryNode.setProperty(AccountingProperty.ITEM_NAME.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.ITEM_NAME)));
			break;

		case DELETE:
			entryNode.setProperty(AccountingProperty.ITEM_NAME.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.ITEM_NAME)));
			entryNode.setProperty(AccountingProperty.FROM_PATH.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.FROM_PATH)));
			break;

		case RENAMING:
			entryNode.setProperty(AccountingProperty.OLD_ITEM_NAME.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.OLD_ITEM_NAME)));
			entryNode.setProperty(AccountingProperty.NEW_ITEM_NAME.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.NEW_ITEM_NAME)));			
			break;
		case PASTE:
			entryNode.setProperty(AccountingProperty.FROM_PATH.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.FROM_PATH)));
			break;
			
		case REMOVAL:
		case ADD:		
		case CUT:
			try{
			FolderItemType folderItemType11 = (FolderItemType) new XStream().fromXML(properties.get(AccountingProperty.FOLDER_ITEM_TYPE));
			entryNode.setProperty(AccountingProperty.FOLDER_ITEM_TYPE.toString(), folderItemType11.toString());
			entryNode.setProperty(AccountingProperty.MIME_TYPE.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.MIME_TYPE)));
			}catch (Exception e) {
//				logger.info("FOLDER_ITEM_TYPE and MIME_TYPE not in " + entryNode.getPath() );
			}
			
			WorkspaceItemType itemType11 = (WorkspaceItemType) new XStream().fromXML(properties.get(AccountingProperty.ITEM_TYPE));
			entryNode.setProperty(AccountingProperty.ITEM_TYPE.toString(), itemType11.toString());
			entryNode.setProperty(AccountingProperty.ITEM_NAME.toString(), (String) new XStream().fromXML(properties.get(AccountingProperty.ITEM_NAME)));
			break;

		default:
			break;
		}

	}



}
