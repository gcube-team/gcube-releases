package org.gcube.common.searchservice.searchlibrary.rsreader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.InitReaderThread;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.MakeLocalThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocationWrapper;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.ReaderInitParams;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.InitReaderThread.RSReaderEnum;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSFullWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSWriterCreationParams;


/**
 * Generic BLOB Reader class used to retrieve payload of a previously created {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * hidding its location
 * The safe way to read the contents of a BLOB resultset is through the use of an iterator and after having localized
 * 
 * @author UoA
 */
public class RSBLOBReader extends RSReader{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSBLOBReader.class);
	/**
	 * The underlying location independent element
	 */
	RSLocationWrapper rs=null;
	
	/**
	 * Creates a new {@link RSBLOBReader} pointing to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that the provided {@link RSLocator} identifies
	 * 
	 * @param locator The {@link RSLocator} that identifies the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @return The {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSBLOBReader getRSBLOBReader(RSLocator locator) throws Exception{
		try{
			return new RSBLOBReader(new RSLocationWrapper(locator));
		}catch(Exception e){
			log.error("Could not create RSBLOBReader Throwing Exception",e);
			throw new Exception("Could not create RSBLOBReader");
		}
	}
	
	/**
	 * Instantiates and localizes or not the readers that point to the specified locator
	 * 
	 * @param params the initialization parameters
	 * @return the created readers in the same order as their input. If some reader could not be initialized null is placed
	 */
	public static RSBLOBReader []getRSBLOBReader(ReaderInitParams []params){
		InitReaderThread []ts=new InitReaderThread[params.length];
		RSBLOBReader []rs=new RSBLOBReader[params.length];
		for(int i=0;i<params.length;i+=1){
			ts[i]=new InitReaderThread(params[i],RSReaderEnum.BLOBReader);
			ts[i].start();
		}
		for(int i=0;i<params.length;i+=1){
			try{
				ts[i].join();
				rs[i]=(RSBLOBReader)ts[i].getReader();
			}catch(Exception e){
				log.error("interrupted whil waiting for reader. setting to null");
				rs[i]=null;
			}
		}
		return rs;
	}

	/**
	 * Creates a new {@link RSBLOBReader} that operates on the prodiced wrapper hidding the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location
	 * 
	 * @param rs The {@link RSLocationWrapper} that hides the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location 
	 */
	protected RSBLOBReader(RSLocationWrapper rs){
		super(rs);
		this.rs=rs;
	}
	
	/**
	 * Creates a new {@link RSBLOBIterator} over the underlying {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * this {@link RSBLOBReader} is initialized to point to
	 * MAKE SURE YOU HAVE CALLED MAKE LOCAL BEFORE INSTANTIATING AN ITERATOR
	 * @see RSIterator
	 * 
	 * @return The {@link RSBLOBIterator}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBIterator getRSIterator() throws Exception{
		try{
			return new RSBLOBIterator(RSBLOBReader.getRSBLOBReader(this.getRSLocator()));
		}catch(Exception e){
			log.error("could not create iterator. Throwing Exception",e);
			throw new Exception("could not create iterator");
		}
	}

	/**
	 * Retrieves the result with the specified index of the current payload part
	 * MAKE SURE YOU HAVE CALLED MAKE LOCAL BEFORE TRYING TO RETRIEVE AN OBJECT
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResult(int)
	 * 
	 * @param template The {@link ResultElementBLOBBase} extending class type that should be used to
	 * instantiate the returned result element
	 * @return The result element
	 */
	protected ResultElementBLOBBase getResults(Class template){
		log.info("getResults called.");
		try{
			if(!this.getNextPart()) return null;
			ResultElementBLOBBase tmp=(ResultElementBLOBBase)template.newInstance();
			tmp.RS_fromXML(rs.getResults(0));
			if(!this.getNextPart()){
				log.error("incomplete chain of pages. Could not retrieve content. returning null");
			}
			if (rs.isSecure()){
				log.info("RS is secure.");
//				tmp.setContentOfBLOB(new StringInputStream(rs.getCurrentContentPartPayload()));
				tmp.setContentOfBLOB(new ByteArrayInputStream(rs.getCurrentContentPartPayload().getBytes()));
			}else{
				log.info("RS is not secure.");
				tmp.setContentOfBLOB(new FileInputStream(new File(this.getCurrentContentPart())));
			}
			return tmp;
		}catch(Exception e){
//			log.info("Could not get result. returning null",e);
			log.info("Could not get result. returning null "+e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a new {@link RSBLOBReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSReader} and which is an exact mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element. Depending on the provided
	 * {@link RSResourceType} the created {@link RSBLOBReader} will point to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end. Thius operation is non blocking. The 
	 * created {@link RSBLOBReader} is returned while on the background the localization is still on going if nessecary
	 * @see RSReader#isLocal()
	 * @see RSReader#wrap(RSResourceType)
	 * 
	 * @param type The type of the Resource to be created
	 * @return The created {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBReader makeLocal(RSResourceType type) throws Exception{
		log.trace("RS makelocal called");
		return RSBLOBReader.getRSBLOBReader(super.makeLocal(type).getRSLocator());
	}
	
	/**
	 * Creates a new {@link RSBLOBReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSBLOBReader} and which is an exact mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element up until the provided number of
	 * results is mirrored. Depending on the provided {@link RSResourceType} the created {@link RSBLOBReader} will point to the 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} either directly as a java element or through
	 * a web service front end. Thius operation is non blocking. The created {@link RSBLOBReader} is returned while on the background
	 * the localization is still on going if nessecary
	 * @see RSReader#isLocal()
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param type The type of the Resource to be created
	 * @param count The number of results to localize
	 * @return The created {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBReader makeLocal(RSResourceType type,int count) throws Exception{
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
	 * Creates a new {@link RSBLOBReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSBLOBReader}.
	 * Depending on the provided {@link RSResourceType} the created {@link RSBLOBReader} will point to the 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} either directly as a java element or through
	 * a web service front end. This operation is non blocking. The created {@link RSBLOBReader} is returned while on the background
	 * the localization is still on going if necessary
	 * @see RSReader#isLocal()
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param type The type of the Resource to be created
	 * @param params new RS creation parameters
	 * @param scope the scope of the RS
 	 * @return The created {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBReader makeLocal(RSResourceType type, RSWriterCreationParams params, GCUBEScope scope) throws Exception{
		try{
			RSReader thisReader=RSReader.getRSReader(getRSLocator());
			//String properties=thisReader.retrieveCustomProperties();
			RSFullWriter writer=RSFullWriter.getRSFullWriter(params);
			if(!(type instanceof RSResourceLocalType) && !(type instanceof RSResourceWSRFType)) {
				log.error("not regognized resource type.Throwing Exception");
				throw new Exception("not regognized resource type.Throwing Exception");
			}
			MakeLocalThreadGeneric worker=new MakeLocalThreadGeneric(writer,thisReader,MakeLocalThreadGeneric.CLEAR,rs.getStaticPort(), rs.getSSLsupport());
			worker.start();
			RSLocator newlocator = writer.getRSLocator(type,scope,params.getPrivKey());
			newlocator.setPrivKey(params.getPrivKey());
			newlocator.setScope(scope);
			return RSBLOBReader.getRSBLOBReader(newlocator);
		}catch(Exception e){
			log.error("Could not make local. Throwing Exception",e);
			throw new Exception("Could not make local");
		}
	}

	
	/**
	 * Creates a new {@link RSBLOBReader} whose content is the top count of the results the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds preserving the records per part the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} has.
	 * The current {@link RSBLOBReader} is not altered. The operation is non blocking.
	 * The {@link RSBLOBReader} will be created and returned and on the background the top operation will 
	 * continue. The computation will take part on the service side that holds the referenced by this {@link RSBLOBReader} {@link ResultSet}.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSBLOBReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that are provided.
	 * @see RSReader#keepTop(int, PropertyElementBase[])
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param count The top count that should be kept
	 * @param properties The properties that should be added to the new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} head part
	 * @return The created {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBReader keepTop(int count,PropertyElementBase[] properties) throws Exception{
		try{
			if(properties==null || properties.length==0){
				log.error("Cannot initialize Result Set with empty property list. Throwing Exception");
				throw new Exception("Cannot initialize Result Set with empty property list");
			}
			String []props=new String[properties.length];
			for(int i=0;i<properties.length;i+=1) props[i]=properties[i].RS_toXML();
			String headName=this.rs.keepTop((2*count),props,KeepTopThreadGeneric.PERPART);
			RSLocator newLocator=null;
			if(this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator=new RSLocator(new RSResourceLocalType(),headName);
			}
			else{
				newLocator=this.rs.wrap(new RSResourceWSRFType(),headName,this.getRSLocator().getURI().toString());
			}
			return RSBLOBReader.getRSBLOBReader(newLocator);
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Creates a new {@link RSBLOBReader} whose content is the top count of the results the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds preserving the records per part the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} has.
	 * The current {@link RSBLOBReader} is not altered. The operation is non blocking.
	 * The {@link RSBLOBReader} will be created and returned and on the background the top operation will 
	 * continue. The computation will take part on the service side that holds the referenced by this {@link RSBLOBReader} {@link ResultSet}.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSBLOBReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that are provided.
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param count The top count that should be kept
	 * @return The created {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBReader keepTop(int count) throws Exception{
		try{
			String headName=this.rs.keepTop((2*count),KeepTopThreadGeneric.PERPART);
			RSLocator newLocator=null;
			if(this.getRSLocator().getRSResourceType() instanceof RSResourceLocalType) {
				newLocator=new RSLocator(new RSResourceLocalType(),headName);
			}
			else{
				newLocator=this.rs.wrap(new RSResourceWSRFType(),headName,this.getRSLocator().getURI().toString());
			}
			return RSBLOBReader.getRSBLOBReader(newLocator);
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Creates a new {@link ResultSet} that is a exect copy of the current one. 
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSBLOBReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSBLOBReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that the current one has
	 * 
	 * @return The created {@link RSBLOBReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSBLOBReader cloneRS() throws Exception{
		try{
			return RSBLOBReader.getRSBLOBReader(super.cloneRS().getRSLocator());
		}catch(Exception e){
			log.error("Could not clone rs. Throwing Exception",e);
			throw new Exception("Could not clone rs");
		}
	}
}
