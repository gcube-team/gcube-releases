package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class AccoutingNodeWrapper {


	private static Logger logger = LoggerFactory.getLogger(AccoutingNodeWrapper.class);

	Node entryNode;
	String portalLogin;
	XStream xstream;

	public AccoutingNodeWrapper(Node entryNode) throws Exception {	
		this.entryNode = entryNode;
	}

	public AccountingDelegate getAccountingDelegate() throws RepositoryException {
		//		System.out.println("*****node.getPath(): " + node.getPath() + " - node.getPrimaryNodeType().getName(): " + node.getPrimaryNodeType().getName());


		switch (AccountingEntryType.getEnum(
				entryNode.getPrimaryNodeType().getName())) {
				case CUT:
					return new JCRAccountingFolderEntryCut(entryNode).getAccoutingDelegate();
				case PASTE:
					return new JCRAccountingEntryPaste(entryNode).getAccoutingDelegate();
				case REMOVAL:
					return new JCRAccountingFolderEntryRemoval(entryNode).getAccoutingDelegate();
				case RENAMING:
					return new JCRAccountingEntryRenaming(entryNode).getAccoutingDelegate();
				case ADD:
					return new JCRAccountingFolderEntryAdd(entryNode).getAccoutingDelegate();							
				case UPDATE:
					return new JCRAccountingEntryUpdate(entryNode).getAccoutingDelegate();
				case READ:
					return new JCRAccountingEntryRead(entryNode).getAccoutingDelegate();
				case SHARE:
					return new JCRAccountingEntryShare(entryNode).getAccoutingDelegate();
				case UNSHARE:
					return new JCRAccountingEntryUnshare(entryNode).getAccoutingDelegate();
				case RESTORE:
					return new JCRAccountingEntryRestore(entryNode).getAccoutingDelegate();
				case DELETE:
					return new JCRAccountingEntryDelete(entryNode).getAccoutingDelegate();
				default:
					return null;		
		}

	}


}
