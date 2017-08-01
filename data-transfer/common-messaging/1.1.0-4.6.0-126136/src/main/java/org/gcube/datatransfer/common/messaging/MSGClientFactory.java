package org.gcube.datatransfer.common.messaging;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.common.messaging.producer.Producer;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class MSGClientFactory {


	static MSGClient client = null;

	/**
	 *  Get a static Instance of a MSGClient 
	 * 
	 * @return MSGClient 
	 * @throws Exception Initialization Exception
	 */
	public static MSGClient getMSGClientInstance() throws Exception {
		if (client == null)
			client= new MSGClient();
		
		GCUBEScope scope = GCUBEScope.getScope(ScopeProvider.instance.get());
		ConnectionsManager.addScope(scope);
		Producer.getSingleton();
		return client;
	}

}
