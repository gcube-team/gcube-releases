package org.gcube.spatial.data.sdi.interfaces;

import java.io.File;

import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;

public interface Metadata {

	public TemplateCollection getAvailableTemplates();
	public MetadataReport pushMetadata(File toPublish);
	public MetadataReport pushMetadata(File toPublish,MetadataPublishOptions options);
}
