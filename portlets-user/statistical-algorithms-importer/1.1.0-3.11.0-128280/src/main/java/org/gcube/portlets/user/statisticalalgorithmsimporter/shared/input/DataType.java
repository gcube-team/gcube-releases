package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum DataType {
	STRING("String"), INTEGER("Integer"), DOUBLE("Double"), ENUMERATED(
			"Enumerated"), FILE("File"), BOOLEAN("Boolean");

	// private static InputTypeMessages
	// msgs=GWT.create(InputTypeMessages.class);
	private String id;

	// private static List<String> inputTypeI18NList;

	/*
	 * static { inputTypeI18NList = new ArrayList<String>(); for (InputType
	 * itype : values()) { inputTypeI18NList.add(msgs.inputType(itype)); } }
	 */

	private DataType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return id;
	}

	public String getLabel() {
		// return msgs.inputType(this);
		return id;
	}

	public static List<DataType> asList() {
		List<DataType> list = Arrays.asList(values());
		return list;
	}

	public static List<DataType> asListForGlobalVariables() {
		List<DataType> list = Arrays.asList(DataType.STRING, DataType.INTEGER,
				DataType.DOUBLE, DataType.FILE, DataType.BOOLEAN);
		return list;
	}

	/*
	 * public static List<String> asI18NList() { return inputTypeI18NList;
	 * 
	 * }
	 */

}
