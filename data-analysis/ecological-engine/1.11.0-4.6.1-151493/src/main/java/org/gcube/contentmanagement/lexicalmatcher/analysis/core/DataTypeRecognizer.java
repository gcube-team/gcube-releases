package org.gcube.contentmanagement.lexicalmatcher.analysis.core;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DataTypeRecognizer {

	// if the DB type contains one of this, org.gcube.contentmanagement.lexicalmatcher will be classified as Decimal
	private static String[] decimalType = { "decimal", "integer", "int", "ordinal", "length", "position" ,"real"};

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
		return guessType(elementlist,true);
	}
	
	public static String guessType(ArrayList<String> elementlist, boolean restricted) {
		
		
		// 0 = String 1 = Boolean 2 = Decimal
		int[] scores = new int[3];
		String[] types = { String.class.getName(), Boolean.class.getName(), BigDecimal.class.getName() };
		
		if (elementlist==null || elementlist.size()==0)
			return types[0];
					
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
		
		//restricted mode
		if (restricted){
			if (scores[0]>0)
				maxindex=0;
			else if ((scores[1]>0)&&(scores[2]==0))
				maxindex=1;
			else if ((scores[1]==0)&&(scores[2]>0))
				maxindex=2;
			else 
				maxindex=0;
		}
//		System.out.println("index " + maxindex + " max " + max);

		String type = types[maxindex];

		return type;
	}

	public static void main(String[] args) throws ClassNotFoundException {

		ArrayList<String> prova = new ArrayList<String>();
		/*
		String[] elementsToGuess = 
		{"65952",   "51809",   "RUSI 600",   "RUSI 9981",   "USNM 00163126",   "MNHN 1989-0806",   "RUSI 57071",   "BMNH 1939.7.4.1-3",   
				"22082",   "8863",   "65410",   "76194",   "76196"};
		*/
		String[] elementsToGuess = {"65952",   "51809",  "22082",   "8863",   "65410",   "76194",   "76196"};
		
		for (int i = 0; i < elementsToGuess.length; i++) {
			prova.add(elementsToGuess[i]);
		}
		
		
		String classtype = guessType(prova);
		System.out.println(classtype);
		
	}

}
