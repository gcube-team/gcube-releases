/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared.action;

import java.io.Serializable;

/**
 * The Class AddCoulmnAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 31, 2015
 */
public class AddCoulmnAction implements TabularDataAction, Serializable{

	/**
	 * 
	 */
	public static final String ADD_COLUMN = "Add Column";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1750646343345695750L;

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getId()
	 */
	@Override
	public String getId() {
		return AddCoulmnAction.class.getName();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getDescription()
	 */
	@Override
	public String getDescription() {
		return ADD_COLUMN;
	}
	
	/**
	 * Instantiates a new adds the coulmn action.
	 */
	public AddCoulmnAction() {
	}

}
