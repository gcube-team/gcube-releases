package org.gcube.common.scope.impl;


import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceMapScannerMediator {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceMapScannerMediator.class);
	
	
	public static Set<String> getScopeKeySet(){
		return new ServiceMapScanner().maps().keySet();
	}

}
