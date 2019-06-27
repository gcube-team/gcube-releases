package org.gcube.application.perform.service.engine.dm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitor;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMUtils {

	private static final Logger log= LoggerFactory.getLogger(DMUtils.class);

	public static SClient getClient() throws DMException {
		try {
			
			return new DataMinerService().getClient();
		}catch(Exception e) {
			throw new DMException(e);
		}
	}

	public static ComputationId getComputation(ImportRoutineDescriptor desc) {
		return new ComputationId(desc.getComputationId(), desc.getComputationUrl(), desc.getComputationOperator(), desc.getComputationOperatorName(), desc.getComputationRequest());
	}


	public static void monitor(SClient client,ComputationId computationId,DMMonitorListener listener) {
		DMMonitor monitor=new DMMonitor(computationId,client);
		monitor.add(listener);
		monitor.start();
	}

	public static void monitor(ComputationId computationId,DMMonitorListener listener) throws DMException {
		AsynchDMMonitor monitor=new AsynchDMMonitor(computationId,getClient());
		monitor.add(listener);
		monitor.startAsynch();
	}

	public static ComputationId submitJob(String operatorId, Map<String,String> parameters) throws DMException {
		return submitJob(getClient(),operatorId,parameters);
	}

	public static ComputationId submitJob(SClient client, String operatorId, Map<String,String> parameters) throws DMException {
		try {
			log.debug("Looking for operator by Id {} ",operatorId);
			Operator op=client.getOperatorById(operatorId);

			List<Parameter> params=client.getInputParameters(op);

			log.debug("Preparing parameters, values : {} ",parameters);
			for(Parameter param:params) {
				String paramName=param.getName();
				if(parameters.containsKey(paramName))
					param.setValue(parameters.get(paramName));
			}

			op.setOperatorParameters(params);
			log.info("Submitting Operator {} to DM",op);

			return client.startComputation(op);
		}catch(Exception e) {
			throw new DMException(e);
		}
	}


	public static final Map<String,String> getOutputFiles(ComputationId computationId) throws DMException{
		try{
			
			Map<String,String> toReturn=new HashMap<String,String>();
			SClient client=getClient();
			OutputData data=client.getOutputDataByComputationId(computationId);
			
		
			Resource resource = data.getResource();
			if (resource.isMap()) {
				MapResource mapResource = (MapResource) resource;
				for (String key : mapResource.getMap().keySet()) {
					

					Resource res = mapResource.getMap().get(key);
					switch (res.getResourceType()) {
					case FILE:
						FileResource fileResource = (FileResource) res;
						toReturn.put(fileResource.getDescription(), fileResource.getUrl());
						break;
					}
				}
			}
			return toReturn;
		}catch(Exception e) {
			throw new DMException(e);
		}
	}

}
