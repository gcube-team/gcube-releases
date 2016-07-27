package org.gcube.informationsystem.notifier.util;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.AttributedURI; 
import org.gcube.common.core.utils.logging.GCUBELog;

import java.io.IOException;
import java.net.*;
import javax.net.SocketFactory;


/**
 * 
 * 
 * @author Christoph Langguth
 *
 */
public class Util {
	
	private static GCUBELog log = new GCUBELog (Util.class);
	
	private static int  SOCKET_CONNECT_TIMEOUT_MS = 5000;
	
	/**
     * Determines whether a given EndpointReference is reachable.
     * The rationale for the existence of this method is the following: in Globus,
     * there is no way to differenciate between a connection timeout and a socket timeout.
     * Since the execution engine cannot know in advance how long a service invocation
     * is supposed to take, the socket timeout is set to infinite in the engine. This,
     * however, creates a problem when an endpoint is not reachable (firewalled or otherwise
     * not responding), because the system will hang forever, while not even being connected to
     * the endpoint. Therefore, it is advisable to call this method before making the actual
     * service request. This method merely tries to establish a socket connection to the
     * given EPR, which is immediately closed; however it times out after a reasonable
     * amount of time (5 seconds).
     * @param epr the EPR to check for availability
     * @return true if a successful socket connection could be established, false if there
     * was a problem with the parameters, or if the connection could not be established.
     * @see SOCKET_CONNECT_TIMEOUT_MS
     */
	
    public static boolean isEndpointReachable(EndpointReferenceType epr) {
    		boolean result= false;
    	
    		if (epr == null) {
                    log.info("returning false because EPR is null");
                    return false;
            }
            AttributedURI address = epr.getAddress();
            if (address == null) {
                    log.info("returning false because address in EPR is null");
                    return false;
            }
            String host = address.getHost();
            int port = address.getPort();
            log.trace("is the host "+ host+" in the port "+port+" reachable?");
            Socket socket=null;
            try {
                    socket = SocketFactory.getDefault().createSocket();
                    SocketAddress socketAddress = new InetSocketAddress(host,port);
                    socket.connect(socketAddress, SOCKET_CONNECT_TIMEOUT_MS);
                    socket.close();
                    result=true;
                    log.trace("the Address is reachable "+address);
            } catch (Throwable t) {
            		log.warn("Endpoint at "+host+":"+port+" is not reachable, Exception",t);
                    result= false;
            } finally {
    				if(socket != null)
						try {
							socket.close();
						} catch (IOException e) {
							log.warn("is not possible to close the socket");
						}
    		}
            log.trace("the Endpoint is reachable - "+result);
            return result;
    }
                                                                                                                                                         

}
