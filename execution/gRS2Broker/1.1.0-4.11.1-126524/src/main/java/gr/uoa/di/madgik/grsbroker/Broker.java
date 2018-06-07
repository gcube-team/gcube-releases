package gr.uoa.di.madgik.grsbroker;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.decorators.keepalive.KeepAliveReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.ObjectField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.URLField;
import gr.uoa.di.madgik.grs.test.SimplePumpable;
import gr.uoa.di.madgik.grsbroker.helpmanagers.FileManager;
import gr.uoa.di.madgik.grsbroker.helpmanagers.ReadersManager;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class Broker
 */

/*
 * example requests : http://localhost:8080/gRS2Broker/Broker?max=12&locator=http://localhost:35453?key=431b4202-e8d5-4ebe-97e5-84c3401b44d3&schema=HTTP
 * 					  http://localhost:8080/gRS2Broker/Broker?locator=http://localhost:34988?key=8f46a44a-d823-4d70-be50-66cabaecef10&schema=HTTP
 * example file request:
 * 					  http://localhost:8080/gRS2Broker/Broker?key=57c1f4d1-4a72-4868-a744-2ef77c685563&filename=57c1f4d1-4a72-4868-a744-2ef77c685563_6988503009891104147.servlet_tmp
 * */

public class Broker extends HttpServlet {
	private static Logger logger = Logger.getLogger(HttpServlet.class.getName());
	private static final long serialVersionUID = 1L;
	private static final int BUFSIZE = 4096;

	
	public Broker() {
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			logger.log(Level.FINE, "servlet got request");

			if (request.getParameter("locator") != null) {
				String locatorStr = request.getParameter("locator") + "#" + request.getParameter("schema");
				URI locator = null;
				locator = new URI(locatorStr);

				Integer max = -1;
				if (request.getParameter("max") != null)
					max = Integer.valueOf(request.getParameter("max"));

				logger.log(Level.FINE, "max read from request : " + max);

				String key = getQueryMap(request.getParameter("locator")).get("key");

				GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(response.getOutputStream()));
				response.setHeader("Content-Encoding", "gzip");

				long before = System.currentTimeMillis();
				try {
					
					logger.log(Level.FINE, "start reading from result set reader with key : " + key + " and locator : "	+ locator.toString());
					KeepAliveReader<GenericRecord> reader = ReadersManager.getReader(key, locator);
					
					if("json".equals(request.getParameter("format"))){
						writeRecsToJSON(reader, out, key, max);
					}
					else{
						
						int count = 0;
						
						out.write("<?xml version=\"1.0\"?>".getBytes());
						out.write("<Records>".getBytes());
						
						while (true) {
							if (count == max)
								break;
							if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
								break;
							GenericRecord rec = reader.get(60, TimeUnit.SECONDS);
	
							if (rec == null)
								continue;
							// rec.makeAvailable();
	//						out.write(getRecordHTML(rec, key).getBytes());
							out.write(getRecordXML(rec, key).getBytes());
							count += 1;
						}
						
						
						out.write("</Records>".getBytes());
						if (count == 0) {
							logger.log(Level.FINE, "closing the reader at result set reader with key : " + key + " and locator : " + locator.toString());
							ReadersManager.closeReader(key);
						}
					}
					
				} catch (Exception ex) {
					logger.log(Level.WARNING, "error reading records from result set",ex);
					ReadersManager.closeReader(key);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}

				logger.log(Level.FINE, "total : " + (System.currentTimeMillis() - before) + " ms");

				try {
					out.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "error closing response stream",e);
				}
			} else if (request.getParameter("filename") != null) {
				String filename = request.getParameter("filename");
				String key = request.getParameter("key");
				String path = FileManager.getFilePath(key, filename);
				String realFilename = FileManager.getFileRealName(key, filename);

				logger.log(Level.FINE, "read from request : " + filename + " and key : " + key);
				logger.log(Level.FINE, "found path : " + path + " and realFilename : " + realFilename);

				if (path == null) {
					logger.log(Level.SEVERE, "path of file : " + filename + " and key : " + key + "not found");
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.getOutputStream().flush();
					response.getOutputStream().close();
				} else
					doDownload(request, response, path, realFilename);

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error parsing the request", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	
	
	private void writeRecsToJSON (KeepAliveReader<GenericRecord> reader, GZIPOutputStream out, String key, long max) throws Exception {
		
		int count = 0;
		
		Map<Long,Properties> recs =  new LinkedHashMap<Long,Properties>(); 
		while (true) {
			Properties keyVal = new Properties();
			if (count == max)
				break;
			if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
				break;
			GenericRecord rec = reader.get(60, TimeUnit.SECONDS);
			if (rec == null)
				continue;
			
			for (Field f : rec.getFields()) {
				keyVal.put(f.getFieldDefinition().getName(), getFieldXML(key, f));
			}
			recs.put(rec.getID(), keyVal);
			count++;
		}
		
		Gson gson = new GsonBuilder().create();
		out.write(gson.toJson(recs).getBytes());
		
//		if (count == 0) {
//			logger.log(Level.FINE, "closing the reader at result set reader with key : " + key + " and locator : " + locator.toString());
//			ReadersManager.closeReader(key);
//		}
		
	}
	
	private String getRecordXML(GenericRecord rec, String key) throws Exception {
		StringBuffer xml = new StringBuffer();
		
		xml.append("<Record id=\"" + rec.getID() + "\">");
		xml.append("<Fields>");
		for (Field f : rec.getFields()) {
			xml.append("<Field>");
			xml.append("<Name>");
			xml.append(StringEscapeUtils.escapeXml(f.getFieldDefinition().getName()));
			xml.append("</Name>");
			xml.append("<Value>");
			xml.append(StringEscapeUtils.escapeXml(getFieldXML(key, f)));
			xml.append("</Value>");
			xml.append("</Field>");
		}
		xml.append("</Fields>");
		xml.append("</Record>");
		
		return xml.toString();
	}
	
	private String getFieldXML(String key, Field f) throws Exception {
		StringBuffer xml = new StringBuffer();

		if (f.getClass().equals(SimplePumpable.class)) {
			xml.append(((ObjectField) f).getPayload());
		} else if (f.getClass().equals(FileField.class)) {
			String filename = ((FileField) f).getOriginalPayload().getName();
			String fname = FileManager.putFileInMap(((FileField) f).getPayload(), filename, key);
			xml.append("./Broker?key=" + key + "&filename=" + fname + "&realname=" + filename);
		} else if (f.getClass().equals(ObjectField.class)) {
			xml.append(((ObjectField) f).getPayload());
		} else if (f.getClass().equals(StringField.class)) {
			xml.append(((StringField) f).getPayload().toString());
		} else if (f.getClass().equals(URLField.class)) {
			xml.append(((URLField) f).getPayload().toString());
		} else {
			throw new Exception("Field : " + f.getClass() + " not of known class");
		}

		return xml.toString();
	}
	
	private String getRecordHTML(GenericRecord rec, String key) throws Exception {
		StringBuffer html = new StringBuffer();

		html.append("<div>");

		html.append("<div>");
		html.append(rec.getID());
		html.append("</div>");

		for (Field f : rec.getFields()) {
			html.append("<div>");
			html.append(getFieldString(key, f));
			html.append("</div>");
		}
		html.append("</div>");

		return html.toString();
	}

	private String getFieldString(String key, Field f) throws Exception {
		StringBuffer html = new StringBuffer();

		if (f.getClass().equals(SimplePumpable.class)) {
			html.append("<div>");
			html.append(((ObjectField) f).getPayload());
			html.append("</div>");
		} else if (f.getClass().equals(FileField.class)) {
			String filename = ((FileField) f).getOriginalPayload().getName();

			String fname = FileManager.putFileInMap(((FileField) f).getPayload(), filename, key);

			html.append("<a href=\"");
			html.append("./Broker?key=" + key + "&filename=" + fname);
			html.append("\">");
			html.append(filename);
			html.append("</a>");
		} else if (f.getClass().equals(ObjectField.class)) {
			html.append("<div>");
			html.append(((ObjectField) f).getPayload());
			html.append("</div>");
		} else if (f.getClass().equals(StringField.class)) {
			html.append("<div>");
			html.append(((StringField) f).getPayload().toString());
			html.append("</div>");
		} else if (f.getClass().equals(URLField.class)) {
			html.append("<a href=\"");
			html.append(((URLField) f).getPayload().toString());
			html.append("\">");
			html.append(((URLField) f).getPayload().toString());
			html.append("</a>");

		} else {
			throw new Exception("Field : " + f.getClass() + " not of known class");
		}

		return html.toString();
	}

	private static Map<String, String> getQueryMap(String url) {
		String[] parts = url.split("\\?");
		if (parts.length == 0)
			return null;

		String[] params = parts[1].split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
	}

	private void doDownload(HttpServletRequest req, HttpServletResponse resp, String filename, String original_filename)
			throws IOException {
		File f = new File(filename);
		int length = 0;
		ServletOutputStream op = resp.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(filename);

		//
		// Set the response and go!
		//
		//
		resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		resp.setContentLength((int) f.length());
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\"");

		//
		// Stream to the requester.
		//
		byte[] bbuf = new byte[BUFSIZE];
		DataInputStream in = new DataInputStream(new FileInputStream(f));

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			op.write(bbuf, 0, length);
		}

		in.close();
		op.flush();
		op.close();
	}
}
