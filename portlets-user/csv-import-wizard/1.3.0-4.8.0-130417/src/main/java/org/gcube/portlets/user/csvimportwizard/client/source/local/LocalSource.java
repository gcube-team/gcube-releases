/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.source.local;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.source.CSVSource;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class LocalSource implements CSVSource {
	
	public static final LocalSource INSTANCE = new LocalSource();

	/**
	 * {@inheritDoc}
	 */
	
	public String getId() {
		return "local";
	}

	/**
	 * {@inheritDoc}
	 */
	
	public String getName() {
		return "Local source";
	}

	/**
	 * {@inheritDoc}
	 */
	
	public String getDescription() {
		return "Select this source if you want to upload the CSV file from your computer.";
	}

	/**
	 * {@inheritDoc}
	 */
	
	public Component getPanel(WizardCard card, CSVImportSession session) {
		return new LocalUploadPanel(card, session);
	}

	/**
	 * {@inheritDoc}
	 */
	
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
