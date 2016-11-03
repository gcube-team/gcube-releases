package org.gcube.common.searchservice.searchlibrary.rsreader;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.InitReaderThread;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.MakeLocalThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocationWrapper;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.ReaderInitParams;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.InitReaderThread.RSReaderEnum;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSFullWriter;

/**
 * Generic Reader class used to retrieve payload of a previously created {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * hidding its location
 * 
 * @author UoA
 */
public class RSReader {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSReader.class);
	/**
	 * The underlying location wrapping element
	 */
	private RSLocationWrapper rs=null;

	/**
	 * Creates a new {@link RSReader} pointing to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that the provided {@link RSLocator} identifies
	 * 
	 * @param locator The {@link RSLocator} that identifies the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @return The {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSReader getRSReader(RSLocator locator) throws Exception{
		try{
			return new RSReader(new RSLocationWrapper(locator));
		}catch(Exception e){
			log.error("Could not create RSReader Throwing Exception",e);
			throw new Exception("Could not create RSReader");
		}
	}
	
	/**
	 * Instantiates and localizes or not the readers that point to the specified locator
	 * 
	 * @param params the initialization parameters
	 * @return the created readers in the same order as their input. If some reader could not be initialized null is placed
	 */
	public static RSReader []getRSReader(ReaderInitParams []params){
		InitReaderThread []ts=new InitReaderThread[params.length];
		RSReader []rs=new RSReader[params.length];
		for(int i=0;i<params.length;i+=1){
			ts[i]=new InitReaderThread(params[i],RSReaderEnum.Reader);
			ts[i].start();
		}
		for(int i=0;i<params.length;i+=1){
			try{
				ts[i].join();
				rs[i]=(RSReader)ts[i].getReader();
			}catch(Exception e){
				log.error("interrupted whil waiting for reader. setting to null");
				rs[i]=null;
			}
		}
		return rs;
	}

	/**
	 * Creates a new {@link RSReader} that operates on the prodiced wrapper hidding the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location
	 * 
	 * @param rs The {@link RSLocationWrapper} that hides the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location 
	 */
	protected RSReader(RSLocationWrapper rs){
		this.rs=rs;
	}
	
	/**
	 * Creates a new {@link RSLocator} pointing to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * this {@link RSReader} points to
	 * @see RSLocator
	 * 
	 * @return The {@link RSLocator}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocator getRSLocator() throws Exception{
		RSLocator locator=this.rs.getRSLocator();
		if(locator==null){
			log.error("loctor is null. THrowin Exception");
			throw new Exception("loctor is null. THrowin Exception");
		}
		return locator;
	}
	
	/**
	 * Checks whether the underlying RS is beeing fully produced by its author or is incrementaly
	 * updated on request
	 * 
	 * @return <code>true</code> if results are generated on request, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFlowControled() throws Exception{
		return this.rs.isFlowContoled();
	}
	
	/**
	 * This operation wraps the current resource in a WSRF web service fron end. The resource beeing read must be local
	 * 
	 * @param type The type of resource to create. Must be {@link RSResourceWSRFType}
	 * @return The locator of the resource created
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSLocator wrap(RSResourceType type) throws Exception{
		if (!(type instanceof RSResourceWSRFType) && !(type instanceof RSResourceWSType)) {
			log.error("Cannot wrap to a local Resource. Throwin Exception");
			throw new Exception("Cannot wrap to a local Resource"); 
		}
		try{
			return this.rs.wrap(type);
		}catch(Exception e){
			log.error("Cannot wrap to a local Resource. Throwin Exception",e);
			throw new Exception("Cannot wrap to a local Resource"); 
		}
	}
	
	/**
	 * Retireves the properties form the head part that are of the specified type
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getProperties(java.lang.String)
	 * 
	 * @param template The {@link PropertyElementBase} extending class type that should be used to 
	 * instantiate the returned property elements
	 * @param type The type of properties to retrieve
	 * @return The array of property elements
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementBase []getProperties(Class template,String type)throws Exception{
		String []props=null;
		try{
			props=rs.getProperties(type);
			if(props==null){
				return new PropertyElementBase[0];
			}
			PropertyElementBase []ret=new PropertyElementBase[props.length];
			for(int i=0;i<props.length;i+=1){
				try{
					PropertyElementBase tmp=((PropertyElementBase)template.newInstance());
					tmp.fromXML(props[i]);
					ret[i]=tmp;
				}catch(Exception e){
					log.error("Could not instnatiate property element from xml. Adding null and continuing",e);
				}
			}
			return ret;
		}catch(Exception e){
			log.error("Could not retrieve properties.Throwing Exception",e);
			throw new Exception("Could not retrieve properties");
		}
	}
	
	/**
	 * Clears the underlying structures
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void clear() throws Exception{
		try{
			this.rs.clear();
		}catch(Exception e){
			log.error("Could not clear underlying structures.Throwing Exception",e);
			throw new Exception("Could not clear underlying structures");
		}
	}
	
	/**
	 * Moves the {@link RSReader} to point to the next payload part waiting for the default expiration
	 * time for the next part to be created
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNextPart(int)
	 * 
	 * @return <code>true</code> if a next part exists and the move has been performed,<code>false</code> if this is the last part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getNextPart() throws Exception{
		try{
			return rs.getNextPart(-1);
		}catch(Exception e){
			log.error("Could not move to next part.Throwing Exception",e);
			throw new Exception("Could not move to next part");
		}
	}
	
	/**
	 * Checks without blocking if the next part is available
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean nextAvailable() throws Exception{
		try{
			return rs.nextAvailable();
		}catch(Exception e){
			log.error("Could not check if next part is available.Throwing Exception",e);
			throw new Exception("Could not check if next part is available");
		}
	}
	
	/**
	 * Moves the {@link RSReader} to point to the next payload part 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNextPart(int)
	 * 
	 * @param waitMax The maximum amount of millisecond the {@link RSReader} should block waiting for the next
	 * part to be produced
	 * @return <code>true</code> if a next part exists and the move has been performed,<code>false</code> if this is the last part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getNextPart(int waitMax) throws Exception{
		try{
			return rs.getNextPart(waitMax);
		}catch(Exception e){
			log.error("Could not move to next part.Throwing Exception",e);
			throw new Exception("Could not move to next part");
		}
	}

	/**
	 * Moves the {@link RSReader} to point to the previous payload part
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getPreviousPart()
	 * 
	 * @return <code>true</code> if a previous part exists and the move has been performed,<code>false</code> if this is the last part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getPreviousPart() throws Exception{
		try{
			return rs.getPreviousPart();
		}catch(Exception e){
			log.error("Could not move to previous part.Throwing Exception",e);
			throw new Exception("Could not move to previous part");
		}
	}

	/**
	 * Moves the {@link RSReader} to point to the first payload part
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getFirstPart()
	 * 
	 * @return boolean balue indication if the move was possible and succesful
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean getFirstPart() throws Exception{
		try{
			return rs.getFirstPart();
		}catch(Exception e){
			log.error("Could not move to head part.Throwing Exception",e);
			throw new Exception("Could not move to head part");
		}
	}

	/**
	 * Checks if the current part is the first payload part
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#isFirst()
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isFirst() throws Exception{
		try{
			return rs.isFirst();
		}catch(Exception e){
			log.error("Could not check if is First.Throwing Exception",e);
			throw new Exception("Could not check if is First");
		}
	}

	/**
	 * Checks if the current part is the last part
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#isLast()
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isLast() throws Exception{
		try{
			return rs.isLast();
		}catch(Exception e){
			log.error("Could not check if is last.Throwing Exception",e);
			throw new Exception("Could not check if is last");
		}
	}

	/**
	 * Checks if the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} this {@link RSReader}
	 * point to receids in the same host with the {@link RSReader}
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHostIP()
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public boolean isLocal() throws Exception{
		try{
			return rs.isLocal();
		}catch(Exception e){
			log.error("Could not check if is local. Throwing Exception",e);
			throw new Exception("Could not check if is local");
		}
	}
	
	/**
	 * Retrieves an xml serialization of the custom properties this {@link ResultSet} has
	 * @see ResultSet#retrieveCustomProperties()
	 * 
	 * @return The proeprties serialization
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String retrieveCustomProperties() throws Exception{
		try{
			return rs.retrieveCustomProperties();
		}catch(Exception e){
			log.error("Could not retrieve Custom Properties. Throwing Exception",e);
			throw new Exception("Could not retrieve Custom Properties");
		}
	}
	
	/**
	 * Retrieves the underlying location independent instance
	 * @see RSLocationWrapper
	 * 
	 * @return the location wrapping element
	 */
	public RSLocationWrapper getLocationIndependent(){
		return this.rs;
	}

	/**
	 * Creates a new {@link RSReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element. Depending on the provided
	 * {@link RSResourceType} the created {@link RSReader} will point to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end. The operation is non blocking. The 
	 * created {@link RSReader} is returned while on the background the localization is still on going if nessecary
	 * @see RSReader#isLocal()
	 * 
	 * @param type The type of the Resource to be created
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSReader makeLocal(RSResourceType type) throws Exception{
		try{
			//TODO for the future. Too many WS calls. Replace with one get properties. 
			//TODO for the future. In case of a local RS you only need to drop the extra properties and copy the content UNLESS it is encrypted
			if(isLocal() && !(rs.isForward()) && !(rs.hasAccessLeasing())
					&& !(rs.hasTimeLeasing()) && !(rs.isSecure())){
				
				log.trace("RS to make local is an oldstyle actualy local RS.");
				if(rs.getRSLocator().getRSResourceType() instanceof RSResourceLocalType){
					log.trace("RS to make local with local request.");
					return RSReader.getRSReader(rs.getRSLocator());
				}
				else if(rs.getRSLocator().getRSResourceType() instanceof RSResourceWSRFType){
					log.trace("RS to make local with remote request.");
					return RSReader.getRSReader(new RSLocator(new RSResourceLocalType(),rs.getHeadFileName()));
				}
				else{
					log.error("RSResource type not recognized. Throwing Exception");
					throw new Exception("RSResource type not recognized");
				}
			}
			else{
				log.trace("Makelocal called for a rely remote RS");
				RSReader thisReader=RSReader.getRSReader(getRSLocator());
				if (getRSLocator().getPrivKey() != null)
					log.info("Reader locator har private key");
				String properties=thisReader.retrieveCustomProperties();
				RSFullWriter writer=RSFullWriter.getRSFullWriter(properties);
				if(!(type instanceof RSResourceLocalType) && !(type instanceof RSResourceWSRFType)) {
					log.error("not regognized resource type.Throwing Exception");
					throw new Exception("not regognized resource type.Throwing Exception");
				}
				MakeLocalThreadGeneric worker=new MakeLocalThreadGeneric(writer,thisReader,MakeLocalThreadGeneric.ENCODED,rs.getStaticPort(), rs.getSSLsupport());
				worker.start();
				return RSReader.getRSReader(writer.getRSLocator(type));
			}
		}catch(Exception e){
			log.error("Could not make local. Throwing Exception",e);
			throw new Exception("Could not make local");
		}
	}
	
	/**
	 * Creates a new {@link RSReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element up until the provided number of
	 * parts is mirrored. Depending on the provided {@link RSResourceType} the created {@link RSReader} will point to the 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} either directly as a java element or through
	 * a web service front end. Thius operation is non blocking. The created {@link RSReader} is returned while on the background
	 * the localization is still on going if nessecary
	 * @see RSReader#isLocal()
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param type The type of the Resource to be created
	 * @param count The number of results to localize
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSReader makeLocal(RSResourceType type,int count) throws Exception{
		if(count<0){
			log.error("invalid topCount argument. Throwing Exception");
			throw new Exception("invalid topCount argument");
		}
		try{
			return keepTop(count).makeLocal(type);
		}catch(Exception e){
			log.error("could not perform partial localization. Throwing Exception",e);
			throw new Exception("could not perform partial localization");
		}
	}
	
	/**
	 * Creates a new {@link RSReader} whose content is the top count of the parts the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds The current {@link RSReader} is not altered. The operation is non blocking.
	 * The {@link RSReader} will be created and returned and on the background the top operation will 
	 * continue. The computation will take part on the service side that holds the referenced by this {@link RSReader} {@link ResultSet}.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that are provided.
	 * @see RSReader#keepTop(int, PropertyElementBase[])
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param count The top count that should be kept
	 * @param properties The properties that should be added to the new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} head part
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSReader keepTop(int count,PropertyElementBase[] properties) throws Exception{
		try{
			if(properties==null || properties.length==0){
				log.error("Cannot initialize Result Set with empty property list. Throwing Exception");
				throw new Exception("Cannot initialize Result Set with empty property list");
			}
			String []props=new String[properties.length];
			for(int i=0;i<properties.length;i+=1) props[i]=properties[i].RS_toXML();
			String headName=this.rs.keepTop(count,props,KeepTopThreadGeneric.PERPART);
			RSLocator newLocator=null;
			if(this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator=new RSLocator(new RSResourceLocalType(),headName);
			}
			else{
				newLocator=this.rs.wrap(new RSResourceWSRFType(),headName,this.getRSLocator().getURI().toString());
			}
			return RSReader.getRSReader(newLocator);
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Creates a new {@link RSReader} whose content is the top count of the parts the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds The current {@link RSReader} is not altered. The operation is non blocking.
	 * The {@link RSReader} will be created and returned and on the background the top operation will 
	 * continue. The computation will take part on the service side that holds the referenced by this {@link RSReader} {@link ResultSet}.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that the current one has
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param count The top count that should be kept
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSReader keepTop(int count) throws Exception{
		try{
			String headName=this.rs.keepTop(count,KeepTopThreadGeneric.PERPART);
			RSLocator newLocator=null;
			if(this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator=new RSLocator(new RSResourceLocalType(),headName);
			}
			else{
				newLocator=this.rs.wrap(new RSResourceWSRFType(),headName,this.getRSLocator().getURI().toString());
			}
			return RSReader.getRSReader(newLocator);
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Creates a new {@link ResultSet} that is a exect copy of the current one. 
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that the current one has
	 * 
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSReader cloneRS() throws Exception{
		try{
			String headName=this.rs.cloneRS();
			RSLocator newLocator=null;
			if(this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator=new RSLocator(new RSResourceLocalType(),headName);
				newLocator.setPrivKey(getRSLocator().getPrivKey());
			}
			else{
				newLocator=this.rs.wrap(new RSResourceWSRFType(),headName,this.getRSLocator().getURI().toString());
				newLocator.setPrivKey(getRSLocator().getPrivKey());
				newLocator.setScope(getRSLocator().getScope());
			}
			return RSReader.getRSReader(newLocator);
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Performs the provided xPath expression on the document of the head part. The output
	 * of the ervaluation is returned in a string serialization
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnHead(java.lang.String)
	 * 
	 * @param xPath The xPath to be evaluated
	 * @return The serialization of the evaluation output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String executeQueryOnHead(String xPath) throws Exception{
		try{
			return rs.executeQueryOnHead(xPath);
		}catch(Exception e){
			log.error("Could not execute query. Throwing Exception",e);
			throw new Exception("Could not execute query");
		}
	}

	/**
	 * Retrieves the name of the current payload part
	 * 
	 * @return The current payload part
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public String getCurrentContentPart() throws Exception{
		try{
			return rs.getCurrentContentPart();
		}catch(Exception e){
			log.error("Could not retrieve current content part. Throwing Exception",e);
			throw new Exception("Could not retrieve current content part");
		}
	}

	/**
	 * Is the RS secure
	 * @return true if RS is encrypted
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public boolean isSecure() throws Exception{
			return rs.isSecure();
	}
}
