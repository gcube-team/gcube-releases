package gr.uoa.di.madgik.rr.element.query;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionService;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;

public class SourceHelper {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SourceHelper.class);
	
	
	public static Set<String> getScopesOfSource(IRRElement source) throws ResourceRegistryException
	{
		if(source==null) throw new ResourceRegistryException("Source does not exist");
		if(source instanceof DataSource)
			return ((DataSource)source).getScopes();
		throw new ResourceRegistryException("Unrecognized source type");
	}
}
