package gr.uoa.di.madgik.workflow.adaptor.utils;

import gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.io.File;

/**
 * The Interface IWorkflowParser is implemented by classes that can parse some external workflow language.
 * It is expected that for every language that will be supported and transformed into some form of internally
 * managed workflow, one respective parser will be made available. The output of the parser will then be used by 
 * the respective {@link IWorkflowAdaptor} that instantiated the {@link IWorkflowParser}
 * 
 * @author gpapanikos
 */
public interface IWorkflowParser
{
	
	/**
	 * Parses the string serialization of the workflow language that the parser is implemented for
	 * 
	 * @param serialization the serialization of the workflow in some language
	 * 
	 * @throws WorkflowSerializationException Problem with the serialization of the provided workflow
	 * @throws WorkflowValidationException Problem with the validity of some element
	 */
	public void Parse(String serialization) throws WorkflowSerializationException, WorkflowValidationException;
	
	/**
	 * Parses the string the workflow language that is contained in the provided file that the parser 
	 * is implemented for
	 * 
	 * @param serialization The file containing the workflow description
	 * 
	 * @throws WorkflowSerializationException Problem with the serialization of the provided workflow
	 * @throws WorkflowValidationException Problem with the validity of some element
	 */
	public void Parse(File serialization) throws WorkflowSerializationException, WorkflowValidationException;
	
	/**
	 * Retrieves the information parsed. This method must be called after a call to one of
	 * {@link IWorkflowParser#Parse(File)} or {@link IWorkflowParser#Parse(String)}
	 * 
	 * @return the parsed information
	 */
	public IParsedInfo GetParsedInfo();
}
