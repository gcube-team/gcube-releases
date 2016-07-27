package org.gcube.application.aquamaps.aquamapsspeciesview.client.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.FilterCategory;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFilterOperator;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class AquaMapsSpeciesViewConstants {

	public static String COMMONGUIDIV="SPECIES_VIEW";
	public static final Map<String,String> servletUrl=new HashMap<String, String>();
	public static final Map<ClientResourceType,String> resourceNames=new HashMap<ClientResourceType, String>();
	public static final Map<SpeciesFields,String> speciesFieldsNames=new HashMap<SpeciesFields, String>();
	public static final Map<String,String> mapFieldsNames=new HashMap<String, String>();
	public static DateTimeFormat timeFormat=DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_LONG);
	public static final Map<ClientFieldType,Object> defaultFilterValues=new HashMap<ClientFieldType, Object>();
	public static final Map<SpeciesFields,ClientFieldType> fieldTypes=new HashMap<SpeciesFields, ClientFieldType>();
	public static final HashMap<ClientFieldType,ArrayList<ClientFilterOperator>> operatorsPerFieldType=new HashMap<ClientFieldType, ArrayList<ClientFilterOperator>>();
	
//*********************** SPECIES FIELDS PER CATEGORY
	
	public static final SpeciesFields[] nameSpeciesFields={
		SpeciesFields.fbname,
		SpeciesFields.scientific_name,
		SpeciesFields.english_name,
		SpeciesFields.french_name,
		SpeciesFields.spanish_name,
	};

	public static final SpeciesFields[] codesSpeciesFields={
		SpeciesFields.speciesid,
		SpeciesFields.speccode,
	};

	public static final SpeciesFields[] characteristicsSpeciesFields={
		SpeciesFields.deepwater,
		SpeciesFields.m_mammals,
		SpeciesFields.angling,
		SpeciesFields.diving,
		SpeciesFields.dangerous,
		SpeciesFields.m_invertebrates,
		SpeciesFields.algae,
		SpeciesFields.seabirds,
		SpeciesFields.freshwater,
	};

	public static final SpeciesFields[] taxonomySpeciesFields={
		SpeciesFields.kingdom,
		SpeciesFields.phylum,
		SpeciesFields.classcolumn,
		SpeciesFields.ordercolumn,
		SpeciesFields.familycolumn,
		SpeciesFields.genus,
		SpeciesFields.species,
	};
	
	
	
	public static void init(String sessionId)
	{
		servletUrl.put(Tags.phylogenyServlet,GWT.getModuleBaseURL()+Tags.phylogenyServlet+";jsessionid=" + sessionId);
		servletUrl.put(Tags.resourceServlet,GWT.getModuleBaseURL()+Tags.resourceServlet+";jsessionid=" + sessionId);
		servletUrl.put(Tags.speciesServlet,GWT.getModuleBaseURL()+Tags.speciesServlet+";jsessionid=" + sessionId);
		servletUrl.put(Tags.imageServlet,GWT.getModuleBaseURL()+Tags.imageServlet);
		servletUrl.put(Tags.mapServlet,GWT.getModuleBaseURL()+Tags.mapServlet+";jsessionid=" + sessionId);
		
		
		servletUrl.put(Tags.localService,GWT.getModuleBaseURL()+Tags.localService+";jsessionid=" + sessionId);
		
		
		
		resourceNames.put(ClientResourceType.HCAF, "Environmental Data ("+ClientResourceType.HCAF+")");
		resourceNames.put(ClientResourceType.HSPEC, "Simulation Data ("+ClientResourceType.HSPEC+")");
		resourceNames.put(ClientResourceType.HSPEN, "Species Envelope Data ("+ClientResourceType.HSPEN+")");
		resourceNames.put(ClientResourceType.OCCURRENCECELLS, "Species Occurrence Cells");
		
		//Species fields labels
		
		speciesFieldsNames.put(SpeciesFields.genus,"Genus");
		speciesFieldsNames.put(SpeciesFields.species,"Species");
		speciesFieldsNames.put(SpeciesFields.fbname ,"FishBase Name");
		speciesFieldsNames.put(SpeciesFields.speciesid,"Species ID");
		speciesFieldsNames.put(SpeciesFields.speccode,"Species Code");
		
		speciesFieldsNames.put(SpeciesFields.scientific_name,"Scientific Name");
		speciesFieldsNames.put(SpeciesFields.english_name,"English Name");
		speciesFieldsNames.put(SpeciesFields.french_name,"French Name");
		speciesFieldsNames.put(SpeciesFields.spanish_name,"Spanish Name");
		speciesFieldsNames.put(SpeciesFields.kingdom,"Kingdom");
		speciesFieldsNames.put(SpeciesFields.phylum,"Phylum");
		speciesFieldsNames.put(SpeciesFields.classcolumn,"Class");
		speciesFieldsNames.put(SpeciesFields.ordercolumn,"Order");
		speciesFieldsNames.put(SpeciesFields.familycolumn,"Family");
		
		speciesFieldsNames.put(SpeciesFields.deepwater,"Deepwater");
		speciesFieldsNames.put(SpeciesFields.m_mammals,"Mammal");
		speciesFieldsNames.put(SpeciesFields.angling,"Angling");
		speciesFieldsNames.put(SpeciesFields.diving,"Diving");
		speciesFieldsNames.put(SpeciesFields.dangerous,"Dangerous");
		speciesFieldsNames.put(SpeciesFields.m_invertebrates,"Invertebrate");
		speciesFieldsNames.put(SpeciesFields.algae,"Algae");
		speciesFieldsNames.put(SpeciesFields.seabirds,"Seabird");
		speciesFieldsNames.put(SpeciesFields.freshwater,"Freshwater");
		speciesFieldsNames.put(SpeciesFields.pelagic,"Pelagic");
		speciesFieldsNames.put(SpeciesFields.picname,"PicName");
		speciesFieldsNames.put(SpeciesFields.customized,"Customized");
		
		//Default values per type
		
		defaultFilterValues.put(ClientFieldType.BOOLEAN, Boolean.TRUE);
		defaultFilterValues.put(ClientFieldType.DOUBLE, new Double(0d));
		defaultFilterValues.put(ClientFieldType.INTEGER, new Integer(0));
		defaultFilterValues.put(ClientFieldType.STRING, "");
		
		
		//Field Types
		
		fieldTypes.put(SpeciesFields.genus,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.species,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.fbname ,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.speciesid,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.speccode,ClientFieldType.INTEGER);
		
		fieldTypes.put(SpeciesFields.scientific_name,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.english_name,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.french_name,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.spanish_name,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.kingdom,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.phylum,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.classcolumn,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.ordercolumn,ClientFieldType.STRING);
		fieldTypes.put(SpeciesFields.familycolumn,ClientFieldType.STRING);
		
		fieldTypes.put(SpeciesFields.deepwater,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.m_mammals,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.angling,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.diving,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.dangerous,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.m_invertebrates,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.algae,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.seabirds,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.freshwater,ClientFieldType.BOOLEAN);
		fieldTypes.put(SpeciesFields.pelagic,ClientFieldType.BOOLEAN);
		
		
		ArrayList<ClientFilterOperator> stringOperators=new ArrayList<ClientFilterOperator>();
		stringOperators.add(ClientFilterOperator.begins);
		stringOperators.add(ClientFilterOperator.contains);
		stringOperators.add(ClientFilterOperator.ends);
		stringOperators.add(ClientFilterOperator.is);
		operatorsPerFieldType.put(ClientFieldType.STRING, stringOperators);
		
		ArrayList<ClientFilterOperator> numberOperators=new ArrayList<ClientFilterOperator>();
		numberOperators.add(ClientFilterOperator.greater_then);
		numberOperators.add(ClientFilterOperator.smaller_then);
		operatorsPerFieldType.put(ClientFieldType.DOUBLE, numberOperators);
		operatorsPerFieldType.put(ClientFieldType.INTEGER, numberOperators);
		
		ArrayList<ClientFilterOperator> booleanOperators=new ArrayList<ClientFilterOperator>();
		booleanOperators.add(ClientFilterOperator.is);		
		operatorsPerFieldType.put(ClientFieldType.BOOLEAN, booleanOperators);
		
		
		
		//******************** MAPS
		
		
		mapFieldsNames.put(CompoundMapItem.ALGORITHM, "Algorithm");
		mapFieldsNames.put(CompoundMapItem.AUTHOR, "Author");
		mapFieldsNames.put(CompoundMapItem.COVERAGE, "Coverage");
		mapFieldsNames.put(CompoundMapItem.CREATION_DATE, "Creation Time");
		mapFieldsNames.put(CompoundMapItem.DATA_GENERATION_TIME, "Data Generation Time");
		mapFieldsNames.put(CompoundMapItem.FILESET_ID, "FileSet id");
		mapFieldsNames.put(CompoundMapItem.GIS, "GIS Data");
		mapFieldsNames.put(CompoundMapItem.IMAGE_COUNT, "Static images");
		mapFieldsNames.put(CompoundMapItem.IMAGE_LIST, "Images");
		mapFieldsNames.put(CompoundMapItem.LAYER_ID, "Layer id");
		mapFieldsNames.put(CompoundMapItem.LAYER_PREVIEW, "Layer Preview url");
		mapFieldsNames.put(CompoundMapItem.LAYER_URL, "Layer url");
		mapFieldsNames.put(CompoundMapItem.RESOURCE_ID, "HSPEC ID");
		mapFieldsNames.put(CompoundMapItem.SPECIES_LIST, "Selected Species");
		mapFieldsNames.put(CompoundMapItem.THUMBNAIL, "Map Thumbnail");
		mapFieldsNames.put(CompoundMapItem.TITLE, "Title");
		mapFieldsNames.put(CompoundMapItem.TYPE, "Map Type");
		mapFieldsNames.put(CompoundMapItem.CUSTOM, "Is Customized");
		
	}
	
	private static final String BEGINS_LABEL="Begins with";
	private static final String CONTAINS_LABEL="Contains";
	private static final String ENDS_LABEL="Ends with";
	private static final String GREATER_LABEL=">";
	private static final String IS_LABEL="IS";
	private static final String SMALLER_LABEL="<";
	
	public static final String getOperatorLabel(ClientFilterOperator op){
		switch(op){
		case begins : return BEGINS_LABEL;
		case contains : return CONTAINS_LABEL;
		case ends : return ENDS_LABEL;
		case greater_then : return GREATER_LABEL;
		case is: return IS_LABEL;
		case smaller_then : return SMALLER_LABEL;
		}
		throw new IllegalArgumentException("unable to handle "+op);
	}
	
	public static final ClientFilterOperator getOperatorFromString(String label){
		if(label.equals(BEGINS_LABEL)) return ClientFilterOperator.begins;
		if(label.equals(CONTAINS_LABEL)) return ClientFilterOperator.contains;
		if(label.equals(ENDS_LABEL)) return ClientFilterOperator.ends;
		if(label.equals(GREATER_LABEL)) return ClientFilterOperator.greater_then;
		if(label.equals(IS_LABEL)) return ClientFilterOperator.is;
		if(label.equals(SMALLER_LABEL)) return ClientFilterOperator.smaller_then;
		throw new IllegalArgumentException(label+" is not a valid ClientFieldOperator label");
	}
}
