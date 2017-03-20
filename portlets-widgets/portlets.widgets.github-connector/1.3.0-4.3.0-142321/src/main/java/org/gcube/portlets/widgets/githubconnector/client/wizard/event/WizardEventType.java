package org.gcube.portlets.widgets.githubconnector.client.wizard.event;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum WizardEventType {
	Completed("Completed"), Failed("Failed"), Aborted("Aborted"), Background(
			"Background");

	/**
	 * @param text
	 */
	private WizardEventType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}

	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static WizardEventType getTypeFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (WizardEventType type : values()) {
			if (type.id.compareToIgnoreCase(id) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<WizardEventType> asList() {
		List<WizardEventType> list = Arrays.asList(values());
		return list;
	}

}
