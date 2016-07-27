///**
// * 
// */
//package org.gcube.portlets.user.workspace.server.util;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.rmi.RemoteException;
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
//import org.apache.commons.io.IOUtils;
//import org.apache.log4j.Logger;
//import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
//import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.util.GenericResource;
//
///**
// * @author Federico De Faveri defaveri@isti.cnr.it
// *
// */
//public class MetadataConverter {
//
//	protected Logger logger;
//
//	/**
//	 * FIXME public as generic resource
//	 */
//	protected static final String DEFAULT_XSLT = "/org/gcube/portlets/user/workspace/server/util/resources/xmlverbatim.xsl";
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
//
//	/**
//	 * Create a new MetadataConverter.
//	 */
//	public MetadataConverter(Logger logger) {
//		this.logger = logger;
//	}
//
//	/**
//	 * Load user xslt.
//	 * 
//	 * @param aslSession
//	 */
//	public synchronized void setup(ASLSession aslSession) {
//		if (!setup)
//			retrieveXSLTIds(aslSession);
//	}
//
//	public synchronized void isReady() {
//
//	}
//
//	/**
//	 * Convert the given metadata XML to HTML.
//	 * 
//	 * @param schemaName
//	 * @param xml
//	 * @param aslSession
//	 * @return
//	 * @throws Exception
//	 */
//	public String convert(String schemaName, String xml, ASLSession aslSession)
//			throws Exception {
//		logger.trace("convert schemaName: " + schemaName + " xml.length: "
//				+ xml.length() + " session: " + aslSession);
//		// we have to find from the schema name the xslt id
//
//		String xsltId = null;
//
//		// FIXME find a rule for schema names
//		if (schemaNameId.containsKey(schemaName))
//			xsltId = schemaNameId.get(schemaName);
//		else if (schemaNameId.containsKey(schemaName.toLowerCase()))
//			xsltId = schemaNameId.get(schemaName.toLowerCase());
//		else if (schemaNameId.containsKey(schemaName.toUpperCase()))
//			xsltId = schemaNameId.get(schemaName.toUpperCase());
//
//		logger.trace("XSLT ID: " + xsltId);
//
//		String xslt = null;
//
//		if (xsltId != null) {
//			try {
//				xslt = getXSL(xsltId, aslSession);
//			} catch (RemoteException e) {
//				logger.error("Error during xslt retrieving", e);
//			}
//		}
//
//		if (xslt == null) {
//			// we have to use the default xslt schema
//			try {
//				xslt = getDefaultXSLT();
//			} catch (IOException e) {
//				logger.error("Error during default xslt retrieving", e);
//				throw e;
//			}
//		}
//
//		if (xslt == null)
//			throw new Exception("No xslt found");
//
//		logger.trace("XSLT: " + xslt);
//		logger.trace("XML: " + xml);
//
//		String html = transform(xml, xslt);
//
//		return html;
//	}
//
//	/**
//	 * Convert the given XML to HTML using the default XSLT.
//	 * 
//	 * @param xml
//	 *            the XML to convert.
//	 * @param aslSession
//	 *            the user session.
//	 * @return the HTML.
//	 * @throws Exception
//	 */
//	public String convertUsingDefault(String xml, ASLSession aslSession)
//			throws Exception {
//		logger.trace("convertUsingDefault xml.length: " + xml.length()
//				+ " session: " + aslSession);
//
//		String xslt = getDefaultXSLT();
//
//		if (xslt == null)
//			throw new Exception("No xslt found");
//
//		logger.trace("XSLT: " + xslt);
//		logger.trace("XML: " + xml);
//
//		String html = transform(xml, xslt);
//
//		return html;
//	}
//
//	/**
//	 * Retrieve from user profile all transformation schemas id.
//	 * 
//	 * @param aslSession
//	 */
//	protected void retrieveXSLTIds(ASLSession aslSession) {
//		logger.trace("retrieveXSLTIds aslSession: " + aslSession);
//
//		/*
//		 * TODO RE-ENABLE
//		 * 
//		 * try{
//		 * 
//		 * UserProfile userprofile = new UserProfile(aslSession);
//		 * logger.trace("Profile loaded");
//		 * 
//		 * HashMap<String, String> idSchemaNameMap =
//		 * userprofile.getMetadataXSLTs(aslSession.getUsername());
//		 * 
//		 * logger.trace("found "+idSchemaNameMap.size()+" schema maps");
//		 * 
//		 * for (Map.Entry<String, String> idSchemaName
//		 * :idSchemaNameMap.entrySet()){ String id = idSchemaName.getValue();
//		 * String schemaName = idSchemaName.getKey();
//		 * 
//		 * logger.trace("id: "+id+" schema: "+schemaName);
//		 * 
//		 * int uIndex = schemaName.lastIndexOf("-|-"); if (uIndex>=0) schemaName
//		 * = schemaName.substring(0, uIndex);
//		 * 
//		 * logger.trace("schema: "+schemaName);
//		 * 
//		 * schemaNameId.put(schemaName, id); } }catch(Throwable e) { //FIXME add
//		 * exception to log
//		 * logger.error("Something wrong retriving the user profile informations"
//		 * ); }
//		 */
//	}
//
//	/**
//	 * Get a XSLT from is id. If the XSLT is not in the cache then is retrieved
//	 * from IS.
//	 * 
//	 * @param xsltId
//	 *            the XSLT id.
//	 * @param aslSession
//	 * @return the XSLT.
//	 * @throws RemoteException
//	 */
//	protected String getXSL(String xsltId, ASLSession aslSession)
//			throws RemoteException {
//		if (xsltCache.containsKey(xsltId))
//			return xsltCache.get(xsltId);
//
//		String xslt = retrieveXSLT(xsltId, aslSession);
//		if (xslt == null)
//			return null;
//
//		xsltCache.put(xsltId, xslt);
//
//		return xslt;
//	}
//
//	/**
//	 * Retrieve a schema from his generic resource.
//	 * 
//	 * @param xslId
//	 *            the schema id.
//	 * @param aslSession
//	 * @return the schema.
//	 * @throws RemoteException
//	 */
//	protected String retrieveXSLT(String xslId, ASLSession aslSession)
//			throws RemoteException {
//		GenericResource resource = new GenericResource(aslSession);
//		List<ISGenericResource> genericResources = resource
//				.getGenericResourceByID(xslId);
//		if (genericResources.size() == 0) {
//			logger.error("xsltId resource not found for id " + xslId);
//			return null;
//		}
//
//		ISGenericResource xsltResource = genericResources.get(0);
//		String xsltBody = xsltResource.getBody();
//		String xslt = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
//				+ xsltBody;
//
//		return xslt;
//	}
//
//	/**
//	 * Transform an XML string to HTML string using an XSLT processor.
//	 * 
//	 * @param xml
//	 *            the XML to convert.
//	 * @param xslt
//	 *            the XML used for the conversion.
//	 * @return the HTML.
//	 * @throws TransformerException
//	 *             if an error occurs.
//	 */
//	protected String transform(String xml, String xslt)
//			throws TransformerException {
//		TransformerFactory tf = TransformerFactory.newInstance();
//		StreamSource source = new StreamSource(new ByteArrayInputStream(
//				xslt.getBytes()));
//		Templates compiledXSLT = tf.newTemplates(source);
//		Transformer t = compiledXSLT.newTransformer();
//		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
//		StringWriter w = new StringWriter();
//		t.transform(new StreamSource(new StringReader(xml)),
//				new StreamResult(w));
//		return w.toString();
//	}
//
//	protected String getDefaultXSLT() throws IOException {
//		InputStream is = MetadataConverter.class
//				.getResourceAsStream(DEFAULT_XSLT);
//
//		if (is == null) {
//			logger.error("Default XSLT resource not found on " + DEFAULT_XSLT);
//			return null;
//		}
//
//		InputStreamReader isr = new InputStreamReader(is);
//
//		StringWriter sw = new StringWriter();
//
//		IOUtils.copy(isr, sw);
//
//		return sw.toString();
//	}
//}
