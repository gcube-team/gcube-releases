package org.gcube.datatransformation.datatransformationlibrary.programs;

import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.ProgramExecutor;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Each <tt>Program</tt> is an independent, self-describing entity that encapsulates the logic of the transformationUnit process it performs. 
 * The gDTS loads these required programs dynamically as the execution proceeds and supplies them with the input data that must be transformed.
 * </p>
 */
public interface Program {
	
	/**
	 * The method invoked by {@link ProgramExecutor} in order for the <tt>Program</tt> to transform the <tt>sources</tt> to the <tt>targetContentType</tt>.
	 *  
	 * @param sources The <tt>DataSources</tt> from which the <tt>Program</tt> will get the <tt>DataElements</tt>.
	 * @param programParameters The parameters of the <tt>Program</tt> which are primarily set by the <tt>TransformationUnit</tt>.
	 * @param targetContentType The <tt>ContentType</tt> in which the source data will be transformed.
	 * @param sink The <tt>DataSink</tt> in which the <tt>Program</tt> will append the transformed <tt>DataElements</tt>.
	 * @throws Exception If the program is not capable to transform <tt>DataElements</tt>.
	 */
	public void transform(List<DataSource> sources, List<Parameter> programParameters, ContentType targetContentType, DataSink sink) throws Exception;
	
}
