package org.gcube.common.searchservice.searchlibrary.rsreader;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.KeepTopThreadGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
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

/**
 * Generic text Reader class used to retrieve payload of a previously created {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * hidding its location
 * 
 * @author UoA
 */
public class RSTEXTReader extends RSReader{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSTEXTReader.class);
	/**
	 * The underlying location wrapping element
	 */
	RSLocationWrapper rs=null;
	
	/**
	 * Creates a new {@link RSTEXTReader} pointing to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that the provided {@link RSLocator} identifies
	 * 
	 * @param locator The {@link RSLocator} that identifies the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @return The {@link RSTEXTReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSTEXTReader getRSTEXTReader(RSLocator locator) throws Exception{
		try{
			return new RSTEXTReader(new RSLocationWrapper(locator));
		}catch(Exception e){
			log.error("Could not create RSXMLReader Throwing Exception",e);
			throw new Exception("Could not create RSXMLReader");
		}
	}
	
	/**
	 * Instantiates and localizes or not the readers that point to the specified locator
	 * 
	 * @param params the initialization parameters
	 * @return the created readers in the same order as their input. If some reader could not be initialized null is placed
	 */
	public static RSTEXTReader []getRSTEXTReader(ReaderInitParams []params){
		InitReaderThread []ts=new InitReaderThread[params.length];
		RSTEXTReader []rs=new RSTEXTReader[params.length];
		for(int i=0;i<params.length;i+=1){
			ts[i]=new InitReaderThread(params[i],RSReaderEnum.TEXTReader);
			ts[i].start();
		}
		for(int i=0;i<params.length;i+=1){
			try{
				ts[i].join();
				rs[i]=(RSTEXTReader)ts[i].getReader();
			}catch(Exception e){
				log.error("interrupted whil waiting for reader. setting to null");
				rs[i]=null;
			}
		}
		return rs;
	}

	/**
	 * Creates a new {@link RSTEXTReader} that operates on the prodiced wrapper hidding the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location
	 * 
	 * @param rs The {@link RSLocationWrapper} that hides the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * location 
	 */
	protected RSTEXTReader(RSLocationWrapper rs){
		super(rs);
		this.rs=rs;
	}
	
	/**
	 * Creates a new {@link RSTEXTReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSTEXTReader} and which is an exact mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element. Depending on the provided
	 * {@link RSResourceType} the created {@link RSTEXTReader} will point to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * either directly as a java element or through a web service front end. The operation is non blocking. The 
	 * created {@link RSTEXTReader} is returned while on the background the localization is still on going if nessecary
	 * @see RSReader#isLocal()
	 * 
	 * @param type The type of the Resource to be created
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTEXTReader makeLocal(RSResourceType type) throws Exception{
		try{
			if(isLocal()){
				if(rs.getRSLocator().getRSResourceType() instanceof RSResourceLocalType){
					return RSTEXTReader.getRSTEXTReader(rs.getRSLocator());
				}
				else if(rs.getRSLocator().getRSResourceType() instanceof RSResourceWSRFType){
					return RSTEXTReader.getRSTEXTReader(new RSLocator(new RSResourceLocalType(),rs.getHeadFileName()));
				}
				else{
					log.error("RSResource type not recognized. Throwing Exception");
					throw new Exception("RSResource type not recognized");
				}
			}
			else{
				RSReader thisReader=RSReader.getRSReader(getRSLocator());
				String properties=thisReader.retrieveCustomProperties();
				RSFullWriter writer=RSFullWriter.getRSFullWriter(properties);
				if(!(type instanceof RSResourceLocalType) && !(type instanceof RSResourceWSRFType)) {
					log.error("not regognized resource type.Throwing Exception");
					throw new Exception("not regognized resource type.Throwing Exception");
				}
				MakeLocalThreadGeneric worker=new MakeLocalThreadGeneric(writer,thisReader,MakeLocalThreadGeneric.CLEAR,rs.getStaticPort(), rs.getSSLsupport());
				worker.start();
				return RSTEXTReader.getRSTEXTReader(writer.getRSLocator(type));
			}
		}catch(Exception e){
			log.error("Could not make local. Throwing Exception",e);
			throw new Exception("Could not make local");
		}
	}
	
	/**
	 * Creates a new {@link RSTEXTReader} that points to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * located in the same host as the {@link RSTEXTReader} and which is an exact mirror of the currently underlying
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element up until the provided number of
	 * parts is mirrored. Depending on the provided {@link RSResourceType} the created {@link RSTEXTReader} will point to the 
	 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} either directly as a java element or through
	 * a web service front end. Thius operation is non blocking. The created {@link RSTEXTReader} is returned while on the background
	 * the localization is still on going if nessecary
	 * @see RSReader#isLocal()
	 * @see RSTEXTReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param type The type of the Resource to be created
	 * @param count The number of results to localize
	 * @return The created {@link RSReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTEXTReader makeLocal(RSResourceType type,int count) throws Exception{
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
	 * Creates a new {@link RSTEXTReader} whose content is the top count of the parts the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds The current {@link RSTEXTReader} is not altered. The operation is non blocking.
	 * The {@link RSTEXTReader} will be created and returned and on the background the top operation will 
	 * continue. The computation will take part on the service side that holds the referenced by this {@link RSTEXTReader} {@link ResultSet}.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSTEXTReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSTEXTReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that are provided.
	 * @see RSTEXTReader#keepTop(int, PropertyElementBase[])
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param count The top count that should be kept
	 * @param properties The properties that should be added to the new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} head part
	 * @return The created {@link RSTEXTReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTEXTReader keepTop(int count,PropertyElementBase[] properties) throws Exception{
		try{
			return RSTEXTReader.getRSTEXTReader(super.keepTop(count,properties).getRSLocator());
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Creates a new {@link RSTEXTReader} whose content is the top count of the parts the current {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * holds The current {@link RSTEXTReader} is not altered. The operation is non blocking.
	 * The {@link RSTEXTReader} will be created and returned and on the background the top operation will 
	 * continue. The computation will take part on the service side that holds the referenced by this {@link RSTEXTReader} {@link ResultSet}.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSTEXTReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSTEXTReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that the current one has
	 * @see RSReader#keepTop(int)
	 * @see KeepTopThreadGeneric#PERPART
	 * 
	 * @param count The top count that should be kept
	 * @return The created {@link RSTEXTReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTEXTReader keepTop(int count) throws Exception{
		try{
			return RSTEXTReader.getRSTEXTReader(super.keepTop(count).getRSLocator());
		}catch(Exception e){
			log.error("Could not keep top. Throwing Exception",e);
			throw new Exception("Could not keep top");
		}
	}
	
	/**
	 * Creates a new {@link ResultSet} that is a exect copy of the current one. 
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that will be 
	 * created and pointed to by the created {@link RSTEXTReader}
	 * will be located to the same host as the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} the
	 * current {@link RSTEXTReader} point to and will have the same access type.
	 * The new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} will have will have the properties
	 * that the current one has
	 * 
	 * @return The created {@link RSTEXTReader}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSTEXTReader cloneRS() throws Exception{
		try{
			return RSTEXTReader.getRSTEXTReader(super.cloneRS().getRSLocator());
		}catch(Exception e){
			log.error("Could not clone rs. Throwing Exception",e);
			throw new Exception("Could not clone rs");
		}
	}
	
	/**
	 * Retrieves the full payload of the current content part
	 * 
	 * @return The full payload of the curren content part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getFullPayload() throws Exception{
		try{
			return this.rs.getCurrentContentPartPayload();
		}catch(Exception e){
			log.error("Could not get payload. Throwing Exception",e);
			throw new Exception("Could not get payload");
		}
	}
	
	/**
	 * Retrieves the number of rsults in the current payload part
	 * 
	 * @return The number of results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public int getNumberOfResults() throws Exception{
		try{
			return rs.getNumberOfResults(PropertyElementType.TEXT);
		}catch(Exception e){
			log.error("Could not get number of results. Throwing Exception",e);
			throw new Exception("Could not get number of results");
		}
	}
}
