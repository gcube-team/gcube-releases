package gr.cite.geoanalytics.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.spark.api.java.function.Function;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.gos.client.ShapeManagement;

public class Mapper implements Function<List<ShapeMessenger>, Set<Boolean>> {

	private static final long serialVersionUID = -8923332794337126038L;
	
	private String layerID;
	private String creatorID;
	private String authStr;
	private String gosEndpoint;
	
	public Mapper(String layerID, String creatorID, String authStr, String gosEndpoint){
		this.layerID = layerID;
		this.creatorID = creatorID;
		this.authStr = authStr;
		this.gosEndpoint = gosEndpoint;
	}
	
	
	@Override
	public Set<Boolean> call(List<ShapeMessenger> shapeMessengers) throws Exception {
		
		if(shapeMessengers==null) 
			return null;
		
		//transform to List<Shape>
		List<Shape> shapes = shapeMessengers.stream()
		.map(shapeMessenger -> {
			try{
				Shape shape = shapeMessenger.toShape();
				shape.setLayerID(UUID.fromString(layerID)); //this is VERY important. DO NOT DELETE!
				shape.setCreatorID(UUID.fromString(creatorID)); 
				return (Shape) shape;
			}
			catch(Exception e){
				System.out.println("AN error occured: "+e.getMessage());
				return null;
			}
		})
		.collect(Collectors.toList());
		
		//insert into geoanalytics
		ShapeManagement shapeManagement = new ShapeManagement(authStr);
		boolean status = false;
		try{
			status = shapeManagement.insertShapes(gosEndpoint, shapes);
		}
		catch(Exception ex){ //return false
			return new HashSet<Boolean>(Arrays.asList(new Boolean(false)));
		}
		
		//determine the status (true/false)
		return new HashSet<Boolean>(Arrays.asList(new Boolean(status)));
		
	}
	
	
}
