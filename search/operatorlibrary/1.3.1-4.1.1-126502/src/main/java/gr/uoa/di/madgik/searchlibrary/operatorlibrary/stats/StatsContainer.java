package gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats;

import java.io.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

//import org.gcube.common.core.resources.GCUBERunningInstance;

/**
 * Generic class containing statistic information
 * 
 * @author UoA
 */
public class StatsContainer extends Thread{
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(StatsContainer.class.getName());
	/**
	 * Used to synchronize
	 */
	private Object lockMe=new Object();
	/**
	 * Number of invocations
	 */
	private long numberOfInvocations=0;
	/**
	 * Number of fatal errors
	 */
	private long numberOfFatalErrors=0;
	/**
	 * Mean time it took to return the initial result reference
	 */
	private long meanTimeToReference=0;
	/**
	 * max time it took to return the initial result reference
	 */
	private long maxTimeToReference=0;
	/**
	 * min time it took to return the initial result reference
	 */
	private long minTimeToReference=Long.MAX_VALUE;
	/**
	 * Mean time it took to populate the complete output
	 */
	private long meanTimeToComplete=0;
	/**
	 * max time it took to return the complete output
	 */
	private long maxTimeToComplete=0;
	/**
	 * min time it took to return the complete output
	 */
	private long minTimeToComplete=Long.MAX_VALUE;
	/**
	 * Mean time it took to initilalize in single invocation
	 */
	private long meanTimeToInitialize=0;
	/**
	 * max time it took to initilalize in single invocation
	 */
	private long maxTimeToInitialize=0;
	/**
	 * min time it took to initilalize in single invocation
	 */
	private long minTimeToInitialize=Long.MAX_VALUE;
	/**
	 * Mean number of results produced
	 */
	private long meanProducedResults=0;
	/**
	 * max number of results produced
	 */
	private long maxProducedResults=0;
	/**
	 * min number of results produced
	 */
	private long minProducedResults=Long.MAX_VALUE;
	/**
	 * Mean time it took to produce the first result
	 */
	private long meanTimeToFirst=0;
	/**
	 * max time it took to produce the first result
	 */
	private long maxTimeToFirst=0;
	/**
	 * min time it took to produce the first result
	 */
	private long minTimeToFirst=Long.MAX_VALUE;
	/**
	 * Mean production rate
	 */
	private float meanProductionRate=0;
	/**
	 * max production rate
	 */
	private float maxProductionRate=0;
	/**
	 * min production rate
	 */
	private float minProductionRate=Long.MAX_VALUE;
	/**
	 * The running instance to keep statistics for
	 */
	//private GCUBERunningInstance RItoKeepStatsFor=null;
	/**
	 * Mean time it took to read first input
	 */
	private long meanTimeToFirstInput=0;
	/**
	 * max time it took to read the first input
	 */
	private long maxTimeToFirstInput=0;
	/**
	 * min time it took to read the first input
	 */
	private long minTimeToFirstInput=Long.MAX_VALUE;
	
	/**
	 * Constructor
	 * 
	 * @param RItoKeepStatsFor the RI to keep statistics for
	 */
	public StatsContainer(/*GCUBERunningInstance RItoKeepStatsFor*/){
	//	this.RItoKeepStatsFor=RItoKeepStatsFor;
	}

	/**
	 * new invocation
	 */
	public void newInvocation(){
		synchronized(this.lockMe){
			this.numberOfInvocations+=1;
		}
	}
	
	/**
	 * fatal error
	 */
	public void fatalError(){
		synchronized(this.lockMe){
			this.numberOfFatalErrors+=1;
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
			if(this.numberOfInvocations!=0) this.meanTimeToReference=(((this.meanTimeToReference*(this.numberOfInvocations-1))+time)/this.numberOfInvocations);
		}
	}
	
	/**
	 * mean time for complete
	 * 
	 * @param time time in millisecs
	 */
	public void timeToComplete(long time){
		synchronized(this.lockMe){
			if(this.minTimeToComplete>time) this.minTimeToComplete=time;
			if(this.maxTimeToComplete<time) this.maxTimeToComplete=time;
			if(this.numberOfInvocations!=0) this.meanTimeToComplete=(((this.meanTimeToComplete*(this.numberOfInvocations-1))+time)/this.numberOfInvocations);
		}
	}
	
	/**
	 * mean time for initialization
	 * 
	 * @param time time in millisecs
	 */
	public void timeToInitialize(long time){
		synchronized(this.lockMe){
			if(this.minTimeToInitialize>time) this.minTimeToInitialize=time;
			if(this.maxTimeToInitialize<time) this.maxTimeToInitialize=time;
			if(this.numberOfInvocations!=0) this.meanTimeToInitialize=(((this.meanTimeToInitialize*(this.numberOfInvocations-1))+time)/this.numberOfInvocations);
		}
	}
	
	/**
	 * results produced
	 * 
	 * @param results number of results
	 */
	public void producedResults(long results){
		synchronized(this.lockMe){
			if(this.minProducedResults>results) this.minProducedResults=results;
			if(this.maxProducedResults<results) this.maxProducedResults=results;
			if(this.numberOfInvocations!=0) this.meanProducedResults=(((this.meanProducedResults*(this.numberOfInvocations-1))+results)/this.numberOfInvocations);
		}
	}
	
	/**
	 * mean time for first result
	 * 
	 * @param time time in millisecs
	 */
	public void timeToFirst(long time){
		synchronized(this.lockMe){
			if(this.minTimeToFirst>time) this.minTimeToFirst=time;
			if(this.maxTimeToFirst<time) this.maxTimeToFirst=time;
			if(this.numberOfInvocations!=0) this.meanTimeToFirst=(((this.meanTimeToFirst*(this.numberOfInvocations-1))+time)/this.numberOfInvocations);
		}
	}
	
	/**
	 * mean production rate
	 * 
	 * @param time time in millisecs
	 */
	public void productionRate(float time){
		synchronized(this.lockMe){
			if(this.minProductionRate>time) this.minProductionRate=time;
			if(this.maxProductionRate<time) this.maxProductionRate=time;
			if(this.numberOfInvocations!=0) this.meanProductionRate=(((this.meanProductionRate*(this.numberOfInvocations-1))+time)/this.numberOfInvocations);
		}
	}

	/**
	 * mean time for first input
	 * 
	 * @param time time in millisecs
	 */
	public void timeToFirstInput(long time){
		synchronized(this.lockMe){
			if(this.minTimeToFirstInput>time) this.minTimeToFirstInput=time;
			if(this.maxTimeToFirstInput<time) this.maxTimeToFirstInput=time;
			if(this.numberOfInvocations!=0) this.meanTimeToFirstInput=(((this.meanTimeToFirstInput*(this.numberOfInvocations-1))+time)/this.numberOfInvocations);
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
	 * generates an xml serialization of the statistics
	 * 
	 * @return the xml serialization
	 */
	public String toXML(){
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
		buf.append("<NumberOfFatalErrors>");
		buf.append("<value>");
		buf.append(numberOfFatalErrors);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the total number of fatal errors that blocked the serving of an invocation");
		buf.append("</description>");
		buf.append("</NumberOfFatalErrors>");
		buf.append("<MeanTimeToReference>");
		buf.append("<value>");
		buf.append(meanTimeToReference);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time to return an EPR to the results");
		buf.append("</description>");
		buf.append("</MeanTimeToReference>");
		buf.append("<MaxTimeToReference>");
		buf.append("<value>");
		buf.append(maxTimeToReference);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time to return an EPR to the results");
		buf.append("</description>");
		buf.append("</MaxTimeToReference>");
		buf.append("<MinTimeToReference>");
		buf.append("<value>");
		buf.append(minTimeToReference);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time to return an EPR to the results");
		buf.append("</description>");
		buf.append("</MinTimeToReference>");
		buf.append("<MeanTimeToComplete>");
		buf.append("<value>");
		buf.append(meanTimeToComplete);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time to complete the service processing");
		buf.append("</description>");
		buf.append("</MeanTimeToComplete>");
		buf.append("<MaxTimeToComplete>");
		buf.append("<value>");
		buf.append(maxTimeToComplete);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time to complete the service processing");
		buf.append("</description>");
		buf.append("</MaxTimeToComplete>");
		buf.append("<MinTimeToComplete>");
		buf.append("<value>");
		buf.append(minTimeToComplete);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time to complete the service processing");
		buf.append("</description>");
		buf.append("</MinTimeToComplete>");
		buf.append("<MeanTimeToInitialize>");
		buf.append("<value>");
		buf.append(meanTimeToInitialize);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time to Initialize the service processing");
		buf.append("</description>");
		buf.append("</MeanTimeToInitialize>");
		buf.append("<MaxTimeToInitialize>");
		buf.append("<value>");
		buf.append(maxTimeToInitialize);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time to Initialize the service processing");
		buf.append("</description>");
		buf.append("</MaxTimeToInitialize>");
		buf.append("<MinTimeToInitialize>");
		buf.append("<value>");
		buf.append(minTimeToInitialize);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time to Initialize the service processing");
		buf.append("</description>");
		buf.append("</MinTimeToInitialize>");
		buf.append("<MeanProducedResults>");
		buf.append("<value>");
		buf.append(meanProducedResults);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean number of produced results");
		buf.append("</description>");
		buf.append("</MeanProducedResults>");
		buf.append("<MaxProducedResults>");
		buf.append("<value>");
		buf.append(maxProducedResults);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max number of produced results");
		buf.append("</description>");
		buf.append("</MaxProducedResults>");
		buf.append("<MinProducedResults>");
		buf.append("<value>");
		buf.append(minProducedResults);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min number of produced results");
		buf.append("</description>");
		buf.append("</MinProducedResults>");
		buf.append("<MeanTimeToFirst>");
		buf.append("<value>");
		buf.append(meanTimeToFirst);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time to produce the first results");
		buf.append("</description>");
		buf.append("</MeanTimeToFirst>");
		buf.append("<MaxTimeToFirst>");
		buf.append("<value>");
		buf.append(maxTimeToFirst);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time to produce the first results");
		buf.append("</description>");
		buf.append("</MaxTimeToFirst>");
		buf.append("<MinTimeToFirst>");
		buf.append("<value>");
		buf.append(minTimeToFirst);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time to produce the first results");
		buf.append("</description>");
		buf.append("</MinTimeToFirst>");
		buf.append("<MeanProductionRate>");
		buf.append("<value>");
		buf.append(meanProductionRate);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean result production rate in records per second");
		buf.append("</description>");
		buf.append("</MeanProductionRate>");
		buf.append("<MaxProductionRate>");
		buf.append("<value>");
		buf.append(maxProductionRate);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max result production rate in records per second");
		buf.append("</description>");
		buf.append("</MaxProductionRate>");
		buf.append("<MinProductionRate>");
		buf.append("<value>");
		buf.append(minProductionRate);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min result production rate in records per second");
		buf.append("</description>");
		buf.append("</MinProductionRate>");
		buf.append("<MeanTimeToFirstInput>");
		buf.append("<value>");
		buf.append(meanTimeToFirstInput);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the mean time needed to read the first input record");
		buf.append("</description>");
		buf.append("</MeanTimeToFirstInput>");
		buf.append("<MaxTimeToFirstInput>");
		buf.append("<value>");
		buf.append(maxTimeToFirstInput);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the max time needed to read the first input record");
		buf.append("</description>");
		buf.append("</MaxTimeToFirstInput>");
		buf.append("<MinTimeToFirstInput>");
		buf.append("<value>");
		buf.append(minTimeToFirstInput);
		buf.append("</value>");
		buf.append("<description>");
		buf.append("the min time needed to read the first input record");
		buf.append("</description>");
		buf.append("</MinTimeToFirstInput>");
		buf.append("</Statistics>");
		return buf.toString();
	}
	
	public void fromXML(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document xmlDoc = db.parse(new InputSource(new StringReader(xml)));
		Element xmlDocEl = xmlDoc.getDocumentElement();
		numberOfInvocations = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("NumberOfInvocations").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		numberOfFatalErrors = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("NumberOfFatalErrors").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanTimeToReference = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MeanTimeToReference").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minTimeToReference = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MinTimeToReference").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxTimeToReference = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MaxTimeToReference").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanTimeToComplete = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MeanTimeToComplete").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minTimeToComplete = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MinTimeToComplete").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxTimeToComplete = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MaxTimeToComplete").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanTimeToInitialize = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MeanTimeToInitialize").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minTimeToInitialize = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MinTimeToInitialize").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxTimeToInitialize = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MaxTimeToInitialize").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanProducedResults = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MeanProducedResults").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minProducedResults = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MinProducedResults").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxProducedResults = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MaxProducedResults").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanTimeToFirst = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MeanTimeToFirst").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minTimeToFirst = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MinTimeToFirst").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxTimeToFirst = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MaxTimeToFirst").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanProductionRate = Float.parseFloat(((Element)xmlDocEl.getElementsByTagName("MeanProductionRate").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minProductionRate = Float.parseFloat(((Element)xmlDocEl.getElementsByTagName("MinProductionRate").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxProductionRate = Float.parseFloat(((Element)xmlDocEl.getElementsByTagName("MaxProductionRate").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		meanTimeToFirstInput = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MeanTimeToFirstInput").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		minTimeToFirstInput = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MinTimeToFirstInput").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
		maxTimeToFirstInput = Long.parseLong(((Element)xmlDocEl.getElementsByTagName("MaxTimeToFirstInput").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue());
	}
}
