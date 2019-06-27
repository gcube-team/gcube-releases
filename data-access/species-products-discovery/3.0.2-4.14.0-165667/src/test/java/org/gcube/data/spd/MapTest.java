package org.gcube.data.spd;

import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.data.spd.utils.MapUtils;

public class MapTest {

	public static void main(String[] args) throws Exception {
		
		
		ScopeProvider.instance.set("/gcube/devsec");
		
//		DataBaseDescription db=new DataBaseDescription(
//		"jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu:5432/timeseriesgisdb","postgres", "d4science2");
//		
//		LayerCreationOptions layerOptions=new LayerCreationOptions
//				("timeseriesws", "point","timeseriesws" ,"Datasets", false, true);
		
		MetadataDetails details=new MetadataDetails(
				"This layers means nothing to me", "Mind your business", "Just a layer", "Qualcuno", "insert credits");
		
		
		ArrayList<PointInfo> points=new ArrayList<>();
		System.out.println("Creating points...");
		for(int x=-180;x<180;x++)
			for(int y=-90;y<90;y++)
				points.add(new PointInfo(x, y));
		
		System.out.println("Launching..");
//		System.out.println("Result : "+MapUtils.publishLayerByCoords(db, layerOptions, details, points));
		System.out.println("Result : "+MapUtils.publishLayerByCoords(details, points,false,true));

	}

}
