/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.ws.client;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.source.CSVSource;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceSource implements CSVSource {
	
	public static final WorkspaceSource INSTANCE = new WorkspaceSource();

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "workspace";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Workspace source";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this source if you want to get the CSV file from your workspace.";
	}

	/**
	 * {@inheritDoc}
	 */
	public Component getPanel(WizardCard card, CSVImportSession session) {
		return new WorkspaceUploadPanel(card, session);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkspaceSource [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}

}
