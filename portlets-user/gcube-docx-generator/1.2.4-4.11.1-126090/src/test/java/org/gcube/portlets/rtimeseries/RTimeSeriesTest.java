package org.gcube.portlets.rtimeseries;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.docxgenerator.transformer.RTimeSeriesTransform;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class RTimeSeriesTest {
	
	private static final String pathCsvFile = "/src/test/resurces/fileCsv.csv";
	private static RTimeSeriesTransform rtsTransform; 
	private static RepTimeSeries rts;
	
	@BeforeClass
	public static void initTest() {
		rtsTransform = new RTimeSeriesTransform();
		rts = new RepTimeSeries(null, null, pathCsvFile);
	}

	@Test
	@Ignore
	public void doxGenerationTest() throws Exception {
		WordprocessingMLPackage wmlPack = WordprocessingMLPackage.createPackage();
	
//		rtsTransform.transform(rts, wmlPack);
		wmlPack.save(new java.io.File("TestRTS.docx"));
	}
}
