package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.security.HeadMnemonic;
import org.gcube.common.searchservice.searchlibrary.resultset.security.Mnemonic;

/**
 * Placeholder for information on a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} chain
 * 
 * @author UoA
 */
public class ResultSetRef {
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(ResultSetRef.class);
	
	/**
	 * The head of this RS
	 */
	private String head=null;
	/**
	 * The header name that this RS is currently pointing at
	 */
	private String currentHeaderName=null;
	/**
	 * The header info that this RS is currently pointing at
	 */
	private HeaderRef currentHeader=null;
	/**
	 * The name of the file this part should wrap
	 */
	private String inwrap=null;
	/**
	 * The results this part has or will have
	 */
	private Vector<String> results=null;
	/**
	 * Whether or not this RS supports on demand production of results
	 */
	private boolean dataFlow=false;
	/**
	 * Whether or not this RS supports erasure of results
	 */
	private boolean forward = false;
	private int access = -1;
	private Date expire_date = new Date(0);
	private Mnemonic mnemonic = null;
	private HeadMnemonic hmnemonic = null;

	/**
	 * Creates a new {@link ResultSetRef}
	 */
	public ResultSetRef(){
		results=new Vector<String>();
	}
	
	/**
	 * Sets the head file part of the chain
	 * 
	 * @param head the head of the RS
	 */
	public void setHead(String head){
		this.head=head;
	}
	
	/**
	 * Sets the header info that is current
	 * 
	 * @param header The current header
	 */
	public void setCurrentHeader(HeaderRef header){
		this.currentHeader=header;
	}
/**
	 * Sets the current header file name
	 * 
	 * @param header The current header file name
	 */
	public void setCurrentHeaderName(String header){
		this.currentHeaderName=header;
	}

	/**
	 * Retrives the head part
	 * 
	 * @return Teh head part
	 */
	public String getHead(){
		return this.head;
	}
	
	/**
	 * Retrieves info of the current header
	 * 
	 * @return The current header info
	 */
	public HeaderRef getCurrentHeader(){
		return this.currentHeader;
	}

	/**
	 * Retrieves the name of the current header
	 * 
	 * @return The current header name
	 */
	public String getCurrentHeaderName(){
		return this.currentHeaderName;
	}
	
	/**
	 * Adds a new result in the results buffer
	 * 
	 * @param result The record to add
	 * @return <code>true</code> if the result was valid and was added,<code>false</code> otherwise
	 */
	public boolean addResult(String result){
		if(ResultElementBase.isValid(result)){
			results.add(result);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a new result in the results buffer bypassing the validation
	 * 
	 * @param result The record to add
	 * @return <code>true</code>
	 */
	public boolean addText(String result){
		results.add(result);
		return true;
	}
	
	/**
	 * Retrieves the results heald in the underlying buffer
	 * 
	 * @return The available results
	 */
	public Vector<String> getResults(){
		return this.results;
	}
	
	/**
	 * Sets the underlying buffer to hold the provided records
	 * 
	 * @param results The records to hold
	 */
	public void setResults(Vector<String> results){
		this.results=results;
	}
	
	/**
	 * Udates the next property of the current header
	 * 
	 * @param next The value to be set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void updateHeaderNext(String next) throws Exception{
		if(this.currentHeader==null){
			log.error("Header document is null. incorrect initialization. Throwing Exception");
			throw new Exception("Header document is null. incorrect initializatio");
		}
		this.currentHeader.setNext(next);
	}
	
	/**
	 * Udates the prev property of the current header
	 * 
	 * @param prev The value to be set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void updateHeaderPrev(String prev) throws Exception{
		if(this.currentHeader==null){
			log.error("Header document is null. incorrect initialization. Throwing Exception");
			throw new Exception("Header document is null. incorrect initializatio");
		}
		this.currentHeader.setPrev(prev);
	}
	
	/**
	 * Clears the underlying results buffer
	 */
	public void clearResults(){
		results.clear();
	}
	
	/**
	 * Clears the underying structures
	 */
	public void clear(){
		currentHeaderName=null;
		currentHeader=null;
		inwrap=null;
		results.clear();
	}
	
	/**
	 * Set that the payload to be persisted should be done wrapping the provided file
	 * 
	 * @param inwrap The file to wrap
	 */
	public void setInWrap(String inwrap){
		this.inwrap=inwrap;
		this.results.clear();
	}
	
	/**
	 * Retrieves the file that shouldbe wrapped
	 * 
	 * @return Teh file to wrap
	 */
	public String getInWrap(){
		return this.inwrap;
	}

	/**
	 * Checks if there is a file o wrap
	 * 
	 * @return <code>true</code> if a file should be wrapped, <code>false</code> otherwise
	 */
	public boolean inWrap(){
		if(inwrap==null) return false;
		return true;
	}
	
	/**
	 * Sets that there is no file to wrap
	 */
	public void resetInWrap(){
		inwrap=null;
	}

	/**
	 * Returns whether or not this RS offeres on demand production of results
	 * 
	 * @return <code>true</code> if the RS offeres on demand production, <code>false</code> otherwise 
	 */
	public boolean isDataFlow() {
		return dataFlow;
	}

	/**
	 * Sets whether or not this RS offeres on demand production of results
	 * 
	 * @param dataFlow <code>true</code> if the RS offeres on demand production, <code>false</code> otherwise
	 */
	public void setDataFlow(boolean dataFlow) {
		this.dataFlow = dataFlow;
	}
	/**
	 * Sets whether or not this RS offeres erasure of parts
	 * 
	 * @param a tha access leasing
	 */
	public void setAccess(int a) {
		this.access = a;
	}

	/**
	 * Get the access leasing
	 * @return access leasing
	 */
	public int getAccess() {
		return access;
	}

	/**
	 * Get the forward property
	 * @return true if forward property is enabled
	 */
	public boolean isForward() {
		return forward;
	}

	/**
	 * Set the forward property
	 * @param forward true if forward property is to be enabled
	 */
	public void setForward(boolean forward) {
		this.forward = forward;
	}

	/**
	 * Get the time leasing
	 * @return time leasing
	 */
	public Date getExpire_date() {
		return expire_date;
	}

	/**
	 * Set the time leasing
	 * @param expire_date for how long
	 */
	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
	}

	/**
	 * Get security properties
	 * @return the security related class
	 */
	public Mnemonic getMnemonic() {
		return mnemonic;
	}

	/**
	 * Set security properties
	 * @param mnemonic the security related class
	 */
	public void setMnemonic(Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * Get security properties
	 * @return the security related class for the header
	 */
	public HeadMnemonic getHmnemonic() {
		return hmnemonic;
	}

	/**
	 * Set security properties
	 * @param hmnemonic the security related class
	 */
	public void setHmnemonic(HeadMnemonic hmnemonic) {
		this.hmnemonic = hmnemonic;
	}
}
