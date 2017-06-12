/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.Panel;



public interface Source  {
	
	/**
	 * Returns the source id.
	 * @return the source id.
	 */
	public String getId();
	
	/**
	 * Returns the source name.
	 * @return the source name.
	 */
	public String getName();
	
	/**
	 * Returns the source description.
	 * @return the source description.
	 */
	public String getDescription();
	
	/**
	 * Returns the {@link Panel} representing this source.
	 * @param card the containing {@link WizardCard}.
	 * @param session the import session.
	 * @return the source panel.
	 */
	public Component getPanel(WizardCard card, ImportSession session);

}
