package org.gcube.common.searchservice.searchlibrary.GarbageCollector;


import java.util.Vector;

/**
 * Container holding the properties for a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class GCProperties {
	/**
	 * The ssids this RS is involved in
	 */
	private Vector<String> ssids=null;
	/**
	 * The WS-Resources pointing to this RS
	 */
	private Vector<String> wsEprs=null;
	/**
	 * The lfespans associated woith this RS
	 */
	private Vector<Long> lifespan=null;
	/**
	 * The chain of files hosting this RS
	 */
	private Vector<String> chain=null;
	/**
	 * Whether or not this RS is complete
	 */
	private boolean complete=false;
	
	/**
	 * Flag marking the last access time has been initialized
	 */
	private boolean lastAccessedInitialized = false;
	/**
	 * The last access time
	 */
	private long lastAccessed=0;
	/**
	 * The last authored time
	 */
	private long lastAuthored=0;
	
	/**
	 * Creates a new {@link GCProperties}
	 */
	public GCProperties(){
		this.ssids=new Vector<String>();
		this.wsEprs=new Vector<String>();
		this.lifespan=new Vector<Long>();
		this.chain=new Vector<String>();
		this.complete=false;
		this.lastAccessed=0;
		this.lastAuthored=0;
		this.lastAccessedInitialized = false;
	}
	
	/**
	 * Adds a search transaction id
	 * 
	 * @param ssid the search transaction id
	 */
	public void addSSID(String ssid){
		this.ssids.add(ssid);
	}
	
	/**
	 * Adds a WSRF epr
	 * 
	 * @param wsEpr the wsrf epr
	 */
	public void addWSEPR(String wsEpr){
		this.wsEprs.add(wsEpr);
	}
	
	/**
	 * Adds the procided filename to the parts of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param file The filename to add
	 */
	public void addToChain(String file){
		this.chain.add(file);
	}
	
	/**
	 * Adds a lifespan property 
	 * 
	 * @param lifeSpan the lifespan property
	 */
	public void addLifeSpan(String lifeSpan){
		try{
			this.lifespan.add(new Long(Long.parseLong(lifeSpan)));
		}catch(Exception e){}
	}
	
	/**
	 * Retrievs the maximum lifespan property
	 * 
	 * @return The maximum lifespan property
	 */
	public long getMaxLifeSpan(){
		long max=0;
		for(int i=0;i<lifespan.size();i+=1){
			if(max<lifespan.get(i).longValue()) max=lifespan.get(i).longValue();
		}
		return max;
	}

	/**
	 * Retrieves the last accessed property
	 * 
	 * @return The last accessed property
	 */
	public long getLastAccessed() {
		return lastAccessed;
	}

	/**
	 * Sets the last accessed property
	 * 
	 * @param lastAccessed The last accessed property
	 */
	public void setLastAccessed(long lastAccessed) {
		if (!lastAccessedInitialized){
			this.lastAccessed = lastAccessed;
			lastAccessedInitialized = true;
		}else{
			if (lastAccessed > this.lastAccessed)
				this.lastAccessed = lastAccessed;
		}
	}

	/**
	 * retrieves the last authored property
	 * 
	 * @return the last authored property
	 */
	public long getLastAuthored() {
		return lastAuthored;
	}

	/**
	 * sets the last authored propery 
	 * 
	 * @param lastAuthored the last authored property
	 */
	public void setLastAuthored(long lastAuthored) {
		this.lastAuthored = lastAuthored;
	}

	/**
	 * Checks if th e{@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} is completed
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * sets the completed property
	 * 
	 * @param complete <code>true</code> if it is, <code>false</code> otherwise
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	/**
	 * Retrieves the chain of files that make up this {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
	 * 
	 * @return the files tha make up this {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 */
	public Vector<String> getChainOfFiles(){
		return this.chain;
	}
	
	/**
	 * Retrieves the WSRF eprs
	 * 
	 * @return the wsrf eprs
	 */
	public Vector<String> getWSEPRs(){
		return this.wsEprs;
	}
}
