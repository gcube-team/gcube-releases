package org.gcube.contentmanagement.timeseries.geotools.finder.external;

import java.net.URLEncoder;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;

public class GoogleGeocodingConnector {

	
	private static final String googleEndPoint = "http://maps.googleapis.com/maps/api/geocode/json?address=%1$s&sensor=false";
	public static void main(String[] args){
		
		getCoordinates("brasil");
		
	}
	public static String[] getCoordinates(String placename){
		String [] coordinates = new String[2];
		String endpoint = "";
		try{
			endpoint = String.format(googleEndPoint, URLEncoder.encode(placename, "UTF-8"));
			GoogleCoordinates gc = (GoogleCoordinates)HttpRequest.getJSonData(endpoint,null,GoogleCoordinates.class);
			coordinates[0] = gc.results.get(0).geometry.location.lat;
			coordinates[1] = gc.results.get(0).geometry.location.lng;
			System.out.println(coordinates[0]+","+coordinates[1]);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		
		return coordinates;
	}
	
	
	public class GoogleCoordinates {
		
		public List<Address> results;
		
		private class Address{
			
			public List<SubAddress> address_components;
			public String formatted_address;
			public Geometry geometry;
			private class SubAddress{
				String long_name;
				String short_name;
				List<String> types;
			}
			private class Geometry{
				Location location;
				String location_type;
				private class Location{
					String lat;
					String lng;
				}
				
			}
		}
	}
	
	
	/*
	{
		   "results" : [
		      {
		         "address_components" : [
		            {
		               "long_name" : "Pomigliano d'Arco",
		               "short_name" : "Pomigliano d'Arco",
		               "types" : [ "locality", "political" ]
		            },
		            {
		               "long_name" : "Napoli",
		               "short_name" : "NA",
		               "types" : [ "administrative_area_level_2", "political" ]
		            },
		            {
		               "long_name" : "Campania",
		               "short_name" : "Campania",
		               "types" : [ "administrative_area_level_1", "political" ]
		            },
		            {
		               "long_name" : "Italia",
		               "short_name" : "IT",
		               "types" : [ "country", "political" ]
		            }
		         ],
		         "formatted_address" : "Pomigliano d'Arco NA, Italia",
		         "geometry" : {
		            "bounds" : {
		               "northeast" : {
		                  "lat" : 41.01994530,
		                  "lng" : 14.66881790
		               },
		               "southwest" : {
		                  "lat" : 40.85604280,
		                  "lng" : 14.32981660
		               }
		            },
		            "location" : {
		               "lat" : 40.90890740,
		               "lng" : 14.38720370
		            },
		            "location_type" : "APPROXIMATE",
		            "viewport" : {
		               "northeast" : {
		                  "lat" : 41.01994530,
		                  "lng" : 14.66881790
		               },
		               "southwest" : {
		                  "lat" : 40.85604280,
		                  "lng" : 14.32981660
		               }
		            }
		         },
		         "types" : [ "locality", "political" ]
		      }
		   ],
		   "status" : "OK"
		}
	
	*/
}
