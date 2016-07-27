package org.gcube.application.aquamaps.aquamapsportlet.client;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.portlets.widgets.gcubelivegrid.client.data.BufferedJsonReader;
import org.gcube.portlets.widgets.gcubelivegrid.client.data.BufferedStore;

import com.gwtext.client.core.SortDir;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;

public class Stores {


	public static BufferedStore jobStore(){
		UrlParam[] pars=new UrlParam[]{
				new UrlParam(Tags.SETTINGS,"jobs")				
		};
		return initStore(AquaMapsPortletCostants.servletUrl.get("jobs"),pars,RecordDefinitions.submittedRecordDef,false);
	}	
	public static BufferedStore toCreateObjectsStore(ClientObjectType toShowType){
		UrlParam[] pars=new UrlParam[]{
			new UrlParam(Tags.AQUAMAPS_TYPE,toShowType.toString())	
		};
		RecordDef recordDefinition=toShowType.equals(ClientObjectType.Biodiversity)?RecordDefinitions.biodiversityObjectRecordDef:RecordDefinitions.distributionObjectRecordDef;
		return initStore(AquaMapsPortletCostants.servletUrl.get("jobs"),pars,recordDefinition,false);
	}
	
	
	public static BufferedStore submittedStore(){
		return initStore(AquaMapsPortletCostants.servletUrl.get("jobs"),new UrlParam[]{},RecordDefinitions.submittedRecordDef,false);
	}	
	public static BufferedStore objectBasketStore(String title){
		UrlParam[] pars=new UrlParam[]{
				new UrlParam(Tags.AQUAMAPS_TITLE,title),
				new UrlParam(Tags.SELECTION_attribute_NAME,Tags.SELECTED_Species),
		};		
		return initStore(AquaMapsPortletCostants.servletUrl.get("selection"),pars,RecordDefinitions.specRecordDef,false);}
	public static BufferedStore	selectedSpeciesStore() {
		UrlParam[] pars=new UrlParam[]{				
				new UrlParam(Tags.SELECTION_attribute_NAME,Tags.SELECTED_Species),
		};
		return initStore(AquaMapsPortletCostants.servletUrl.get("selection"),pars,RecordDefinitions.specRecordDef,false);}
	public static BufferedStore	availableSpeciesStore(){
		UrlParam[] pars=new UrlParam[]{
		};
		return initStore(AquaMapsPortletCostants.servletUrl.get("species"),pars,RecordDefinitions.specRecordDef,false);}
	public static BufferedStore	selectedAreasStore(){
		UrlParam[] pars=new UrlParam[]{				
				new UrlParam(Tags.SELECTION_attribute_NAME,Tags.SELECTED_AREAS),
		};
		return initStore(AquaMapsPortletCostants.servletUrl.get("selection"),pars,RecordDefinitions.areaRecordDef,false);}
	
	
	public static BufferedStore occurrenceCellsStore(String speciesId){
		UrlParam[] pars=new UrlParam[]{				
				new UrlParam(SpeciesFields.speciesid+"",speciesId),
		};
		return initStore(AquaMapsPortletCostants.servletUrl.get("occurrenceCells"),pars,RecordDefinitions.cellRecordDef,true);}
	

	public static BufferedStore smartAvailableAreasStore(){
		UrlParam[] pars=new UrlParam[]{				
//				new UrlParam(Tags.showFAO,enableFAO),
//				new UrlParam(Tags.showEEZ,enableEEZ),
//				new UrlParam(Tags.showLME,enableLME)
		};
		return initStore(AquaMapsPortletCostants.servletUrl.get("area"),pars,RecordDefinitions.areaRecordDef,false);}
	
	
	public static BufferedStore initStore(String url,UrlParam[] extraParams,RecordDef recordDef,boolean autoLoad){
		
		BufferedJsonReader reader;
		reader = new BufferedJsonReader(Tags.DATA,recordDef);		  
		reader.setTotalProperty(Tags.TOTAL_COUNT);
		reader.setVersionProperty("version");
		BufferedStore toReturn=new BufferedStore(new HttpProxy(url),reader, extraParams, new SortState(recordDef.getFields()[0].getName(), SortDir.ASC),
	            true) ;
		toReturn.setAutoLoad(autoLoad);
		toReturn.setBufferSize(100);        
		return toReturn;
	}
}
