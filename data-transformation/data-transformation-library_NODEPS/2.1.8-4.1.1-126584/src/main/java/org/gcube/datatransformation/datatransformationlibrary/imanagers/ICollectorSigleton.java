package org.gcube.datatransformation.datatransformationlibrary.imanagers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.is.discovery.InformationCollectorFactory;

public class ICollectorSigleton {
	private static ICollectorSigleton instance;
	private InformationCollector icollector;

	public static ICollectorSigleton getInstance() {
		if (instance == null) {
			instance = new ICollectorSigleton();
		}
		return instance;
	}

	private ICollectorSigleton() {
		icollector = InformationCollectorFactory.buildInformationCollector(100, 10, TimeUnit.MINUTES);
	}
	
	public synchronized List<String> listGenericResourceIDsByType(String type, String scope) {
		return icollector.listGenericResourceIDsByType(type, scope);
	}

	public synchronized List<Resource> getGenericResourcesByName(String name, String scope) {
		return icollector.getGenericResourcesByName(name, scope);
	}

	public synchronized List<Resource> getGenericResourcesByID(String id, String scope) {
		return icollector.getGenericResourcesByID(id, scope);
	}
}
