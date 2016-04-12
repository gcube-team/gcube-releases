package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.environment.is.elements.matching.MatchParser;
import gr.uoa.di.madgik.execution.plan.PlanConfig;
import gr.uoa.di.madgik.execution.plan.PlanConfig.ConnectionMode;
import gr.uoa.di.madgik.execution.plan.element.BagPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FlowPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowJDLAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IParsedInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class ParsedJDLInfo acts as a container of information parsed for a single Job or a DAG of Jobs
 * 
 * @author gpapanikos
 */
public class ParsedJDLInfo implements IParsedInfo
{
	
	/**
	 * The know JDL keys that are supported. These include original JDL keys 
	 * as well as extensions supported by the {@link WorkflowJDLAdaptor}
	 */
	public enum KnownKeys
	{
		
		/** The Type of the JDL. This can be one of the values defined in {@link ParsedJDLInfo.JobDescriptionType} */
		Type,
		
		/** The Job type. This can be one of the values defined in {@link ParsedJDLInfo.JobType} */
		JobType,
		
		/** The Executable of a single job. */
		Executable,
		
		/** The Arguments to be supplied to the executable. */
		Arguments,
		
		/** The StdIn to be supplied to the executable. */
		StdInput,
		
		/** The StdOut of the executable. */
		StdOutput,
		
		/** The StdErr of the executable. */
		StdError,
		
		/** The Input sandbox of the job. */
		InputSandbox,
		
		/** The Output sandbox of the job. */
		OutputSandbox,
		
		/** The Environment this job requires. */
		Environment,
		
		/** The Retry count of the job. */
		RetryCount,
		
		/** The Retry interval of the job. */
		RetryInterval,
		
		/** The Shallow retry count of the job. */
		ShallowRetryCount,
		
		/** Whether the nodes should be collocated in case of a DAG */
		NodesCollocation,
		
		/** The Rank function to use on class ads */
		Rank,
		
		/** The Requirements function to use on class ads */
		Requirements,
		
		/** The Nodes collection of a DAG */
		Nodes,
		
		/** The Dependencies that formulate the DAG */
		Dependencies,
		
		/** The Description of a DAG node job */
		Description,
		
		/** The Max_ running_ nodes in a DAG. */
		Max_Running_Nodes,
		
		/** The Parsing mode that the {@link WorkflowJDLAdaptor} should use 
		 * while constructing the plan. This can be one of the values defined in 
		 * {@link ParsedJDLInfo.ParsingModeType}. */
		ParsingMode,
		
		/** The Connection mode that the {@link WorkflowJDLAdaptor} should use.
		 * while constructing the plan. This can be one of the values defined in 
		 * {@link PlanConfig.ConnectionMode}. */
		ConnectionMode
	}
	
	/**
	 * The ParsingModeType.
	 */
	public enum ParsingModeType
	{
		
		/** A {@link BagPlanElement} should be used to orchestrate the plan execution */
		Bag,
		/** The orchestration should be defined with a series of {@link SequencePlanElement} and {@link FlowPlanElement} */
		Plan
	}
	
	/**
	 * The JobDescriptionType.
	 */
	public enum JobDescriptionType
	{
		
		/** The JDL defines a single Job */
		Job,
		/** The JDL defines a DAG of Jobs. */
		DAG
	}
	
	/**
	 * The Recognized JobType.
	 */
	public enum JobType
	{
		
		/** Normal job type. This Job type is supported by the {@link WorkflowJDLAdaptor} */
		Normal,
		/** Interactive job type. This Job type is not supported by the {@link WorkflowJDLAdaptor} */
		Interactive,
		/** MPICH  job type. This Job type is not supported by the {@link WorkflowJDLAdaptor} */
		MPICH,
		/** Checkpointable job type. This Job type is not supported by the {@link WorkflowJDLAdaptor} */
		Checkpointable,
		/** Partitionable job type. This Job type is not supported by the {@link WorkflowJDLAdaptor} */
		Partitionable,
		/** WS job type. This Job type is supported by the {@link WorkflowJDLAdaptor} */
		WS
	}
	
	/** The Default retry interval currently set to 10 seconds. */
	public static long DefaultRetryInterval=10000; //10 seconds
	
	/** The Default concurrent running jobs currently set to 10 nodes. */
	public static int DefaultConcurrentRunningJobs=10; //10 jobs
	
	/** The job description type. */
	public JobDescriptionType jobDescriptionType=JobDescriptionType.Job;
	
	/** The job type. */
	public JobType jobType=JobType.Normal;
	
	/** The Executable. */
	public String Executable=null;
	
	/** The Arguments. */
	public String Arguments=null;
	
	/** The Input. */
	public String Input=null;
	
	/** The Output. */
	public String Output=null;
	
	/** The Error. */
	public String Error=null;
	
	/** The In sandbox. */
	public List<String> InSandbox=new ArrayList<String>();
	
	/** The Out sandbox. */
	public List<String> OutSandbox=new ArrayList<String>();
	
	/** The Environment. */
	public List<EnvironmentKeyValue> Environment=new ArrayList<EnvironmentKeyValue>();
	
	/** The Retry count. */
	public int RetryCount=0;
	
	/** The Retry interval. */
	public long RetryInterval=0;
	
	/** The Rank function */
	public String Rank=null;
	
	/** The Requirements function */
	public String Requirements=null;
	
	/** The Nodes collocation attribute */
	public boolean NodesCollocation=false;
	
	/** The information on the Nodes in case the JDL describes a DAG */
	public Map<String, ParsedJDLInfo> Nodes=new HashMap<String, ParsedJDLInfo>();
	
	/** The Dependencies that form the DAG */
	public Map<String, List<String>> Dependencies=new HashMap<String, List<String>>();
	
	/** The Max running nodes. */
	public int MaxRunningNodes=PlanConfig.DefaultConcurrentActionsPerBoundary;
	
	/** The Mode of parsing. */
	public ParsingModeType ModeOfParsing=ParsingModeType.Bag;
	
	/** The Mode of connection. */
	public ConnectionMode ModeOfConnection=ConnectionMode.Callback;
	
	private String CommonRequirements=null;
	
	private Map<String, String> ExtraRequirements = new HashMap<String, String>();
	
	/**
	 * Sets the connection mode.
	 * 
	 * @param mode the mode
	 */
	public void SetConnectionMode(String mode)
	{
		if(mode==null) return;
		this.ModeOfConnection=ConnectionMode.valueOf(mode);
	}
	
	/**
	 * Sets the parsing mode.
	 * 
	 * @param mode the mode
	 */
	public void SetParsingMode(String mode)
	{
		if(mode==null) return;
		this.ModeOfParsing=ParsingModeType.valueOf(mode);
	}
	
	/**
	 * Sets the max running nodes.
	 * 
	 * @param number the maximum number of concurrently running nodes
	 */
	public void SetMaxRunningNodes(String number)
	{
		if(number==null) return;
		int c=Integer.parseInt(number);
		MaxRunningNodes=c;
	}
	
	/**
	 * Sets the retry count. In cases of multiple
	 * invocations, only the largest value is stored
	 * 
	 * @param counter the counter. The argument expected is a 
	 * string representation of an integer
	 */
	public void SetRetryCount(String counter)
	{
		if(counter==null) return;
		int c=Integer.parseInt(counter);
		if(c>RetryCount) RetryCount=c;
	}
	
	/**
	 * Sets the retry interval attribute. In cases of multiple
	 * invocations, only the largest value is stored
	 * 
	 * @param interval the interval. The argument expected is a 
	 * string representation of a long
	 */
	public void SetRetryInterval(String interval)
	{
		if(interval==null) return;
		long c=Long.parseLong(interval);
		if(c>RetryInterval) RetryInterval=c;
	}
	
	/**
	 * Sets the node collocation attribute
	 * 
	 * @param collocation the collocation attribute value. The argument expected is a 
	 * string representation of a boolean value 
	 */
	public void SetNodesCollocation(String collocation)
	{
		if(collocation==null) return;
		this.NodesCollocation=Boolean.parseBoolean(collocation);
	}
	
	public String GetCommonRequirements()
	{
		if(this.CommonRequirements!=null) return this.CommonRequirements;
		Set<String> commonRequirements = new HashSet<String>();
		boolean initial = true;
		if(Nodes.isEmpty()) return this.Requirements;
		Map<String, Set<String>> reqs = new HashMap<String, Set<String>>();
		for(Map.Entry<String, ParsedJDLInfo> nodeRequirements : this.Nodes.entrySet())
		{
			Set<String> nodeReqs = new HashSet<String>();
			MatchParser parser = new MatchParser(this.Nodes.get(nodeRequirements.getKey()).Requirements);
			for(Map.Entry<String, String> parsedReq : parser.requirments.entrySet())
				nodeReqs.add(MatchParser.toRequirement(parsedReq.getKey(), parsedReq.getValue()));
			if(initial)
			{
				commonRequirements.addAll(nodeReqs);
				initial=false;
			}
			else
				commonRequirements.retainAll(nodeReqs);
		}
		this.CommonRequirements = MatchParser.toRequirements(commonRequirements);
		return this.CommonRequirements;
	}
	
	public String GetExtraRequirements(String nodeName)
	{
		if(this.ExtraRequirements.containsKey(nodeName)) return this.ExtraRequirements.get(nodeName);
		String cr = GetCommonRequirements();
		MatchParser parser = new MatchParser(cr);
		
		ParsedJDLInfo nodeInfo = this.Nodes.get(nodeName);
		MatchParser nodeParser = new MatchParser(nodeInfo.Requirements);
		
		Set<String> commonRequirements = new HashSet<String>();
		Set<String> nodeRequirements = new HashSet<String>();
		for(Map.Entry<String, String> commonReq : parser.requirments.entrySet())
			commonRequirements.add(MatchParser.toRequirement(commonReq.getKey(), commonReq.getValue()));
		for(Map.Entry<String, String> nodeReq : nodeParser.requirments.entrySet())
			nodeRequirements.add(MatchParser.toRequirement(nodeReq.getKey(), nodeReq.getValue()));
		
		nodeRequirements.removeAll(commonRequirements);
		this.ExtraRequirements.put(nodeName, MatchParser.toRequirements(nodeRequirements));
		return this.ExtraRequirements.get(nodeName);
	}
	
	/**
	 * A draft XML presentation of the information stored in the instance
	 * 
	 * @return the serialization
	 */
	public String ToXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<Type value=\""+this.jobDescriptionType+"\"/>");
		buf.append("<JobType value=\""+this.jobType+"\"/>");
		if(this.Executable!=null)buf.append("<Executable value=\""+this.Executable+"\"/>");
		if(this.Arguments!=null)buf.append("<Arguments value=\""+this.Arguments+"\"/>");
		if(this.Input!=null)buf.append("<Input value=\""+this.Input+"\"/>");
		if(this.Output!=null)buf.append("<Output value=\""+this.Output+"\"/>");
		if(this.Error!=null)buf.append("<Error value=\""+this.Error+"\"/>");
		if(this.InSandbox.size()>0)
		{
			buf.append("<InSandbox>");
			for(String it : InSandbox) buf.append("<entry>"+it+"</entry>");
			buf.append("</InSandbox>");
		}
		if(this.OutSandbox.size()>0)
		{
			buf.append("<OutSandbox>");
			for(String it : OutSandbox) buf.append("<entry>"+it+"</entry>");
			buf.append("</OutSandbox>");
		}
		if(this.Environment.size()>0)
		{
			buf.append("<Environment>");
			for(EnvironmentKeyValue it : Environment) buf.append("<entry><key>"+it.Key+"</key><value>"+it.Value+"</value></entry>");
			buf.append("</Environment>");
		}
		if(this.Rank!=null)buf.append("<Rank>"+this.Rank+"</Rank>");
		if(this.Requirements!=null)buf.append("<Requirements>"+this.Requirements+"</Requirements>");
		if(this.RetryCount>0)buf.append("<RetryCount value=\""+this.RetryCount +"\"/>");
		if(this.NodesCollocation)buf.append("<NodesCollocation value=\""+this.NodesCollocation +"\"/>");
		if(this.jobDescriptionType==JobDescriptionType.DAG)
		{
			buf.append("<ParsingMode value=\""+this.ModeOfParsing.toString()+"\"/>");
			buf.append("<MaxRunningJobs value=\""+this.MaxRunningNodes+"\"/>");
			buf.append("<Nodes>");
			for(Map.Entry<String, ParsedJDLInfo> entry : this.Nodes.entrySet()) buf.append("<node name=\""+entry.getKey()+"\">"+entry.getValue().ToXML()+"</node>");
			buf.append("</Nodes>");
			buf.append("<Dependencies>");
			for(Map.Entry<String, List<String>> entry : this.Dependencies.entrySet())
			{
				buf.append("<dep node=\""+entry.getKey()+"\">");
				for(String waitFor : entry.getValue()) buf.append("<waitFor node=\""+waitFor+"\"/>");
				buf.append("</dep>");
			}
			buf.append("</Dependencies>");
		}
		return buf.toString();
	}
}
