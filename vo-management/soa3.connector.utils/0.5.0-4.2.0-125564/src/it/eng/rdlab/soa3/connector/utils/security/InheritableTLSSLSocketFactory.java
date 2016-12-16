package it.eng.rdlab.soa3.connector.utils.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Inheritable thread local ssl socket factory, for using different socket factories in different threads
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class InheritableTLSSLSocketFactory extends SSLSocketFactory 

{
	private static SSLSocketFactory defaultSSLSocketFactory;
	private static InheritableTLSSLSocketFactory instance;
	private InheritableThreadLocal<SSLSocketFactory> socketFactoryes;
	private Logger logger;
	
	/**
	 * 
	 */
	private InheritableTLSSLSocketFactory() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.socketFactoryes = new InheritableThreadLocal<SSLSocketFactory>();
	}
	
	/**
	 * 
	 * Returns the singleton instance of InheritableTLSSLSocketFactory
	 * 
	 * @return the singleton instance
	 */
	public static InheritableTLSSLSocketFactory getInstance () 
	{
		if (instance == null)
		{
			// saves the instance of socket factory at the moment of the first instantiation
			defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			instance = new InheritableTLSSLSocketFactory();
		}
		
		return instance;
	}
	
	/**
	 * 
	 * sets the sslSocketFactory for the current thread
	 * 
	 * @param sslSocketFactory
	 */
	public void setSSLSocketFactory (SSLSocketFactory sslSocketFactory)
	{
		logger.debug("Setting ssl socket factory for thread "+Thread.currentThread().getId());
		this.socketFactoryes.set(sslSocketFactory);
	}
	
	/**
	 * 
	 * Removes the ssl socket factory from the current thread
	 */
	public void reset ()
	{
		logger.debug("Resetting ssl socket factory for thread "+Thread.currentThread().getId());
		this.socketFactoryes.remove();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException 
	{
		return getCurrentSSLSocketFactory().createSocket(s, host, port, autoClose);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getDefaultCipherSuites() 
	{

		return getCurrentSSLSocketFactory().getDefaultCipherSuites();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getSupportedCipherSuites() {

		return getCurrentSSLSocketFactory().getDefaultCipherSuites();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(String host, int port) throws IOException,UnknownHostException 
	{

		return getCurrentSSLSocketFactory().createSocket(host, port);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException 
	{
		return getCurrentSSLSocketFactory().createSocket(host, port);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(String address, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException
	{
		return getCurrentSSLSocketFactory().createSocket(address, port, localAddress, localPort);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress,int localPort) throws IOException 
	{

		return getCurrentSSLSocketFactory().createSocket(address, port, localAddress, localPort);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket() throws IOException 
	{
		return getCurrentSSLSocketFactory().createSocket();
	}

	/**
	 * 
	 * @return
	 */
	public SSLSocketFactory getCurrentSSLSocketFactory ()
	{
		SSLSocketFactory response = this.socketFactoryes.get();
		
		if (response == null)
		{
			logger.debug("No socket factory found for "+Thread.currentThread().getId()+", using default one");
			response = defaultSSLSocketFactory;
		}
		
		return response;
		
		
	}
	
	
	

}
