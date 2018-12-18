package org.gcube.dataanalysis.geo.connectors.asc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;

/**
 * Modified from java-esri-ascii
 * @author coro
 *
 */

public class AscRasterWriter {

	

		NumberFormat cellFormat = null;
		String nodataString = AscRaster.DEFAULT_NODATA;
		
		/**
		 * Writes out the given Raster object to the given filename.
		 * 
		 * Throws the exceptions associated with filehandling
		 * @param filename
		 * @param r
		 * @throws IOException
		 */
		public void writeRaster( String filename, AscRaster r ) throws IOException
		{
			File f = new File( filename );
			if( f.exists() ) f.delete();
			if( ! f.createNewFile() ) throw new RuntimeException( "Could not create file for some reason!");
			PrintStream o = new PrintStream( f );
			o.println( "ncols " + r.getCols() );
			o.println( "nrows " + r.getRows() );
			o.println( "xllcorner " + r.getXll() );
			o.println( "yllcorner " + r.getYll());
			if (r.getCellsize()>0)
				o.println( "cellsize " + r.getCellsize() );
			else
			{
				o.println( "dx " + r.getdx() );
				o.println( "dy " + r.getdy() );
			}
			o.println( "NODATA_value " + r.getNDATA()  );
			
			double[][] values = r.getData();
			//for(int k=values.length-1;k>=0;k-- )
			for(int k=0;k<values.length;k++ )
			{
				double[] row  =values[k];
				StringBuffer b = new StringBuffer();
				for( int i = 0; i < row.length; i++ )
				{
					if( Double.isNaN( row[i] )  ) b.append( r.getNDATA() );
					else if( cellFormat != null ) b.append( cellFormat.format( row[i] ));
					else b.append( row[i] );
					if( i < row.length-1 ) b.append(  " "  );
				}
				o.println( b );
			}
			o.close();
		}
		
		public void writeRasterInvertYAxis( String filename, AscRaster r ) throws IOException
		{
			File f = new File( filename );
			if( f.exists() ) f.delete();
			if( ! f.createNewFile() ) throw new RuntimeException( "Could not create file for some reason!");
			PrintStream o = new PrintStream( f );
			o.println( "ncols " + r.getCols() );
			o.println( "nrows " + r.getRows() );
			o.println( "xllcorner " + r.getXll() );
			o.println( "yllcorner " + r.getYll());
			if (r.getCellsize()>0)
				o.println( "cellsize " + r.getCellsize() );
			else
			{
				o.println( "dx " + r.getdx() );
				o.println( "dy " + r.getdy() );
			}
			o.println( "NODATA_value " + r.getNDATA()  );
			
			double[][] values = r.getData();
			for(int k=values.length-1;k>=0;k-- )
//			for(int k=0;k<values.length;k++ )
			{
				double[] row  =values[k];
				StringBuffer b = new StringBuffer();
				for( int i = 0; i < row.length; i++ )
				{
					if( Double.isNaN( row[i] )  ) b.append( r.getNDATA() );
					else if( cellFormat != null ) b.append( cellFormat.format( row[i] ));
					else b.append( row[i] );
					if( i < row.length-1 ) b.append(  " "  );
				}
				o.println( b );
			}
			o.close();
		}
		
		/**
		 * Shortcut method, if you just have some data and want to write it out as a Raster.
		 * 
		 * There is no error checking at the moment (e.g. about equal size rows)
		 * @param filename
		 * @param data
		 * @param xll
		 * @param yll
		 * @param size
		 * @param ndata
		 * @throws IOException
		 */
		public void writeRaster( String filename, double[][] data, double xll, double yll, double size, String ndata ) throws IOException
		{
			writeRaster( filename, AscRaster.getTempRaster( data, xll, yll, size, ndata ) );
		}
		
		public void writeRasterInvertYAxis( String filename, double[][] data, double xll, double yll, double size, String ndata ) throws IOException
		{
			writeRasterInvertYAxis( filename, AscRaster.getTempRaster( data, xll, yll, size, ndata ) );
		}
		
		/**
		 * Can be used to set a number format for the cells. For example, if they are all integer
		 * values, you can set an integer format. This should help with roundtrippability for
		 * existing Raster files
		 * @param format
		 */
		public void setCellFormat( NumberFormat format )
		{
			cellFormat = format;
		}
		
}
