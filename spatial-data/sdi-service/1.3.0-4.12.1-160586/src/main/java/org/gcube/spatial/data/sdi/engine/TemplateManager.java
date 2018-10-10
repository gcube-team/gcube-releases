package org.gcube.spatial.data.sdi.engine;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.gcube.spatial.data.sdi.engine.impl.metadata.TemplateApplicationReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;

public interface TemplateManager {

	
	public TemplateCollection getAvailableMetadataTemplates();
	public TemplateApplicationReport applyMetadataTemplates(File original,Set<TemplateInvocation> invocations) throws IOException, TransformerException;
	public File generateFromTemplate(Map<String,String> parameters, String templateID) throws Exception;
	
	
	
}
