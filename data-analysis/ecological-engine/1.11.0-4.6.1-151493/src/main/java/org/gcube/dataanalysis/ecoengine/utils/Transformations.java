package org.gcube.dataanalysis.ecoengine.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.ExampleTable;
import com.sun.org.apache.regexp.internal.RE;
import com.thoughtworks.xstream.XStream;

public class Transformations {

	public static ExampleSet matrix2ExampleSet(double[][] sampleVectors) {

		int m = sampleVectors.length;

		BigSamplesTable samples = new BigSamplesTable();

		for (int k = 0; k < m; k++)
			samples.addSampleRow("sample", sampleVectors[k]);

		return samples.generateExampleSet();

	}

	public static double[][] exampleSet2Matrix(ExampleSet set) {

		int m = set.size();
		ExampleTable table = set.getExampleTable();
		int n = table.getAttributeCount();
		double[][] matrix = new double[m][n - 1];
		for (int i = 0; i < m; i++) {
			DataRow row = table.getDataRow(i);
			for (int j = 0; j < n - 1; j++) {
				if (!table.getAttribute(j).isNominal()) {
					double d = row.get(table.getAttribute(j));
					matrix[i][j] = d;
				}
			}
		}

		return matrix;

	}

	// gets all the columns from a matrix
	public static double[][] traspose(double[][] matrix) {
		int m = matrix.length;
		if (m > 0) {
			int n = matrix[0].length;

			double columns[][] = new double[n][m];

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++)
					columns[i][j] = matrix[j][i];
			}

			return columns;
		} else
			return null;
	}

	// gets a column from a matrix
	public static double[] getColumn(int index, double[][] matrix) {
		int colulen = matrix.length;
		double column[] = new double[colulen];
		for (int i = 0; i < colulen; i++) {
			column[i] = matrix[i][index];
		}
		return column;
	}

	// gets a column from a matrix
	public static void substColumn(double[] column, int index, double[][] matrix) {

		for (int i = 0; i < matrix.length; i++) {
			matrix[i][index] = column[i];
		}

	}

	// merge matrixes: puts the rows of a matrix under another matrix
	public static double[][] mergeMatrixes(double[][] matrix1, double[][] matrix2) {

		if ((matrix1 == null) || (matrix1.length == 0))
			return matrix2;
		else if ((matrix2 == null) || (matrix2.length == 0))
			return matrix1;
		else {
			int len1 = matrix1.length;
			int len2 = matrix2.length;
			int superlen = len1 + len2;
			double[][] supermatrix = new double[superlen][];
			for (int i = 0; i < len1; i++) {
				supermatrix[i] = matrix1[i];
			}
			for (int i = len1; i < superlen; i++) {
				supermatrix[i] = matrix2[i - len1];
			}
			return supermatrix;
		}
	}

	public static String vector2String(double[] vector) {
		String out = "";
		for (int i = 0; i < vector.length; i++) {
			if (i > 0)
				out = out + "," + vector[i];
			else
				out = "" + vector[i];
		}

		return out;
	}

	public static Object getObjectFromFile(String file) throws Exception {

		/*
		 * FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis); return ois.readObject();
		 */
		XStream xstream = new XStream();
		return xstream.fromXML(new FileInputStream(file));
		// return xstream.fromXML(FileTools.loadString(file, "UTF-8"));
	}

	public static void dumpObjectToFile(String file, Object toWrite) throws Exception {

		XStream xstream = new XStream();
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(xstream.toXML(toWrite));
		bw.close();
		/*
		 * FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream ois = new ObjectOutputStream(fos); ois.writeObject(toWrite);
		 */
	}

	public static void dumpConfig(String pathToFile, AlgorithmConfiguration config) throws Exception {
		Transformations.dumpObjectToFile(pathToFile, config);
	}

	public static AlgorithmConfiguration restoreConfig(String configFile) throws Exception {
		FileInputStream fis = new FileInputStream(new File(configFile));
		AlgorithmConfiguration config = (AlgorithmConfiguration) new XStream().fromXML(fis);
		fis.close();
		return config;
	}

	public static double indexString(String string) {
		// string = Sha1.SHA1(string);
		StringBuffer sb = new StringBuffer();
		if ((string == null) || (string.length() == 0))
			return -1;

		int m = string.length();
		for (int i = 0; i < m; i++) {
			sb.append((int) string.charAt(i));
		}

		double d = Double.MAX_VALUE;
		try {
			d = Double.valueOf(sb.toString());
		} catch (Throwable e) {
		}

		if (d > Integer.MAX_VALUE)
			return (indexString(string.substring(0, 3)));

		return d;
	}

	public static void main(String[] args) throws Exception {
		String in = "h,t,,,,k,";
		in = "Salmo lucidus (Richardson 1836)\",\"Salmo lucidus\",\"Richardson 1836\",\"SIMPLE\",0.6547619047619048,\"ASFIS\",\"APL\",\"Plantae aquaticae\",\"\",\"\",\"PLANTAE AQUATICAE MISCELLANEA\",\"Aquatic plants nei\",\"Plantes aquatiques nca\",\"Plantas acuáticas nep\"";

		List<String> ll = Transformations.parseCVSString(in, ",");

		for (String l : ll) {
			System.out.println(l);
		}

		String s = "un'estate al mare";
		System.out.println("index: " + indexString(s));
		System.out.println("sha1: " + Sha1.SHA1(s));
		String s1 = "un'estate al mari";
		System.out.println("index: " + indexString(s1));
		System.out.println("sha1: " + Sha1.SHA1(s1));
		String s2 = "ciao amico mio come stai oggi? fa caldo o sono io che mi scotto?";
		System.out.println("index: " + indexString(s2));
		System.out.println("sha1: " + Sha1.SHA1(s2));
	}

	public static List<String> parseCVSString(String row, String delimiter) throws Exception {

		List<String> elements = new ArrayList<String>();
		String phrase = row;
		int idxdelim = -1;
		boolean quot = false;
		phrase = phrase.trim();
		while ((idxdelim = phrase.indexOf(delimiter)) >= 0) {
			quot = phrase.startsWith("\"");
			if (quot) {
				phrase = phrase.substring(1);
				String quoted = "";
				if (phrase.startsWith("\""))
					phrase = phrase.substring(1);
				else{
					RE regexp = new RE("[^\\\\]\"");
					boolean matching = regexp.match(phrase);

					if (matching) {
						int i0 = regexp.getParenStart(0);
						quoted = phrase.substring(0, i0 + 1).trim();
						phrase = phrase.substring(i0 + 2).trim();
					}
				}

				if (phrase.startsWith(delimiter))
					phrase = phrase.substring(1);

				elements.add(quoted);

			} else {
				elements.add(phrase.substring(0, idxdelim));
				phrase = phrase.substring(idxdelim + 1).trim();
			}
		}
		if (phrase.startsWith("\""))
			phrase = phrase.substring(1);

		if (phrase.endsWith("\""))
			phrase = phrase.substring(0, phrase.length() - 1);

		elements.add(phrase);

		return elements;
	}
}
