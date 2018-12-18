package org.gcube.datapublishing.sdmx.datasource.data;

import java.util.List;

import org.gcube.datapublishing.sdmx.datasource.series.model.SeriesModel;

public interface ResultSetExtractor
{

	public void setMetadataProvider (SDMXMetadataProvider sdmxMetadataProvider);
	
	public List<SeriesModel> getSeriesList ();


}
