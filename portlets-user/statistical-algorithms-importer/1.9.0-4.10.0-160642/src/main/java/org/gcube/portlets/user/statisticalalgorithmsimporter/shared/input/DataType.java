package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum DataType {
	STRING("String"), INTEGER("Integer"), DOUBLE("Double"), ENUMERATED("Enumerated"), FILE("File"), BOOLEAN("Boolean");

	private String id;

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
		return id;
	}

	public static List<DataType> asList() {
		List<DataType> list = Arrays.asList(values());
		return list;
	}

	public static List<DataType> asListForGlobalVariables() {
		List<DataType> list = Arrays.asList(DataType.STRING, DataType.INTEGER, DataType.DOUBLE, DataType.FILE,
				DataType.BOOLEAN);
		return list;
	}

}
