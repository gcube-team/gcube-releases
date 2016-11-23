/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceLightTreeResources extends ClientBundle {
	
	public static final WorkspaceLightTreeResources INSTANCE =  GWT.create(WorkspaceLightTreeResources.class);
	
	@Source("gbe.png")
	ImageResource gCube();
	
	@Source("root.png")
	ImageResource root();

	@Source("folder.png")
	ImageResource folder();
	
	@Source("shared_folder.png")
	ImageResource sharedFolder();

	@Source("external_image.gif")
	ImageResource external_image();

	@Source("external_pdf.gif")
	ImageResource external_pdf();

	@Source("external_file.png")
	ImageResource external_file();
	
	@Source("external_resource_link.png")
	ImageResource external_resource_link();
	
	@Source("external_url.png")
	ImageResource external_url();
	
	@Source("report_template.png")
	ImageResource report_template();
	
	@Source("report.png")
	ImageResource report();

	@Source("query.png")
	ImageResource query();

	@Source("document.png")
	ImageResource document();

	@Source("metadata.png")
	ImageResource metadata();

	@Source("pdf_document.png")
	ImageResource pdf_document();

	@Source("image_document.png")
	ImageResource image_document();

	@Source("url_document.png")
	ImageResource url_document();
	
	@Source("timeseries.png")
	ImageResource timeseries();
	
	@Source("aquamaps.png")
	ImageResource aquamaps();
	
	@Source("workflow_report.png")
	ImageResource workflow_report();
	
	@Source("workflow_template.png")
	ImageResource workflow_template();

	@Source("noimage.png")
	ImageResource unknownType();
	
	@Source("ajax-loader.gif")
	ImageResource loading();
	
	@Source("invalid_name.gif")
	DataResource invalidName();
	
	@Source("WorkspacePortletLightTree.css")
	WorkspaceLightTreeCss css();
	
	@Source("error.png")
	ImageResource error();
	
	@Source("refresh.png")
	ImageResource refresh();
}
