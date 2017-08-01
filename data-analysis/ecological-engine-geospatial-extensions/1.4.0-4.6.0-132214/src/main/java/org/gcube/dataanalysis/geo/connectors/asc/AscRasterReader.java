package org.gcube.dataanalysis.geo.connectors.asc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.geo.utils.transfer.TransferUtil;

/**
 * A class which reads an ESRI ASCII raster file into a Raster
 */
public class AscRasterReader {
	String noData = AscRaster.DEFAULT_NODATA;
	Pattern header = Pattern.compile("^(\\w+)\\s+(-?\\d+(.\\d+)?)");

	public static void main(String[] args) throws IOException {
		AscRasterReader rt = new AscRasterReader();
		rt.readRaster("data/test.asc");
	}

	/**
	 * The most useful method - reads a raster file, and returns a Raster object.
	 * 
	 * Throws standard IOExceptions associated with opening and reading files, and RuntimeExceptions if there are problems with the file format
	 * 
	 * @param filename
	 * @return the Raster object read in from the file
	 * @throws IOException
	 */
	public AscRaster readRaster(String filename) throws IOException, RuntimeException {
		AscRaster raster = new AscRaster();
		BufferedReader input = null;
		URLConnection urlConn = null;
		if (filename.startsWith("http")) {
			AnalysisLogger.getLogger().debug("Getting file from http");
			/*
			 * URL fileurl = new URL(filename); urlConn = fileurl.openConnection(); urlConn.setConnectTimeout(120000); urlConn.setReadTimeout(1200000); urlConn.setAllowUserInteraction(false); urlConn.setDoOutput(true); input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			 */
			// using Manzi's data Transfer to overcome https issues
			try {
				input = new BufferedReader(new InputStreamReader(TransferUtil.getInputStream(new URI(filename), 120000)));
			} catch (URISyntaxException e) {
				AnalysisLogger.getLogger().debug("Error: Bad URI " + filename);
			}
		} else {
			AnalysisLogger.getLogger().debug("Getting file from local file");
			input = new BufferedReader(new FileReader(filename));
		}
		try {
			
//			 while( input.ready() )
			while (true) 
				 {
				String line = input.readLine();
				if (line==null)
					break;
				if (line != null && line.length() > 0)
					line = line.trim();
				
				Matcher headMatch = header.matcher(line);
				// Match all the heads
				if (headMatch.matches()) {
					String head = headMatch.group(1);
					String value = headMatch.group(2);
					if (head.equalsIgnoreCase("nrows"))
						raster.rows = Integer.parseInt(value);
					else if (head.equalsIgnoreCase("ncols"))
						raster.cols = Integer.parseInt(value);
					else if (head.equalsIgnoreCase("xllcorner"))
						raster.xll = Double.parseDouble(value);
					else if (head.equalsIgnoreCase("yllcorner"))
						raster.yll = Double.parseDouble(value);
					else if (head.equalsIgnoreCase("NODATA_value"))
						raster.NDATA = value;
					else if (head.equals("cellsize"))
						raster.cellsize = Double.parseDouble(value);
					else if (head.equals("dx"))
						raster.dx = Double.parseDouble(value);
					else if (head.equals("dy"))
						raster.dy = Double.parseDouble(value);
					else
						System.out.println("Unknown setting: " + line);
				} else if (line.matches("^-?\\d+.*")) {
					// System.out.println( "Processing data section");
					// Check that data is set up!
					// Start processing numbers!
					int row = 0;
					double[][] data = new double[raster.rows][];
					while (true) {
						line = line.trim();
						// System.out.println( "Got data row: " + line );
						String[] inData = line.split("\\s+");
						double[] numData = new double[raster.cols];
						if (inData.length != numData.length) {
							System.out.println(inData);
							throw new RuntimeException("Wrong number of columns: Expected " + raster.cols + " got " + inData.length + " for line \n" + line);
						}
						for (int col = 0; col < raster.cols; col++) {
							if (inData[col].equals(noData))
								numData[col] = Double.NaN;
							else
								numData[col] = Double.parseDouble(inData[col]);
						}
						data[row] = numData;
						// Ugly backward input structure...
						line = input.readLine();
//						if (input.ready())
						if (line==null)
							break;
//						else
//							break;
						row++;
					}
					if (row != raster.rows - 1)
						throw new RuntimeException("Wrong number of rows: expected " + raster.rows + " got " + (row + 1));
					raster.data = data;
				} else {
					if (line.length() >= 0 && !line.matches("^\\s*$"))
						AnalysisLogger.getLogger().debug("Unknown line: " + line);
				}
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("ASC Reader: Finished to read the stream");
		}
		if (input != null) {
			input.close();
			if (urlConn != null && urlConn.getInputStream() != null)
				urlConn.getInputStream().close();
		}
		return raster;
	}
}
