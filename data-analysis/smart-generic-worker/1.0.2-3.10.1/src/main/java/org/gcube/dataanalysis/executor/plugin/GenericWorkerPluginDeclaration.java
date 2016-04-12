/**
 * 
 */
package org.gcube.dataanalysis.executor.plugin;

import java.util.HashMap;
import java.util.Map;


import org.apache.naming.resources.DirContextURLStreamHandlerFactory;
//import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class GenericWorkerPluginDeclaration implements PluginDeclaration {

	/**{@inheritDoc}*/
	@Override
	public void init() throws Exception {
		DirContextURLStreamHandlerFactory.addUserFactory(new ConfigurableStreamHandlerFactory("smp", new Handler()));
		
		/*
		 * TomcatURLStreamHandlerFactory ushf = TomcatURLStreamHandlerFactory.getInstance();
		 * boolean registered = TomcatURLStreamHandlerFactory.register();
		 * if(!registered){
		 * 		throw new Exception();
		 * }
		 * ushf.addUserFactory(new ConfigurableStreamHandlerFactory("smp", new Handler()));
		*/
	}
	
	/** {@inheritDoc} */
	@Override
	public String getName() {
		return "SmartGenericWorker";
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, String> getSupportedCapabilities() {
		return new HashMap<String, String>();
	}

	/** {@inheritDoc} */
	@Override
	public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
		return GenericWorkerPlugin.class;
	}

	/** {@inheritDoc} */
	@Override
	public String getDescription() {
		return "Smart Generic Worker Description";
	}

	/** {@inheritDoc} */
	@Override
	public String getVersion() {
		return "1.0.0-SNAPSHOT";
	}

}
