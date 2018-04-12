package org.gcube.portlets.widgets.netcdfbasicwidgets.client.event;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum SelectVariableEventType {
	Completed("Completed"), Failed("Failed"), Aborted("Aborted");

	/**
	 * 
	 * @param id id
	 */
	private SelectVariableEventType(final String id) {
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
	 * @param id id
	 * @return select area dialog event type
	 */
	public static SelectVariableEventType getTypeFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (SelectVariableEventType type : values()) {
			if (type.id.compareToIgnoreCase(id) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<SelectVariableEventType> asList() {
		List<SelectVariableEventType> list = Arrays.asList(values());
		return list;
	}

}
