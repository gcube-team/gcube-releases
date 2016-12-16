package org.apache.jackrabbit.j2ee.workspacemanager;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccoutingNodeWrapper;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WrapManager {

	private static Logger logger = LoggerFactory.getLogger(WrapManager.class);
	
	Session session;
	String login;

	public WrapManager(Session session, String login) throws Exception {
		this.session = session;
		this.login = login;
	}
	
	/**
	 * Get itemDelegate by Node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public ItemDelegate getItemDelegateByNode(Node node) throws Exception {
		logger.info("Getting itemDelegate by Node with id: " +  node.getIdentifier());
		NodeManager wrap = new NodeManager(node, login);
		ItemDelegate item = wrap.getItemDelegate();
		return item;
	}

	/**
	 * Save changes and get new itemDelegate
	 * @param delegate
	 * @return
	 * @throws Exception
	 */
	public ItemDelegate save(ItemDelegate delegate) throws Exception {
		logger.info("Save changes to itemDelegate with id: " +  delegate.getId());
		ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, "");
		ItemDelegate new_item = wrapper.save(session);
		return new_item;
	}

	
	public AccountingDelegate getAccoutingDelegateByNode(Node node) throws Exception {
		logger.info("Getting itemDelegate by Node with id: " +  node.getIdentifier());
		AccoutingNodeWrapper wrap = new AccoutingNodeWrapper(node);
		AccountingDelegate item = wrap.getAccountingDelegate();
		return item;
	}
	

}
