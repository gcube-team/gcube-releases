/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local;


import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;

import com.extjs.gxt.ui.client.widget.Component;


public class LocalSource implements Source {
	
	public static final LocalSource INSTANCE = new LocalSource();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return "local";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "Local source";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "Select this source if you want to upload the file from your computer.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getPanel(WizardCard card, ImportSession session) {
		return new LocalUploadPanel(card, session);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalSource [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}

	

}
