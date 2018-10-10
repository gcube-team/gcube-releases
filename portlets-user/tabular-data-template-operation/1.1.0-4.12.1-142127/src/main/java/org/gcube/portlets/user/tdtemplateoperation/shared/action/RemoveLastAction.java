/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared.action;

import java.io.Serializable;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 31, 2015
 */
public class RemoveLastAction implements TabularDataAction, Serializable{

	/**
	 * 
	 */
	public static final String REMOVE_LAST_ACTION = "Remove Last Action";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8218417582987724698L;

	/**
	 * @param result 
	 * 
	 */
	public RemoveLastAction() {
		
	}
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getId()
	 */
	@Override
	public String getId() {
		return RemoveLastAction.class.getName();
	}
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getDescription()
	 */
	@Override
	public String getDescription() {
		return REMOVE_LAST_ACTION;
	}

}
