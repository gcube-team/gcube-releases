package org.gcube.data.analysis.tabulardata.operation.sdmx;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidParameterException;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.model.SDMXRegistryDescriptorImpl;
import org.gcube.datapublishing.sdmx.impl.registry.FusionRegistryClient;

public class WorkerUtils {

	public static final String VERSION = "version";
	public static final String AGENCY = "agency";
	public static final String ID = "id";
	public static final String REGISTRY_BASE_URL = "registryBaseUrl";

	public static void checkParameters(List<Parameter> parameters, OperationInvocation invocation)
			throws InvalidInvocationException {
		Map<String, Object> parameterInstances = invocation.getParameterInstances();
		for (Parameter parameterDefinition : parameters) {
			checkParameter(parameterInstances.get(parameterDefinition.getIdentifier()), parameterDefinition, invocation);
		}
	}

	public static void checkParameter(Object parameterValue, Parameter parameterDefinition, OperationInvocation invocation)
			throws InvalidInvocationException {
		if (parameterValue == null)
			throw new InvalidParameterException(invocation, parameterDefinition.getIdentifier());		
		try {
			RegexpStringParameter regexpStringParameter = (RegexpStringParameter) parameterDefinition;
			regexpStringParameter.validateValue((String) parameterValue);
		} catch (Exception e) {
			throw new InvalidParameterException(invocation, parameterDefinition.getIdentifier());
		}		
	}

	public static SDMXRegistryClient initSDMXClient(String url) {
		SDMXRegistryDescriptorImpl descriptor = new SDMXRegistryDescriptorImpl();
		descriptor.setUrl(SDMXRegistryInterfaceType.RESTV2_1, url);
		SDMXRegistryClient registryClient = new FusionRegistryClient(descriptor);
		return registryClient;
	}

}
