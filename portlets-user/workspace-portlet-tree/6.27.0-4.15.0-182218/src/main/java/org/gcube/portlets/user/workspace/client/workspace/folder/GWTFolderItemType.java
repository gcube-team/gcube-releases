/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder;

import org.gcube.portlets.user.workspace.client.workspace.GWTItemDescription;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public enum GWTFolderItemType implements GWTItemDescription{
	AQUAMAPS_ITEM("AquaMaps Item", "tree-aquamaps-icon"),
	EXTERNAL_FILE("External File", "tree-external-file-icon"),
	EXTERNAL_IMAGE("External Image", "tree-external-image-icon"),
	EXTERNAL_PDF_FILE("External PDF File", "tree-external-pdf-file-icon"),
	EXTERNAL_URL("External Url", "tree-externalUrl-icon"),
	QUERY("Query", "tree-query-icon"),
	REPORT("Report", "tree-report-icon"),
	REPORT_TEMPLATE("Report Template", "tree-report-template-icon"),
	TIME_SERIES("Time Series", "tree-timeseries-icon"),
	DOCUMENT("Document", "tree-document-icon"),
	IMAGE_DOCUMENT("Image Document", "tree-imageDocument-icon"),
	PDF_DOCUMENT("PDF Document", "tree-pdfDocument-icon"),
	URL_DOCUMENT("Url Document", "tree-urlDocument-icon"),
	METADATA("Metadata", "tree-metadata-icon"),
	WORKFLOW_REPORT("Workflow Report", "tree-workflowReport-icon"),
	WORKFLOW_TEMPLATE("Workflow Template", "tree-workflowTemplate-icon"),
	//TODO to add
	ANNOTATION("Annotation", "");
	
	protected String iconClass;
	protected String label;
	
	GWTFolderItemType(String label, String iconClass)
	{
		this.label = label;
		this.iconClass = iconClass;
	}
	
	public String getIconClass() {
		return iconClass;
	}

	public String getLabel()
	{
		return label;
	}

}
