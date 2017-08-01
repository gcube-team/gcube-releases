package org.gcube.portlets.user.speciesdiscovery.client.gridview;

import com.google.gwt.event.shared.EventBus;

public class GisLayerJobGridManager {

	private String searchTerm;
	private EventBus eventBus;
	private long expectedOccurrences;
	private boolean isSearchByCommonName;
	private static final String SCIENTIFIC_NAME = "(scientific name)";
	private static final String COMMON_NAME = "(common name)";

	public GisLayerJobGridManager(EventBus eventBus, String searchTerm, boolean isSearchByCommonName, long expectedOccurrences) {
		this.eventBus = eventBus;
		this.expectedOccurrences = expectedOccurrences;
		this.searchTerm = searchTerm;
		this.isSearchByCommonName = isSearchByCommonName;
	}

//	public void saveOccurrence(){
//
//		SpeciesDiscovery.taxonomySearchService.getCountOfOccurrencesBatch(new AsyncCallback<OccurrencesStatus>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Log.error("Error on loading", "An error occurred on count of occurrence point, retry." +caught.getMessage());
//
//			}
//
//			@Override
//			public void onSuccess(final OccurrencesStatus result) {
//				MessageDialog dialog;
//				if(expectedOccurrences>result.getSize()){
//
//					dialog = new MessageDialog("Info", "Loading in progress", "On server are available only "+result.getSize()+" of "+expectedOccurrences+" occurrences points. Do you want continue?");
//					dialog.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {
//
//						public void handleEvent(MessageBoxEvent be) {
//							//IF NOT CANCELLED
//							String clickedButton = be.getButtonClicked().getItemId();
//							if(clickedButton.equals(Dialog.YES))
//								createGisLayerJobEventCallback(result.getSize());
//							}
//				        });
//				}
//
//				else
//					createGisLayerJobEventCallback(result.getSize());
//		}
//		});
//
//	}

//	private void createGisLayerJobEventCallback(int totalPoints) {
//
//		String jobName = getSearchTermBySearchType(isSearchByCommonName, searchTerm);
//		eventBus.fireEvent(new CreateGisLayerJobEvent(jobName, "Gis Layer generated from SPD Portlet by gCube Framework", totalPoints));
//	}

//	public static String getSearchTermBySearchType(boolean isSearchByCommonName, String searchTerm){
//
//		if(isSearchByCommonName)
//			return NormalizeString.lowerCaseUpFirstChar(searchTerm) + " " + COMMON_NAME;
//		else
//			return NormalizeString.lowerCaseUpFirstChar(searchTerm) + " " + SCIENTIFIC_NAME;
//	}



}
