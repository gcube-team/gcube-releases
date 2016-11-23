package org.gcube.application.aquamaps.aquamapsportlet.servlet.utils;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBInterface;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {

	public static final String xmlHeader="<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private static final String DEFAULT_USER="fabio.sinibaldi";
//	private static final String DEFAULT_SCOPE="/d4science.research-infrastructures.eu/gCubeApps";
	private static final String DEFAULT_SCOPE="/gcube/devsec";

	


	public static String dateFormatter(Date time){
		return sdf.format(time);

	}

//	public String getGeoServer(GCUBEScope scope) throws ParameterNotFoundException, ScopeNotFoundException{
////		String geoServerUrl="http://geoserver.d4science-ii.research-infrastructures.eu:8080";
////		return geoServerUrl;
//		return config.getGeoServers(scope).get(0).getEntryPoint();
//	}


	public static synchronized ASLSession getSession(HttpSession httpSession)throws Exception
	{

		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession toReturn=null;
		if(user==null) toReturn=getDefaultSession(httpSession.getId());
		else{
			String sessionID = httpSession.getId();
			
			toReturn=SessionManager.getInstance().getASLSession(sessionID, user);
		}


		try{
			logger.debug("Trying to initialize session for user : "+user);
			if(!toReturn.hasAttribute(Tags.showFAO))toReturn.setAttribute(Tags.showFAO,true);
			if(!toReturn.hasAttribute(Tags.showLME))toReturn.setAttribute(Tags.showLME,false);
			if(!toReturn.hasAttribute(Tags.showEEZ))toReturn.setAttribute(Tags.showEEZ,false);

			if(!toReturn.hasAttribute(Tags.lastFetchedBasket))toReturn.setAttribute(Tags.lastFetchedBasket, new ArrayList<Integer>());

			if(!toReturn.hasAttribute(ResourceType.HCAF+"")||!toReturn.hasAttribute(ResourceType.HSPEC+"")||!toReturn.hasAttribute(ResourceType.HSPEN+"")){
				for(Field f:dataManagement().build().getDefaultSources()){
					if(f.name().equals(ResourceType.HCAF+""))toReturn.setAttribute(ResourceType.HCAF+"",f.getValueAsInteger());
					else if(f.name().equals(ResourceType.HSPEN+""))toReturn.setAttribute(ResourceType.HSPEN+"",f.getValueAsInteger());
					else if(f.name().equals(ResourceType.HSPEC+""))toReturn.setAttribute(ResourceType.HSPEC+"",f.getValueAsInteger());
				}	
			}
			logger.debug("Completed");
		}catch(Exception e){
			
			logger.warn("Couldn't complete, probably session already existing");
		}

		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public static void addFetchedBasketId(ASLSession session,int objId)throws Exception{
		List<Integer> fetchedBaskets=(List<Integer>) session.getAttribute(Tags.lastFetchedBasket);
		if(!fetchedBaskets.contains(objId))fetchedBaskets.add(objId);
		session.setAttribute(Tags.lastFetchedBasket, fetchedBaskets);
	}




	public static Envelope loadCustomizations(Envelope toUpdate,String speciesId,
			ASLSession session) throws Exception{
		logger.debug("Loading customization for psecies "+speciesId);
		Map<String,Perturbation> perts=JSONUtils.JSONtoPert(DBManager.getInstance(session.getScope()).getPerturbation(speciesId, session.getUsername()));
		for(EnvelopeFields envField: EnvelopeFields.values())
			for(HspenFields hField : toUpdate.getValueNames(envField) ){
				if(perts.containsKey(hField+"")) {
					Float value=Float.parseFloat(perts.get(hField+"").getPerturbationValue());
					logger.debug("Setting customization "+envField+","+hField+","+value);
					toUpdate.setValue(envField, hField, value);
				}
			}

		return toUpdate;
	}



	public static Job loadSettings(ASLSession session, boolean fetchSpeciesIDs, boolean fetchResources) throws Exception{
		Job job= new Job();
		logger.debug("load settings..");
		ScopeProvider.instance.set(session.getScope().toString());
		DataManagement dm=dataManagement().build();

		if(fetchResources){
			try{
				Integer hspenId=(Integer) session.getAttribute(ResourceType.HSPEN+"");
				Integer hspecId=(Integer) session.getAttribute(ResourceType.HSPEC+"");
				Integer hcafId=(Integer) session.getAttribute(ResourceType.HCAF+"");

				job.setSourceHSPEN(dm.loadResource(hspenId));
				job.setSourceHSPEC(dm.loadResource(hspecId));
				job.setSourceHCAF(dm.loadResource(hcafId));
			}catch(Exception e){
				logger.error("Unable to fetch resource details for job belonging to session : "+session.getUsername(), e);
			}
		}
		DBInterface db=DBManager.getInstance(session.getScope());

		job.getSelectedAreas().addAll(db.getAreaSelection(session.getUsername()));

		job.setAuthor(session.getUsername());

		if(fetchSpeciesIDs){

			List<String> speciesIds= db.getUserBasketIds(session.getUsername());

			for(String id:speciesIds){
				Species toAdd=new Species(id);
				job.getSelectedSpecies().add(toAdd);
				Map<String,Perturbation> speciesPerts=JSONUtils.JSONtoPert(db.getPerturbation(toAdd.getId(), session.getUsername()));
				if(speciesPerts!=null && !speciesPerts.isEmpty())job.getEnvelopeCustomization().put(toAdd.getId(),speciesPerts);
			}
		}
		for(ClientObject obj:db.getObjects(session.getUsername())){

			AquaMapsObject toAdd=new AquaMapsObject();
			toAdd.setAuthor(session.getUsername());
			toAdd.setName(obj.getName());
			toAdd.getBoundingBox().parse((obj.getBoundingBox()+""));
			toAdd.setThreshold(obj.getThreshold());
			toAdd.setType(ObjectType.valueOf(obj.getType()+""));
			toAdd.setGis(obj.getGis());
			toAdd.setAlgorithmType(job.getSourceHSPEC().getAlgorithm());
			if(toAdd.getType().equals(ObjectType.Biodiversity)){
				List<String> ids=db.getObjectBasketIds(session.getUsername(),toAdd.getName());
				for(String id: ids)toAdd.getSelectedSpecies().add(new Species(id));
			}else if(toAdd.getType().equals(ObjectType.SpeciesDistribution)){
				toAdd.getSelectedSpecies().add(new Species(obj.getSelectedSpecies().getValue()));
			}
			job.getAquaMapsObjectList().add(toAdd);
		}	
		return job;
	}

	public static SettingsDescriptor getStats(Job job)throws Exception{

		int biodCount=0;
		int distrCount=0;
		boolean biodiversityBasketSizeCheck=true;
		for(AquaMapsObject obj: job.getAquaMapsObjectList()){
			switch(obj.getType()){
			case Biodiversity: {
				if(obj.getSelectedSpecies().size()<2){
					biodiversityBasketSizeCheck=false;
					logger.debug("Found invalid Biodiversity object "+obj.getName()+" size : "+obj.getSelectedSpecies().size());
				}
				biodCount++;	
				break;
			}
			case SpeciesDistribution : distrCount++; break;
			}
		}

		SettingsDescriptor toReturn=new SettingsDescriptor();
		toReturn.setBiodiversityObjectsCount(biodCount);
		toReturn.setCreateGroup(false);
		toReturn.setHspecId(job.getSourceHSPEC().getSearchId());
		toReturn.setHspecTitle(job.getSourceHSPEC().getTitle());
		toReturn.setNumberOfCustomizedSpecies(job.getEnvelopeCustomization().size());
		toReturn.setSelectedAreas(job.getSelectedAreas().size());
		toReturn.setSpeciesDistributionObjectCount(distrCount);
		toReturn.setSpeciesInBasket(job.getSelectedSpecies().size());
		toReturn.setToSubmitName(job.getName());
		//Submit check...
		if(job.getAquaMapsObjectList().size()==0)toReturn.setSubmittable(new Msg(false,"No AquaMaps objects to create"));
		else if(!biodiversityBasketSizeCheck) toReturn.setSubmittable(new Msg(false,"Biodiversity objects must be bound to at least 2 species"));
		else if(toReturn.getToSubmitName()==null) toReturn.setSubmittable(new Msg(false,"Please specify a title for this job"));
		else toReturn.setSubmittable(new Msg(true,"OK"));
		return toReturn;
	}

	@Deprecated
	private static ASLSession getDefaultSession(String id)throws Exception{
		ASLSession toReturn=null;
		String sessionID = id;
		String user = DEFAULT_USER;
		toReturn=SessionManager.getInstance().getASLSession(sessionID, user);
		String scope=DEFAULT_SCOPE;
		toReturn.setScope(scope);
		return toReturn;
	}

	public static Collection<String> getAvailableScopes() throws Exception {
		ArrayList<String> toReturn=new ArrayList<String>();
		String infrastructureScope="/"+PortalContext.getConfiguration().getInfrastructureName();
		toReturn.add(infrastructureScope);
		
		for(String vo:PortalContext.getConfiguration().getVOs())toReturn.add(infrastructureScope+"/"+vo);
		

		
		return toReturn;
	}
	
	
	/**
	 * Returns the enclosing VO scope in case currentScope is a VRE, otherwise the passed scope itself    
	 * 
	 * @param currentScope
	 * @return
	 */
	public static String removeVRE(String currentScope){
		if(currentScope.matches("/(.)*/(.)*/(.)*")) return currentScope.substring(0, currentScope.lastIndexOf('/'));
		return currentScope;
	}
}
