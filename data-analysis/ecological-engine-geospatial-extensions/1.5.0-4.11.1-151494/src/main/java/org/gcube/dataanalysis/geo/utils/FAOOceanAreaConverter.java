package org.gcube.dataanalysis.geo.utils;

public class FAOOceanAreaConverter {

	
	public static void main(String[] args) {
		FAOOceanAreaConverter converter = new FAOOceanAreaConverter();
		int quadrant = -1;
		//double longitude = 12.5;
		//double latitude = 12.5;
		//double longitude = 129;
		//double latitude = -29;
		double longitude = -1;
		double latitude = -56.1;
		int resolution = 5;
		
		String conv = converter.FAOOceanArea(quadrant, longitude, latitude, resolution);
		System.out.println(conv);
	}

	public String padLongitude(int longitude){
		String longi = ""+longitude;
		if (longi.length()==1)
			longi = "00"+longi;
		else if (longi.length()==2)
			longi = "0"+longi;
		return longi; 
	}
	
	public String padLatitude(int latitude){
		String lati = ""+latitude;
		if (lati.length()==1)
			lati = "0"+lati;
		return lati; 
	}
	
	public int getQuadrant(double longitude,double latitude){
		if (longitude>=0 && latitude>=0)
			return 1;
		else if (longitude>=0 && latitude<0)
			return 2;
		else if (longitude<0 && latitude<0)
			return 3;
		else 
			return 4;
	}
	
	public int[] getBounding(int resolution, double dimension){
		
		int lowDim = (int)(resolution*Math.floor(dimension/resolution));
		int upDim = (int) (resolution*Math.ceil(dimension/resolution));
		int[] bounding =  {lowDim,upDim};
		return bounding;
	}
	
	public double getDistanceFromCenter(double x, double y){
		return Math.sqrt(x*x+y*y);
	}
	
	public String FAOOceanArea(int quadrant, double longitude,double latitude, int resolution){
		
		if (quadrant<=0||quadrant>4){
			quadrant = getQuadrant(longitude, latitude);
		}
		if (longitude>180 || longitude<-180)
			return null;
		if (latitude>90 || latitude<-90)
			return null;
		if (resolution<=0 || resolution>90)
			return null;
		
		double alongitude = Math.abs(longitude);
		double alatitude = Math.abs(latitude);
		int [] bblons = getBounding(resolution, alongitude); 
		int [] bblats = getBounding(resolution, alatitude);
		int bestlon = -1;
		int bestlat = -1;
		double bestdist = Double.MAX_VALUE;
		for (int bblon:bblons){
			for (int bblat:bblats){
				double dist = getDistanceFromCenter(bblon, bblat);
				if (dist<bestdist){
					bestdist=dist;
					bestlon = bblon;
					bestlat = bblat;
				}
			}
		}
		
		String FaoOceanArea = getResolutionChar(resolution)+quadrant+padLatitude(bestlat)+padLongitude(bestlon);
		if (FaoOceanArea.length()!=7)
			return null;
		
		return FaoOceanArea;
	}
	
	public String getResolutionChar(int resolution){
		if (resolution == 1)
		 return "5";
		else if (resolution == 5)
			 return "6";	
		else if (resolution == 10)
			 return "7";	
		else if (resolution == 20)
			 return "8";	
		else if (resolution == 30)
			 return "9";	
		else
			return "0";
	}
	
}
