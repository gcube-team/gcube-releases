package gr.uoa.di.madgik.rr.plugins.impl.gcube;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.plugins.Plugin;

import java.util.Set;
import java.util.logging.Logger;

import org.gcube.common.core.contexts.GHNContext;

public class GCubeHostFinderPlugin extends Plugin 
{
	private static Logger logger = Logger.getLogger(GCubeHostFinderPlugin.class.getName());
	
	public GCubeHostFinderPlugin()
	{
		this.type = Type.ONE_OFF;
	}
	
	@Override
	public void setup() throws ResourceRegistryException { }
	
	@Override
	protected void execute(Set<Class<?>> targets)throws ResourceRegistryException 
	{
		try 
		{
			String host = GHNContext.getContext().getHostname();
			String port = new Integer(GHNContext.getContext().getPort()).toString();
			
			ResourceRegistry.getContext().setLocalNodeHostname(host);
			ResourceRegistry.getContext().setLocalNodePort(port);
			
		}catch(Exception e)
		{
			throw new ResourceRegistryException("could not resolve local node hostname or port", e);
		}	
	}
	
}
