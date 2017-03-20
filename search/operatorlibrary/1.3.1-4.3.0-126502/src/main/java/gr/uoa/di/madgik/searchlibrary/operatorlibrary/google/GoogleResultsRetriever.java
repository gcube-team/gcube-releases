/**
 * 
 */
package gr.uoa.di.madgik.searchlibrary.operatorlibrary.google;


import gr.uoa.di.madgik.searchlibrary.operatorlibrary.google.util.EscapeCharacters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author paul
 * 
 */
public class GoogleResultsRetriever {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1 && args.length != 2)
			printUsageAndExit();

		if (args.length == 1)
			printResults(args[0]);
		else
			printResults(args[0], Integer.parseInt(args[1]));
	}

	private static void printResults(String query) throws Exception {
		printResults(query, 64);
	}

	private static void printResults(String query, int resNo) throws Exception {
		print(getResults(query, resNo));
	}
	
	protected static void print(GoogleResultElement[] array) {
		for(int i=0;i<array.length;i++) {
			System.out.println("====================== " + i + " ====================");
			System.out.println(array[i].getKey() + " : " + array[i].getPayload());
		}
	}
	
	protected static GoogleResultElement[] getResults(String query) throws Exception {
		return getResults(query, 10);
	}

	protected static GoogleResultElement[] getResults(String query, int resNo) throws Exception {
		List<GoogleResultElement> elmts = new LinkedList<GoogleResultElement>();
		String arg = EscapeCharacters.forURL(query);

		int cnt = 0;
		int pageCnt = 0;
		
		String urlStr = buildURL(arg, pageCnt);
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		df.setNamespaceAware(true);
		DocumentBuilder db = df.newDocumentBuilder();
		TransformerFactory tf = TransformerFactory.newInstance();

		do {

			String xmlPayload = null;
			try {
				xmlPayload = getResultsFromURL(urlStr);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ResultLimitExceededException e) {
				break;
			}
//			System.out.println(xmlPayload);
			pageCnt++;

			Document doc = null;
			//	doc = XMLUtils.newDocument(new InputSource(
			//			new StringReader(xmlPayload)));
			doc = db.parse(new InputSource(new StringReader(xmlPayload)));

			NodeList nl = doc.getElementsByTagName("results");

			for (int i = 0; i < nl.getLength() && cnt < resNo; i++, cnt++) {
				Element elmt = (Element) nl.item(i);
				StringWriter sw = new StringWriter();
				//XMLUtils.PrettyElementToWriter((Element)elmt, sw);
			//	XMLUtils.ElementToWriter(elmt, sw);
				Transformer tr = tf.newTransformer();
				tr.transform(new DOMSource((Element)elmt), new StreamResult(sw));
				
				GoogleResultElement ge = new GoogleResultElement();
				ge.setPayload(sw.toString().replaceAll("<results>", "").replaceAll("</results>", ""));
				ge.setKey(((Element)elmt.getElementsByTagName("url").item(0)).getFirstChild().getNodeValue());
				elmts.add(ge);
			}

			/*
			 * int indx1 = 0; int indx2 = 0; while(true) { cnt++; if(cnt >
			 * resNo) break; indx1 = xmlPayload.indexOf("<results>", indx1);
			 * if(indx1 == -1) break; indx2 = xmlPayload.indexOf("</results>",
			 * indx1);
			 * System.out.println("----------------------------------------");
			 * System.out.println(xmlPayload.substring(indx1 +
			 * "<results>".length(), indx2));
			 * System.out.println("----------------------------------------");
			 * indx1 = indx2; }
			 */
			if (cnt < resNo) {
				urlStr = buildURL(arg, pageCnt);
				//System.out.println("More results at: " + urlStr);
			}

		} while (cnt < resNo);
		return elmts.toArray(new GoogleResultElement[0]);
	}
	
	private static int countSubstrOccurs(String str, String substr) {
		return (str.length() - str.replaceAll(substr, "").length())
				/ substr.length();
	}

	private static String buildURL(String query, int pageNo) {
		return "http://ajax.googleapis.com/ajax/services/search/web?rsz=large&v=1.0&start="
				+ (pageNo * 8) + "&q=" + query;
	}

	private static String getResultsFromURL(String urlStr) throws IOException,
			JSONException, ResultLimitExceededException {

		URL url = new URL(urlStr);
		URLConnection connection = url.openConnection();
		/*
		 * connection.addRequestProperty("Referer",
		 * "http://www.mysite.com/index.html");
		 */

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONObject json = new JSONObject(builder.toString());
		if(json.getString("responseData").equals("null"))
			throw new ResultLimitExceededException();
		return XML.toString(json, "root");
	}

	private static void printUsageAndExit() {
		System.err
				.println("Wrong numberof arguments.\n"
						+ "Usage: java test.google.ajax.TestGoogleAjax <query> [<resultsNo> (default 10)]");
		System.exit(1);
	}

}
