package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;


/**
 * The Enum GXTFolderItemTypeEnum.
 *
 * @author Francesco Mangiacrapa 
 * Jun 19, 2015
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
	SHARED_FOLDER("Shared Folder"), 
	GCUBE_ITEM("Gcube Item");

	protected String label;
	
	/**
	 * Instantiates a new GXT folder item type enum.
	 */
	GXTFolderItemTypeEnum(){}
	
	/**
	 * Instantiates a new GXT folder item type enum.
	 *
	 * @param label the label
	 */
	GXTFolderItemTypeEnum(String label)
	{
		this.label = label;
		
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}
}
