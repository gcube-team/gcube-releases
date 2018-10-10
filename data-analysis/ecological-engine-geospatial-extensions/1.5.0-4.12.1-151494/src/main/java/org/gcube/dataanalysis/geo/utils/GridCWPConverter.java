package org.gcube.dataanalysis.geo.utils;

public class GridCWPConverter {


	public static void main(String[] args) {
		GridCWPConverter gridder = new GridCWPConverter();
		gridder.gridCodeToPair("5206064");
		System.out.println(gridder.outlon+","+gridder.outlat+","+gridder.gridresolution);
	}

	public float outlon;
	public float outlat;
	public float gridresolution;
	
	public void gridCodeToPair (String grid){
		char resolution = grid.charAt(0);
		int quadrant = Integer.parseInt(""+grid.charAt(1));
		float lat = Float.parseFloat(grid.substring(2,4));
		float lon = Float.parseFloat(grid.substring(4));
		float Xr = getResolutionX(resolution);
//		int Yr = getResolutionY(resolution);
		gridresolution = Xr;
		float latAdj = adjustLat(lat, quadrant);
		float lonAdj = adjustLon(lon, quadrant);
		float resolutionY = getResolutionY(resolution);
		float resolutionX = getResolutionX(resolution);
		
		float centerLat = getCenterLat(latAdj, resolutionY, quadrant);
		float centerLon = getCenterLon(lonAdj, resolutionX, quadrant);
		
//		System.out.println("Xr:"+Xr+" Yr:"+Yr+" Lat:"+lat+" Lon:"+lon+" LatAdj:"+latAdj+" LonAdj:"+lonAdj+" Q:"+quadrant+" centLon:"+centerLon+" centLat:"+centerLat);
		
		outlat = centerLat;
		outlon = centerLon;
		
	}
	
	public float getCenterLat(float lat, float resolutionY, int quadrant){
		float halfres = (float) resolutionY/2f;
		
		if (quadrant == 1 || quadrant == 4) 
			return lat+halfres;
		else
			return lat-halfres;
	}
	
	public float getCenterLon(float lon, float resolutionX, int quadrant){
		float halfres = (float) resolutionX/2f;
		
		if (quadrant == 1 || quadrant == 2) 
			return lon+halfres;
		else
			return lon-halfres;
	}
	
	public float adjustLat(float lat,int quadrant){
		if (quadrant==1 || quadrant==4)
			return lat;
		else
			return -1*lat;
	}
	
	public float adjustLon(float lon,int quadrant){
		if (quadrant==1 || quadrant==2)
			return lon;
		else
			return -1*lon;
	}
	
	public float getResolutionY(char token){
		if (token == '5')
			return 1;
		else if (token == '6')
			return 5;
		else if (token == '7')
			return 10;
		else if (token == '8')
			return 20;
		else if (token == '9')
			return 30;
		else if (token == '0')
			return 0;
		else if (token == '4')
			return 0.5f;
		else if (token == '3')
			return 0.5f;
		else if (token == '2')
			return 0.22f;
		else if (token == '1')
			return 0.17f;
		else
			return -1;
	}
	
	public float getResolutionX(char token){
		if (token == '5')
			return 1;
		else if (token == '6')
			return 5;
		else if (token == '7')
			return 10;
		else if (token == '8')
			return 20;
		else if (token == '9')
			return 30;
		else if (token == '0')
			return 0;
		else if (token == '4')
			return 1;
		else if (token == '3')
			return 0.5f;
		else if (token == '2')
			return 0.22f;
		else if (token == '1')
			return 0.17f;
		else
			return -1;
	}
	
}

