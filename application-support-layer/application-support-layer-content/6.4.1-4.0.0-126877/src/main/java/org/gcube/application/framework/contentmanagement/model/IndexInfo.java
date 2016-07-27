package org.gcube.application.framework.contentmanagement.model;

import java.io.Serializable;

/**
 * @author Valia Tsagkalidou (NKUA)
 *
 */
public class IndexInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean fts;
	protected boolean geospatial;
	protected boolean similarity;
	protected boolean opensearch;
	
	/**
	 * The constructor of the class
	 *
	 */
	public IndexInfo() {
		super();
		fts = false;
		geospatial = false;
		similarity = false;
		opensearch = false;
	}
	
	/**
	 * @return whether an FTS index exists for this collection
	 */
	public boolean isFts() {
		return fts;
	}
	
	public boolean isOpenSearch() {
		return opensearch;
	}
	
	/**
	 * @return whether a geospatial index exists for this collection
	 */
	public boolean isGeospatial() {
		return geospatial;
	}
	
	/**
	 * @return whether a similarity index exists for this collection
	 */
	public boolean isSimilarity() {
		return similarity;
	}
	
	/**
	 * Sets the value whether a full text index exists
	 * @param fts true or false
	 */
	public void setFts(boolean fts) {
		this.fts = fts;
	}
	
	/**
	 * Sets the value whether a geo-spatial index exists
	 * @param geospatial true or false
	 */
	public void setGeospatial(boolean geospatial) {
		this.geospatial = geospatial;
	}
	
	/**
	 * Sets the value whether a similarity index exists
	 * @param similarity true or false
	 */
	public void setSimilarity(boolean similarity) {
		this.similarity = similarity;
	}
	
	public void setOpenSearch(boolean openSearch) {
		this.opensearch = openSearch;
	}
	
	public String indexType() {
		if (opensearch) {
			return "opensearch";
		} else if (fts) {
			return "fts";
		} else if (similarity) {
			return "similarity";
		} else
			return "geospatial";
	}
}
