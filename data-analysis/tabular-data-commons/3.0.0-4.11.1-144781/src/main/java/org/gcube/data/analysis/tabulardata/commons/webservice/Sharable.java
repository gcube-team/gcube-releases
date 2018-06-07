package org.gcube.data.analysis.tabulardata.commons.webservice;

import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TabularDataException;

public interface Sharable<T, R, E extends TabularDataException> {

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	R share(T entityId, SharingEntity ... entities) throws E, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	R unshare(T entityId, SharingEntity ... entities) throws E, InternalSecurityException;
}
