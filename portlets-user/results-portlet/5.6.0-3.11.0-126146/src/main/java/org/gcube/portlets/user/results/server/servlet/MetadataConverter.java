//package org.gcube.portlets.user.results.server.servlet;
//
///**
// * 
// */
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.rmi.RemoteException;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Templates;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//
//import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
//import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.util.GenericResource;
//import org.gcube.application.framework.userprofiles.library.impl.UserProfile;
//import org.gcube.common.core.utils.logging.GCUBELog;
//
//
//
///**
// * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
// *
// */
//public class MetadataConverter {
//	GCUBELog _log = new GCUBELog(MetadataConverter.class);
//
//	/**
//	 * Schema name to XSLT generic resource id map. 
//	 */
//	protected Map<String, String> schemaNameId = new LinkedHashMap<String, String>();
//
//	/**
//	 * Id to XSLT map cache.
//	 */
//	protected Map<String, String> xsltCache = new LinkedHashMap<String, String>();
//
//	protected boolean setup = false;
//	private NewresultsetServiceImpl myServlet;
//	/**
//	 * Create a new MetadataConverter.
//	 */
//	public MetadataConverter(NewresultsetServiceImpl myServlet)
//	{
//		this.myServlet = myServlet;
//	}
//
//	/**
//	 * Load user xslt.
//	 * @param d4session
//	 */
//	public synchronized void setup(ASLSession d4session)
//	{
//		if (!setup) retrieveXSLTIds(d4session);
//	}
//
//	public synchronized void isReady()
//	{
//
//	}
//
//
//	/**
//	 * Convert the given metadata XML to HTML.
//	 * @param schemaName
//	 * @param xml
//	 * @param d4session 
//	 * @return
//	 * @throws Exception 
//	 */
//	public String convert(String schemaName, String xml, ASLSession d4session) throws Exception
//	{
//		_log.debug("convert schemaName: "+schemaName+" xml.length: "+xml.length()+" session: "+d4session);
//		//we have to find from the schema name the xslt id
//
//		String xsltId = null;
//
//
//		for (String schemaid : schemaNameId.keySet()) {
//			_log.debug("schemakey" + schemaid ) ;
//		}
//
//		//FIXME find a rule for schema names
//		if (schemaNameId.containsKey(schemaName)) xsltId = schemaNameId.get(schemaName);
//		else if (schemaNameId.containsKey(schemaName.toLowerCase())) xsltId = schemaNameId.get(schemaName.toLowerCase());
//		else if (schemaNameId.containsKey(schemaName.toUpperCase())) xsltId = schemaNameId.get(schemaName.toUpperCase());
//
//
//		_log.debug("XSLT ID: "+xsltId);
//
//		String xslt = null;
//
//		if (xsltId != null){
//			try {
//				xslt = getXSL(xsltId, d4session);
//			} catch (RemoteException e) {
//				_log.error("Error during xslt retrieving");
//			}
//		}
//
//		if (xslt == null){
//			//we have to use the default xslt schema
//			try {
//				xslt = getDefaultXSLT();
//			} catch (IOException e) {
//				_log.error("Error during default xslt retrieving");
//				throw e;
//			}
//		}
//
//		if (xslt == null) throw new Exception("No xslt found");
//		//		
//		//		Logger.debug("XSLT: "+xslt);
//		//		Logger.debug("XML: "+xml);
//
//		String html = transform(xml, xslt);
//
//		return html;
//	}
//
//	/**
//	 * Convert the given XML to HTML using the default XSLT.
//	 * @param xml the XML to convert.
//	 * @param d4session the user session.
//	 * @return the HTML.
//	 * @throws Exception 
//	 */
//	public String convertUsingDefault(String xml, ASLSession d4session) throws Exception
//	{
//		_log.debug("convertUsingDefault xml.length: "+xml.length()+" session: "+d4session);
//
//		String xslt = getDefaultXSLT();
//
//		if (xslt == null) throw new Exception("No xslt found");
//
//		//		
//		//		Logger.debug("XSLT: "+xslt);
//		//		Logger.debug("XML: "+xml);
//
//		String html = transform(xml, xslt);
//
//		return html;
//	}
//
//	/**
//	 * Retrieve from user profile all transformation schemas id. 
//	 * @param d4session
//	 */
//	protected void retrieveXSLTIds(ASLSession session)
//	{
//		_log.debug("retrieveXSLTIds CALLING USER PROFILE");
//		UserProfile userprofile = new UserProfile(session);
//		_log.debug("Profile loaded, calling getMetadataXSLTs with user " + session.getUsername());
//
//		HashMap<String, String> idSchemaNameMap = userprofile.getMetadataXSLTs(session.getUsername());
//
//		_log.debug("FOUND "+idSchemaNameMap.size()+" schema maps");
//
//		_log.debug("ITERATING THE HASMAP ");
//
//		for (Map.Entry<String, String> idSchemaName :idSchemaNameMap.entrySet()){
//			String id = idSchemaName.getValue();
//			String schemaName = idSchemaName.getKey();
//			_log.trace("id: "+id+" schema: "+schemaName);
//			_log.trace("PUTTING in schemaNameId " + schemaName  + "," + id);
//			schemaNameId.put(schemaName, id);
//
//		}
//	}
//
//	/**
//	 * Get a XSLT from is id.
//	 * If the XSLT is not in the cache then is retrieved from IS.
//	 * @param xsltId the XSLT id.
//	 * @param d4session 
//	 * @return the XSLT.
//	 * @throws RemoteException 
//	 */
//	protected String getXSL(String xsltId, ASLSession d4session) throws RemoteException
//	{
//		if (xsltCache.containsKey(xsltId)) return xsltCache.get(xsltId);
//
//		String xslt = retrieveXSLT(xsltId, d4session);
//		if (xslt == null) return null;
//
//		xsltCache.put(xsltId, xslt);
//
//		return xslt;
//	}
//
//	/**
//	 * Retrieve a schema from his generic resource.
//	 * @param xslId the schema id.
//	 * @param d4session 
//	 * @return the schema.
//	 * @throws RemoteException 
//	 */
//	protected String retrieveXSLT(String xslId, ASLSession d4session) throws RemoteException
//	{
//		GenericResource resource = new GenericResource(d4session);
//		List<ISGenericResource> genericResources = resource.getGenericResourceByID(xslId);
//		if (genericResources.size() == 0){
//			_log.error("xsltId resource not found for id "+xslId);
//			return null;
//		}
//
//		ISGenericResource xsltResource = genericResources.get(0);
//		String xsltBody = xsltResource.getBody(); 		
//		String xslt = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"+xsltBody;
//
//		return xslt;
//	}
//
//	/**
//	 * Transform an XML string to HTML string using an XSLT processor.
//	 * @param xml the XML to convert.
//	 * @param xslt the XML used for the conversion.
//	 * @return the HTML.
//	 * @throws TransformerException if an error occurs.
//	 */
//	protected String transform(String xml, String xslt) throws TransformerException
//	{
//		TransformerFactory tf = TransformerFactory.newInstance();
//		StreamSource source = new StreamSource(new ByteArrayInputStream(xslt.getBytes()));
//		Templates compiledXSLT = tf.newTemplates(source);
//		Transformer t = compiledXSLT.newTransformer();
//		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
//		StringWriter w = new StringWriter();
//		t.transform(new StreamSource(new StringReader(xml)), new StreamResult(w));
//		return w.toString();
//	}
//
//	/**
//	 * 
//	 * @return
//	 * @throws IOException
//	 */
//	protected String getDefaultXSLT() throws IOException {
//
//		FileInputStream fis = new FileInputStream(myServlet.getRealPath()+"/config/xmlverbatim.xsl");
//		InputStreamReader isr = new InputStreamReader(fis);
//
//		BufferedReader filebuf = null;
//		String nextStr = null;
//		String toReturn = new String();
//		try {
//			filebuf = new BufferedReader(isr);
//			nextStr = filebuf.readLine(); 
//			while (nextStr != null) {
//				toReturn += nextStr ;
//				nextStr = filebuf.readLine(); 
//			}
//			filebuf.close(); // chiude il file 
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
//		return toReturn;
//	}
//}
//
