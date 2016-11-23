package org.gcube.rest.resourcemanager.is.discovery;

import java.util.concurrent.TimeUnit;

import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.memoization.Memoizer;

public class InformationCollectorFactory {
	public static InformationCollector buildInformationCollector() {
		return new ISInformationCollector();
	}
	
	public static InformationCollector buildInformationCollector(long maximumSize, long duration, TimeUnit unit) {
		return(InformationCollector) new Memoizer().maximumSize(maximumSize).expireAfterWrite(duration, unit).build(new ISInformationCollector());
	}
}
