package org.gcube.portlets.admin.software_upload_wizard.server.util;

import java.lang.reflect.Field;

public class ReflectionStringer implements Stringer {

	@Override
	public String toString(Object o) {

		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(o.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		//Determine fields in object's class
		Field[] fields = o.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(o));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();

	}

}
