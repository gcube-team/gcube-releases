package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.AquaMapsPortletLocalService;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientArea;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.JSONUtils;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FilterType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class AquaMapsPortletLocalImpl extends RemoteServiceServlet implements AquaMapsPortletLocalService{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2337999405162440998L;

	private static final Logger logger = LoggerFactory.getLogger(AquaMapsPortletLocalImpl.class);

	





	@Override
	public Msg setSpeciesFilter(List<ClientFilter> filter,String filterSummary) throws Exception {
		try{			

			ASLSession session = Utils.getSession(this.getThreadLocalRequest().getSession()); 
			
			logger.debug("Setting characteristic filters");
			ArrayList<Filter> toSet=new ArrayList<Filter>();
			
			
//			for(ClientField f:characteristicFilter)
//				toSet.add(new Filter(FilterType.is, new Field(f.getName(),f.getValue(),FieldType.valueOf(f.getType()+""))));
//				
//			for(ClientFilter f:nameFilter)
//				toSet.add(new Filter(FilterType.valueOf(f.getType()+""),new Field(f.getField().getName(),f.getField().getValue(),FieldType.valueOf(f.getField().getType()+""))));

			for(ClientFilter f:filter)
				toSet.add(new Filter(FilterType.valueOf(f.getType()+""),new Field(f.getField().getName(),f.getField().getValue(),FieldType.valueOf(f.getField().getType()+""))));
			
			session.setAttribute(Tags.SPECIES_FILTER, toSet);
			
			logger.debug("Setting filter summary");
			session.setAttribute(Tags.filterSummary, filterSummary);
			return new Msg(true,"Setted "+toSet.size()+" filters ");
		}catch(Exception e){
			logger.error("Set Species Filter exception", e);
			throw new Exception(e.getMessage());
		}
	}


	@Override
	public Msg clearSpeciesFilter() throws Exception {
		try{
			logger.debug("Clear species filter");

			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			session.removeAttribute(Tags.SPECIES_FILTER);
			session.removeAttribute(Tags.filterSummary);
			return new Msg(true,"Species Filter cleared");
		}catch(Exception e){
			logger.error("Clear Species Filter exception", e);
			throw new Exception(e.getMessage());
		}
	}





	@Override
	public Msg addToAreaSelection(List<ClientArea> toAdd) throws Exception {
		try{
			logger.debug("addtoAreaSelection");
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			List<Area> translated=new ArrayList<Area>();
			
			if(toAdd.size()>0)
				for(ClientArea cArea:toAdd)
					translated.add(new Area(AreaType.valueOf(cArea.getType()+""),cArea.getCode(),cArea.getName()));
			else{
				boolean showFAO=(Boolean)session.getAttribute(Tags.showFAO);
				boolean showLME=(Boolean)session.getAttribute(Tags.showLME);
				boolean showEEZ=(Boolean)session.getAttribute(Tags.showEEZ);
				translated=DBManager.getInstance(session.getScope()).getAreasByType(showFAO, showEEZ, showLME);
			}
			
			int count=DBManager.getInstance(session.getScope()).addToAreaSelection(session.getUsername(), translated);
			
			logger.debug("Added "+count+" areas");
			return new Msg(true,"Added : "+count);
		}catch(Exception e){
			logger.error("AddArea exception exception", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Msg addAllFilteredSpecies(int totalCount,String sortColumn,String sortDir)throws Exception{
		try{
			logger.debug("addAllFilteredSpecies...");			
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			
			(new SpeciesImporterThread(totalCount, session,  sortColumn, sortDir)).start();
			
			return new Msg(true,"Started importing process species");
		}catch(Exception e){
			logger.error("Unable to import all filtered species", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Msg addToSpeciesSelection(List<String> toAdd) throws Exception {
		try{
			logger.debug("add to species selection "+(toAdd==null?"ALL":toAdd.size()+" element(s)") );

			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			int count=DBManager.getInstance(session.getScope()).addToUserBasket(toAdd, session.getUsername());
			return new Msg(true,"Added to basket : "+count);
		}catch(Exception e){
			logger.error("Unable to add area to selection", e);
			throw new Exception(e.getMessage());
		}
	}





	@Override
	public Msg removeFromAreaSelection(List<ClientArea> toRemove)
	throws Exception {
		try{
			logger.debug("remove from area selection "+(toRemove==null?"ALL":toRemove.size()+" element(s)") );
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			List<Area> translated=new ArrayList<Area>();
			for(ClientArea cArea:toRemove)
				translated.add(new Area(AreaType.valueOf(cArea.getType()+""),cArea.getCode(),cArea.getName()));
			
			int count= DBManager.getInstance(session.getScope()).removeFromAreaSelection(session.getUsername(), translated);

			logger.debug("Removed "+count+" areas");
			return new Msg(true,"Removed "+count+" areas");
		}catch(Exception e){
			logger.error("Unable to remove from area selection", e);
			throw new Exception(e.getMessage());
		}
	}





	@Override
	public Msg setEnvelopeCustomization(String ids, Map<SpeciesFields,Float> perturbations)
	throws Exception {
		try{
			logger.debug("Set envelope customization");
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());		
			

			DBManager.getInstance(session.getScope()).setPerturbation(ids, session.getUsername(), JSONUtils.pertMapToJSON(perturbations));
			return new Msg(true,"Committed "+perturbations.size()+" customization(s) for selected species");
		}catch(Exception e){
			logger.error("Unable to set Envelope customization for species "+ids, e);
			throw new Exception(e.getMessage());
		}
	}



	
	@Override
	public Msg addToObjectBasket(List<String> ids, String objTitle) throws Exception {
		try{
			logger.debug("Add selection "+(ids==null?"[EMPTY]":ids.size()+"")+
					" from "+(objTitle==null?"Session Basket ":objTitle));
			
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			int count = DBManager.getInstance(session.getScope()).addToObjectBasket(ids, session.getUsername(), objTitle);
			return new Msg(true,"Added "+count+" species to "+objTitle+" basket");
		}catch(Exception e){
			logger.error("Unable to add to object basket", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Msg removeObject(String title,ClientObjectType type) throws Exception {
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			if(title!=null){
				int count=DBManager.getInstance(session.getScope()).removeObjectByTitle(session.getUsername(), title);
				return new Msg(true,"Removed "+count+" by title ( "+title+" )");
			}else {
				int count = DBManager.getInstance(session.getScope()).removeObjectByType(session.getUsername(), type);
				return new Msg(true,"Removed "+count+" By type ( "+type+" )");
			}
		}catch(Exception e){
			logger.error("Unable to remove object ", e);
			throw new Exception(e.getMessage());
		}
	}


	@Override
	public Msg filterSubmitted(String parameter, String value) throws Exception {
		try{
			logger.debug("Filtering submitted by "+parameter+" = "+value);
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			if(value!=null)session.setAttribute(parameter, value);
			else session.removeAttribute(parameter);
			return new Msg(true, "Setted "+parameter+" = "+value);
		}catch(Exception e){
			logger.error("Unable to filter submitted", e);
			throw new Exception(e.getMessage());
		}
	}



	@Override
	public Msg updateObject(String oldTitle, String title, ClientObjectType type,
			String bBox, Float threshold, Boolean gis) throws Exception {
		try{
			logger.debug("Updating obj "+oldTitle+"("+type+") to "+title+", "+bBox+", "+threshold+", "+gis);
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			DBManager.getInstance(session.getScope()).updateObject(session.getUsername(), oldTitle, title, type,bBox,threshold,gis);
			return new Msg(true,"Updated "+title);
		}catch(Exception e){
			logger.error("Unable to update object ", e);
			throw new Exception(e.getMessage());
		}
	}
	@Override
	public SettingsDescriptor getStats() throws Exception {
		try{
			logger.debug("GET STATS ...");
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			return Utils.getStats(Utils.loadSettings(session, true,true));
		}catch(Exception e){
			logger.error("Unable to load session preferences :", e);
			throw new Exception("Unable to load session preferences :"+e.getMessage());
		}
	}
	@Override
	public Msg changeGisById(List<String> ids) throws Exception {
		try{
			logger.debug("Changing gis to "+ids.size()+" object(s) ");
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			int count=DBManager.getInstance(session.getScope()).changeGis(session.getUsername(), ids);
			return new Msg(true,"Done "+count+"changes");
		}catch(Exception e){
			logger.error("Unable to change gis by id", e);
			throw new Exception(e.getMessage());
		}
	}
	@Override
	public Msg changeGisByType(String type) throws Exception {
		try{
			logger.debug("Change gis by type "+type);
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			List<AquaMapsObject> list= Utils.loadSettings(session, false,false).getAquaMapsObjectList();
			ObjectType objType= ObjectType.valueOf(type);
			ArrayList<String> ids=new ArrayList<String>();
			for(AquaMapsObject obj:list)
				if(obj.getType().equals(objType)) ids.add(obj.getName());
			DBManager.getInstance(session.getScope()).changeGis(session.getUsername(), ids);
			return new Msg(true,"Done "+ids.size()+" changes");
		}catch(Exception e){
			logger.error("Unable to change gis by id", e);
			throw new Exception(e.getMessage());
		}
	}
	@Override
	public Msg setSource(int id, ClientResourceType ctype) throws Exception {
		try{
		logger.debug("Setting source "+id+" "+ctype);
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		session.setAttribute(ResourceType.valueOf(ctype+"")+"", new Integer(id));
		if(ctype.equals(ClientResourceType.HSPEC)){
			
			Resource selectedHSPEC=dataManagement().build().loadResource(id);
			session.setAttribute(ResourceType.HSPEN+"", selectedHSPEC.getSourceHSPENIds().get(0));
			session.setAttribute(ResourceType.HCAF+"", selectedHSPEC.getSourceHCAFIds().get(0));
		}
		return new Msg(true,"Done");
		}catch(Exception e){
			logger.error("Unable to set resource "+id+" ("+ctype+")");
			throw new Exception("Unable to set resource");
		}
	}


	@Override
	public Msg filterAreas(Boolean enableFao, Boolean enableLME,
			Boolean enableEEZ) throws Exception {
		try{
		logger.debug("SETTING AREA FILTERS FAO :"+enableFao+" LME "+enableLME+" EEZ "+enableEEZ);
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		session.setAttribute(Tags.showEEZ, enableEEZ);
		session.setAttribute(Tags.showLME, enableLME);
		session.setAttribute(Tags.showFAO, enableFao);
		return new Msg(true,"Done");
		}catch(Exception e){
			logger.error("Unable to filter areas",e);
			throw new Exception (e.getMessage());
		}
	}


	@Override
	public Msg createObjectsBySelection(ClientObjectType type,
			List<String> ids, String title) throws Exception {
		try{
		logger.debug("Create object(s) by selection "+(ids==null?"[EMPTY]":ids.size()+"")+" type is "+type);
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		int created=DBManager.getInstance(session.getScope()).createObjectsBySelection(ids, title, type, 0.5f, new BoundingBox().toString(), session.getUsername());
		return new Msg(true,"Created "+created+" object(s) to submit");
		}catch(Exception e ){
			logger.error("Unable to create objects ",e);
			throw new Exception(e.getMessage());
		}
	}


	@Override
	public Msg removeSelectionFromBasket(String title, List<String> speciesIds)
			throws Exception {
		try{
		logger.debug("Removing selection "+(speciesIds==null?"[EMPTY]":speciesIds.size()+"")+
				" from "+(title==null?"Session Basket ":title));
		
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		int count=0;
		if(title==null)
			count= DBManager.getInstance(session.getScope()).removeFromBasket(speciesIds, session.getUsername());
		else count=DBManager.getInstance(session.getScope()).removeFromObjectBasket(speciesIds, session.getUsername(), title);
		return new Msg(true,"Affected "+count+" species");
		}catch(Exception e ){
			logger.error("Unable to remove selection ",e);
			throw new Exception(e.getMessage());
		}
	}


	@Override
	public Msg clearEnvelopeCustomization(String id) throws Exception {
		try{
			logger.debug("Clear envelope customization");
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());		

			DBManager.getInstance(session.getScope()).clearPerturbation(id, session.getUsername());
			return new Msg(true,"Cleared customization(s) for selected species");
		}catch(Exception e){
			logger.error("Unable to clear Envelope customization for species "+id, e);
			throw new Exception(e.getMessage());
		}
	}


	@Override
	public Integer getImportProgress() throws Exception {
		try{
			logger.debug("Clear envelope customization");
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());		
			return (Integer) session.getAttribute(Tags.IMPORT_PROGRESS);
		}catch(Exception e){			
			throw new Exception(e.getMessage());
		}
	}
	
	





}
