package org.gcube.data.transfer.plugins.thredds.catalog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.data.transfer.plugin.AbstractPluginFactory;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;
import org.gcube.data.transfer.plugin.fails.PluginInitializationException;
import org.gcube.data.transfer.plugin.fails.PluginShutDownException;
import org.gcube.data.transfer.plugin.model.DataTransferContext;
import org.gcube.data.transfer.plugins.thredds.ThreddsInstanceManager;
import org.gcube.data.transfer.plugins.thredds.XMLCatalogHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterCatalogPluginFactory extends AbstractPluginFactory<RegisterCatalogPlugin>{

	static final String PLUGIN_ID="REGISTER_CATALOG";
	public static final String CATALOG_FILE="CATALOG_FILE";
	public static final String CATALOG_REFERENCE="CATALOG_REFERENCE";


	static final Map<String,String> PARAMETERS_DESCRIPTION= new HashMap<String,String>();

	static{		
		PARAMETERS_DESCRIPTION.put(CATALOG_REFERENCE, "[String value] The reference value under which the passed catalog should be registered.");
	}

	@Override
	public PluginInvocation checkInvocation(PluginInvocation arg0, String toRegisterCatalog) throws ParameterException {
		log.trace("Checking invocation {} ",arg0);
		Map<String,String> params=arg0.getParameters();
		if(params==null||params.isEmpty()||(!params.containsKey(CATALOG_REFERENCE))||(params.get(CATALOG_REFERENCE)==null))
			throw new ParameterException(CATALOG_REFERENCE + "parameter is mandatory.");
		arg0.getParameters().put(CATALOG_FILE, toRegisterCatalog);
		log.debug("Checking if file {} is a valid catalog..");
		try {
			new XMLCatalogHandler(new File(toRegisterCatalog)).getCatalogDescriptor();
		}catch(Throwable t) {
			throw new ParameterException("Catalog File "+toRegisterCatalog+" is not valid.", t);
		}
		
		
		return arg0;
	}

	@Override
	public RegisterCatalogPlugin createWorker(PluginInvocation arg0) {
		return new RegisterCatalogPlugin(arg0,ThreddsInstanceManager.get(ctx));
	}

	@Override
	public String getDescription() {
		return String.format("Registers a catalog file as <%s> under main thredds catalog.", CATALOG_REFERENCE);
	}

	@Override
	public String getID() {
		return PLUGIN_ID;
	}

	@Override
	public Map<String, String> getParameters() {
		return PARAMETERS_DESCRIPTION;
	}

	DataTransferContext ctx=null;

	@Override
	public boolean init(DataTransferContext arg0) throws PluginInitializationException {
		try{
			this.ctx=arg0;
			ThreddsInstanceManager.get(ctx).updatePublishedInfo();
			return true;
		}catch(Throwable t) {
			throw new PluginInitializationException(t);
		}
	}

	@Override
	public boolean shutDown() throws PluginShutDownException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getInfo() throws PluginExecutionException{
		try{
			ThreddsInfo info=ThreddsInstanceManager.get(ctx).getInfo();
			log.info("Returning {} ",info);
			return info;
		}catch(Throwable t) {
			log.error("Unable to gather catalog information",t);
			throw new PluginExecutionException("Unable to gather information",t);
		}
	}

}
