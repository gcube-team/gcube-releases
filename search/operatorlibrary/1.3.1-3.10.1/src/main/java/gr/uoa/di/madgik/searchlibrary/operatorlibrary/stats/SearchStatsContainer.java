package gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.gcube.common.core.resources.GCUBERunningInstance;


/**
 * Statistics
 * 
 * @author UoA
 */
public class SearchStatsContainer extends Thread{
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(SearchStatsContainer.class.getName());
	/**
	 * Used to synchronize
	 */
	private Object lockMe=new Object();
	/**
	 * Number of invocations
	 */
	private long numberOfInvocations=0;
	/**
	 * Number of configuration invocation
	 */
	private long numberOfConfigurations=0;
	/**
	 * Parsing errors
	 */
	private long parseErrors=0;
	/**
	 * Parsing errors
	 */
	private long validationErrors=0;
	/**
	 * preprocess Error
	 */
	private long preprocessErrors=0;
	/**
	 * planning Error
	 */
	private long planningErrors=0;
	/**
	 * execution Error
	 */
	private long executionErrors=0;
	/**
	 * number of executions through pes
	 */
	private long pesExecution=0;
	/**
	 * number of executions through internal engine
	 */
	private long internalExecution=0;
	/**
	 * Mean time it took to return the initial result referense
	 */
	private long meanTimeToReference=0;
	/**
	 * max time it took to return the initial result referense
	 */
	private long maxTimeToReference=0;
	/**
	 * min time it took to return the initial result referense
	 */
	private long minTimeToReference=Long.MAX_VALUE;
	/**
	 * Mean time it took to return the initial result referense
	 */
	private long meanTimeToParse=0;
	/**
	 * max time it took to return the initial result referense
	 */
	private long maxTimeToParse=0;
	/**
	 * min time it took to return the initial result referense
	 */
	private long minTimeToParse=Long.MAX_VALUE;
	/**
	 * Mean time it took to return the initial result referense
	 */
	private long meanTimeToValidate=0;
	/**
	 * max time it took to return the initial result referense
	 */
	private long maxTimeToValidate=0;
	/**
	 * min time it took to return the initial result referense
	 */
	private long minTimeToValidate=Long.MAX_VALUE;
	/**
	 * Mean time it took to return the initial result referense
	 */
	private long meanTimeToPreprocess=0;
	/**
	 * max time it took to return the initial result referense
	 */
	private long maxTimeToPreprocess=0;
	/**
	 * min time it took to return the initial result referense
	 */
	private long minTimeToPreprocess=Long.MAX_VALUE;
	/**
	 * Mean time it took to return the initial result referense
	 */
	private long meanTimeToPlan=0;
	/**
	 * max time it took to return the initial result referense
	 */
	private long maxTimeToPlan=0;
	/**
	 * min time it took to return the initial result referense
	 */
	private long minTimeToPlan=Long.MAX_VALUE;
	/**
	 * Mean time it took to return the initial result referense
	 */
	private long meanTimeToExecute=0;
	/**
	 * max time it took to return the initial result referense
	 */
	private long maxTimeToExecute=0;
	/**
	 * min time it took to return the initial result referense
	 */
	private long minTimeToExecute=Long.MAX_VALUE;
	/**
	 * The running instance to keep statistics for
	 */
	//private GCUBERunningInstance RItoKeepStatsFor=null;
	
	/**
	 * Constructor
	 * 
	 * @param RItoKeepStatsFor the RI to keep statistics for
	 */
	public SearchStatsContainer(/*GCUBERunningInstance RItoKeepStatsFor*/){
	//	this.RItoKeepStatsFor=RItoKeepStatsFor;
	}
	
	/**
	 * New invocation
	 */
	public void newInvocation(){
		synchronized(this.lockMe){
			this.numberOfInvocations+=1;
		}
	}
	
	/**
	 * New configuration invocation
	 */
	public void newConfiguration(){
		synchronized(this.lockMe){
			this.numberOfConfigurations+=1;
		}
	}
	
	/**
	 * parsing error
	 */
	public void parseError(){
		synchronized(this.lockMe){
			this.parseErrors+=1;
		}
	}
	
	/**
	 * validation error
	 */
	public void validationError(){
		synchronized(this.lockMe){
			this.validationErrors+=1;
		}
	}
	
	/**
	 * preprocess Error
	 */
	public void preprocessError(){
		synchronized(this.lockMe){
			this.preprocessErrors+=1;
		}
	}
	
	/**
	 * planning Error
	 */
	public void planningError(){
		synchronized(this.lockMe){
			this.planningErrors+=1;
		}
	}
	
	/**
	 * execution Error
	 */
	public void executionError(){
		synchronized(this.lockMe){
			this.executionErrors+=1;
		}
	}
	
	/**
	 * execute through internal engine
	 */
	public void executeInternal(){
		synchronized(this.lockMe){
			this.internalExecution+=1;
		}
	}
	
	/**
	 * execute through pes
	 */
	public void executePES(){
		synchronized(this.lockMe){
			this.pesExecution+=1;
		}
	}
	
	/**
	 * mean time for reference
	 * 
	 * @param time time in millisecs
	 */
	public void timeToReference(long time){
		synchronized(this.lockMe){
			if(this.minTimeToReference>time) this.minTimeToReference=time;
			if(this.maxTimeToReference<time) this.maxTimeToReference=time;
			if(this.numberOfInvocations-numberOfConfigurations!=0) this.meanTimeToReference=(((this.meanTimeToReference*(this.numberOfInvocations-numberOfConfigurations-1))+time)/(this.numberOfInvocations-numberOfConfigurations));
		}
	}
	
	/**
	 * mean time for parse
	 * 
	 * @param time time in millisecs
	 */
	public void timeToParse(long time){
		synchronized(this.lockMe){
			if(this.minTimeToParse>time) this.minTimeToParse=time;
			if(this.maxTimeToParse<time) this.maxTimeToParse=time;
			if((this.numberOfInvocations-numberOfConfigurations)!=0) this.meanTimeToParse=(((this.meanTimeToParse*(this.numberOfInvocations-numberOfConfigurations-1))+time)/(this.numberOfInvocations-numberOfConfigurations));
		}
	}
	
	/**
	 * mean time for validation
	 * 
	 * @param time time in millisecs
	 */
	public void timeToValidate(long time){
		synchronized(this.lockMe){
			if(this.minTimeToValidate>time) this.minTimeToValidate=time;
			if(this.maxTimeToValidate<time) this.maxTimeToValidate=time;
			if((this.numberOfInvocations-numberOfConfigurations)!=0) this.meanTimeToValidate=(((this.meanTimeToValidate*(this.numberOfInvocations-numberOfConfigurations-1))+time)/(this.numberOfInvocations-numberOfConfigurations));
		}
	}
	
	/**
	 * mean time for preprocessing
	 * 
	 * @param time time in millisecs
	 */
	public void timeToPreprocess(long time){
		synchronized(this.lockMe){
			if(this.minTimeToPreprocess>time) this.minTimeToPreprocess=time;
			if(this.maxTimeToPreprocess<time) this.maxTimeToPreprocess=time;
			if((this.numberOfInvocations-numberOfConfigurations)!=0) this.meanTimeToPreprocess=(((this.meanTimeToPreprocess*(this.numberOfInvocations-numberOfConfigurations-1))+time)/(this.numberOfInvocations-numberOfConfigurations));
		}
	}
	
	/**
	 * mean time for plan
	 * 
	 * @param time time in millisecs
	 */
	public void timeToPlan(long time){
		synchronized(this.lockMe){
			if(this.minTimeToPlan>time) this.minTimeToPlan=time;
			if(this.maxTimeToPlan<time) this.maxTimeToPlan=time;
			if((this.numberOfInvocations-numberOfConfigurations)!=0) this.meanTimeToPlan=(((this.meanTimeToPlan*(this.numberOfInvocations-numberOfConfigurations-1))+time)/(this.numberOfInvocations-numberOfConfigurations));
		}
	}
	
	/**
	 * mean time for execution
	 * 
	 * @param time time in millisecs
	 */
	public void timeToExecute(long time){
		synchronized(this.lockMe){
			if(this.minTimeToExecute>time) this.minTimeToExecute=time;
			if(this.maxTimeToExecute<time) this.maxTimeToExecute=time;
			if((this.numberOfInvocations-numberOfConfigurations)!=0) this.meanTimeToExecute=(((this.meanTimeToExecute*(this.numberOfInvocations-numberOfConfigurations-1))+time)/(this.numberOfInvocations-numberOfConfigurations));
		}
	}
	
	/**
	 * Writes the statistics to the RI profile
	 */
	public void writeToRI() {
		//RItoKeepStatsFor.setSpecificData(this.toXML());
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(1200000); //20 minutes
			}catch(Exception e){}
			try{
				this.writeToRI();
			}catch(Exception e){
				logger.error("could not update RI profile.continuing",e);
			}
		}
	}
	
	/**
	 * generates an xml representation of the statistics
	 * 
	 * @return the xml representation
	 */
	private String toXML(){
		StringBuilder buf=new StringBuilder();
		buf.append("<Statistics>");
		buf.append("<text>");
		buf.append("this section summarizes some statisitc information for the service since the last restart");
		buf.append("</text>");
		buf.append("<NumberOfInvocations>");
		buf.append("<value>");
		buf.append(numberOfInvocations);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of calls made to the service");
		buf.append("</description>");
		buf.append("</NumberOfInvocations>");
		buf.append("<NumberOfConfigurations>");
		buf.append("<value>");
		buf.append(numberOfConfigurations);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of configuration retrieval calls");
		buf.append("</description>");
		buf.append("</NumberOfConfigurations>");
		buf.append("<ParseErrors>");
		buf.append("<value>");
		buf.append(parseErrors);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of query parsing errors");
		buf.append("</description>");
		buf.append("</ParseErrors>");
		buf.append("<ValidationErrors>");
		buf.append("<value>");
		buf.append(validationErrors);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of query validation errors");
		buf.append("</description>");
		buf.append("</ValidationErrors>");
		buf.append("<PreprocessErrors>");
		buf.append("<value>");
		buf.append(preprocessErrors);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of query preprocessing errors");
		buf.append("</description>");
		buf.append("</PreprocessErrors>");
		buf.append("<PlanningErrors>");
		buf.append("<value>");
		buf.append(planningErrors);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of query planning errors");
		buf.append("</description>");
		buf.append("</PlanningErrors>");
		buf.append("<ExecutionErrors>");
		buf.append("<value>");
		buf.append(executionErrors);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of plan execution errors");
		buf.append("</description>");
		buf.append("</ExecutionErrors>");
		buf.append("<PESExecution>");
		buf.append("<value>");
		buf.append(pesExecution);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of plan executions from the PES");
		buf.append("</description>");
		buf.append("</PESExecution>");
		buf.append("<InternalExecution>");
		buf.append("<value>");
		buf.append(internalExecution);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of plan executions from the internal engine");
		buf.append("</description>");
		buf.append("</InternalExecution>");
		buf.append("<MeanTimeToReference>");
		buf.append("<value>");
		buf.append(meanTimeToReference);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time in millisecs to return with a EPR to the results");
		buf.append("</description>");
		buf.append("</MeanTimeToReference>");
		buf.append("<MaxTimeToReference>");
		buf.append("<value>");
		buf.append(maxTimeToReference);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the maximum time in millisecs to return with a EPR to the results");
		buf.append("</description>");
		buf.append("</MaxTimeToReference>");
		buf.append("<MinTimeToReference>");
		buf.append("<value>");
		buf.append(minTimeToReference);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the minimum time in millisecs to return with a EPR to the results");
		buf.append("</description>");
		buf.append("</MinTimeToReference>");
		buf.append("<MeanTimeToParse>");
		buf.append("<value>");
		buf.append(meanTimeToParse);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time in millisecs to parse the query");
		buf.append("</description>");
		buf.append("</MeanTimeToParse>");
		buf.append("<MaxTimeToParse>");
		buf.append("<value>");
		buf.append(maxTimeToParse);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time in millisecs to parse the query");
		buf.append("</description>");
		buf.append("</MaxTimeToParse>");
		buf.append("<MinTimeToParse>");
		buf.append("<value>");
		buf.append(minTimeToParse);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time in millisecs to parse the query");
		buf.append("</description>");
		buf.append("</MinTimeToParse>");
		buf.append("<MeanTimeToValidate>");
		buf.append("<value>");
		buf.append(meanTimeToValidate);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time in millisecs to validate the query");
		buf.append("</description>");
		buf.append("</MeanTimeToValidate>");
		buf.append("<MaxTimeToValidate>");
		buf.append("<value>");
		buf.append(maxTimeToValidate);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time in millisecs to validate the query");
		buf.append("</description>");
		buf.append("</MaxTimeToValidate>");
		buf.append("<MinTimeToValidate>");
		buf.append("<value>");
		buf.append(minTimeToValidate);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time in millisecs to validate the query");
		buf.append("</description>");
		buf.append("</MinTimeToValidate>");
		buf.append("<MeanTimeToPreprocess>");
		buf.append("<value>");
		buf.append(meanTimeToPreprocess);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time in millisecs to preprocess the query");
		buf.append("</description>");
		buf.append("</MeanTimeToPreprocess>");
		buf.append("<MaxTimeToPreprocess>");
		buf.append("<value>");
		buf.append(maxTimeToPreprocess);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time in millisecs to preprocess the query");
		buf.append("</description>");
		buf.append("</MaxTimeToPreprocess>");
		buf.append("<MinTimeToPreprocess>");
		buf.append("<value>");
		buf.append(minTimeToPreprocess);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time in millisecs to preprocess the query");
		buf.append("</description>");
		buf.append("</MinTimeToPreprocess>");
		buf.append("<MeanTimeToPlan>");
		buf.append("<value>");
		buf.append(meanTimeToPlan);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time in millisecs to plan the query");
		buf.append("</description>");
		buf.append("</MeanTimeToPlan>");
		buf.append("<MaxTimeToPlan>");
		buf.append("<value>");
		buf.append(maxTimeToPlan);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time in millisecs to plan the query");
		buf.append("</description>");
		buf.append("</MaxTimeToPlan>");
		buf.append("<MinTimeToPlan>");
		buf.append("<value>");
		buf.append(minTimeToPlan);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time in millisecs to plan the query");
		buf.append("</description>");
		buf.append("</MinTimeToPlan>");
		buf.append("<MeanTimeToExecute>");
		buf.append("<value>");
		buf.append(meanTimeToExecute);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time in millisecs to execute the plan");
		buf.append("</description>");
		buf.append("</MeanTimeToExecute>");
		buf.append("<MaxTimeToExecute>");
		buf.append("<value>");
		buf.append(maxTimeToExecute);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time in millisecs to execute the plan");
		buf.append("</description>");
		buf.append("</MaxTimeToExecute>");
		buf.append("<MinTimeToExecute>");
		buf.append("<value>");
		buf.append(minTimeToExecute);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time in millisecs to execute the plan");
		buf.append("</description>");
		buf.append("</MinTimeToExecute>");
		buf.append("</Statistics>");
		return buf.toString();
	}
}
