package org.gcube.common.vremanagement.ghnmanager.client.fws;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.RIData;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ScopeRIParams;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ShutdownOptions;

import static org.gcube.common.vremanagement.ghnmanager.client.Constants.*;


/**
 * 
 * @author andrea
 *
 */
@WebService(name=porttypeLocalName,targetNamespace=porttypeNS)
public interface GHNManagerServiceJAXWSStubs {
		
		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public boolean addScope(AddScopeInputParams params);
		
		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public boolean removeScope(String scope);
		
		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public boolean addRIToScope(ScopeRIParams params);


		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public boolean activateRI(RIData params);


		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public boolean deactivateRI(RIData params);
		
		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public boolean removeRIFromScope(ScopeRIParams params);
		
		@SOAPBinding(parameterStyle=ParameterStyle.BARE)
		public Empty shutdown(ShutdownOptions options) throws Exception;
}
