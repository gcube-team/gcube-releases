package org.gcube.data.access.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Logger logger;
	
	public ProxyServlet() {
	 super();
	 this.logger = LoggerFactory.getLogger(this.getClass());
	}
	

	

	private void forwardRequest (String finalAddress, HttpServletResponse servletResponse) throws Exception
	{

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(finalAddress);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		servletResponse.setStatus(response.getStatusLine().getStatusCode());
		Header [] headers = response.getAllHeaders();
		
		for (Header header : headers)
		{
			String headerName = header.getName();
			String headerValue = header.getValue();
			logger.debug("Header name "+headerName);
			logger.debug("Header value "+headerValue);
			//servletResponse.addHeader(headerName, headerValue);
	
		}
		
		
		HttpEntity responseEntity = response.getEntity();
		logger.debug("Entity found");
		InputStream in = responseEntity.getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer,"UTF-8");
		
	
		logger.debug("Printing data");
		servletResponse.setContentType(response.getFirstHeader("Content-Type").getValue());
	      PrintWriter out = servletResponse.getWriter();
	      out.println(writer.toString());
	      writer.close();
	      logger.debug("Data printed");
	      out.close();
//	  	OutputStream out = servletResponse.getOutputStream();
//		logger.debug("Copying message...");
//		IOUtils.copy(in, out);
//		logger.debug("Message copied");
//		in.close();
//		out.close();
//		logger.debug("Streams closed");
//		response.close();
//		httpClient.close();
//		logger.debug("Clients closed");

	}
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{

		logger.debug("Forwarding request to the final address");
		String path = req.getPathInfo();
		logger.debug("Path = "+path);
		
		if (path != null)
		{
			StringBuilder finalAddressBuilder = new StringBuilder(path);
			finalAddressBuilder.deleteCharAt(0);

			try
			{
				if (finalAddressBuilder.indexOf("http//") != 0) finalAddressBuilder.insert(0, "http://");
				
				setParameters(finalAddressBuilder, req.getParameterMap());
				String finalAddress = finalAddressBuilder.toString();
				logger.debug("Final address "+finalAddress);
				forwardRequest(finalAddress, resp);
			} catch (Exception e)
			{
				logger.error("Unable to serve the request",e);
				throw new ServletException(e);
			}
			
			
		}
		else
		{
			   // Set response content type
			resp.setContentType("text/html");

		      // Actual logic goes here.
		      PrintWriter out = resp.getWriter();
		      out.println("<h1>Please set the address</h1>");
		}

	}
	
	private void setParameters (StringBuilder finalAddressBuilder,Map<String, String[]> parameters)
	{
		
		if (parameters == null || parameters.isEmpty())
		{
			logger.debug("No parameters found");
		}
		else
		{
			logger.debug("Getting parameters");
			finalAddressBuilder.append("?");
			Iterator<String> paramsNames = parameters.keySet().iterator();
			
			while (paramsNames.hasNext())
			{
				String name = paramsNames.next();
				logger.debug("Parameter name = "+name);
				String[] values = parameters.get(name);
				
				if (values != null && values.length>0)
				{
					String value = values[0];
					logger.debug("Value = "+value);
					finalAddressBuilder.append(name).append("=");
					finalAddressBuilder.append(value).append("&");
				}
				else logger.debug("Invalid parameter");
				

			}
			
			finalAddressBuilder.deleteCharAt(finalAddressBuilder.length()-1);
		}
				
	}
	

	public static void main(String[] args) throws Exception{
		Logger logger = LoggerFactory.getLogger(ProxyServlet.class);
		
	//	ProxyAuthenticator authenticator = new ProxyAuthenticator();
		
		
//		if (authenticator.isActive())
//			{
//			logger.debug("Setting proxy");
//			Authenticator.setDefault(authenticator);
//			}
//		
		

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://www.eng.it");
		CloseableHttpResponse response = httpClient.execute(httpGet);
		System.out.println("Status = "+response.getStatusLine());

		Header [] headers = response.getAllHeaders();
		
		for (Header header : headers)
		{
			String headerName = header.getName();
			String headerValue = header.getValue();
			logger.debug("Header name "+headerName);
			logger.debug("Header value "+headerValue);
	
		}
		
		
		HttpEntity responseEntity = response.getEntity();
		logger.debug("Entity found");
		InputStream in = responseEntity.getContent();
		
		List<String> lines = IOUtils.readLines(in);
		
		for (String line : lines)
		{
			System.out.println(line);
		}
		
		
	}

}
