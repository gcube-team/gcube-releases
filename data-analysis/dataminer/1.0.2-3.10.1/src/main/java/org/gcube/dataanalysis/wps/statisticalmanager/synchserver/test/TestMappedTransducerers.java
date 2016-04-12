package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.io.File;

import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ABSENCE_CELLS_FROM_AQUAMAPS;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GENERIC_CHARTS;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GEO_CHART;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.HCAF_FILTER;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.MAX_ENT_NICHE_MODELLING;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_MOST_OBSERVED_SPECIES;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_MOST_OBSERVED_TAXA;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_SPECIES_OBSERVATIONS_PER_LME_AREA;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_SPECIES_OBSERVATIONS_PER_YEAR;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_TAXA_OBSERVATIONS_PER_YEAR;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OCCURRENCE_ENRICHMENT;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PRESENCE_CELLS_GENERATION;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_SERIES_ANALYSIS;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR;
import org.junit.Test;
import org.n52.wps.io.data.GenericFileData;

public class TestMappedTransducerers {

	
	@Test
	public void testBionymLocal() throws Exception{
		BIONYM_LOCAL algorithm = new BIONYM_LOCAL();
		algorithm.setAccuracy_vs_Speed("MAX_ACCURACY");
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setScope("/d4science.research-infrastructures.eu/gCubeApps");
//		algorithm.setUserName("tester.user");
		algorithm.setSpeciesAuthorName("Gadus morhua (Linnaeus, 1758)");
		algorithm.setTaxa_Authority_File("ASFIS");
		algorithm.setParser_Name("SIMPLE");
		algorithm.setActivate_Preparsing_Processing(false);
		algorithm.setUse_Stemmed_Genus_and_Species(false);
		algorithm.setMatcher_1("GSAy");
		algorithm.setThreshold_1(0.6);
		algorithm.setMaxResults_1(3);
		algorithm.run();
	}
	
	/*
	@Test
	public void testBioclimateHCAF() throws Exception{
		BIOCLIMATE_HCAF algorithm = new BIOCLIMATE_HCAF();
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("test.user");
		
		File f = new File("./datasets/locallinkshcaf.txt");
		
		algorithm.setHCAF_Table_List(new GenericFileData(f,"text/csv"));
		algorithm.setHCAF_Table_Names("hcaf1|hcaf2");
		algorithm.run();
	}

	@Test
	public void testBioclimateHSPEN() throws Exception{
		BIOCLIMATE_HSPEN algorithm = new BIOCLIMATE_HSPEN();
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("test.user");
		File f = new File("./datasets/locallinkshspen.txt");
		
		algorithm.setHSPEN_Table_List(new GenericFileData(f,"text/csv"));
		algorithm.setHSPEN_Table_Names("hspen1|hspen2");
		algorithm.run();
	}
	
	@Test
	public void testBioclimateHSPEC() throws Exception{
		BIOCLIMATE_HSPEC algorithm = new BIOCLIMATE_HSPEC();
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("test.user");
		File f = new File("./datasets/locallinkshspec.txt");
		
		algorithm.setHSPEC_Table_List(new GenericFileData(f,"text/csv"));
		algorithm.setHSPEC_Table_Names("hspec1|hspec2");
		algorithm.setThreshold(0.5);
		algorithm.run();
	}
	
	@Test
	public void testHCAF_INTERPOLATION() throws Exception{
		HCAF_INTERPOLATION algorithm = new HCAF_INTERPOLATION();
//		algorithm.setScope("/gcube/devsec/devVRE");
//		algorithm.setScope("/d4science.research-infrastructures.eu/gCubeApps");
//		algorithm.setUserName("test.user");
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		File f2 = new File("./datasets/locallinkshcaf2.txt");
		
		algorithm.setFirstHCAF(new GenericFileData(f1,"text/csv"));
		algorithm.setSecondHCAF(new GenericFileData(f2,"text/csv"));
		algorithm.setYearStart(2015);
		algorithm.setYearEnd(2050);
		algorithm.setNumberOfInterpolations(1);
		algorithm.setInterpolationFunction("LINEAR");
		algorithm.run();
	}
	*/
	@Test
	public void testHCAF_FILTER() throws Exception{
		HCAF_FILTER algorithm = new HCAF_FILTER();
//		algorithm.setScope("/gcube/devsec");
	
		algorithm.setTable_Label("wps_hcaf_filtered");
		algorithm.setB_Box_Left_Lower_Lat(-17.098);
		algorithm.setB_Box_Left_Lower_Long(89.245);
		algorithm.setB_Box_Right_Upper_Lat(25.086);
		algorithm.setB_Box_Right_Upper_Long(147.642);
		
		algorithm.run();
	}
	
	@Test
	public void testABSENCE_CELLS_FROM_AQUAMAPS() throws Exception{
		ABSENCE_CELLS_FROM_AQUAMAPS algorithm = new ABSENCE_CELLS_FROM_AQUAMAPS();
//		algorithm.setScope("/gcube/devsec");
		File f1 = new File("./datasets/locallinkshspec1.txt");
			
		algorithm.setTable_Label("wps_absence_cells");
		algorithm.setAquamaps_HSPEC(new GenericFileData(f1,"text/csv"));
		algorithm.setNumber_of_Points(20);
		algorithm.setTake_Randomly(true);
		algorithm.setSpecies_Code("Fis-30189");
		
		algorithm.run();
	}
	
	@Test
	public void testPRESENCE_CELLS_GENERATION() throws Exception{
		PRESENCE_CELLS_GENERATION algorithm = new PRESENCE_CELLS_GENERATION();
//		algorithm.setScope("/gcube/devsec");
			
		algorithm.setTable_Label("wps_presence_cells");
		algorithm.setNumber_of_Points(20);
		algorithm.setSpecies_Code("Fis-30189");
		
		algorithm.run();
	}
	
	@Test
	public void testOBIS_MOST_OBSERVED_SPECIES() throws Exception{
		OBIS_MOST_OBSERVED_SPECIES algorithm = new OBIS_MOST_OBSERVED_SPECIES();
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("tester");
		algorithm.setSpecies_number("5");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		
		algorithm.run();
	}
	
	@Test
	public void testOBIS_MOST_OBSERVED_TAXA() throws Exception{
		OBIS_MOST_OBSERVED_TAXA algorithm = new OBIS_MOST_OBSERVED_TAXA();
//		algorithm.setScope("/gcube/devsec");
//			algorithm.setUserName("tester");
		algorithm.setTaxa_number("5");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		algorithm.setLevel("GENUS");
		algorithm.run();
	}

	
	@Test
	public void testOBIS_TAXA_OBSERVATIONS_PER_YEAR() throws Exception{
		OBIS_TAXA_OBSERVATIONS_PER_YEAR algorithm = new OBIS_TAXA_OBSERVATIONS_PER_YEAR();
//		algorithm.setScope("/gcube/devsec");
			
		algorithm.setSelected_taxonomy("Gadus");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		algorithm.setLevel("GENUS");
		algorithm.run();
	}
	
	
	@Test
	public void testOBIS_SPECIES_OBSERVATIONS_PER_LME_AREA() throws Exception{
		OBIS_SPECIES_OBSERVATIONS_PER_LME_AREA algorithm = new OBIS_SPECIES_OBSERVATIONS_PER_LME_AREA();
//		algorithm.setScope("/gcube/devsec");
			
		algorithm.setArea_type("NORTH_SEA");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		algorithm.setSelected_species("Gadus morhua");
		
		algorithm.run();
	}
	
	@Test
	public void testMOST_OBSERVED_SPECIES() throws Exception{
		OBIS_MOST_OBSERVED_SPECIES algorithm = new OBIS_MOST_OBSERVED_SPECIES();
//		algorithm.setScope("/gcube/devsec");
			
		algorithm.setSpecies_number("5");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		
		algorithm.run();
	}
	
	@Test
	public void testSPECIES_OBSERVATIONS_TREND_PER_YEAR() throws Exception{
		OBIS_SPECIES_OBSERVATIONS_PER_YEAR algorithm = new OBIS_SPECIES_OBSERVATIONS_PER_YEAR();
//		algorithm.setScope("/gcube/devsec");
			
		algorithm.setSelected_species("Gadus morhua");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		
		algorithm.run();
	}
	
	@Test
	public void testTAXONOMY_OBSERVATIONS_TREND_PER_YEAR() throws Exception{
		OBIS_TAXA_OBSERVATIONS_PER_YEAR algorithm = new OBIS_TAXA_OBSERVATIONS_PER_YEAR();
//		algorithm.setScope("/gcube/devsec");
			
		algorithm.setLevel("GENUS");
		algorithm.setSelected_taxonomy("Gadus|Latimeria");
		algorithm.setStart_year("1800");
		algorithm.setEnd_year("2013");
		
		algorithm.run();
	}
	
	/*
	@Test
	public void testPOINTS_TO_MAP() throws Exception{
		POINTS_TO_MAP algorithm = new POINTS_TO_MAP();
//		algorithm.setScope("/gcube/devsec/devVRE");
			
		algorithm.setMapName("Example map generated from WPS 2");
		algorithm.setxDimension("centerlong");
		algorithm.setyDimension("centerlat");
		algorithm.setInfo("faoaream");
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setInputTable(new GenericFileData(f1,"text/csv"));
		
		algorithm.run();
	}
	*/
	
	@Test
	public void testOCCURRENCE_ENRICHMENT() throws Exception{
		OCCURRENCE_ENRICHMENT algorithm = new OCCURRENCE_ENRICHMENT();
//		algorithm.setScope("/gcube/devsec/devVRE");
//		algorithm.setUserName("wps.test");
		File f1 = new File("./datasets/locallinksspecies2.txt");
		algorithm.setOccurrenceTable(new GenericFileData(f1,"text/csv"));
		algorithm.setScientificNameColumn("scientificname");
		algorithm.setLongitudeColumn("decimallongitude");
		algorithm.setLatitudeColumn("decimallatitude");
		algorithm.setTimeColumn("eventdate");
		algorithm.setOptionalFilter(" ");
		algorithm.setResolution(0.5);
		algorithm.setOutputTableName("wps_enrichment_test");
		algorithm.setLayers("http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/WOA2005TemperatureAnnual_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc");
		algorithm.setFeaturesNames("wind");
		algorithm.run();
	}
	
	@Test
	public void testXYEXTRACTOR() throws Exception{
		XYEXTRACTOR algorithm = new XYEXTRACTOR();
//		algorithm.setScope("/gcube/devsec/devVRE");
		
		algorithm.setBBox_LowerLeftLat(-17.098);
		algorithm.setBBox_LowerLeftLong(89.245);
		algorithm.setBBox_UpperRightLat(25.086);
		algorithm.setBBox_UpperRightLong(147.642);
		algorithm.setOutputTableLabel("wps_xy_extraction");
		algorithm.setTimeIndex(0);
		algorithm.setXResolution(0.5);
		algorithm.setYResolution(0.5);
		algorithm.setZ(0d);
		algorithm.setLayer("http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/WOA2005TemperatureAnnual_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc");
		
		algorithm.run();
	}
	
	
	
	
	//DONE adjust input FFT window samples to Integer in the Time series algorithm
	@Test
	public void testTIME_SERIES_ANALYSIS() throws Exception{
		TIME_SERIES_ANALYSIS algorithm = new TIME_SERIES_ANALYSIS();
//		algorithm.setScope("/gcube/devsec/devVRE");
		File f1 = new File("./datasets/locallinkstimeseries.txt");
		algorithm.setTimeSeriesTable(new GenericFileData(f1,"text/csv"));
		algorithm.setFFT_Window_Samples(12);
		algorithm.setAggregationFunction("SUM");
		algorithm.setSensitivity("LOW");
		algorithm.setSSA_Window_in_Samples(20);
		algorithm.setSSA_EigenvaluesThreshold(0.7);
		algorithm.setSSA_Points_to_Forecast(10);
		algorithm.setValueColum("x");
		
		algorithm.run();
	}

	@Test
	public void testMAX_ENT_NICHE_MODELLING() throws Exception{
		MAX_ENT_NICHE_MODELLING algorithm = new MAX_ENT_NICHE_MODELLING();
//		algorithm.setScope("/gcube/devsec/devVRE");

		algorithm.setXResolution(0.5);
		algorithm.setYResolution(0.5);
		algorithm.setDefaultPrevalence(0.5);
		algorithm.setLayers("http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/WOA2005TemperatureAnnual_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc");
		algorithm.setZ(0d);
		algorithm.setTimeIndex(0);
		algorithm.setMaxIterations(1000);
		algorithm.setSpeciesName("Mola mola");
		File f1 = new File("./datasets/locallinksspecies.txt");
		algorithm.setOccurrencesTable(new GenericFileData(f1,"text/csv"));
		algorithm.setOutputTableLabel("Mola mola maxent table");
		algorithm.setLongitudeColumn("decimallongitude");
		algorithm.setLatitudeColumn("decimallatitude");
		
		algorithm.run();
	}

	@Test
	public void testFAO_OCEAN_AREA_COLUMN_CREATOR() throws Exception{
		FAO_OCEAN_AREA_COLUMN_CREATOR algorithm = new FAO_OCEAN_AREA_COLUMN_CREATOR();
//		algorithm.setScope("/gcube/devsec/devVRE");

		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setInputTable(new GenericFileData(f1,"text/csv"));
		algorithm.setLatitude_Column("centerlat");
		algorithm.setLongitude_Column("centerlong");
		algorithm.setOutputTableName("wps_faoareacolumn");
		algorithm.setResolution(5);
		
		
		algorithm.run();
	}
	
	@Test
	public void testGEO_CHART() throws Exception{
		GEO_CHART algorithm = new GEO_CHART();
//		algorithm.setScope("/gcube/devsec/devVRE");

		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setInputTable(new GenericFileData(f1,"text/csv"));
		algorithm.setLatitude("centerlat");
		algorithm.setLongitude("centerlong");
		algorithm.setQuantities("depthsd");
		
		algorithm.run();
	}	
	
	
	@Test
	public void testvGENERIC_CHARTS() throws Exception{
		GENERIC_CHARTS algorithm = new GENERIC_CHARTS();
//		algorithm.setScope("/gcube/devsec/devVRE");

		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setInputTable(new GenericFileData(f1,"text/csv"));
		algorithm.setTopElementsNumber(10);
		algorithm.setAttributes("csquarecode|lme");
		algorithm.setQuantities("depthsd");
		
		algorithm.run();
	}	
}
