/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.io.Serializable;

import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 5, 2013
 *
 */
//@Entity
public class ClusterCommonNameDataSourceForResultRow implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7566939414748687391L;
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int internalId;
	
	private String scientificName; //IS THE KEY
	
	private ClusterCommonNameDataSource<ResultRow> cluster = new ClusterCommonNameDataSource<ResultRow>(); //NEW CLUSTER
	
	
	public ClusterCommonNameDataSourceForResultRow(){}
	
	/**
	 * @param scientificName
	 * @param cluster
	 */
	public ClusterCommonNameDataSourceForResultRow(String scientificName, ClusterCommonNameDataSource<ResultRow> cluster) {
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

	public ClusterCommonNameDataSource<ResultRow> getCluster() {
		return cluster;
	}

	public void setCluster(ClusterCommonNameDataSource<ResultRow> cluster) {
		this.cluster = cluster;
	}
	
	
//	public int getInternalId() {
//		return internalId;
//	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClusterCommonNameDataSourceForResultRow [scientificName=");
		builder.append(scientificName);
		builder.append(", cluster=");
		builder.append(cluster);
		builder.append("]");
		return builder.toString();
	}


}
