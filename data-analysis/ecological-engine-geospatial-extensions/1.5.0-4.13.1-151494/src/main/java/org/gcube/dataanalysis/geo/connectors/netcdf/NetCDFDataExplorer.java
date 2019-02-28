package org.gcube.dataanalysis.geo.connectors.netcdf;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.utils.VectorOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.Array;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.Range;
import ucar.ma2.StructureData;
import ucar.ma2.StructureMembers.Member;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.ft.FeatureCollection;
import ucar.nc2.ft.FeatureDataset;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
import ucar.nc2.ft.PointFeatureCollection;
import ucar.nc2.ft.PointFeatureIterator;
import ucar.nc2.ft.point.PointDatasetImpl;
import ucar.nc2.ft.point.standard.StandardPointCollectionImpl;
import ucar.unidata.geoloc.LatLonPoint;
import ucar.unidata.geoloc.LatLonPointImpl;
import ucar.unidata.geoloc.LatLonRect;

public class NetCDFDataExplorer {

	// http://thredds.research-infrastructures.eu:8080/thredds/catalog/public/netcdf/catalog.xml
	public static String timePrefix = "time:";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NetCDFDataExplorer.class);
	
	public NetCDFDataExplorer(String openDapLink, String layer) {
		calcZRange(openDapLink, layer);
	}

	public List<Double> retrieveDataFromNetCDF(String openDapLink, String layer, int time, List<Tuple<Double>> triplets) {
		try {
			List<Double> values = new ArrayList<Double>();
			if (isGridDataset(openDapLink)) {
				LOGGER.debug("Managing Grid File");
				return manageGridDataset(layer, openDapLink, time, triplets);
			}
			/*
			 * else if (isPointDataset(openDapLink)) { LOGGER.debug("Managing Points File"); }
			 */
			else
				LOGGER.debug("Warning: the NETCDF file is of an unknown type");
			return values;
		} catch (Exception e) {
			LOGGER.error("ERROR",e);
			return null;
		}
	}

	public double minZ = 0;
	public double maxZ = 0;

	public void calcZRange(String openDapLink, String layer) {
		try {
			if (isGridDataset(openDapLink)) {
				gds = ucar.nc2.dt.grid.GridDataset.open(openDapLink);
				List<GridDatatype> gridTypes = gds.getGrids();
				for (GridDatatype gdt : gridTypes) {

					// LOGGER.debug("Inside File - layer name: " + gdt.getFullName());
					LOGGER.debug("Inside File - layer name: " + gdt.getName());
					if (layer.equalsIgnoreCase(gdt.getName())) {
						CoordinateAxis zAxis = gdt.getCoordinateSystem().getVerticalAxis();
						minZ = zAxis.getMinValue();
						maxZ = zAxis.getMaxValue();
						break;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("NetCDF Explorer Error",e);
			
		}
	}

	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	GridDataset gds;

	public List<Double> manageGridDataset(String layer, String filename, int time, List<Tuple<Double>> triplets) throws Exception {
		List<Double> values = new ArrayList<Double>();
		if (gds == null)
			gds = ucar.nc2.dt.grid.GridDataset.open(filename);

		List<GridDatatype> gridTypes = gds.getGrids();
		for (GridDatatype gdt : gridTypes) {
			LOGGER.debug("Inside File - layer name: " + gdt.getName() + " layer to find " + layer);
			// if the layer is an HTTP link then take the first innser layer
			if (layer.equalsIgnoreCase(gdt.getName()) || layer.toLowerCase().startsWith("http:")) {
				LOGGER.debug("Found layer " + layer + " inside file");
				GridDatatype grid = gds.findGridDatatype(gdt.getName());
				CoordinateAxis zAxis = gdt.getCoordinateSystem().getVerticalAxis();
				CoordinateAxis xAxis = gdt.getCoordinateSystem().getXHorizAxis();
				CoordinateAxis yAxis = gdt.getCoordinateSystem().getYHorizAxis();
				double resolutionZ = 0;
				try {
					resolutionZ = Math.abs((double) (zAxis.getMaxValue() - zAxis.getMinValue()) / (double) zAxis.getShape()[0]);
					LOGGER.debug("Zmin:" + zAxis.getMinValue() + " Zmax:" + zAxis.getMaxValue());
				} catch (Exception e) {
				}

				GridCoordSystem gcs = grid.getCoordinateSystem(); 
				int tsize = triplets.size();
				long t01 = System.currentTimeMillis();
				LatLonRect llr = null;
				LOGGER.debug("Extracting subset...");
				GridDatatype gdtsub = grid.makeSubset(new Range(time, time), null, llr, 1, 1, 1);
				Array data = gdtsub.readVolumeData(time); // note order is t, z, y, x
				int[] shapeD = data.getShape();
				int zD = 0;
				int xD = 0;
				int yD = 0;
				if (shapeD.length > 2) {
					zD = shapeD[0];
					yD = shapeD[1];
					xD = shapeD[2];
				}

				else if (shapeD.length > 1) {
					yD = shapeD[0];
					xD = shapeD[1];
				}

				// double resolutionX = Math.abs((double) (xAxis.getMaxValue() - xAxis.getMinValue()) / (double) xAxis.getShape()[0]);
				// double resolutionY = Math.abs((double) (yAxis.getMaxValue() - yAxis.getMinValue()) / (double) yAxis.getShape()[0]);
				double resolutionX = Math.abs((double) (xAxis.getMaxValue() - xAxis.getMinValue()) / (double) xD);
				double resolutionY = Math.abs((double) (yAxis.getMaxValue() - yAxis.getMinValue()) / (double) yD);

				LOGGER.debug("Shape: Z:" + zD + " X:" + xD + " Y:" + yD);

				LOGGER.debug("Layer Information Retrieval ELAPSED Time: " + (System.currentTimeMillis() - t01));
				int rank = data.getRank();
				LOGGER.debug("Rank of the layer: " + rank);

				ArrayFloat.D3 data3Float = null;
				ArrayDouble.D3 data3Double = null;
				ArrayInt.D3 data3Int = null;
				ArrayLong.D3 data3Long = null;
				ArrayFloat.D2 data2Float = null;
				ArrayDouble.D2 data2Double = null;
				ArrayInt.D2 data2Int = null;
				ArrayLong.D2 data2Long = null;

				if (data.getRank() == 3) {
					if (data instanceof ArrayFloat.D3)
						data3Float = (ArrayFloat.D3) data;
					else if (data instanceof ArrayInt.D3)
						data3Int = (ArrayInt.D3) data;
					else if (data instanceof ArrayDouble.D3)
						data3Double = (ArrayDouble.D3) data;
					else if (data instanceof ArrayDouble.D3)
						data3Double = (ArrayDouble.D3) data;
					else if (data instanceof ArrayLong.D3)
						data3Long = (ArrayLong.D3) data;
					else if (data instanceof ArrayByte.D3)
						data3Double = (ArrayDouble.D3) VectorOperations.arrayByte3DArrayDouble((ArrayByte) data);
					else
						throw new Exception("Layer data format not supported");
				} else {
					if (data instanceof ArrayFloat.D2)
						data2Float = (ArrayFloat.D2) data;
					else if (data instanceof ArrayInt.D2)
						data2Int = (ArrayInt.D2) data;
					else if (data instanceof ArrayDouble.D2)
						data2Double = (ArrayDouble.D2) data;
					else if (data instanceof ArrayLong.D2)
						data2Long = (ArrayLong.D2) data;
					else if (data instanceof ArrayByte.D2)
						data2Double = (ArrayDouble.D2) VectorOperations.arrayByte2DArrayDouble((ArrayByte) data);
					else
						throw new Exception("Layer data format not supported");
				}

				double xmin = xAxis.getMinValue();
				double xmax = xAxis.getMaxValue();
				double ymax = yAxis.getMaxValue();
				double ymin = yAxis.getMinValue();
				int xmaxidx = (int) Math.round((xmax - xmin) / resolutionX);
				int ymaxidx = (int) Math.round((ymax - ymin) / resolutionY);

				boolean is0_360 = false;
				// if (((xmax == 360) && (xmin == 0)) || ((xmax == 359.5) && (xmin == 0.5))) {
				// if ((xmin>=0) || (ymin == -77.0104751586914 && ymax==89.94786834716797)) {

				LOGGER.debug("X dimension: " + xD + " Xmin:" + xmin + " Xmax:" + xmax + " Xmaxidx:" + xmaxidx+" XRes: "+resolutionX);
				LOGGER.debug("Y dimension: " + yD + " Ymin:" + ymin + " Ymax:" + ymax + " Ymaxidx:" + ymaxidx+" YRes: "+resolutionY);

				if ((xmin >= 0)) {
					xmax = 180;
					xmin = -180;
					is0_360 = true;
				}
				LOGGER.debug("Assigning "+tsize+" grid elements to the NetCDF values");
				for (int i = 0; i < tsize; i++) {
					int zint = 0;
					int xint = 0;
					int yint = 0;
					Tuple<Double> triplet = triplets.get(i);
					double x = triplet.getElements().get(0);
					double y = triplet.getElements().get(1);
					if (x == 180)
						x = -180;
					if (y == 90)
						y = -90;

					double z = 0;

					if (triplet.getElements().size() > 1)
						z = triplet.getElements().get(2);
					if (resolutionZ > 0) {
						if ((zAxis.getMinValue() <= z) && (zAxis.getMaxValue() >= z))
							zint = Math.abs((int) Math.round((z - zAxis.getMinValue()) / resolutionZ));
					}

					if (y < ymin)
						y = ymin;
					if (x < xmin)
						x = xmin;
					if (y > ymax)
						y = ymax;
					if (x > xmax)
						x = xmax;

				
					try{
					int[] idxbb = gcs.findXYindexFromLatLon(75,10, null);
					int[] idxo = gcs.findXYindexFromLatLon(0,0, null);					
					LatLonPoint inverseOrigin = gcs.getLatLon(idxo[0],idxo[1]);
//					LatLonPoint inverseBB = gcs.getLatLon(idxbb[0],idxbb[1]);
					//correction to origin offset
					x = x - inverseOrigin.getLongitude();
					y = y - inverseOrigin.getLatitude();
					if (i==0)
						LOGGER.debug("bb: " + idxbb[0] +","+idxbb[1]+" origin: "+idxo[0]+","+idxo[1]+" middle "+xD/2+","+yD/2+" shift "+(idxo[0]-(xD/2))+" inverse shift on origin "+inverseOrigin);
					}catch(Exception e){
						LOGGER.debug("Error getting x,y corrections "+e.getLocalizedMessage());
						e.printStackTrace();
					}
					
					int[] idx = gcs.findXYindexFromLatLon(y,x, null);

					xint = idx[0];
					yint = idx[1];

					
					
					if (yint < 0) {
						yint = 0;
					}
					if (xint < 0) {
						xint = 0;
					}
					if (xint > xD - 1)
						xint = xD - 1;
					if (yint > yD - 1)
						yint = yD - 1;

					/*
					 * if ((xmin <= x) && (xmax >= x)) // xint = (int) Math.round((x - xmin) / resolutionX); { if (is0_360) { if (x < 0) xint = (int) Math.round((x - xmin + xmax) / resolutionX); else xint = (int) Math.round((x) / resolutionX); } else { xint = (int) Math.round((x-xmin) / resolutionX); } }
					 * 
					 * if ((yAxis.getMinValue() <= y) && (yAxis.getMaxValue() >= y)) { yint = (int) Math.round((ymax - y) / resolutionY); }
					 * 
					 * 
					 * if (xint > xD - 1) xint = xD - 1; if (yint > yD - 1) yint = yD - 1;
					 */
					Double val = Double.NaN;
					if (zint > zD - 1)
						zint = zD - 1;

					if (data3Float != null)
						val = Double.valueOf(data3Float.get(zint, yint, xint));
					else if (data3Int != null)
						val = Double.valueOf(data3Int.get(zint, yint, xint));
					else if (data3Double != null)
						val = Double.valueOf(data3Double.get(zint, yint, xint));
					else if (data3Long != null)
						val = Double.valueOf(data3Long.get(zint, yint, xint));

					else if (data2Float != null)
						val = Double.valueOf(data2Float.get(yint, xint));
					else if (data2Int != null)
						val = Double.valueOf(data2Int.get(yint, xint));
					else if (data2Double != null)
						val = Double.valueOf(data2Double.get(yint, xint));
					else if (data2Long != null)
						val = Double.valueOf(data2Long.get(yint, xint));
					
					/*LOGGER.debug("Choice "+ (data3Float!=null)+","+
					(data3Int!=null)+","+
					(data3Double!=null)+","+
					(data3Long!=null)+","+
					(data2Float!=null)+","+
					(data2Int!=null)+","+
					(data2Double!=null)+","+
					(data2Long!=null));
						*/
//					LOGGER.debug("Assigning "+val+" to "+x+","+y+" ["+xint+","+yint+"]");
//					LOGGER.debug("checking "+data2Float.get(yint, xint)+" vs ");
//					try{LOGGER.debug("checking2 "+data2Float.get(xint,yint));}catch(Exception e){}
					
					
					values.add(val);
				}
				break;
			}
		}
		return values;
	}

	private boolean detIsPositive(double x0, double y0, double x1, double y1, double x2, double y2) {
		double det = (x1 * y2 - y1 * x2 - x0 * y2 + y0 * x2 + x0 * y1 - y0 * x1);
		if (det == 0)
			System.out.printf("determinate = 0%n");
		return det > 0;
	}

	public static GridDatatype getGrid(String layer, String netcdffile) throws Exception{
		LOGGER.debug("Opening File : " + netcdffile);
		LOGGER.debug("Searching for layer: " + layer);
		GridDataset gds = ucar.nc2.dt.grid.GridDataset.open(netcdffile);
		List<GridDatatype> gridTypes = gds.getGrids();
		StringBuffer sb = new StringBuffer();
		for (GridDatatype gdt : gridTypes) {
			LOGGER.debug("Inside File - layer name: " + gdt.getName());
			sb.append(gdt.getName()+" ");
			if (layer.equals(gdt.getName())) {
				LOGGER.debug("Found layer " + layer + " inside file");
				GridDatatype grid = gds.findGridDatatype(gdt.getName());
				return grid;
			}
		}
		
		throw new java.lang.Exception("No layer with  name "+layer+" is available in the NetCDF file. Possible values are "+sb.toString());
	}
	
	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	public static LinkedHashMap<String, Double> manageGridDataset(String layer, String filename, double x, double y, double z) throws Exception {
		LinkedHashMap<String, Double> valuesMap = new LinkedHashMap<String, Double>();
		GridDataset gds = ucar.nc2.dt.grid.GridDataset.open(filename);
		List<GridDatatype> gridTypes = gds.getGrids();
		for (GridDatatype gdt : gridTypes) {
			LOGGER.debug("Inside File - layer name: " + gdt.getName());
			if (layer.equalsIgnoreCase(gdt.getName())) {
				LOGGER.debug("Found layer " + layer + " inside file");
				GridDatatype grid = gds.findGridDatatype(gdt.getName());
				GridCoordSystem gcs = grid.getCoordinateSystem();
				long timeSteps = 0;
				java.util.Date[] dates = null;
				if (gcs.hasTimeAxis1D()) {
					CoordinateAxis1DTime tAxis1D = gcs.getTimeAxis1D();
					dates = tAxis1D.getTimeDates();
					timeSteps = dates.length;
				} else if (gcs.hasTimeAxis()) {
					CoordinateAxis tAxis = gcs.getTimeAxis();
					timeSteps = tAxis.getSize();
				}

				CoordinateAxis zAxis = gdt.getCoordinateSystem().getVerticalAxis();
				double resolutionZ = Math.abs((double) (zAxis.getMaxValue() - zAxis.getMinValue()) / (double) zAxis.getShape()[0]);
				int zint = 0;
				if (resolutionZ > 0) {
					if ((zAxis.getMinValue() <= z) && (zAxis.getMaxValue() >= z))
						zint = Math.abs((int) Math.round((z - zAxis.getMinValue()) / resolutionZ));
				}

				LOGGER.debug("Z index to take: " + zint);

				int[] xy = gcs.findXYindexFromLatLon(x, y, null);
				for (int j = 0; j < timeSteps; j++) {
					try {
						Array data = grid.readDataSlice(j, zint, xy[1], xy[0]); // note order is t, z, y, x
						Double val = takeFirstDouble(data);
						if (!val.isNaN()) {
							String date = "" + j;
							if (dates != null)
								date = dates[j].toString();
							valuesMap.put(timePrefix + date, Double.parseDouble("" + val));
						}
					} catch (Exception e) {
						LOGGER.debug("Error in getting grid values in (" + x + "," + y + "," + z + "= with zint: " + zint + " resolution: " + resolutionZ + " and shape: " + zAxis.getShape()[0]);
					}
				}
				break;
			}
		}
		return valuesMap;
	}

	public static Double takeFirstDouble(Array data) {
		long datal = data.getSize();
		Double val = Double.NaN;
		try {
			for (int k = 0; k < datal; k++) {
				Double testVal = data.getDouble(k);
				if (!testVal.isNaN()) {
					val = testVal;
					break;
				}
			}
		} catch (Exception ee) {
			LOGGER.debug("NetCDFDataExplorer-> WARNING: Error in getting value: " + ee.getLocalizedMessage());
		}
		return val;
	}

	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	public LinkedHashMap<String, String> managePointsDataset(String layer, String filename, double x, double y) throws Exception {
		LinkedHashMap<String, String> valuesMap = new LinkedHashMap<String, String>();
		float tolerance = 0.25f;
		Formatter errlog = new Formatter();
		FeatureDataset fdataset = FeatureDatasetFactoryManager.open(FeatureType.POINT, filename, null, errlog);
		PointDatasetImpl ds = (PointDatasetImpl) fdataset;
		List<FeatureCollection> lfc = ds.getPointFeatureCollectionList();

		for (FeatureCollection fc : lfc) {

			StandardPointCollectionImpl spf = (StandardPointCollectionImpl) fc;
			PointFeatureIterator iter = null;
			while ((y - tolerance > -90) && (x - tolerance > -180) && (y + tolerance < 90) && (x + tolerance < 180)) {
				LatLonRect rect = new LatLonRect(new LatLonPointImpl(y - tolerance, x - tolerance), new LatLonPointImpl(y + tolerance, x + tolerance));
				PointFeatureCollection coll = spf.subset(rect, null);
				iter = coll.getPointFeatureIterator(100 * 1000); // 100Kb buffer
				if (iter.getCount() == 0)
					iter.finish();
				else
					break;
				tolerance = tolerance + 0.25f;
				LOGGER.debug("NetCDFDataExplorer-> -> tolerance = " + tolerance);
			}

			if (iter != null) {
				try {
					while (iter.hasNext()) {
						ucar.nc2.ft.PointFeature pf = iter.next();
						LOGGER.debug("NetCDFDataExplorer-> -> EarthLoc: " + pf.getLocation());
						LOGGER.debug("NetCDFDataExplorer-> -> EarthTime: " + pf.getObservationTime());
						StructureData sd = pf.getData();
						List<Member> mems = sd.getMembers();
						for (Member m : mems) {
							String unit = m.getUnitsString();
							if ((unit != null) && (unit.length() > 0)) {
								LOGGER.debug("NetCDFDataExplorer-> -> description: " + m.getDescription());
								LOGGER.debug("NetCDFDataExplorer-> -> data param: " + m.getDataParam());
								LOGGER.debug("NetCDFDataExplorer-> -> name: " + m.getName());
								LOGGER.debug("NetCDFDataExplorer-> -> unit: " + m.getUnitsString());
								LOGGER.debug("NetCDFDataExplorer-> -> type: " + m.getDataType());
								Array arr = sd.getArray(m.getName());
								LOGGER.debug("NetCDFDataExplorer-> -> is Time: " + m.getDataType());
								Double val = takeFirstDouble(arr);

								LOGGER.debug("NetCDFDataExplorer-> -> extracted value: " + val);
							}
						}
						LOGGER.debug("NetCDFDataExplorer-> -> EarthTime: ");
					}
				} finally {
					iter.finish();
				}
			}
			break;
		}
		return valuesMap;
	}

	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	public static boolean isGridDataset(String filename) {
		try {
			LOGGER.debug("Analyzing file " + filename);
			Formatter errlog = new Formatter();
			FeatureDataset fdataset = FeatureDatasetFactoryManager.open(FeatureType.GRID, filename, null, errlog);
			if (fdataset == null) {
				// System.out.printf("GRID Parse failed --> %s\n", errlog);
				LOGGER.debug("NetCDFDataExplorer-> NOT GRID");
				return false;
			} else
				return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	// A GridDatatype is like a specialized Variable that explicitly handles X,Y,Z,T dimensions
	public static boolean isPointDataset(String filename) {
		try {
			Formatter errlog = new Formatter();
			FeatureDataset fdataset = FeatureDatasetFactoryManager.open(FeatureType.POINT, filename, null, errlog);
			if (fdataset == null) {
				LOGGER.debug("NetCDFDataExplorer-> NOT POINT");
				return false;
			} else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDataset(String filename) throws Exception {
		boolean isdataset = false;
		try {
			Formatter errlog = new Formatter();
			FeatureType[] fts = FeatureType.values();
			for (int i = 0; i < fts.length; i++) {
				FeatureDataset fdataset = FeatureDatasetFactoryManager.open(fts[i], filename, null, errlog);
				if (fdataset == null) {
					// System.out.printf(fts[i]+": Parse failed --> %s\n",errlog);
				} else {
					LOGGER.debug("NetCDFDataExplorer->  " + fts[i] + " OK!");
					isdataset = true;
				}
			}
		} catch (Exception e) {
		}
		return isdataset;
	}

	public static double adjX(double x) {
		/*
		 * if (x < -180) x = -180; if (x > 180) x = 180;
		 */
		return x;
	}

	public static double adjY(double y) {
		/*
		 * if (y < -90) y = -90; if (y > 90) y = 90;
		 */
		return y;
	}

	public static double getMinX(GridCoordSystem gcs) {
		CoordinateAxis xAxis = gcs.getXHorizAxis();
		return adjX(xAxis.getMinValue());
	}

	public static double getMaxX(GridCoordSystem gcs) {
		CoordinateAxis xAxis = gcs.getXHorizAxis();
		return adjX(xAxis.getMaxValue());
	}

	public static double getMinY(GridCoordSystem gcs) {
		CoordinateAxis yAxis = gcs.getYHorizAxis();
		return adjY(yAxis.getMinValue());
	}

	public static double getMaxY(GridCoordSystem gcs) {
		CoordinateAxis yAxis = gcs.getYHorizAxis();
		return adjY(yAxis.getMaxValue());
	}
	
	public static double getResolution(String layer, String file) throws Exception{
		GridDatatype gdt = getGrid(layer, file);

		double minY = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
		double maxY = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
		
		GridDatatype gdtsub = gdt.makeSubset(new Range(0, 0), null, null, 1, 1, 1);
		Array data = gdtsub.readVolumeData(0); // note order is t, z, y, x
		int[] shapeD = data.getShape();
		int yD = 0;
		if (shapeD.length > 2) 
			yD = shapeD[1];
		else
			yD = shapeD[0];
		
		double resolutionY = Math.abs((double) (maxY - minY) / (double) yD);
		resolutionY = MathFunctions.roundDecimal(resolutionY, 4);
		return resolutionY;
	}
}
