package org.gcube.common.searchservice.searchlibrary.rswriter;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementType;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementWellFormed;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.PoolConfig;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.PoolObjectConfig;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPool;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject.PoolObjectType;

/**
 * Factroy class for rs writers
 * 
 * @author UoA
 */
public class RSWriterFactory {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSWriterFactory.class);
	/**
	 * The pool
	 */
	private RSPool pool=null;
	/**
	 * Creates a new instance
	 * 
	 * @param poolConfig the pool configuration
	 */
	public RSWriterFactory(PoolConfig poolConfig){
		this.pool=new RSPool(poolConfig);
	}
	
	/**
	 * Creates a new {@link RSFullWriter} declaring that all results will be produced fully.
	 * @see {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#ResultSet(java.lang.String[])}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @return The {@link RSFilterWriter}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSFullWriter getRSFullWriter(PropertyElementBase []properties) throws Exception{
		try{
			RSFullWriter writer= (RSFullWriter)this.pool.GetObject(PoolObjectType.WriterFull);
			writer.overrideProperties(properties);
			return writer;
		}catch(Exception e){
			log.error("Could not create full writer. Throwing Exception",e);
			throw new Exception("Could not create full writer");
		}
	}
	
	/**
	 * Creates a new {@link RSFullWriter} declaring that all results will be produced fully. 
	 * @see {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#ResultSet(java.lang.String)}
	 * 
	 * @param properties The properties the {@link ResultSet} must have
	 * @return The {@link RSFilterWriter}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSFullWriter getRSFullWriter(String properties) throws Exception{
		try{
			RSFullWriter writer= (RSFullWriter)this.pool.GetObject(PoolObjectType.WriterFull);
			writer.overrideProperties(properties);
			return writer;
		}catch(Exception e){
			log.error("Could not create full writer. Throwing Exception",e);
			throw new Exception("Could not create full writer");
		}
	}

	/**
	 * Creates a new {@link RSTEXTWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified}  declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSTEXTWriter getRSTEXTWriter() throws Exception{
		try{
			RSTEXTWriter writer= (RSTEXTWriter)this.pool.GetObject(PoolObjectType.WriterFull);
			PropertyElementWellFormed wf=null;
			PoolObjectConfig conf=this.pool.getConfig().get(PoolObjectType.WriterText);
			boolean wellformed=false;
			if(conf!=null) wellformed=conf.WellFormed;
			if(wellformed) wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			writer.overrideProperties(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.TEXT),wf});
			return writer;
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
	 * @return The created {@link RSTEXTWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSTEXTWriter getRSTEXTWriter(PropertyElementBase []properties) throws Exception{
		try{
			RSTEXTWriter writer= (RSTEXTWriter)this.pool.GetObject(PoolObjectType.WriterFull);
			PropertyElementWellFormed wf=null;
			PoolObjectConfig conf=this.pool.getConfig().get(PoolObjectType.WriterText);
			boolean wellformed=false;
			if(conf!=null) wellformed=conf.WellFormed;
			PropertyElementBase []props=new PropertyElementBase[properties.length+2];
			if(wellformed) wf=new PropertyElementWellFormed(PropertyElementWellFormed.YES);
			else wf=new PropertyElementWellFormed(PropertyElementWellFormed.NO);
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.TEXT);
			props[properties.length+1]=wf;
			writer.overrideProperties(props);
			return writer;
		}catch(Exception e){
			log.error("could not create RSTEXTWriter. Throwing Exception",e);
			throw new Exception("could not create RSTEXTWriter");
		}
	}

	/**
	 * Creates a new {@link RSXMLWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @return The created {@link RSXMLWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSXMLWriter getRSXMLWriter() throws Exception{
		try{
			RSXMLWriter writer=(RSXMLWriter)this.pool.GetObject(PoolObjectType.WriterXML);
			writer.overrideProperties(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.XML)});
			return writer;
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
	public RSXMLWriter getRSXMLWriter(PropertyElementBase []properties) throws Exception{
		try{
			RSXMLWriter writer=(RSXMLWriter)this.pool.GetObject(PoolObjectType.WriterXML);
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.XML);
			writer.overrideProperties(props);
			return writer;
		}catch(Exception e){
			log.error("could not create RSSimpelWriter. Throwing Exception",e);
			throw new Exception("could not create RSSimpelWriter");
		}
	}
	/**
	 * Creates a new {@link RSBLOBWriter} setting a property of {@link PropertyElementGC} with
	 * value {@link PropertyElementGC#unspecified} declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @return The created {@link RSBLOBWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSBLOBWriter getRSBLOBWriter() throws Exception{
		try{
			RSBLOBWriter writer=(RSBLOBWriter)this.pool.GetObject(PoolObjectType.WriterBLOB);
			writer.overrideProperties(new PropertyElementBase[]{new PropertyElementGC(PropertyElementGC.unspecified),new PropertyElementType(PropertyElementType.BLOB)});
			return writer;
		}catch(Exception e){
			log.error("could not create RSBLOBWriter. Throwing Exception",e);
			throw new Exception("could not create RSBLOBWriter");
		}
	}
	
	/**
	 * Creates a new {@link RSBLOBWriter} setting the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * properties to the provided ones declaring that all results will be produced fully.
	 * @see RSFullWriter#RSFullWriter(PropertyElementBase[])
	 * 
	 * @param properties The proeprties the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} must have
	 * @return The created {@link RSBLOBWriter}
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public RSBLOBWriter getRSBLOBWriter(PropertyElementBase []properties) throws Exception{
		try{
			RSBLOBWriter writer=(RSBLOBWriter)this.pool.GetObject(PoolObjectType.WriterBLOB);
			PropertyElementBase []props=new PropertyElementBase[properties.length+1];
			System.arraycopy(properties,0,props,0,properties.length);
			props[properties.length]=new PropertyElementType(PropertyElementType.BLOB);
			writer.overrideProperties(props);
			return writer;
		}catch(Exception e){
			log.error("could not create RSBLOBWriter. Throwing Exception",e);
			throw new Exception("could not create RSBLOBWriter");
		}
	}
}
