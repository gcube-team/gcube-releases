package org.gcube.spatial.data.sdi.engine.impl.metadata.templates;

import java.util.List;

import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataHandler;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataUtils.Position;
import org.gcube.spatial.data.sdi.model.ParameterType;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public abstract class AbstractMetadataTemplate<T> {

	
	
	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	public static class InsertionPoint{
		private Position position;
		private String elementReference;
	}
	
		
	private String fileName;
	private InsertionPoint insertionPoint;
	private TemplateDescriptor descriptor;	
	
	public abstract T getInstantiationRequest(MetadataHandler original, TemplateInvocation invocation) throws InvalidTemplateInvocationException,Exception;
		
	protected String getParameter(String parameterName, List<ParameterType> parameters, boolean mandatory,String defaultValue)throws InvalidTemplateInvocationException{
		
		//if collection not empty look for it
		if(!(parameters==null || parameters.isEmpty()))
			for(ParameterType param:parameters)
				if(param.getName().equals(parameterName)) return param.getValue();
		
		//nothing found..
		if(mandatory) throw new InvalidTemplateInvocationException("Missing parameter "+parameterName+".");
		else return defaultValue;		
	}
}
