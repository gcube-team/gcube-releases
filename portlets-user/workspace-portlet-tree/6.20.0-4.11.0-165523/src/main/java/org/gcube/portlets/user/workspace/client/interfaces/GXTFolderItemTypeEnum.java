package org.gcube.portlets.user.workspace.client.interfaces;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public enum GXTFolderItemTypeEnum implements Serializable {
	EXTERNAL_FILE("External File"),
	EXTERNAL_IMAGE("External Image"),
	EXTERNAL_PDF_FILE("External PDF File"),
	EXTERNAL_URL("External Url"),
	EXTERNAL_RESOURCE_LINK("Exteranl Resource Link"),
	QUERY("Query"),
	REPORT("Report"),
	REPORT_TEMPLATE("Report Template"),
	TIME_SERIES("Time Series"),
	DOCUMENT("Document"),
	IMAGE_DOCUMENT("Image Document"),
	PDF_DOCUMENT("PDF Document"),
	URL_DOCUMENT("Url Document"),
	METADATA("Metadata"),
	WORKFLOW_REPORT("Workflow Report"),
	WORKFLOW_TEMPLATE("Workflow Template"),
	UNKNOWN_TYPE("Unknow Type"),
	ANNOTATION("Annotation"),
	FOLDER("Folder"),
	GCUBE_ITEM("Gcube Item"),
	FOLDER_SHARED("Shared Folder"),
	FOLDER_PUBLIC("Public Folder"),
	FOLDER_SHARED_PUBLIC("Shared and Public Folder");

	protected String label;

	GXTFolderItemTypeEnum(){}

	GXTFolderItemTypeEnum(String label)
	{
		this.label = label;

	}
	public String getLabel()
	{
		return label;
	}
}
