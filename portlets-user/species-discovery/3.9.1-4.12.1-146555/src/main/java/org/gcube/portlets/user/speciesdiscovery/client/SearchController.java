/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.user.speciesdiscovery.client.advancedsearch.AdvancedSearchPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveFilterOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveFilterOnResultEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveMaskLoadingGrid;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveMaskLoadingGridHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.CapabilitySelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CapabilitySelectedEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ChangeFilterClassificationOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ChangeFilterClassificationOnResultEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.CompletedLoadDataSourceEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CompletedLoadDataSourceEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateGisLayerJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateGisLayerJobEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateOccurrenceJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateOccurrenceJobEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateTaxonomyJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateTaxonomyJobEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.DisableFilterEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.DisableFilterEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.LoadDataSourceEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.LoadDataSourceEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReLoadListJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReLoadListJobEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReSubmitJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReSubmitJobEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveItemsEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveItemsEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobErrorEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobErrorEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchStartedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchTypeSelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchTypeSelectedEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SetCommonNamesEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SetCommonNamesEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOccurrencesEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOccurrencesEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOnlySelectedRowEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOnlySelectedRowEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.StopCurrentSearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.StopCurrentSearchEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.StreamCompletedEventEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.StreamEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.StreamEvent.Event;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateAllRowSelectionEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateAllRowSelectionEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateFilterOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateFilterOnResultEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateRowSelectionEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateRowSelectionEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.ViewDetailsOfSelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ViewDetailsOfSelectedEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.filterresult.ResultFilterPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.job.gislayer.GisLayerJobSpeciesPanel;
import org.gcube.portlets.user.speciesdiscovery.client.job.occurrence.OccurrenceJobSpeciesPanel;
import org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy.TaxonomyJobSpeciesPanel;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.DataSource;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.DataSourceManager;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.client.window.MessageBoxAlert;
import org.gcube.portlets.user.speciesdiscovery.client.window.MessageDialog;
import org.gcube.portlets.user.speciesdiscovery.client.window.MessageForm;
import org.gcube.portlets.user.speciesdiscovery.client.windowdetail.ViewDetailsWindow;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.Coordinate;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchByQueryParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SessionExpired;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;
import org.gcube.portlets.user.speciesdiscovery.shared.util.SearchTermValidator;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it" - "Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it"
 *
 */
public class SearchController {

	protected static final int PAGE_SIZE = 25;

	public static EventBus eventBus;
	protected StreamPagingLoader streamPagingLoader;
	protected boolean showOnlySelected = false;
	protected boolean isActiveFilterOnResult = false;
	protected int currentPage = 1;
	protected SearchController instance = null;

	private SearchEvent lastSearchEvent = null;

	private SearchBorderLayoutPanel searchBorderLayoutPanel;

	private DataSourceManager dataSourceManager;

	/**
	 * @param eventBus
	 * @param searchBorderLayoutPanel
	 */
	public SearchController(EventBus eventBus, SearchBorderLayoutPanel searchBorderLayoutPanel) {
		SearchController.eventBus = eventBus; //TODO Temporary solution
		this.searchBorderLayoutPanel = searchBorderLayoutPanel;
		this.dataSourceManager = DataSourceManager.getInstance();
		this.streamPagingLoader = new StreamPagingLoader(PAGE_SIZE);
//		initialize();
		bind();
		instance = this;
	}

	protected void switchDataSource(SpeciesCapability resultType){

		setFiltersCriteria();

		DataSource currentDataSource = dataSourceManager.getDataSourceByResultType(resultType);

		Log.trace("current data source: " + currentDataSource.getInfo());

		//SET DATASOURCE
		this.streamPagingLoader.setDataSource(currentDataSource);

	}

	private void setFiltersCriteria(){
		dataSourceManager.setActiveFilters(isActiveFilterOnResult);
		dataSourceManager.setOnlySelected(showOnlySelected);
	}


	/**
	 * @return the streamPagingLoader
	 */
	public StreamPagingLoader getStreamPagingLoader() {
		return streamPagingLoader;
	}

	protected void bind()
	{

		eventBus.addHandler(UpdateFilterOnResultEvent.TYPE, new UpdateFilterOnResultEventHandler() {

			@Override
			public void onUpdateFilter(UpdateFilterOnResultEvent updateFilterOnResultEvent) {

				ResultFilterPanelManager.getInstance().updateDataSourceFilterById(updateFilterOnResultEvent.getUpdateFilterId());
			}
		});

		eventBus.addHandler(StreamEvent.TYPE, new StreamCompletedEventEventHandler() {

			@Override
			public void onStreamCompleteEvent(StreamEvent streamEvent) {

				//ACTIVE SELECT ALL ROW
				if(lastSearchEvent.getResultType().equals(SpeciesCapability.RESULTITEM))
					searchBorderLayoutPanel.getSpeciesCenterPanel().getResultRowPanel().activeCheckAllRows(streamEvent.isActiveFilter());
				else if(lastSearchEvent.getResultType().equals(SpeciesCapability.TAXONOMYITEM))
					searchBorderLayoutPanel.getSpeciesCenterPanel().getTaxonomyRowPanel().activeCheckAllRows(streamEvent.isActiveFilter());



				if(streamEvent.getEvent().equals(Event.COMPLETED))
					searchBorderLayoutPanel.getSpeciesNorthPanel().visibleButtonStopSearch(false);

			}
		});


		eventBus.addHandler(UpdateAllRowSelectionEvent.TYPE, new UpdateAllRowSelectionEventHandler() {

			@Override
			public void onUpdateAllRowSelection(final UpdateAllRowSelectionEvent updateAllRowSelectionEvent) {

				SpeciesDiscovery.taxonomySearchService.updateRowSelections(updateAllRowSelectionEvent.getSelectionValue(), streamPagingLoader.getActiveFilterObject(), new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error on selected all rows", "An error occurred on selected all rows " + caught);
						Log.error("Error on selected all rows", "An error occurred on selected all rows " + caught);

					}

					@Override
					public void onSuccess(Integer result) {

						if(result.intValue()>0){

							boolean selected = updateAllRowSelectionEvent.getSelectionValue();

							String msgRow = result.intValue()==1?"row was":"rows were";

							String msgSel = selected==true?"selected":"deselected";

							Info.display("Info", result.intValue()+" "+msgRow+" "+msgSel);

							if(updateAllRowSelectionEvent.getSearchType().equals(SearchResultType.SPECIES_PRODUCT))
								searchBorderLayoutPanel.getSpeciesCenterPanel().getResultRowPanel().selectAllRows(selected);
							else if(updateAllRowSelectionEvent.getSearchType().equals(SearchResultType.TAXONOMY_ITEM))
								searchBorderLayoutPanel.getSpeciesCenterPanel().getTaxonomyRowPanel().selectAllRows(selected);
						}
					}
				});
			}
		});

		eventBus.addHandler(ReSubmitJobEvent.TYPE, new ReSubmitJobEventHandler() {

			@Override
			public void onResubmitJob(ReSubmitJobEvent reSubmitJobEvent) {
				if(reSubmitJobEvent.getLoadType().equals(SearchResultType.OCCURRENCE_POINT)){
					resubmitJob(reSubmitJobEvent.getLoadType(), reSubmitJobEvent.getJobOccurrenceModel().getJobIdentifier());
				}
				else if(reSubmitJobEvent.getLoadType().equals(SearchResultType.TAXONOMY_ITEM)){
					resubmitJob(reSubmitJobEvent.getLoadType(), reSubmitJobEvent.getJobTaxonomyModel().getIdentifier());
				}else if(reSubmitJobEvent.getLoadType().equals(SearchResultType.GIS_LAYER_POINT)){
					resubmitJob(reSubmitJobEvent.getLoadType(), reSubmitJobEvent.getJobGisModel().getJobIdentifier());
				}
			}
		});

		eventBus.addHandler(ReLoadListJobEvent.TYPE, new ReLoadListJobEventHandler() {

			@Override
			public void onLoadJobList(ReLoadListJobEvent loadJobListEvent) {

				if(loadJobListEvent.getLoadType()!=null)
					excecuteGetJobs(loadJobListEvent.getLoadType(), true);
			}
		});

		eventBus.addHandler(SearchTypeSelectedEvent.TYPE, new SearchTypeSelectedEventHandler() {

			@Override
			public void onSearchTypeSelected(SearchTypeSelectedEvent searchTypeSelectedEvent) {

				if(searchTypeSelectedEvent.getType()!=null)
					AdvancedSearchPanelManager.getInstance().disableFilterForSearchType(searchTypeSelectedEvent.getType());

			}
		});

		eventBus.addHandler(CreateGisLayerJobEvent.TYPE, new CreateGisLayerJobEventHandler() {

			@Override
			public void onCreateGisLayerJob(final CreateGisLayerJobEvent createGisLayerJobEvent) {

				SpeciesDiscovery.taxonomySearchService.retrieveOccurencesFromSelection(new AsyncCallback<Integer>() {

					@Override
					public void onSuccess(Integer returnedPoints) {
						Long expectedPoints = createGisLayerJobEvent.getTotalPoints();
						Log.trace("Expected points: "+expectedPoints);
						if(returnedPoints==null || returnedPoints.intValue()==0){
							Info.display("Info", "There are no occurrence points selected to create a gis layer");
							return;
						}
						Log.trace("Returned points: "+expectedPoints);

						SpeciesDiscovery.taxonomySearchService.createGisLayerJobFromSelectedOccurrenceKeys(createGisLayerJobEvent.getJobName(), createGisLayerJobEvent.getJobDescription(), createGisLayerJobEvent.getTotalPoints(), new AsyncCallback<JobGisLayerModel>() {

							@Override
							public void onFailure(Throwable caught) {

								Info.display("Error generating the map", "An error occurred generating the map, retry");
								Log.error("Error on loading", "An error occurred on edit listner, retry." +caught.getMessage());

							}

							@Override
							public void onSuccess(JobGisLayerModel gisJob) {
								GWT.log("Returned gis job: "+gisJob);
								Info.display("Gis Layer Occurrence Job", "Generating Gis layer: "+gisJob.getJobName()+" submitted");
								excecuteGetJobs(SearchResultType.GIS_LAYER_POINT, false);
								searchBorderLayoutPanel.getSpeciesSouthPanel().setIconGisLayerByCounter(1);
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error getting occurrences", "Error getting occurrences, retry");
						Log.trace("Error getting occurrences", caught);
					}
				});
			}
		});


		eventBus.addHandler(SearchEvent.TYPE, new SearchEventHandler() {

			@Override
			public void onSearch(SearchEvent event) {
				GWT.log(event.toString());
				Log.trace(event.toString());
				lastSearchEvent = event;
				doActiveMaskLoadingGridAndButtonSearch(true);
				searchBorderLayoutPanel.getSpeciesNorthPanel().visibleButtonStopSearch(true);
				if(event.getType().equals(SearchType.BY_COMMON_NAME) || event.getType().equals(SearchType.BY_SCIENTIFIC_NAME)){
					search(event.getType(), event.getSearchTerm().trim(), event.getUpperBoundLongitude(), event.getUpperBoundLatitude(), event.getLowerBoundLongitude(), event.getLowerBoundLatitude(), event.getFromDate(), event.getToDate(), event.getLstDataSources(), event.getGroupByRank(), event.getResultType(), event.getListDataSourcesForSynonyms(), event.getListDataSourcesForUnfold());
				}
				else{
					searchByQuery(event.getQuery());
				}

			}
		});


		eventBus.addHandler(StopCurrentSearchEvent.TYPE, new StopCurrentSearchEventHandler() {

			@Override
			public void onAbortCurrentSearch(StopCurrentSearchEvent abortCurrentSearchEvent) {

				SpeciesDiscovery.taxonomySearchService.userStopSearch(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {

						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Void result) {

						// TODO Auto-generated method stub

					}
				});
			}
		});

		eventBus.addHandler(CreateOccurrenceJobEvent.TYPE, new CreateOccurrenceJobEventHandler() {

			@Override
			public void onCreateSpeciesJob(CreateOccurrenceJobEvent createOccurrenceJobEvent) {

				List<JobOccurrencesModel> listJobOccurrenceModel = new ArrayList<JobOccurrencesModel>();

				if(createOccurrenceJobEvent.getListDataSourceFound()==null){
					Info.display("Error on loading", "An error occurred on recover data sources, please close window and try again.");
				}

				if(!createOccurrenceJobEvent.isByDataSource()){ //ONE JOB FOR ALL DATASOURCE

					String jobName = createOccurrenceJobEvent.getSearchTerm() + " occurrences";
					List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> dataSourceList = new ArrayList<org.gcube.portlets.user.speciesdiscovery.shared.DataSource>();

					for (String dataSource : createOccurrenceJobEvent.getListDataSourceFound()) {
						dataSourceList.add(new org.gcube.portlets.user.speciesdiscovery.shared.DataSource(dataSource,dataSource));
					}

					listJobOccurrenceModel.add(new JobOccurrencesModel("", jobName,createOccurrenceJobEvent.getSearchTerm(), dataSourceList, createOccurrenceJobEvent.getFileFormat(), createOccurrenceJobEvent.getSaveEnum(), createOccurrenceJobEvent.isByDataSource(), createOccurrenceJobEvent.getExpectedOccurrences()));

				}else{ //IS BY DATASOURCE - ONE JOB FOR EACH DATASOURCE

					for (String dataSource : createOccurrenceJobEvent.getListDataSourceFound()) {
//						System.out.println("########DATASOURCE FOUND: "+dataSource);
//						String jobName = createOccurrenceJobEvent.getSearchTerm() + " occurrences from " +dataSource;
						String jobName = createOccurrenceJobEvent.getSearchTerm() + " occurrences";
						List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> dataSourceList = new ArrayList<org.gcube.portlets.user.speciesdiscovery.shared.DataSource>();
						dataSourceList.add(new org.gcube.portlets.user.speciesdiscovery.shared.DataSource(dataSource,dataSource));
						listJobOccurrenceModel.add(new JobOccurrencesModel("", jobName,createOccurrenceJobEvent.getSearchTerm(), dataSourceList, createOccurrenceJobEvent.getFileFormat(), createOccurrenceJobEvent.getSaveEnum(), createOccurrenceJobEvent.isByDataSource()));
					}
				}

				SpeciesDiscovery.taxonomySearchService.createOccurrencesJob(listJobOccurrenceModel, createOccurrenceJobEvent.getFileFormat(), createOccurrenceJobEvent.getSaveEnum(), createOccurrenceJobEvent.isByDataSource(), createOccurrenceJobEvent.getExpectedOccurrences(), new AsyncCallback<List<JobOccurrencesModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error", "Sorry, An error occurred on create job. Please try again later");
						Log.error("Error on loading", "An error occurred on create job, retry." +caught.getMessage());

					}

					@Override
					public void onSuccess(List<JobOccurrencesModel> result) {

						if(result!=null){
							int jobs = result.size();
							if(jobs>0){
								String msg = jobs==1? "was":"were";
								Info.display("Species Occurrence Job", result.size() + " occurrence job "+msg+" submitted");
								excecuteGetJobs(SearchResultType.OCCURRENCE_POINT, false);
								searchBorderLayoutPanel.getSpeciesSouthPanel().setIconOccurrenceByCounter(result.size());
							}
							else{
								Info.display("Species Occurrence Job","An error occurred on submit job, retry");
							}

						}
					}
				});

			}
		});


		eventBus.addHandler(ActiveMaskLoadingGrid.TYPE, new ActiveMaskLoadingGridHandler() {

			@Override
			public void onActiveMaskLoadingGrid(ActiveMaskLoadingGrid activeLoadingGrid) {

				doActiveMaskLoadingGridAndButtonSearch(activeLoadingGrid.isActive());

			}
		});

		eventBus.addHandler(SaveJobEvent.TYPE, new SaveJobEventHandler() {

			@Override
			public void onSaveJob(SaveJobEvent saveJobEvent) {

				if(saveJobEvent.getItemType().equals(SearchResultType.TAXONOMY_ITEM)){
					saveTaxonomyJob(saveJobEvent.getJobTaxonomyModel(), saveJobEvent.getScientificName(), saveJobEvent.getListDataSources(), saveJobEvent.getRank());
				}
				else if(saveJobEvent.getItemType().equals(SearchResultType.OCCURRENCE_POINT)){
					saveOccurencesJob(saveJobEvent.getJobOccurrenceModel(), saveJobEvent.getScientificName(), saveJobEvent.getListDataSources());
				}else if(saveJobEvent.getItemType().equals(SearchResultType.GIS_LAYER_POINT)){
					saveGisLayerJob(saveJobEvent.getJobGisLayer());
				}
			}

		});

		eventBus.addHandler(SaveJobErrorEvent.TYPE, new SaveJobErrorEventHandler() {

			@Override
			public void onSaveJobError(SaveJobErrorEvent saveJobErrorEvent) {

				if(saveJobErrorEvent.getItemType().equals(SearchResultType.TAXONOMY_ITEM)){
					saveTaxonomyJobError(saveJobErrorEvent.getJobTaxonomyModel(), saveJobErrorEvent.getScientificName(), saveJobErrorEvent.getListDataSources(), saveJobErrorEvent.getRank());
				}
				else if(saveJobErrorEvent.getItemType().equals(SearchResultType.OCCURRENCE_POINT)){
					saveOccurencesJobError(saveJobErrorEvent.getJobOccurrenceModel(), saveJobErrorEvent.getScientificName(), saveJobErrorEvent.getListDataSources());
				}
			}

		});

		eventBus.addHandler(CreateTaxonomyJobEvent.TYPE, new CreateTaxonomyJobEventHandler() {

			@Override
			public void onCreateSpeciesJob(CreateTaxonomyJobEvent createSpeciesJobEvent) {

				switch (createSpeciesJobEvent.getJobType()) {

					case BYCHILDREN:

						LightTaxonomyRow taxonomy =  createSpeciesJobEvent.getTaxonomy();

						SpeciesDiscovery.taxonomySearchService.createTaxonomyJobByChildren(taxonomy.getServiceId(), taxonomy.getName(), taxonomy.getRank(), createSpeciesJobEvent.getDataSourceName(), new AsyncCallback<JobTaxonomyModel>() {

							@Override
							public void onFailure(Throwable caught) {
								Info.display("Error", "Sorry, An error occurred on create job. Please try again later");
								Log.error("Error on loading", "An error occurred on create job by children, retry." +caught.getMessage());
							}

							@Override
							public void onSuccess(JobTaxonomyModel result) {

								if(result!=null){
									Info.display("Species Taxonomy Job","A new taxonomy job was submitted");
									excecuteGetJobs(SearchResultType.TAXONOMY_ITEM, false);
									searchBorderLayoutPanel.getSpeciesSouthPanel().setIconTaxonomyByCounter(1);
								}
							}
						});


						break;

					case BYIDS:

						SpeciesDiscovery.taxonomySearchService.createTaxonomyJobByIds(lastSearchEvent.getSearchTerm(), lastSearchEvent.getLstDataSources(), new AsyncCallback<JobTaxonomyModel>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.error("Error on loading", "An error occurred on create job by ids, retry." +caught.getMessage());

							}

							@Override
							public void onSuccess(JobTaxonomyModel result) {

								if(result!=null){
									Info.display("Species Taxonomy Job","A new taxonomy job was submitted");
									excecuteGetJobs(SearchResultType.TAXONOMY_ITEM, false);
									searchBorderLayoutPanel.getSpeciesSouthPanel().setIconTaxonomyByCounter(1);
								}
							}

							});

						break;

				default:
					break;
				}
			}
		});

		eventBus.addHandler(CompletedLoadDataSourceEvent.TYPE, new CompletedLoadDataSourceEventHandler() {

			@Override
			public void onCompletedLoadDataSource(CompletedLoadDataSourceEvent completedLoadDataSourceEvent) {

				searchBorderLayoutPanel.getSpeciesNorthPanel().unmask();
				eventBus.fireEvent(new CapabilitySelectedEvent(searchBorderLayoutPanel.getSpeciesNorthPanel().getSelectedCapability()));

			}
		});

		eventBus.addHandler(ChangeFilterClassificationOnResultEvent.TYPE, new ChangeFilterClassificationOnResultEventHandler() {

			@Override
			public void onChangeFilter(ChangeFilterClassificationOnResultEvent changeFilterClassificationOnResultEvent) {
				ResultFilterPanelManager.getInstance().updateFilterCounterForClassification();

			}
		});

		eventBus.addHandler(CapabilitySelectedEvent.TYPE, new CapabilitySelectedEventHandler() {

			@Override
			public void onCapabilitySelected(CapabilitySelectedEvent capabilitySelectedEvent) {

				AdvancedSearchPanelManager.getInstance().setCurrentCapability(capabilitySelectedEvent.getCapability());

				switch (capabilitySelectedEvent.getCapability()) {
					case RESULTITEM:

						searchBorderLayoutPanel.getSpeciesNorthPanel().setValueCheckValidateOcccurrences(true);
						searchBorderLayoutPanel.getSpeciesNorthPanel().setVisibleCheckValidateOcccurrences(true);
						break;

					default:
						searchBorderLayoutPanel.getSpeciesNorthPanel().setValueCheckValidateOcccurrences(false);
						searchBorderLayoutPanel.getSpeciesNorthPanel().setVisibleCheckValidateOcccurrences(false);
					};

//				//TODO CHANGE
//				searchBorderLayoutPanel.getSpeciesCenterPanel().setCurrentView(capabilitySelectedEvent.getCapability());


			}
		});

		eventBus.addHandler(UpdateRowSelectionEvent.TYPE, new UpdateRowSelectionEventHandler() {

			@Override
			public void onUpdateRowSelection(final UpdateRowSelectionEvent updateRowSelectionEvent) {

				Log.trace("in update..... rowid "+ updateRowSelectionEvent.getRowId());

				SpeciesDiscovery.taxonomySearchService.updateRowSelection(updateRowSelectionEvent.getRowId(), updateRowSelectionEvent.getSelectionValue(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						Log.trace("rowid "+ updateRowSelectionEvent.getRowId() +" updated");

					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error on loading", "An error occurred on check row, please retry.");
						Log.error("Error on loading", "An error occurred on check row, please retry." +caught.getMessage());
					}
				});
			}
		});

		eventBus.addHandler(ShowOccurrencesEvent.TYPE, new ShowOccurrencesEventHandler() {

			@Override
			public void onShowOccurrences(ShowOccurrencesEvent event) {
				openOccurenceWindow();
			}
		});

		eventBus.addHandler(SaveItemsEvent.TYPE, new SaveItemsEventHandler() {

			@Override
			public void onSaveOccurrences(final SaveItemsEvent event) {

				if(event.getItemType().equals(SearchResultType.OCCURRENCE_POINT)){

					//OLD CODE***********
					SpeciesDiscovery.taxonomySearchService.getCountOfOccurrencesBatch(new AsyncCallback<OccurrencesStatus>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.error("Error on loading", "An error occurred on count of occurrence point, retry." +caught.getMessage());

						}

						@Override
						public void onSuccess(OccurrencesStatus result) {
							MessageDialog dialog;
							if(event.getExpectedPoints()>result.getSize()){
								dialog = new MessageDialog("Info", "Loading in progress", "On server are available only "+result.getSize()+" of "+event.getExpectedPoints()+" occurrence points. Do you want continue?");
								dialog.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

									public void handleEvent(MessageBoxEvent be) {
										//IF NOT CANCELLED
										String clickedButton = be.getButtonClicked().getItemId();
										if(clickedButton.equals(Dialog.YES))
											saveOccurences(event.getFileFormat(), event.getCsvType());
										}
							        });
							}

							else
								saveOccurences(event.getFileFormat(), event.getCsvType());
						}
					});

					//END OLD CODE***********
					saveOccurences(event.getFileFormat(), event.getCsvType());

				}

				else
					if(event.getItemType().equals(SearchResultType.TAXONOMY_ITEM))
						saveTaxonomy(event.getFileFormat());


			}
		});


		eventBus.addHandler(ShowOnlySelectedRowEvent.TYPE, new ShowOnlySelectedRowEventHandler() {

			@Override
			public void onShowOnlySelectedRow(ShowOnlySelectedRowEvent event) {

				showFilterResults(event.isOnlySelected());
			}
		});


		eventBus.addHandler(LoadDataSourceEvent.TYPE, new LoadDataSourceEventHandler() {

			@Override
			public void onLoadDataSource(LoadDataSourceEvent loadDataSourceEvent) {

				searchBorderLayoutPanel.getSpeciesNorthPanel().mask("Loading data sources...", ConstantsSpeciesDiscovery.LOADINGSTYLE);

				loadDataSourceFromService();

			}
		});

		eventBus.addHandler(ViewDetailsOfSelectedEvent.TYPE, new ViewDetailsOfSelectedEventHandler() {

			@Override
			public void onViewDetails(ViewDetailsOfSelectedEvent viewDetailsOfSelectedEvent) {

//				final ViewDetailsWindow view = new ViewDetailsWindow(instance, 	lastSearchEvent.getResultType(), lastSearchEvent.getType());

				final ViewDetailsWindow view = new ViewDetailsWindow(lastSearchEvent);

				//IF SELECTED CAPABILITY IS OCCURENCES - IS CALL LOAD OCCURENCES METHOD
				if(lastSearchEvent.getResultType().getName().compareTo(SpeciesCapability.RESULTITEM.getName())==0){

					Scheduler.get().scheduleDeferred(new ScheduledCommand() {

						@Override
						public void execute() {
							view.loadOccurences();
						}
					});
				}
			}
		});

		eventBus.addHandler(ActiveFilterOnResultEvent.TYPE, new ActiveFilterOnResultEventHandler() {

			@Override
			public void onActiveFilter(ActiveFilterOnResultEvent activeFilterOnResultEvent) {

//				for(Integer id: activeFilterOnResultEvent.getActiveFilterObject().getListByClassification()){
//					Log.trace("current id "+ id   + " size " +activeFilterOnResultEvent.getActiveFilterObject().getListByClassification().size());
//				}
				doActiveMaskLoadingGridAndButtonSearch(true);

				activeFilterOnResult(true);
				setOnlySelected(false);
				updateOnlySelectedOnManager(false);

				ResultFilter filterObj = activeFilterOnResultEvent.getActiveFilterObject();
				streamPagingLoader.setActiveFilterObject(filterObj);
				streamPagingLoader.reloadPageForFiltering(filterObj.getNumberOfData(), true);

				searchBorderLayoutPanel.getSpeciesCenterPanel().setFilterActive(true, filterObj.getFilterValue());

			}
		});


		eventBus.addHandler(SetCommonNamesEvent.TYPE, new SetCommonNamesEventHandler() {

			@Override
			public void onSetCommonNames(SetCommonNamesEvent setCommonNamesEvent) {

				final BaseModelData data = setCommonNamesEvent.getBaseModelData();

				final ResultRow row = (ResultRow) data.get(SpeciesGridFields.ROW.getId());

				SpeciesDiscovery.taxonomySearchService.loadListCommonNameByRowId(""+row.getId(), new AsyncCallback<ArrayList<CommonName>>() {

					@Override
					public void onFailure(Throwable caught) {
//						Window.alert(caught.getMessage());
						Info.display("Error on loading", "An error occurred on loading Common Names, retry.");
						Log.error("Error in SetCommonNamesEvent: "+caught.getMessage());

					}

					@Override
					public void onSuccess(ArrayList<CommonName>result) {

						if(result!=null){

							String matchingTaxonName = data.get(SpeciesGridFields.MATCHING_NAME.getId());
							String matchingAccordionTo =  data.get(SpeciesGridFields.MATCHING_AUTHOR.getId());
							String matchingCredits = data.get(SpeciesGridFields.MATCHING_CREDITS.getId());

							String commonNames = getCommonNamesHTML(matchingTaxonName, matchingAccordionTo, matchingCredits, result);
							data.set(SpeciesGridFields.COMMON_NAMES.getId(), commonNames);
						}

						row.setCommonNames(result);
					}
				});

			}
		});

		eventBus.addHandler(DisableFilterEvent.TYPE, new DisableFilterEventHandler() {

			@Override
			public void onDisableFilter(DisableFilterEvent disableFilterEvent) {

				doActiveMaskLoadingGridAndButtonSearch(true);
				streamPagingLoader.reloadPageWithoutFiltering();
				disableFilters();
//				updateOnlySelectedOnManager();
			}
		});

	}

	public void resubmitJob(SearchResultType resultType, String jobIdentifier){

		if(jobIdentifier!=null){
			if(resultType.equals(SearchResultType.OCCURRENCE_POINT)){

				SpeciesDiscovery.taxonomySearchService.resubmitOccurrencesJob(jobIdentifier, new AsyncCallback<List<JobOccurrencesModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(List<JobOccurrencesModel> result) {

						if(result!=null && result.size()>0)
							excecuteGetJobs(SearchResultType.OCCURRENCE_POINT, false);

					}
				});
			}else if(resultType.equals(SearchResultType.TAXONOMY_ITEM)){

				SpeciesDiscovery.taxonomySearchService.resubmitTaxonomyJob(jobIdentifier, new AsyncCallback<JobTaxonomyModel>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error on resubmit", "An error occurred on resubmit job, retry.");
						Log.error("Error on resubmit", "An error occurred on resubmit job, retry." +caught.getMessage());
						caught.printStackTrace();

					}

					@Override
					public void onSuccess(JobTaxonomyModel result) {

						if(result!=null)
							excecuteGetJobs(SearchResultType.TAXONOMY_ITEM, false);

					}
				});

			}else if(resultType.equals(SearchResultType.GIS_LAYER_POINT)){

				SpeciesDiscovery.taxonomySearchService.resubmitGisLayerJob(jobIdentifier, new AsyncCallback<JobGisLayerModel>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error on resubmit", "An error occurred on resubmit job, retry.");
						Log.error("Error on resubmit", "An error occurred on resubmit job, retry." +caught.getMessage());
						caught.printStackTrace();

					}

					@Override
					public void onSuccess(JobGisLayerModel result) {

						if(result!=null)
							excecuteGetJobs(SearchResultType.GIS_LAYER_POINT, false);

					}
				});

			}
		}
		else
			Info.display("Error", "job identifier is wrong");

	}

	protected void activeFilterOnResult(boolean bool){
		isActiveFilterOnResult = bool;
		dataSourceManager.setActiveFilters(isActiveFilterOnResult);
	}

	protected void disableFilters(){

		activeFilterOnResult(false);
		streamPagingLoader.resetFilters();
		searchBorderLayoutPanel.getSpeciesCenterPanel().setFilterActive(false, "");
	}


	protected void loadDataSourceFromService() {

		SpeciesDiscovery.taxonomySearchService.loadDataSourceList(new AsyncCallback<List<DataSourceModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Error", caught.getMessage() + ". Please, try your request again later.", null);
				GWT.log("loadDataSourceFromService error: "+caught.getMessage());
				searchBorderLayoutPanel.getSpeciesNorthPanel().unmask();
				searchBorderLayoutPanel.getSpeciesNorthPanel().activeButtonSearch(false);
				searchBorderLayoutPanel.getSpeciesNorthPanel().visibleButtonStopSearch(false);
			}

			@Override
			public void onSuccess(List<DataSourceModel> result) {
				GWT.log("LOADED "+result.size()+" plugin's");

				if(result!=null && result.size()>0)
					AdvancedSearchPanelManager.getInstance().loadDataSource(result);
				else
					Info.display("Error", "No data sources are loaded, retry");
//				ResultFilterPanelManager.getInstance().loadDataSource(result);

				eventBus.fireEvent(new CompletedLoadDataSourceEvent());
			}

		});
	}

	protected void doActiveMaskLoadingGridAndButtonSearch(boolean mask) {

		searchBorderLayoutPanel.getSpeciesCenterPanel().setMaskGridPanel(mask);

		if(mask)
			searchBorderLayoutPanel.getSpeciesWestPanel().mask();
		else
			searchBorderLayoutPanel.getSpeciesWestPanel().unmask();

		searchBorderLayoutPanel.getSpeciesNorthPanel().activeButtonSearch(!mask);
	}

	protected void showFilterResults(boolean onlySelected)
	{
		Log.trace("showFilterResults ******* onlySelected "+onlySelected);

		setOnlySelected(onlySelected);
		updateOnlySelectedOnManager(onlySelected);

		streamPagingLoader.reset();
		streamPagingLoader.pollingState();

		if (onlySelected) {
			disableFilters();
			streamPagingLoader.setPage(0);
		} else {
			Log.trace("currentPage: "+currentPage);
			currentPage = streamPagingLoader.getCurrentPage();
			streamPagingLoader.setPage(currentPage);
		}
	}

	public void openOccurenceWindow() {
		final OccurrencesWindow occurencesWindow = new OccurrencesWindow();
		occurencesWindow.show();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				occurencesWindow.loadOccurences();
			}
		});

	}

	public void saveOccurences(final SaveFileFormat fileFormat, final OccurrencesSaveEnum typeCSV) {

		String fileName = "Occurrences";

		switch (fileFormat) {
			case CSV: fileName += ".csv"; break;
			case DARWIN_CORE: fileName += ".xml"; break;
		}

		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save "+fileFormat.toString()+" As... ", fileName, false);

		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

			@Override
			public void onSaving(Item parent, final String fileName) {
				GWT.log("onSaving parent: "+parent +", fileName" +fileName);
				navigator.hide();

				Info.display("Saving in progress", "...");

				SpeciesDiscovery.taxonomySearchService.saveSelectedOccurrencePoints(parent.getId(), fileName, fileFormat, typeCSV, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");
					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error saving the file", "An error occurred saving the file, retry.");
						Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
					}
				});
			}

			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};

		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.show();
	}


	public void saveTaxonomyJobError(final JobTaxonomyModel jobTaxonomyModel, final String scientificName, final List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> listDataSources, final String rank) {

		// IN THIS CASE THERE IS ONE DATASOURCE
		final String dataSourceName = listDataSources.get(0)!=null?listDataSources.get(0).getName():"no datasource";

//		final String fileName = "Error on DWCA " +rank + " -"+scientificName +"- from "+dataSourceName+".zip";

		final String fileName = "Error file: "+jobTaxonomyModel.getName()+" from "+dataSourceName+".txt";

		SpeciesDiscovery.taxonomySearchService.isAvailableTaxonomyJobReportError(jobTaxonomyModel.getIdentifier(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error saving the file", "An error occurred saving the file, retry.");
				Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());

			}

			@Override
			public void onSuccess(Boolean result) {

				if(result)
					saveTaxonomyJobErrorFile();
				else
					Info.display("Info", "The selected job has not got an error file");
			}

			private void saveTaxonomyJobErrorFile() {

				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save the file with job Error/s As...", fileName, false);

				WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

					@Override
					public void onSaving(Item parent, final String fileName) {
						GWT.log("onSaving parent: "+parent +", fileName" +fileName);
						navigator.hide();

						Info.display("Saving in progress", "...");

						SpeciesDiscovery.taxonomySearchService.saveTaxonomyJobError(jobTaxonomyModel.getIdentifier(), parent.getId(), fileName, scientificName, dataSourceName, new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {

								if(result){
									Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");

									changeStatusJob(SearchResultType.TAXONOMY_ITEM, jobTaxonomyModel.getIdentifier(), DownloadState.SAVED);
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								Info.display("Error saving the file", "An error occurred saving the file, retry.");
								Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
							}
						});
					}

					@Override
					public void onAborted() {
						GWT.log("onAborted");
					}

					@Override
					public void onFailed(Throwable throwable) {
						GWT.log("onFailed");
					}
				};

				navigator.addWorkspaceExplorerSaveNotificationListener(listener);
				navigator.show();
			}
		});
	}

	public void saveTaxonomyJob(final JobTaxonomyModel jobTaxonomyModel, final String scientificName, final List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> listDataSources, String rank) {

		// IN THIS CASE THERE IS ONE DATASOURCE
		final String dataSourceName = listDataSources.get(0)!=null?listDataSources.get(0).getName():"no datasource";

		String fileName = jobTaxonomyModel.getName() +" from "+dataSourceName+" - DWCA.zip";

		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save DWCA As...", fileName, false);

		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

			@Override
			public void onSaving(Item parent, final String fileName) {
				GWT.log("onSaving parent: "+parent +", fileName" +fileName);
				navigator.hide();

				Info.display("Saving in progress", "...");

				SpeciesDiscovery.taxonomySearchService.saveTaxonomyJob(jobTaxonomyModel.getIdentifier(), parent.getId(), fileName, scientificName, dataSourceName, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {

						if(result){
							Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");

							changeStatusJob(SearchResultType.TAXONOMY_ITEM, jobTaxonomyModel.getIdentifier(), DownloadState.SAVED);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error saving the file", "An error occurred saving the file, retry.");
						Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
					}
				});
			}

			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};

		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.show();
	}

	private void saveOccurencesJobError(final JobOccurrencesModel jobOccurrencesModel,final String scientificName, List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> dataSourceList) {

		String dataSourceName = "";

		for (org.gcube.portlets.user.speciesdiscovery.shared.DataSource dataSource : dataSourceList) {
			dataSourceName+= dataSource.getName() + ",";
		}

		//remove last - char
		if(dataSourceName.endsWith(","))
			dataSourceName = dataSourceName.substring(0, dataSourceName.length()-1);

		final String dataSources = dataSourceName;

		final String fileName = "Error file: "+jobOccurrencesModel.getJobName()+" from "+dataSourceName+".txt";


		SpeciesDiscovery.taxonomySearchService.isAvailableOccurrenceJobReportError(jobOccurrencesModel.getJobIdentifier(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error saving the file", "An error occurred saving the file, retry.");
				Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());

			}

			@Override
			public void onSuccess(Boolean result) {
				if(result){
					saveOccurrenceJobError();
				}
				else
					Info.display("Info", "The selected job has not got an error file");
			}

			private void saveOccurrenceJobError() {

				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save the Error File As...", fileName, false);

				WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

					@Override
					public void onSaving(Item parent, final String fileName) {
						GWT.log("onSaving parent: "+parent +", fileName" +fileName);
						navigator.hide();

						Info.display("Saving in progress", "...");

						SpeciesDiscovery.taxonomySearchService.saveOccurrenceJobError(jobOccurrencesModel, parent.getId(), fileName, scientificName, dataSources, new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {

								if(result){
									Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");
									changeStatusJob(SearchResultType.OCCURRENCE_POINT, jobOccurrencesModel.getJobIdentifier(), DownloadState.SAVED);
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								Info.display("Error saving the file", "An error occurred saving the file, retry.");
								Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
							}
						});
					}

					@Override
					public void onAborted() {
						GWT.log("onAborted");
					}

					@Override
					public void onFailed(Throwable throwable) {
						GWT.log("onFailed");
					}
				};

				navigator.addWorkspaceExplorerSaveNotificationListener(listener);
				navigator.show();
			}
		});

	}

	private void saveGisLayerJob(final JobGisLayerModel jobGisLayer) {

		String fileName = jobGisLayer.getJobName();
		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save Gis Layer", fileName, false);
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

			@Override
			public void onSaving(Item parent, final String fileName) {
				GWT.log("onSaving parent: "+parent +", fileName" +fileName);
				navigator.hide();

				Info.display("Saving in progress", "...");

				SpeciesDiscovery.taxonomySearchService.saveGisLayerAsWsLink(jobGisLayer, parent.getId(), fileName, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {

						if(result){
							Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");
							changeStatusJob(SearchResultType.GIS_LAYER_POINT, jobGisLayer.getJobIdentifier(), DownloadState.SAVED);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error saving the file", "An error occurred saving the file, retry.");
						Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
					}
				});
			}

			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};

		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.setZIndex(XDOM.getTopZIndex()+1000);
		navigator.show();

	}


	private void saveOccurencesJob(final JobOccurrencesModel jobOccurrencesModel,final String scientificName, List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> dataSourceList) {

		String dataSourceName = "";

		for (org.gcube.portlets.user.speciesdiscovery.shared.DataSource dataSource : dataSourceList) {
			dataSourceName+= dataSource.getName() + ",";
		}
		//remove last - char
		if(dataSourceName.endsWith(","))
			dataSourceName = dataSourceName.substring(0, dataSourceName.lastIndexOf(","));

		String extension = "";
		String suffix = "";

		switch (jobOccurrencesModel.getFileFormat()) {
			case CSV: {
				suffix = "CSV "+jobOccurrencesModel.getCsvType();
				extension = "csv";
			} break;
			case DARWIN_CORE:{
				extension = "xml";
				suffix = "DwC";
			} break;
		}

//		String fileName = "Occurrences results of "+scientificName +"- from "+dataSourceName+" - "+suffix+"."+extension;

		String fileName = jobOccurrencesModel.getJobName() +" from "+dataSourceName+" - "+suffix+"."+extension;
		final String dataSources = dataSourceName;
		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save "+jobOccurrencesModel.getFileFormat().toString() +" As...", fileName, false);
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

			@Override
			public void onSaving(Item parent, final String fileName) {
				GWT.log("onSaving parent: "+parent +", fileName" +fileName);
				navigator.hide();

				Info.display("Saving in progress", "...");

				SpeciesDiscovery.taxonomySearchService.saveOccurrenceJob(jobOccurrencesModel, parent.getId(), fileName, scientificName, dataSources, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {

						if(result){
							Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");
							changeStatusJob(SearchResultType.OCCURRENCE_POINT, jobOccurrencesModel.getJobIdentifier(), DownloadState.SAVED);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error saving the file", "An error occurred saving the file, retry.");
						Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
					}
				});
			}

			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};

		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.show();
	}


	public void changeStatusJob(SearchResultType jobType, String jobId, DownloadState state){

		if(jobType.equals(SearchResultType.OCCURRENCE_POINT)){
			SpeciesDiscovery.taxonomySearchService.changeStatusOccurrenceJob(jobId, state, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("Error on change status", "An error occurred on change status, retry." +caught.getMessage());

				}

				@Override
				public void onSuccess(Boolean result) {
					if(result)
						excecuteGetJobs(SearchResultType.OCCURRENCE_POINT, true);

				}
			});
		}else if(jobType.equals(SearchResultType.TAXONOMY_ITEM)){
			SpeciesDiscovery.taxonomySearchService.changeStatusTaxonomyJob(jobId, state, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("Error on change status", "An error occurred on change status, retry." +caught.getMessage());

				}

				@Override
				public void onSuccess(Boolean result) {
					if(result)
						excecuteGetJobs(SearchResultType.TAXONOMY_ITEM, true);

				}
			});
		}else if(jobType.equals(SearchResultType.GIS_LAYER_POINT)){
			SpeciesDiscovery.taxonomySearchService.changeStatusGisLayerJob(jobId, state, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("Error on change status", "An error occurred on change status, retry." +caught.getMessage());

				}

				@Override
				public void onSuccess(Boolean result) {
					if(result)
						excecuteGetJobs(SearchResultType.GIS_LAYER_POINT, true);

				}
			});
		}
		//TODO
	}


	public void saveTaxonomy(final SaveFileFormat fileFormat) {

		String fileName = "Taxonomy";

		switch (fileFormat) {
			case DARWIN_CORE_ARCHIVE: fileName += ".zip"; break;
		}

		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save "+fileFormat.toString() +" As...", fileName, false);
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

			@Override
			public void onSaving(Item parent, final String fileName) {
				GWT.log("onSaving parent: "+parent +", fileName" +fileName);
				navigator.hide();

				Info.display("Saving in progress", "...");

				SpeciesDiscovery.taxonomySearchService.saveSelectedTaxonomyPoints(parent.getId(), fileName, fileFormat, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						Info.display("File saved", "The "+fileName+" file has been saved in the workspace.");
					}

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error saving the file", "An error occurred saving the file, retry.");
						Log.error("Error saving the file", "An error occurred saving the file, retry." +caught.getMessage());
					}
				});
			}

			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};

		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.show();
	}

	//MOVED
//	public void showOccurrencesMap()
//	{
//		final MessageBox progress = MessageBox.wait("Generating map", "Calculating occurrence points", "generating...");
//
//		SpeciesDiscovery.taxonomySearchService.generateMapFromSelectedOccurrencePoints(new AsyncCallback<String>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				progress.close();
//				Info.display("Error generating the map", "An error occurred generating the map, retry");
//				Log.error("Error on loading", "An error occurred on edit listner, retry." +caught.getMessage());
//
//			}
//
//			@Override
//			public void onSuccess(String layerName) {
//				progress.close();
//				showMap(layerName);
//			}
//		});
//	}

	public void showMap(final String layerName){

		Log.trace("Obtaining public link for layer : "+layerName);
		Info.display("Just moment...", "Generating link to layer");
		final DialogBox box = new DialogBox(true);
		box.setText("Link to Occurrence Layer (CTRL+C to copy)");
		final MessageForm form = new MessageForm(true, "Generating link...") {
			@Override
			public void closeHandler() {
				box.hide();
			}
		};

		box.setWidget(form);
		box.center();

		SpeciesDiscovery.gisInfoService.getGisLinkByLayerName(layerName, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error showing the map", "An error occurred while opening the map. Please retry");
				Log.error("Error on opening map", "An error occurred while getting gis info for layer : "+layerName+"."+caught.getMessage());
			}


			@Override
			public void onSuccess(String result) {
				form.setTextMessage(result, true);
				//com.google.gwt.user.client.Window.open(result,"Occurrence Map","");
			}
		});
	}


	private void setOnlySelected(boolean b){

		showOnlySelected = b;
		searchBorderLayoutPanel.getSpeciesCenterPanel().activeBtnShowOnlySelected(b);

	}


	private void updateOnlySelectedOnManager(boolean showOnlySelected){
		dataSourceManager.setOnlySelected(showOnlySelected);
	}

	public void reset(){
		streamPagingLoader.reset();
		streamPagingLoader.setActiveFilterObject(null);
//		isActiveFilterOnResult = false;
		activeFilterOnResult(false);

		//ADDED 17/07/2013
		searchBorderLayoutPanel.getSpeciesCenterPanel().getResultRowPanel().getClassicGridView().unmask();
		searchBorderLayoutPanel.getSpeciesWestPanel().unmask();
		searchBorderLayoutPanel.getSpeciesWestPanel().resetFilters();
		searchBorderLayoutPanel.getSpeciesNorthPanel().enableSearch();
	}

	private void switchView(SpeciesCapability resultType) {
		Log.trace("switchView ... ");
		searchBorderLayoutPanel.getSpeciesCenterPanel().updateCurrentGridView(resultType);
	}

	private AsyncCallback<Void> initSearchCallback(){

		reset();
		searchBorderLayoutPanel.getSpeciesCenterPanel().setFilterActive(false, "");
		setOnlySelected(false);

		return new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void voi) {
				streamPagingLoader.startLoading(false);
				loadLastQuery(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				loadLastQuery(true);
				Info.display("Error during the search", caught.getMessage());
				Log.error("Error during the search", caught.getMessage());
				reset();
				streamPagingLoader.resetFilters();
			}
		};
	}

	private AsyncCallback<SearchByQueryParameter> initSearchByQueryCallback(){

		reset();
		searchBorderLayoutPanel.getSpeciesCenterPanel().setFilterActive(false, "");
		setOnlySelected(false);

		return new AsyncCallback<SearchByQueryParameter>() {

			@Override
			public void onSuccess(SearchByQueryParameter queryParameters) {

				SpeciesCapability capability = Util.getCapabilityFromResultType(queryParameters.getSearchResultType());

				//UPDATING LAST SEARCH EVENT FOR UPDATE THE GUI
				lastSearchEvent.setResultType(capability);
				lastSearchEvent.setMapTermsSearched(queryParameters.getTerms());

				System.out.println("queryParameters.getTerms() "+queryParameters.getTerms());

				if(queryParameters.getTerms()!=null){
					String terms = "";
					for (SearchType key : queryParameters.getTerms().keySet()) {
						terms += queryParameters.getTerms().get(key)+",";
					}

					if(terms.length()>2){
						terms = terms.substring(0, terms.length()-1);
					}
					terms = terms.replaceAll("\\[", "").replaceAll("\\]",""); //REMOVE brackets

					lastSearchEvent.setSearchTerm(terms);
				}

				setDataSourceType(capability);
				switchView(capability);

				searchBorderLayoutPanel.getSpeciesCenterPanel().activeToolBarButtons(true);
//				eventBus.fireEvent(new SearchStartedEvent());
				streamPagingLoader.startLoading(false);

				loadLastQuery(false);
			}

			@Override
			public void onFailure(Throwable caught) {

				doActiveMaskLoadingGridAndButtonSearch(false);
				Info.display("Error during the search", caught.getMessage());
				Log.error("Error during the search", caught.getMessage());
				reset();
				streamPagingLoader.resetFilters();
				loadLastQuery(true);
			}
		};
	}

	protected void loadLastQuery(boolean isError){

		if(isError){
			searchBorderLayoutPanel.getSpeciesSouthPanel().setLastQueryAsEmpty();
			return;
		}

		SpeciesDiscovery.taxonomySearchService.getLastQuery(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {

				searchBorderLayoutPanel.getSpeciesSouthPanel().setLastQueryAsEmpty();
			}

			@Override
			public void onSuccess(String query) {
				GWT.log("load last Query: "+query);

				if(query!=null && !query.isEmpty())
					searchBorderLayoutPanel.getSpeciesSouthPanel().setLastQuery(query);
				else
					searchBorderLayoutPanel.getSpeciesSouthPanel().setLastQueryAsEmpty();
			}
		});
	}

	protected void searchByQuery(String query) {

		Log.trace("IN SEARCH BY QUERY..." + query);

		AsyncCallback<SearchByQueryParameter> callback = initSearchByQueryCallback();

		//VALIDATOR

		query = SearchTermValidator.replaceOccurrenceTermWithProduct(query);

		SpeciesDiscovery.taxonomySearchService.searchByQuery(query, callback);

		eventBus.fireEvent(new SearchStartedEvent());


	}

	protected void search(SearchType type, String searchTerm, Number upperBoundLongitude, Number upperBoundLatitude, Number lowerBoundLongitude, Number lowerBoundLatitude, Date fromDate, Date toDate, List<DataSourceModel> listDataSources, String groupRank, SpeciesCapability resultType, List<DataSourceModel> listDataSourcesForSynonyms, List<DataSourceModel> listDataSourcesForUnfold)
	{
		GWT.log("IN SEARCH..............");
		Log.trace("IN SEARCH..............");

		AsyncCallback<Void> callback = initSearchCallback();

		Coordinate upperCoordinate = upperBoundLatitude!=null && upperBoundLongitude!=null?new Coordinate(upperBoundLatitude.floatValue(), upperBoundLongitude.floatValue()):null;
		Coordinate lowerCoordinate = lowerBoundLatitude!=null && lowerBoundLongitude!=null?new Coordinate(lowerBoundLatitude.floatValue(), lowerBoundLongitude.floatValue()):null;

		SearchFilters filters = new SearchFilters(upperCoordinate, lowerCoordinate, fromDate, toDate, listDataSources, groupRank, resultType, listDataSourcesForSynonyms, listDataSourcesForUnfold);

		Log.trace("**********result type: " + filters.getResultType());

		//VALIDATOR
		searchTerm = SearchTermValidator.validateQueryTerm(searchTerm);


		switch (type) {

			case BY_SCIENTIFIC_NAME:
				GWT.log("search BY_SCIENTIFIC_NAME");
				SpeciesDiscovery.taxonomySearchService.searchByScientificName(searchTerm, filters, callback);
				break;
			case BY_COMMON_NAME:
				GWT.log("search BY_COMMON_NAME");
				SpeciesDiscovery.taxonomySearchService.searchByCommonName(searchTerm, filters, callback);
				break;
		}

		Log.trace("resultType "+ resultType);

		setDataSourceType(resultType);
		switchView(resultType);
		searchBorderLayoutPanel.getSpeciesCenterPanel().activeToolBarButtons(true);


		eventBus.fireEvent(new SearchStartedEvent());

	}


	private void setDataSourceType(SpeciesCapability resultType) {
		switchDataSource(resultType);
	}

	protected String getCommonNamesHTML(String scientificName, String author, String credits, List<CommonName> commonNames)
	{
		StringBuilder html = new StringBuilder("<p><h1 style=\"color: #385F95;\">");

		html.append(scientificName);
		html.append("</h1>");

		if (commonNames.size()>0)  html.append("aka : ");

		html.append("<table>");

		Set<String> insertedLanguages = new HashSet<String>();

		for (CommonName commonName:commonNames) {
			if (insertedLanguages.contains(commonName.getLanguage())) continue;
			else insertedLanguages.add(commonName.getLanguage());

			html.append("<tr><td><b>");
			html.append(commonName.getLanguage());
			html.append(":</b></td><td>");
			html.append(commonName.getName());
			html.append("</td></tr>");
		}

		html.append("<tr></tr>");
		html.append("<tr><td><b>Inserted by: </b></td><td>");
		html.append(author);
		html.append("</td></tr>");
		html.append("<tr><td><b>Credits: </b></td><td>");
		html.append(credits);
		html.append("</td></tr>");
		html.append("</table>");

		html.append("</p>");

		return html.toString();
	}


	public EventBus getEventBus() {
		return eventBus;
	}

	public SearchEvent getLastSearchEvent() {
		return lastSearchEvent;
	}

	public static void excecuteGetJobs(SearchResultType type, final boolean resetStructures){

		System.out.println("New rpc get list SpeciesJobs......." + type);

		 if(type.equals(SearchResultType.TAXONOMY_ITEM)){

			 if(resetStructures)
				 TaxonomyJobSpeciesPanel.getInstance(eventBus).getGridJob().mask("Loading", ConstantsSpeciesDiscovery.LOADINGSTYLE);

			 SpeciesDiscovery.taxonomySearchService.getListTaxonomyJobs(new AsyncCallback<List<JobTaxonomyModel>>() {

				@Override
				public void onFailure(Throwable caught) {

					if(caught instanceof SessionExpired)
						CheckSession.showLogoutDialog();
				}

				@Override
				public void onSuccess(List<JobTaxonomyModel> result) {

					if(resetStructures)
						TaxonomyJobSpeciesPanel.getInstance(eventBus).getGridJob().unmask();

					if(result.size()>0){

						if(resetStructures)
							TaxonomyJobSpeciesPanel.getInstance(eventBus).resetStructures();

						TaxonomyJobSpeciesPanel.getInstance(eventBus).addListJob(result);
					}

				}

			 });
		 }

		 else  if(type.equals(SearchResultType.OCCURRENCE_POINT)){

			 if(resetStructures)
				 OccurrenceJobSpeciesPanel.getInstance(eventBus).getGridJob().mask("Loading", ConstantsSpeciesDiscovery.LOADINGSTYLE);

			 SpeciesDiscovery.taxonomySearchService.getListOccurrencesJob(new AsyncCallback<List<JobOccurrencesModel>>() {

					@Override
					public void onFailure(Throwable caught) {

						if(caught instanceof SessionExpired)
							CheckSession.showLogoutDialog();
					}

					@Override
					public void onSuccess(List<JobOccurrencesModel> result) {

						if(resetStructures)
							OccurrenceJobSpeciesPanel.getInstance(eventBus).getGridJob().unmask();

						if(result.size()>0){

							if(resetStructures)
								OccurrenceJobSpeciesPanel.getInstance(eventBus).resetStructures();

							OccurrenceJobSpeciesPanel.getInstance(eventBus).addListJob(result);
						}

					}

			 });
		 }
		 else if(type.equals(SearchResultType.GIS_LAYER_POINT)){

			 if(resetStructures)
				 GisLayerJobSpeciesPanel.getInstance(eventBus).getGridJob().mask("Loading", ConstantsSpeciesDiscovery.LOADINGSTYLE);

			 //TODO

			 SpeciesDiscovery.taxonomySearchService.getListGisLayerJob(new AsyncCallback<List<JobGisLayerModel>>() {

				@Override
				public void onFailure(Throwable caught) {

					if(caught instanceof SessionExpired)
						CheckSession.showLogoutDialog();

				}

				@Override
				public void onSuccess(List<JobGisLayerModel> result) {

					if(resetStructures)
						GisLayerJobSpeciesPanel.getInstance(eventBus).getGridJob().unmask();

					if(result.size()>0){

						if(resetStructures)
							GisLayerJobSpeciesPanel.getInstance(eventBus).resetStructures();

						GisLayerJobSpeciesPanel.getInstance(eventBus).addListJob(result);
					}

				}
			});

		 }

	}

}
