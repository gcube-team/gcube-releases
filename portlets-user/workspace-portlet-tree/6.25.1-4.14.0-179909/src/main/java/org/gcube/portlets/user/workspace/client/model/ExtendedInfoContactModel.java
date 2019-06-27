package org.gcube.portlets.user.workspace.client.model;

import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class ExtendedInfoContactModel extends InfoContactModel {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9115514914951357467L;
	
	public static final String ICON = "icon";
	

	public ExtendedInfoContactModel() {}

	public ExtendedInfoContactModel(String id, String login, String fullName, boolean isGroup) {
		super(id, login, fullName, isGroup);
	}
	
	
	public void setIcon() {
		set(ICON, getIcon());
	}
	
	
	public Image getIcon() {
		
		if (!this.isGroup()) // NOT IS GROUP
			return Resources.getIconShareUser().createImage();
		else
			return Resources.getIconShareGroup().createImage();
	}
	
}