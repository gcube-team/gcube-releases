/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * The Class ReleaseFile.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 5, 2015
 */
@Entity
public class ReleaseFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; //PRIMARY KEY
	
	@Lob
	private String distributionXmlFile;
	
	private Integer fk_release_internalId;

	/**
	 * 
	 */
	public ReleaseFile() {
	}

	/**
	 * Instantiates a new release file.
	 *
	 * @param distributionXmlFile the distribution xml file
	 * @param release the release
	 */
	public ReleaseFile(String distributionXmlFile, Integer release_internalId) {
		this.distributionXmlFile = distributionXmlFile;
		this.fk_release_internalId = release_internalId;
	}


	/**
	 * @return the distributionXmlFile
	 */
	public String getDistributionXmlFile() {
		return distributionXmlFile;
	}


	/**
	 * @param distributionXmlFile the distributionXmlFile to set
	 */
	public void setDistributionXmlFile(String distributionXmlFile) {
		this.distributionXmlFile = distributionXmlFile;
	}

	/**
	 * @return the fk_release_internalId
	 */
	public Integer getFk_release_internalId() {
		return fk_release_internalId;
	}

	/**
	 * @param fk_release_internalId the fk_release_internalId to set
	 */
	public void setFk_release_internalId(Integer fk_release_internalId) {
		this.fk_release_internalId = fk_release_internalId;
	}

	/**
	 * @return the internalId
	 */
	public int getInternalId() {
		return internalId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReleaseFile [internalId=");
		builder.append(internalId);
		builder.append(", distributionXmlFile=");
		builder.append(distributionXmlFile);
		builder.append(", fk_release_internalId=");
		builder.append(fk_release_internalId);
		builder.append("]");
		return builder.toString();
	}

}
