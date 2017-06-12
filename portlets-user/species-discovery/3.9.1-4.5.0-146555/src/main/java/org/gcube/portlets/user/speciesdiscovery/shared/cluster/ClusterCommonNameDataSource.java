package org.gcube.portlets.user.speciesdiscovery.shared.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 5, 2013
 *
 * @param <T>
 */
//@Entity
public class ClusterCommonNameDataSource<T extends FetchingElement> implements Serializable{
	
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = -4602671242562941238L;
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int internalId;
	
	
	private HashMap<String, ArrayList<String>> hashMapCommonNameDataSources = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> listDataSourcesFound =  new ArrayList<String>();
	
	public ClusterCommonNameDataSource() {
	}

	//UPDATE A HASH MAP WITH ALL COMMON NAME - DATA SORUCES.
	public void updateHashCommonNamesDataSources(T item) {
		
		if(item instanceof ResultRow)
			updateHashCommonNamesDataSources((ResultRow) item);
		
		else if (item instanceof TaxonomyRow)
			updateHashCommonNamesDataSources((TaxonomyRow) item);

	}
	
	//UPDATE A HASH MAP WITH ALL COMMON NAME - DATA SORUCES. THIS IS USED FOR COMMON NAME 
	private void updateHashCommonNamesDataSources(TaxonomyRow row) {
	
		addDataSourceIfNotExists(row.getDataProviderName());
		updateHashCommonNamesDataSources(row.getDataProviderName(), row.getCommonNames());
		
	}
	
	
	//UPDATE A HASH MAP WITH ALL COMMON NAME - DATA SORUCES. THIS IS USED FOR COMMON NAMES 
	private void updateHashCommonNamesDataSources(ResultRow row) {
		
		addDataSourceIfNotExists(row.getDataSourceName());
		updateHashCommonNamesDataSources(row.getDataSourceName(), row.getCommonNames());
	}
	
	private void addDataSourceIfNotExists(String dataSourceName){
		
		if(listDataSourcesFound.indexOf(dataSourceName)==-1)
			listDataSourcesFound.add(dataSourceName);
		
	}
	
	
	//UPDATE A HASH MAP WITH ALL COMMON NAME - DATA SORUCES. THIS IS USED FOR COMMON NAME 
	private void updateHashCommonNamesDataSources(String dataProviderName, List<CommonName> listCommonName) {
		
		if(listCommonName!=null){
			
			//CREATE HASH CONTAINS COMMON NAME
			for (CommonName comName : listCommonName) {
				
					String commonNameKey = comName.getName() + " (" +comName.getLanguage() + ")";
//					System.out.println("common name: "+comName.getName());
//					System.out.println("common name key: "+commonNameKey);

					ArrayList<String> dataSources = hashMapCommonNameDataSources.get(NormalizeString.lowerCaseUpFirstChar(commonNameKey));
					
					if(dataSources==null){
						
						dataSources=new ArrayList<String>();
						
						dataSources.add(dataProviderName);

						hashMapCommonNameDataSources.put(NormalizeString.lowerCaseUpFirstChar(commonNameKey), dataSources);
						
					}
					else{
						
						//DEBUG
//						System.out.println("dataSources.contains : "+dataSources.contains(dataProviderName));
//						System.out.println("row.getDataProviderName(): "+dataProviderName);
						
						if(!dataSources.contains(dataProviderName)){
						
							dataSources.add(dataProviderName);
							hashMapCommonNameDataSources.put(NormalizeString.lowerCaseUpFirstChar(commonNameKey), dataSources);
							
							//DEBUG
//							System.out.println("put "+ commonNameKey +" in datasource list: "+dataProviderName);
						}	
					}
			}
		}
		
	}

	public HashMap<String, ArrayList<String>> getHashMapCommonNameDataSources() {
		return hashMapCommonNameDataSources;
	}

	public ArrayList<String> getListDataSourcesFound() {
		return listDataSourcesFound;
	}

}
