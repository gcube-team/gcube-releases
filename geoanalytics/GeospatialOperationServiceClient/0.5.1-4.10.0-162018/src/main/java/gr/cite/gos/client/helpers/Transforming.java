package gr.cite.gos.client.helpers;

import java.util.List;
import java.util.stream.Collectors;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public class Transforming {

	
	public static List<Shape> fromShapeMessenger(List<ShapeMessenger> smList){
		
		return smList.parallelStream().map(sm -> {
			try{ 
				return sm.toShape();}
			catch(Exception ex){ 
				return null;
			}
		})
		.filter(sm -> sm!=null)
		.collect(Collectors.toList());
		
	}
	
	
	public static List<ShapeMessenger> fromShape(List<Shape> shapeList){
		
		return shapeList.parallelStream().map(shape -> new ShapeMessenger(shape)).collect(Collectors.toList());
	
	}
	
	
}
