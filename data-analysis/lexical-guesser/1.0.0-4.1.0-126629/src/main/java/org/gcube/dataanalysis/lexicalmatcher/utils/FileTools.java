package org.gcube.dataanalysis.lexicalmatcher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class FileTools {

	public static String readXMLDoc(String xmlFilePath) throws Exception {
		String xml = null;
		InputStream stream;
		try {
			File fl = new File(xmlFilePath);
			stream = new FileInputStream(fl);
		} catch (Exception e) {
			stream = ClassLoader.getSystemResourceAsStream(xmlFilePath);
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(stream);
		xml = document.asXML();
		stream.close();

		return xml;
	}

	public static void saveString2File(String filename, String string2save) throws Exception {

	}

	public static boolean checkInput(String filename) {
		File file = new File(filename);
		if (!file.exists())
			return false;
		if (!file.canRead())
			return false;
		else
			return true;
	}

	public static boolean checkOutput(String filename, boolean overwrite) {
		File file = new File(filename);
		if (!overwrite && file.exists())
			return false;
		if (file.exists() && (file.isDirectory() || !file.canWrite()))
			return false;
		else
			return true;
	}

	public static String loadString(String filename, String encoding) throws Exception {
		try {
			if (checkInput(filename)) {

				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encoding));
				String line = null;
				StringBuilder vud = new StringBuilder();

				while ((line = in.readLine()) != null) {
					vud.append(line + "\n");
				}
				in.close();
				return vud.toString();
			} else
				return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception("The file " + filename + " is not in the correct format!");
		} catch (IOException e) {
			throw new Exception("The file " + filename + " is not in the correct format!");
		}
	}

	public static void saveString(String filename, String s, boolean overwrite, String encoding) throws Exception {
		try {
			if (checkOutput(filename, overwrite)) {
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), encoding));
				out.write(s);
				out.close();
			}
		} catch (IOException e) {
			throw new Exception("The system can not write in " + filename + " because:\n" + e.getMessage());
		}
	}

}
