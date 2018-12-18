package org.gcube.common.storagehub.client;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "StorageHub";

	/** Service class. */
	public static final String SERVICE_CLASS = "DataAccess";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	public static final String NAMESPACE = "http://gcube-system.org/namespaces/common/storagehub";
	
	public static final QName MANAGER_QNAME = new QName(NAMESPACE, "itemmanager");
	
	
/*
	public static final GcubeService<ManagerStubs> manager = service().withName(org.gcube.data.spd.model.service.Constants.manager_name).andInterface(ManagerStubs.class);
	
	public static final GcubeService<ClassificationStubs> classification = service().withName(org.gcube.data.spd.model.service.Constants.classification_name).andInterface(ClassificationStubs.class);
	
	public static final GcubeService<ExecutorStubs> executor = service().withName(org.gcube.data.spd.model.service.Constants.executor_name).andInterface(ExecutorStubs.class);
	
	public static final GcubeService<OccurrenceStubs> occurrence = service().withName(org.gcube.data.spd.model.service.Constants.occurrence_name).andInterface(OccurrenceStubs.class);
	
	private static final GcubeService<RemoteDispatcher> remoteDispatcher = service().withName(org.gcube.data.spd.model.service.Constants.remoteDispatcher_name).andInterface(RemoteDispatcher.class);

	public static final RemoteDispatcher getRemoteDispatcherService(String address){
		return stubFor(remoteDispatcher).at(address);
	}*/
}
