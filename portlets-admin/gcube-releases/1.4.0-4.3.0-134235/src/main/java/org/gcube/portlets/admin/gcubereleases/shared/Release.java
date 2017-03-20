/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * The Class Release.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
@Entity
public class Release implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 781101530705538680L;

	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; //PRIMARY KEY
	
	private String id;
	private String name;
	private String url;
	private int packagesNmb;
	
	@Lob
	private String description; //release notes
	private boolean onLine = true;
	
	private long insertTime; //in Milliseconds
	
	private long latestUpdate; //in Milliseconds
	
	public static final String ID_FIELD = "id";
	
//	@OneToMany(mappedBy="releaseInternalId", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@OneToMany(mappedBy="release", orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@CascadeOnDelete
	private List<Package> listPackages = new ArrayList<Package>();
	
	private Long endReleaseDate; //in Milliseconds
	
	private Long startReleaseDate; //in Milliseconds
	
	/**
	 * Instantiates a new release.
	 */
	public Release() {
	}


	
	/**
	 * Instantiates a new release.
	 *
	 * @param id the id
	 * @param name the name
	 * @param url the url
	 * @param endReleaseDate the end release date
	 */
	public Release(String id, String name, String url, Long endReleaseDate) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.endReleaseDate = endReleaseDate;
	}


	
	/**
	 * Instantiates a new release.
	 *
	 * @param id the id
	 * @param name the name
	 * @param url the url
	 * @param endReleaseDate the end release date
	 * @param listPackages the list packages
	 */
	public Release(String id, String name, String url, Long endReleaseDate,
			List<Package> listPackages) {
		this(id, name, url, endReleaseDate);
		this.listPackages = listPackages;
	}


	
	/**
	 * Instantiates a new release.
	 *
	 * @param id the id
	 * @param name the name
	 * @param url the url
	 * @param packagesNmb the packages nmb
	 * @param description the description
	 * @param onLine the on line
	 * @param insertTime the insert time
	 * @param latestUpdate the latest update
	 * @param endReleaseDate the end release date
	 * @param listPackages the list packages
	 */
	public Release(String id, String name, String url, int packagesNmb,
			String description, boolean onLine, long insertTime,
			long latestUpdate, Long endReleaseDate, List<Package> listPackages) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.packagesNmb = packagesNmb;
		this.description = description;
		this.onLine = onLine;
		this.insertTime = insertTime;
		this.latestUpdate = latestUpdate;
		this.endReleaseDate = endReleaseDate;
		this.listPackages = listPackages;
	}


	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
	 */
	public int getInternalId() {
		return internalId;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Gets the list packages.
	 *
	 * @return the list packages
	 */
	public List<Package> getListPackages() {
		return listPackages;
	}

	/**
	 * Sets the internal id.
	 *
	 * @param internalId the new internal id
	 */
	public void setInternalId(int internalId) {
		this.internalId = internalId;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Sets the list packages.
	 *
	 * @param listPackages the new list packages
	 */
	public void setListPackages(List<Package> listPackages) {
		this.listPackages = listPackages;
	}

	/**
	 * Sets the packages nmb.
	 *
	 * @param i the new packages nmb
	 */
	public void setPackagesNmb(int i) {
		this.packagesNmb = i;
	}

	/**
	 * Gets the packages nmb.
	 *
	 * @return the packages nmb
	 */
	public int getPackagesNmb() {
		return packagesNmb;
	}

	/**
	 * Gets the insert time.
	 *
	 * @return the insert time
	 */
	public long getInsertTime() {
		return insertTime;
	}

	/**
	 * Sets the insert time.
	 *
	 * @param insertTime the new insert time
	 */
	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * Checks if is on line.
	 *
	 * @return true, if is on line
	 */
	public boolean isOnLine() {
		return onLine;
	}


	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * Sets the on line.
	 *
	 * @param onLine the new on line
	 */
	public void setOnLine(boolean onLine) {
		this.onLine = onLine;
	}


	/**
	 * Gets the latest update.
	 *
	 * @return the latest update
	 */
	public long getLatestUpdate() {
		return latestUpdate;
	}


	/**
	 * Sets the latest update.
	 *
	 * @param latestUpdate the new latest update
	 */
	public void setLatestUpdate(long latestUpdate) {
		this.latestUpdate = latestUpdate;
	}

	

	/**
	 * Gets the release date.
	 *
	 * @return the releaseDate
	 */
	public Long getReleaseDate() {
		return endReleaseDate;
	}


	/**
	 * Sets the release date.
	 *
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(Long releaseDate) {
		this.endReleaseDate = releaseDate;
	}
	
	/**
	 * Gets the start release date.
	 *
	 * @return the startReleaseDate
	 */
	public Long getStartReleaseDate() {
		return startReleaseDate;
	}


	/**
	 * Sets the start release date.
	 *
	 * @param startReleaseDate the startReleaseDate to set
	 */
	public void setStartReleaseDate(Long startReleaseDate) {
		this.startReleaseDate = startReleaseDate;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Release [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", url=");
		builder.append(url);
		builder.append(", packagesNmb=");
		builder.append(packagesNmb);
		builder.append(", description=");
		builder.append(description);
		builder.append(", onLine=");
		builder.append(onLine);
		builder.append(", insertTime=");
		builder.append(insertTime);
		builder.append(", latestUpdate=");
		builder.append(latestUpdate);
//		builder.append(", listPackages=");
//		builder.append(listPackages);
		builder.append(", endReleaseDate=");
		builder.append(endReleaseDate);
		builder.append(", startReleaseDate=");
		builder.append(startReleaseDate);
		builder.append("]");
		return builder.toString();
	}

	
}
