package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Transformations Graph is responsible to find <tt>Transformation Units</tt> which can be used in order to perform a transformationUnit for an object, from its content type (source) to a target content type.
 * </p>
 */
public interface TransformationsGraph {

	/**
	 * Finds applicable <tt>Transformation Units</tt> from the <tt>sourceContentType</tt> to the <tt>targetContentType</tt>.
	 * 
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @param targetContentType The <tt>ContentType</tt> of the target <tt>DataElement</tt>.
	 * @param createAndPublishCompositeTP If true then a new composite <tt>TransformationProgram</tt> is created and published if no available <tt>TransformationProgram</tt> exists.
	 * @return The available <tt>TransformationUnits</tt>.
	 */
	public ArrayList<TransformationUnit> findApplicableTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP);

	/**
	 * Finds <tt>Transformation Units</tt> from the <tt>sourceContentType</tt> to the <tt>targetContentType</tt>. Returns also generic <tt>Transformation Units</tt>.
	 *  
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @param targetContentType The <tt>ContentType</tt> of the target <tt>DataElement</tt>.
	 * @param createAndPublishCompositeTP If true then a new composite <tt>TransformationProgram</tt> is created and published if no available <tt>TransformationProgram</tt> exists.
	 * @return The available <tt>TransformationUnits</tt>.
	 */
	public ArrayList<TransformationUnit> findAnyTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP);

	/**
	 * Finds the available target <tt>ContentTypes</tt> from the <tt>sourceContentType</tt>.
	 * 
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @return The available target <tt>ContentTypes</tt> from the <tt>sourceContentType</tt>.
	 */
	public ArrayList<ContentType> findAvailableTargetContentTypes(ContentType sourceContentType);
	
	/**
	 * Destroys the graph.
	 */
	public void destroy();

}