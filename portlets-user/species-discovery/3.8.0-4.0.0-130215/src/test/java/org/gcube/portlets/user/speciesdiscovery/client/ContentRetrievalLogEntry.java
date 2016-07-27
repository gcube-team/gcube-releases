///**
// * 
// */
//package org.gcube.portlets.user.speciesdiscovery.client;
//
//import org.gcube.application.framework.accesslogger.model.AccessLogEntry;
//import org.gcube.application.framework.accesslogger.model.EntryTypeConstants;
//import org.gcube.application.framework.accesslogger.model.TemplateConstants;
//
///**
// * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
// * @Nov 8, 2013
// *
// */
//public class ContentRetrievalLogEntry extends AccessLogEntry {
//	 
//	private String objectID;
// 
//	private String objectName;
// 
//	/**
//	 * Constructor 
//	 * 
//	 * @param objectID The ID of the object that is retrieved
//	 * @param objectName The name of the object that is retrieved
//	 */
//	public ContentRetrievalLogEntry(String objectID, String objectName) {
//		super(EntryTypeConstants.RETRIEVE_CONTENT_ENTRY);
// 
//		this.objectID = replaceReservedChars(objectID);
//		this.objectName = replaceReservedChars(objectName);
//	}
// 
//	/**
//	 * @return The log message for a Content retrieval entry
//	 */
//	public String getLogMessage() {
//		String message = "";
//		message += TemplateConstants.CONTENT_ID + TemplateConstants.eqChar + 
//			this.objectID + TemplateConstants.separateCharacters + TemplateConstants.CONTENT_NAME +  
//			TemplateConstants.eqChar + this.objectName;
// 
//		return message;
//	}
// 
//}
