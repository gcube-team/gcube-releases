package org.gcube.data.analysis.statisticalmanager.proxies;

import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;

public interface StatisticalManagerService {

	String executeComputation(SMComputationConfig computationConfig);

	SMOperationInfo getComputationInfo(String computationId);

	SMAbstractResource getComputationOutput(String computationId);

	void removeComputation(String computationId);


}
