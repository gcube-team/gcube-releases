package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.log4j.Logger;



/**
 * Listens to a predefined port and transfers result set parts to requestors
 * 
 * @author UoA
 */
public class StreamManager extends Thread{
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(StreamManager.class);
	/**
	 * The prot to listen to
	 */
	private int port=0;
	private boolean SSLsupport=true;

	/**
	 * Creates a new instance 
	 * 
	 * @param port the port to listen to
	 * @param SSLsupport SSL support
	 */
	public StreamManager(int port, boolean SSLsupport){
		log.trace("Stream Manager created and listenning on port "+ port);
		this.port=port;
		this.SSLsupport=SSLsupport;
	}

	/**
	 * The port used
	 * 
	 * @return the port
	 */
	public int getPort(){
		return this.port;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{

			ServerSocket sock = getServeSocket();
			while(true){
				try{
				Socket skt = sock.accept();
				StreamWorker worker=new StreamWorker(skt);
				worker.start();
				}catch(Exception e){
					log.error("Could not initialize worker. continuing",e);
				}
			}
		}catch(Exception e){
			log.error("Could not initialite socket. FATAL ERROR",e);
		}

	}

	private ServerSocket getServeSocket() throws Exception{
		ServerSocket socket = null;
		
		if (SSLsupport){
			SSLServerSocketFactory sslserversocketfactory =
				(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			log.info("Port for secure stream manager to listen to: " + this.port);
			
			SSLServerSocket sslserversocket =
				(SSLServerSocket) sslserversocketfactory.createServerSocket(this.port);
			
			String[] enable = { "TLS_DH_anon_WITH_AES_128_CBC_SHA" }; 
			sslserversocket.setEnabledCipherSuites(enable);
			
			log.debug("Availuable chifer suits" + sslserversocket.getEnabledCipherSuites());
			for (int i = 0 ; i < sslserversocket.getEnabledCipherSuites().length; i++)
				log.debug(sslserversocket.getEnabledCipherSuites()[i]);
			
			socket = sslserversocket;
		}else{
			log.info("Port for non secure stream manager to listen to: " + this.port);
			socket = new ServerSocket(port);
		}
		return socket;
	}
}
