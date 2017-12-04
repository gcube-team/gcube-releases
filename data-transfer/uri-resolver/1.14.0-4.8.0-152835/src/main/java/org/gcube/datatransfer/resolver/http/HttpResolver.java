package org.gcube.datatransfer.resolver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;
import org.gcube.datatransfer.resolver.MultiReadHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class HttpResolver.
 *
 * @author Andrea Manzi(CERN)
 * updated by Francesco Mangiacrapa
 */
public class HttpResolver extends HttpServlet {

	protected static final String SMP_URI = "smp-uri";
	protected static final String VALIDATION = "validation";
	protected static final String CONTENT_TYPE = "contentType";
	protected static final String FILE_NAME = "fileName";
	protected static final String SMP_PATH_SEPARATOR = "/";

	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpResolver.class);

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig conf) throws ServletException {
		Handler.activateProtocol();
		super.init(conf);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String uri =null;
		String fileName =null;
		String contentType =null;
		boolean validatingURI = false;

//		logger.info("The http session id is: " + request.getSession().getId());

		uri = request.getParameter(SMP_URI);

		if (uri == null || uri.equals("")) {
			logger.warn("URI not found");
			response.sendError(404);
			return;
		}

		fileName  = request.getParameter(FILE_NAME);


		if (fileName == null || fileName.equals("")) {
			logger.warn("fileName not found");
			fileName = null;
		}

		contentType  = request.getParameter(CONTENT_TYPE);

		if (contentType == null || contentType.equals("")) {
			logger.warn("contentType not found");
			contentType = null;
		}

		String validation = request.getParameter(VALIDATION);
		validatingURI = Boolean.parseBoolean(validation);
		logger.info("validation? "+validatingURI);

		//we should not unescape the filename with spaces

		logger.debug("uri = "+uri);

		int index= uri.indexOf("?");

		if (index!= -1){
			logger.debug("Found char ?");
			String firsPart = uri.substring(0, index);

			//PATCH TO FIX #2695
			try {
				firsPart = validateItemName(firsPart, fileName);
				logger.trace("valid smp path is: "+firsPart);
			}
			catch (Exception e) {
				logger.warn("An error occurred during check right filename into SMP PATH");
			}

			String secondPart=  uri.substring( index+1);
			logger.debug("replacing in smp-uri char space with char + ...");
			//FIXED BY FRANCESCO M.
			secondPart = secondPart.replace(" ","+");//the char + is removed when the servlet is doing unescaping of the query parameters, we just put it back
			logger.debug("new secondPart: "+secondPart);
			uri= firsPart+"?"+secondPart;

		}else {
			logger.debug("Not found char ?");
			uri = uri.replace(" ","+");//the char + is removed when the servlet is doing unescaping of the query parameters, we just put it back
		}

		logger.info("URI = "+ uri);
		InputStream in = null;
		try {

			OutputStream out = response.getOutputStream();

			if (fileName != null){
				//The filename should be a quoted string. (According to Section 19.5.1 of RFC 2616)
				//http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html#sec19.5.1
				response.setHeader(ConstantsHttpResolver.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
//				response.addHeader("content-disposition", "attachment; filename=" +fileName);
			}else
				response.addHeader("content-disposition", "attachment; filename="+ConstantsHttpResolver.DEFAULT_FILENAME_FROM_STORAGE_MANAGER);

			if (contentType!= null)
				response.setContentType(contentType);
			else
				response.setContentType(ConstantsHttpResolver.DEFAULT_CONTENTTYPE_UNKNOWN_UNKNOWN);


			URL url = new URL(null, uri, new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL u) throws IOException {
					return new SMPURLConnection(u);
				}
			});

			URLConnection uc = null;

			try {
				uc = url.openConnection();
				in = uc.getInputStream();
			}
			catch(Exception e){
				response.sendError(404);
				logger.error("URLConnection Exception:", e);
				return;
			}

			//CASE InputStream NULL
			if(in==null){
				logger.warn("Input stream is null, sending status error 404");
				sendErrorQuietly(response, 404);
				return;
			}

			//CASE VALIDATION
			if(validatingURI){
				byte[] bytes = new byte[1]; //1B
				int c = in.read(bytes);
				logger.info(c+" byte read from InputStream");
				if(c>0){
					logger.info("at least 1 byte read, returning status 200");
					IOUtils.closeQuietly(in);
					response.setStatus(200);
					return;
				}
			}

			//CHANGED BY FRANCESCO M.
			try {

				IOUtils.copy(in, out);

			} catch (IOException e){
				logger.warn("IOException class name: "+e.getClass().getSimpleName());
				if (e.getClass().getSimpleName().equals("ClientAbortException"))
					logger.warn("Skipping ClientAbortException: "+e.getMessage());
				else
					throw e; //Sending Exceptions

			}	catch (NullPointerException e) {
				logger.warn("NullPointerException during copy, skipping printStrackTrace");
				sendErrorQuietly(response, 404);

			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}

		} catch (Exception e) {
			logger.error("Exception:", e);
			IOUtils.closeQuietly(in);
			sendErrorQuietly(response, 404);
			return;
		}

	}

	/**
	 * Validate item name.
	 * Since the right filename is in the URI (fileName=COL_Mammalia_taxa.taf.gz), if SMP path does't contain it the right file is overwritten into SMP PATH
	 *
	 * @param smpPath the smp path
	 * @param fileName the file name
	 * @return the string
	 * @throws Exception the exception
	 */
	protected static String validateItemName(String smpPath, String fileName) throws Exception{
		logger.debug("Checking right filename into SMP path..");

		if(smpPath==null)
			throw new Exception("Invalid smp path: "+smpPath);

		int lastSp = smpPath.lastIndexOf(SMP_PATH_SEPARATOR);
		if(lastSp<0)
			throw new Exception(SMP_PATH_SEPARATOR + " not found in "+smpPath);

		String smpItemName = smpPath.substring(lastSp+1, smpPath.length());

		if(smpItemName.compareTo(fileName)!=0){
			logger.info("SMP PATH contains a different filename, overrinding with "+fileName);
			return smpPath.substring(0, lastSp+1)+fileName;
		}

		logger.info("SMP PATH contains same filename, returning");
		return smpPath;

	}

	/**
	 * Send error quietly.
	 *
	 * @param response the response
	 * @param code the code
	 */
	protected void sendErrorQuietly(HttpServletResponse response, int code){

		if(response!=null){
			try {
				response.sendError(code);
				logger.info("Response sent error: "+code);
			} catch (IOException ioe) {
				 // ignore
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("doHead working..");

		String hpc = req.getParameter(ConstantsHttpResolver.HPC); //for HProxy check
		try{
			if(hpc==null ||  Boolean.parseBoolean(hpc)){
				logger.trace("returning status 200 for Hproxy check");
				resp.setStatus(200);
				return;
			}
		}catch (Exception e) {
			//silent exception to continue..
		}

		//ADDING PARAMETER TO PERFORM ONLY VALIDATION
		Map<String, String[]> additionalParams = new HashMap<String, String[]>();
		String[] value = new String[1];
		value[0] = "true";
		additionalParams.put(VALIDATION, value);
		MultiReadHttpServletRequest request = new MultiReadHttpServletRequest(req, additionalParams);
        doGet(request, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		doGet(request,response);
	}

//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args) {
//
//		String fileName = "COL_taxa.taf.gz";
//		String smpPath = "smp://Share/89971b8f-a993-4e7b-9a95-8d774cb68a99/Work+Packages/WP+6+-+Virtual+Research+Environments+Deployment+and+Operation/T6.2+Resources+and+Tools/COMET-Species-Matching-Engine/YASMEEN/1.2.0/Data/BiOnymTAF/COL_taxa.taf.gz";
//		try {
//			System.out.println(validateItemName(smpPath, fileName));
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}