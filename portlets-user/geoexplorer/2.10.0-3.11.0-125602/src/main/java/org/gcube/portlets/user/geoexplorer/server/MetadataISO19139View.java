package org.gcube.portlets.user.geoexplorer.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.dumper.ObjectDumper;
import org.gcube.portlets.user.geoexplorer.server.util.TagHTML;
import org.gcube.portlets.user.geoexplorer.server.util.UrlEncoderUtil;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.citation.CitationItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.identification.DataIdentificationItem;


public class MetadataISO19139View extends HttpServlet {
	
	private static final String GCUBE_METADATA_ISO = "GCUBE Metadata ISO ";

	private static final long serialVersionUID = -7330998232693921348L;
	
	public static final String TEXT_HTML = "text/html";
	
	public static final String DOCTYPE = TagHTML.DOCTYPE;
	
	public static Logger logger = Logger.getLogger(MetadataISO19139View.class);
	
	public static String headWithTitle(String title) {
		return (DOCTYPE + "\n" + TagHTML.HTML+"\n<link type=\"text/css\" rel=\"stylesheet\" href=\"dumpmetadata.css\">" + TagHTML.HEAD+TagHTML.TITLE + title + TagHTML.TITLECLOSE+TagHTML.HEADCLOSE+"\n");
	}
	
	/**
	 * 
	 * @param title
	 * @param pathContext
	 * @param layer
	 * @return
	 */
	public static String headWithJsLayerItemPreview(String title, String pathContext, LayerItem layer) {
//		return (DOCTYPE + "\n" + TagHTML.HTML+"\n<link type=\"text/css\" rel=\"stylesheet\" href=\"dumpmetadata.css\">" + 
//	
//				TagHTML.HEAD+
//				"<script type=\"text/javascript\">"+getJsPreviewMap(pathContext, layer)+"</script>"+
//				TagHTML.TITLE + title + TagHTML.TITLECLOSE+TagHTML.HEADCLOSE+"\n");
			
		return (DOCTYPE + "\n" + TagHTML.HTML+"\n<link type=\"text/css\" rel=\"stylesheet\" href=\"dumpmetadata.css\">" + 
		
				"<script type=\"text/javascript\" src=\"//code.jquery.com/jquery-1.11.0.min.js\"></script>" +
				"<script type=\"text/javascript\"> " +
					"function loadPreview(){" +
					" var dataString ={"+Constants.WMS_REQUEST_PARAMETER+":'"+ layer.getWMSRequest()+"'}; "+
						"$.ajax({" +
						"type: \"POST\"," +
						"url: \"MapPreviewGenerator\"," +
						"data: dataString, "+
								"success: function(data) {" +
								"$('#layerLoading').remove(); "+
								"$('#centercontainpreview').append(data); "+
								"}" +
								"});" +
					"} "+
				"</script>"
				+TagHTML.TITLE + title + TagHTML.TITLECLOSE+TagHTML.HEADCLOSE+"\n");
	}
	
	/**
	 * 
	 * @param path
	 * @param layerItem
	 * @return
	 */
	public static String getJsPreviewMap(String path, LayerItem layerItem) {

		return path + Constants.MAP_PREVIEW_GENERATOR+"?" + 
				Constants.WMS_REQUEST_PARAMETER+"="+UrlEncoderUtil.encodeQuery(layerItem.getWMSRequest());
		
//		String call = path+ "MapPreviewGenerator?"
//				+ Const + layerItem.getWmsServiceUrl()
//				+ "&layer=" + layerItem.getLayer();
//		 return "function loadPreview() { document.location.href='"+call+"'; }";
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		String metadataUUID = "";
		PrintWriter out = null;
		String scope = "";

//		System.out.println("servlet context "+ request.getContextPath());
//		System.out.println("http session id"+ request.getSession().getId());

		
		try {
			// get parameters
			String outputFormat = TEXT_HTML;
			response.setContentType(outputFormat);
			response.setCharacterEncoding("UTF-8");

			
			out =  response.getWriter();
//			dumpFileStream = new FileInputStream(resource.getPath());
			
			boolean isHeadHtml = true;
			boolean isBodyHtml = true;
			boolean isLoadPreview = true;

			metadataUUID = request.getParameter(Constants.UUID);
			
			//IS VALID UUID?
			if(metadataUUID==null || metadataUUID.isEmpty()){
				out.println(errorPage(GCUBE_METADATA_ISO+metadataUUID, "The metadata universally unique identifier (UUID) is null or empty. Re-call this page with a valid UUID"));
			}else{		
				String getHeadHtml = request.getParameter(Constants.GETHEADHTML);
				String getBodyHtml = request.getParameter(Constants.GETBODYHTML);
				String loadPreview = request.getParameter(Constants.LOADPREVIEW);
				scope = request.getParameter(Constants.SCOPE);

				  //Do the String to boolean conversion
				if(getHeadHtml!=null && !getHeadHtml.isEmpty())
					isHeadHtml = Boolean.parseBoolean(getHeadHtml);
				
				  //Do the String to boolean conversion
				if(getBodyHtml!=null && !getBodyHtml.isEmpty())
					isBodyHtml = Boolean.parseBoolean(getBodyHtml);
				
				
				
				if(loadPreview!=null && !loadPreview.isEmpty()){
					isLoadPreview = Boolean.parseBoolean(loadPreview);

				}
				
				LayerItem layerItem = null;
				if(isLoadPreview){
//					GeonetworkInstance gn = GeoExplorerServiceImpl.getGeonetworkInstanceFromSession(request.getSession());
//					logger.trace("-- GeonetworkInstance OK");
//					layerItem = MetadataConverter.getLayerItemFromMetadataUUID(gn.getGeonetworkReader(), metadataUUID);
					layerItem = GeoExplorerServiceImpl.getLayerItemByUUID(metadataUUID, request.getSession(), scope);
				}
				
				logger.trace("found UUID "+metadataUUID);
				logger.trace("return HEAD tag "+isHeadHtml);
				logger.trace("return BODY tag "+isBodyHtml);

				
			    if(isHeadHtml){
			    	
			    	if(!isLoadPreview)
			    		out.println(headWithTitle(GCUBE_METADATA_ISO+metadataUUID));
			    	else{
			    		
//			    		System.out.println("layer is "+layerItem);
			    		
			    		out.println(headWithJsLayerItemPreview(GCUBE_METADATA_ISO+metadataUUID, request.getContextPath(), layerItem));
			    	}
			    }
			    
				try {
	
					MetadataItem meta = GeoExplorerServiceImpl.getMetadataItemByUUID(metadataUUID, request.getSession(), scope);
					
//					MetadataItem meta  = getMetadataItemByUUID(metadataUUID, request.getSession());
					
	//				logger.trace(meta);
					out.println(getBody(meta, isBodyHtml, isLoadPreview));
					
					if(isHeadHtml)
						out.println(TagHTML.HTMLCLOSE);
	
				} catch (Exception e) {
//					e.printStackTrace();
					out.close(); //CLOSE STREAM
					logger.error("Error in do get metadata iso view: ", e);
					throw new Exception("Error in layers csw loader ", e);
				}
			
			}

			out.close(); //CLOSE STREAM

		}catch (Exception e) {
			String error = "Sorry an error occurred when creating the metadata with uuid: "+metadataUUID;
			
			if(out!=null)
				out.println(error);
			else
				throw new ServletException(error);	

			logger.error(error, e);
			out.close(); //CLOSE STREAM
		}
	}
	
	public static ObjectDumper getDumper(){
		Writer writer = new StringWriter();
		return new ObjectDumper(writer);
	}
	
	public static ObjectDumper getNewWriter(ObjectDumper dumper){
		Writer writer = new StringWriter();
		dumper.setWriter(writer);
		return dumper;
	}
	
	
	public String getBody(MetadataItem meta, boolean includeTagBody, boolean loadPreview) throws FileNotFoundException, IOException, TransformerException, ParserConfigurationException {
		
		String htmlBody = "";
		
		if(includeTagBody){
			
			if(!loadPreview)
				htmlBody = TagHTML.BODY;
			else
				htmlBody = "<body onLoad = loadPreview()>";
	
		}
//		htmlBody += meta.toString();
		
		//Before dumping
		Writer writer = new StringWriter();
		ObjectDumper dumper = new ObjectDumper(writer);
//		dumper.loadConfig(dumpFileStream);
//		dumper.loadConfig(DUMP_PROPERTIES);
		dumper.setWriter(writer);
		
		
		//INSERT Metadata Identification
		htmlBody+=TagHTML.HI(2);
		String metadataIdentification = "Metadata Identification: ";
		String metadataTitle="";
		
	
		
		if(meta.getIdentificationInfo()!=null){
			DataIdentificationItem dataId = meta.getIdentificationInfo();
			
			if(dataId.getCitation()!=null){
				CitationItem citation = dataId.getCitation();
				metadataTitle = citation.getTitle();
			}
			//INSERT METADATA TITLE
			metadataIdentification+=metadataTitle;
			htmlBody+=metadataIdentification;
			htmlBody+=TagHTML.HIClose(2);
			htmlBody+=TagHTML.HR; 
//			+ TagHTML.BR;
			
			
			//INSERT ABSTRACT
			htmlBody+=TagHTML.DIV 
					+ TagHTML.HI(3)+"Abstract:"+TagHTML.HIClose(3)
//					+ TagHTML.BR
					+ TagHTML.HI(4)+dataId.getAbstracts()+TagHTML.HIClose(4)
					+ TagHTML.DIVCLOSE;
			
//			htmlBody+= TagHTML.BR;
			
			if(loadPreview){
				
				htmlBody+=TagHTML.DivWithIdAndStyle("containerpreview", "containerpreview");
				htmlBody+=TagHTML.DivWithIdAndStyle("centercontainpreview", "centercontainpreview");

//				InputStream imagePreload = MetadataISO19139View.class.getResourceAsStream(Constants.PRELOAD_LAYER);
//				BufferedImage imgTrueMarble = ImageIO.read(imagePreload);
				htmlBody+="<img id=\"layerLoading\" src = "+Constants.PRELOAD_LAYER+"></img><h5>Layer Preview</h5>";
				
				htmlBody+=TagHTML.IMGCLOSE;
				htmlBody+=TagHTML.DIVCLOSE;
				htmlBody+=TagHTML.DIVCLOSE;
			}
			
//			htmlBody+= TagHTML.BR;
			
			//CHARSET
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(dataId.getCharacterSet());
			htmlBody+=	TagHTML.DIV 
					+	TagHTML.HI(3)+"Charset:"+TagHTML.HIClose(3)
//					+	TagHTML.BR
					+	writer.toString()
					+	TagHTML.DIVCLOSE;
			
//			htmlBody+= TagHTML.BR;
			
			
			//Credits
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(dataId.getCredits());
			htmlBody+=	TagHTML.DIV 
					+	TagHTML.HI(3)+"Credits:"+TagHTML.HIClose(3)
//					+	TagHTML.BR
					+	writer.toString()
					+	TagHTML.DIVCLOSE;
			
//			htmlBody+= TagHTML.BR;
			
			//Language
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(dataId.getLanguage());
			htmlBody+=	TagHTML.DIV 
					+	TagHTML.HI(3)+"Language:"+TagHTML.HIClose(3)
//					+	TagHTML.BR
					+	writer.toString()
					+	TagHTML.DIVCLOSE;
			
//			htmlBody+= TagHTML.BR;
//			
			
			//Language
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(dataId.getPurpose());
			htmlBody+=	TagHTML.DIV 
					+	TagHTML.HI(3)+"Purpose:"+TagHTML.HIClose(3)
//					+	TagHTML.BR
					+	writer.toString()
					+	TagHTML.DIVCLOSE;
			

//			htmlBody+= TagHTML.BR;
			
			//Topic Category
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(dataId.getTopicCategory());
			htmlBody+=	TagHTML.DIV 
					+	TagHTML.HI(3)+"Topic Category:"+TagHTML.HIClose(3)
//					+	TagHTML.BR
					+	writer.toString()
					+	TagHTML.DIVCLOSE;
			

//			htmlBody+= TagHTML.BR;
			
			//Supplemental Information
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(dataId.getSupplementalInformation());
			htmlBody+=	TagHTML.DIV 
					+	TagHTML.HI(3)+"Supplemental Information:"+TagHTML.HIClose(3)
//					+	TagHTML.BR
					+	writer.toString()
					+	TagHTML.DIVCLOSE;
			
	
//			htmlBody+= TagHTML.BR;
//			htmlBody+= TagHTML.BR;


			//CITATION
			if(dataId.getCitation()!=null){
				dumper = getNewWriter(dumper);
				writer = dumper.writeObjectDump(dataId.getCitation());
				htmlBody+= TagHTML.DIV
				+	TagHTML.HI(3)+"Citation Information:"+TagHTML.HIClose(3)
				+	TagHTML.HR
				+	writer.toString()
				+	TagHTML.DIVCLOSE;
			}
			
			//DescriptiveKeywords
			if(dataId.getDescriptiveKeywords()!=null){
				dumper = getNewWriter(dumper);
				writer = dumper.writeObjectDump(dataId.getDescriptiveKeywords());
				htmlBody+= TagHTML.DIV
				+	TagHTML.HI(3)+"Descriptive Information:"+TagHTML.HIClose(3)
				+	TagHTML.HR
				+	writer.toString()
				+	TagHTML.DIVCLOSE;
			}
			
			//Point Of Contacts
			if(dataId.getPointOfContacts()!=null){
				dumper = getNewWriter(dumper);
				writer = dumper.writeObjectDump(dataId.getPointOfContacts());
				htmlBody+= TagHTML.DIV
				+	TagHTML.HI(3)+"Point Of Contacts:"+TagHTML.HIClose(3)
				+	TagHTML.HR
				+	writer.toString()
				+	TagHTML.DIVCLOSE;
			}
			
			//Extent
			if(dataId.getExtent()!=null){
				dumper = getNewWriter(dumper);
				writer = dumper.writeObjectDump(dataId.getExtent());
				htmlBody+= TagHTML.DIV
				+	TagHTML.HI(3)+"Extent:"+TagHTML.HIClose(3)
				+	TagHTML.HR
				+	writer.toString()
				+	TagHTML.DIVCLOSE;
			}

		}
		else{
			htmlBody+=metadataIdentification+"not found";
			htmlBody+=TagHTML.HIClose(2);
		}
		
		
		//RESPONSABILITY PARTY
		if(meta.getContacts()!=null){
			
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(meta.getContacts());
			htmlBody+= TagHTML.DIV
			+	TagHTML.HI(3)+"Responsability Party:"+TagHTML.HIClose(3)
			+	TagHTML.HR
			+	writer.toString()
			+	TagHTML.DIVCLOSE;
			
		}
		
		//ReferenceSystems
//		if(meta.getReferenceSystems()!=null){
//			
//			dumper = getNewWriter(dumper);
//			writer = dumper.writeObjectDump(meta.getReferenceSystems());
//			htmlBody+= TagHTML.DIV
//			+	TagHTML.HI(3)+"Reference Systems:"+TagHTML.HIClose(3)
//			+	TagHTML.HR
//			+	writer.toString()
//			+	TagHTML.DIVCLOSE;
//		}
		
//		//SpatialRepresentation
//		if(meta.getSpatialRepresentation()!=null){
//			
//			dumper = getNewWriter(dumper);
//			writer = dumper.writeObjectDump(meta.getSpatialRepresentation());
//			htmlBody+= TagHTML.DIV
//			+	TagHTML.HI(3)+"Spatial Representations:"+TagHTML.HIClose(3)
//			+	TagHTML.HR
//			+	writer.toString()
//			+	TagHTML.DIVCLOSE;
//		}
//		
		//ExtensionsInfo
		if(meta.getExtensionsInfo()!=null){
			
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(meta.getExtensionsInfo());
			htmlBody+= TagHTML.DIV
			+	TagHTML.HI(3)+"Extension Informations:"+TagHTML.HIClose(3)
			+	TagHTML.HR
			+	writer.toString()
			+	TagHTML.DIVCLOSE;
		}

		//Distribution Info
		if(meta.getDistributionInfo()!=null){
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(meta.getDistributionInfo());
			htmlBody+= TagHTML.DIV
			+	TagHTML.HI(3)+"Distribution Informations:"+TagHTML.HIClose(3)
			+	TagHTML.HR
			+	writer.toString()
			+	TagHTML.DIVCLOSE;
		}
		
		//Data Quality Info
		if(meta.getDistributionInfo()!=null){
			dumper = getNewWriter(dumper);
			writer = dumper.writeObjectDump(meta.getDataQualityInfo());
			htmlBody+= TagHTML.DIV
			+	TagHTML.HI(3)+"Data Quality Info:"+TagHTML.HIClose(3)
			+	TagHTML.HR
			+	writer.toString()
			+	TagHTML.DIVCLOSE;
		}

//		htmlBody+= TagHTML.BR;
		
		
		htmlBody+= TagHTML.HI(3)+"Other Meta Info:"+TagHTML.HIClose(3);
		htmlBody+= TagHTML.HR;
		
		//File Identifier
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"File Identifier:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getFileIdentifier()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;
		
		//Metadata Standard Name
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"Metadata Standard Name:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getMetadataStandardName()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;
		
		//Metadata Standard Version
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"Metadata Standard Version:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getMetadataStandardVersion()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;
		
		//LANGUAGE
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"Language:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getLanguage()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;
		
		//Locale
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"Locale:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getLocale()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;
		
		//Hierchical
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"Hierarchy Level Name:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getHierarchyLevelName()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;
		
		//Metadata Data Set URI
		htmlBody+=TagHTML.DIV 
				+ TagHTML.HI(3)+"Data Set URI:"+TagHTML.HIClose(3)
//				+ TagHTML.BR
				+ TagHTML.HI(4)+meta.getDataSetURI()+TagHTML.HIClose(4)
				+ TagHTML.DIVCLOSE;
		
//		htmlBody+= TagHTML.BR;

	
		if(includeTagBody)
			htmlBody += TagHTML.BODYCLOSE;
		
		return htmlBody;

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