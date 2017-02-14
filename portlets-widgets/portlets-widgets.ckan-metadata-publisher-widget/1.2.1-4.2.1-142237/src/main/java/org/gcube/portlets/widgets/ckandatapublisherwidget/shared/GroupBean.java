package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;

/**
 * A group bean.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GroupBean implements Serializable {

	private static final long serialVersionUID = -5529957814115387053L;
	String groupTitle;
	String groupName;

	public GroupBean() {
		super();
	}

	/**
	 * @param groupTitle
	 * @param groupName
	 */
	public GroupBean(String groupTitle, String groupName) {
		super();
		this.groupTitle = groupTitle;
		this.groupName = groupName;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "GroupBean [groupTitle=" + groupTitle + ", groupName="
				+ groupName + "]";
	}

}
