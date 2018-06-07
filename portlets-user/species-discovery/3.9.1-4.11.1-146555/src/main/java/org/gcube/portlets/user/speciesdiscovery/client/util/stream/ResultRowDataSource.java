package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.user.speciesdiscovery.client.SearchController;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveMaskLoadingGrid;
import org.gcube.portlets.user.speciesdiscovery.client.gridview.ResultRowResultsPanel;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResult;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ResultRowDataSource implements DataSource {
	
	protected boolean showOnlySelected = false;
	protected boolean isActiveFilterOnResult = false;
	
	@Override
	public void getStreamState(final AsyncCallback<StreamState> callback) {

		SpeciesDiscovery.taxonomySearchService.getSearchStatus(showOnlySelected, isActiveFilterOnResult, new AsyncCallback<SearchStatus>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error", "An error occurred on retriving search results");
				Log.error("Error retriving get stream state", caught);
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(SearchStatus result) {
				
				SearchController.eventBus.fireEvent(new ActiveMaskLoadingGrid(false));
				
				if(result!=null)
					callback.onSuccess(new StreamState(result.getSize(), result.isResultEOF(), result.isMaxSize()));
				else
					Log.error("Error retriving search status is null");
			}
		});
	}
	


	@Override
	public void getData(int start, int limit, ResultFilter activeFiltersObject, final AsyncCallback<List<ModelData>> callback) {

			SpeciesDiscovery.taxonomySearchService.getSearchResultRows(start, limit, activeFiltersObject, showOnlySelected,  new AsyncCallback<SearchResult<ResultRow>>() {

				@Override
				public void onFailure(Throwable caught) {
					Info.display("Error", "An error occurred on retriving search results, retry.");
					Log.error("Error retriving search results", caught);			
					callback.onFailure(caught);
//					reset();
//					streamPagingLoader.resetFilters();
				}

				@Override
				public void onSuccess(SearchResult<ResultRow> result) {
					if(result!=null){
						List<ModelData> data = convertData(result.getResults());

						callback.onSuccess(data);
					}
					else
						Log.error("Error retriving search result is null");
				}
			});

	}

	
	protected List<ModelData> convertData(ArrayList<ResultRow> rows)
	{
	
		List<ModelData> data = new ArrayList<ModelData>(rows.size());
		for (ResultRow row:rows)
			data.add(convertResultRow(row));
		
		return data;
	}
	
	//TODO modified
	public static String getClassification(List<Taxon> listTaxon)
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = listTaxon.size()-1; i >= 0; i--) {
			Taxon taxon = listTaxon.get(i);

			sb.append("<b>");
			
			if(!NormalizeString.isUndefined(taxon.getRank()))
				sb.append(taxon.getRank());
			else
				sb.append("Rank not found");
	
			sb.append("</b>: ");
			
			if(!NormalizeString.isUndefined(taxon.getName()))
				sb.append(taxon.getName());
			else
				sb.append("Name not found");
			
			if(i!=0)
				sb.append(" -> ");
		}
		return sb.toString();
	}

	protected BaseModelData convertResultRow(ResultRow row)
	{
		BaseModelData data = new BaseModelData();
		
		//Init values
		String dataSourceName = "";
		String dataProviderName = "";
		String dataSetName = "";
		String dataSetCitation= "";
		String matchingTaxonName= "";
		String matchingAccordionTo= "";
		String matchingRank= "";
		String matchingCredits= "";
		int imagesCount= 0;
		int mapCount= 0;
		int layerCount = 0;
		int occurencesCount = 0;
		String authorship = "";
		
		List<ItemParameter> listProperties = new ArrayList<ItemParameter>();
		
		//Validate values
		if(row.getParents()!=null){
			
			if(row.getParents().get(0).getName()!=null) matchingTaxonName = row.getParents().get(0).getName();
			
			if(row.getParents().get(0).getAccordingTo()!=null) matchingAccordionTo = row.getParents().get(0).getAccordingTo();
			
			if(row.getParents().get(0).getRank()!=null) matchingRank = row.getParents().get(0).getRank();
			
			String rankHTML = getRankHtml(row.getParents());
			data.set(SpeciesGridFields.TAXON.getId(), rankHTML);
			
			String classificationString = getClassification(row.getParents());
			data.set(SpeciesGridFields.CLASSIFICATION_STRING.getId(), classificationString);
		
		}
	
		if(row.getDataSourceName()!=null) dataSourceName = row.getDataSourceName();
		if(row.getDataProviderName()!=null) dataProviderName = row.getDataProviderName();
		if(row.getDataSetName()!=null) dataSetName = row.getDataSetName();
		if(row.getDataSetCitation()!=null) dataSetCitation = row.getDataSetCitation();
		if(row.getMatchingCredits()!=null) matchingCredits = row.getMatchingCredits();
		if(row.getImagesCount()!=0) imagesCount = row.getImagesCount();
		if(row.getMapsCount()!=0) mapCount = row.getMapsCount();
		if(row.getLayersCount()!=0) layerCount = row.getLayersCount();
		if(row.getOccurencesCount()!=0) occurencesCount = row.getOccurencesCount();
		if(row.getScientificNameAuthorship()!=null) authorship = row.getScientificNameAuthorship();
		
		if(row.getProperties()!=null) listProperties = row.getProperties();
		
		
		data.set(SpeciesGridFields.SELECTION.getId(), row.isSelected());
		data.set(SpeciesGridFields.DATASOURCE.getId(), dataSourceName);
		data.set(SpeciesGridFields.DATAPROVIDER.getId(), dataProviderName);
		data.set(SpeciesGridFields.DATASET.getId(), dataSetName);
		data.set(SpeciesGridFields.DATASET_CITATION.getId(), dataSetCitation);

		
		data.set(SpeciesGridFields.MATCHING_NAME.getId(), matchingTaxonName);
		data.set(SpeciesGridFields.MATCHING_AUTHOR.getId(), matchingAccordionTo);
		data.set(SpeciesGridFields.MATCHING_RANK.getId(), matchingRank);
		
		data.set(SpeciesGridFields.MATCHING_CREDITS.getId(),matchingCredits);
		data.set(SpeciesGridFields.PROPERTIES.getId(),listProperties);
		data.set(SpeciesGridFields.SCIENTIFICNAMEAUTHORSHIP.getId(), authorship);
		
		List<CommonName> listCommonName = new ArrayList<CommonName>();

		if(row.getCommonNames()!=null){
			listCommonName = row.getCommonNames();
		}

		String commonNames = getCommonNamesHTML(matchingTaxonName, matchingAccordionTo, matchingCredits, listCommonName);
		data.set(SpeciesGridFields.COMMON_NAMES.getId(), commonNames);
		
//		if(row.existsCommonName())
//		eventBus.fireEvent(new SetCommonNamesEvent(data));

		data.set(SpeciesGridFields.PRODUCT_IMAGES.getId(), imagesCount);
		data.set(SpeciesGridFields.PRODUCT_MAPS.getId(), mapCount);
		data.set(SpeciesGridFields.PRODUCT_LAYERS.getId(), layerCount);
		data.set(SpeciesGridFields.PRODUCT_OCCURRENCES.getId(), occurencesCount);

		String provenance = getProvenanceHTML(dataSourceName, dataProviderName, dataSetName, dataSetCitation);
		data.set(SpeciesGridFields.PROVENANCE.getId(), provenance);

		String products = getProdutcsHTML(imagesCount, mapCount, layerCount, occurencesCount);
		data.set(SpeciesGridFields.PRODUCTS.getId(), products);

		data.set(SpeciesGridFields.ROW.getId(), row);
		
		Log.info("Result row on client id: "+row.getId() + " service id: " +row.getServiceId());
		
		return data;
	}
	
	
	protected String getRankHtml(List<Taxon> listTaxon)
	{
		String id = Document.get().createUniqueId();
		StringBuilder html = new StringBuilder("<p><br/>");
		int offset = html.length();
		html.append("<table id=\"");
		html.append(id);
		html.append("\">");
		boolean addToggler = false;
		
		for (Taxon taxon : listTaxon) {
			if (!Util.isMainTaxonomicRank(taxon.getRank())) {
				html.append("<tr class=\"");
				html.append(ResultRowResultsPanel.TOGGLE_CLASS);
				html.append("\" style=\"display:none\"><td><b>");
				addToggler = true;
			} else html.append("<tr><td><b>");
			
			
			if(!NormalizeString.isUndefined(taxon.getRank()))
				html.append(taxon.getRank());
			else
				html.append("Rank not found");
			
			html.append(":</b></td><td>{");
			
			if(!NormalizeString.isUndefined(taxon.getName()))
				html.append(taxon.getName());
			else
				html.append("Name not found");
			
//			html.append(taxon.getRank());
//			html.append(":</b></td><td>{");
//			html.append(taxon.getName());
			html.append("}</td></tr>");
		}
		html.append("</table>");
		

		if (addToggler) {
			StringBuilder toggler = new StringBuilder();

			String idImgExpand = Document.get().createUniqueId();
			String idImgCollapse = Document.get().createUniqueId();

			toggler.append("<a href=\"#\" onclick=\"toggle('");
			toggler.append(ResultRowResultsPanel.TOGGLE_CLASS);		
			toggler.append("','");
			toggler.append(id);
			toggler.append("','tr');toggleSingle('");
			toggler.append(idImgExpand);
			toggler.append("');toggleSingle('");
			toggler.append(idImgCollapse);
			toggler.append("')\">");


			toggler.append("<img id=\"");
			toggler.append(idImgExpand);
			toggler.append("\" src=\"");
			toggler.append(Resources.INSTANCE.getExpand().getSafeUri().asString());
			toggler.append("\" style=\"float:left;\" />");

			toggler.append("<img id=\"");
			toggler.append(idImgCollapse);
			toggler.append("\" src=\"");
			toggler.append(Resources.INSTANCE.getCollapse().getSafeUri().asString());
			toggler.append("\" style=\"float:left;display:none\" />");

			toggler.append("</a>");


			html.insert(offset, toggler.toString());
		} else {
			html.insert(offset, "<div style=\"padding-left:" + Resources.INSTANCE.getExpand().getWidth() + "px\">");
			html.append("</div>");
		}
		html.append("</p>");

		return html.toString();
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

	protected String getProvenanceHTML(String datasource, String dataprovider, String dataset, String datasetCitation)
	{
		StringBuilder html = new StringBuilder("<p><br/><table>");

		html.append("<tr><td><b>Data Source:</b></td><td>");
		html.append(datasource);
		html.append("</td></tr>");

		html.append("<tr><td><b>Data Provider:</b></td><td>");
		html.append(dataprovider);
		html.append("</td></tr>");

		html.append("<tr><td><b>Data Set:</b></td><td>");
		html.append(dataset);
		html.append("</td></tr>");

		html.append("<tr><td><b>Citation:</b></td><td>");
		html.append(datasetCitation);
		html.append("</td></tr>");

		html.append("</table></p>");

		return html.toString();
	}

	protected String getProdutcsHTML(int imagesCount, int mapCounts, int layersCount, int occurencesCount)
	{
		StringBuilder html = new StringBuilder("<p><br/><table>");
		html.append("<tr><td><b>");
		html.append(SpeciesGridFields.PRODUCT_OCCURRENCES.getName());
		html.append(":</b></td><td>");
		html.append(occurencesCount);
		html.append("</td></tr>");

		html.append("</table></p>");

		return html.toString();
	}



	public boolean isShowOnlySelected() {
		return showOnlySelected;
	}



	public void setShowOnlySelected(boolean showOnlySelected) {
		this.showOnlySelected = showOnlySelected;
	}



	public boolean isActiveFilterOnResult() {
		return isActiveFilterOnResult;
	}



	public void setActiveFilterOnResult(boolean isActiveFilterOnResult) {
		this.isActiveFilterOnResult = isActiveFilterOnResult;
	}



	@Override
	public String getInfo() {
		return "ResultRowDataSource";
	}


}
