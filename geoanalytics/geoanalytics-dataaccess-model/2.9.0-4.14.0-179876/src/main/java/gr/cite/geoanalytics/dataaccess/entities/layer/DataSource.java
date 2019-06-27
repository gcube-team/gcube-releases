package gr.cite.geoanalytics.dataaccess.entities.layer;

public enum DataSource {
	PostGIS("PostGIS"),
	GeoTIFF("GeoTIFF"),
	NetCDF("NetCDF");
	
	private final String type;
	
    private DataSource(final String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }    
	
	public static boolean isPostGIS(Layer layer){
		return PostGIS.equals(layer.getDataSource());
	}
	public static boolean isGeoTIFF(Layer layer){
		return GeoTIFF.equals(layer.getDataSource());
	}
	public static boolean isNetCDF(Layer layer){
		return NetCDF.equals(layer.getDataSource());
	}
	
	public static boolean isPostGIS(DataSource dataSource){
		return PostGIS.equals(dataSource);
	}
	public static boolean isGeoTIFF(DataSource dataSource){
		return GeoTIFF.equals(dataSource);
	}
	public static boolean isNetCDF(DataSource dataSource){
		return NetCDF.equals(dataSource);
	}
	
	public static boolean isVector(DataSource dataSource){
		return isPostGIS(dataSource);
	}

	public static boolean isRaster(DataSource dataSource){
		return isGeoTIFF(dataSource) || isNetCDF(dataSource);
	}
	
	public String getImageType(){
		return ( isGeoTIFF(this) || isNetCDF(this) ) ? "RASTER" : "VECTOR";
	}
}
