package org.gcube.datatransformation.datatransformationlibrary.imanagers;

import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Retrieves, publishes and queries {@link TransformationProgram}s from the TP registry.
 */
public interface IManager {
	
	/**
	 * Returns a {@link TransformationProgram} instance by its id.  
	 * 
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt>.
	 * @return The instance of the <tt>Transformation Program</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Program</tt> from the registry.
	 */
	public TransformationProgram getTransformationProgram(String transformationProgramID) throws Exception;
	
	/**
	 * Returns a {@link TransformationUnit} instance by its id.
	 * 
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt> in which the <tt>Transformation Unit</tt> belongs to.
	 * @param transformationUnitID The id of the <tt>Transformation Unit</tt>.
	 * @return The instance of the <tt>Transformation Unit</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Unit</tt> from the registry.
	 */
	public TransformationUnit getTransformationUnit(String transformationProgramID, String transformationUnitID) throws Exception;
	
	/**
	 * Returns the available <tt>Transformation Program IDs</tt>.
	 * 
	 * @return The available <tt>Transformation Program IDs</tt>.
	 * @throws Exception If the available <tt>Transformation Program IDs</tt> could not be fetched from the registry.
	 */
	public String[] getAvailableTransformationProgramIDs() throws Exception;
	
	/**
	 * Publishes a <tt>Transformation Program</tt> to the registry.
	 * 
	 * @param transformationProgram The <tt>Transformation Program</tt> instance which will be published.
	 * @throws Exception If the <tt>Transformation Program</tt> could not be published.
	 */
	public void publishTransformationProgram(TransformationProgram transformationProgram) throws Exception;
	
	/**
	 * Queries <tt>Transformation Programs</tt>.
	 * 
	 * @param query The query.
	 * @return The result of the query in <tt>xml</tt> format.
	 * @throws Exception If the query could not be performed.
	 */
	public String queryTransformationPrograms(String query) throws Exception;
}
