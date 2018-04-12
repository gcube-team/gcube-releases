/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.trash;

/**
 * @author valentina
 *
 */
public enum WorkspaceTrashItemProperties {

	DELETE_DATE("hl:deletedTime"),
	DELETE_USER("hl:deletedBy"),
	ORIGINAL_PARENT_ID("hl:originalParentId"),
	ORIGINAL_PATH("hl:deletedFrom"),
	NAME("hl:name"),
	MIME_TYPE("hl:mimeType"),
	LENGTH("hl:length"),
	IS_FOLDER("hl:isFolder");
		
	public String value;
	
	WorkspaceTrashItemProperties(String value)
	{
		this.value = value;
	}
	
	/**
	 * Return the WorkspaceTrashItemProperties value.
	 * @return the WorkspaceTrashItemProperties string.
	 */
	public String getValue()
	{
		return value;
	}


}