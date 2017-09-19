package org.gcube.data.analysis.wps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gcube.data.analysis.wps.processes.Processes;
import org.n52.wps.commons.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelComputation extends HttpServlet {

	private final static Logger LOGGER = LoggerFactory.getLogger(CancelComputation.class);
	private static final long serialVersionUID = -268198171054599696L;
	// This is required for URL generation for response documents.
	public final static String SERVLET_PATH = "CancelComputationServlet";
	// in future parameterize
	//private final boolean indentXML = false;

	private final int uuid_length = 36;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public static String empty = "<wps:ExecuteResponse service=\"WPS\" " +
			"serviceInstance=\"\" statusLocation=\"\" version=\"1.0.0\" xml:lang=\"en-US\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsExecute_response.xsd\" " +
			"xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" " +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> " +
			"<wps:Process wps:processVersion=\"1.1.0\"/> " +
			"</wps:ExecuteResponse>";
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// id of result to retrieve.
		String id = request.getParameter("id");
		
		LOGGER.debug("CANCEL COMPUTATION -> RETRIEVING ID {}", id);
		if (StringUtils.isEmpty(id)) {
			errorResponse("id parameter missing", response);
		} else {
			LOGGER.debug("CANCEL COMPUTATION -> ID {} RETRIEVED ", id);
			if (!isIDValid(id)) {
				errorResponse("id parameter not valid", response);
			}
			LOGGER.debug("CANCEL COMPUTATION -> ID {} IS VALID ",id);
			/*IDatabase db = DatabaseFactory.getDatabase();
			long len = db.getContentLengthForStoreResponse(id);
			LOGGER.debug("CANCEL COMPUTATION -> INITIAL ID RESPONSE LENGTH " + len);
			
			try {
				LOGGER.debug("CANCEL COMPUTATION -> DELETING ID " + id);
				
				try {
//					String empty = "";
					InputStream stream = new ByteArrayInputStream(empty.getBytes("UTF-8"));
					db.updateResponse(id, stream);
					stream.close();
				} catch (Exception e) {
					LOGGER.error("error reading th einput stream",e);
				}
				LOGGER.debug("CANCEL COMPUTATION -> ID DELETED " + id);
				len = db.getContentLengthForStoreResponse(id);
				LOGGER.debug("CANCEL COMPUTATION -> ID RESPONSE LENGTH " + len);
			} catch (Exception e) {
				LOGGER.error("error in do get",e);
			} finally {
			}*/
			if (Processes.getRunningProcesses().containsKey(id)){
				boolean cancelled = Processes.getRunningProcesses().get(id).cancel();
				if(!cancelled)
					LOGGER.debug("CANCEL COMPUTATION -> process with id {} already cancelled ", id);
			} else LOGGER.debug("CANCEL COMPUTATION -> no running process with id {} (probably it already finished) ", id);
		}
		
	}

	protected void errorResponse(String error, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		PrintWriter writer = response.getWriter();
		writer.write("<html><title>Error</title><body>" + error + "</body></html>");
		writer.flush();
		LOGGER.warn("Error processing response: " + error);
	}

	protected void copyResponseStream(InputStream inputStream, OutputStream outputStream, String id, long contentLength) throws IOException {
		long contentWritten = 0;
		try {
			byte[] buffer = new byte[8192];
			int bufferRead;
			while ((bufferRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bufferRead);
				contentWritten += bufferRead;
			}
		} catch (IOException e) {
			String exceptionMessage = contentLength > -1 ? String.format("Error writing response to output stream for id %s, %d of %d bytes written", id, contentWritten, contentLength) : String.format("Error writing response to output stream for id %s, %d bytes written", id, contentWritten);
			throw new IOException(exceptionMessage, e);
		}
		LOGGER.info("{} bytes written in response to id {}", contentWritten, id);
	}

	protected void copyResponseAsXML(InputStream inputStream, OutputStream outputStream, boolean indent, String id) throws IOException {
		try {
			XMLUtil.copyXML(inputStream, outputStream, indent);
		} catch (IOException e) {
			throw new IOException("Error writing XML response for id " + id, e);
		}
	}

	public static Throwable getRootCause(Throwable t) {
		return t.getCause() == null ? t : getRootCause(t.getCause());
	}

	public boolean isIDValid(String id) {

		if (id.length() <= uuid_length) {

			try {
				UUID checkUUID = UUID.fromString(id);

				if (checkUUID.toString().equals(id)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

		} else {

			String uuidPartOne = id.substring(0, uuid_length);
			String uuidPartTwo = id.substring(id.length() - uuid_length, id.length());

			return isUUIDValid(uuidPartOne) && isUUIDValid(uuidPartTwo);
		}
	}

	public boolean isUUIDValid(String uuid) {

		// the following can be used to check whether the id is a valid UUID
		try {
			UUID checkUUID = UUID.fromString(uuid);

			if (checkUUID.toString().equals(uuid)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
