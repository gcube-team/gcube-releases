package org.gcube.common.searchservice.searchlibrary.rswriter;

import java.util.Timer;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementWellFormed;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.ForceProductionTask;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject;

/**
 * This class provides basic functionality to author a local {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class RSTEXTWriter extends RSPoolObject {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSTEXTWriter.class);
	/**
	 * Used for synchronization
	 */
	private Object synchAddition=null;
	/**
	 * The writer to use
	 */
	private RSFullWriter writer=null;
	/**
	 * The default number of reslts per part
	 */
	private int recsPerCount=20;
	/**
	 * Teh default part size
	 */
	private int partSize=102400;
	/**
	 * Record buffer
	 */
	private Vector<String> records=null;
	/**
	 * The current size
	 */
	private int size=0;
	/**
	 * Timer instance
	 */
	private Timer timer=null;
	/**
	 * The production task
	 */
	private ForceProductionTask task=null;
	/**
	 * Wheteher or not the produced parts should be well formed 
	 */
	private boolean wellformed=true;
	
	/**
	 * Creates a new {@link RSTEXTWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified}  declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param wellformed Whether or not the created payload part should be wellformed
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(boolean wellformed) throws Exception{
		try{
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.TEXT),wf}),wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSTEXTWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified}
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param wellformed Whether or not the created payload part should be wellformed
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(boolean wellformed,boolean dataFlow) throws Exception{
		try{
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.TEXT),wf},dataFlow),wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSTEXTWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} and setting the desired number of results per 
	 * content part and the maximum size of the part declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size of the part
	 * @param wellformed Whether or not the produced xontent part should be welformed
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(int recsPerPart,int partSize,boolean wellformed) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.TEXT),wf}),recsPerPart,partSize,wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} and setting the desired number of results per 
	 * content part and the maximum size of the part
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size of the part
	 * @param wellformed Whether or not the produced xontent part should be welformed
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(int recsPerPart,int partSize,boolean wellformed,boolean dataFlow) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.TEXT),wf},dataFlow),recsPerPart,partSize,wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param wellformed Whether or not the produced xontent part should be welformed
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(PropertyElementBase []properties,boolean wellformed) throws Exception{
		try{
			PropertyElementBase []props=new PropertyElementBase[properties.length+2];
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.TEXT);
			props[properties.length+1]=wf;
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(props),wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param wellformed Whether or not the produced xontent part should be welformed
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(PropertyElementBase []properties,boolean wellformed,boolean dataFlow) throws Exception{
		try{
			PropertyElementBase []props=new PropertyElementBase[properties.length+2];
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.TEXT);
			props[properties.length+1]=wf;
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(props,dataFlow),wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones and setting the desired number of results per content part
	 * declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size each part should have
	 * @param wellformed Whether or not the payload should be welformed
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(PropertyElementBase []properties,int recsPerPart,int partSize,boolean wellformed) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.TEXT);
			props[properties.length+1]=wf;
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(props),recsPerPart,partSize,wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones and setting the desired number of results per content part
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size each part should have
	 * @param wellformed Whether or not the payload should be welformed
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSTEXTWriter getRSTEXTWriter(PropertyElementBase []properties,int recsPerPart,int partSize,boolean wellformed,boolean dataFlow) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			PropertyElementWellFormed wf=null;
			if(wellformed)  wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.TEXT);
			props[properties.length+1]=wf;
			return new RSTEXTWriter(RSFullWriter.getRSFullWriter(props,dataFlow),recsPerPart,partSize,wellformed);
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} using the default number of results per part 
	 * 
	 * @param writer The {@link RSFullWriter} tat must be used to perform low level operations
	 * @param wellformed Whether or not the payload should be welformed
	 */
	protected RSTEXTWriter(RSFullWriter writer,boolean wellformed){
		this.writer=writer;
		this.records=new Vector<String>();
		this.synchAddition=new Object();
		this.wellformed=wellformed;
	}
	
	/**
	 * Creates a new {@link RSTEXTWriter} seting the number of results per part 
	 * 
	 * @param writer The {@link RSFullWriter} tat must be used to perform low level operations
	 * @param recsPerPart The number of results per part to be used
	 * @param partSize The size each part should have
	 * @param wellformed Whether or not the payload should be welformed
	 */
	protected RSTEXTWriter(RSFullWriter writer,int recsPerPart,int partSize,boolean wellformed){
		this.writer=writer;
		this.records=new Vector<String>();
		this.recsPerCount=recsPerPart;
		this.partSize=partSize;
		this.synchAddition=new Object();
		this.wellformed=wellformed;
	}
	
	/**
	 * Sets a timer task to ensure part creation
	 * 
	 * @param delay The dealy after which the timer should start operating
	 * @param period The period between each time the timer task starts
	 * @param times The number of times the timer task should be executed
	 * @return <code>true</code> if the timer was set, <code>false</code> otherwise
	 */
	public boolean setTimer(long delay,long period,int times){
		if(this.timer!=null || this.task!=null || times<=0) return false;
		try{
			timer=new Timer();
			task=new ForceProductionTask(this,this.synchAddition,times);
			timer.schedule(task,delay,period);
			return true;
		}catch(Exception e){
			log.error("Caught Exception while trying to create Timer. returning false",e);
			return false;
		}
	}
	
	/**
	 * Stops the timer if it was active and cleans its footprint so that it can be started again
	 * 
	 * @return <code>true</code> if the timer was set and cleared, <code>false</code> otherwise
	 */
	public boolean resetTimer(){
		if(this.timer==null && this.task==null) return false;
		if(this.task!=null) task.cancel();
		if(this.timer!=null) timer.cancel();
		this.timer=null;
		this.task=null;
		return true;
	}
	
	/**
	 * Checks if the timer task is still active
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 */
	public boolean isTimerAlive(){
		if(this.timer==null || this.task==null) return false;
		return this.task.isAlive();
	}

	/**
	 * Sets the number of results per part to be used. A call to this method will only affect the 
	 * parts that will be created from the time of the invocation and onwards, and will take affect
	 * in the next invocation to the {@link RSTEXTWriter#addResults(ResultElementBase)} or
	 * {@link RSTEXTWriter#addResults(ResultElementBase[])}
	 * 
	 * @param rpp The number of results per part to use
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void setRecsPerPart(int rpp) throws Exception{
		if(rpp<=0){
			log.error("Could not set RecsPerPart. Illegal value "+rpp);
			throw new Exception("Could not set RecsPerPart. Illegal value "+rpp);
		}
		this.recsPerCount=rpp;
	}
	
	/**
	 * Sets the size per part to be used. A call to this method will only affect the 
	 * parts that will be created from the time of the invocation and onwards, and will take affect
	 * in the next invocation to the {@link RSTEXTWriter#addResults(ResultElementBase)} or
	 * {@link RSTEXTWriter#addResults(ResultElementBase[])}
	 * 
	 * @param partSize The part size that should be used
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void setPartSize(int partSize) throws Exception{
		if(partSize<=0){
			log.error("Could not set PartSize. Illegal value "+partSize);
			throw new Exception("Could not set PartSize. Illegal value "+partSize);
		}
		this.partSize=partSize;
	}
	
	
	/**
	 * Retrieves an {@link RSLocator} to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} beeing authored
	 * @see RSFullWriter#getRSLocator(RSResourceType)
	 * 
	 * @param type The type of {@link RSLocator} to be retrieved
	 * @param scope The RS scope
	 * @return The created{@link RSLocator}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSLocator getRSLocator(RSResourceType type, GCUBEScope scope) throws Exception{
		try{
			synchronized(synchAddition){
				return writer.getRSLocator(type, scope);
			}
		}catch(Exception e){
			log.error("Could not retrieve RSLocator. Throwing Exception",e);
			throw new Exception("Could not retrieve RSLocator");
		}
	}

	
	/**
	 * Retrieves an {@link RSLocator} to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} beeing authored
	 * @see RSFullWriter#getRSLocator(RSResourceType)
	 * 
	 * @param type The type of {@link RSLocator} to be retrieved
	 * @return The created{@link RSLocator}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSLocator getRSLocator(RSResourceType type) throws Exception{
		try{
			synchronized(synchAddition){
				return writer.getRSLocator(type);
			}
		}catch(Exception e){
			log.error("Could not retrieve RSLocator. Throwing Exception",e);
			throw new Exception("Could not retrieve RSLocator");
		}
	}
	
	/**
	 * Checks whether or not more results should be produced. THis operation blocks until
	 * an answer can be produced. This answer will define either that more results are nessecary,
	 * or the producer can relinguish any resources it has for producing more results because the 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} is no longer
	 * used, or the timeout the user specified has been reached
	 * 
	 * @param time The timeout for the answer
	 * @return The answer
	 * @throws Exception An unrecoverabvle for the operation error has occured
	 */
	public RSConstants.CONTROLFLOW more(long time) throws Exception{
		try{
			return this.writer.more(time);
		}catch(Exception e){
			log.error("Could not check if more records are needed. Throwing Exception",e);
			throw new Exception("Could not check if more records are needed");
		}
	}

	/**
	 * Checks whether the underlying RS is beeing fully produced by its author or is incrementaly
	 * updated on request
	 * 
	 * @return <code>true</code> if results are generated on request, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFlowControled() throws Exception{
		return this.writer.isFlowControled();
	}

	/**
	 * Adds the provided result in the underlying {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}.
	 * If with this addition the records per part has been reached or the part size has been reached, the cashed records are stored in the current part and a new 
	 * part is created. Otherwise the result is cashed  
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param record The result to add
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void addResults(String record) throws Exception{
		if(this.records==null){
			log.error("records vector is null. Incorrect initialization incorrect. Throwing Exception");
			throw new Exception("records vector is null. Incorrect initialization incorrect");
		}
		synchronized(synchAddition){
			try{
				this.size+=record.getBytes().length;
				if(this.records.size()+1>=this.recsPerCount){
					records.add(record);
					if(this.wellformed){
						writer.addText("<"+RSConstants.HeadTag+">"+(Integer.toString(records.size()))+"</"+RSConstants.HeadTag+">");
						writer.addText("<"+RSConstants.ResultSetTag+">");
					}
					else{
						writer.addText(Integer.toString(records.size()));
					}
					writer.addText(records.toArray(new String[0]));
					if(this.wellformed){
						writer.addText("</"+RSConstants.ResultSetTag+">");
					}
					writer.startNewPart();
					this.size=0;
					records.clear();
				}
				else{
					this.records.add(record);
				}
				if(this.size>this.partSize) this.writeThrough();
			}catch(Exception e){
				log.error("Could not perform addition. Throwing Exception",e);
				throw new Exception("Could not perform addition");
			}
		}
	}

	/**
	 * This operation flushes any cahed records the {@link RSTEXTWriter} has stored, and creates a
	 * new part to store the next results in the underlying {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}.
	 * If there were no cached results to add, nothing is altered.
	 * 
	 * @return <code>true</code> if the operation did perform some addition, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public boolean writeThrough() throws Exception{
		if(this.records==null){
			log.error("records vector is null. Incorrect initialization incorrect. Throwing Exception");
			throw new Exception("records vector is null. Incorrect initialization incorrect");
		}
		synchronized(synchAddition){
			try{
				if(this.records.size()>0){
					if(this.wellformed){
						writer.addText("<"+RSConstants.HeadTag+">"+(Integer.toString(records.size()))+"</"+RSConstants.HeadTag+">");
						writer.addText("<"+RSConstants.ResultSetTag+">");
					}
					else{
						writer.addText(Integer.toString(records.size()));
					}
					writer.addText(records.toArray(new String[0]));
					if(this.wellformed){
						writer.addText("</"+RSConstants.ResultSetTag+">");
					}
					writer.startNewPart();
					this.size=0;
					records.clear();
					return true;
				}
				return false;
			}catch(Exception e){
				log.error("Could not perform writeTrhough. Throwing Exception",e);
				throw new Exception("Could not perform writeTrhough");
			}
		}
	}
	
	/**
	 * This operation first performs a call to {@link RSTEXTWriter#writeThrough()} and if that call
	 * returned <code>false</code>, creates a new part in the underlying 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void force() throws Exception{
		try{
			if(!this.writeThrough()){
				writer.startNewPart();
			}
		}catch(Exception e){
			log.error("Could not perform force. Throwing Exception",e);
			throw new Exception("Could not perform force");
		}
	}
	
	/**
	 * Adds the provided results in the underlyoing {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}.
	 * If with this addition the records per part has been reached or the part size has been reached or exceeded, the cashed records are stored in the current part and a new 
	 * part is created. If the cashed records are still over the defined records per part, this will be repeated. Otherwise the results are cashed 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param record The result to add
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void addResults(String []record) throws Exception{
		if(this.records==null){
			log.error("records vector is null. Incorrect initialization incorrect. Throwing Exception");
			throw new Exception("records vector is null. Incorrect initialization incorrect");
		}
		synchronized(synchAddition){
			try{
				for(int i=0;i<record.length;i+=1){
					this.addResults(record[i]);
				}
			}catch(Exception e){
				log.error("Could not perform addition. Throwing Exception",e);
				throw new Exception("Could not perform addition");
			}
		}
	}
	
	/**
	 * Ends the authoring to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} after flushing
	 * the remaing records, if any using {@link RSTEXTWriter#addResults(ResultElementBase[])}
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#endAuthoring()
	 * 
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void close() throws Exception{
		if(this.records==null){
			this.resetTimer();
			log.error("records vector is null. Incorrect initialization incorrect. Throwing Exception");
			throw new Exception("records vector is null. Incorrect initialization incorrect");
		}
		this.resetTimer();
		synchronized(synchAddition){
			try{
				if(this.records.size()>0){
					if(this.wellformed){
						writer.addText("<"+RSConstants.HeadTag+">"+(Integer.toString(records.size()))+"</"+RSConstants.HeadTag+">");
						writer.addText("<"+RSConstants.ResultSetTag+">");
					}
					else{
						writer.addText(Integer.toString(records.size()));
					}
					writer.addText(records.toArray(new String[0]));
					if(this.wellformed){
						writer.addText("</"+RSConstants.ResultSetTag+">");
					}
					this.size=0;
					records.clear();
				}
				this.force();
				writer.endAuthoring();
			}catch(Exception e){
				log.error("Could not perform addition. Throwing Exception",e);
				throw new Exception("Could not perform addition");
			}
		}
	}

	/**
	 * Wraps the provided file in the authored {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param filename The name of the file t wrap
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void wrapFile(String filename) throws Exception{
		try{
			writer.wrapFile(filename);
		}catch(Exception e){
			log.error("Could not wrap file. Throwing Exception",e);
			throw new Exception("Could not wrap file");
		}
	}

	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties the properties to set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void overrideProperties(PropertyElementBase []properties) throws Exception{
		try{
			writer.overrideProperties(properties);
		}catch(Exception e){
			log.error("could not override properties. Throwing Exception",e);
			throw new Exception("could not override properties");
		}
	}

	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties the properties to set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void overrideProperties(String properties) throws Exception{
		try{
			writer.overrideProperties(properties);
		}catch(Exception e){
			log.error("could not override properties. Throwing Exception",e);
			throw new Exception("could not override properties");
		}
	}
}
