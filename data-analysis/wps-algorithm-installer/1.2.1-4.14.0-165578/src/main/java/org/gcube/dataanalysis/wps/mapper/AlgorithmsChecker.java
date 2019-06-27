package org.gcube.dataanalysis.wps.mapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;

public class AlgorithmsChecker {

	public static String readPage(URL url) throws Exception {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 7 * 60000);
		HttpConnectionParams.setSoTimeout(params, 7 * 60000);
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setSoKeepalive(params, false);
		HttpGet request = null;
		try {
			request = new HttpGet(url.toURI());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		HttpResponse response = httpClient.execute(request);
		System.out.println("URL executed!");
		Reader reader = null;
		try {
			reader = new InputStreamReader(response.getEntity().getContent());
			System.out.println("Read input stream!");
			StringBuffer sb = new StringBuffer();
			{
				int read;
				char[] cbuf = new char[1024];
				while ((read = reader.read(cbuf)) != -1)
					sb.append(cbuf, 0, read);
			}

			EntityUtils.consume(response.getEntity());
			httpClient.getConnectionManager().shutdown();

			return sb.toString();

		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String httpgetpage(String url) throws Exception {
		String page = "";
		URL urlObj = new URL(url);
		/*
		 * HttpURLConnection connection = (HttpURLConnection) urlObj
		 * .openConnection(); System.out.println("Connection Opened"); String
		 * encoded =
		 * "Z2lhbmNhcmxvLnBhbmljaGk6ZjA2NjY1OTctNDMwMi00OWNlLWJlYTItNTU1Yjk0ZTU2OWNi"
		 * ; //connection.setRequestMethod("GET"); connection.setDoOutput(true);
		 */

		// connection.setConnectTimeout(60000);
		// connection.setReadTimeout(60000);
		// connection.setRequestProperty("Authorization", "Basic " + encoded);
		/*
		 * HttpURLConnection connection = (HttpURLConnection)
		 * urlObj.openConnection(); connection.setRequestMethod("GET");
		 * connection.setDoOutput(true); connection.setDoInput(true);
		 * connection.setInstanceFollowRedirects(true); connection.connect();
		 */
		urlObj.openConnection().getInputStream();
		// BufferedInputStream reader = new
		// BufferedInputStream(connection.getInputStream());

		// InputStream is = connection.getInputStream();
		// OutputStream os = connection.getOutputStream();

		System.out.println("input stream OK");
		return page;
	}

	public static void main2(String[] args) throws Exception {
		// String url =
		// "http://dataminer1-d-d4s.d4science.org:80/wps/WebProcessingService?Service=WPS&gcube-token=f0666597-4302-49ce-bea2-555b94e569cb&Request=DescribeProcess&identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.KNITR_COMPILER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESTIMATE_FISHING_ACTIVITY,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GRID_CWP_TO_COORDINATES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MOST_OBSERVED_TAXA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.MAPS_COMPARISON,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_INTERSECTOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_SUBTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.LOF,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.LWR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SAMPLEONTABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ZEXTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_MERGER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CSQUARES_TO_COORDINATES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATION_MEOW_AREA_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_MARINE_TERRESTRIAL,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_MAP_FROM_POINTS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.DISCREPANCY_ANALYSIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.BIONYM,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MOST_OBSERVED_SPECIES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.XMEANS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.KMEANS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ABSENCE_CELLS_FROM_AQUAMAPS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATIONS_TREND_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PRESENCE_CELLS_GENERATION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TAXONOMY_OBSERVATIONS_TREND_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HCAF,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATION_LME_AREA_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.DBSCAN,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.RASTER_DATA_PUBLISHER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_GEO_CHART,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GENERIC_CHARTS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GEO_CHART,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_SERIES_CHARTS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.HCAF_INTERPOLATION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.ICCAT_VPA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.HCAF_FILTER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.modellers.AQUAMAPSNN,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_DUPLICATES_DELETER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SGVM_INTERPOLATION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HSPEC,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTTABLES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SEADATANET_INTERPOLATOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ZEXTRACTION_TABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBINFO,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR_TABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_MAP_FROM_CSQUARES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ECOPATH_WITH_ECOSIM,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESTIMATE_MONTHLY_FISHING_EFFORT,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIMEEXTRACTION_TABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CSQUARE_COLUMN_CREATOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.POLYGONS_TO_MAP,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SUBMITQUERY,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBSCHEMA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MAX_ENT_NICHE_MODELLING,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GETTABLEDETAILS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATIONS_PER_AREA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIMEEXTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCE_ENRICHMENT,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.QUALITY_ANALYSIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SMARTSAMPLEONTABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.RANDOMSAMPLEONTABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_SERIES_ANALYSIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HSPEN,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.HRS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBNAMES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.POINTS_TO_MAP,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ABSENCE_GENERATION_FROM_OBIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESRI_GRID_EXTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.WEB_APP_PUBLISHER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CCAMLRTEST&version=1.0.0&";
		String url = "http://dataminer1-d-d4s.d4science.org:80/wps/WebProcessingService?Service=WPS&gcube-token=f0666597-4302-49ce-bea2-555b94e569cb&Request=DescribeProcess&identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.KNITR_COMPILER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESTIMATE_FISHING_ACTIVITY&version=1.0.0&";
		String page = httpgetpage(url);
		// String page = readPage(new URL(url));
		System.out.println(page);
		System.out.println("*******************");
	}

	public static void main1(String[] args) throws Exception {

		String page = readPage(new URL(
				"http://dataminer1-d-d4s.d4science.org:80/wps/WebProcessingService?Service=WPS&gcube-token=4ccc2c35-60c9-4c9b-9800-616538d5d48b&Request=DescribeProcess&identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.KNITR_COMPILER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESTIMATE_FISHING_ACTIVITY,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GRID_CWP_TO_COORDINATES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MOST_OBSERVED_TAXA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.MAPS_COMPARISON,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_INTERSECTOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_SUBTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.LOF,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.LWR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SAMPLEONTABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ZEXTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_MERGER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CSQUARES_TO_COORDINATES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATION_MEOW_AREA_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_MARINE_TERRESTRIAL,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_MAP_FROM_POINTS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.DISCREPANCY_ANALYSIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.BIONYM,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MOST_OBSERVED_SPECIES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.XMEANS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.KMEANS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ABSENCE_CELLS_FROM_AQUAMAPS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATIONS_TREND_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PRESENCE_CELLS_GENERATION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TAXONOMY_OBSERVATIONS_TREND_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HCAF,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATION_LME_AREA_PER_YEAR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.DBSCAN,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.RASTER_DATA_PUBLISHER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_GEO_CHART,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GENERIC_CHARTS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GEO_CHART,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_SERIES_CHARTS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.HCAF_INTERPOLATION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.HCAF_FILTER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.modellers.AQUAMAPSNN,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCES_DUPLICATES_DELETER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SGVM_INTERPOLATION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HSPEC,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTTABLES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SEADATANET_INTERPOLATOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ZEXTRACTION_TABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBINFO,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR_TABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_MAP_FROM_CSQUARES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ECOPATH_WITH_ECOSIM,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESTIMATE_MONTHLY_FISHING_EFFORT,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIMEEXTRACTION_TABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CSQUARE_COLUMN_CREATOR,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.POLYGONS_TO_MAP,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SUBMITQUERY,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBSCHEMA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MAX_ENT_NICHE_MODELLING,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GETTABLEDETAILS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SPECIES_OBSERVATIONS_PER_AREA,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIMEEXTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCE_ENRICHMENT,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.QUALITY_ANALYSIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SMARTSAMPLEONTABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.RANDOMSAMPLEONTABLE,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_SERIES_ANALYSIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HSPEN,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.HRS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBNAMES,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.POINTS_TO_MAP,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ABSENCE_GENERATION_FROM_OBIS,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ESRI_GRID_EXTRACTION,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.WEB_APP_PUBLISHER,org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CCAMLRTEST&version=1.0.0&"));
		System.out.println(page);
		System.out.println("*******************");
	}

	public class WebPageSaver {

		public void save(String page, String file) throws Exception {
			OutputStream out = new FileOutputStream(file);

			URL url = new URL(page);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();

			copy(is, out);
			is.close();
			out.close();
		}

		private void copy(InputStream from, OutputStream to) throws IOException {
			byte[] buffer = new byte[4096];
			while (true) {
				int numBytes = from.read(buffer);
				if (numBytes == -1) {
					break;
				}
				to.write(buffer, 0, numBytes);
			}
		}
	}

	public static void checkService(String hostname, String token) throws Exception {

		FileTools reader = new FileTools();
		System.out.println("Downloading capabilities");
		String getCapaURL = "http://" + hostname + "/wps/WebProcessingService?Request=GetCapabilities&Service=WPS&gcube-token=" + token;
		WebPageSaver saver = new AlgorithmsChecker().new WebPageSaver();
		String file = "WebProcessingService.xml";
		saver.save(getCapaURL, file);
		String xmlString = reader.loadString(file, "UTF-8");
		String wpsURL = "http://" + hostname + "/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0&gcube-token=" + URLEncoder.encode(token,"UTF-8") + "&Identifier=";
		String[] lines = xmlString.split("\n");
		// String tocall = wpsURL;
		StringBuffer sb = new StringBuffer();

		int j = 0;
		boolean fail = false;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains("ows:Identifier")) {
				String id = lines[i];
				id = id.substring(id.indexOf(">") + 1);
				id = id.substring(0, id.indexOf("<"));
				System.out.println("ID:" + id);
				/*
				 * if (j>0) tocall= tocall+","+id; else tocall= tocall+id;
				 */
				String tocall = wpsURL + URLEncoder.encode(id, "UTF-8");
				String page = readPage(new URL(tocall));

				System.out.println(page);
				String startAbs = "<ows:Abstract>";
				String stopAbs = "</ows:Abstract>";

				System.out.println("*******************");
				if (page.trim().length() == 0 || page.contains("ExceptionText")) {
					System.out.println("*******************STOP!!!");
					fail = true;
					break;
				}

				// sb.append(id.substring(id.lastIndexOf(".")+1)+"\n");
				String algo = id.substring(id.lastIndexOf(".") + 1);
				String IDWiki = "\n! colspan=2 bgcolor=lightgrey | <div id=\"" + algo + "\">" + algo + "</div>\n|-\n|| Description\n||";

				String Description = page.substring(page.indexOf(startAbs) + startAbs.length(), page.indexOf(stopAbs));
				sb.append(IDWiki);
				sb.append(Description + "\n");
				sb.append("|-\n");

				j++;
			}

		}
		/*
		 * System.out.println("To call: "+tocall); String page = readPage(new
		 * URL(tocall)); System.out.println(page);
		 */
		System.out.println("*******************");
		if (!fail)
			System.out.println(sb);
	}

	public static void main(String[] args) throws Exception {
		// checkService("dataminer1-d-d4s.d4science.org",
		// "4ccc2c35-60c9-4c9b-9800-616538d5d48b");
		checkService("dataminer1-devnext.d4science.org", "f9d49d76-cd60-48ed-9f8e-036bcc1fc045");
	}

	public static void main3(String[] args) throws Exception {

		FileTools reader = new FileTools();
		// String xmlString = reader.loadString("WebProcessingServicePre.xml",
		// "UTF-8");
		// String xmlString = reader.loadString("WebProcessingServiceDev.xml",
		// "UTF-8");
		String xmlString = reader.loadString("WebProcessingServiceNext.xml", "UTF-8");
		// String wpsURL =
		// "http://dataminer1-d-d4s.d4science.org/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0&gcube-token=4ccc2c35-60c9-4c9b-9800-616538d5d48b&Identifier=";
		// String wpsURL =
		// "http://dataminer1-pre.d4science.org/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0&gcube-token=0afbf5c8-b1ee-4e44-a6e7-0c235c8dc959&Identifier=";
		String wpsURL = "http://dataminer1-devnext.d4science.org/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0&gcube-token=f9d49d76-cd60-48ed-9f8e-036bcc1fc045&Identifier=";
		String[] lines = xmlString.split("\n");
		// String tocall = wpsURL;
		StringBuffer sb = new StringBuffer();

		int j = 0;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains("ows:Identifier")) {
				String id = lines[i];
				id = id.substring(id.indexOf(">") + 1);
				id = id.substring(0, id.indexOf("<"));
				System.out.println("ID:" + id);
				/*
				 * if (j>0) tocall= tocall+","+id; else tocall= tocall+id;
				 */
				String tocall = wpsURL + id;
				String page = readPage(new URL(tocall));

				System.out.println(page);
				String startAbs = "<ows:Abstract>";
				String stopAbs = "</ows:Abstract>";

				System.out.println("*******************");
				if (page.trim().length() == 0 || page.contains("JAVA_StackTrace")) {
					System.out.println("*******************STOP!!!");
					break;
				}

				// sb.append(id.substring(id.lastIndexOf(".")+1)+"\n");
				String algo = id.substring(id.lastIndexOf(".") + 1);
				String IDWiki = "\n! colspan=2 bgcolor=lightgrey | <div id=\"" + algo + "\">" + algo + "</div>\n|-\n|| Description\n||";

				String Description = page.substring(page.indexOf(startAbs) + startAbs.length(), page.indexOf(stopAbs));
				sb.append(IDWiki);
				sb.append(Description + "\n");
				sb.append("|-\n");

				j++;
			}

		}
		/*
		 * System.out.println("To call: "+tocall); String page = readPage(new
		 * URL(tocall)); System.out.println(page);
		 */
		System.out.println("*******************");

		System.out.println(sb);
	}

}
