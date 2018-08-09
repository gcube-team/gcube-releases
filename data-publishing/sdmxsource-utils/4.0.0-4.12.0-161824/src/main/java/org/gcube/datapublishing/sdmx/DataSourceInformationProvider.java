package org.gcube.datapublishing.sdmx;

import java.util.List;
import java.util.Random;

import org.gcube.datapublishing.sdmx.is.data.ISDataSourceDataReader;
import org.gcube.datapublishing.sdmx.model.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceInformationProvider {


	public static DataSource getDataSource ()
	{
		Logger log = LoggerFactory.getLogger(DataSourceInformationProvider.class);
		ISDataSourceDataReader isReader = new ISDataSourceDataReader();
		List<DataSource> dataSources = isReader.getDataSources();
		DataSource response = null;
		int nDataSources = dataSources.size();
		log.debug("Found "+nDataSources+" instances in this VRE");
	
		if (nDataSources>0)
		{
			Random random = new Random();
			int chosenDataSource = random.nextInt(nDataSources);
			log.debug("Taken Data Source "+chosenDataSource);
			response = dataSources.get(chosenDataSource);
			log.debug("Data Source "+response.getName());
		}
		else
		{
			log.debug("No Data Source found");
		}
		
		return response;
	}
	
	

}
