/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 30, 2019
 */
public class TestReadCSVFromURL {

	private static final String URL_TO_CVS_FILE = "http://data-d.d4science.org/Q2RZOW5MaXA2UEdFMW4wK2tYMHo3cjV1UTNFbEFGMWpHbWJQNStIS0N6Yz0-VLT";

	public static void main(String[] args) throws Exception {

		CSVFile theCSVFile = new PerformFishAnalyticsServiceImpl().readCSVFile(URL_TO_CVS_FILE);
		System.out.println(theCSVFile);
	}
}
