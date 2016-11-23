package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

import java.util.HashMap;

import org.gcube.datatransformation.datatransformationlibrary.imanagers.queries.ContentTypeCondition;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * QueryObject for transformation unit queries.
 * </p>
 */
public class TransformationUnitQueryObject extends QueryObject{

	/**
	 * The transformation program id.
	 */
	public String transformationProgramID = null;
	
	/**
	 * The source content type conditions.
	 */
	public HashMap<Integer, ContentTypeCondition> sourceContentTypeConditions = new HashMap<Integer, ContentTypeCondition>();
	/**
	 * The target content type conditions.
	 */
	public HashMap<Integer, ContentTypeCondition> targetContentTypeConditions = new HashMap<Integer, ContentTypeCondition>();
	
}
