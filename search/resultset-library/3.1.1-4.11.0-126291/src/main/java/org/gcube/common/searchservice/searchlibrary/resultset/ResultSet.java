package org.gcube.common.searchservice.searchlibrary.resultset;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.crypto.SecretKey;
import javax.xml.transform.Templates;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.CloneThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.CreationParams;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.FilterByxPathThread;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.HeaderRef;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementWSEPR;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultSetRef;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.TransformByXSLTThread;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSXMLHelper;
import org.gcube.common.searchservice.searchlibrary.resultset.security.HeadMnemonic;
import org.gcube.common.searchservice.searchlibrary.resultset.security.Mnemonic;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *The {@link ResultSet} element provides a container for placing produced records and for consuming them.
 *It organizes them in  a chain of parts explicitly created by the producer. It provides incremental
 *population of the results through the chain of parts. The contents of the result set parts are immutable
 *once created 
 * 
 * @author UoA
 */
public class ResultSet {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(ResultSet.class);
	/**
	 * The ref instance holding general info on the RS 
	 */
	private ResultSetRef ref=null;
	/**
	 * Creates a new {@link ResultSet}
	 * 
	 * @param params RS creation params
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public ResultSet(CreationParams params) throws Exception{
		try{
			ref=new ResultSetRef();
			String head=RSFileHelper.generateName(RSConstants.HEADER,null);
			String header=RSFileHelper.generateName(RSConstants.HEADER,null);
			String encriptedKey = null;
			String strpkey = null;
			if (params.getPKey()!=null){
				ref.setHmnemonic(new HeadMnemonic());
				ref.getHmnemonic().EnableEncryption(params.getPKey());
				strpkey = new sun.misc.BASE64Encoder().encode(params.getPKey().getEncoded());
				
				SecretKey enckey = Mnemonic.genKey();
//				log.info("Secret Key: "+new String(enckey.getEncoded()));
				ref.setMnemonic(new Mnemonic(enckey));

				encriptedKey = ref.getHmnemonic().Encrypt(enckey.getEncoded());
//				log.info("Encrypted Crypted Secret Key: "+encriptedKey);
			}

			RSXMLHelper.createHead(head,header, params.properties.toArray(new String[params.properties.size()]),
									params.isDataflow(), params.getAccessReads(), params.isForward(),
									params.getExpire_date(), strpkey, encriptedKey, params.getAll_properties());
			ref.setHead(head);
			ref.setCurrentHeader(new HeaderRef(header,head));
//			ref.setCurrentHeader(RSXMLHelper.createHeader(header,head));
			ref.setCurrentHeaderName(header);
			ref.setDataFlow(params.isDataflow());
			ref.setAccess(params.getAccessReads());
			ref.setExpire_date(params.getExpire_date());
		}catch(Exception e){
			log.error("Could not create head part. Throwing Exception",e);
			throw new Exception("Could not create head part");
		}
	}


	
	/**
	 * Creates a new {@link ResultSet}
	 * 
	 * @param properties The properties this {@link ResultSet} should have
	 * @param dataFlow wheteher or not this {@link ResultSet} should enable rcord production on demand
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public ResultSet(String []properties,boolean dataFlow) throws Exception{
		try{
			ref=new ResultSetRef();
			String head=RSFileHelper.generateName(RSConstants.HEADER,null);
			String header=RSFileHelper.generateName(RSConstants.HEADER,null);
			RSXMLHelper.createHead(head,header,properties,dataFlow);
			ref.setHead(head);
			ref.setCurrentHeader(new HeaderRef(header,head));
//			ref.setCurrentHeader(RSXMLHelper.createHeader(header,head));
			ref.setCurrentHeaderName(header);
			ref.setDataFlow(dataFlow);
		}catch(Exception e){
			log.error("Could not create head part. Throwing Exception",e);
			throw new Exception("Could not create head part");
		}
	}

	/**
	 * Creates a new {@link ResultSet}
	 * 
	 * @param properties The properties this {@link ResultSet} should have
	 * @param dataFlow wheteher or not this {@link ResultSet} should enable rcord production on demand
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public ResultSet(String properties,boolean dataFlow) throws Exception{
		try{
			ref=new ResultSetRef();
			String head=RSFileHelper.generateName(RSConstants.HEADER,null);
			String header=RSFileHelper.generateName(RSConstants.HEADER,null);
			RSXMLHelper.createHead(head,header,properties,dataFlow);
			ref.setHead(head);
			ref.setCurrentHeader(new HeaderRef(header,head));
//			ref.setCurrentHeader(RSXMLHelper.createHeader(header,head));
			ref.setCurrentHeaderName(header);
			ref.setDataFlow(dataFlow);
		}catch(Exception e){
			log.error("Could not create head part. Throwing Exception",e);
			throw new Exception("Could not create head part");
		}
	}
	 

	/**
	 * Creates a new {@link ResultSet}
	 * 
	 * @param filename The file that holds the head of the existing {@link ResultSet} chain to point to
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public ResultSet(String filename) throws Exception{
		ref=new ResultSetRef();
		if(!new File(filename).exists()){
			log.error("Head file "+filename+" does not exist. Throwsin Exception");
			throw new Exception("Head file "+filename+" does not exist");
		}
		ref.setHead(filename);
		ref.setDataFlow(RSXMLHelper.getDataFlow(RSXMLHelper.getDocument(ref.getHead())));
		String pk = null;
		try{
			pk = RSXMLHelper.getPublicKey(RSXMLHelper.getDocument(ref.getHead()));
		}catch(Exception x){
			log.info("Read of security properties failed.");			
		}
			
		if (pk != null)
			throw new Exception("Resultset is security enabled");

		try{
			ref.setAccess(decAccess());
		}catch(Exception e){
			log.debug("Access counter decrease failed.");
		}
		try{
			ref.setForward(RSXMLHelper.getForward(RSXMLHelper.getDocument(ref.getHead())));
		}catch(Exception e){
			log.debug("Read of Forward property failed.");
		}
		try{
			ref.setExpire_date(RSXMLHelper.getExpireDate(RSXMLHelper.getDocument(ref.getHead())));
		}catch(Exception e){
			log.debug("Read of expire date property failed.");
		}
		
		if (ref.getAccess() == 0){
			log.info("Access leasing exeeced.");
			throw new Exception("Access leasing exeeced.");
		}
		log.info(ref.getExpire_date().getTime() + " ?? " + Calendar.getInstance().getTimeInMillis());
		if (ref.getExpire_date().getTime() != 0 && ref.getExpire_date().getTime() <	 Calendar.getInstance().getTimeInMillis()){
			log.info("Time leasing exeeced.");
			throw new Exception("Time leasing exeeced.");
		}

	}

	/**
	 * Creates a new {@link ResultSet}
	 * 
	 * @param filename The file that holds the head of the existing {@link ResultSet} chain to point to
	 * @param noDecrypt Flag saying to do a decrypt
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public ResultSet(String filename, int noDecrypt) throws Exception{
		ref=new ResultSetRef();
		if(!new File(filename).exists()){
			log.error("Head file "+filename+" does not exist. Throwsin Exception");
			throw new Exception("Head file "+filename+" does not exist");
		}
		ref.setHead(filename);
		ref.setDataFlow(RSXMLHelper.getDataFlow(RSXMLHelper.getDocument(ref.getHead())));

		String pk =null;
		try{
			ref.setHmnemonic(new HeadMnemonic());
    		pk = RSXMLHelper.getPublicKey(RSXMLHelper.getDocument(ref.getHead()));
			//pk = new String(new sun.misc.BASE64Decoder().decodeBuffer(pk));

			ref.setHmnemonic(new HeadMnemonic());
			//dummy key
			SecretKey enckey = Mnemonic.genKey();
//			log.info("Secret Key: "+new String(enckey.getEncoded()));
			ref.setMnemonic(new Mnemonic(enckey));
		}catch(Exception x){
					log.info("Read of security properties failed.");			
			throw new Exception("Resultset is not security enabled",x);
		}
		if (pk != null && noDecrypt != 1 )
			throw new Exception("Resultset is security enabled");

			
		try{
			ref.setAccess(decAccess());
		}catch(Exception e){
			log.debug("Access counter decrease failed.");
		}
		try{
			ref.setForward(RSXMLHelper.getForward(RSXMLHelper.getDocument(ref.getHead())));
		}catch(Exception e){
			log.debug("Read of Forward property failed.");
		}
		try{
			ref.setExpire_date(RSXMLHelper.getExpireDate(RSXMLHelper.getDocument(ref.getHead())));
		}catch(Exception e){
			log.debug("Read of expire date property failed.");
		}
		
		if (ref.getAccess() == 0){
			log.debug("Access leasing exeeced.");
			throw new Exception("Access leasing exeeced.");
		}
//		log.debug(ref.getExpire_date().getTime() + " ?? " + Calendar.getInstance().getTimeInMillis());
		if (ref.getExpire_date().getTime() != 0 && ref.getExpire_date().getTime() <	 Calendar.getInstance().getTimeInMillis()){
			log.debug("Time leasing exeeced.");
			throw new Exception("Time leasing exeeced.");
		}

	}

	/**
	 * Creates a new {@link ResultSet}
	 * 
	 * @param filename The file that holds the head of the existing {@link ResultSet} chain to point to
	 * @param privkey Private key to be used with the RS
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public ResultSet(String filename, PrivateKey privkey) throws Exception{
		ref=new ResultSetRef();
		if(!new File(filename).exists()){
			log.error("Head file "+filename+" does not exist. Throwsin Exception");
			throw new Exception("Head file "+filename+" does not exist");
		}
		ref.setHead(filename);
		ref.setDataFlow(RSXMLHelper.getDataFlow(RSXMLHelper.getDocument(ref.getHead())));
		String pk =null;
		try{
			pk = RSXMLHelper.getPublicKey(RSXMLHelper.getDocument(ref.getHead()));
			//pk = new String(new sun.misc.BASE64Decoder().decodeBuffer(pk));

			ref.setHmnemonic(new HeadMnemonic());
			ref.getHmnemonic().EnableDecryption(privkey);
			ref.getHmnemonic().EnableEncryption(new sun.misc.BASE64Decoder().decodeBuffer(pk));
			
			String enckey = RSXMLHelper.getEncKey(RSXMLHelper.getDocument(ref.getHead()));
//			log.info("Encrypted Crypted Secret Key: "+enckey);
			ref.setMnemonic(new Mnemonic(ref.getHmnemonic().Decrypt(enckey)));
		}catch(Exception x){
			log.info("Read of security properties failed.");			
			throw new Exception("Resultset is not security enabled",x);
		}
		if (pk == null)
			throw new Exception("Resultset is not security enabled");

		try{
			ref.setAccess(decAccess());
		}catch(Exception e){
			log.debug("Access counter decrease failed.");
		}
		try{
			ref.setForward(RSXMLHelper.getForward(RSXMLHelper.getDocument(ref.getHead())));
		}catch(Exception e){
			log.debug("Read of Forward property failed.");
		}
		try{
			ref.setExpire_date(RSXMLHelper.getExpireDate(RSXMLHelper.getDocument(ref.getHead())));
		}catch(Exception e){
			log.debug("Read of expire date property failed.");
		}
		
		if (ref.getAccess() == 0){
			log.debug("Access leasing exeeced.");
			throw new Exception("Access leasing exeeced.");
		}
//		log.info(ref.getExpire_date().getTime() + " ?? " + Calendar.getInstance().getTimeInMillis());
		if (ref.getExpire_date().getTime() != 0 && ref.getExpire_date().getTime() <	 Calendar.getInstance().getTimeInMillis()){
			log.debug("Time leasing exeeced.");
			throw new Exception("Time leasing exeeced.");
		}

	}

	
	/**
	 * Adds a WS-EPR serialization to the head properties container
	 * 
	 * @param epr The WS-EPR to add
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void addWSEPR(String epr) throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(RSXMLHelper.addCustomProperty(RSXMLHelper.getDocument(ref.getHead()),new PropertyElementWSEPR(epr)),ref.getHead());
			}
		}catch(Exception e){
			log.error("Could not add WSEPR to file "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not add WSEPR to file "+ref.getHead());
		}
	}
	
	/**
	 * Retrieves the name of the head part containing file
	 * 
	 * @return The name of the file
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHeadName() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		return ref.getHead();
	}

	/**
	 * Clears underlying structures
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void clear() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		ref.clear();
	}

	/**
	 * Retrieves the container of the {@link ResultSet} info
	 * 
	 * @return The info container
	 */
	public ResultSetRef getRSRef(){
		return this.ref;
	}
	
	/**
	 * Adds teh provided results to the underlying buffer
	 * 
	 * @param results to add
	 * @return <code>true</code> if all the results were successfully added, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean addResults(String []results) throws Exception{
		
		log.trace("addResults called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(results==null || results.length==0) return true;
		boolean ret=true;
		for(int i=0;i<results.length;i+=1){
			if(results[i]==null) continue;
			if(!ref.addResult(results[i])) ret=false;
		}
		return ret;
	}
	
	/**
	 * Adds teh provided text records in the underlying buffer
	 * 
	 * @param results The records to add
	 * @return <code>true</code> if all the records were successfully written, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean addText(String []results) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(results==null || results.length==0) return true;
		for(int i=0;i<results.length;i+=1){
			if(results[i]==null) continue;
			ref.addText(results[i]);
		}
		return true;
	}
	
	/**
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
			if(!ref.isDataFlow()) return RSConstants.CONTROLFLOW.MORE;
			return RSFileHelper.waitOnFlowNotification(ref.getHead(),time);
		}catch(Exception e){
			log.error("Could not check for more results request. Throwing exception",e);
			throw new Exception("Could not check for more results request");
		}
	}
	
	/**
	 * Starts a new part in the chain of parts, making the part that was currently authored available
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void startNewPart() throws Exception{
		log.trace("startNewPart called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			String header=RSFileHelper.generateName(RSConstants.HEADER,null);
			ref.updateHeaderNext(header);
			String currentHeaderPart=ref.getCurrentHeaderName();
			String currentContentPart=RSFileHelper.headerToContent(currentHeaderPart);
			if(!ref.inWrap() || ref.getInWrap()==null) 
				RSFileHelper.persistContent(currentContentPart,ref.getResults(),-1, ref.getMnemonic());
			else{
				RSFileHelper.persistContent(currentContentPart,ref.getInWrap(), ref.getMnemonic());
				ref.resetInWrap();
			}
			RSFileHelper.persistHeader(ref.getCurrentHeader());
//			RSXMLHelper.persistDocument(ref.getCurrentHeader(),ref.getCurrentHeaderName());
			ref.clear();
			ref.setCurrentHeader(new HeaderRef(header,currentHeaderPart));
//			ref.setCurrentHeader(RSXMLHelper.createHeader(header,currentHeaderPart));
			ref.setCurrentHeaderName(header);
			synchronized(RSConstants.sleepOnIt){
				RSConstants.sleepOnIt.notifyAll();
				if(ref.isDataFlow()) RSFileHelper.notifyOnFlowCreation(ref.getHead());
			}
		}catch(Exception e){
			log.error("Could not create a new part. Throwing Exception",e);
			throw new Exception("Could not create a new part");
		}
	}
	
	/**
	 * Stops the authoring of the current part and makes it available
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void endAuthoring() throws Exception{
		log.trace("endAuthoring called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			ref.updateHeaderNext("no");
			String currentHeaderPart=ref.getCurrentHeaderName();
			String currentContentPart=RSFileHelper.headerToContent(currentHeaderPart);
			if(!ref.inWrap() || ref.getInWrap()==null) 
				RSFileHelper.persistContent(currentContentPart, ref.getResults(), -1, ref.getMnemonic());
			else{
				RSFileHelper.persistContent(currentContentPart,ref.getInWrap(), ref.getMnemonic());
				ref.resetInWrap();
			}
			RSFileHelper.persistHeader(ref.getCurrentHeader());
//			RSXMLHelper.persistDocument(ref.getCurrentHeader(),ref.getCurrentHeaderName());
			String headname=ref.getHead();
			ref.clear();
			ref.setHead(headname);
			synchronized(RSConstants.sleepOnIt){
				RSConstants.sleepOnIt.notifyAll();
				if(ref.isDataFlow()) RSFileHelper.notifyOnFlowCreation(ref.getHead());
			}
		}catch(Exception e){
			log.error("Could not end authoring. Throwing Exception",e);
			throw new Exception("Could not end authoring");
		}
	}
	
	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties teh proeprties to set
	 * @throws Exception An unrecoverable for the operatiuon error occured
	 */
	public void overrideProperties(PropertyElementBase []properties) throws Exception{
		try{
			String []props=new String [properties.length];
			for(int i=0;i<properties.length;i+=1) props[i]=properties[i].RS_toXML();
			RSXMLHelper.updateProperties(ref.getHead(),props);
		}catch(Exception e ){
			log.error("could not override and update properties. throwing exception",e);
			throw new Exception("could not override and update properties");
		}
	}
	
	/**
	 * removes any existing properties and sets the new ones
	 * 
	 * @param properties teh proeprties to set
	 * @throws Exception An unrecoverable for the operatiuon error occured
	 */
	public void overrideProperties(String properties) throws Exception{
		try{
			RSXMLHelper.updateProperties(ref.getHead(),properties);
		}catch(Exception e ){
			log.error("could not override and update properties. throwing exception",e);
			throw new Exception("could not override and update properties");
		}
	}
	
	/**
	 * Wraps the provided file content
	 * 
	 * @param filename Teh filename to wrap
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void wrapFile(String filename) throws Exception{
		log.trace("wrapFile called with "+filename);
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(!new File(filename).exists()){
			log.error("Head file "+filename+" does not exist. Throwsin Exception");
			throw new Exception("Head file "+filename+" does not exist");
		}
		ref.setInWrap(filename);
	}

	/**
	 * Retrieves the properties of the provided type
	 * 
	 * @param type The type of properties to retrieve
	 * @return The retrieved properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String [] getProperties(String type) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		Vector<String> ret=null;
		if(ref.getHead()==null){
			log.error("Head File name is null. Throwing Exception");
			throw new Exception("Head File name is null");
		}
		try{
			synchronized(RSConstants.lockHead){
				ret=RSXMLHelper.getProperties(RSXMLHelper.getDocument(ref.getHead()),type);
			}
			if(ret==null || ret.size()==0){
				return new String [0];
			}
			String []props=new String[ret.size()];
			for(int i=0;i<ret.size();i+=1) props[i]=ret.get(i);
			return props; 
		}catch(Exception e){
			log.error("could not retrieve properties. Throwing Exception",e);
			throw new Exception("could not retrieve properties");
		}
	}

	/**
	 * Checks without blocking if the next part in this chain is available
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean nextAvailable() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			String next=null;
			if(ref.getCurrentHeaderName()==null){
				next=RSXMLHelper.getLink(RSXMLHelper.getDocument(ref.getHead()),RSConstants.nextLink);
			}
			else if(ref.getCurrentHeader()!=null){
				next=ref.getCurrentHeader().getNext();
//				next=RSXMLHelper.getLink(ref.getCurrentHeader(),RSConstants.nextLink);
			}
			else{
				next=RSFileHelper.populateHeader(ref.getCurrentHeaderName()).getNext();
//				next=RSXMLHelper.getLink(RSXMLHelper.getDocument(ref.getCurrentHeaderName()),RSConstants.nextLink);
			}
			if(next.equalsIgnoreCase("no")){
				return true;
			}
			if(RSFileHelper.isReady(next)) return true;
			return false;
		}catch(Exception e){
			log.error("Could not check if next part is available. Throwing Exception",e);
			throw new Exception("Could not check if next part is available");
		}
	}
	
	/**
	 * Sets the next part in the chain as the current one waiting at most the provided amount of time in
	 * milliseconds for it to become available
	 * 
	 * @param waitMax T		he amount of time to wait for the next part to become available. If this is negative,
	 * a default amount of time defiened in {@link RSConstants#sleepMax} is used 
	 * @return <code>true</code> if there is a next part and it has become the current one, <code>false</code> if there is no next part
	 * @throws Exception An unrecoverable for the operation error occured or the timer expired
	 */
	public boolean getNextPart(int waitMax) throws Exception{
		log.debug("getNextPart called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			int maxWaitingTime=RSConstants.sleepMax;
			if(waitMax>=0) maxWaitingTime=waitMax;
			String next=null;
			if(ref.getCurrentHeaderName()==null){
				next=RSXMLHelper.getLink(RSXMLHelper.getDocument(ref.getHead()),RSConstants.nextLink);
			}
			else if(ref.getCurrentHeader()!=null){
				next=ref.getCurrentHeader().getNext();
			}
			else{
				if(!RSFileHelper.waitForIt(ref.getCurrentHeaderName(),maxWaitingTime,ref.isDataFlow(),ref.getHead())){
					log.error("Maximum waiting ammount of time reached. Throwing Exception");
					throw new Exception("Maximum waiting ammount of time reached");
				}
				next=RSFileHelper.populateHeader(ref.getCurrentHeaderName()).getNext();
			}
			if(next.equalsIgnoreCase("no")){
				ref.clearResults();
				return false;
			}
			if(RSFileHelper.isReady(next)){
				ref.setCurrentHeaderName(next);
				ref.setCurrentHeader(RSFileHelper.populateHeader(next));
				if(ref.getCurrentHeader().getLocalName()==null){
					log.error("THE NEXT1 IS NULL");
				}
				RSFileHelper.touchHeader(next);
				ref.clearResults();
				if ( getAccessLeasing() == 0 && isForward() ) removePreviousPart();
				return true;
			}
			long startTime=Calendar.getInstance().getTimeInMillis();
			while(true){
				if(Calendar.getInstance().getTimeInMillis()-startTime>=maxWaitingTime){
					log.error("Maximum waiting ammount of time reached. Throwing Exception");
					throw new Exception("Maximum waiting ammount of time reached");
				}
				try{
					if(ref.isDataFlow()) RSFileHelper.requestFlowCreation(ref.getHead());
					synchronized(RSConstants.sleepOnIt){
						RSConstants.sleepOnIt.wait(RSConstants.sleepTime);
					}
				}catch(Exception ee){}
				if(RSFileHelper.isReady(next)){
					ref.setCurrentHeaderName(next);
					ref.setCurrentHeader(RSFileHelper.populateHeader(next));
					if(ref.getCurrentHeader().getLocalName()==null){
						log.error("THE NEXT2 IS NULL");
					}
					RSFileHelper.touchHeader(next);
//					ref.setCurrentHeader(RSXMLHelper.getDocument(next));
					ref.clearResults();
					if ( getAccessLeasing() == 0 && isForward() ) removePreviousPart();
					return true;
				}
			}
		}catch(Exception e){
			ref.clearResults();
			log.error("Could not move to next part. Throwing Exception",e);
			throw new Exception("Could not move to next part");
		}
	}

	/**
	 * Sets the current part to be the first payload part
	 * 
	 * @return <code>true</code> if the operation was successful,<code>false</code> otehrwise
	 * @throws Exception An unrecoverable for the operation error occured or the timer expired
	 */
	public boolean getFirstPart() throws Exception{
		log.debug("getFirstPart called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		ref.setCurrentHeader(null);
		ref.setCurrentHeaderName(null);
		ref.clearResults();
		return getNextPart(-1);
	}
	
	/**
	 * Sets the current part to be the previous part
	 * 
	 * @return <code>true</code> if the operation was successful,<code>false</code> otehrwise
	 * @throws Exception An unrecoverable for the operation error occured or the timer expired
	 */
	public boolean getPreviousPart() throws Exception{
		if (isForward()) {
			log.error("This is a forward only RS.Throwing Exception");
			throw new Exception("This is a forward only RS.");
		}
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			String prev=null;
			if(ref.getCurrentHeaderName()==null) return false;
			if(ref.getCurrentHeader()==null){
				prev=RSFileHelper.populateHeader(ref.getCurrentHeaderName()).getPrev();
//				prev=RSXMLHelper.getLink(RSXMLHelper.getDocument(ref.getCurrentHeaderName()),RSConstants.previousLink);
			}
			else{
				prev=ref.getCurrentHeader().getPrev();
//				prev=RSXMLHelper.getLink(ref.getCurrentHeader(),RSConstants.previousLink);
			}
			if(prev.compareTo(ref.getHead())==0){
				return false;
			}
			if(RSFileHelper.isReady(prev)){
				ref.setCurrentHeaderName(prev);
				ref.setCurrentHeader(RSFileHelper.populateHeader(prev));
//				ref.setCurrentHeader(RSXMLHelper.getDocument(prev));
				ref.clearResults();
				return true;
			}
			log.error("Could not move to previous part. This shouldn't happen. Throwing Exception");
			throw new Exception("Could not move to previous part");
		}catch(Exception e){
			log.error("Could not move to previous part. Throwing Exception",e);
			throw new Exception("Could not move to previous part");
		}
	}

	/**
	 * Sets the current part to be the previous part
	 * 
	 * @return <code>true</code> if the operation was successful,<code>false</code> otehrwise
	 * @throws Exception An unrecoverable for the operation error occured or the timer expired
	 */
	public boolean removePreviousPart() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			ResultSetRef prevref=new ResultSetRef();
			String prev=null;
			if(ref.getCurrentHeaderName()==null) return false; //You are at the first part.. probably
			if(ref.getCurrentHeader()==null){
				prev=RSFileHelper.populateHeader(ref.getCurrentHeaderName()).getPrev();
//				prev=RSXMLHelper.getLink(RSXMLHelper.getDocument(ref.getCurrentHeaderName()),RSConstants.previousLink);
			}else{
				prev=ref.getCurrentHeader().getPrev();
//				prev=RSXMLHelper.getLink(ref.getCurrentHeader(),RSConstants.previousLink);
			}
			if(prev.compareTo(ref.getHead())==0){
				return false; //You are at the first part.. probably
			}
			if(RSFileHelper.isReady(prev)){
				prevref.setCurrentHeaderName(prev);
				prevref.setCurrentHeader(RSFileHelper.populateHeader(prev));
//				ref.setCurrentHeader(RSXMLHelper.getDocument(prev));
				prevref.clearResults();
//				return true;
			}else{
				return false;
			}
			ResultSetRef prevprevref=new ResultSetRef();
			String prevprev=null;
			if(prevref.getCurrentHeaderName()==null) return false; //You are at the first part.. probably
			prevprev=prevref.getCurrentHeader().getPrev();
			if(prevprev.compareTo(ref.getHead())==0){
				return false; //You are at the first part.. probably
			}
			if(RSFileHelper.isReady(prevprev)){
				prevprevref.setCurrentHeaderName(prevprev);
				prevprevref.setCurrentHeader(RSFileHelper.populateHeader(prevprev));
//				ref.setCurrentHeader(RSXMLHelper.getDocument(prev));
				prevprevref.clearResults();
//				return true;
			}else{
				return false;
			}

			prevprevref.updateHeaderNext(prevref.getCurrentHeader().getNext());
			RSFileHelper.persistHeader(prevprevref.getCurrentHeader());

			ref.updateHeaderPrev(prevref.getCurrentHeader().getPrev());
			RSFileHelper.persistHeader(ref.getCurrentHeader());
			
			String dir = null;
			try{
				File tmp=new File(prevref.getCurrentHeaderName());
				dir = tmp.getParent();
				tmp.delete();
				log.debug("deleting file (name retrivied from metadata .rs ) "+prevref.getCurrentHeaderName());
			}catch(Exception e){
				log.error("could not delete file "+prevref.getCurrentHeaderName()+" continuing",e);
			}
			try{
				File tmp=new File(RSFileHelper.headerToContent(prevref.getCurrentHeaderName()));
				tmp.delete();
				log.debug("deleting file (name retrivied from metadata .hrs )"+RSFileHelper.headerToContent(prevref.getCurrentHeaderName()));
			}catch(Exception e){
				log.error("could not delete content file of "+prevref.getCurrentHeaderName()+" continuing",e);
			}
			try{
				log.trace("try to remove the RS directory");	
				File tmp=new File(dir);
				tmp.delete()		;
				log.trace("Directory "+dir+" removed");
			}catch(Exception e){
				log.error("could not remove directory",e);
			}
//			log.info("All went OK with remoing part");
			return true;
		}catch(Exception e){
			log.error("Could not move to previous part. Throwing Exception",e);
			throw new Exception("Could not move to previous part");
		}
	}

	/**
	 * Checks if the current part is the first payload part
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured or the timer expired
	 */
	public boolean isFirst() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		try{
			if(ref.getCurrentHeaderName()==null) return true;
			String prev=null;
			if(ref.getCurrentHeader()==null){
				prev=RSFileHelper.populateHeader(ref.getCurrentHeaderName()).getPrev();
			}
			else{
				prev=ref.getCurrentHeader().getPrev();
			}
			return (prev.compareTo(ref.getHead())==0);
		}catch(Exception e){
			log.error("Could not move to previous part. Throwing Exception",e);
			throw new Exception("Could not move to previous part");
		}
	}
	
	/**
	 * Checks if the current part is the last payload part
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured or the timer expired
	 */
	public boolean isLast() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null) return false;
		String next=null;
		if(ref.getCurrentHeader()==null){
			next=RSFileHelper.populateHeader(ref.getCurrentHeaderName()).getNext();
		}
		else{
			next=ref.getCurrentHeader().getNext();
		}
		return (next.compareTo("no")==0);
	}

	/**
	 * Retrieves the number of results stored in a payload part of the {@link ResultSet}
	 * 
	 * @param type The type of the RS part whose records should be conted  
	 * @return The number of results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public int getNumberOfResults(String type) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
//				ref.setCurrentHeader(RSXMLHelper.getDocument(ref.getCurrentHeaderName()));
			}
			if(type.equalsIgnoreCase(PropertyElementType.XML)){
				if(ref.getResults().size()==0){
					ref.setResults(RSFileHelper.populateResults(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.getMnemonic()));
				}
				return ref.getResults().size();
			}
			else if(type.equalsIgnoreCase(PropertyElementType.TEXT)){
				return RSFileHelper.getNumberOfResultsInTextFile(RSFileHelper.headerToContent(ref.getCurrentHeaderName()));
			}
			else{
				log.error("Unregognized type "+type+". Throwing Exception");
				throw new Exception("Unregognized type "+type);
			}
		}catch(Exception e){
			log.error("Could not get Number of results. Throwing Exception",e);
			throw new Exception("Could not get Number of results");
		}
	}

	/**
	 * Retrieves the payload of the current content part
	 * 
	 * @return The payload of the current payload part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartPayload() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			return this.getFileContent(RSFileHelper.headerToContent(ref.getCurrentHeaderName()));
		}catch(Exception e){
			log.error("Could not get Number of results. Throwing Exception",e);
			throw new Exception("Could not get Number of results");
		}
	}

	/**
	 * Retrieves the result that has the specified index in the current payload part
	 * 
	 * @param index The index of the result to be retrieved
	 * @return The result serialization
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getResult(int index) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			if(ref.getResults().size()==0){
				ref.setResults(RSFileHelper.populateResults(RSFileHelper.headerToContent(ref.getCurrentHeaderName()), ref.getMnemonic()));
			}
			if(index>=ref.getResults().size() || index<0){
				log.warn("provided index excedes available records. In case of a BLOB rs this is not alarming :-).Throwing Exception");
				throw new Exception("provided index excedes available records. In case of a BLOB rs this is not alarming :-)");
			}
			return ref.getResults().get(index);
		}catch(Exception e){
			//log.warn("Could not retrieve result with index"+index+". In case of a BLOB rs this is not alarming :-). Throwing Exception",e);
			log.warn("Could not retrieve result with index"+index+". In case of a BLOB rs this is not alarming :-). Throwing Exception");
			throw new Exception("Could not retrieve result with index"+index+". In case of a BLOB rs this is not alarming :-)");
		}
	}
	
	/**
	 * Retrieves all the results that are in the current payload part
	 * 
	 * @return An array of all the serialized results of the current payload part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getResults() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			if(ref.getResults().size()==0){
				ref.setResults(RSFileHelper.populateResults(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.getMnemonic() ));
			}
			return ref.getResults().toArray(new String[0]);
		}catch(Exception e){
			log.error("Could not retrieve results. Throwing Exception",e);
			throw new Exception("Could not retrieve results");
		}
	}

	/**
	 * Retrieves all the results that are in the current payload part that have an index in the specified
	 * index span
	 * 
	 * @param from the starting index
	 * @param to the ending index
	 * @return An array of all the serialized results of the current payload part that have an index
	 * in the specified index span
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []getResults(int from,int to) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			if(ref.getResults().size()==0){
				ref.setResults(RSFileHelper.populateResults(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.getMnemonic()));
			}
			if(from<0 || to>ref.getResults().size() || to<from || to==from){
				log.error("Not valid from-to arguments. Throwing Exception");
				throw new Exception("Not valid from-to arguments");
			}
			return ref.getResults().subList(from,to).toArray(new String[0]);
		}catch(Exception e){
			log.error("Could not retrieve results. Throwing Exception",e);
			throw new Exception("Could not retrieve results");
		}
	}
	
	/**
	 * Retrieves the IP address of the host that the {@link ResultSet} receides in
	 * 
	 * @return The IP address
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHostIP() throws Exception{
		try{
			InetAddress localaddr = InetAddress.getLocalHost();
			return localaddr.getHostAddress();
		}catch (Exception e){
			log.error("Could not get Host IP. Throwing Exception",e);
			throw new Exception("Could not get Host IP");
		}
	}
	
	/**
	 * Retrieves the name of the host that the {@link ResultSet} receides in
	 * 
	 * @return The host name
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getHostName() throws Exception{
		try{
			InetAddress localaddr = InetAddress.getLocalHost();
			return localaddr.getHostName();
		}catch (Exception e){
			log.error("Could not get Host name. Throwing Exception",e);
			throw new Exception("Could not get Host name");
		}
	}
	
	/**
	 * Splits the current payload part in sized defined by the {@link RSConstants#partSize} property and encdes
	 * them using Base64 encoding
	 * 
	 * @return An array with the parts names
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []splitEncoded() throws Exception{
		log.trace("splitEncoded called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		FileInputStream in=null;
		FileOutputStream out=null;
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			//decrypt input stream before spliting
			String plain=RSFileHelper.generateName(RSConstants.PAGEDCONTENT,null);
			DataInputStream instring = RSFileHelper.getBinaryContent(RSFileHelper.headerToContent(ref.getCurrentHeaderName()), ref.getMnemonic());
			out=new FileOutputStream(new File(plain));
			byte[] buf = new byte[4096];
			int len;
			while ((len = instring.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			instring.close();
			out.close();

			BASE64Encoder encoder=new BASE64Encoder();
			in=new FileInputStream(new File(plain));
			String encoded=RSFileHelper.generateName(RSConstants.PAGEDCONTENT,null);
			out=new FileOutputStream(new File(encoded));
			encoder.encode(in,out);
			in.close();
			in=null;
			out.close();
			out=null;
			
			return RSFileHelper.splitFile(encoded);
		}catch(Exception e){
			if(in!=null) in.close();
			if(out!=null) out.close();
			log.error("Could not retrieve results. Throwing Exception",e);
			throw new Exception("Could not retrieve results");
		}
	}
	
	/**
	 * Splits the current payload part in sized defined by the {@link RSConstants#partSize} property
	 * 
	 * @return An array with the parts names
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []splitClear() throws Exception{
		log.trace("splitClear called");
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			if (ref.getMnemonic()!= null){
				//decrypt input stream before spliting
				String plain=RSFileHelper.generateName(RSConstants.PAGEDCONTENT,null);
				String content = RSFileHelper.getContent(RSFileHelper.headerToContent(ref.getCurrentHeaderName()), ref.getMnemonic());
				FileOutputStream out=new FileOutputStream(new File(plain));
				InputStream instring = new ByteArrayInputStream(content.getBytes());
				byte[] buf = new byte[4096];
				int len;
				while ((len = instring.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				instring.close();
				out.close();
				return RSFileHelper.splitFile(plain);
			}else{
				return RSFileHelper.splitFile(RSFileHelper.headerToContent(ref.getCurrentHeaderName()));
			}
		}catch(Exception e){
			log.error("Could not retrieve results. Throwing Exception",e);
			throw new Exception("Could not retrieve results");
		}
	}
	
	/**
	 * Retrieves the content of the provided local file
	 * 
	 * @param filename The file whose content must e retrieved
	 * @return The file content
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getFileContent(String filename) throws Exception{
		try{
			return RSFileHelper.getContent(filename, ref.getMnemonic());
		}catch(Exception e){
			log.error("Could not retrieve file content "+filename,e);
			throw new Exception("Could not retrieve file content "+filename);
		}
	}

	/**
	 * Retrieves a serialization of the head custom properties
	 * 
	 * @return The custom properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String retrieveCustomProperties() throws Exception{
		if(ref==null || ref.getHead()==null){
			log.error("Incorrect initialization.Throwing Exception");
			throw new Exception("Incorrect initialization");
		}
		if(!RSFileHelper.waitForIt(ref.getHead(),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find head part "+ref.getHead()+". Throwing Exception");
			throw new Exception("Could not find head part "+ref.getHead());
		}
		try{
			String props=null;
			synchronized (RSConstants.lockHead) {
				props=RSXMLHelper.retrieveCustomProperties(RSXMLHelper.getDocument(ref.getHead()));
			}
			return props;
		}catch(Exception e){
			log.error("Could not retrieve Custom Properties . Throwing Exception",e);
			throw new Exception("Could not retrieve Custom Properties");
		}
	}
	
	/**
	 * Clones the implied {@link ResultSet}
	 * 
	 * @return The name of the head file pointing to the new chain of parts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String cloneRS() throws Exception{
		try{
			CreationParams cp = new CreationParams();
			cp.setAccessReads(getAccessLeasing());
			cp.setDataflow(false);
			cp.setExpire_date(getTimeLeasing());
			cp.setForward(ref.isForward());
			if (ref.getHmnemonic()!=null)
				cp.setPKey(ref.getHmnemonic().getPublickey());
			cp.setAll_properties(retrieveCustomProperties());
			ResultSet rs=new ResultSet(cp);
			ResultSet rsRead = null;
			if (ref.getHmnemonic()!=null){
				if (ref.getHmnemonic().getPrivatekey()!=null)
					rsRead=new ResultSet(ref.getHead(),ref.getHmnemonic().getPrivatekey());
				else
					rsRead=new ResultSet(ref.getHead());
			}else{
				rsRead=new ResultSet(ref.getHead());				
			}
			String rsHead=rs.getHeadName();
			CloneThreadGeneric worker=new CloneThreadGeneric(rs,rsRead);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform cloning in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform cloning in Service Side");
		}

	}
	
	/**
	 * Performs a keep top operation on the implied {@link ResultSet} chain and creates a bew {@link ResultSet}
	 * with the top kept. The new {@link ResultSet} has the same properties with the current one
	 * 
	 * @param count The number of top elements to keep
	 * @param type The type of operation to perform. This can be one of {@link KeepTopThreadGeneric#PERPART}, {@link KeepTopThreadGeneric#PERRECORD}
	 * @return The name of the head part of the new {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String keepTop(int count,short type) throws Exception{
		try{
			String properties=this.retrieveCustomProperties();
			ResultSet rs=new ResultSet(properties,false);
			ResultSet rsRead=new ResultSet(ref.getHead());
			String rsHead=rs.getHeadName();
			KeepTopThreadGeneric worker=new KeepTopThreadGeneric(rs,rsRead,count,type);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform keepTop in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform keepTop in Service Side");
		}

	}
	
	/**
	 * Performs a keep top operation on the implied {@link ResultSet} chain and creates a bew {@link ResultSet}
	 * with the top kept
	 * 
	 * @param properties The properties the new {@link ResultSet} must have
	 * @param count The number of top elements to keep
	 * @param type The type of operation to perform. This can be one of {@link KeepTopThreadGeneric#PERPART}, {@link KeepTopThreadGeneric#PERRECORD}
	 * @return The name of the head part of the new {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String keepTop(String []properties,int count,short type) throws Exception{
		try{
			ResultSet rs=new ResultSet(properties,false);
			ResultSet rsRead=new ResultSet(ref.getHead());
			String rsHead=rs.getHeadName();
			KeepTopThreadGeneric worker=new KeepTopThreadGeneric(rs,rsRead,count,type);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform keepTop in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform keepTop in Service Side");
		}

	}
	
	/**
	 * Retrieves the name of the current payload part
	 * 
	 * @return The file name
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartName() throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		return RSFileHelper.headerToContent(ref.getCurrentHeaderName());
	}

	/**
	 * Evaluates the provided xPath expression against the head part document and returns the output
	 * as a serialized string.
	 * 
	 * @param xPath The xPath to be evaluated
	 * @return The serialization of the evaluation output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnHead(String xPath) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getHead()==null){
			log.error("Head File name is null. Throwing Exception");
			throw new Exception("Head File name is null");
		}
		try{
			String ret=null;
			synchronized(RSConstants.lockHead){
				ret=RSXMLHelper.executeQueryOnDocument(RSXMLHelper.getDocument(ref.getHead()),xPath);
			}
			return ret; 
		}catch(Exception e){
			log.error("could not execute query on head. Throwing Exception",e);
			throw new Exception("could not execute query on head");
		}
	}

	/**
	 * Evaluates the provided xPath expression against the head part document and returns the output
	 * as a serialized string.
	 * 
	 * @param xPath The xPath to be evaluated
	 * @return The serialization of the evaluation output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnDocument(String xPath) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			String ret=null;
			synchronized(RSConstants.lockHead){
				ret=RSXMLHelper.executeQueryOnDocument(RSXMLHelper.getDocument(RSFileHelper.headerToContent(ref.getCurrentHeaderName())),xPath);
			}
			return ret; 
		}catch(Exception e){
			log.error("could not execute query on head. Throwing Exception",e);
			throw new Exception("could not execute query on head");
		}
	}

	/**
	 * Evaluates the provided expression against each of the results in the current payload part and if it
	 * produces output the entire result serialization is considered as output.  
	 * 
	 * @param xPath The xPath expression to be evaluated. it must start evaluation under the current node
	 * @return The results that produced output for the provided expression
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String []executeQueryOnResults(String xPath) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			if(ref.getResults().size()==0){
				ref.setResults(RSFileHelper.populateResults(RSFileHelper.headerToContent(ref.getCurrentHeaderName()), ref.getMnemonic()));
			}
			Vector<String> res=new Vector<String>();
			for(int i=0;i<ref.getResults().size();i+=1){
				try{
					if(RSXMLHelper.executeQueryOnResults(ref.getResults().get(i),xPath)) res.add(ref.getResults().get(i));
				}catch(Exception e){
					log.error("Caught Exception while executing Query on result. Continuing",e);
				}
			}
			return res.toArray(new String[0]);
		}catch(Exception e){
			log.error("Could not execute query. Throwing Exception",e);
			throw new Exception("Could not execute query");
		}
	}

	/**
	 * Performs a filtering operation based on an provided xPath on the referenced {@link ResultSet} returning 
	 * the name of the local file that holds the head part of the new {@link ResultSet}. The xPath is applied to each result
	 * and should begin referencing the current node
	 * 
	 * @param xPath The xpath to apply to the referenced {@link ResultSet} results to create the new one
	 * @return The name of the file that holds the head part of the created {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String filterRS(String xPath) throws Exception{
		try{
			String properties=this.retrieveCustomProperties();
			ResultSet rs=new ResultSet(properties,false);
			ResultSet rsRead=new ResultSet(ref.getHead());
			String rsHead=rs.getHeadName();
			FilterByxPathThread worker=new FilterByxPathThread(rs,rsRead,xPath);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform filter By xPath in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform filter By xPath in Service Side");
		}
	}

	/**
	 * Performs a filtering operation based on an provided xPath on the referenced {@link ResultSet} returning 
	 * the name of the local file that holds the head part of the new {@link ResultSet}. The xPath is applied to each result
	 * and should begin referencing the current node
	 * 
	 * @param xPath The xpath to apply to the referenced {@link ResultSet} results to create the new one
	 * @param properties The properties the new {@link ResultSet} should have
	 * @return The name of the file that holds the head part of the created {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String filterRS(String xPath,String []properties) throws Exception{
		try{
			ResultSet rs=new ResultSet(properties,false);
			ResultSet rsRead=new ResultSet(ref.getHead());
			String rsHead=rs.getHeadName();
			FilterByxPathThread worker=new FilterByxPathThread(rs,rsRead,xPath);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform filter By xPath in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform filter By xPath in Service Side");
		}
	}
	
	/**
	 * Performs an xslt transformation against the current payload part
	 * 
	 * @param xslt The xslt to apply
	 * @return The output of the transformation
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformByXSLT(String xslt) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			String transformFile=RSFileHelper.generateName(RSConstants.CONTENT,null);
			RSXMLHelper.transform(xslt,RSFileHelper.headerToContent(ref.getCurrentHeaderName()),transformFile);
			String content=RSFileHelper.getContent(transformFile, ref.getMnemonic());
			new File(transformFile).delete();
			return content;
		}catch(Exception e){
			log.error("Could not execute query. Throwing Exception",e);
			throw new Exception("Could not execute query");
		}
		
	}
	
	/**
	 * Performs an xslt transformation against the current payload part
	 * 
	 * @param xslt The xslt to apply
	 * @return The name of the file that holds the output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformByXSLTAndPersist(String xslt) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			String transformFile=RSFileHelper.generateName(RSConstants.CONTENT,null);
			RSXMLHelper.transform(xslt,RSFileHelper.headerToContent(ref.getCurrentHeaderName()),transformFile);
			return transformFile;
		}catch(Exception e){
			log.error("Could not execute query. Throwing Exception",e);
			throw new Exception("Could not execute query");
		}
		
	}
	
	/**
	 * Performs an xslt transformation against the current payload part
	 * 
	 * @param xslt The xslt to apply
	 * @return The name of the file that holds the output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformByXSLTAndPersist(Templates xslt) throws Exception{
		if(ref==null){
			log.error("Incorrect initialization. RSRef is null.Throwing Exception");
			throw new Exception("Incorrect initialization. RSRef is null");
		}
		if(ref.getCurrentHeaderName()==null){
			getNextPart(-1);
		}
		if(!RSFileHelper.waitForIt(RSFileHelper.headerToContent(ref.getCurrentHeaderName()),ref.isDataFlow(),ref.getHead())){
			log.error("Could not find payload part"+RSFileHelper.headerToContent(ref.getCurrentHeaderName())+". Throwing Exception");
			throw new Exception("Could not find payload part");
		}
		try{
			if(ref.getCurrentHeader()==null){
				ref.setCurrentHeader(RSFileHelper.populateHeader(ref.getCurrentHeaderName()));
			}
			String transformFile=RSFileHelper.generateName(RSConstants.CONTENT,null);
			RSXMLHelper.transform(xslt,RSFileHelper.headerToContent(ref.getCurrentHeaderName()),transformFile);
			return transformFile;
		}catch(Exception e){
			log.error("Could not execute query. Throwing Exception",e);
			throw new Exception("Could not execute query");
		}
		
	}
	
	/**
	 * Performs an xslt transfromation against the payload part of the implied {@link ResultSet} and creates
	 * a new {@link ResultSet} with payload the output of each transformation. The new {@link ResultSet}
	 * has as properties the properties of the current one
	 * 
	 * @param transformation The tranformation to apply
	 * @return The name of the file holding the new {@link ResultSet} head part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformRS(String transformation) throws Exception{
		try{
			String properties=this.retrieveCustomProperties();
			ResultSet rs=new ResultSet(properties,false);
			ResultSet rsRead=new ResultSet(ref.getHead());
			String rsHead=rs.getHeadName();
			TransformByXSLTThread worker=new TransformByXSLTThread(rs,rsRead,transformation);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform transformation By XSLT in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform transformation By XSLT in Service Side");
		}
	}

	/**
	 * Performs an xslt transfromation against the payload part of the implied {@link ResultSet} and creates
	 * a new {@link ResultSet} with payload the output of each transformation. The new {@link ResultSet}
	 * has as properties the properties of the current one
	 * 
	 * @param transformation The tranformation to apply
	 * @param properties The properties the new {@link ResultSet} must have
	 * @return The name of the file holding the new {@link ResultSet} head part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String transformRS(String transformation,String []properties) throws Exception{
		try{
			ResultSet rs=new ResultSet(properties,false);
			ResultSet rsRead=new ResultSet(ref.getHead());
			String rsHead=rs.getHeadName();
			TransformByXSLTThread worker=new TransformByXSLTThread(rs,rsRead,transformation);
			worker.start();
			return rsHead;
		}catch(Exception e){
			log.error("Could not get perform transformation By XSLT in Service Side. Throwing Exception",e);
			throw new Exception("Could not get perform transformation By XSLT in Service Side");
		}
	}

	/* Access leasing helpers */
	private int decAccess() throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				int access = RSXMLHelper.getAccess(RSXMLHelper.getDocument(ref.getHead()));
				if (access == 0) return access;
				
				access--;
				RSXMLHelper.persistDocument(RSXMLHelper.updateAccessCounter(
						RSXMLHelper.getDocument(ref.getHead()),access),
						ref.getHead());
				return access;
			}
		}catch(Exception e){
			log.error("Could not decrease forward counter "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not decrease forward counter "+ref.getHead());
		}
	}
	
	private int setAccess(int access) throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(RSXMLHelper.updateAccessCounter(
						RSXMLHelper.getDocument(ref.getHead()),access),
						ref.getHead());
				return access;
			}
		}catch(Exception e){
			log.error("Could not set access counter "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not set access counter "+ref.getHead());
		}
	}
	
	/**
	 * 
	 * @throws Exception when access leasing cannot be disabled
	 */
	public void disableAccessLeasing() throws Exception{
		try{
			setAccess(-1);
		}catch(Exception e){
			log.error("Could not disable access counter "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not disable access counter "+ref.getHead());
		}
	}
	
	/**
	 * Extend the access leasing
	 * @param extend Extend for how much
	 * @throws Exception when extending failed
	 */
	public void extendAccessLeasing(int extend) throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				int access = RSXMLHelper.getAccess(RSXMLHelper.getDocument(ref.getHead()));
				
				access+=extend;
				RSXMLHelper.persistDocument(RSXMLHelper.updateAccessCounter(
						RSXMLHelper.getDocument(ref.getHead()),access),
						ref.getHead());
				log.info("Access leasing extended to "+ --access);
			}
		}catch(Exception e){
			log.error("Could not extend access counter "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not extend access counter "+ref.getHead());
		}
	}
	
	/**
	 * Get the access leasing
	 * @return the access leasing
	 * @throws Exception when retriving the access leasing failed
	 */
	public int getAccessLeasing() throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				int access = RSXMLHelper.getAccess(RSXMLHelper.getDocument(ref.getHead()));
				access--; //count the creator
				return access;
			}
		}catch(Exception e){
			log.error("Could not get access counter "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not get access counter "+ref.getHead());
		}
	}
	/*End of access leasing helpers*/

	/*Start of forward functions*/
	/**
	 * Is the RS forward reading only. 
	 * @return true if rs is of forward reading type
	 * @throws Exception when information retrival failed
	 */
	public boolean isForward() throws Exception{
		return RSXMLHelper.getForward(RSXMLHelper.getDocument(ref.getHead()));
	}

	/**
	 * Set an RS to be forward
	 * @param f true if to set forward
	 * @return true on success
	 * @throws Exception when seting forward failed
	 */
	public boolean setForward(boolean f) throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(RSXMLHelper.setForward(RSXMLHelper.getDocument(ref.getHead()),f),
						ref.getHead());
				return f;
			}
		}catch(Exception e){
			log.error("Could not set forward "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not set forward "+ref.getHead());
		}
	}
	
	/*End of forward functions*/

	/*Start of time leasing functions*/
	
	/**
	 * Get the time leasing
	 * @return the time leasing
	 * @throws Exception when retriving the time leasing failed
	 */
	public Date getTimeLeasing() throws Exception{
		return RSXMLHelper.getExpireDate(RSXMLHelper.getDocument(ref.getHead()));
	}

	/**
	 * Extend the time leasing
	 * @param extend Extend for how long
	 * @return true if successfull
	 * @throws Exception when extending failed
	 */
	public boolean extendTimeLeasing(Date extend) throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(RSXMLHelper.setExpireDate(
						RSXMLHelper.getDocument(ref.getHead()),extend),
						ref.getHead());
				log.info("Time leasing extended to "+ extend);
				return true;
			}
		}catch(Exception e){
			log.error("Could not extend time "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not extend time "+ref.getHead());
		}
	}

	/**
	 * 
	 * @throws Exception when time leasing cannot be disabled
	 */
	public void disableTimeLeasing() throws Exception{
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(RSXMLHelper.setExpireDate(
						RSXMLHelper.getDocument(ref.getHead()),new Date(0)),
						ref.getHead());
				log.info("Time leasing disabled");
			}
		}catch(Exception e){
			log.error("Could not disable time "+ref.getHead()+" Throwing Exception",e);
			throw new Exception("Could not disable time "+ref.getHead());
		}
	}
	
	/**
	 * Get security properties
	 * @return the security related class
	 */
	public Mnemonic getMnemonic(){
		return ref.getMnemonic();
	}

	/**
	 * Get security properties
	 * @return the security related class for the header
	 */
	public HeadMnemonic getHeadMnemonic(){
		return ref.getHmnemonic();
	}
	
	/**
	 * Get the encryption key
	 * @return the key
	 * @throws Exception An error occurred
	 */
	public byte[] getEncKey() throws Exception{
		return RSXMLHelper.getEncKey(RSXMLHelper.getDocument(ref.getHead())).getBytes();
	}
	/*End of time leasing functions*/

}
