package gr.cite.geoanalytics.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vfloros
 *
 */
public class Task implements Callable<RequestResult> {
	private Logger log = LoggerFactory.getLogger(Task.class);
	
	private final String taskUrl;
	private final HttpServletRequest taskRequest;
	private final HttpServletResponse taskResponse;
	private final String layerID;
	
	Task(String url, HttpServletRequest theRequest, HttpServletResponse theResponse, String layerID) {
		taskUrl = url;
		taskRequest = theRequest;
		taskResponse = theResponse;
		this.layerID = layerID;
	}
	
	public RequestResult resultReportStatus(String aURL, String layerID, HttpServletRequest request, HttpServletResponse response) throws MalformedURLException {
		RequestResult result = new RequestResult();
		result.setUrl(aURL);
		result.setLayerID(layerID);

		long start = System.currentTimeMillis();
		URL url = new URL(aURL);

		try {
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				connection.setRequestProperty(headerName, request.getHeader(headerName));
				//System.out.println("Request header: " + headerName + ":" + request.getHeader(headerName));
			}
			
			for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
				//System.out.println("Response header: " + header.getKey() + " : " + header.getValue());
				List<String> vals = new LinkedList<String>(header.getValue());
				if (header.getKey() != null && !header.getKey().equalsIgnoreCase("Transfer-Encoding")) 
					response.setHeader(header.getKey(), vals.get(0));
				vals.remove(0);
				for (String val : new HashSet<String>(vals))
					if (!header.getKey().equalsIgnoreCase("Transfer-Encoding"))
						response.addHeader(header.getKey(), val);
			}
			
			result.setSuccess(true);
			long end = System.currentTimeMillis();
			result.setTiming(end - start);
			result.setIs(connection.getInputStream());
		}
		catch(IOException ex){
			log.error("A connection error occurred");
		}

		return result;
	}

	@Override
	public RequestResult call() throws Exception {
		return resultReportStatus(taskUrl, layerID, taskRequest, taskResponse);
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public String getLayerID() {
		return layerID;
	}
}
