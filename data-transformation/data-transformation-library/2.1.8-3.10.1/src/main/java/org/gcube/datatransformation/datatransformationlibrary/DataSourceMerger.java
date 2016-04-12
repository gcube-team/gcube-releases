package org.gcube.datatransformation.datatransformationlibrary;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;

/**
 * @author Dimitris Katris, NKUA
 * 
 * This class is responsible to get the {@link DataElement}s of many {@link DataSource}s and store them into one {@link DataSink}.
 */
public class DataSourceMerger extends Thread {

	/**
	 * {@link Thread}s from which the {@link DataElement}s are fetched. 
	 */
	private List<DataSourceRetriever> retrievers = new ArrayList<DataSourceRetriever>();
	
	/**
	 * The {@link DataSink} in which the {@link DataElement}s are stored.
	 */
	private DataSink sink;
	
	/**
	 * True if no other {@link DataSource} will be appended into the {@link DataSourceMerger}.
	 */
	private boolean finishedAddingSources=false;
	
	/**
	 * The lock of this object is used for synchronizing the writing in the common {@link DataSink} by the {@link DataSourceRetriever}s.   
	 */
	private Object retrieversLock = new Object();
	
	/**
	 * Logs operations performed by {@link DataSourceMerger} class
	 */
	private static Logger log = LoggerFactory.getLogger(DataSourceMerger.class);
	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		try{
			synchronized(this){
//				log.debug("Before entering the 1st while(...) wait");
				while(retrievers.size()<1 && !finishedAddingSources){
					try {
						wait();
					} catch (InterruptedException e) {
						log.error("Someone interrupted me...");
					}
				}
				if(retrievers.size()>0){
					log.info("At least one retriever is appended to the merger...");
					while(!finishedAddingSources || !allHaveEnded()){
						try {
							wait();
						} catch (InterruptedException e) {
							log.error("Someone interrupted me...");
						}
					}
					log.info("All the transformed data have been appended in the sink...");
				} else {
					log.warn("No retrievre has been appended so normally no transformationUnit performed...");
				}
			} 
		}catch(Exception e) {
			log.error("Error in DataSourceMerger",e);
		} finally {
//			All transformations have ended closing the sink...
			if( sink != null){
				log.info("DataSourceMerger is closing the common sink");
				sink.close();
			}
		}
	}
	/**
	 * Checks all the {@link DataSourceRetriever}s if they have finished adding {@link DataElement}s.
	 * 
	 * @return True if all {@link DataSourceRetriever}s have ended appending {@link DataElement}s. 
	 */
	private boolean allHaveEnded(){
		
		for(DataSourceRetriever retriever: retrievers){
			if(retriever.hasEnded==false)
				return false;
		}
		return true;
	}
	
	/**
	 * This method have to been invoked in order to denote that no more {@link DataSource}s will be appended in the {@link DataSourceMerger}.
	 */
	public synchronized void finishedAddingSources(){
		this.finishedAddingSources=true;
		log.info("Have finished adding sources in the DataSourceMerger...");
		notifyAll();
	}

	/**
	 * Adds a {@link DataSource} in the {@link DataSourceMerger}.
	 * 
	 * @param source The {@link DataSource} which will be added.
	 * @return True if the {@link DataSource} appended in the {@link List} successfully.
	 */
	public synchronized boolean add(DataSource source) {
		
		DataSourceRetriever retriever = new DataSourceRetriever();
		retriever.source=source;
		retriever.sink=this.sink;
		retriever.sinklock=retrieversLock;
		retriever.mergerLock=this;
		retriever.start();
		
		boolean ret = retrievers.add(retriever);
		if(retrievers.size()==1)
			notifyAll();
		return ret;
	}
	
	/**
	 * Sets the common <tt>DataSink</tt>.
	 * @param sink The common <tt>DataSink</tt>.
	 */
	public void setSink(DataSink sink) {
		this.sink = sink;
	}
}

/**
 * @author Dimitris Katris, NKUA
 *
 * Reads the {@link DataElement}s from a {@link DataSource} and appends them in the common {@link DataSink} which is set by the merger.
 */
class DataSourceRetriever extends Thread{
	/**
	 * Logs operations performed by {@link DataSourceRetriever} class,
	 */
	private static Logger log = LoggerFactory.getLogger(DataSourceRetriever.class);
	
	/**
	 * The {@link DataSource} from which the {@link DataElement}s are fetched.
	 */
	protected DataSource source;
	/**
	 * The {@link DataSink} in which the {@link DataElement}s are appended.
	 */
	protected DataSink sink;
	/**
	 * Lock that synchronizes the writing in the common {@link DataSink}. 
	 */
	protected Object sinklock;//In case DataSink is not threadsafe...
	/**
	 * Boolean that denotes that the {@link DataSourceRetriever} has moved all the elements of the source to the sink. 
	 */
	boolean hasEnded=false;
	/**
	 * The lock responsible to synchronize the {@link DataSourceRetriever}s with the {@link DataSourceMerger}.
	 */
	protected Object mergerLock;
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while(source.hasNext()){
//			log.info("Having next....");
			DataElement object = null;
			try {
				object = source.next();
			} catch (Exception e) {
				sink.append(new DTSExceptionWrapper(e));
			}
			synchronized(sinklock){
				sink.append(object);
			}
//			log.info("Got next....");
		}
		log.debug("Finished getting objects from the transformer....");
		synchronized(mergerLock){
			hasEnded=true;
//			log.info("Finished getting objects from the transformer, notifyingg....");
			mergerLock.notifyAll();
		}
	}
}
