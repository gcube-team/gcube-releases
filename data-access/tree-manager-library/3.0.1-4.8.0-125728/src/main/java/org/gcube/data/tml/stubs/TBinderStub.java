package org.gcube.data.tml.stubs;

import static javax.jws.soap.SOAPBinding.ParameterStyle.*;
import static org.gcube.data.tml.Constants.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.stubs.Types.BindingsHolder;
import org.gcube.data.tml.stubs.Types.InvalidRequestFault;

/**
 * A stub for the T-Binder service.
 * 
 * @author Fabio Simeoni
 *
 */
@WebService(name=binderPortType,targetNamespace=namespace)
@SOAPBinding(parameterStyle=BARE)
public interface TBinderStub {

	public BindingsHolder bind(BindRequest params) throws InvalidRequestFault; 
	
}
