package org.gcube.dataanalysis.geo.batch;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.insertion.ThreddsFetcher;

public class ThreddsMetadataBatchInserter {
	
	public static void main(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		ThreddsFetcher tf = new ThreddsFetcher("/gcube/devsec");
		tf.fetch("http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
	}
}
