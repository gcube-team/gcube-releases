package gr.uoa.di.madgik.rr.plugins.impl;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.plugins.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostFinderPlugin extends Plugin 
{
	private static final Logger logger = LoggerFactory
			.getLogger(HostFinderPlugin.class);
	
	public static final String PROPERTIES_FILE = "deploy.properties";
	
	private Properties properties = null;
	private synchronized Properties getPropertyFile() throws IOException {
		if (properties != null)
			return properties;
		
		properties = new Properties();
		InputStream is = HostFinderPlugin.class.getResourceAsStream("/" + PROPERTIES_FILE);
		properties.load(is);

		return properties;
	}
	
	private String getHostname() throws IOException{
		return getPropertyFile().getProperty("hostname");
	}
	
	private String getPort() throws IOException{
		return getPropertyFile().getProperty("port");
	}
	
	public HostFinderPlugin()
	{
		this.type = Type.ONE_OFF;
	}
	
	@Override
	public void setup() throws ResourceRegistryException {
	}
	
	@Override
	protected void execute(Set<Class<?>> targets)throws ResourceRegistryException 
	{
		try 
		{
//			System.out.println("getting hostname : ");
//			System.out.println("getting port : ");
			
			String host = this.getHostname();
			String port = this.getPort();
			
			logger.info( "hostname : " + host);
			logger.info( "port     : " + port);
			
//			System.out.println("hostname : " + host);
//			System.out.println("port     : " + port);
			
			ResourceRegistry.getContext().setLocalNodeHostname(host);
			ResourceRegistry.getContext().setLocalNodePort(port);
			
		}catch(Exception e)
		{
			throw new ResourceRegistryException("could not resolve local node hostname or port", e);
		}	
	}
	
}
