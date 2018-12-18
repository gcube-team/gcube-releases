package org.gcube.vremanagement.executor.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class IOUtility {

	public static String readFile(String filePath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
	
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		reader.close();
		
		return stringBuilder.toString();
	}
	
}
