package org.gcube.portlets.user.speciesdiscovery.client.gridview;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateOccurrenceJobEvent;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSource;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OccurrenceJobGridManager {
	
	private int expectedPoints;
	private List<String> listDataSource;
	private SaveFileFormat fileFormat;
	private OccurrencesSaveEnum saveType;
	private String searchTerm;
	private boolean isByDataSource;
	private EventBus eventBus;
	private boolean isSearchByCommonName;

	private static int EMPTY = -1;
	private static final String SCIENTIFIC_NAME = "(scientific name)";
	private static final String COMMON_NAME = "(common name)";
	
	public OccurrenceJobGridManager(EventBus eventBus, SaveFileFormat fileFormat, OccurrencesSaveEnum save, String searchTerm, boolean isByDataSource, boolean isSearchByCommonName) {
		this.expectedPoints = EMPTY;
		this.listDataSource = null;
		
		this.eventBus = eventBus;
		
		this.fileFormat = fileFormat;
		this.saveType = save;
		this.searchTerm = searchTerm;
		this.isByDataSource = isByDataSource;
		
		this.isSearchByCommonName = isSearchByCommonName;

	}
	
	public void saveOccurrence(){
		
		SpeciesDiscovery.taxonomySearchService.loadDataSourceForResultRow(true, true, new AsyncCallback<List<DataSource>>() {
			
			@Override
			public void onSuccess(List<DataSource> result) {
				
				listDataSource = new ArrayList<String>();
				
				if(result!=null){
					for (DataSource dataSource : result)
						listDataSource.add(dataSource.getName());
				}
				else
					Info.display("Error", "Error getting data source list, retry");	

				createOccurrenceJobEventCallback();	
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error getting data source list", "Error getting data source list, retry");	
				Log.error("An error occured in loadDataSourceForResultRow "+caught);
				
			}
		});
		
		SpeciesDiscovery.taxonomySearchService.retrieveOccurencesFromSelection(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer points) {
				Log.trace("Expected points: "+expectedPoints);
				
				expectedPoints = points.intValue();
				
				if(expectedPoints>0)
					createOccurrenceJobEventCallback();
				else
					Info.display("Info", "There are no occurrence points to save");	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error getting occurrences", "Error getting occurrences, retry");	
				Log.trace("Error getting occurrences", caught);
			}
		});
		
		
	}

	private void createOccurrenceJobEventCallback() {
		
		if(listDataSource==null || expectedPoints==EMPTY) //control if both async call is completed
			return;
		
		String jobName = getSearchTermBySearchType(isSearchByCommonName, searchTerm);
		
		eventBus.fireEvent(new CreateOccurrenceJobEvent(fileFormat, expectedPoints, saveType, listDataSource, jobName, isByDataSource));

	}
	
	public static String getSearchTermBySearchType(boolean isSearchByCommonName, String searchTerm){
		
		if(isSearchByCommonName)
			return NormalizeString.lowerCaseUpFirstChar(searchTerm) + " " + COMMON_NAME;
		else
			return NormalizeString.lowerCaseUpFirstChar(searchTerm) + " " + SCIENTIFIC_NAME;
	}
	
	

}
