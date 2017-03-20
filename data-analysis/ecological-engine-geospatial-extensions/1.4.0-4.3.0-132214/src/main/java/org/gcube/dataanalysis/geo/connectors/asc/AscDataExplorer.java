package org.gcube.dataanalysis.geo.connectors.asc;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;



public class AscDataExplorer {

	public double xOrigin;
	public double yOrigin;
	public int ncolumns;
	public int nrows;
	public double cellsize;
	public double dx;
	public double dy;
	
	public AscRaster ascFile;
	
	public AscDataExplorer(String file) throws Exception{
		AnalysisLogger.getLogger().debug("Managing Asc File: "+file);
		AscRasterReader reader = new AscRasterReader();
		int i=0;
		while(i<10){
			try{
				ascFile = reader.readRaster(file);
				break;
			}catch(Exception e){
				e.printStackTrace();
				AnalysisLogger.getLogger().debug("Error in reading remote file: "+file);
			}
			i++;
		}
		
		if (i==10)
			 throw new Exception("Reading Timeout for the file "+file);
		
		xOrigin=ascFile.getXll();
		yOrigin=ascFile.getYll();
		ncolumns = ascFile.getCols();
		nrows = ascFile.getRows();
		cellsize = ascFile.getCellsize();
		dx = ascFile.getdx();
		dy = ascFile.getdy();
		
		AnalysisLogger.getLogger().debug("Origin: "+xOrigin+","+yOrigin);
		AnalysisLogger.getLogger().debug("Cellsize: "+cellsize);
		AnalysisLogger.getLogger().debug("dx: "+dx+" dy:"+dy);
		AnalysisLogger.getLogger().debug("Rows: "+nrows+" Cols:"+ncolumns);
		
		
	}
	
	public List<Double> retrieveDataFromAsc( List<Tuple<Double>> triplets, int time) throws Exception{
		if (time>0)
			throw new Exception("No Time Dimension For ASC Files!");
		
		List<Double> values = new ArrayList<Double>();
		for (Tuple<Double> triplet:triplets){
			double x = triplet.getElements().get(0);
			double y = triplet.getElements().get(1);
			int j = ascFile.longitude2Index(x);
			int i = ascFile.latitude2Index(y);
			
			if ((j>ncolumns) || (j<0) || (i>nrows) || (i<0)){
				values.add(Double.NaN);
			}
			else{
				double value = ascFile.getValue(i, j);
				if (value==Double.parseDouble(ascFile.NDATA))
					values.add(Double.NaN);
				else
					values.add(value);
			}
			/*
			if (j>ncolumns){
				AnalysisLogger.getLogger().debug("Warning: Column Overflow: adjusting!");
				AnalysisLogger.getLogger().debug("Overflow: y:"+y+","+"x:"+x);
				AnalysisLogger.getLogger().debug("Overflow: iy:"+i+","+"jx:"+j);
				j=ncolumns;
			}
			if (i>nrows){
				AnalysisLogger.getLogger().debug("Warning: Row Overflow: adjusting!");
				AnalysisLogger.getLogger().debug("Overflow: y:"+y+","+"x:"+x);
				AnalysisLogger.getLogger().debug("Overflow: iy:"+i+","+"jx:"+j);
				i=nrows;
			}
			
			//AnalysisLogger.getLogger().debug("y:"+y+","+"x:"+x);
			//AnalysisLogger.getLogger().debug("iy:"+i+","+"jx:"+j);
			double value = ascFile.getValue(i, j);
			values.add(value);
			*/
		}
		
		return values;
	}
	
	public static void testReaders() throws Exception {
/*
//		File file = new File("http://thredds.d4science.org/thredds/fileServer/public/netcdf/sstrange.tiff");
		File file = new File("sstrange.tiff");
		AbstractGridFormat format = new GeoTiffFormat();
		StringBuilder buffer = new StringBuilder();

		buffer.append(file.getAbsolutePath()).append("\n");
//		Object o = file.toURI().toURL();
		Object o = file;
		if (format.accepts(o)) {
				buffer.append("ACCEPTED").append("\n");

				// getting a reader
				GeoTiffReader reader = new GeoTiffReader(o, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
						
				if (reader != null) {
					// reading the coverage
					GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
					buffer.append("CRS: ").append(coverage.getCoordinateReferenceSystem2D().toWKT()).append("\n");
					buffer.append("GG: ").append(coverage.getGridGeometry().toString()).append("\n");
					// display metadata
					IIOMetadataDumper iIOMetadataDumper = new IIOMetadataDumper(((GeoTiffReader) reader).getMetadata().getRootNode());
					buffer.append("TIFF metadata: ").append(iIOMetadataDumper.getMetadata()).append("\n");
					coverage.show();
					
//					PlanarImage.wrapRenderedImage(coverage.getRenderedImage()).getTiles();
				}
				else
					buffer.append("NOT ACCEPTED").append("\n");
		}
*/
		/*
		GeoTiff gt = new GeoTiff("sstrange.tiff");
		gt.read();
		System.out.println(gt.showInfo());
		gt.close();
		*/
//		GridDataset gds = ucar.nc2.dt.grid.GridDataset.open("sstrange.tiff");
//		List<GridDatatype> gridTypes = gds.getGrids();
//		GridDatatype grid = gds.findGridDatatype(gdt.getName());
//		GridCoordSystem gcs = grid.getCoordinateSystem();
		AscRasterReader reader = new AscRasterReader();
		AscRaster r1 = reader.readRaster( "ph.asc" );
		System.out.println("center:"+r1.getXll()+","+r1.getYll());
		System.out.println("cols:"+r1.getCols());
		System.out.println("data:"+r1.getValue(1, 1));
	}

	/*
	public void testBandNames() throws Exception {
		final File file = TestData.file(GeoTiffReaderTest.class, "wind.tiff");
		assertNotNull(file);
		final AbstractGridFormat format = new GeoTiffFormat();
		GridCoverage2D coverage = format.getReader(file).read(null);
		String band1Name = coverage.getSampleDimension(0).getDescription().toString();
		String band2Name = coverage.getSampleDimension(1).getDescription().toString();
		assertEquals("Band1", band1Name);
		assertEquals("Band2", band2Name);
	}
*/
	
	public static void main(String[] args) throws Exception {
//		AscDataExplorer ade = new AscDataExplorer("http://thredds.d4science.org/thredds/fileServer/public/netcdf/ph.asc");
		AscDataExplorer ade = new AscDataExplorer("https://dl.dropboxusercontent.com/u/12809149/geoserver-GetCoverage.image.asc");
		
		List<Tuple<Double>> triplets = new ArrayList<Tuple<Double>>();
		triplets.add(new Tuple<Double>(-180d,-90d));
		triplets.add(new Tuple<Double>(0d,0d));
		triplets.add(new Tuple<Double>(180d,90d));
		triplets.add(new Tuple<Double>(18.620429d,20.836419d));
		 
		List<Double> values = ade.retrieveDataFromAsc(triplets,0);
		for (Double value:values){
			AnalysisLogger.getLogger().debug("val:"+value);
		}
		
	}

}
