/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.source;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.Panel;


/**
 * CSV source interface. All CSV source have to implement this interface.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface CSVSource {
	
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
	public Component getPanel(WizardCard card, CSVImportSession session);

}
