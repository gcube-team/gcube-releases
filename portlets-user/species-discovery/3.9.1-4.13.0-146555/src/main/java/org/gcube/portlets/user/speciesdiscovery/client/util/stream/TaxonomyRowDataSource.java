package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.SearchController;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveMaskLoadingGrid;
import org.gcube.portlets.user.speciesdiscovery.client.gridview.ResultRowResultsPanel;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResult;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TaxonomyRowDataSource implements DataSource {
	
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

			SpeciesDiscovery.taxonomySearchService.getSearchTaxonomyRow(start, limit, activeFiltersObject, showOnlySelected,  new AsyncCallback<SearchResult<TaxonomyRow>>() {

				@Override
				public void onFailure(Throwable caught) {
					Info.display("Error", "An error occurred on retriving search results, retry.");
					Log.error("Error retriving search results", caught);			
					callback.onFailure(caught);
//					reset();
//					streamPagingLoader.resetFilters();
				}

				@Override
				public void onSuccess(SearchResult<TaxonomyRow> result) {
					if(result!=null){
						List<ModelData> data = convertData(result.getResults());
						callback.onSuccess(data);
					}
					else
						Log.error("Error retriving search result is null");
				}
			});

	}

	
	protected List<ModelData> convertData(ArrayList<TaxonomyRow> rows)
	{
		List<ModelData> data = new ArrayList<ModelData>(rows.size());
		for (TaxonomyRow row:rows)
			data.add(convertTaxonomyRow(row));
		
		return data;
	}
	
	protected BaseModelData convertTaxonomyRow(TaxonomyRow row)
	{
		BaseModelData data = new BaseModelData();
		
		//Init values
		String dataProviderName = "";
		String dataSetCitation= "";
		String rank= "";
//		String matchingAccordingTo= "";
		String statusRedId= "";
		String statusRefName= "";
		String dateModified = "";
		String name = "";
		String statusRemarks = "";
		String classificationString = "";
		String author = "";
		List<ItemParameter> hashProperties = new ArrayList<ItemParameter>();

//		Log.trace("Taxonomy returned in client: " + row);

		if(row.getDataProviderName()!=null) dataProviderName = row.getDataProviderName();
		if(row.getDataSetCitation()!=null) dataSetCitation = row.getDataSetCitation();
		if(row.getRank()!=null) rank = row.getRank();
		if(row.getStatusRefId()!=null) statusRedId = row.getStatusRefId();
		if(row.getStatusName()!=null) statusRefName = row.getStatusName();
		if(row.getDateModified()!=null) dateModified = row.getDateModified();
		if(row.getName()!=null) name = row.getName();
//		if(row.getAccordingTo()!=null) matchingAccordingTo = row.getAccordingTo();
		if(row.getStatusRemarks()!=null) statusRemarks = row.getStatusRemarks();
		if(row.getProperties()!=null) hashProperties = row.getProperties();
		if(row.getScientificNameAuthorship()!=null) author = row.getScientificNameAuthorship();
		
		if(row.getParents()!=null){
			classificationString = getClassification(row.getParents());
			data.set(TaxonomyGridField.CLASSIFICATION_STRING.getId(), classificationString);
			String rankHTML = getRankHtml(row.getParents());
			data.set(TaxonomyGridField.TAXONOMY.getId(), rankHTML);
		}

		data.set(TaxonomyGridField.SCIENTIFIC_NAME.getId(), name);
		data.set(TaxonomyGridField.SELECTION.getId(), row.isSelected());
		data.set(TaxonomyGridField.DATASOURCE.getId(), dataProviderName);
		data.set(TaxonomyGridField.CITATION.getId(), dataSetCitation);
		data.set(TaxonomyGridField.MATCHING_RANK.getId(), rank);
		data.set(TaxonomyGridField.STATUSREFNAME.getId(), statusRefName);
		data.set(TaxonomyGridField.STATUSREFID.getId(), statusRedId);
		data.set(TaxonomyGridField.DATEMODIFIED.getId(), dateModified);
		data.set(TaxonomyGridField.SCIENTIFICNAMEAUTHORSHIP.getId(), author);
//		data.set(TaxonomyGridField.MATCHING_ACCORDING_TO.getId(),matchingAccordingTo);
		data.set(TaxonomyGridField.STATUS_REMARKS.getId(),statusRemarks);
		
		data.set(TaxonomyGridField.PROPERTIES.getId(),hashProperties);
		
//		String products = getProdutcsHTML(layerCount, occurencesCount);
//		data.set(TaxonomyGridField.PRODUCTS.getId(), products);
		
		String provenance = getProvenanceHTML(dataProviderName, dataSetCitation);
		data.set(SpeciesGridFields.PROVENANCE.getId(), provenance);

		data.set(TaxonomyGridField.ROW.getId(), row);
		
		return data;
	}
	
	public String getClassification(List<TaxonomyRow> listTaxonomy)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = listTaxonomy.size()-1; i >= 0; i--) {
			TaxonomyRow taxonomy = listTaxonomy.get(i);

			sb.append("<b>");

			if(!NormalizeString.isUndefined(taxonomy.getRank()))
				sb.append(taxonomy.getRank()); 
			else
				sb.append("Rank not found");
			
			sb.append("</b>: ");
			if(!NormalizeString.isUndefined(taxonomy.getName()))
				sb.append(taxonomy.getName());
			else
				sb.append("Name not found");
	
			if(i!=0)
				sb.append(" -> ");
		}
		
		return sb.toString();
		
	}

	
	//TODO MODIFY
	
	protected String getRankHtml(List<TaxonomyRow> listTaxonomy)
	{
		String id = Document.get().createUniqueId();
		StringBuilder html = new StringBuilder("<p><br/>");
		int offset = html.length();
		html.append("<table id=\"");
		html.append(id);
		html.append("\">");
		boolean addToggler = false;
		for (TaxonomyRow taxonomy : listTaxonomy) {
			if (!Util.isMainTaxonomicRank(taxonomy.getRank())) {
				html.append("<tr class=\"");
				html.append(ResultRowResultsPanel.TOGGLE_CLASS);
				html.append("\" style=\"display:none\"><td><b>");
				addToggler = true;
			} else html.append("<tr><td><b>");
			
			if(!NormalizeString.isUndefined(taxonomy.getRank()))
				html.append(taxonomy.getRank());
			else
				html.append("Rank not found");

//			html.append(taxonomy.getRank());
			html.append(":</b></td><td>{");
			
			if(!NormalizeString.isUndefined(taxonomy.getName()))
				html.append(taxonomy.getName());
			else
				html.append("Name not found");
			
//			html.append(taxonomy.getName());
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

	

	protected String getProvenanceHTML(String dataSource, String datasetCitation)
	{
		StringBuilder html = new StringBuilder("<p><br/><table>");

//		html.append("<tr><td><b>Data Source:</b></td><td>");
//		html.append(datasource);
//		html.append("</td></tr>");
//
		html.append("<tr><td><b>Data Source:</b></td><td>");
		html.append(dataSource);
		html.append("</td></tr>");

//		html.append("<tr><td><b>Data Set:</b></td><td>");
//		html.append(dataset);
//		html.append("</td></tr>");

		html.append("<tr><td><b>Citation:</b></td><td>");
		html.append(datasetCitation);
		html.append("</td></tr>");

		html.append("</table></p>");

		return html.toString();
	}

//	protected String getProdutcsHTML(int layersCount, int occurencesCount)
//	{
//		StringBuilder html = new StringBuilder("<p><br/><table>");
//		html.append("<tr><td><b>");
//		html.append(TaxonomyGridField.PRODUCT_OCCURRENCES.getName());
//		html.append(":</b></td><td>");
//		html.append(layersCount);
//		html.append("</td></tr>");
//
//		html.append("</table></p>");
//
//		return html.toString();
//	}



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
		return "TaxonomyRowDataSource";
	}


}
