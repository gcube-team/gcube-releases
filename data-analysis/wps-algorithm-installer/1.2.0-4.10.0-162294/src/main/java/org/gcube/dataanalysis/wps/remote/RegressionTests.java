package org.gcube.dataanalysis.wps.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.LoggerFactory;

public class RegressionTests {

	public static boolean checkHttpPage(String httplink, String page) {
		if (page.contains("ows:ExceptionText") || page.contains("Exception")) {
			System.out.println("Reading Link: " + httplink);
			System.out.println("ERROR:\n" + page);
			return false;
		}
		return true;
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	public static String prepareURL(String executionURL) throws Exception {

		String firstPart = executionURL.substring(0, executionURL.indexOf("DataInputs=") + 11);
		System.out.println("Execution URL:" + firstPart);
		String secondPart = URLEncoder.encode(executionURL.substring(executionURL.indexOf("DataInputs=") + 11), "UTF-8");
		System.out.println("Parameters: " + secondPart);
		executionURL = firstPart + secondPart;
		return executionURL;
	}

	public static void callHttps(String httpURLFile) throws Exception {
		//ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		//root.setLevel(ch.qos.logback.classic.Level.OFF);

		BufferedReader br = new BufferedReader(new FileReader(new File(httpURLFile)));
		String line = br.readLine();
		int counter = 0;
		Date d0 = new Date(System.currentTimeMillis());
		LinkedHashMap<String, String> executionTimes = new LinkedHashMap<String, String>();
		while (line != null) {
			boolean check = true;
			if (line.contains(" - ") && !line.startsWith("#")) {
				Date d00 = new Date(System.currentTimeMillis());
				String algorithmName = line.substring(line.indexOf("-") + 1).trim();
				String detailsURL = br.readLine();
				if (!detailsURL.startsWith("http://"))
					detailsURL = "http://" + detailsURL;
				System.out.println("************************************************************");
				System.out.println("TESTING ALGORITHM : " + algorithmName);
				System.out.println("************************************************************");

				String executionURL = br.readLine();
				String firstPart = executionURL.substring(0, executionURL.indexOf("DataInputs=") + 11);
				System.out.println("Execution URL:" + firstPart);
				String secondPart = URLEncoder.encode(executionURL.substring(executionURL.indexOf("DataInputs=") + 11), "UTF-8");
				System.out.println("Parameters: " + secondPart);
				executionURL = firstPart + secondPart;

				System.out.println("CHECKING DEFINITION " + algorithmName + " : " + detailsURL);
				String pageCheck = GetCapabilitiesChecker.readPageNoHttpClient(new URL(detailsURL));
				check = checkHttpPage(detailsURL, pageCheck);
				System.out.println("DEFINITION CHECK " + check);
				if (!check)
					break;
				System.out.println("EXECUTING " + algorithmName + " : " + executionURL);
				executionURL = executionURL.replace(".nc", "_test" + UUID.randomUUID() + ".nc");
				pageCheck = GetCapabilitiesChecker.readPageNoHttpClient(new URL(executionURL));
				System.out.println("EXECUTION RESULT " + pageCheck);
				check = checkHttpPage(executionURL, pageCheck);
				System.out.println("EXECUTION CHECK " + check);
				if (!check)
					break;
				Date d11 = new Date(System.currentTimeMillis());
				System.out.println("EXECUTION TIME " + algorithmName + " : " + getDateDiff(d00, d11, TimeUnit.MILLISECONDS) + " s");
				executionTimes.put(algorithmName, "" + getDateDiff(d00, d11, TimeUnit.MILLISECONDS));
				System.out.println("-------------------------------------------------------------\n");
				counter++;
			}
			if (!check) {
				System.out.println("EXECUTION FAILURE! - BREAK -");
				break;
			}
			line = br.readLine();

		}

		Date d1 = new Date(System.currentTimeMillis());
		System.out.println("CHECKED " + counter + " PAGES in " + getDateDiff(d0, d1, TimeUnit.MINUTES) + " minutes " + " (" + getDateDiff(d0, d1, TimeUnit.SECONDS) + " s)");
		System.out.println("EXECUTION TIMES SUMMARY:");
		for (String key : executionTimes.keySet()) {
			String time = executionTimes.get(key);
			System.out.println(key + "," + time + " s");
		}

		br.close();
	}

	@Test
	public void testDevNext() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-devNext.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testPreprod() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-pre.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testDevVRE() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-dev1.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProd1() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-prod1.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProd2() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-prod2.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProd3() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-prod3.txt";
		callHttps(algorithmsfile);
	}

	
	@Test
	public void testProd4() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-prod4.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProd5() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-prod5.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProd6() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-prod6.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProdGeneralProxy() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-proxy-general.txt";
		callHttps(algorithmsfile);
	}

	@Test
	public void testProdBigDataProxy() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-proxy-bigdata.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testEGI() throws Exception {
		String algorithmsfile = "tests/Test-dataminer-EGI.txt";
		callHttps(algorithmsfile);
	}


	
}
