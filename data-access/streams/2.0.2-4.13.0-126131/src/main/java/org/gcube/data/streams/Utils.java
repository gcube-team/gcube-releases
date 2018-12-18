package org.gcube.data.streams;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;

import java.net.InetAddress;
import java.util.ArrayList;

import org.gcube.data.streams.exceptions.StreamContingency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Library utils.
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Initialises gRS2 library.
	 */
	public static synchronized void initialiseRS() {
		
		if (TCPConnectionManager.IsInitialized())
			return;
		
		log.info("gRS2 is not initialised: using defaults");
		
		String host =null;
		try {
			host = InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e) {
			log.info("could not discover hostname, using 'localhost' to allow offline usage");
			host="localhost";
		}
		
		try {
			TCPConnectionManager.Init(new TCPConnectionManagerConfig(host,new ArrayList<PortRange>(),true));
			TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Indicates whether a failure or its indirect causes are annotated with {@link StreamContingency}.
	 * @param t the failure
	 * @return <code>true</code> if the failure or its indirect causes are annotated with {@link StreamContingency}.
	 */
	public static boolean isContingency(Throwable t) {
		return t.getClass().isAnnotationPresent(StreamContingency.class)
				|| ((t.getCause()!=null) &&(isContingency(t.getCause())));
	}
		
}
