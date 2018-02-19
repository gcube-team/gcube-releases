package org.gcube.dataanalysis.geothermal;

public class EnergyHistogramPerCountry extends EnergyHistogramPerYear {

	@Override
	public String getDescription() {
		return "An algorithm reporting the energy produced by the countries contributing to EGIP";
	}
	
	@Override
	void fulfillDataset(String f_name, String f_area, double f_f1990_mwe, double f_f1995_mwt, double f_f1995_h_tjy, double f_f1995_mwe, double f_f2000_mwt, double f_f2000_h_tjy, double f_f2000_mwe, double f_f2005_mwt, double f_f2005_h_tjy, double f_f2005_mwe, double f_f2010_mwt, double f_f2010_h_tjy, double f_f2010_mwe, double f_f2013_mwe, int startYear, int endYear) {

		if (startYear <= 1990 && endYear >= 1990)
			datasetMWE.addValue(f_f1990_mwe, f_name, "1990");
		if (startYear <= 1995 && endYear >= 1995) {
			datasetMWE.addValue(f_f1995_mwe, f_name, "1995");
			datasetMWT.addValue(f_f1995_mwt, f_name, "1995");
			datasetTJY.addValue(f_f1995_h_tjy, f_name, "1995");
		}
		if (startYear <= 2000 && endYear >= 2000) {
			datasetMWE.addValue(f_f2000_mwe, f_name, "2000");
			datasetMWT.addValue(f_f2000_mwt, f_name, "2000");
			datasetTJY.addValue(f_f2000_h_tjy, f_name, "2000");
		}
		if (startYear <= 2005 && endYear >= 2005) {
			datasetMWE.addValue(f_f2005_mwe, f_name, "2005");
			datasetMWT.addValue(f_f2005_mwt, f_name, "2005");
			datasetTJY.addValue(f_f2005_h_tjy, f_name, "2005");
		}
		if (startYear <= 2010 && endYear >= 2010) {
			datasetMWE.addValue(f_f2010_mwe, f_name, "2010");
			datasetMWT.addValue(f_f2010_mwt, f_name, "2010");
			datasetTJY.addValue(f_f2010_h_tjy, f_name, "2010");
		}
		if (startYear <= 2013 && endYear >= 2013)
			datasetMWE.addValue(f_f2013_mwe, f_name, "2013");

	}

}
