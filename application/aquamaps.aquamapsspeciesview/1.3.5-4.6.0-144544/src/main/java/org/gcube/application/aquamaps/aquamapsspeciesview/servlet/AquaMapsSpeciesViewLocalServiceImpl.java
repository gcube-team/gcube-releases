package org.gcube.application.aquamaps.aquamapsspeciesview.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;
import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;
import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Maps;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FilterType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.AquaMapsSpeciesViewLocalService;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.EnumSerializationForcer;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ClientResource;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.Response;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesFilter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesSearchDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveRequest;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.save.SaveManager;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.ModelTranslation;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.Utils;
import org.gcube.application.framework.core.session.ASLSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class AquaMapsSpeciesViewLocalServiceImpl extends RemoteServiceServlet implements AquaMapsSpeciesViewLocalService{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2337999405162440998L;

	private static final Logger logger = LoggerFactory.getLogger(AquaMapsSpeciesViewLocalServiceImpl.class);
	private static final String layersPath=File.separator+"config"+File.separator+"layers.xml";

	@Override
	public Response setGenericSearchFilter(String searchValue)
			throws Exception {
		logger.debug("SetGenericSearchFilter "+searchValue);
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			SpeciesSearchDescriptor descriptor=(SpeciesSearchDescriptor) session.getAttribute(Tags.SPECIES_SEARCH_FILTER);
			descriptor.setGenericSearchFieldValue((searchValue!=null&&!searchValue.isEmpty())?searchValue:null);
			session.setAttribute(Tags.SPECIES_SEARCH_FILTER, descriptor);
			return new Response(true);
		}catch(Exception e){
			logger.error("Set generic search filter : "+searchValue,e);
			return new Response(false);
		}
	}


	@Override
	public SpeciesSearchDescriptor getFilterSettings() throws Exception{
		logger.debug("GetFilterSettings");
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			SpeciesSearchDescriptor descriptor=(SpeciesSearchDescriptor) session.getAttribute(Tags.SPECIES_SEARCH_FILTER);
			return descriptor;
		}catch(Exception e){
			logger.error("get filter settings ",e);
			throw new Exception("Session might be expired");
		}
	}


	@Override
	public Response setAdvancedSpeciesFilter(List<SpeciesFilter> updatedList)
			throws Exception {
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			SpeciesSearchDescriptor descriptor=(SpeciesSearchDescriptor) session.getAttribute(Tags.SPECIES_SEARCH_FILTER);
			descriptor.getAdvancedFilterList().clear();
			descriptor.getAdvancedFilterList().addAll(updatedList);
			return new Response(true);
		}catch(Exception e){
			logger.error("Set advanced Species filter ",e);
			return new Response(false);
		}
	}


	@Override
	public Response dummyOperation(EnumSerializationForcer forcer)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SettingsDescriptor getSessionSettings() throws Exception {
		try{
			SettingsDescriptor toReturn=new SettingsDescriptor();
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			toReturn.setSpeciesSearchDescriptor((SpeciesSearchDescriptor) session.getAttribute(Tags.SPECIES_SEARCH_FILTER));
			toReturn.setSelectedHspen(ModelTranslation.toClient(dataManagement().build().loadResource((Integer) session.getAttribute(ClientResourceType.HSPEN+""))));
//			if(session.hasAttribute(name))
//			for(ClientResourceType t:ClientResourceType.values())
//				if(session.hasAttribute(t+""))
//				toReturn.getResources().put(t, ModelTranslation.toClient(dataManagement().build().loadResource((Integer) session.getAttribute(t+""))));
			
			//FIXME complete Settings descriptor fields
			return toReturn;
		}catch(Exception e){
			logger.error("",e);
			throw new Exception("Session might be expired");
		}
	}


	@Override
	public Response setSource(ClientResource selected) throws Exception {
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			session.setAttribute(selected.getType()+"",selected.getSearchId());
			return new Response(true);
		}catch(Exception e){
			logger.error("Set source ",e);
			return new Response(false);
		}
	}

	@Override
	public Response retrieveMapPerSpeciesList(List<String> species)
			throws Exception {
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			
			DBManager.getInstance(session.getScope()).cleanMaps(session.getUsername());
			List<CompoundMapItem> retrieved=new ArrayList<CompoundMapItem>();
				
			for(AquaMap map:publisher().build().getMapsBySpecies(species.toArray(new String[species.size()]), true, true, null)){
				retrieved.add(ModelTranslation.toClient(map));
			}
			
			int count=DBManager.getInstance(session.getScope()).putMaps(session.getUsername(), retrieved);
			Response toReturn=new Response(true);
			toReturn.getAdditionalObjects().put(Tags.RETRIEVED_MAPS, count+"");
			return toReturn;
		}catch(Exception e){
			logger.error("Unable to Retrieve Maps from selection "+species,e);
			Response toReturn=new Response(false);
			toReturn.getAdditionalObjects().put(Tags.ERROR_MESSAGE, "Session might be expired");
			return toReturn;
		}
	}
	@Override
	public SaveOperationProgress getSaveProgress() throws Exception {
		return SaveManager.getProgress(Utils.getSession(this.getThreadLocalRequest().getSession()));
	}
	
	@Override
	public SaveOperationProgress saveOperationRequest(SaveRequest request)
			throws Exception {
		return SaveManager.startSaving(request,Utils.getSession(this.getThreadLocalRequest().getSession()));
	}
	
//	@Override
//	public List<String> getDefaultLayers() throws Exception {
//		try{
//			return (List<String>) AquaMapsXStream.deSerialize(getServletContext().getRealPath("")+layersPath);
//		}catch(Exception e){
//			logger.error("Unable to read default layers",e);
//			return new ArrayList<String>();
//		}
//	}


	@Override
	public String loadSpeciesByMapsId(String id) throws Exception {
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		Maps am=maps().build();
		AquaMapsObject object=am.loadObject(Integer.parseInt(id));
		String speciesId=object.getSelectedSpecies().iterator().next().getId();
		int hspenId=(Integer) session.getAttribute(ResourceType.HSPEN+"");
		String jsonSource=am.getJSONSpecies(hspenId, Arrays.asList(new Filter[]{
				new Filter(FilterType.is,new Field(SpeciesOccursumFields.speciesid+"", speciesId,FieldType.STRING))
		}), new ArrayList<Filter>(), new PagedRequestSettings(1, 0, SpeciesOccursumFields.speciesid+"",OrderDirection.ASC ));
		String jsonObjString=jsonSource.substring(jsonSource.indexOf('[')+1,jsonSource.lastIndexOf(']'));
		JSONObject obj=new JSONObject(jsonObjString);
		return obj.getString(SpeciesOccursumFields.genus+"")+"_"+obj.getString(SpeciesOccursumFields.species+"");
	}
}
