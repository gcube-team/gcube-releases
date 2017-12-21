package org.gcube.application.aquamaps.aquamapsportlet.client;



import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.AreaFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.CellFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.LocalObjectFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.ResourceFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;

import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.Converter;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;

public class RecordDefinitions {
	
	public static RecordDef submittedRecordDef=new RecordDef(
		new FieldDef[]{
				new StringFieldDef(SubmittedFields.title+""),
				new StringFieldDef(SubmittedFields.author+""),
				new StringFieldDef(SubmittedFields.jobid+""),
//				new StringFieldDef(SubmittedFields.), resourcePath
				new StringFieldDef(SubmittedFields.starttime+""),
				new StringFieldDef(SubmittedFields.endtime+""),
				new StringFieldDef(SubmittedFields.submissiontime+""),
				new StringFieldDef(SubmittedFields.status+""),
				new StringFieldDef(SubmittedFields.searchid+""),
				new StringFieldDef(SubmittedFields.type+""),
				new BooleanFieldDef(SubmittedFields.saved+"")
		}
	);
	
	public static RecordDef distributionObjectRecordDef= new RecordDef(
			new FieldDef[]{
					new StringFieldDef(LocalObjectFields.title+""),
					new StringFieldDef(LocalObjectFields.type+""),
					new StringFieldDef(LocalObjectFields.species+""),
					new StringFieldDef(LocalObjectFields.bbox+""),					
					new BooleanFieldDef(LocalObjectFields.gis+"")
			}
			);
	
	public static RecordDef biodiversityObjectRecordDef= new RecordDef(
			new FieldDef[]{
					new StringFieldDef(LocalObjectFields.title+""),
					new StringFieldDef(LocalObjectFields.type+""),
					new IntegerFieldDef(LocalObjectFields.species+""),
					new StringFieldDef(LocalObjectFields.bbox+""),					
					new BooleanFieldDef(LocalObjectFields.gis+""),
					new FloatFieldDef(LocalObjectFields.threshold+"")
			}
			);
	
	public static Converter FloatConverter = new Converter (){
		public String format(String data) {
		/*	Log.debug("Formatting "+data);
			if(data.contains(".")){
				int pointIndex=data.indexOf(".");
				if((data.length()>pointIndex+1+2)){
					//precision greater then 2
					int toRoundDigit = Integer.parseInt(data.substring(pointIndex+2,pointIndex+2)); 
					int toMaskDigit = Integer.parseInt(data.substring(pointIndex+3,pointIndex+3));
					return data.substring(0, pointIndex+1)+((toMaskDigit>4)?toRoundDigit+1:toRoundDigit);
				}else {
					Log.debug("Already on setted precision");
					return data;			
				}				
			}else {
				Log.debug("Integer data");
				return data;			
			}
		*/ return data;}
	};
	
	
	
	public static RecordDef kingdomRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef(SpeciesFields.kingdom+"")
			});
	public static RecordDef phylumRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef(SpeciesFields.kingdom+""),
					new StringFieldDef(SpeciesFields.phylum+"")
			});
	public static RecordDef classRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef(SpeciesFields.kingdom+""),
					new StringFieldDef(SpeciesFields.phylum+""),
					new StringFieldDef(SpeciesFields.classcolumn+"")
			});
	public static RecordDef orderRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef(SpeciesFields.kingdom+""),
					new StringFieldDef(SpeciesFields.phylum+""),
					new StringFieldDef(SpeciesFields.classcolumn+""),
					new StringFieldDef(SpeciesFields.ordercolumn+""),
			});
	public static RecordDef familyRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef(SpeciesFields.kingdom+""),
					new StringFieldDef(SpeciesFields.phylum+""),
					new StringFieldDef(SpeciesFields.classcolumn+""),
					new StringFieldDef(SpeciesFields.ordercolumn+""),
					new StringFieldDef(SpeciesFields.familycolumn+""),
			});
	public static RecordDef specRecordDef = new RecordDef(  
			new FieldDef[]{				
					new StringFieldDef(SpeciesFields.speciesid+""),
					new StringFieldDef(SpeciesFields.genus+""),
					new StringFieldDef(SpeciesFields.fbname+""),
					new StringFieldDef(SpeciesFields.speccode+""),
					new StringFieldDef(SpeciesFields.kingdom+""),
					new StringFieldDef(SpeciesFields.phylum+""),
					new StringFieldDef(SpeciesFields.classcolumn+""),
					new StringFieldDef(SpeciesFields.ordercolumn+""),
					new StringFieldDef(SpeciesFields.familycolumn+""),
					new StringFieldDef(SpeciesFields.deepwater+""),
					new StringFieldDef(SpeciesFields.m_mammals+""),
					new StringFieldDef(SpeciesFields.angling+""),
					new StringFieldDef(SpeciesFields.diving+""),
					new StringFieldDef(SpeciesFields.dangerous+""),
					new StringFieldDef(SpeciesFields.m_invertebrates+""),
					new StringFieldDef(SpeciesFields.algae+""),
					new StringFieldDef(SpeciesFields.seabirds+""),
					new StringFieldDef(SpeciesFields.freshwater+""),
					new StringFieldDef(SpeciesFields.scientific_name+""),
					new StringFieldDef(SpeciesFields.french_name+""),
					new StringFieldDef(SpeciesFields.english_name+""),
					new StringFieldDef(SpeciesFields.spanish_name+""),
					new StringFieldDef(SpeciesFields.pelagic+""),
					new StringFieldDef(SpeciesFields.species+""),
					new StringFieldDef(SpeciesFields.occurrecs+""),
				 	new StringFieldDef(SpeciesFields.occurcells+""),
				 	new StringFieldDef(SpeciesFields.map_beforeafter+""),
				 	new StringFieldDef(SpeciesFields.map_seasonal+""),
				 	new StringFieldDef(SpeciesFields.with_gte_5+""),
				 	new StringFieldDef(SpeciesFields.with_gte_6+""),
				 	new StringFieldDef(SpeciesFields.with_gt_66+""),
				 	new StringFieldDef(SpeciesFields.no_of_cells_0+""),
				 	new StringFieldDef(SpeciesFields.no_of_cells_3+""),
				 	new StringFieldDef(SpeciesFields.no_of_cells_5+""),
				 	new StringFieldDef(SpeciesFields.database_id+""),
				 	new StringFieldDef(SpeciesFields.picname+""),
				 	new StringFieldDef(SpeciesFields.authname+""),
				 	new StringFieldDef(SpeciesFields.entered+""),
				 	new StringFieldDef(SpeciesFields.total_native_csc_cnt+""),
				 	new StringFieldDef(SpeciesFields.timestampcolumn+""),
				 	new StringFieldDef(SpeciesFields.pic_source_url+""), 
				 	new BooleanFieldDef(SpeciesFields.customized+""),
					new StringFieldDef(SpeciesFields.querynumber+"")
			});
	

	public static RecordDef areaRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef(AreaFields.type+""),
					new StringFieldDef(AreaFields.code+""),
					new StringFieldDef(AreaFields.name+"")
			});
	
	public static RecordDef cellRecordDef=new RecordDef(
		new FieldDef[]{
				new StringFieldDef(CellFields.csquarecode+""),
				new IntegerFieldDef(CellFields.loiczid+""),
				new FloatFieldDef(CellFields.nlimit+"",CellFields.nlimit+"", FloatConverter),
				new FloatFieldDef(CellFields.slimit+"",CellFields.slimit+"", FloatConverter),
				new FloatFieldDef(CellFields.elimit+""),
				new FloatFieldDef(CellFields.wlimit+""),
				new FloatFieldDef(CellFields.centerlat+""),
				new FloatFieldDef(CellFields.centerlong+""),
				new FloatFieldDef(CellFields.cellarea+""),
				new FloatFieldDef(CellFields.oceanarea+""),
				new StringFieldDef(CellFields.celltype+""),
				new FloatFieldDef(CellFields.pwater+""),
				new IntegerFieldDef(CellFields.faoaream+""),
				new IntegerFieldDef(CellFields.faoareain+""),
				new StringFieldDef(CellFields.countrymain+""),
				new StringFieldDef(CellFields.countrysecond+""),
				new StringFieldDef(CellFields.countrythird+""),
				new IntegerFieldDef(CellFields.eezfirst+""),
				new IntegerFieldDef(CellFields.eezsecond+""),
				new IntegerFieldDef(CellFields.eezthird+""),
				new StringFieldDef(CellFields.eezall+""),
				new IntegerFieldDef(CellFields.eezremark+""),
				new IntegerFieldDef(CellFields.lme+""),
				new IntegerFieldDef(CellFields.oceanbasin+""),
				new IntegerFieldDef(CellFields.longhurst+""),
				new IntegerFieldDef(CellFields.islandsno+""),
				new FloatFieldDef(CellFields.area0_20+""),
				new FloatFieldDef(CellFields.area20_40+""),
				new FloatFieldDef(CellFields.area40_60+""),
				new FloatFieldDef(CellFields.area60_80+""),
				new FloatFieldDef(CellFields.area80_100+""),
				new FloatFieldDef(CellFields.areabelow100+""),
				new FloatFieldDef(CellFields.elevationmin+""),
				new FloatFieldDef(CellFields.elevationmax+""),
				new FloatFieldDef(CellFields.elevationmean+""),
				new FloatFieldDef(CellFields.elevationsd+""),
				new FloatFieldDef(CellFields.waveheight+""),
				new FloatFieldDef(CellFields.tidalrange+""),
				new IntegerFieldDef(CellFields.landdist+""),
				new FloatFieldDef(CellFields.shelf+""),
				new FloatFieldDef(CellFields.slope+""),
				new FloatFieldDef(CellFields.abyssal+""),
				new StringFieldDef(CellFields.coral+""),
				new FloatFieldDef(CellFields.estuary+""),
				new StringFieldDef(CellFields.seagrass+""),
				new IntegerFieldDef(CellFields.seamount+""),
				new FloatFieldDef(CellFields.primprodmean+""),
				new FloatFieldDef(CellFields.iceconann+""),
				new FloatFieldDef(CellFields.depthmean+""),
				new FloatFieldDef(CellFields.sstanmean+""),
				new FloatFieldDef(CellFields.sbtanmean+""),
				new FloatFieldDef(CellFields.salinitybmean+""),
				new FloatFieldDef(CellFields.salinitymean+""),
				new FloatFieldDef(CellFields.inboundbox+""),
				new FloatFieldDef(CellFields.infaoarea+""),
				new StringFieldDef(CellFields.goodcell+""),
	});
	
	public static RecordDef envRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef("parameter"),					
					new FloatFieldDef("min"),
					new FloatFieldDef("max"),
					new FloatFieldDef("prefMin"),
					new FloatFieldDef("prefMax"),
			});
	public static RecordDef perturbationRecordDef=new RecordDef(
			new FieldDef[]{
					new StringFieldDef("fieldName"),
					new StringFieldDef("type"),
					new StringFieldDef("value")
			});
	
	public static RecordDef fileRecordDef=new RecordDef(new FieldDef[]{  
            new StringFieldDef("Type"),  
            new StringFieldDef("nameHuman"),
            new StringFieldDef("Path"),
    });
	
	public static RecordDef filterRecordDef=new RecordDef(new FieldDef[]{
			new StringFieldDef("type"),
			new StringFieldDef("attribute"),
			new StringFieldDef("operator"),
			new StringFieldDef("value")
	});
	
	public static RecordDef resourceRecordDef=new RecordDef(new FieldDef[]{
			new IntegerFieldDef(ResourceFields.searchid+""),
			new StringFieldDef(ResourceFields.title+""),
			new StringFieldDef(ResourceFields.tablename+""),
			new StringFieldDef(ResourceFields.description+""),
			new StringFieldDef(ResourceFields.author+""),
			new StringFieldDef(ResourceFields.disclaimer+""),
			new StringFieldDef(ResourceFields.provenience+""),
			new DateFieldDef(ResourceFields.generationtime+""),
			new IntegerFieldDef(ResourceFields.sourcehcaf+""),
			new IntegerFieldDef(ResourceFields.sourcehspen+""),
			new IntegerFieldDef(ResourceFields.sourcehspec+""),
			new StringFieldDef(ResourceFields.parameters+""),
			new StringFieldDef(ResourceFields.status+""),
			new StringFieldDef(ResourceFields.sourcehcaftable+""),
			new StringFieldDef(ResourceFields.sourcehspentable+""),
			new StringFieldDef(ResourceFields.sourcehspectable+""),
			new StringFieldDef(ResourceFields.type+""),
			new StringFieldDef(ResourceFields.algorithm+""),
	});
}
