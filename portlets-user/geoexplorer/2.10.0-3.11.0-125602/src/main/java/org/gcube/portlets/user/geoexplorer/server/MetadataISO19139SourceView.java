package org.gcube.portlets.user.geoexplorer.server;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.util.TagHTML;


public class MetadataISO19139SourceView extends HttpServlet {
	
	/**
	 * 
	 */
	public static final String UTF_8 = "UTF-8";

	/**
	 * 
	 */
	protected static final String TEXT_XML = "text/xml; charset=UTF-8";

	private static final long serialVersionUID = -8381123618309936627L;
	
	protected static final String TEXT_HTML = "text/html";

	private static final String GCUBE_METADATA_SOURCE_ISO = "GCUBE Metadata ISO souce view";

	public static final String DOCTYPE = TagHTML.DOCTYPE;
	
	public static Logger logger = Logger.getLogger(MetadataISO19139SourceView.class);


	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		logger.trace("doGet request");
//		System.out.println("doGet request");
		retrieveMetadataSource(request, response);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		logger.trace("doPost request");
//		System.out.println("doPost request");
		retrieveMetadataSource(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException 
	 */
	private void retrieveMetadataSource(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	
		String metadataUUID = "";
		PrintWriter out = null;
		String scope = "";
		try {
			// get parameters
			String outputFormat = TEXT_XML;
			response.setCharacterEncoding("UTF-8");
			
			out =  response.getWriter();
			
			metadataUUID = request.getParameter(Constants.UUID);
			scope = request.getParameter(Constants.SCOPE);
			
			//IS VALID UUID?
			if(metadataUUID==null || metadataUUID.isEmpty()){
				outputFormat = TEXT_HTML;
				out.println(errorPage(GCUBE_METADATA_SOURCE_ISO+metadataUUID, Constants.MESSAGE_METADATA_UUID_NOT_FOUND));
			}else{		

				logger.trace("found UUID "+metadataUUID);
				response.setContentType(outputFormat); 
				try {
	
					String metaAsXml  = GeoExplorerServiceImpl.getRowMetadataItemByUUID(metadataUUID, request.getSession(), scope);

					logger.trace("metaAsXml returned is null?"+(metaAsXml==null));
					//DEBUG
//					logger.trace(metaAsXml);
					out.println(metaAsXml);
					
				} catch (Exception e) {
					throw new Exception("Error in layers csw loader ", e);
				}
			}

			out.close(); //CLOSE STREAM
			
		}catch (Exception e) {
			String error = "Sorry an error occurred when creating the metadata with uuid: "+metadataUUID;
			response.setContentType(TEXT_HTML); 
			
			if(out!=null)
				out.println(error);
			else
				throw new ServletException(error);	
			
			logger.error(error, e);
			
			out.close(); //CLOSE STREAM

		}
	}

	public String errorPage(String title, String message){
		String errorPage = "";
	    errorPage +=("<html>");
	    errorPage +=("<head>");
	    errorPage +=("<title>Error: "+title+"</title>");
	    errorPage +=("</head>");
	    errorPage +=("<body>");
	    errorPage +=(message);
	    errorPage +=("</body>");
	    errorPage +=("</html>");
	    return errorPage;
	}
	
}