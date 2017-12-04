package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResult;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ClusterStructuresForTaxonomyRow implements Serializable{
	
	private static final String DATA_SOURCE_NOT_FOUND = "Data Source not found";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2680552140187511057L;
	
	//USED ON CLIENT
	private Map<String, LightTaxonomyRow> hashLightResult = new HashMap<String, LightTaxonomyRow>();
	
	private HashMap<String, ArrayList<String>> hashClusterScientificNameTaxonomyRowServiceID = new HashMap<String, ArrayList<String>>(); //HASH scientific name - taxonomy row service ID
	private ArrayList<String> listFoundDataSources = new ArrayList<String>();
	private ArrayList<String> listTaxonomyRowServiceID;

	private boolean isReduced;

	private int totalRow;
	
	//USED ON SERVER FOR CLUSERING ON COMMON NAMES
	private Map<String, TaxonomyRow> hashResult = new HashMap<String, TaxonomyRow>();

	public ClusterStructuresForTaxonomyRow(){
		
	}
	
	public ClusterStructuresForTaxonomyRow(SearchResult<TaxonomyRow> result, boolean isReduced, int totalRow) {
		//Create hashMap row service ID - resultRow
		for (TaxonomyRow row : result.getResults()){
			
			LightTaxonomyRow lr = convetTaxonomyRowToLigthTaxonomyRow(row);
			
			List<LightTaxonomyRow> parents = new ArrayList<LightTaxonomyRow>();
			for (TaxonomyRow txParent: row.getParents()) {	
				parents.add(convetTaxonomyRowToLigthTaxonomyRow(txParent));
			}
			
			lr.setParent(parents);
			hashLightResult.put(row.getServiceId(), lr);
			hashResult.put(row.getServiceId(), row);
		}
		
		this.isReduced = isReduced;
		this.totalRow = totalRow;
		
		createStructures(result.getResults());
	}
	
	public static LightTaxonomyRow convetTaxonomyRowToLigthTaxonomyRow(TaxonomyRow row){
		
		LightTaxonomyRow lr = new LightTaxonomyRow(row.getId());
		lr.setServiceId(row.getServiceId());
		lr.setBaseTaxonValue(row.getBaseTaxonValue());
		lr.setDataProviderId(row.getDataProviderId());
		lr.setDataProviderName(row.getDataProviderName());
		lr.setName(row.getName());
		lr.setParentID(row.getParentID());
		lr.setRank(row.getRank());
		lr.setParentIndex(row.getParentIndex());
		lr.setStatusName(row.getStatusName());
		lr.setStatusRefId(row.getStatusRefId());
		lr.setStatusRemarks(row.getStatusRemarks());
		
		return lr;
	}
	
	public void createStructures(ArrayList<TaxonomyRow> arrayListTax){

		if(arrayListTax.size()>0){

			for (TaxonomyRow row : arrayListTax) {

				String dataSource = row.getDataProviderName();
				
				if(dataSource==null || dataSource.isEmpty())
					dataSource = DATA_SOURCE_NOT_FOUND;

				String keyScientificName = row.getName();
				
				if(keyScientificName== null || keyScientificName.isEmpty())
					keyScientificName = "Undefined";
				else
					keyScientificName = NormalizeString.lowerCaseUpFirstChar(keyScientificName);
				
				if(hashClusterScientificNameTaxonomyRowServiceID.get(keyScientificName)==null){

					listTaxonomyRowServiceID = new ArrayList<String>(); //USED FOR CLUSTER COMMON NAMES
						
				}
				else{

					listTaxonomyRowServiceID = hashClusterScientificNameTaxonomyRowServiceID.get(keyScientificName); //USED FOR CLUSTER COMMON NAMES
				}

				if(listFoundDataSources.indexOf(dataSource)==-1)
					listFoundDataSources.add(dataSource);

				listTaxonomyRowServiceID.add(row.getServiceId());
				hashClusterScientificNameTaxonomyRowServiceID.put(keyScientificName, listTaxonomyRowServiceID);

			}
		}
		
	}
	
	public ArrayList<String> getListFoundDataSources() {
		return listFoundDataSources;
	}

	public HashMap<String, ArrayList<String>> getHashClusterScientificNameTaxonomyRowServiceID() {
		return hashClusterScientificNameTaxonomyRowServiceID;
	}

	public ArrayList<LightTaxonomyRow> getResult() {
		return new ArrayList<LightTaxonomyRow>(hashLightResult.values());
	}
	
	public LightTaxonomyRow getLightTaxonomyRowByKey(String key) {
		return hashLightResult.get(key);
	}

	public boolean isReduced() {
		return isReduced;
	}

	public void setReduced(boolean isReduced) {
		this.isReduced = isReduced;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public Map<String, LightTaxonomyRow> getHashLightResult() {
		return hashLightResult;
	}

	public Map<String, TaxonomyRow> getHashResult() {
		return hashResult;
	}

	public void setHashResult(Map<String, TaxonomyRow> hashResult) {
		this.hashResult = hashResult;
	}

}
