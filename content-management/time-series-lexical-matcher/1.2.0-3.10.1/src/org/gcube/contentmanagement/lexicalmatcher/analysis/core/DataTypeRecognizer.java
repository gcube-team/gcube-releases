package org.gcube.contentmanagement.lexicalmatcher.analysis.core;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DataTypeRecognizer {

	// if the DB type contains one of this, org.gcube.contentmanagement.lexicalmatcher will be classified as Decimal
	private static String[] decimalType = { "decimal", "integer", "int", "ordinal", "length", "position" };

	private static String[] booleanType = { "bool" };

	private static String[] stringType = { "varchar", "char", "string", "text" };

	public static String transformTypeFromDB(String DBType) {

		// check if the db type is yet known
		String type = null;

		try {
			// check if org.gcube.contentmanagement.lexicalmatcher is a char
			if (contains(DBType, stringType)) {
				type = String.class.getName();
			}
			// check if org.gcube.contentmanagement.lexicalmatcher is a decimal
			else if (contains(DBType, decimalType))
				type = BigDecimal.class.getName();
			// check if org.gcube.contentmanagement.lexicalmatcher is a boolean
			else if (contains(DBType, booleanType))
				type = Boolean.class.getName();
			else
				type = String.class.getName();
		} catch (Exception e) {
			type = String.class.getName();
		}
		return type;
	}

	// guesses the type of an object
	public static Object guessType(String entry) {

		Object type = null;

		// try to transform to a double
		try {
			double d = Double.parseDouble(entry);
			type = BigDecimal.valueOf(d);
		} catch (Exception eD) {
			// try to transform to a boolean
			if (entry.equalsIgnoreCase("true") || (entry.equalsIgnoreCase("false"))) {
				boolean b = Boolean.parseBoolean(entry);
				type = Boolean.valueOf(b);
			} else
				type = entry;
		}

		return type;

	}

	private static boolean contains(String element, String[] array) {
		element = element.toLowerCase();
		for (String arrayElem : array) {

			if (element.contains(arrayElem)) {
				return true;
			}
		}

		return false;
	}

	public static String guessType(ArrayList<String> elementlist) {

		// 0 = String 1 = Boolean 2 = Decimal
		int[] scores = new int[3];
		String[] types = { String.class.getName(), Boolean.class.getName(), BigDecimal.class.getName() };
		for (String element : elementlist) {
			Object guessedObj = guessType(element);
			if (guessedObj instanceof String) {
				scores[0] = scores[0] + 1;
			} else if (guessedObj instanceof Boolean) {
				scores[1] = scores[1] + 1;
			} else if (guessedObj instanceof BigDecimal) {
				scores[2] = scores[2] + 1;
			}

		}
		int max = -1;
		int maxindex = -1;
		for (int i = 0; i < scores.length; i++) {
			if (scores[i] > max) {
				max = scores[i];
				maxindex = i;
			}
		}

//		System.out.println("index " + maxindex + " max " + max);

		String type = types[maxindex];

		return type;
	}

	public static void main(String[] args) throws ClassNotFoundException {

		ArrayList<String> prova = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			prova.add("1234");
		}

		String classtype = guessType(prova);
		System.out.println(classtype);
		
	}

}
