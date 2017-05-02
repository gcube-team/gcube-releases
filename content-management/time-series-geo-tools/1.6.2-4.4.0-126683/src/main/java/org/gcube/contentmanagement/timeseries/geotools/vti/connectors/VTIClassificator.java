package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;

public class VTIClassificator {
	
		private static int bathymetryThr = -500;
		public static Tuple<Integer>[]  classify(Tuple<String>[] couples){
			
			Tuple<Integer>[] outClasses= new  Tuple[couples.length];
			int i=0;
			for (Tuple<String> couple:couples){
				Double speed = Double.parseDouble(couple.getElements().get(0));
				Double bathymetry = Double.parseDouble(couple.getElements().get(1));
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
