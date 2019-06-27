package org.gcube.data.transfer.plugins.thredds.sis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.AbstractPluginFactory;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginInitializationException;
import org.gcube.data.transfer.plugin.fails.PluginShutDownException;
import org.gcube.data.transfer.plugin.model.DataTransferContext;
import org.gcube.data.transfer.plugins.thredds.ThreddsInstanceManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SISPluginFactory extends AbstractPluginFactory<SisPlugin> {




	static final String PLUGIN_ID="SIS/GEOTK";

	public static final String SOURCE_PARAMETER="SOURCE_FILE";
	public static final String GEONETWORK_CATEGORY="GEONETWORK_CATEGORY";
	public static final String GEONETWORK_STYLESHEET="GEONETWORK_STYLESHEET";

	public static final String VALIDATE_PARAMETER="VALIDATE";
	
	
	static final Map<String,String> PARAMETERS_DESCRIPTION= new HashMap<String,String>();

	static{		
		PARAMETERS_DESCRIPTION.put(GEONETWORK_CATEGORY, "[String value] GeoNetwork category for publiehd metadata. Default is 'Dataset'.");
		PARAMETERS_DESCRIPTION.put(GEONETWORK_STYLESHEET, "[String value] GeoNetwork stylesheet for publiehd metadata. Default is '_none_'.");
		PARAMETERS_DESCRIPTION.put(VALIDATE_PARAMETER, "[Boolean value] Ask GeoNetwork to validate generated metadata. Default is 'true'.");
	}




	//INSTANCE
	public SISPluginFactory() {

	}

	DataTransferContext ctx;


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
		return new SisPlugin(arg0,ThreddsInstanceManager.get(ctx));
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
		try {
			this.ctx=ctx;
			ThreddsInstanceManager.get(ctx).updatePublishedInfo();
			return true;
		}catch(Throwable t) {
			throw new PluginInitializationException(t);
		}
	}

	@Override
	public boolean shutDown() throws PluginShutDownException {
		return true;
	}



}
