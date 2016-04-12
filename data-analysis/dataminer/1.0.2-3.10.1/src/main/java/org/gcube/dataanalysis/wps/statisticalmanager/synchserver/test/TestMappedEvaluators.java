package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.io.File;

import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.HRS;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.MAPS_COMPARISON;
import org.junit.Test;
import org.n52.wps.io.data.GenericFileData;

public class TestMappedEvaluators {

	
	@Test
	public void testMAPS_COMPARISON() throws Exception{
		MAPS_COMPARISON algorithm = new MAPS_COMPARISON();
		
//		algorithm.setScope("/gcube/devsec");
		
		algorithm.setKThreshold(0.5);
		algorithm.setLayer_1("http://geoserver.d4science.org/geoserver/ows?service=wfs&version=1.0.0&request=GetFeature&typeName=stat9fca7a86789046edbf4c697b9ef8f8f4&format=json");
		algorithm.setLayer_2("http://geoserver.d4science.org/geoserver/ows?service=wfs&version=1.0.0&request=GetFeature&typeName=stat9fca7a86789046edbf4c697b9ef8f8f4&format=json");
		algorithm.setTimeIndex_1(0);
		algorithm.setTimeIndex_2(0);
		algorithm.setValuesComparisonThreshold(0.5);
		algorithm.setZ(0);
		
		algorithm.run();
	}
	
	@Test
	public void testHRS() throws Exception{
		HRS algorithm = new HRS();
		
//		algorithm.setScope("/gcube/devsec");
		
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setProjectingAreaTable(new GenericFileData(f1,"text/csv"));
		algorithm.setNegativeCasesTable(new GenericFileData(f1,"text/csv"));
		algorithm.setPositiveCasesTable(new GenericFileData(f1,"text/csv"));
		algorithm.setOptionalCondition("where oceanarea>0");
		algorithm.setFeaturesColumns("depthmin|depthmax");
		
		algorithm.run();
	}
	
	
		@Test
		public void testQualityAnalysis() throws Exception{
			HRS algorithm = new HRS();
			
//			http://localhost:8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.QUALITY_ANALYSIS&DataInputs=user.name=test.user;scope=/gcube/devsec;NegativeCasesTableKeyColumn=csquarecode;DistributionTableProbabilityColumn=probability;PositiveCasesTableKeyColumn=csquarecode;PositiveThreshold=0.8;PositiveCasesTable=http://goo.gl/DEYAbT;DistributionTableKeyColumn=csquarecode;DistributionTable=http://goo.gl/DEYAbT;NegativeThreshold=0.3;NegativeCasesTable=http://goo.gl/DEYAbT;
				
//			algorithm.setScope("/gcube/devsec");
			
			File f1 = new File("./datasets/locallinkshcaf1.txt");
			algorithm.setProjectingAreaTable(new GenericFileData(f1,"text/csv"));
			algorithm.setNegativeCasesTable(new GenericFileData(f1,"text/csv"));
			algorithm.setPositiveCasesTable(new GenericFileData(f1,"text/csv"));
			algorithm.setOptionalCondition("where oceanarea>0");
			algorithm.setFeaturesColumns("depthmin|depthmax");
			
			algorithm.run();
		}
	
	
}
