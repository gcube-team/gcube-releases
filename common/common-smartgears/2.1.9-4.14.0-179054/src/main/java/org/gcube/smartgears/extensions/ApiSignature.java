package org.gcube.smartgears.extensions;

import static org.gcube.common.events.impl.Utils.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.smartgears.extensions.HttpExtension.Method;

public class ApiSignature {
	
	
	private final String mapping;
	private Set<Method> methods = new HashSet<Method>();
	private Map<Method,Set<String>> requestTypes = new HashMap<Method, Set<String>>();
	private Map<Method,Set<String>> responseTypes = new HashMap<Method, Set<String>>();

	
	public ApiSignature(String mapping) {
		this.mapping=mapping;
	}
	
	public ApiSignature with(ApiMethodSignature signature) {
		
		notNull("method signature",signature);
		
		this.methods.add(signature.method);
		this.requestTypes.put(signature.method,signature.requestTypes);
		this.responseTypes.put(signature.method,signature.responseTypes);

		return this;
	}
	
	public String mapping() {
		return mapping;
	}
	
	public Set<Method> methods() {
		return methods;
	}
	
	public Map<Method,Set<String>> requestTypes() {
		return requestTypes;
	}
	
	public Map<Method,Set<String>> responseTypes() {
		return responseTypes;
	}
}