package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResult;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

//import com.extjs.gxt.ui.client.widget.Html;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ClusterStructuresForResultRow implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1205113606387863736L;
	private ArrayList<String> listResultRowServiceID;
	private ArrayList<String> listFoundDataSources = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> hashScientificNameResultRowServiceID = new HashMap<String, ArrayList<String>>(); //HASH scientific name - result row service ID
	private boolean isReduced;
	private int allResultRowSize;

	private int resultSize;

	private Map<String, ResultRow> hashResult = new HashMap<String, ResultRow>(); //HASH result row service ID - result row
	
	public ClusterStructuresForResultRow(){}

	public ClusterStructuresForResultRow(SearchResult<ResultRow> result, boolean isReduced, int totalRow){

		//Create hashMap service ID - resultRow
		for (ResultRow row : result.getResults()) {
//			hashResult.put(row.getIdToString(), row);
			
			hashResult.put(row.getServiceId(), row);
		}
//		
		this.isReduced = isReduced;
		
		this.allResultRowSize = totalRow;
		this.resultSize = result.getResults().size();
		
		createStructures(result.getResults());
//		createStructuresForCommonName();
	}
	
	
	public void createStructures(ArrayList<ResultRow> arrayListRR){

		if(arrayListRR.size()>0){

			for (ResultRow row : arrayListRR) {

				String keyScientificName = "Name not found";
				
				if(row.getParents().get(0)!=null){
					
					keyScientificName = row.getParents().get(0).getName();
					if(keyScientificName== null || keyScientificName.isEmpty())
						keyScientificName = "Undefined";
					else
						keyScientificName = NormalizeString.lowerCaseUpFirstChar(keyScientificName);
					
					if(hashScientificNameResultRowServiceID.get(keyScientificName)==null){
						
						listResultRowServiceID = new ArrayList<String>(); //USED FOR CLUSTER COMMON NAMES
					}
					else{
						
						listResultRowServiceID = hashScientificNameResultRowServiceID.get(keyScientificName); //USED FOR CLUSTER COMMON NAMES
					}
				}
				
				String dataSource = row.getDataSourceName();

				if(dataSource==null || dataSource.isEmpty())
					dataSource = "Data Source not found";
				
				if(listFoundDataSources.indexOf(dataSource)==-1)
					listFoundDataSources.add(dataSource);
				
//				listResultRowServiceID.add(row.getIdToString()+"");
				
				listResultRowServiceID.add(row.getServiceId());
				
				//USED FOR CLUSTER COMMON NAMES
				hashScientificNameResultRowServiceID.put(keyScientificName, listResultRowServiceID);
			}
		}	
	}
	
	public HashMap<String, ArrayList<String>> getHashClusterScientificNameResultRowServiceID() {
		return hashScientificNameResultRowServiceID;
	}

	public List<String> getListFoundDataSources() {
		return listFoundDataSources;
	}

	public ArrayList<String> getListResultRowID() {
		return listResultRowServiceID;
	}

	public void setListResultRowID(ArrayList<String> listResultRowID) {
		this.listResultRowServiceID = listResultRowID;
	}

	public boolean isReduced() {
		return isReduced;
	}

	public void setReduced(boolean isReduced) {
		this.isReduced = isReduced;
	}

	public int getAllResultRowSize() {
		return allResultRowSize;
	}

	public void setAllResultRowSize(int allResultRowSize) {
		this.allResultRowSize = allResultRowSize;
	}

	public int getResultSize() {
		return resultSize;
	}

	public void setResultSize(int resultSize) {
		this.resultSize = resultSize;
	}

	public void setListFoundDataSources(ArrayList<String> listFoundDataSources) {
		this.listFoundDataSources = listFoundDataSources;
	}
	
	public Map<String, ResultRow> getHashResult() {
		return hashResult;
	}

	public void setHashResult(Map<String, ResultRow> hashResult) {
		this.hashResult = hashResult;
	}
}
