/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * The Interface WorkspaceLightTreeResources.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 8, 2015
 */
public interface WorkspaceLightTreeResources extends ClientBundle {

	public static final WorkspaceLightTreeResources INSTANCE =  GWT.create(WorkspaceLightTreeResources.class);

	/**
	 * Root.
	 *
	 * @return the image resource
	 */
	@Source("root.png")
	ImageResource root();

	/**
	 * Folder.
	 *
	 * @return the image resource
	 */
	@Source("folder.png")
	ImageResource folder();

	/**
	 * Shared folder.
	 *
	 * @return the image resource
	 */
	@Source("shared_folder.png")
	ImageResource sharedFolder();

	/**
	 * External_image.
	 *
	 * @return the image resource
	 */
	@Source("external_image.gif")
	ImageResource external_image();

	/**
	 * External_pdf.
	 *
	 * @return the image resource
	 */
	@Source("external_pdf.gif")
	ImageResource external_pdf();

	/**
	 * External_file.
	 *
	 * @return the image resource
	 */
	@Source("external_file.png")
	ImageResource external_file();

	/**
	 * External_resource_link.
	 *
	 * @return the image resource
	 */
	@Source("external_resource_link.png")
	ImageResource external_resource_link();

	/**
	 * External_url.
	 *
	 * @return the image resource
	 */
	@Source("external_url.png")
	ImageResource external_url();

	/**
	 * Report_template.
	 *
	 * @return the image resource
	 */
	@Source("report_template.png")
	ImageResource report_template();

	/**
	 * Report.
	 *
	 * @return the image resource
	 */
	@Source("report.png")
	ImageResource report();

	/**
	 * Query.
	 *
	 * @return the image resource
	 */
	@Source("query.png")
	ImageResource query();

	/**
	 * Document.
	 *
	 * @return the image resource
	 */
	@Source("document.png")
	ImageResource document();

	/**
	 * Metadata.
	 *
	 * @return the image resource
	 */
	@Source("metadata.png")
	ImageResource metadata();

	/**
	 * Pdf_document.
	 *
	 * @return the image resource
	 */
	@Source("pdf_document.png")
	ImageResource pdf_document();

	/**
	 * Image_document.
	 *
	 * @return the image resource
	 */
	@Source("image_document.png")
	ImageResource image_document();

	/**
	 * Url_document.
	 *
	 * @return the image resource
	 */
	@Source("url_document.png")
	ImageResource url_document();

	/**
	 * Timeseries.
	 *
	 * @return the image resource
	 */
	@Source("timeseries.png")
	ImageResource timeseries();

	/**
	 * Aquamaps.
	 *
	 * @return the image resource
	 */
	@Source("aquamaps.png")
	ImageResource aquamaps();

	/**
	 * Workflow_report.
	 *
	 * @return the image resource
	 */
	@Source("workflow_report.png")
	ImageResource workflow_report();

	/**
	 * Workflow_template.
	 *
	 * @return the image resource
	 */
	@Source("workflow_template.png")
	ImageResource workflow_template();

	/**
	 * Unknown type.
	 *
	 * @return the image resource
	 */
	@Source("noimage.png")
	ImageResource unknownType();

	/**
	 * Loading.
	 *
	 * @return the image resource
	 */
	@Source("ajax-loader.gif")
	ImageResource loading();

	/**
	 * Gucbe item.
	 *
	 * @return the image resource
	 */
	@Source("gcubeItem.jpeg")
	ImageResource gucbeItem();


	/**
	 * Unknown.
	 *
	 * @return the image resource
	 */
	@Source("icon-unknown.gif")
	ImageResource unknown();

	/**
	 * Invalid name.
	 *
	 * @return the data resource
	 */
	@Source("invalid_name.gif")
	DataResource invalidName();

	/**
	 * Css.
	 *
	 * @return the workspace light tree css
	 */
	@Source("WorkspaceExplorerAppCss.css")
	WorkspaceExplorerAppCss css();

	/**
	 * Error.
	 *
	 * @return the image resource
	 */
	@Source("error.png")
	ImageResource error();

	/**
	 * Refresh.
	 *
	 * @return the image resource
	 */
	@Source("refresh.png")
	ImageResource refresh();
}
