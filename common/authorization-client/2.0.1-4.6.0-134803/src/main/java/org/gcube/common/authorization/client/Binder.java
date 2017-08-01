package org.gcube.common.authorization.client;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.ExternalServiceList;
import org.gcube.common.authorization.library.Policies;
import org.gcube.common.authorization.library.QualifiersList;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;

public class Binder {

	private static JAXBContext context;
	
	public static JAXBContext getContext() throws JAXBException{
		if (context==null)
			context = JAXBContext.newInstance(ExternalServiceList.class, QualifiersList.class, AuthorizationEntry.class, ClientInfo.class, UserInfo.class, 
					ServiceInfo.class, Policies.class, Policy.class);
		return context;
	}
	
	
}
