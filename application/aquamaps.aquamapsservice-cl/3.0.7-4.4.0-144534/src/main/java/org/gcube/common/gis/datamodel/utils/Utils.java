package org.gcube.common.gis.datamodel.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerInfoType;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;



public class Utils {
	public static ArrayList<String> loadString(StringArray sources) {
		try {
			ArrayList<String> res = new ArrayList<String>();
			for (String style: sources.items()){
				res.add(style);
			}
			return res;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public static ArrayList<LayerInfo> loadArray(LayerArray sources) {


		ArrayList<LayerInfo> res = new ArrayList<LayerInfo>();
		
		if((sources!=null))
		for (LayerInfoType layer: sources.theList())
			res.add(new LayerInfo(layer));

		return res;
	}

	public static LayerArray loadArray(ArrayList<LayerInfo> sources) {
		LayerInfoType[] res = new LayerInfoType[(sources!=null)?sources.size():0];
		int i = 0;
		for (LayerInfo layer: sources){
			res[i] = layer.toStubsVersion();
			i += 1;
		}
		return new LayerArray(Arrays.asList(res));
	}
	

}