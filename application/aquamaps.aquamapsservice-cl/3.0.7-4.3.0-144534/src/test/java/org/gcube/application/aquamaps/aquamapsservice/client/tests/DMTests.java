package org.gcube.application.aquamaps.aquamapsservice.client.tests;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.common.scope.api.ScopeProvider;

public class DMTests {

	
//	private static final String URI="http://dbtest.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement";
	
	
	private static final String path="/home/fabio/Downloads/sillyHSPEC.csv";
//	private static final String path="/home/fabio/Downloads/HSPEC_Native_Range.csv";
	
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set(TestCommon.SCOPE);
		DataManagement dm=dataManagement().withTimeout(5, TimeUnit.MINUTES).build();
//		Resource defaultHSPEC=null;
//		Resource defaultHSPEN=null;
//		for(Field f:dm.getDefaultSources()){
//			Resource defaultRes=dm.loadResource(f.getValueAsInteger());
//			if(f.name().equalsIgnoreCase("HSPEC")) defaultHSPEC=dm.loadResource(f.getValueAsInteger());
//			if(f.name().equalsIgnoreCase("HSPEN")) defaultHSPEN=dm.loadResource(f.getValueAsInteger());
//			System.out.println(defaultRes);
//		}
//		
//		Boolean[] fieldsMask=new Boolean[]{
//				true,
//				true,
//				true,
//				true,
//				true,
//				true,
//				true,
//				true
//		};		
//		
//		
//		
//		
////		System.out.println(dm.syncImportResource(new File(path), "fabio.sinibaldi", ResourceType.HSPEC, Charset.defaultCharset().toString(), fieldsMask, true, ','));
//		
//		ArrayList<Field> filter=new ArrayList<Field>();
//		filter.add(new Field(SpeciesOccursumFields.familycolumn+"","Gadilidae",FieldType.STRING));
////		filter.add(new Field(SpeciesOccursumFields.speciesid+"","Fis-10407",FieldType.STRING));
//		
//		System.out.println(dm.getJSONView(new PagedRequestSettings(1, 0, "id",OrderDirection.ASC), dm.getSystemTableName(SystemTable.DATASOURCE_GENERATION_REQUESTS), null));
//		
//		System.out.println(dm.getJSONView(new PagedRequestSettings(1, 0, MetaSourceFields.searchid+"",OrderDirection.ASC), dm.getSystemTableName(SystemTable.DATASOURCES_METADATA), Arrays.asList(new Field[]{
//			new Field(MetaSourceFields.type+"", ResourceType.HCAF+"", FieldType.STRING)
//	    })));
//		
////		System.out.println(dm.setCustomQuery("fabio.sinibaldi", "Select * from meta_sources where searchid=325"));
////		
//		
//		
//		System.out.println("Submitted job "+dm.generateMaps("fabio.sinibaldi", true, defaultHSPEC.getSearchId(), filter, true));
//		
		
		//*************** IMPORT / EXPORT
		
//		System.out.println("Exporting HSPEC");
//		
//		File exported =dm.exportTableAsCSV(defaultHSPEC.getTableName(), null, "fabio.sinibaldi", "exported", ExportOperation.TRANSFER);
//		
//		CustomQueryDescriptorStubs cus=dm.setCustomQuery("fabio.sinibaldi", "Select * from "+defaultHSPEN.getTableName());
//		fieldsMask=new Boolean[cus.fields().theList().size()];
//		for(int i=0;i<fieldsMask.length;i++)fieldsMask[i]=true;
//				
//		
//		
		System.out.println(dm.syncImportResource(new File("/home/fabio/Downloads/aquamaps_fish_vre_2015/vre_occurrencecells_fish_2015.csv"), 
				"fabio.sinibaldi", ResourceType.OCCURRENCECELLS, Charset.defaultCharset().toString(), new Boolean[]{
			true,true,true,true,true,true,true,true,true,true			
		}, true, '\t'));
		
	}
	
}
