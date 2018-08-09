package org.gcube.spatial.data.sdi.engine.impl.metadata;

import java.util.Set;

import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;

import lombok.Data;

@Data
public class TemplateApplicationReport {

	private String generatedFilePath;
	private Set<String> appliedTemplates;
	private Set<TemplateInvocation> requestedInvocations;
}
