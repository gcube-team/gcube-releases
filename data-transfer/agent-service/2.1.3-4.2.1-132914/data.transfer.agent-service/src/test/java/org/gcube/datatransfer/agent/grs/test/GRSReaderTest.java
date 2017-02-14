/**
 * 
 */
package org.gcube.datatransfer.agent.grs.test;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

/**
 * @author andrea
 *
 */
public class GRSReaderTest {

	GRSWriter writer = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		List<PortRange> ports=new ArrayList<PortRange>(); //The ports that the TCPConnection manager should use
		    ports.add(new PortRange(3000, 3050));             //Any in the range between 3000 and 3050
		    ports.add(new PortRange(3055, 3055));             //Port 3055
		    TCPConnectionManager.Init(
		      new TCPConnectionManagerConfig("localhost", //The hostname by which the machine is reachable 
		        ports,                                    //The ports that can be used by the connection manager
		        true                                      //If no port ranges were provided, or none of them could be used, use a random available port
		    ));
		    TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());      //Register the handler for the gRS2 incoming requests
		    TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler()); //Register the handler for the gRS2 store incoming requests
		    try {
				 writer = new GRSWriter(new TCPWriterProxy());
			} catch (GRS2WriterException e) {
				e.printStackTrace();
			}
		    // gr.uoa.di.madgik.grs.prox
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void test() throws GRS2ReaderException, GRS2WriterException, InterruptedException {
		
		GRSReader reader=new GRSReader(writer.getLocator());
		 
	    writer.start();
	    reader.start();
	 
	    writer.join();
	    reader.join();
		
	}

}
