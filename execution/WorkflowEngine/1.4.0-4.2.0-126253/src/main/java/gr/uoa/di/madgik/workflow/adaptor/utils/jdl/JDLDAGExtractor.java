package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobDescriptionType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.KnownKeys;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JDLDAGExtractor operates on a previously extracted map of the keys and their respective values
 * contained in the provided JDL description. The extractor will scan through the map for known keys and process 
 * their respective values to populate the produced {@link ParsedJDLInfo}. The known keys which the extractor 
 * looks for in the provided map are the following:
 *  - {@link ParsedJDLInfo.KnownKeys#ParsingMode} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Max_Running_Nodes} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Rank} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Requirements} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#NodesCollocation} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#ConnectionMode} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Nodes} (optional)
 *  - {@link ParsedJDLInfo.KnownKeys#Dependencies} (optional)
 *  For every Node described in the DAG, the {@link ParsedJDLInfo.KnownKeys#Description} value is extracted
 *  and is used to populate a new {@link ParsedJDLInfo}, contained in the {@link ParsedJDLInfo} produced by
 *  the {@link JDLDAGExtractor}, using the {@link JDLJobExtractor}.
 * 
 * The Keys are expected to be found with the exact capitalization that is defined for each key. The dependency
 * list retrieve by the {@link ParsedJDLInfo.KnownKeys#Dependencies} are expected to be defined in pairs. No
 * inner list expression is supported. The {@link ParsedJDLInfo.KnownKeys#InputSandbox} is not supported at the 
 * level of DAG. This means that every node needs to define its own Input Sandbox and cannot reference the 
 * global one.
 * 
 *  TODO:
 *   - Fix capitalization requirement
 *   - Support inner list definition of dependencies
 *   
 * @author gpapanikos
 */
public class JDLDAGExtractor
{
	
	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(JDLDAGExtractor.class);
	
	/** The Key values. */
	private Map<String, String> KeyValues;
	
	/**
	 * Instantiates a new JDL DAG extractor.
	 * 
	 * @param KeyValues the key values
	 */
	public JDLDAGExtractor(Map<String, String> KeyValues)
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
		Internal.jobDescriptionType=JobDescriptionType.DAG;
		//Internal.InSandbox=JDLParsingUtils.ParseSandbox(JDLParsingUtils.StripBrackets(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.InputSandbox,false)));
		Internal.SetParsingMode(JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.ParsingMode,false)));
		Internal.SetMaxRunningNodes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Max_Running_Nodes,false));
		Internal.Rank=JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Rank,false);
		Internal.Requirements=JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.Requirements,false);
		Internal.SetNodesCollocation(JDLParsingUtils.GetKeyValue(KeyValues, KnownKeys.NodesCollocation, false));
		Internal.SetConnectionMode(JDLParsingUtils.StripQuotes(JDLParsingUtils.GetKeyValue(KeyValues, ParsedJDLInfo.KnownKeys.ConnectionMode, false)));
		String nodes=JDLParsingUtils.GetDefinitionBlock(JDLParsingUtils.GetKeyValue(KeyValues,ParsedJDLInfo.KnownKeys.Nodes,false));
		String dependencies=JDLParsingUtils.GetKeyValue(KeyValues,ParsedJDLInfo.KnownKeys.Dependencies,false);
		Map<String,String> nodeList= JDLParsingUtils.GetKeyValues(nodes);
		for(String nodeName : nodeList.keySet())
		{
			Map<String,String> defList= JDLParsingUtils.GetKeyValues(JDLParsingUtils.GetDefinitionBlock(nodeList.get(nodeName)));
			String definition = JDLParsingUtils.GetKeyValue(defList, KnownKeys.Description, true);
			Map<String,String> jobList= JDLParsingUtils.GetKeyValues(JDLParsingUtils.GetDefinitionBlock(definition));
			JDLJobExtractor jobextr=new JDLJobExtractor(jobList);
			ParsedJDLInfo nfo= jobextr.ProcessKeys();
			Internal.Nodes.put(nodeName, nfo);
			logger.debug("Node with name "+nodeName+" and definition value "+ nfo.ToXML());
		}
		// Checking if all nodes of dag are of the same jobtype and assigning this type to the dag
		JobType type = null;
		for(ParsedJDLInfo nfo : Internal.Nodes.values())
		{
			if(!nfo.jobType.equals(type) && type!=null)
				throw new WorkflowValidationException();
			type = nfo.jobType;
		}
		Internal.jobType = type;
		logger.debug("Dependencies="+dependencies);
		Internal.Dependencies=JDLParsingUtils.GetDependencies(dependencies);
		return Internal;
	}
}
