/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * The Class AccountingPackage.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
@Entity
@CascadeOnDelete
public class AccountingPackage implements Accounting, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7602074324205596793L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; //PRIMARY KEY
	
	private String id;
	private int downloadNmb = 0;
	private int javadocNmb = 0;
	private int mavenRepoNmb = 0; //IS NEXUS
	private int wikiNmb = 0;
	
	private String packageRef; // is package ID property
	
	public static final String ID_FIELD = "id";
	public static final String PACKAGE_REF = "packageRef";
	
	/**
	 * Instantiates a new accounting package.
	 */
	public AccountingPackage() {
	}


	/**
	 * Instantiates a new accounting package.
	 *
	 * @param id the id
	 * @param packageRef the package ref
	 * @param downloadNmb the download nmb
	 * @param javadocNmb the javadoc nmb
	 * @param mavenRepoNmb the maven repo nmb
	 * @param wikiNmb the wiki nmb
	 */
	public AccountingPackage(String id, String packageRef, int downloadNmb,
			int javadocNmb, int mavenRepoNmb, int wikiNmb) {
		this.id = id;
		this.packageRef = packageRef;
		this.downloadNmb = downloadNmb;
		this.javadocNmb = javadocNmb;
		this.mavenRepoNmb = mavenRepoNmb;
		this.wikiNmb = wikiNmb;
	}
	

	/**
	 * Instantiates a new accounting package.
	 *
	 * @param packageRef the package ref
	 * @param downloadNmb the download nmb
	 * @param javadocNmb the javadoc nmb
	 * @param mavenRepoNmb the maven repo nmb
	 * @param wikiNmb the wiki nmb
	 */
	public AccountingPackage(String packageRef, int downloadNmb,
			int javadocNmb, int mavenRepoNmb, int wikiNmb) {
		this.packageRef = packageRef;
		this.downloadNmb = downloadNmb;
		this.javadocNmb = javadocNmb;
		this.mavenRepoNmb = mavenRepoNmb;
		this.wikiNmb = wikiNmb;
	}
	

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the package ref.
	 *
	 * @return the package ref
	 */
	public String getPackageRef() {
		return packageRef;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.shared.Accounting#getDownloadNmb()
	 */
	public int getDownloadNmb() {
		return downloadNmb;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.shared.Accounting#getJavadocNmb()
	 */
	public int getJavadocNmb() {
		return javadocNmb;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.shared.Accounting#getMavenRepoNmb()
	 */
	public int getMavenRepoNmb() {
		return mavenRepoNmb;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.shared.Accounting#getWikiNmb()
	 */
	public int getWikiNmb() {
		return wikiNmb;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the package ref.
	 *
	 * @param packageRef the new package ref
	 */
	public void setPackageRef(String packageRef) {
		this.packageRef = packageRef;
	}

	/**
	 * Sets the download nmb.
	 *
	 * @param downloadNmb the new download nmb
	 */
	public void setDownloadNmb(int downloadNmb) {
		this.downloadNmb = downloadNmb;
	}

	/**
	 * Sets the javadoc nmb.
	 *
	 * @param javadocNmb the new javadoc nmb
	 */
	public void setJavadocNmb(int javadocNmb) {
		this.javadocNmb = javadocNmb;
	}


	/**
	 * Sets the maven repo nmb.
	 *
	 * @param mavenRepoNmb the new maven repo nmb
	 */
	public void setMavenRepoNmb(int mavenRepoNmb) {
		this.mavenRepoNmb = mavenRepoNmb;
	}

	/**
	 * Sets the wiki nmb.
	 *
	 * @param wikiNmb the new wiki nmb
	 */
	public void setWikiNmb(int wikiNmb) {
		this.wikiNmb = wikiNmb;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.shared.Accounting#getPackageId()
	 */
	@Override
	public String getPackageId() {
		return this.id;
	}

	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
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
		builder.append("AccountingPackage [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", downloadNmb=");
		builder.append(downloadNmb);
		builder.append(", javadocNmb=");
		builder.append(javadocNmb);
		builder.append(", mavenRepoNmb=");
		builder.append(mavenRepoNmb);
		builder.append(", wikiNmb=");
		builder.append(wikiNmb);
		builder.append(", packageRef=");
		builder.append(packageRef);
		builder.append("]");
		return builder.toString();
	}
}
