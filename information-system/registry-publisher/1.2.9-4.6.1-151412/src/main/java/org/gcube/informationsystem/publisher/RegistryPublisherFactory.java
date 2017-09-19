package org.gcube.informationsystem.publisher;

public class RegistryPublisherFactory {

	private static RegistryPublisher singleton = new RegistryPublisherImpl();
	
	public static RegistryPublisher create(){
		return singleton;
	}

	
	public static void setPublisher(RegistryPublisher registryPublisher){
		singleton = registryPublisher;
	}
	
	public static ScopedPublisher scopedPublisher(){
		return new ScopedPublisherImpl(singleton);
	}
	
}
