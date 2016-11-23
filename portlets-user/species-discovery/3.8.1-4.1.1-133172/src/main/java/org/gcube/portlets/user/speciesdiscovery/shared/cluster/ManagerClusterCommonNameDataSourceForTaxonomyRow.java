/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 18, 2013
 * 
 * This class calculates the cluster for common names / data sources: hashClusterCommonNameDataSource
 *
 */
public class ManagerClusterCommonNameDataSourceForTaxonomyRow {
	

	private HashMap<String, ArrayList<String>> hashScientificNameTaxonomyRowServiceID = new HashMap<String, ArrayList<String>>(); //HASH scientific name - taxonomy row service ID
	private Map<String, TaxonomyRow> hashResult = new HashMap<String, TaxonomyRow>();
	
	private HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> hashClusterCommonNameDataSource = new HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow>(); //HASH scientific name - cluster
	
	
	/**
	 * @param hashScientificNameResultRowServiceID
	 * @param hashClusterCommonNameDataSource
	 * @param hashResult
	 * @throws Exception 
	 */
	public ManagerClusterCommonNameDataSourceForTaxonomyRow(HashMap<String, ArrayList<String>> hashScientificNameResultRowServiceID, Map<String, TaxonomyRow> hashResult) throws Exception {
		
		this.hashScientificNameTaxonomyRowServiceID = hashScientificNameResultRowServiceID;
		this.hashResult = hashResult;
		
		createStructuresForCommonName();

	}


	public void createStructuresForCommonName() throws Exception{


		if(hashScientificNameTaxonomyRowServiceID.size()>0){

			for(String scientificName : hashScientificNameTaxonomyRowServiceID.keySet()){
				
				ArrayList<String> listTaxonomyRowServiceID = hashScientificNameTaxonomyRowServiceID.get(scientificName);
				
				ClusterCommonNameDataSource<TaxonomyRow> cluster = new ClusterCommonNameDataSource<TaxonomyRow>(); //NEW CLUSTER

				for(int i=0; i<listTaxonomyRowServiceID.size(); i++){
					TaxonomyRow row = hashResult.get(listTaxonomyRowServiceID.get(i));
					cluster.updateHashCommonNamesDataSources(row);
				}
				
				hashClusterCommonNameDataSource.put(scientificName, new ClusterCommonNameDataSourceForTaxonomyRow(scientificName, cluster));
		
			}
		}
		
	}


	public HashMap<String, ArrayList<String>> getHashScientificNameResultRowID() {
		return hashScientificNameTaxonomyRowServiceID;
	}


	public void setHashScientificNameResultRowID(
			HashMap<String, ArrayList<String>> hashScientificNameResultRowID) {
		this.hashScientificNameTaxonomyRowServiceID = hashScientificNameResultRowID;
	}


	public HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> getHashClusterCommonNameDataSource() {
		return hashClusterCommonNameDataSource;
	}


	public void setHashClusterCommonNameDataSource(HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> hashClusterCommonNameDataSource) {
		this.hashClusterCommonNameDataSource = hashClusterCommonNameDataSource;
	}


	public Map<String, TaxonomyRow> getHashResult() {
		return hashResult;
	}


	public void setHashResult(Map<String, TaxonomyRow> hashResult) {
		this.hashResult = hashResult;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ManagerClusterCommonNameDataSourceForTaxonomyRow [hashScientificNameTaxonomyRowServiceID=");
		builder.append(hashScientificNameTaxonomyRowServiceID);
		builder.append(", hashResult=");
		builder.append(hashResult);
		builder.append(", hashClusterCommonNameDataSource=");
		builder.append(hashClusterCommonNameDataSource);
		builder.append("]");
		return builder.toString();
	}

}
