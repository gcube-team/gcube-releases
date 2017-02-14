package org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementLifeSpanGC;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSType;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSBLOBWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSFullWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSTEXTWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter;

/**
 * interface defining that an object can be included in the pool
 * 
 * @author UoA
 */
public abstract class RSPoolObject {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSPoolObject.class);
	/**
	 * The objects types supported
	 * 
	 * @author UoA
	 */
	public static enum PoolObjectType{
		/**
		 * Full writer
		 */
		WriterFull,
		/**
		 * Blob writer
		 */
		WriterBLOB,
		/**
		 * Text Writer
		 */
		WriterText,
		/**
		 * XML Writer
		 */
		WriterXML
	}
	
	/**
	 * The type of resource to use
	 * 
	 * @author UoA
	 */
	public static enum PoolObjectResourceType{
		/**
		 * WSRF type of resource
		 */
		WSRFType,
		/**
		 * WS type of resource
		 */
		WSType
	}
	
	/**
	 * retrieves the class of the ovject
	 * 
	 * @param type the type of object
	 * @param config the config for the object
	 * @return the class
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static RSPoolObject getPoolObjectInstance(PoolObjectType type,PoolObjectConfig config) throws Exception{
		switch(type){
			case WriterBLOB:{
				RSBLOBWriter writer=RSBLOBWriter.getRSBLOBWriter(new PropertyElementBase[]{new PropertyElementLifeSpanGC(Long.MAX_VALUE)},config.FlowControl);
				switch(config.ResourceType){
					case WSRFType:{
						log.debug("instantiating object "+PoolObjectType.WriterBLOB.toString()+":"+PoolObjectResourceType.WSRFType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSRFType());
						else writer.getRSLocator(new RSResourceWSRFType(config.ServiceEndPoint));
						break;
					}
					case WSType:{
						log.debug("instantiating object "+PoolObjectType.WriterBLOB.toString()+":"+PoolObjectResourceType.WSType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSType());
						else writer.getRSLocator(new RSResourceWSType(config.ServiceEndPoint));
						break;
					}
				}
				return writer;
			}
			case WriterFull:{
				RSFullWriter writer=RSFullWriter.getRSFullWriter(new PropertyElementBase[]{new PropertyElementLifeSpanGC(Long.MAX_VALUE)},config.FlowControl);
				switch(config.ResourceType){
					case WSRFType:{
						log.debug("instantiating object "+PoolObjectType.WriterFull.toString()+":"+PoolObjectResourceType.WSRFType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSRFType());
						else writer.getRSLocator(new RSResourceWSRFType(config.ServiceEndPoint));
						break;
					}
					case WSType:{
						log.debug("instantiating object "+PoolObjectType.WriterFull.toString()+":"+PoolObjectResourceType.WSType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSType());
						else writer.getRSLocator(new RSResourceWSType(config.ServiceEndPoint));
						break;
					}
				}
				return writer;
			}
			case WriterText:{
				RSTEXTWriter writer=RSTEXTWriter.getRSTEXTWriter(new PropertyElementBase[]{new PropertyElementLifeSpanGC(Long.MAX_VALUE)},config.WellFormed,config.FlowControl);
				switch(config.ResourceType){
					case WSRFType:{
						log.debug("instantiating object "+PoolObjectType.WriterText.toString()+":"+PoolObjectResourceType.WSRFType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSRFType());
						else writer.getRSLocator(new RSResourceWSRFType(config.ServiceEndPoint));
						break;
					}
					case WSType:{
						log.debug("instantiating object "+PoolObjectType.WriterText.toString()+":"+PoolObjectResourceType.WSType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSType());
						else writer.getRSLocator(new RSResourceWSType(config.ServiceEndPoint));
						break;
					}
				}
				return writer;
			}
			case WriterXML:{
				RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementLifeSpanGC(Long.MAX_VALUE)},config.FlowControl);
				switch(config.ResourceType){
					case WSRFType:{
						log.debug("instantiating object "+PoolObjectType.WriterXML.toString()+":"+PoolObjectResourceType.WSRFType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSRFType());
						else writer.getRSLocator(new RSResourceWSRFType(config.ServiceEndPoint));
						break;
					}
					case WSType:{
						log.debug("instantiating object "+PoolObjectType.WriterXML.toString()+":"+PoolObjectResourceType.WSType.toString());
						if(config.ServiceEndPoint==null) writer.getRSLocator(new RSResourceWSType());
						else writer.getRSLocator(new RSResourceWSType(config.ServiceEndPoint));
						break;
					}
				}
				return writer;
			}
		}
		throw new Exception("Non recognizable pool object type");
	}
}
