package org.gcube.portlets.user.statisticalalgorithmsimporter.client.codeparser;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.regexp.shared.RegExp;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CodeParser {

	private static final String IMAGE_PATTERN = "([^\\s]+(\\.(jpg|png|gif|bmp)\"))";
	private static final String FILE_PATTERN = "([^\\s]+(\\.(txt|csv|pdf|doc|zip|bin|dat|mat|java|m|exe|sh|tar|gz|r)\"))";

	// private static final String ENUM1_PATTERN = "(c\\([^\\)]*\\))";
	// private static final String ENUM2_PATTERN = "(\\{[^\\}]*\\})";

	public CodeParser() {

	}

	public InputOutputVariables parse(String parameter, IOType ioType) {
		InputOutputVariables selectedRowsVariables = null;

		if (parameter == null) {
			return null;
		}

		if (parameter.contains("<-")) {
			String[] varDescription = parameter.split("<-");
			String checkDataTypeValue = varDescription[1].trim();
			if (checkDataTypeValue.endsWith(";"))
				checkDataTypeValue = checkDataTypeValue.substring(0, checkDataTypeValue.length() - 1);
			String defaultValue = varDescription[1].trim();
			if (defaultValue.endsWith(";"))
				defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
			if (defaultValue.startsWith("\""))
				defaultValue = defaultValue.substring(1);
			if (defaultValue.endsWith("\""))
				defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
			String[] def = defaultValue.split("\"");
			if (def.length > 2) {
				defaultValue = def[0];
				for (int i = 1; i < def.length; i++) {
					defaultValue = defaultValue + "\\\"" + def[i];
				}
			}
			if (ioType.compareTo(IOType.OUTPUT) == 0) {
				selectedRowsVariables = new InputOutputVariables(varDescription[0].trim(), varDescription[0].trim(),
						defaultValue, DataType.FILE, ioType, parameter);
			} else {
				selectedRowsVariables = new InputOutputVariables(varDescription[0].trim(), varDescription[0].trim(),
						defaultValue, checkDataType(checkDataTypeValue), ioType, parameter);
			}
		} else {
			if (parameter.contains("=")) {
				String[] varDescription = parameter.split("=");
				String checkDataTypeValue = varDescription[1].trim();
				if (checkDataTypeValue.endsWith(";"))
					checkDataTypeValue = checkDataTypeValue.substring(0, checkDataTypeValue.length() - 1);
				String defaultValue = varDescription[1].trim();
				if (defaultValue.endsWith(";"))
					defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
				if (defaultValue.startsWith("\""))
					defaultValue = defaultValue.substring(1);
				if (defaultValue.endsWith("\""))
					defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
				String[] def = defaultValue.split("\"");
				if (def.length > 2) {
					defaultValue = def[0];
					for (int i = 1; i < def.length; i++) {
						defaultValue = defaultValue + "\\\"" + def[i];
					}
				}

				if (ioType.compareTo(IOType.OUTPUT) == 0) {
					selectedRowsVariables = new InputOutputVariables(varDescription[0].trim(), varDescription[0].trim(),
							defaultValue, DataType.FILE, ioType, parameter);
				} else {
					selectedRowsVariables = new InputOutputVariables(varDescription[0].trim(), varDescription[0].trim(),
							defaultValue, checkDataType(checkDataTypeValue), ioType, parameter);
				}

			} else {
				return null;
			}
		}

		/*if (selectedRowsVariables != null) {
			if (selectedRowsVariables.getName() != null && !selectedRowsVariables.getName().isEmpty()
					&& selectedRowsVariables.getName().length() > 32) {
				String nameLimited = selectedRowsVariables.getName().substring(0, 33);
				selectedRowsVariables.setName(nameLimited);
			}
		}*/
		Log.debug("CodeParser: " + selectedRowsVariables);
		return selectedRowsVariables;
	}

	private DataType checkDataType(String data) {
		if (data == null || data.isEmpty()) {
			return DataType.STRING;
		}

		// Compile and use regular expression
		RegExp regExpFile = RegExp.compile(FILE_PATTERN);
		if (regExpFile.test(data)) {
			return DataType.FILE;
		}

		RegExp regExpImage = RegExp.compile(IMAGE_PATTERN);
		if (regExpImage.test(data)) {
			return DataType.FILE;
		}

		/*
		 * RegExp regExpEnum1 = RegExp.compile(ENUM1_PATTERN); if
		 * (regExpEnum1.test(data)) { return DataType.ENUMERATED; }
		 * 
		 * RegExp regExpEnum2 = RegExp.compile(ENUM2_PATTERN); if
		 * (regExpEnum2.test(data)) { return DataType.ENUMERATED; }
		 */

		try {
			Integer.parseInt(data);
			return DataType.INTEGER;
		} catch (NumberFormatException e) {

		}

		try {
			Double.parseDouble(data);
			return DataType.DOUBLE;
		} catch (NumberFormatException e) {

		}

		return DataType.STRING;

	}

}
