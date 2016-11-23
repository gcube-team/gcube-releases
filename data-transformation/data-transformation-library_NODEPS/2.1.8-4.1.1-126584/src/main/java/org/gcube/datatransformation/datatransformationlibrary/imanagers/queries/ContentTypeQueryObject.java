package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Query object for content types of a tu.
 * </p>
 */
public class ContentTypeQueryObject extends QueryObject {
	
	/**
	 * The transformation program id.
	 */
	public String transformationProgramID;
	/**
	 * The transformation unit id.
	 */
	public String transformationUnitID;
	
	/**
	 * The content type condition.
	 */
	public ContentTypeCondition contentTypeCondition = new ContentTypeCondition();  
}
