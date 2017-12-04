package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;

/**
 * Class for obtaining altimetry and bathymetry values from a netCDF file.
 * Requires the netCDF libraries (see here: http://www.unidata.ucar.edu/software/netcdf-java/documentation.htm)
 * 
 * You should use the class in the following way:
 * <code><pre>
 *   VTIBathymetry bath = new VTIBathymetry("/path/to/bath.nc");   // e.g. gebco_08.nc
 *   bath.open();                                                  // file must be explicitly opened, this takes a short time, avoid opening it several times
 *   short value1 = bath.getZ(142.2, 11.35);                       // e.g. Mariana Trench
 *   short value2 = bath.getZ(0, 0);
 *   // ...
 *   bath.close();                                                 // file should be closed when not needed any longer
 * </pre></code>
 * 
 * @author Frank Loeschau, Terradue Srl.
 */
public class VTIBathymetry {
	
	private String filename;
	private String mode;
	private java.awt.geom.Point2D.Double[] points;
	private ucar.nc2.NetcdfFile ncFile;
	private ucar.nc2.Variable var;
	//private ucar.ma2.Array arr;
	private boolean debug = false;
	public final int GRID_COLS = 360 * 120;
	public final int GRID_ROWS = 180 * 120;
	
	/**
	 * The class can also be used as command-line tool.
	 * The syntax is: <p><code>java org.d4science2.vtivre.VTIBathymetry get x1,y1 [x2,y2 [...]] filename</code></p>
	 * If only one pair of coordinates is specified, debug information about the calculation is written.
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		int status = 0;
		VTIBathymetry bm = new VTIBathymetry();

		try {
			if (!bm.parseArguments(args)) System.exit(1);
			
			bm.open();
			
			short[][] res = new short[361][181];
			
			if (bm.mode.compareTo("draw") == 0) {
				bm.draw();
			} else if (bm.mode.compareTo("get") == 0) {
				bm.debug = false;
				
				if (bm.points == null || bm.points.length == 0) {
					System.err.println("No point specified");
					System.exit(1);
				} else if (bm.points.length == 1) {
					bm.debug = true;
					short z = bm.getZ(bm.points[0]);
					System.out.println(z);
				} else {
					short[] zs = bm.getZ(bm.points);
					for (int i = 0; i < zs.length; i++) System.out.println(zs[i]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			status = 1;
		} finally {
			try {
				bm.close();
			} catch (Exception e2) {}
		}
		System.exit(status);
	}
	
	private static void printUsage() {
		System.out.println("Usage: org.d4science2.vtivre.VTIBathymetry (get x,y [x2,y2 [x3,y3 ...]]) file");
		System.out.println();
		System.exit(1);
	}
	
	private VTIBathymetry() {}

	private boolean parseArguments(String[] args) throws Exception {
		try {
			if (args.length >= 1) mode = args[0];
			if (args.length >= 2) filename = args[args.length - 1];  
				
			points = new java.awt.geom.Point2D.Double[args.length > 2 ? args.length - 2 : 0];
			for (byte i = 0; i < args.length - 2; i++) {
				String[] s = args[i + 1].split(",");
				if (s.length != 2) {
					System.err.println("Invalid coordinate format: must be x,y (e.g. -12.34,56.78)");
					System.exit(1);
				}
				points[i] = new java.awt.geom.Point2D.Double();
				points[i].setLocation(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
			}
		} catch (Exception e) {
			printUsage();
			return false;
		}

		if (mode == null) {
			System.err.println("No mode specified");
			printUsage();
			return false;
		} else if (filename == null) {
			System.err.println("No filename specified");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Creates an instance of VTIBathymetry with the specified file.
	 * @param filename the netCDF filename containing the altimetry/bathymetry data
	 */
	public VTIBathymetry(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Opens the netCDF file and loads the data array.
	 * This operation is somewhat slow and requires the creation of a big object in memory. Ideally, this method is called only once, before the altrimetry/bathymetry values are needed.
	 * @throws java.io.IOException
	 */
	public void open() throws java.io.IOException {
		ncFile = ucar.nc2.dataset.NetcdfDataset.openFile(filename, null);
		var = ncFile.findVariable("z");
		//arr = var.read();
		
	}
	
	/**
	 * Closes the netCDF file.
	 * @throws java.io.IOException
	 */
	public void close() throws java.io.IOException {
		if (ncFile != null) ncFile.close();
	}
	
	/**
	 * Returns the altimetric/bathymetric value of the given geographical coordinates.
	 * The value derives from the corresponding cell in the coordinate grid (120 x 120 cells per degree).
	 * If a coordinate is close to the cell border (< 0.1 * arc cell size), the result value is averaged with the value from the adjacent cell.
	 * @param point the geographical coordinates (x = longitude value (-180 to 180), y = latitude value (-90 to 90))
	 * @return the altimetric/bathymetric value for the coordinate (in meters)
	 * @throws java.io.IOException if the netCDF variable <code>z</code> could not been read
	 */
	public short getZ(java.awt.geom.Point2D.Double point) throws java.io.IOException {
		return getZ(point.getX(), point.getY());
	}

	/**
	 * Returns the altimetric/bathymetric values of the given array of geographical coordinates.
	 * The value derives from the corresponding cell in the coordinate grid (120 x 120 cells per degree).
	 * If a coordinate is close to the cell border (< 0.1 * arc cell size), the result value is averaged with the value from the adjacent cell.
	 * @param points array of geographical coordinates (x = longitude value (-180 to 180), y = latitude value (-90 to 90))
	 * @return an array of the same shape as points, containing the altimetric/bathymetric value for the coordinate (in meters)
	 * @throws java.io.IOException if the netCDF variable <code>z</code> could not been read
	 */
	public short[] getZ(java.awt.geom.Point2D.Double[] points) throws java.io.IOException {
		short[] result = new short[points.length];
		for (int i = 0; i < points.length; i++) result[i] = getZ(points[i].getX(), points[i].getY());
		return result;
	}

	/**
	 * Returns the altimetric/bathymetric value of the given geographical coordinates.
	 * The value derives from the corresponding cell in the coordinate grid (120 x 120 cells per degree).
	 * If a coordinate is close to the cell border (< 0.1 * arc cell size), the result value is averaged with the value from the adjacent cell.
	 * @param x longitude coordinate (-180 to 180)
	 * @param y latitude coordinate (-90 to 90)
	 * @return the altimetric/bathymetric value for the coordinate (in meters)
	 * @throws NullPointerException if the netCDF file has not been opened or the variable <code>z</code> (containing the altimetry/bathymetry values) is not found
	 * @throws java.io.IOException if the netCDF variable <code>z</code> could not been read
	 */
	public short getZ(double x, double y) throws java.lang.NullPointerException, java.io.IOException {
		int resultInt = 0;
		
		if (ncFile == null) throw new NullPointerException("No netCDF file not open");
		if (var == null) throw new NullPointerException("Variable 'z' not found");
			
		double gridColD = (x + 180) * 120; // left border of cell
		boolean averageWithNextCol = false, averageWithNextRow = false;

		int gridCol, gridRow;
		if (gridColD < 0 || gridColD >= GRID_COLS) {
			gridCol = GRID_COLS - 1;
			averageWithNextCol = true;
		} else {
			gridCol = (int)Math.floor(gridColD);
			if (gridColD - gridCol < 0.1) {
				gridCol = (gridCol + GRID_COLS - 1) % GRID_COLS;
				averageWithNextCol = true;
			} else if (gridColD - gridCol > 0.9) {
				averageWithNextCol = true;
			}
		}
		double gridRowD = (90 - y) * 120; // upper border of cell
		if (gridRowD < 0) {
			gridRow = 0;
		} else if (gridRowD >= GRID_ROWS) {
			gridRow = GRID_ROWS - 1;
		} else {
			gridRow = (int)Math.floor(gridRowD);
			if (gridRowD - gridRow < 0.1 && gridRow > 0) {
				gridRow--;
				averageWithNextRow = true;
			} else if (gridRowD - gridRow > 0.9 && gridRow < GRID_ROWS - 1) {
				averageWithNextRow = true;
			}
		}
		
		int[] shape = new int[1];
		if (averageWithNextCol) shape[0] = 2;
		else shape[0] = 1;
		int[] origin = {GRID_COLS * gridRow + gridCol}; 
		
		short result = 0;
		try {
			Array arr = var.read(origin, shape);
			resultInt = arr.getShort(0);
			if (averageWithNextCol) resultInt += arr.getShort(1);
			if (averageWithNextRow) {
				origin[0] += GRID_COLS;
				arr = var.read(origin, shape);
				resultInt += arr.getShort(0);
				if (averageWithNextCol) resultInt += arr.getShort(1);
			}
			result = (short)(resultInt / ((averageWithNextCol ? 2 : 1) * (averageWithNextRow ? 2 : 1)));
		} catch (InvalidRangeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (debug) {
			System.out.println("x,y --- col,row (calculated) --- col[+],row[+] (actual): " + x + "," + y + " --- " + gridColD + "," + gridRowD + " --- " + gridCol + (averageWithNextCol ? "+" : "") + "," + gridRow + (averageWithNextRow ? "+" : ""));
	
			System.out.print("Grid cell indexes used: " + (GRID_COLS * gridRow + gridCol));
			if (averageWithNextCol) System.out.print(" " + (GRID_COLS * gridRow + (gridCol + 1) % GRID_COLS));
			if (averageWithNextRow) {
				System.out.print(" " + (GRID_COLS * (gridRow + 1) + gridCol));
				if (averageWithNextCol) System.out.print(" " + (GRID_COLS * (gridRow + 1) + (gridCol + 1) % GRID_COLS));
			}
			if (averageWithNextCol) System.out.print(", eastern adjacent cell used");
			if (averageWithNextRow) System.out.print(", southern adjacent cell used");
			System.out.println();
			
			/*System.out.print("Corresponding altimetry/bathymetry values: " + arr.getShort(GRID_COLS * gridRow + gridCol));
			if (averageWithNextCol) System.out.print(" " + arr.getShort(GRID_COLS * gridRow + (gridCol + 1) % GRID_COLS));
			if (averageWithNextRow) {
				System.out.print(" " + arr.getShort(GRID_COLS * (gridRow + 1) + gridCol));
				if (averageWithNextCol) System.out.print(" " + arr.getShort(GRID_COLS * (gridRow + 1) + (gridCol + 1) % GRID_COLS));
			}*/
			System.out.println();
			System.out.println("Result: " + result);
		}

		return result;
	}
	
	private void draw() {
	/*	try {
			if (arr == null) throw new Exception("Variable 'z' not found");
			
			long size = arr.getSize();
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 120; j++) {
					int index = 31104000 * i + 360 * j + 15552000 + 180;
					if (index >= size) return;
					System.out.print(arr.getShort(index) < 0 ? "~" : "@");
				}
				System.out.println();
			}
			
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}*/
	}
	
}
