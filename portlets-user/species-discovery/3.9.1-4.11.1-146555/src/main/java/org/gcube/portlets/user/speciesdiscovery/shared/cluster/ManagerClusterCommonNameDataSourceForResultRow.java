/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 5, 2013
 *
 * This class calculates the cluster for common names / data sources: hashClusterCommonNameDataSource
 */
public class ManagerClusterCommonNameDataSourceForResultRow {
	

	private HashMap<String, ArrayList<String>> hashScientificNameResultRowServiceID = new HashMap<String, ArrayList<String>>(); //HASH scientific name - result row service ID
	private Map<String, ResultRow> hashResult = new HashMap<String, ResultRow>();
	
	private HashMap<String, ClusterCommonNameDataSourceForResultRow> hashClusterCommonNameDataSource = new HashMap<String, ClusterCommonNameDataSourceForResultRow>(); //HASH scientific name - cluster
	
	
	/**
	 * @param hashScientificNameResultRowID
	 * @param hashClusterCommonNameDataSource
	 * @param hashResult
	 * @throws Exception 
	 */
	public ManagerClusterCommonNameDataSourceForResultRow(HashMap<String, ArrayList<String>> hashScientificNameResultRowID, Map<String, ResultRow> hashResult) throws Exception {
		
		this.hashScientificNameResultRowServiceID = hashScientificNameResultRowID;
		this.hashResult = hashResult;
		
		createStructuresForCommonName();

	}


	public void createStructuresForCommonName() throws Exception{

		if(hashScientificNameResultRowServiceID.size()>0){

			for(String scientificName : hashScientificNameResultRowServiceID.keySet()){
				
				ArrayList<String> listResultRowServiceID = hashScientificNameResultRowServiceID.get(scientificName); //get RR serviceID

				ClusterCommonNameDataSource<ResultRow> cluster = new ClusterCommonNameDataSource<ResultRow>(); //NEW CLUSTER

				for(int i=0; i<listResultRowServiceID.size(); i++){
					ResultRow row = hashResult.get(listResultRowServiceID.get(i));
					cluster.updateHashCommonNamesDataSources(row);
				}
				
				hashClusterCommonNameDataSource.put(scientificName, new ClusterCommonNameDataSourceForResultRow(scientificName, cluster));
			}
		}
		
	}


	public HashMap<String, ArrayList<String>> getHashScientificNameResultRowID() {
		return hashScientificNameResultRowServiceID;
	}


	public void setHashScientificNameResultRowID(
			HashMap<String, ArrayList<String>> hashScientificNameResultRowID) {
		this.hashScientificNameResultRowServiceID = hashScientificNameResultRowID;
	}


	public HashMap<String, ClusterCommonNameDataSourceForResultRow> getHashClusterCommonNameDataSource() {
		return hashClusterCommonNameDataSource;
	}


	public void setHashClusterCommonNameDataSource(
			HashMap<String, ClusterCommonNameDataSourceForResultRow> hashClusterCommonNameDataSource) {
		this.hashClusterCommonNameDataSource = hashClusterCommonNameDataSource;
	}


	public Map<String, ResultRow> getHashResult() {
		return hashResult;
	}


	public void setHashResult(Map<String, ResultRow> hashResult) {
		this.hashResult = hashResult;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ManagerClusterCommonNameDataSource [hashScientificNameResultRowID=");
		builder.append(hashScientificNameResultRowServiceID);
		builder.append(", hashClusterCommonNameDataSource=");
		builder.append(hashClusterCommonNameDataSource);
		builder.append(", hashResult=");
		builder.append(hashResult);
		builder.append("]");
		return builder.toString();
	}

}
