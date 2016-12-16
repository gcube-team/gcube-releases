package org.gcube.datatransfer.agent.library.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class ConfigurationException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public ConfigurationException(){
			super();
		}
		
		public ConfigurationException(String message){
			super(message);
		}
		
}
