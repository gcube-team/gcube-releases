package org.gcube.common.authorization.library;

public abstract class ResourceAuthorizationProxy<I,T extends I> {

	private I delegate;

	public ResourceAuthorizationProxy(Class<I> classIntrface, T wrapped){
		delegate = GenericProxyFactory.getProxy(classIntrface,wrapped, this );
	}

	public I getDelegate() {
		return delegate;
	}

	public abstract String getServiceClass();

	public abstract String getServiceName();

}
