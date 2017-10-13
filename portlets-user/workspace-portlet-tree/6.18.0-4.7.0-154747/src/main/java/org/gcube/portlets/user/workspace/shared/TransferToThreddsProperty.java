/**
 *
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * The Class TransferToThreddsProperty.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 29, 2017
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TransferToThreddsProperty implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7160662779647989154L;


	private String folderId;
	private String catalogueName;
	private String vreName;
	private String vreScope;
	private String metadataFolderId;
	private String metadataFolderName;


	/**
	 * Instantiates a new transfering to thredds property.
	 */
	public TransferToThreddsProperty() {

	}

	/**
	 * Instantiates a new transfering to thredds property.
	 *
	 * @param folderId the folder id
	 * @param catalogueName the catalogue name
	 * @param vreName the vre name
	 * @param vreScope the vre scope
	 * @param metadataFolderId the metadata folder id
	 */
	public TransferToThreddsProperty(
		String folderId, String catalogueName, String vreName,
		String vreScope, String metadataFolderId, String metadataFolderName) {

		super();
		this.folderId = folderId;
		this.catalogueName = catalogueName;
		this.vreName = vreName;
		this.vreScope = vreScope;
		this.metadataFolderId = metadataFolderId;
		this.metadataFolderName = metadataFolderName;
	}


	/**
	 * @return the folderId
	 */
	public String getFolderId() {

		return folderId;
	}


	/**
	 * @return the catalogueName
	 */
	public String getCatalogueName() {

		return catalogueName;
	}


	/**
	 * @return the vreName
	 */
	public String getVreName() {

		return vreName;
	}


	/**
	 * @return the vreScope
	 */
	public String getVreScope() {

		return vreScope;
	}


	/**
	 * @return the metadataFolderId
	 */
	public String getMetadataFolderId() {

		return metadataFolderId;
	}


	/**
	 * @return the metadataFolderName
	 */
	public String getMetadataFolderName() {

		return metadataFolderName;
	}


	/**
	 * @param folderId the folderId to set
	 */
	public void setFolderId(String folderId) {

		this.folderId = folderId;
	}


	/**
	 * @param catalogueName the catalogueName to set
	 */
	public void setCatalogueName(String catalogueName) {

		this.catalogueName = catalogueName;
	}


	/**
	 * @param vreName the vreName to set
	 */
	public void setVreName(String vreName) {

		this.vreName = vreName;
	}


	/**
	 * @param vreScope the vreScope to set
	 */
	public void setVreScope(String vreScope) {

		this.vreScope = vreScope;
	}


	/**
	 * @param metadataFolderId the metadataFolderId to set
	 */
	public void setMetadataFolderId(String metadataFolderId) {

		this.metadataFolderId = metadataFolderId;
	}


	/**
	 * @param metadataFolderName the metadataFolderName to set
	 */
	public void setMetadataFolderName(String metadataFolderName) {

		this.metadataFolderName = metadataFolderName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TransferToThreddsProperty [folderId=");
		builder.append(folderId);
		builder.append(", catalogueName=");
		builder.append(catalogueName);
		builder.append(", vreName=");
		builder.append(vreName);
		builder.append(", vreScope=");
		builder.append(vreScope);
		builder.append(", metadataFolderId=");
		builder.append(metadataFolderId);
		builder.append(", metadataFolderName=");
		builder.append(metadataFolderName);
		builder.append("]");
		return builder.toString();
	}


}
