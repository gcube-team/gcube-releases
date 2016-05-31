/**
 * 
 */
package org.cotrix.gcube.extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.cotrix.gcube.stubs.CopyUtils;


/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class DefaultHttpClient implements HttpClient {
	
	@Override
	public String get(URL url, String cookie) {
		try {

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Cookie", cookie);

			String response = readResponse(connection);

			return response;
		} catch (Exception e) {
			throw new RuntimeException("Get to url " + url + " with cookie " + cookie + " failed", e);
		}
	}
	
	@Override
	public String post(URL url, String cookie, String content) {
		try {

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Cookie", cookie);
			connection.setRequestProperty("Content-Type", "application/json" );
			connection.setRequestProperty("Content-Length", String.valueOf(content.length()));

			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(content.getBytes());

			String response = readResponse(connection);

			return response;
		} catch (Exception e) {
			throw new RuntimeException("Post to url " + url + " with cookie " + cookie + " failed", e);
		}
	}

	private String readResponse(HttpURLConnection connection) throws IOException {
		InputStream is = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter writer = new StringWriter();
		CopyUtils.copy(reader, writer);
		reader.close();
		is.close();
		return writer.toString();
	}



}
