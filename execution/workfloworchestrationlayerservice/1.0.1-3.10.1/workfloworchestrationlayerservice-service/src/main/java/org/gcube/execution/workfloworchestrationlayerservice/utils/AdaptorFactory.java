package org.gcube.execution.workfloworchestrationlayerservice.utils;

import org.gcube.execution.workfloworchestrationlayerservice.wrappers.AdaptorBase;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.CondorAdaptor;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.GridAdaptor;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.HadoopAdaptor;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.JDLAdaptor;

public class AdaptorFactory {

	private static final String JDL = "JDL";
	private static final String CONDOR = "CONDOR";
	private static final String GRID = "GRID";
	private static final String HADOOP = "HADOOP";

	public static AdaptorBase createAdaptor(String type) {
		if (type.equals(JDL))
			return new JDLAdaptor();
		else if (type.equals(CONDOR))
			return new CondorAdaptor();
		else if (type.equals(GRID))
			return new GridAdaptor();
		else if (type.equals(HADOOP))
			return new HadoopAdaptor();
		return null;
	}
}
