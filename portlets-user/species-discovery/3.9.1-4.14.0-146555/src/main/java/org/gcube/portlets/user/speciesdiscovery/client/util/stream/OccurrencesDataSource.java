package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.util.OccurencesGridFields;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrenceBatch;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OccurrencesDataSource implements DataSource{

	private int count;
	

		@Override
		public void getStreamState(AsyncCallback<StreamState> callback) {
			callback.onSuccess(new StreamState(count, true, false));
		}
		
		@Override
		public void getData(int start, int limit, ResultFilter activeFiltersObject, final AsyncCallback<List<ModelData>> callback) {
			SpeciesDiscovery.taxonomySearchService.getOccurrencesBatch(start, limit, new AsyncCallback<OccurrenceBatch>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("Error retrieving occurrences points", "Error retrieving occurrences points, please retry");
					callback.onFailure(caught);
					
				}

				@Override
				public void onSuccess(OccurrenceBatch result) {
					
					Log.trace("Retrieved Occurrence Batch: "+result);
					
					if (result.getOccurrences()!=null) {
						Log.trace("#Occurrences: "+result.getOccurrences().size());
						
						List<ModelData> data = new ArrayList<ModelData>(result.getOccurrences().size());
						for (Occurrence occurence:result.getOccurrences()) {
							data.add(convertToData(occurence));
						}
						callback.onSuccess(data);
					}
				}
			});
		}

	
	protected BaseModelData convertToData(Occurrence occurrence)
	{
		BaseModelData data = new BaseModelData();
		
		String authorship = "";
		String lsid = "";
		String credits = "";
		
		String propertiesHtml = "";
		
		if(occurrence.getScientificNameAuthorship()!=null) authorship = occurrence.getScientificNameAuthorship();
		
		if(occurrence.getLsid()!=null) lsid = occurrence.getLsid();
		
		if(occurrence.getCredits()!=null) credits = occurrence.getCredits();
		
	
		if(occurrence.getProperties()!=null){

			List<ItemParameter> listProperties = occurrence.getProperties();
			Collections.sort(listProperties, ItemParameter.COMPARATOR);
			
			propertiesHtml+="<table class=\"parameters\">";
			
			for (ItemParameter itemParameter : listProperties) {
				
				propertiesHtml+=
					"<tr>" +
					"	<td class=\"title\">"+itemParameter.getKey()+"</td>" +
					"	<td>"+itemParameter.getValue()+"</td>" +
					"</tr>";
			}
			
			propertiesHtml+="</table>";
		}
		
		data.set(OccurencesGridFields.INSTITUTION_CODE.getId(), occurrence.getInstitutionCode());
		data.set(OccurencesGridFields.COLLECTION_CODE.getId(), occurrence.getCollectionCode());
		data.set(OccurencesGridFields.CATALOGUE_NUMBER.getId(), occurrence.getCatalogueNumber());
		
		data.set(OccurencesGridFields.DATASET.getId(), occurrence.getDataSet());
		data.set(OccurencesGridFields.DATAPROVIDER.getId(), occurrence.getDataProvider());
		data.set(OccurencesGridFields.DATASOURCE.getId(), occurrence.getDataSource());
		
		
		data.set(OccurencesGridFields.SCIENTIFICNAMEAUTHORSHIP.getId(),authorship);
//		data.set(OccurencesGridFields.LSID.getId(), lsid);
		data.set(OccurencesGridFields.CREDITS.getId(), credits);
		data.set(OccurencesGridFields.PROPERTIES.getId(), propertiesHtml);
		
		data.set(OccurencesGridFields.RECORDED_BY.getId(), occurrence.getRecordedBy());
		data.set(OccurencesGridFields.IDENTIFIED_BY.getId(), occurrence.getIdentifiedBy());
		data.set(OccurencesGridFields.EVENT_DATE.getId(), occurrence.getEventDate());
		data.set(OccurencesGridFields.MODIFIED.getId(), occurrence.getModified());
		data.set(OccurencesGridFields.SCIENTIFIC_NAME.getId(), occurrence.getScientificName());
		data.set(OccurencesGridFields.KINGDOM.getId(), occurrence.getKingdom());
		data.set(OccurencesGridFields.FAMILY.getId(), occurrence.getFamily());
		data.set(OccurencesGridFields.LOCALITY.getId(), occurrence.getLocality());
		data.set(OccurencesGridFields.COUNTRY.getId(), occurrence.getCountry());
		data.set(OccurencesGridFields.CITATION.getId(), occurrence.getCitation());
		data.set(OccurencesGridFields.DECIMAL_LATITUDE.getId(), occurrence.getDecimalLatitude());
		data.set(OccurencesGridFields.DECIMAL_LONGITUDE.getId(), occurrence.getDecimalLongitude());
		data.set(OccurencesGridFields.COORDINATE_UNCERTAINTY_IN_METERS.getId(), occurrence.getCoordinateUncertaintyInMeters());
		data.set(OccurencesGridFields.MAX_DEPTH.getId(), occurrence.getMaxDepth());
		data.set(OccurencesGridFields.MIN_DEPTH.getId(), occurrence.getMinDepth());
		data.set(OccurencesGridFields.BASIS_OF_RECORD.getId(), occurrence.getBasisOfRecord());
		
		
		return data;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String getInfo() {
		return "OccurrencesDataSource";
	}
}
