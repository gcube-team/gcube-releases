package org.gcube.common.searchservice.searchlibrary.rswriter;

import java.io.File;
import java.io.StringReader;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementURIValidation;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementXSDContentValidation;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.ForceProductionTask;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject;

/**
 * This class provides basic functionality to author a local {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class RSXMLWriter extends RSPoolObject {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSXMLWriter.class);
	/**
	 * Used for synchronization
	 */
	private Object synchAddition=null;
	/**
	 * The writer to use
	 */
	private RSFullWriter writer=null;
	/**
	 * Record buffer
	 */
	private Vector<ResultElementBase> records=null;
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
	 * dominant Validation xsd
	 */
	private Validator validationElement=null;
	
	int recsPerCount=20;
	int partSize=102400;
	private RSWriterCreationParams attributes = null;

	/**
	 * Create a new RSXMLwriter
	 * @param intParams Creation parameters
	 * @return the created writer
	 * @throws Exception the creation failed
	 */
	public static RSXMLWriter getRSXMLWriter(RSWriterCreationParams intParams) throws Exception{
		intParams.properties.add(new PropertyElementGC(PropertyElementGC.unspecified));
		intParams.properties.add(new PropertyElementType(PropertyElementType.XML));
		return new RSXMLWriter(intParams, RSFullWriter.getRSFullWriter(intParams));
	}


	/**
	 * Creates a new {@link RSXMLWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter() throws Exception{
		try{
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.XML)}),null);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSXMLWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified}
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(boolean dataFlow) throws Exception{
		try{
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.XML)},dataFlow),null);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSXMLWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} and setting the desired number of results per 
	 * content part declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size of each part
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(int recsPerPart,int partSize) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			return new RSXMLWriter(
					RSFullWriter.getRSFullWriter(
							new PropertyElementBase[]{
									new PropertyElementGC(PropertyElementGC.unspecified),
									new PropertyElementType(PropertyElementType.XML)
									}
							),
							recsPerPart,partSize,null);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}

	/**
	 * Creates a new {@link RSXMLWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} and setting the desired number of results per 
	 * content part
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size of each part
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(int recsPerPart,int partSize,boolean dataFlow) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.XML)},dataFlow),recsPerPart,partSize,null);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}

	/**
	 * Creates a new {@link RSXMLWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(PropertyElementBase []properties) throws Exception{
		try{
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.XML);
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(props),properties);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}

	/**
	 * Creates a new {@link RSXMLWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(PropertyElementBase []properties,boolean dataFlow) throws Exception{
		try{
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.XML);
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(props,dataFlow),properties);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}

	/**
	 * Creates a new {@link RSXMLWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones and setting the desired number of results per content part and size per content part
	 * declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size of each part
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(PropertyElementBase []properties,int recsPerPart,int partSize) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.XML);
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(props),recsPerPart,partSize,properties);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}

	/**
	 * Creates a new {@link RSXMLWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones and setting the desired number of results per content part and size per content part
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @param recsPerPart The number of results that must be stored in each content part
	 * @param partSize The size of each part
	 * @param dataFlow <code>true</code> if the results should be produced on demand, <code>false</code> otherwise
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static RSXMLWriter getRSXMLWriter(PropertyElementBase []properties,int recsPerPart,int partSize,boolean dataFlow) throws Exception{
		try{
			if(recsPerPart<=0 || partSize<=0){
				log.error("One or more Illegal values in "+recsPerPart+" or "+partSize);
				throw new Exception("One or more Illegal values in "+recsPerPart+" or "+partSize);
			}
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.XML);
			return new RSXMLWriter(RSFullWriter.getRSFullWriter(props,dataFlow),recsPerPart,partSize,properties);
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}
	
	/**
	 * retrieves a validator 
	 * 
	 * @param properties the properties
	 * @throws Exception could not instantiate validator
	 */
	private void getValidator(PropertyElementBase []properties) throws Exception{
		if(properties==null || properties.length==0){
			this.validationElement=null;
			return;
		}
		String xsd=null;
		for(int i=0;i<properties.length;i+=1){
			if(properties[i] instanceof PropertyElementURIValidation){
				xsd=new String(RSFileHelper.getBytesFromFile(new File(((PropertyElementURIValidation)properties[i]).getURIxsd())));
			}
			else if(properties[i] instanceof PropertyElementURIValidation){
				xsd=new String(((PropertyElementXSDContentValidation)properties[i]).getContentXSD());
			}
		}
		if(xsd==null){
			this.validationElement=null;
			return;
		}
		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		// load a WXS schema, represented by a Schema instance
		Source schemaSource = new StreamSource(new StringReader(xsd));
		Schema schema = factory.newSchema(schemaSource);
		
		// create a Validator instance, which can be used to validate an instance document
		this.validationElement = schema.newValidator();
	}
	
	/**
	 * validates the xml document
	 * 
	 * @param xml the xml
	 * @return <code>true</code> if valide, <code>false</code> otherwise
	 */
	private boolean validate(String xml){
		if( this.validationElement==null) return true;
	    try {
	    	this.validationElement.validate(new StreamSource(new StringReader(xml)));
	    } catch (Exception e) {
	    	log.error("could not validate record. returning false",e);
	        return false;
	    }
	    return true;
	}

	protected RSXMLWriter(RSWriterCreationParams initParams, RSFullWriter writer) throws Exception{
		attributes = initParams;
		if (attributes.getPartSize() != -1) partSize = attributes.getPartSize();
		if (attributes.getRecsPerPart() != -1) recsPerCount = attributes.getRecsPerPart();		
		this.writer=writer;
		this.records=new Vector<ResultElementBase>();
		this.synchAddition=new Object();
		this.getValidator(attributes.properties.toArray(new PropertyElementBase[attributes.properties.size()]));
	}
	/**
	 * Creates a new {@link RSXMLWriter} using the default number of results per part and size per part
	 * 
	 * @param writer The {@link RSFullWriter} tat must be used to perform low level operations
	 * @param properties the properties defined to search for validation properties
	 * @throws Exception could not instantiate writer
	 */
	protected RSXMLWriter(RSFullWriter writer,PropertyElementBase [] properties) throws Exception{
		this.writer=writer;
		this.records=new Vector<ResultElementBase>();
		this.synchAddition=new Object();
		this.getValidator(properties);
	}
	
	/**
	 * Creates a new {@link RSXMLWriter} seting the number of results and size per part 
	 * 
	 * @param writer The {@link RSFullWriter} tat must be used to perform low level operations
	 * @param recsPerPart The number of results per part to be used
	 * @param partSize The size of each part
	 * @param properties the properties defined to search for validation properties
	 * @throws Exception could not instantiate writer
	 */
	protected RSXMLWriter(RSFullWriter writer,int recsPerPart,int partSize,PropertyElementBase [] properties) throws Exception{
		this.writer=writer;
		this.records=new Vector<ResultElementBase>();
		this.recsPerCount=recsPerPart;
		this.partSize=partSize;
		this.synchAddition=new Object();
		this.getValidator(properties);
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
	 * in the next invocation to the {@link RSXMLWriter#addResults(ResultElementBase)} or
	 * {@link RSXMLWriter#addResults(ResultElementBase[])}
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
	 * in the next invocation to the {@link RSXMLWriter#addResults(ResultElementBase)} or
	 * {@link RSXMLWriter#addResults(ResultElementBase[])}
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
	 * @throws Exception An unrecoverable for the operation error has occurred 
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
	 * If with this addition the records per part has been reached, the cashed records are stored in the current part and a new 
	 * part is created. Otherwise the result is cashed  
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param record The result to add
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void addResults(ResultElementBase record) throws Exception{
		if(this.records==null){
			log.error("records vector is null. Incorrect initialization incorrect. Throwing Exception");
			throw new Exception("records vector is null. Incorrect initialization incorrect");
		}
		synchronized(synchAddition){
			try{
				if(this.validationElement!=null){
					if(!this.validate(record.toXML())){
						log.error("could not validate record. thrpwing Exception");
						throw new Exception("could not validate record");
					}
				}
				this.size+=record.RS_toXML().getBytes().length;
				if(this.records.size()+1>=this.recsPerCount){
					ResultElementBase []res=new ResultElementBase[this.records.size()+1];
					for(int i=0;i<records.size();i+=1){
						res[i]=records.get(i);
					}
					res[records.size()]=record;
					writer.addResults(res);
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
	 * This operation flushes any cahed records the {@link RSXMLWriter} has stored, and creates a
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
					ResultElementBase []res=new ResultElementBase[this.records.size()];
					for(int i=0;i<records.size();i+=1){
						res[i]=records.get(i);
					}
					writer.addResults(res);
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
	 * This operation first performs a call to {@link RSXMLWriter#writeThrough(String)} and if that call
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
	 * If with this addition the records per part has been reached or exceeded, the cashed records are stored in the current part and a new 
	 * part is created. If the cashed records are still over the defined records per part, this will be repeated. Otherwise the results are cashed 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])
	 * 
	 * @param record The result to add
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public void addResults(ResultElementBase []record) throws Exception{
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
	 * the remaing records, if any using {@link RSXMLWriter#addResults(ResultElementBase[])}
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
					ResultElementBase []res=new ResultElementBase[this.records.size()];
					for(int i=0;i<records.size();i+=1){
						res[i]=records.get(i);
					}
					writer.addResults(res);
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
	
	
	
	
	/*Start of access leasing helpers*/
	/**
	 * 
	 * @throws Exception when access leasing cannot be disabled
	 */
	public void disableAccessLeasing() throws Exception{
		writer.disableAccessLeasing();
	}
	
	/**
	 * Extend the access leasing
	 * @param extend Extend for how much
	 * @throws Exception when extending failed
	 */
	public void extendAccessLeasing(int extend) throws Exception{
		writer.extendAccessLeasing(extend);
	}
	
	/**
	 * Get the access leasing
	 * @return the access leasing
	 * @throws Exception when retriving the access leasing failed
	 */
	public int getAccessLeasing() throws Exception{
		return writer.getAccessLeasing();
	}
	/*End of access leasing helpers*/

	/*Start of forward functions*/
	/**
	 * Is the RS forward reading only. 
	 * @return true if rs is of forward reading type
	 * @throws Exception when information retrival failed
	 */
	public boolean isForward() throws Exception{
		return writer.isForward();
	}

	/**
	 * Set an RS to be forward
	 * @param f true if to set forward
	 * @return true on success
	 * @throws Exception when seting forward failed
	 */
	public boolean setForward(boolean f) throws Exception{
		return writer.setForward(f);
	}
	
	/*End of forward functions*/

	/*Start of time leasing functions*/
	/**
	 * Get the time leasing
	 * @return the time leasing
	 * @throws Exception when retriving the time leasing failed
	 */
	public Date getTimeLeasing() throws Exception{
		return writer.getTimeLeasing();
	}

	/**
	 * Extend the time leasing
	 * @param extend Extend for how long
	 * @return true if successfull
	 * @throws Exception when extending failed
	 */
	public boolean extendTimeLeasing(Date extend) throws Exception{
		return writer.extendTimeLeasing(extend);
	}

	/**
	 * 
	 * @throws Exception when time leasing cannot be disabled
	 */
	public void disableTimeLeasing() throws Exception{
		writer.disableTimeLeasing();
	}
	
	/*End of time leasing functions*/
	
}
