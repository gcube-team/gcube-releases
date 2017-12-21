package org.gcube.dataanalysis.geo.vti.vesselsprocessing;

import org.gcube.dataanalysis.ecoengine.utils.Tuple;


public class VTIClassificator {
	
		private static int bathymetryThr = -500;
		
		public static String  speedClassification(int classif){
			
			if (classif==1)
				return "Hauling";
			else if (classif==2)
				return "Fishing";
			else if (classif==3)
				return "Steaming";
			else
				return "Unclassified";
		}

		
		public static String  bathymetryClassification(int classif){
			
			if (classif==1)
				return "Hauling";
			else if (classif==2)
				return "Trawling";
			else if (classif==3)
				return "Midwater trawling";
			else if (classif==4)
				return "Steaming";
			else
				return "Unclassified";
		}

		public static Tuple<Integer>[]  classify(Tuple<String>[] pairs){
			
			Tuple<Integer>[] outClasses= new  Tuple[pairs.length];
			int i=0;
			for (Tuple<String> pair:pairs){
				Double speed = Double.parseDouble(pair.getElements().get(0));
				Double bathymetry = Double.parseDouble(pair.getElements().get(1));
				//Hauling, dodging, shooting
				Integer c1 = 1;
				Integer c2 = 1;
				//Fishing
				if ((speed>2)&&(speed<=5))
					c1 = 2;
				//Steaming
				else if (speed>5)
					c1 = 3;
				
				//midwater trawling
				if ((speed>2)&&(speed<=4)&&(bathymetry<bathymetryThr))
					c2 = 3;
				//midwater trawling
				else if ((speed>4)&&(speed<=6))
					c2 = 3;
				
				//trawling
				else if ((speed>2)&&(speed<=4)&&(bathymetry>=bathymetryThr))
					c2 = 2;
				
				//Steaming
				else if (speed>6)
					c2 = 4;

				
				
				//classification suggestions by Anton:
//			if depth > 500 and speed  [2.5, 4] -> midwater trawling
//			else if depth > 500 -> trawling
//			else -> bottom trawling
				
				
				
				 Tuple<Integer> outTuple = new Tuple<Integer>(c1,c2);
				 outClasses[i] = outTuple;
				 
				i++;
			}
			
			return outClasses;
		}
}
