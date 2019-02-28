package org.gcube.smartgears.extensions;

import static java.util.Arrays.*;
import static org.gcube.common.events.impl.Utils.*;

import java.util.HashSet;
import java.util.Set;

import org.gcube.smartgears.extensions.HttpExtension.Method;

public class  ApiMethodSignature {
	
	Method method;
	Set<String> requestTypes = new HashSet<String>();
	Set<String> responseTypes = new HashSet<String>();
	
	public ApiMethodSignature(Method method) {
		notNull("method",method);
		this.method=method;
	}
	
	public ApiMethodSignature accepts(String ... types) {
		
		notNull("request types",types);
		this.requestTypes.addAll(asList(types));
		return this;
	}
	
	public ApiMethodSignature produces(String ... types) {

		notNull("response types",types);
		this.responseTypes.addAll(asList(types));
		return this;
	}
	
	
}