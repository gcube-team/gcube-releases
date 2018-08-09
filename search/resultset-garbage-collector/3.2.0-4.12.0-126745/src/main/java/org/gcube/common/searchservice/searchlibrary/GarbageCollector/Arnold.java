package org.gcube.common.searchservice.searchlibrary.GarbageCollector;


import java.io.File;
import java.io.StringReader;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.gcube.common.searchservice.resultsetservice.stubs.ResultSetPortType;
import org.gcube.common.searchservice.resultsetservice.stubs.service.ResultSetServiceAddressingLocator;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.WSRSSessionToken;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.oasis.wsrf.lifetime.Destroy;
import org.xml.sax.InputSource;

/**
 * Class that handles destruction of resources, locally form the filesystem and WSRF resources
 * 
 * @author UoA
 */
public class Arnold {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(Arnold.class);

	/**
	 * Clears resources that are indicated by the provided properties element
	 * 
	 * @param props The properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 */
	public static void terminate(GCProperties props){
		for(int i=0;i<props.getChainOfFiles().size();i+=1){
			String dir = null;
			try{
				File tmp=new File(props.getChainOfFiles().get(i));
				tmp.delete();
				dir = tmp.getParent();
				log.trace("deleting file (name retrivied from metadata .rs ) "+props.getChainOfFiles().get(i));
			}catch(Exception e){
				log.error("could not delete file "+props.getChainOfFiles().get(i)+" continuing",e);
			}
			try{
				File tmp=new File(RSFileHelper.headerToContent(props.getChainOfFiles().get(i)));
				tmp.delete();
				log.trace("deleting file (name retrivied from metadata .hrs )"+RSFileHelper.headerToContent(props.getChainOfFiles().get(i)));
			}catch(Exception e){
				log.error("could not delete content file of "+props.getChainOfFiles().get(i)+" continuing",e);
			}
			try{
				log.trace("try to remove the RS directory");
				File tmp=new File(dir);
				tmp.delete();
				log.trace("Directory "+dir+" removed");
			}catch(Exception e){
				log.error("could not remove directory",e);
			}
			
		}
		for(int i=0;i<props.getWSEPRs().size();i+=1){
			try{
				log.trace("destroying WSRF resource "+props.getWSEPRs().get(i));
				ResultSetServiceAddressingLocator instanceLocator = new ResultSetServiceAddressingLocator();
				EndpointReferenceType instanceEPR = (EndpointReferenceType) ObjectDeserializer.deserialize(new InputSource(new StringReader(props.getWSEPRs().get(i))),EndpointReferenceType.class);
				ResultSetPortType resultset=instanceLocator.getResultSetPortTypePort(instanceEPR);
				resultset.destroy(new Destroy());
				log.trace("destroyed WSRF resource");
			}catch(Exception e){
				try{
					WSRSSessionToken token=WSRSSessionToken.deserialize(props.getWSEPRs().get(i));
					EndpointReferenceType endpoint = new EndpointReferenceType();
					endpoint.setAddress(new Address(token.getServiceInstance()));
					ResultSetServiceAddressingLocator rlocator= new ResultSetServiceAddressingLocator();
					ResultSetPortType resultset= rlocator.getResultSetPortTypePort(endpoint);
					resultset.destroySession(token.getSessionToken());
				}catch(Exception ee){
					log.error("could not destroy WSRF resource. continuing",ee);
				}
			}
		}
	}
	
	/**
	 * Removes the provided file from local storage
	 * 
	 * @param file The file to delete
	 */
	public static void terminate(File file){
		try{
			file.delete();
			log.trace("deleting file "+file.toString());
		}catch(Exception e){
			log.error("could not delete file "+file.toString()+" continuing",e);
		}
	}
}
