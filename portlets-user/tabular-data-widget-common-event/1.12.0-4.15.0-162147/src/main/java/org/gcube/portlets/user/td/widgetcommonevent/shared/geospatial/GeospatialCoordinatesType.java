package org.gcube.portlets.user.td.widgetcommonevent.shared.geospatial;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum GeospatialCoordinatesType {
	C_SQUARE("CSQUARECODE"), OCEAN_AREA("OCEANAREA");
	
	/**
	 * @param text
	 */
	private GeospatialCoordinatesType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
	
	public String getId() {
		return id;
	}
	
	
	public static List<GeospatialCoordinatesType> getList(){
		return Arrays.asList(values());
		
	}
	

	public static GeospatialCoordinatesType getGeospatialCoordinatesTypeFromId(
			String id) {
		for(GeospatialCoordinatesType coordType:values()){
			if (id.compareTo(coordType.id) == 0) {
				return coordType;
			}
		}
		return null;
	}
	
	public String getLabel(){
		switch(this){
		case C_SQUARE:
			return "C-Square";
			
		case OCEAN_AREA:
			return "Ocean Area";
		default:
			return null;
		
		}
	}
	
	
}
