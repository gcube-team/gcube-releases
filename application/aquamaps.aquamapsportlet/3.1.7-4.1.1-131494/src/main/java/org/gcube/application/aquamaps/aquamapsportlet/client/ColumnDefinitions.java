package org.gcube.application.aquamaps.aquamapsportlet.client;

import java.sql.Timestamp;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.AreaFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.CellFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.LocalObjectFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;

import com.google.gwt.core.client.GWT;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.Renderer;


public class ColumnDefinitions {


	static Renderer booleanRenderer(){
		return new Renderer() {  
			public String render(Object value, CellMetadata cellMetadata, Record record,  
					int rowIndex, int colNum, Store store) {  
				boolean checked = ((Boolean) value).booleanValue();  
				return "<img class=\"checkbox\" src=\""+GWT.getModuleBaseURL() +"../js/ext/resources/images/default/menu/" +  
				(checked ? "checked.gif" : "unchecked.gif") + "\"/>";   
			}  
		};
	}
	
	
	public static ColumnModel toCreateObjectsColumnModel(ClientObjectType type){
		ColumnModel toReturn=null;
		if(type.equals(ClientObjectType.Biodiversity)){
			toReturn=	new ColumnModel(new BaseColumnConfig[]{
			new ColumnConfig("Title",LocalObjectFields.title+"",200,true,null,"title"),
			new ColumnConfig("Geographic map extent",LocalObjectFields.bbox+"",200,true,null,"bbox"),
			new ColumnConfig("Species Count",LocalObjectFields.species+"",100,true,null,"species"),
			new ColumnConfig("PSO threshold",LocalObjectFields.threshold+"",100,true,null,"threshold"),
			new ColumnConfig("GIS generation",LocalObjectFields.gis+"",150,false,booleanRenderer(),"gis")
		});}
		else toReturn=	new ColumnModel(new BaseColumnConfig[]{
				new ColumnConfig("Title",LocalObjectFields.title+"",200,true,null,"title"),
				new ColumnConfig("Geographic map extent",LocalObjectFields.bbox+"",200,true,null,"bbox"),
				new ColumnConfig("Associated Species",LocalObjectFields.species+"",100,true,null,"species"),				
				new ColumnConfig("GIS generation",LocalObjectFields.gis+"",150,false,booleanRenderer(),"gis")
			}); 
		return toReturn;
		
	}
	
	
	public static ColumnModel availableSpeciesColumnModel (){

		ColumnModel col= new ColumnModel(new BaseColumnConfig[]{
				new ColumnConfig("Genus", SpeciesFields.genus+"", 160, true, null, "genus"),
				new ColumnConfig("Species",SpeciesFields.species+"",160, true,null),
				new ColumnConfig("Family",SpeciesFields.familycolumn+"",160, true,null),
				new ColumnConfig("FB Common Name ", SpeciesFields.fbname+"", 160, true, null, "fbname"),
				//new ColumnConfig("Common", SpeciesFields.Common, 160, true, null, "common"),			

				// Hide from here
				new ColumnConfig("SPECIESID", SpeciesFields.speciesid+"", 160, true, null, SpeciesFields.speciesid+""),
				new ColumnConfig("Species Code",SpeciesFields.speccode+"",160, true,null),

				new ColumnConfig("Scientific Name", SpeciesFields.scientific_name+"", 160, true, null, "scientific_name"),												
				new ColumnConfig("English Name", SpeciesFields.english_name+"", 160, true, null, "english_name"),
				new ColumnConfig("French Name", SpeciesFields.french_name+"", 160, true, null, "french_name"),
				new ColumnConfig("Spanish Name", SpeciesFields.spanish_name+"", 160, true, null, "spanish_name"),			
				new ColumnConfig("Kingdom",SpeciesFields.kingdom+"",160, true,null),
				new ColumnConfig("Phylum",SpeciesFields.phylum+"",160, true,null),
				new ColumnConfig("Class",SpeciesFields.classcolumn+"",160, true,null),
				new ColumnConfig("Order",SpeciesFields.ordercolumn+"",160, true,null),

				new ColumnConfig("Deep water",SpeciesFields.deepwater+"",160, true,null),
				new ColumnConfig("Mammal",SpeciesFields.m_mammals+"",160, true,null),
				new ColumnConfig("Angling",SpeciesFields.angling+"",160, true,null),
				new ColumnConfig("Diving",SpeciesFields.diving+"",160, true,null),
				new ColumnConfig("Dangerous",SpeciesFields.dangerous+"",160, true,null),
				new ColumnConfig("Invertebrate",SpeciesFields.m_invertebrates+"",160, true,null),
				new ColumnConfig("Algae",SpeciesFields.algae+"",160, true,null),
				new ColumnConfig("Sea birds",SpeciesFields.seabirds+"",160, true,null),
				new ColumnConfig("Fresh water",SpeciesFields.freshwater+"",160, true,null),				
				new ColumnConfig("Pelagic",SpeciesFields.pelagic+"",160,true,null),

		});
		for(int i=4;i<col.getColumnCount();i++){
			col.setHidden(i, true);
		}
		return col;
	}

	public static ColumnModel selectedSpeciesColumnModel (){

		ColumnModel col= new ColumnModel(new BaseColumnConfig[]{
				new ColumnConfig("Genus", SpeciesFields.genus+"", 160, true, null, "genus"),
				new ColumnConfig("Species",SpeciesFields.species+"",160, true,null),
				new ColumnConfig("Family",SpeciesFields.familycolumn+"",160, true,null),
				new ColumnConfig("FB Common Name ", SpeciesFields.fbname+"", 160, true, null, "fbname"),
				new ColumnConfig("Customized", SpeciesFields.customized+"", 160, true, null, "customized"),
		});
		return col;
	}

	public static ColumnModel areasColumnModel (){
		ColumnModel col= new ColumnModel(new BaseColumnConfig[]{
				new ColumnConfig("Type", AreaFields.type+"", 160, true, null, AreaFields.type+""),
				new ColumnConfig("Code", AreaFields.code+"", 160, true, null, AreaFields.code+""),
				new ColumnConfig("Name", AreaFields.name+"", 160, true, null, AreaFields.name+"")});
		return col;
	}



	public static ColumnModel envColumnModel (boolean setEditable){
		ColumnModel col=new ColumnModel(new BaseColumnConfig[]{
				new ColumnConfig("Parameter","parameter"),			
				new ColumnConfig("Min","min"),			
				new ColumnConfig("Pref Min (10th)","prefMin"),
				new ColumnConfig("Pref Max (90th)","prefMax"),
				new ColumnConfig("Max","max"),			
		});
		//col.setHidden(2,true);
		if(setEditable)
			for(int i=2;i<col.getColumnCount();i++){
				col.setEditor(i, new GridEditor(new TextField()));
				col.setEditable(i, true);
			}			
		return col;
	}




	public static ColumnModel goodCellsColumnModel(){
		return new ColumnModel(new ColumnConfig[]{
				new ColumnConfig("Good Cell",CellFields.goodcell+"",80,false,
				new Renderer(){
					public String render(Object value, CellMetadata cellMetadata, Record record,
							int rowIndex, int colNum, Store store) {
						String current=(String) value;
						if(current.equals("0")) return "false";
						else if (current.equals("1")) return "true";
						else return value+"";
					}

				}),
				new ColumnConfig("Center Lat",CellFields.centerlat+""),
				new ColumnConfig("Center Long",CellFields.centerlong+""),
				new ColumnConfig("Depth",CellFields.depthmean+""),
				new ColumnConfig("Surface Temp",CellFields.sstanmean+""),
				new ColumnConfig("Bottom Temp",CellFields.sbtanmean+""),
				new ColumnConfig("Surface Salinity",CellFields.salinitymean+""),
				new ColumnConfig("Bottom Salinity",CellFields.salinitybmean+""),
				new ColumnConfig("Primary Production",CellFields.primprodmean+""),
				new ColumnConfig("Ice Concentration",CellFields.iceconann+""),
				new ColumnConfig("Land Distance",CellFields.landdist+""),
		});
	}
	
	public static ColumnModel submittedColumnModel(){
		ColumnModel col= new ColumnModel(new ColumnConfig[]{
			new ColumnConfig("Title",SubmittedFields.title+""),
			new ColumnConfig("Status",SubmittedFields.status+""),
//			new ColumnConfig("Submitted",SubmittedFields.date+""),
			new ColumnConfig("Submission",SubmittedFields.submissiontime+""),
			new ColumnConfig("Start",SubmittedFields.starttime+""),
			new ColumnConfig("Completion",SubmittedFields.endtime+""),
			new ColumnConfig("Type",SubmittedFields.type+""),
			//new ColumnConfig("ID","searchId"),
			new ColumnConfig("Saved",SubmittedFields.saved+"")
		});
		col.setDefaultSortable(true);
		col.setColumnWidth(0, 140);
		col.setRenderer(1, new Renderer() {
			
			public String render(Object value, CellMetadata cellMetadata,
					Record record, int rowIndex, int colNum, Store store) {
				return "<img src=\""+GWT.getModuleBaseURL()+"../img/"+(String)value+"_status.png\"/>";
			}
		});
		
		Renderer timestampRender=new Renderer() {
			
			@Override
			public String render(Object value, CellMetadata cellMetadata,
					Record record, int rowIndex, int colNum, Store store) {
				try{
					long starttime=Long.parseLong((String)value);
					return AquaMapsPortletCostants.timeFormat.format(new Timestamp(starttime));
				}catch(Exception e){
					return "N/A";
				}
			}
		};
		
		col.setRenderer(2, timestampRender);
		col.setRenderer(3, timestampRender);
		col.setRenderer(4, timestampRender);
		return col;
	}
	
}


