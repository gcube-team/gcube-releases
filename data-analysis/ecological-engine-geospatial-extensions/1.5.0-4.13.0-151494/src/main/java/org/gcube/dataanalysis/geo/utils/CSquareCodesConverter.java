package org.gcube.dataanalysis.geo.utils;

import it.cnr.aquamaps.CSquare;
import it.cnr.aquamaps.CSquareParser;

public class CSquareCodesConverter {
	
	
		public static String convertHalfDegree(double x, double y){
				
				if (x ==0)
					x = 0.25;
				if (y ==0)
					y = 0.25;
				
				String csquare = it.cnr.aquamaps.CSquare.centroidToCode(x,y, 0.5);
				return csquare;
		}
	
		
		public static String convertAtResolution(double x, double y, double resolution){
			if (resolution==0)
					resolution=0.1;
			if (x ==0)
				x = resolution;
			if (y ==0)
				y = resolution;
			
			String csquare = it.cnr.aquamaps.CSquare.centroidToCode(x,y, resolution);
			return csquare;
	}
	
		
		
		public void parse(String csquare){
			CSquareParser parser = it.cnr.aquamaps.CSquare.parser();
			CSquare square = parser.parse(csquare);
			currentResolution = square.size();
			scalala.tensor.Vector<java.lang.Object> coords= square.center(); 
			scala.collection.Iterator<Object> iterator = coords.valuesIterator();
			currentLong = Double.parseDouble(""+iterator.next());
			currentLat = Double.parseDouble(""+iterator.next());
	}
		
		public static void main(String[] args){
			//System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(0.5,0.5, 0.0001));
//			System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(-49.99999999999994,35.75000000000006, 1));
//			System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(35.75000000000006,-49.99999999999994, 0.5));
//			System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(35.75,-50, 0.5));
//			System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(0.75,31.25, 0.5));
			//System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(31.25, 0.75,0.1));
			
//			System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(50.006,-127.211, 0.5));
			//System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(0.75,31.25, 0.1));
			CSquareCodesConverter converter = new CSquareCodesConverter();
			String csquare = convertAtResolution(55.5, 20.5, 0.5);
			System.out.println(csquare);
			converter.parse("1010:132:3");
			
			System.out.println("lat:"+converter.getCurrentLat());
			System.out.println("long:"+converter.getCurrentLong());
			System.out.println("res:"+converter.getCurrentResolution());
			
		}
		
		
		double currentLat;
		double currentLong;
		double currentResolution;
		
		public double getCurrentLat() {
			return currentLat;
		}


		public void setCurrentLat(double currentLat) {
			this.currentLat = currentLat;
		}


		public double getCurrentLong() {
			return currentLong;
		}


		public void setCurrentLong(double currentLong) {
			this.currentLong = currentLong;
		}


		public double getCurrentResolution() {
			return currentResolution;
		}


		public void setCurrentResolution(double currentResolution) {
			this.currentResolution = currentResolution;
		}

		
}

