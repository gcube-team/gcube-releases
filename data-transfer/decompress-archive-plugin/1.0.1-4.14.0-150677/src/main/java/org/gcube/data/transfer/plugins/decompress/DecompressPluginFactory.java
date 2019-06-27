package org.gcube.data.transfer.plugins.decompress;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.AbstractPluginFactory;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginInitializationException;
import org.gcube.data.transfer.plugin.fails.PluginShutDownException;
import org.gcube.data.transfer.plugin.model.DataTransferContext;

public class DecompressPluginFactory extends AbstractPluginFactory<DecompressPlugin> {

	static final String PLUGIN_ID="DECOMPRESS";


	public static final String SOURCE_PARAMETER="SOURCE_ARCHIVE";
	public static final String DESTINATION_PARAMETER="DESTINATION";
	public static final String OVERWRITE_DESTINATION="OVERWITE_DESTINATION";
	public static final String DELETE_ARCHIVE="DELETE_ARCHIVE";
	public static final String ARCHIVE_TYPE="ARCHIVE_TYPE";

	static final Map<String,String> PARAMETERS_DESCRIPTION= new HashMap<String,String>();

	static{		
		PARAMETERS_DESCRIPTION.put(DESTINATION_PARAMETER, "[String value] The folder destination of uncompressed content expressed as a path relative to <"+SOURCE_PARAMETER+">. Default is same directory of <"+SOURCE_PARAMETER+">.");
		PARAMETERS_DESCRIPTION.put(OVERWRITE_DESTINATION, "[Boolean value] Set true in order to overwrite <"+DESTINATION_PARAMETER+"> content. Default is false");
		PARAMETERS_DESCRIPTION.put(DELETE_ARCHIVE, "[Boolean value] Set true in order to delete <"+SOURCE_PARAMETER+"> after extracting content. Default is false");
		//		PARAMETERS_DESCRIPTION.put(ARCHIVE_TYPE, "[String value] <"+SOURCE_PARAMETER+"> archive type. Supported types are : ");


	}




	@Override
	public String getID() {
		return PLUGIN_ID;
	}

	@Override
	public String getDescription() {
		return String.format("Decompress a <%s> archive file to a given <%s>.",SOURCE_PARAMETER,DESTINATION_PARAMETER);
	}

	@Override
	public Map<String, String> getParameters() {
		return PARAMETERS_DESCRIPTION;
	}

	@Override
	public boolean init(DataTransferContext ctx) throws PluginInitializationException {
		return true;
	}

	@Override
	public DecompressPlugin createWorker(PluginInvocation invocation) {
		return new DecompressPlugin(invocation);
	}


	@Override
	public PluginInvocation checkInvocation(PluginInvocation invocation,String transferredFile) throws ParameterException {
		try{
			Map<String,String> params=invocation.getParameters();
			if(params == null || params.isEmpty()) params=new HashMap<String,String>();
			if(!params.containsKey(SOURCE_PARAMETER)) params.put(SOURCE_PARAMETER, transferredFile);
			if(!params.containsKey(OVERWRITE_DESTINATION)) params.put(OVERWRITE_DESTINATION, "false");
			if(!params.containsKey(DELETE_ARCHIVE)) params.put(DELETE_ARCHIVE, "false");
			

			
			File source=new File(params.get(SOURCE_PARAMETER));
			if (!source.exists()) 
				throw new ParameterException("Source file "+params.get(SOURCE_PARAMETER)+" not found.");
			if(!source.canRead())
				throw new ParameterException("Cannot read Source file "+params.get(SOURCE_PARAMETER)+".");
			
			
			String destinationPath=source.getParent() +(
					params.containsKey(DecompressPluginFactory.DESTINATION_PARAMETER)? File.pathSeparator+params.get(DecompressPluginFactory.DESTINATION_PARAMETER):"");
			new File(destinationPath);			
			
			
			try{				
					Boolean.parseBoolean(params.get(OVERWRITE_DESTINATION));
			}catch(Throwable t){
				throw new ParameterException("Unable to evaluate parameter "+OVERWRITE_DESTINATION,t);
			}
			
			try{				
					Boolean.parseBoolean(params.get(DELETE_ARCHIVE));
			}catch(Throwable t){
				throw new ParameterException("Unable to evaluate parameter "+DELETE_ARCHIVE,t);
			}
			invocation.setParameters(params);
			return invocation;
		}catch(ParameterException e){
			throw e;
		}catch (Throwable t){
			throw new ParameterException ("Unable to check invocation parameters ",t);
		}

	}


	@Override
	public boolean shutDown() throws PluginShutDownException {
		return true;
	}

}
