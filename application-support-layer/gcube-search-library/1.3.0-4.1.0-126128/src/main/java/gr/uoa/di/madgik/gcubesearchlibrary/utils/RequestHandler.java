package gr.uoa.di.madgik.gcubesearchlibrary.utils;

import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.BadRequestException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.InternalServerErrorException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.NotFoundException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.UnauthorizedException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class for making requests to a servlet
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RequestHandler {

	/**
	 * Submits a post request
	 * 
	 * @param targetURL
	 * @param urlParameters
	 * @return The servlet's response
	 * @throws UnauthorizedException 
	 * @throws NotFoundException  
	 * @throws IOException 
	 */
	public static String submitPostRequest (URL url, String urlParameters) throws UnauthorizedException, NotFoundException, IOException
	{
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		//conn.setReadTimeout(5000);

		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
		if (urlParameters != null)
			writer.write(urlParameters);
		writer.flush();
		writer.close();
		// Get the response
		StringBuffer answer = new StringBuffer();

		int responseCode = conn.getResponseCode();
		
		System.out.println("Response code is -> " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream in = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}
			System.out.println("RESPONSE -> " + answer.toString());
			reader.close();
			return answer.toString();
		}
		else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new UnauthorizedException("User is unauthorized");
		}
		else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
			throw new NotFoundException("Not found");
		}
		
		return null;
	}

	/**
	 * Submits a GET request
	 * 
	 * @param targetURL
	 * @param sessionID The HTTP Session ID
	 * @return The servlet's response
	 * @throws UnauthorizedException 
	 * @throws NotFoundException 
	 * @throws InternalServerErrorException 
	 * @throws BadRequestException 
	 */
	public static String submitGetRequest (String targetURL, String sessionID) throws UnauthorizedException, NotFoundException, InternalServerErrorException, BadRequestException
	{
		String result = null;
		try {
			URLEncoder.encode(targetURL, "UTF-8");
			URL url = new URL(targetURL);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Cookie", "JSESSIONID=" + URLEncoder.encode(sessionID, "UTF-8"));
			int responseCode = conn.getResponseCode();
			//System.out.println("Response code is -> " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				//Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				//System.out.println(sb.toString());
				rd.close();
				result = sb.toString();
				//System.out.println("RESPONSE --->>>" + result);
			}
			else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				throw new InternalServerErrorException();
			}
			else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new UnauthorizedException("User is unauthorized");
			}
			else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new NotFoundException("Not found");
			}
			else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				throw new BadRequestException("Bad Request");
			}
		} catch (UnsupportedEncodingException e) {

		} catch (IOException e) {

		}
		return result;
	}

	/**
	 * Submits a GET request
	 * 
	 * @param url The server's URL
	 * @param sessionID The HTTP Session ID
	 * @return The servlet's response
	 * @throws InternalServerErrorException
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws IOException 
	 */
	public static String submitGetRequest (URL url, String sessionID) throws InternalServerErrorException, UnauthorizedException, NotFoundException, BadRequestException, IOException
	{
		System.out.println("Request URL is -> " + url.toString());
		String result = null;
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		if (sessionID != null)
			conn.setRequestProperty("Cookie", "JSESSIONID=" + URLEncoder.encode(sessionID, "UTF-8"));
		int responseCode = conn.getResponseCode();
		System.out.println("Response code is -> " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) {
			//Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			System.out.println("RESPONSE --->>>" + result);
		}
		else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
			throw new InternalServerErrorException();
		}
		else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new UnauthorizedException("User is unauthorized");
		}
		else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
			throw new NotFoundException("Not found");
		}
		else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
			throw new BadRequestException("Bad Request");
		}

		return result;
	}

	public static ByteArrayOutputStream submitGetRequestByteStream(URL url, String sessionID)
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Cookie", "JSESSIONID=" + URLEncoder.encode(sessionID, "UTF-8"));

			//Get the response
			InputStream input = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {

		}
		return output;
	}
}
