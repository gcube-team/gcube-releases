package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.workflow.adaptor.utils.IParsedInfo;
import gr.uoa.di.madgik.workflow.adaptor.utils.IWorkflowParser;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobDescriptionType;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JDLParser is an implementation of the {@link IWorkflowParser} that is capable of parsing a JDL description
 * that formulates a job or a DAG of jobs. Depending on the type of the JDL, the actual parsing is forwarded to the 
 * two supporting parsers, {@link JDLJobExtractor} and {@link JDLDAGExtractor}.
 * 
 * @author gpapanikos
 */
public class JDLParser implements IWorkflowParser
{
	
	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(JDLParser.class);
	
	/** The information parsed */
	public ParsedJDLInfo Internal=null;

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.utils.IWorkflowParser#Parse(java.io.File)
	 */
	public void Parse(File serialization) throws WorkflowSerializationException, WorkflowValidationException
	{
		String payload=null;
		try
		{
			payload=FileUtils.ReadFileToString(serialization);
		}
		catch(Exception ex)
		{
			throw new WorkflowSerializationException("Could not parse jdl file", ex);
		}
		this.Parse(payload);
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.utils.IWorkflowParser#Parse(java.lang.String)
	 */
	public void Parse(String serialization) throws WorkflowSerializationException, WorkflowValidationException
	{
		String jdlSer=JDLParsingUtils.Trim(serialization);
		jdlSer=JDLParsingUtils.StripComments(jdlSer);
		String jdlBlock = JDLParsingUtils.GetDefinitionBlock(jdlSer);
		Map<String, String> KeyValues=JDLParsingUtils.GetKeyValues(jdlBlock);
		logger.info("JDL key-values: " + KeyValues);
		try
		{
			this.ProcessKeys(KeyValues);
		}catch(WorkflowInternalErrorException ex)
		{
			throw new WorkflowSerializationException("Could not parse provided jdl serialization",ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.utils.IWorkflowParser#GetParsedInfo()
	 */
	public IParsedInfo GetParsedInfo()
	{
		return this.Internal;
	}
	
	/**
	 * The key values that were previously extracted during the parsing is forwarded either to
	 * the {@link JDLJobExtractor} or to the {@link JDLDAGExtractor} depending on the type
	 * of JDL provided. After this processing an instance of {@link ParsedJDLInfo} is populated
	 * and ready to be used to produce an {@link ExecutionPlan}
	 * 
	 * @param KeyValues the key values
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 * @throws WorkflowInternalErrorException An internal error occurred
	 */
	private void ProcessKeys(Map<String, String> KeyValues) throws WorkflowValidationException, WorkflowInternalErrorException
	{
		JobDescriptionType jdt=JobDescriptionType.valueOf(JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Type,true)));
		switch(jdt)
		{
			case DAG:
			{
				JDLDAGExtractor extr=new JDLDAGExtractor(KeyValues);
				this.Internal=extr.ProcessKeys();
				break;
			}
			case Job:
			{
				JDLJobExtractor extr=new JDLJobExtractor(KeyValues);
				this.Internal=extr.ProcessKeys();
				break;
			}
			default:
			{
				throw new WorkflowValidationException("Unrecognized job type");
			}
		}
		logger.debug("Parsed info is "+this.Internal.ToXML());
	}
}
