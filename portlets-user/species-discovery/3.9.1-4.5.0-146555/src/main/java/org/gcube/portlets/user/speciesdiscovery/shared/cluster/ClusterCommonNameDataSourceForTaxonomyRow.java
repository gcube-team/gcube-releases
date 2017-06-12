/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.io.Serializable;

import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 18, 2013
 *
 */
public class ClusterCommonNameDataSourceForTaxonomyRow implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 769286880968121045L;

	private String scientificName; //IS THE KEY
	
	private ClusterCommonNameDataSource<TaxonomyRow> cluster = new ClusterCommonNameDataSource<TaxonomyRow>(); //NEW CLUSTER
	
	
	public ClusterCommonNameDataSourceForTaxonomyRow(){}
	
	/**
	 * @param scientificName
	 * @param cluster
	 */
	public ClusterCommonNameDataSourceForTaxonomyRow(String scientificName, ClusterCommonNameDataSource<TaxonomyRow> cluster) {
		super();
		this.scientificName = scientificName;
		this.cluster = cluster;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public ClusterCommonNameDataSource<TaxonomyRow> getCluster() {
		return cluster;
	}

	public void setCluster(ClusterCommonNameDataSource<TaxonomyRow> cluster) {
		this.cluster = cluster;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClusterCommonNameDataSourceForTaxonomyRow [scientificName=");
		builder.append(scientificName);
		builder.append(", cluster=");
		builder.append(cluster);
		builder.append("]");
		return builder.toString();
	}

}
