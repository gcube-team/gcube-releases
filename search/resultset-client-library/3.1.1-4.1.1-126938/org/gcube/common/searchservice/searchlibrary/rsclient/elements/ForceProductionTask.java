package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSTEXTWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter;

/**
 * This class is a helper class used by the {@link org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter}
 * and {@link org.gcube.common.searchservice.searchlibrary.rswriter.RSTEXTWriter} to force the production of parts
 * 
 * @author UoA
 */
public class ForceProductionTask extends TimerTask{
	/**
	 * Logger used for this class
	 */
	private static Logger log = Logger.getLogger(ForceProductionTask.class);
	/**
	 * The {@link RSXMLWriter} used
	 */
	private RSXMLWriter xmlwriter=null;
	/**
	 * The {@link RSTEXTWriter} used
	 */
	private RSTEXTWriter textwriter=null;
	/**
	 * Used for synchronization
	 */
	private Object synchAddition=null;
	/**
	 * The maximum number of parts to produce
	 */
	private int times=0;
	/**
	 * The number of parts produced this far
	 */
	private int counter=0;
	/**
	 * Whether or not the timer os active
	 */
	private boolean alive=false;
	
	/**
	 * Creates a new {@link ForceProductionTask}
	 * 
	 * @param writer The writer used to force the parts production
	 * @param synchAddition Object used to synchronize parts production
	 * @param times The maximum number of parts to produce
	 */
	public ForceProductionTask(RSXMLWriter writer,Object synchAddition,int times){
		this.xmlwriter=writer;
		this.textwriter=null;
		this.synchAddition=synchAddition;
		this.times=times;
		this.counter=0;
		this.alive=true;
	}
	
	/**
	 * Creates a new {@link ForceProductionTask}
	 * 
	 * @param writer The writer used to force the parts production
	 * @param synchAddition Object used to synchronize parts production
	 * @param times The maximum number of parts to produce
	 */
	public ForceProductionTask(RSTEXTWriter writer,Object synchAddition,int times){
		this.textwriter=writer;
		this.xmlwriter=null;
		this.synchAddition=synchAddition;
		this.times=times;
		this.counter=0;
		this.alive=true;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			if(this.times>counter){
				synchronized (synchAddition) {
					if(xmlwriter!=null) xmlwriter.force();
					else textwriter.force();
					alive=true;
				}
				counter+=1;
			}
			else{
				this.cancel();
				synchronized (synchAddition) {
					alive=false;
				}
			}
		}catch(Exception e){
			log.error("Coaught exception while forcing part creation. Canseling my self",e);
			this.cancel();
			synchronized (synchAddition) {
				alive=false;
			}
		}
	}
	
	/**
	 * @see java.util.TimerTask#cancel()
	 * @return wheter or not the timer could be canceled
	 */
	public boolean cancel(){
		synchronized (synchAddition) {
			this.alive=false;
		}
		return super.cancel();
	}
	
	/**
	 * Checks if the timer task is still active
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 */
	public boolean isAlive(){
		synchronized (synchAddition) {
			return this.alive;
		}
	}
}
