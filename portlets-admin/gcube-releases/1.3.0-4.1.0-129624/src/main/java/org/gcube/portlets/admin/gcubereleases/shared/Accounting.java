/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

/**
 * The Interface Accounting.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface Accounting {

	/**
	 * Gets the package id.
	 *
	 * @return the package id
	 */
	public String getPackageId();

	/**
	 * Gets the download nmb.
	 *
	 * @return the download nmb
	 */
	public int getDownloadNmb();

	/**
	 * Gets the javadoc nmb.
	 *
	 * @return the javadoc nmb
	 */
	public int getJavadocNmb();


	/**
	 * Gets the maven repo nmb.
	 *
	 * @return the maven repo nmb
	 */
	public int getMavenRepoNmb();

	/**
	 * Gets the wiki nmb.
	 *
	 * @return the wiki nmb
	 */
	public int getWikiNmb();
}
