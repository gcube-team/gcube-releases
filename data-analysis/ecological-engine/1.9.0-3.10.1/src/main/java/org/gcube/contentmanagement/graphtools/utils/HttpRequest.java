package org.gcube.contentmanagement.graphtools.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

import com.google.gson.Gson;

public class HttpRequest {
	/**
	 * Sends an HTTP GET request to a url
	 * 
	 * @param endpoint
	 *            - The URL of the server. (Example: " http://www.yahoo.com/search")
	 * @param requestParameters
	 *            - all the request parameters (Example: "param1=val1&param2=val2"). Note: This method will add the question mark (?) to the request - DO NOT add it yourself
	 * @return - The response from the end point
	 */
	public static String sendGetRequest(String endpoint, String requestParameters) {
		String result = null;
		if (endpoint.startsWith("http://")) {
			// Send a GET request to the servlet
			try {
				// Send data
				String urlStr = endpoint;
				if (requestParameters != null && requestParameters.length() > 0) {
					urlStr += "?" + requestParameters;
				}
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(120000);
		        conn.setReadTimeout(120000);
		          
				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				result = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Reads data from the data reader and posts it to a server via POST request. data - The data you want to send endpoint - The server's address output - writes the server's response to output
	 * 
	 * @throws Exception
	 */
	public static void postData(Reader data, URL endpoint, Writer output) throws Exception {
		HttpURLConnection urlc = null;
		try {
			urlc = (HttpURLConnection) endpoint.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (ProtocolException e) {
				throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");

			OutputStream out = urlc.getOutputStream();

			try {
				Writer writer = new OutputStreamWriter(out, "UTF-8");
				pipe(data, writer);
				writer.close();
			} catch (IOException e) {
				throw new Exception("IOException while posting data", e);
			} finally {
				if (out != null)
					out.close();
			}

			InputStream in = urlc.getInputStream();
			try {
				Reader reader = new InputStreamReader(in);
				pipe(reader, output);
				reader.close();
			} catch (IOException e) {
				throw new Exception("IOException while reading response", e);
			} finally {
				if (in != null)
					in.close();
			}

		} catch (IOException e) {
			throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}
	}

	// performs a simple Get from a remote url
	public static Object getJSonData(String endpoint, String requestParameters, Type outputClass) throws Exception {
		String output = sendGetRequest(endpoint, requestParameters);
		Gson gson = new Gson();
		// AnalysisLogger.getLogger().debug("HttpRequest-> OUTPUT JSON:\n"+output.toString());
		// Output the response
		Object rebuiltJson = gson.fromJson(output.toString(), outputClass);
		return rebuiltJson;
	}

	// performs a simple transformation to a json object
	public static String toJSon(Object obj) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(obj);
		return jsonString;
	}

	public static Object postJSonData(String endpoint, Object obj, Type outputClass) throws Exception {

		HttpURLConnection urlc = null;
		try {

			// Send the request
			URL url = new URL(endpoint);
			urlc = (HttpURLConnection) url.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (ProtocolException e) {
				throw new Exception("Error in HttpURLConnection", e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-type", "application/json; charset=" + "UTF-8");
			OutputStreamWriter writer = null;
			Gson gson = new Gson();

			if (obj != null) {
				OutputStream out = urlc.getOutputStream();
				writer = new OutputStreamWriter(out);
				// write parameters
				String jsonString = gson.toJson(obj);
				AnalysisLogger.getLogger().trace("INPUT JSON:\n" + jsonString);
				writer.write(jsonString);
				writer.flush();
			}

			// Get the response
			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}

			if (obj != null)
				writer.close();

			reader.close();
			// AnalysisLogger.getLogger().debug("OUTPUT JSON:\n"+answer.toString());
			// Output the response
			Object rebuiltJson = gson.fromJson(answer.toString(), outputClass);
			return rebuiltJson;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Pipes everything from the reader to the writer via a buffer
	 */
	private static void pipe(Reader reader, Writer writer) throws IOException {
		char[] buf = new char[1024];
		int read = 0;
		while ((read = reader.read(buf)) >= 0) {
			writer.write(buf, 0, read);
		}
		writer.flush();
	}

	public static String sendPostRequest(String endpoint, String requestParameters) {

		// Build parameter string
		String data = requestParameters;
		try {

			// Send the request
			URL url = new URL(endpoint);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			// write parameters
			writer.write(data);
			writer.flush();

			// Get the response
			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}
			writer.close();
			reader.close();

			// Output the response
			return answer.toString();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String ManageCDATA(String phrase) {

		return phrase.replace("<![CDATA[", "").replace("]]>", "");

	}

	public static String AddCDATA(String phrase) {

		return "<![CDATA[" + phrase + "]]>";

	}

	public static int checkUrl(String url, final String username, final String password) {
		int checkConn = -1;
		try {
			if ((username != null) && (password != null)) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password.toCharArray());
					}

				});
			}

			URL checkurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) checkurl.openConnection();
			checkConn = conn.getResponseCode();
			conn.disconnect();
		} catch (Exception e) {
			System.out.println("ERROR in URL " + e.getMessage());
		}
		return checkConn;
	}

	public static void downloadFile(String fileurl, String localFile) throws Exception {
		URL smpFile = new URL(fileurl);
		URLConnection uc = (URLConnection) smpFile.openConnection();
		InputStream is = uc.getInputStream();
		AnalysisLogger.getLogger().debug("GenericWorker-> Retrieving from " + fileurl + " to :" + localFile);
		inputStreamToFile(is, localFile);
		is.close();
		is=null;
		System.gc();
	}

	public static void inputStreamToFile(InputStream is, String path) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(new File(path));
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}

	public static void main(String[] args) {

		String url = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/rest/layergroups/group4402c0cff-27e3-4606-a2f1-993ad37c3dfb.json";
		int d = checkUrl(url, "admin", "gcube@geo2010");
		System.out.println(d);
	}
}