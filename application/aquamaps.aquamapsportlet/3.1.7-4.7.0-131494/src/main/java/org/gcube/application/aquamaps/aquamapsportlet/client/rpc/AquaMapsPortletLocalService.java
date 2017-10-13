package org.gcube.application.aquamaps.aquamapsportlet.client.rpc;

import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientArea;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("AquaMapsPortletLocalService")
public interface AquaMapsPortletLocalService extends RemoteService {


	Msg setSpeciesFilter(List<ClientFilter> Filter, String filterSummary)throws Exception;
	
	public Msg clearSpeciesFilter()throws Exception; 

	
	
	public Msg setSource(int id,ClientResourceType type)throws Exception;
	
	
	
	
	public Msg addToSpeciesSelection(List<String> toAdd)throws Exception;
	public Msg addAllFilteredSpecies(int totalCount,String sortColumn,String sortDir)throws Exception;
	
	
	/**
	 * 
	 * @param title if null clears session basket
	 * @return
	 * @throws Exception
	 */
	
	public Msg removeSelectionFromBasket(String title, List<String> speciesIds)throws Exception;
	public Msg addToObjectBasket(List<String> ids,String objTitle)throws Exception;
	
	public Msg setEnvelopeCustomization(String ids,Map<SpeciesFields,Float> perturbations)throws Exception;
	public Msg clearEnvelopeCustomization(String id)throws Exception;
	
	public Msg addToAreaSelection(List<ClientArea> toAdd)throws Exception;
	public Msg removeFromAreaSelection(List<ClientArea> toRemove) throws Exception;
	
	public SettingsDescriptor getStats()throws Exception;
	
	
	public Msg createObjectsBySelection(ClientObjectType type, List<String> ids, String title)throws Exception;
	
	public Msg removeObject(String title,ClientObjectType type)throws Exception;
	public Msg updateObject(String oldTitle,String title, ClientObjectType type,String bBox,Float threshold,Boolean gis)throws Exception;
	public Msg changeGisByType(String type)throws Exception;
	public Msg changeGisById(List<String> ids)throws Exception;
	
	
	public Msg filterSubmitted(String parameter,String value)throws Exception;	
	
	public Msg filterAreas(Boolean enableFao, Boolean enableLME, Boolean enableEEZ)throws Exception;
	
	public Integer getImportProgress()throws Exception;
	
}
