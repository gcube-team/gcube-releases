package org.gcube.data.spd.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gcube.data.spd.model.KeyValue;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.OccurrencePoint;


public class BindingTest {

	public static void main(String[] args) throws Exception{
		occurrenceTest();
	}
	
	static private String occurrencePoint = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><occurrencePoint basisOfRecord=\"PreservedSpecimen\" minDepth=\"0.0\" maxDepth=\"0.0\" decimalLongitude=\"-59.167\" decimalLatitude=\"50.617\" country=\"Canada\" locality=\"\" scientificName=\"Carcharodon carcharias\" recordedBy=\"Mareoux, A.; Mareoux, C.\" catalogueNumber=\"CMNFI 1989-0126.1\" collectionCode=\"CMNFI\" institutionCode=\"CMN\" credits=\"Biodiversity occurrence data published by: Ocean Biogeographic Information System (Accessed through GBIF Data Portal, data.gbif.org, 2013-08-30)\" author=\"\" provider=\"\" id=\"17589157\"><dataSet id=\"344\"><citation>Canadian Museum of Nature - Fish Collection</citation><name>Canadian Museum of Nature - Fish Collection (OBIS Canada)</name><dataProvider id=\"82\"><name>Ocean Biogeographic Information System</name></dataProvider></dataSet></occurrencePoint>";
	
	public static void pointInfoTest() throws Exception{
		PointInfo pi= new PointInfo(12.3, 15.5);
		
		List<KeyValue> keyvaluelist= new ArrayList<KeyValue>();
		keyvaluelist.add(new KeyValue("test", "value"));
		
		
		//System.out.println(Arrays.toString(pi.getPropertiesList().toArray(new KeyValue[0])));
		
		pi.setPropertiesList(keyvaluelist);
		System.out.println(Arrays.toString(pi.getPropertiesList().toArray(new KeyValue[0])));
		
		String xml =Bindings.toXml(pi);
		System.out.println(xml);
		
		PointInfo resPi = Bindings.fromXml(xml);
		
		System.out.println(Arrays.toString(pi.getPropertiesList().toArray(new KeyValue[0])));
	}
	
	
	
	public static void occurrenceTest() throws Exception{
		OccurrencePoint point = (OccurrencePoint)Bindings.fromXml(occurrencePoint);
		
		System.out.println(Bindings.toXml(point));
	
	}
	
}
