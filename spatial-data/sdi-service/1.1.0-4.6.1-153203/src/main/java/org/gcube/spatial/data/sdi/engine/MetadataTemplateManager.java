package org.gcube.spatial.data.sdi.engine;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.gcube.spatial.data.sdi.engine.impl.metadata.TemplateApplicationReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;

public interface MetadataTemplateManager {

	
	public TemplateCollection getAvailableTemplates();
	public TemplateApplicationReport applyTemplates(File original,Set<TemplateInvocation> invocations) throws IOException, TransformerException;
	
}
