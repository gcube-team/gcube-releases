package org.gcube.portlets.admin.software_upload_wizard.server.rpc.handlers;

import org.slf4j.Logger;

public class HandlerExceptionLogger {
	
	public static void logHandlerException(Logger log, Exception e){
		log.error("Error while executing " + log.getName() + ":\n" + e.getMessage(),e);
	}

}
