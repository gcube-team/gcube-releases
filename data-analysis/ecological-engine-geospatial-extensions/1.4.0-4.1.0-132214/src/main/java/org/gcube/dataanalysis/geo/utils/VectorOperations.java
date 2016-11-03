package org.gcube.dataanalysis.geo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;

import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayDouble;
import ucar.ma2.IndexIterator;

public class VectorOperations {

	public static ArrayDouble.D2 arrayByte2DArrayDouble(ArrayByte bytes) {
		int[] shapeD = bytes.getShape();
		int yD = shapeD[0];
		int xD = shapeD[1];
		AnalysisLogger.getLogger().debug(xD + "X" + yD + "=" + (xD * yD));
		ArrayDouble.D2 doublea = new ArrayDouble.D2(yD, xD);

		IndexIterator iterator = bytes.getIndexIterator();
		for (int x = 0; x < xD; x++) {
			for (int y = 0; y < yD; y++) {
				Byte bytex = (Byte) iterator.next();
				doublea.set(y, x, bytex.doubleValue());
			}
		}
		return doublea;
	}

	public static ArrayDouble.D3 arrayByte3DArrayDouble(ArrayByte bytes) {
		int[] shapeD = bytes.getShape();
		int zD = shapeD[0];
		int yD = shapeD[1];
		int xD = shapeD[2];

		AnalysisLogger.getLogger().debug(xD + "X" + yD + "X" + zD + "=" + (xD * yD * zD));
		ArrayDouble.D3 doublea = new ArrayDouble.D3(zD, yD, xD);

		IndexIterator iterator = bytes.getIndexIterator();
		for (int x = 0; x < xD; x++) {
			for (int y = 0; y < yD; y++) {
				for (int z = 0; z < zD; z++) {
					Byte bytex = (Byte) iterator.next();
					doublea.set(z, y, x, bytex.doubleValue());
				}
			}
		}

		return doublea;
	}

	/**
	 * Finds the best association between a grid of 3d points in a certain time instant and a set of 5 dimensional points. each tuple is expected to be formed by (x,y,z,t,value) in 5 dimensions and by (x,y,z) in 3 dimensions
	 **/
	// suggestion: given a resolution R, give Math.sqrt(2)*R/2=0.7*R as tolerance
	public static List<Double> assignPointsValuesToGrid(List<Tuple<Double>> grid3d, int gridTimeInstant, List<Tuple<Double>> coordinates5d, double tolerance) {
//		List<Double> valuesForGrid = new ArrayList<Double>();
		int gridSize = grid3d.size();
		Double [] valuesForGridd = new Double[gridSize];
		
		for (int i = 0; i < gridSize; i++) {
//			valuesForGrid.add(Double.NaN);
			valuesForGridd [i] = Double.NaN;
		}

//		AnalysisLogger.getLogger().debug("Grid contains: "+grid3d.size()+" values");
//		AnalysisLogger.getLogger().debug("Dataset contains: "+coordinates5d.size()+" values");
		int foundmatches = 0;
		long count = 0;
		AnalysisLogger.getLogger().debug("Assigning : "+coordinates5d.size()+" elements");
		for (Tuple<Double> coord5d : coordinates5d) {
			double rx = coord5d.getElements().get(0);
			double ry = coord5d.getElements().get(1);
			double rz = coord5d.getElements().get(2);
			double rt = coord5d.getElements().get(3);
			double rvalue = coord5d.getElements().get(4);
			int gridIdx = 0;
			for (Tuple<Double> coord3d : grid3d) {
				double x = coord3d.getElements().get(0);
				double y = coord3d.getElements().get(1);
				double z = coord3d.getElements().get(2);

				double d = distance(x, y, z, gridTimeInstant, rx, ry, rz, rt);
				
				if (d <= tolerance) {
//					AnalysisLogger.getLogger().debug("Association: distance between grid:("+x+","+y+","+z+","+gridTimeInstant+") and point:("+rx+","+ry+","+rz+","+rt+") is "+d);
//					valuesForGrid.set(gridIdx, rvalue);
					valuesForGridd[gridIdx]=rvalue;
					foundmatches++;
				}
				gridIdx++;
			}
			count++;
			if (count%50000==0)
				AnalysisLogger.getLogger().debug("Vector Operations Assigned: "+count+" elements");
		}
		
		AnalysisLogger.getLogger().debug("Association: Found "+foundmatches+" matches between the grid of points and the coordinates");
		
		List<Double> valuesForGrid = Arrays.asList(valuesForGridd);
		return valuesForGrid;
	}

	public static List<Double> assignGridValuesToPoints2D(List<Tuple<Double>> grid3d, List<Double> gridValues, List<Tuple<Double>> coordinates4d, double tolerance) {
		
//		List<Double> valuesForPoints = new ArrayList<Double>();
		int gridSize = coordinates4d.size();
		Double [] valuesForPoints = new Double[gridSize];
		
		for (int i = 0; i < gridSize; i++) {
			valuesForPoints[i]=Double.NaN;
		}
		
		
		int foundmatches = 0;
		int points=0;
		for (Tuple<Double> coord4d : coordinates4d) {
			double rx = coord4d.getElements().get(0);
			double ry = coord4d.getElements().get(1);
			
			int gridIdx = 0;
			for (Tuple<Double> gridElement : grid3d) {
				double x = gridElement.getElements().get(0);
				double y = gridElement.getElements().get(1);

				double d = distance(x, y, 0, 0, rx, ry, 0, 0);
				if (d <= tolerance) {
//					AnalysisLogger.getLogger().debug("Association: distance between grid:("+x+","+y+","+z+","+gridTimeInstant+") and point:("+rx+","+ry+","+rz+","+rt+") is "+d);
					valuesForPoints[points] = gridValues.get(gridIdx);
					foundmatches++;
					break;
				}
				gridIdx++;
			}
			points++;
		}
		
		AnalysisLogger.getLogger().debug("Association: Found "+foundmatches+" matches between the points and the grid");
		
		return Arrays.asList(valuesForPoints);
	}
	
		public static List<Double> assignGridValuesToPoints(List<Tuple<Double>> grid3d, int gridTimeInstant, List<Double> gridValues, List<Tuple<Double>> coordinates4d, double tolerance) {
			
			List<Double> valuesForPoints = new ArrayList<Double>();
			int gridSize = coordinates4d.size();
			for (int i = 0; i < gridSize; i++) {
				valuesForPoints.add(Double.NaN);
			}
			
			
			int foundmatches = 0;
			int points=0;
			for (Tuple<Double> coord4d : coordinates4d) {
				double rx = coord4d.getElements().get(0);
				double ry = coord4d.getElements().get(1);
				double rz = coord4d.getElements().get(2);
				double rt = coord4d.getElements().get(3);
				
				int gridIdx = 0;
				for (Tuple<Double> gridElement : grid3d) {
					double x = gridElement.getElements().get(0);
					double y = gridElement.getElements().get(1);
					double z = gridElement.getElements().get(2);

					double d = distance(x, y, z, gridTimeInstant, rx, ry, rz, rt);
					if (d <= tolerance) {
//						AnalysisLogger.getLogger().debug("Association: distance between grid:("+x+","+y+","+z+","+gridTimeInstant+") and point:("+rx+","+ry+","+rz+","+rt+") is "+d);
						valuesForPoints.set(points, gridValues.get(gridIdx));
						foundmatches++;
						break;
					}
					gridIdx++;
				}
				points++;
			}
			
			AnalysisLogger.getLogger().debug("Association: Found "+foundmatches+" matches between the points and the grid");
			return valuesForPoints;
		}
		
	public static double distance(double x1, double y1, double z1, double t1, double x2, double y2, double z2, double t2) {

		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)) + ((z1 - z2) * (z1 - z2)) + ((t1 - t2) * (t1 - t2)));

	}
	
	public static List<Tuple<Double>> generateCoordinateTripletsInBoundingBox_old(double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution) {

		int ysteps = (int) ((y2 - y1) / yResolution);
		int xsteps = (int) ((x2 - x1) / xResolution);
		List<Tuple<Double>> tuples = new ArrayList<Tuple<Double>>();
		AnalysisLogger.getLogger().debug("Building the points grid according to YRes:" + yResolution + " and XRes:" + xResolution);
		// build the tuples according to the desired resolution
		for (int i = 0; i < ysteps + 1; i++) {
			double y = (i * yResolution) + y1;
			if (i == ysteps)
				y = y2;
			for (int j = 0; j < xsteps + 1; j++) {
				double x = (j * xResolution) + x1;
				if (j == xsteps)
					x = x2;
				tuples.add(new Tuple<Double>(x, y, z));
			}
		}
		return tuples;
	}
	
	
	public static List<Tuple<Double>> generateCoordinateTripletsInBoundingBox(double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution) {

		List<Tuple<Double>> tuples = new ArrayList<Tuple<Double>>();
		for (double y=y1;y<=y2;y+=yResolution){
			for (double x=x1;x<=x2;x+=xResolution){
				tuples.add(new Tuple<Double>(x,y,z));
			}
		}

		return tuples;
	}
	
	
	public static double [][] vectorToMatix(List<Double> values, double x1, double x2, double y1, double y2, double xResolution, double yResolution){
		
		int ntriplets = values.size();
		int ysteps = 0;
		for (double y=y1;y<=y2;y+=yResolution){
			ysteps++;
		}
		int xsteps = 0;
		for (double x=x1;x<=x2;x+=xResolution){
			xsteps++;
		}

		double[][] slice = new double[ysteps][xsteps];
		int k = 0;
		int g = 0;
		
		// cycle on all the triplets to recontruct the matrix
				for (int t = 0; t < ntriplets; t++) {
					// take the corresponding (time,value) pair
					Double value = values.get(t);
					// if there is value, then set it, otherwise set NaN
					// the layer is undefined in that point and a value must be generated
					// assign a value to the matrix
					slice[k][g] = value;
					// increase the x step according to the matrix
					
					if (g==xsteps-1) {
						g = 0;
						k++;
					} else
						g++;
			}
				
				return slice;
	}

	public void applyNearestNeighbor() {

		/*
		 * AnalysisLogger.getLogger().debug("Applying nearest Neighbor to all the rows"); //apply nearest neighbor to each row AlgorithmConfiguration config = new AlgorithmConfiguration(); config.setConfigPath(configDir); boolean rapidinit = false;
		 * 
		 * 
		 * for (int i=0;i<slice.length;i++){ // AnalysisLogger.getLogger().debug("Checking for unfilled values"); boolean tofill = false; for (int j=0;j<slice[i].length;j++) { if (new Double(slice[i][j]).equals(Double.NaN)) tofill = true; } if (tofill){ if (!rapidinit){ config.initRapidMiner(); rapidinit=true; } AnalysisLogger.getLogger().debug("Filling signal"); double[] ssliced = SignalProcessing.fillSignal(slice[i]); slice[i] = ssliced; } // else // AnalysisLogger.getLogger().debug("Signal yet complete"); }
		 */
	}
}

