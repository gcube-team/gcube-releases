package org.gcube.data.transfer.plugins.sis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.AbstractPluginFactory;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginInitializationException;
import org.gcube.data.transfer.plugin.fails.PluginShutDownException;
import org.gcube.data.transfer.plugin.model.DataTransferContext;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SISPluginFactory extends AbstractPluginFactory<SisPlugin> {

	
	
	
	static final String PLUGIN_ID="SIS/GEOTK";

	public static final String SOURCE_PARAMETER="SOURCE_FILE";
	public static final String GEONETWORK_CATEGORY="GEONETWORK_CATEGORY";
	public static final String GEONETWORK_STYLESHEET="GEONETWORK_STYLESHEET";

	static final Map<String,String> PARAMETERS_DESCRIPTION= new HashMap<String,String>();

	static{		
		PARAMETERS_DESCRIPTION.put(GEONETWORK_CATEGORY, "[String value] GeoNetwork category for publiehd metadata. Default is 'Dataset'.");
		PARAMETERS_DESCRIPTION.put(GEONETWORK_STYLESHEET, "[String value] GeoNetwork stylesheet for publiehd metadata. Default is '_none_'.");
	}



	
	//INSTANCE
	public SISPluginFactory() {
		
	}
	
	String publicCatalogLocation=null;
	
	
	@Override
	public PluginInvocation checkInvocation(PluginInvocation arg0,String transferredFile) throws ParameterException {
		log.debug("Setting default parameters for  {} ",arg0);
		Map<String,String> params=arg0.getParameters();
		
		if(params==null||params.isEmpty())	params=new HashMap<String,String>();
		if(!params.containsKey(SOURCE_PARAMETER)) params.put(SOURCE_PARAMETER, transferredFile);
		if(!params.containsKey(GEONETWORK_CATEGORY)) params.put(GEONETWORK_CATEGORY, "Dataset");
		if(!params.containsKey(GEONETWORK_STYLESHEET)) params.put(GEONETWORK_STYLESHEET, "_none_");
		String source=params.get(SOURCE_PARAMETER);
		log.debug("Checking access to source {} ",source);
		
		if(source==null||source.length()==0) throw new ParameterException(SOURCE_PARAMETER+" cannot be null.");
		try{
			File f=new File(source);
			if(!f.exists()) throw new ParameterException(SOURCE_PARAMETER+" ["+source+"] not found.");
			if(!f.canRead()) throw new ParameterException("Cannot read "+SOURCE_PARAMETER+" ["+source+"].");
		}catch(ParameterException e){
			throw e;
		}catch(Exception e){
			throw new ParameterException("Unable to access source file ",e);
		}
		
		arg0.setParameters(params);
		return arg0;
		
	}

	@Override
	public SisPlugin createWorker(PluginInvocation arg0) {
		return new SisPlugin(arg0,ctx,publicCatalogLocation);
	}

	@Override
	public String getDescription() {
		return String.format("Extracts ISO metadata file from <%s> and publishes to GeoNetwork.", SOURCE_PARAMETER);
	}

	@Override
	public String getID() {
		return PLUGIN_ID;
	}

	@Override
	public Map<String,String> getParameters() {
		return PARAMETERS_DESCRIPTION;
	}

	@Override
	public boolean init(DataTransferContext ctx) throws PluginInitializationException {
		log.trace("Loading configuration .. ");
		this.ctx=ctx;
		String threddsContentRoot=null;
//		String threddsContentRoot=System.getenv("THREDDS_CONTENT_ROOT")+File.separator+"thredds";
//		log.info("Thredds catalog base path from ENV is {} ",threddsContentRoot);
		
		for(ApplicationConfiguration app:ctx.getCtx().container().configuration().apps()) {
			if(app.context().equals("thredds")||app.context().equals("/thredds")) {
				threddsContentRoot=app.persistence().location();
				log.info("Thredds catalog base path from Context is {} ",threddsContentRoot);
		
				// Found thredds
				// Get catalog base folder
//				app.persistence().
			}
		}
		if(threddsContentRoot==null) throw new PluginInitializationException("No Thredds instance found in context");
		publicCatalogLocation=threddsContentRoot+"/public/netcdf/";		
		return true;
	}

	@Override
	public boolean shutDown() throws PluginShutDownException {
		return true;
	}

	
	private DataTransferContext ctx;
	
}
