package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobDescriptionType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobType;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.Map;

/**
 * The Class JDLJobExtractor operates on a previously extracted map of the keys and their respective values
 * contained in the provided JDL description. The extractor will scan through the map for known keys and process 
 * their respective values to populate the produced {@link ParsedJDLInfo}. The known keys which the extractor 
 * looks for in the provided map are the following:
 *  - {@link ParsedJDLInfo.KnownKeys#JobType} (mandatory)
 *  - {@link ParsedJDLInfo.KnownKeys#Executable} (mandatory)
 *  - {@link ParsedJDLInfo.KnownKeys#Arguments} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#StdInput} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#StdOutput} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#StdError} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#InputSandbox} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#OutputSandbox} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Environment} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#RetryCount} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#ShallowRetryCount} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#RetryInterval} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Rank} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Requirements} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#ConnectionMode} (optional)
 *  
 * The Keys are expected to be found with the exact capitalization that is defined for each key. No distinction
 * between the two {@link ParsedJDLInfo.KnownKeys#RetryCount} and {@link ParsedJDLInfo.KnownKeys#ShallowRetryCount}
 * is made.
 * 
 *  TODO:
 *   - Fix capitalization requirement
 *   
 * @author gpapanikos
 */
public class JDLJobExtractor
{
	
	/** The Key values. */
	private Map<String, String> KeyValues;
	
	/**
	 * Instantiates a new jDL job extractor.
	 * 
	 * @param KeyValues the key values
	 */
	public JDLJobExtractor(Map<String, String> KeyValues)
	{
		this.KeyValues=KeyValues;
	}
	
	/**
	 * Process keys.
	 * 
	 * @return the parsed jdl info
	 * 
	 * @throws WorkflowValidationException the workflow validation exception
	 */
	public ParsedJDLInfo ProcessKeys() throws WorkflowValidationException
	{
		ParsedJDLInfo Internal=new ParsedJDLInfo();
		Internal.jobDescriptionType=JobDescriptionType.Job;
		Internal.jobType=JobType.valueOf(JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.JobType,true)));
		Internal.Executable=JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Executable,true));
		Internal.Arguments=JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Arguments,false));
		Internal.Input=JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.StdInput,false));
		Internal.Output=JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.StdOutput,false));
		Internal.Error=JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.StdError,false));
		Internal.InSandbox=JDLParsingUtils.ParseSandbox(JDLParsingUtils.StripBrackets(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.InputSandbox,false)));
		Internal.OutSandbox=JDLParsingUtils.ParseSandbox(JDLParsingUtils.StripBrackets(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.OutputSandbox,false)));
		Internal.Environment=JDLParsingUtils.ParseEnvironment(JDLParsingUtils.StripBrackets(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Environment,false)));
		Internal.SetRetryCount(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.RetryCount,false));
		Internal.SetRetryCount(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.ShallowRetryCount,false));
		Internal.SetRetryInterval(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.RetryInterval,false));
		Internal.Rank=JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Rank,false);
		Internal.Requirements=JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Requirements,false);
		Internal.SetConnectionMode(JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.ConnectionMode, false)));
		return Internal;
	}
}
