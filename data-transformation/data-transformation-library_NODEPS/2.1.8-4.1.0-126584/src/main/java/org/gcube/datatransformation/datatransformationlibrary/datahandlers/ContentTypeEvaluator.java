package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Content type evaluator is an interface which is implemented by classes which are able to find the {@link ContentType} of a data elements.
 */
public interface ContentTypeEvaluator {

	/**
	 * Evaluates the {@link ContentType} of the {@link DataElement} with id dataElementID.
	 * 
	 * @param dataElementID The id of the {@link DataElement}.
	 * @return The {@link ContentType} of the {@link DataElement}.
	 * @throws Exception If the evaluator did not manage to evaluate the {@link ContentType}.
	 */
	public ContentType evaluateContentTypeOfDataElement(String dataElementID) throws Exception;
}
