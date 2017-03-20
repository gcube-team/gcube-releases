package org.gcube.dataanalysis.geo.test.projections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.DistanceCalculator;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;

public class GeolocateCountry {

	static String faoreport = "FAO data.csv";

	// static String faoreport = "C:\\Users\\coro\\Desktop\\allCountries.txt";

	public static void main1(String[] args) throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(new File(faoreport)));
		String line = fr.readLine();
		long counter = 0;
		while (line != null) {
			// System.out.println(line);
			String[] split = line.split("\t");
			String country = split[17];
			String x = split[5];
			String y = split[4];
			if (country.contains("Russia"))
				break;
			// else
			// System.out.println("Country:"+country+" "+x+","+y);

			counter++;
			if (counter % 500000 == 0)
				System.out.println("Country:" + country + " " + x + "," + y);
			line = fr.readLine();
		}

		fr.close();
	}

	public static void main2(String[] args) throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(new File(faoreport)));
		String line = fr.readLine();
		parseCentroidsFile();
		parseWorldCapitalsFile();
		line = fr.readLine();
		HashMap<String, String> yetDone = new HashMap<String, String>();
		while (line != null) {
			List<String> p = Transformations.parseCVSString(line, ",");
			String country = p.get(1);
			// TO DO rebuild the original CSV file
			String suggestion = yetDone.get(country);
			if (suggestion == null) {
				suggestion = getCentroid(country, capitals, 0.6);
				if (suggestion.length() == 0)
					suggestion = getCentroid(country, centroids, 0.3);

				yetDone.put(country, suggestion);
			}

			System.out.println(line + "," + suggestion);

			line = fr.readLine();
		}

		fr.close();
	}

	public static void main(String[] args) throws Exception {
		String file = "LargeTS.csv";
		System.out.println("Processing");
		List<String> countries = GeolocateCountry.geoLocateCountries(1, file);
		System.out.println("Dumping");
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("LargeTsGeo.csv")));
		for (String country:countries){
			bw.write(country+"\n");
		}
		bw.close();
		System.out.println("Done");
		}
	
	public static List<String> geoLocateCountries(int idxCountryColumn, String file) throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(new File(file)));
		String line = fr.readLine();
		parseCentroidsFile();
		parseWorldCapitalsFile();
		line = fr.readLine();
		List<String> yetDone = new ArrayList<String>();

		while (line != null) {
			List<String> p = Transformations.parseCVSString(line, ",");
			String country = p.get(idxCountryColumn);
			String suggestion = null;
			suggestion = getCentroid(country, capitals, 0.6);
			if (suggestion.length() == 0)
				suggestion = getCentroid(country, centroids, 0.3);
			if (suggestion==null || suggestion.length()==0)
				suggestion = ",,,,";
			String outstring = country + "," + suggestion;
			yetDone.add(outstring);
//			System.out.println(outstring);
			line = fr.readLine();
		}

		fr.close();
		return yetDone;
	}

	public static Map<String, String> geoLocateCountriesWithNoDuplicates(int idxCountryColumn, String file) throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(new File(file)));
		String line = fr.readLine();
		parseCentroidsFile();
		parseWorldCapitalsFile();
		line = fr.readLine();
		LinkedHashMap<String, String> yetDone = new LinkedHashMap<String, String>();

		while (line != null) {
			List<String> p = Transformations.parseCVSString(line, ",");
			String country = p.get(idxCountryColumn);
			String suggestion = yetDone.get(country);
			if (suggestion == null) {
				suggestion = getCentroid(country, capitals, 0.6);
				if (suggestion.length() == 0)
					suggestion = getCentroid(country, centroids, 0.3);

				yetDone.put(country, suggestion);
			}

			System.out.println(line + "," + suggestion);

			line = fr.readLine();
		}

		fr.close();
		return yetDone;
	}

	static HashMap<String, String> centroids = new HashMap<String, String>();
	static HashMap<String, String> capitals = new HashMap<String, String>();

	public static void parseCentroidsFile() throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(new File("countriescentroids.txt")));
		String line = fr.readLine();

		while (line != null) {
			String[] elems = line.split(",");
			String x = elems[0];
			String y = elems[1];
			String cntry_name = elems[2];
			centroids.put(cntry_name, x + "," + y);
			line = fr.readLine();
		}

		fr.close();
	}

	public static void parseWorldCapitalsFile() throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(new File("country-capitals.csv")));
		String line = fr.readLine();

		while (line != null) {
			String[] elems = line.split(",");
			String x = elems[3];
			String y = elems[2];
			String cntry_name = elems[0];
			capitals.put(cntry_name, x + "," + y);
			line = fr.readLine();
		}

		fr.close();
	}

	public static String getCentroid(String country, HashMap<String, String> centroids, double threshold) {

		String c = centroids.get(country);
		List<String> sb = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		DistanceCalculator dc = new DistanceCalculator();
		if (c == null) {
			for (String key : centroids.keySet()) {
				if (key.length() > 0) {
					/*
					 * if (key.contains(country) || country.contains(key)) { if (sb.length() > 0) sb.append("/");
					 * 
					 * sb.append(key + "," + centroids.get(key) + "("+0.8+")"+" "); } else {
					 */
					double score = dc.CD(false, country, key, true, false);
					if (score > threshold) {
						int i = 0;
						for (Double cscore : scores) {
							if (cscore < score)
								break;
							i++;
						}

						sb.add(i, key + "," + centroids.get(key) + "," + MathFunctions.roundDecimal(score, 2));
						scores.add(i, score);
					}

					// }
				}
			}
			if (sb.size() > 0)
				return sb.get(0).toString();
			else
				return "";
		} else
			return country + "," + c + "," + 1;
	}

}
