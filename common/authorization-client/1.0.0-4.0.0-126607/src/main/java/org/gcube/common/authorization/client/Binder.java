package org.gcube.common.authorization.client;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.BannedService;
import org.gcube.common.authorization.library.BannedServices;

public class Binder {

	private static JAXBContext context ;
	
	public static JAXBContext getContext() throws JAXBException{
		if (context==null)
			context = JAXBContext.newInstance(AuthorizationEntry.class, BannedService.class, BannedServices.class);
		return context;
	}
	
	
}
