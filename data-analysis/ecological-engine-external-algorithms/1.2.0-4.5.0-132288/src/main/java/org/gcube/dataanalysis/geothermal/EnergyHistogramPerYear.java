package org.gcube.dataanalysis.geothermal;

import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class EnergyHistogramPerYear extends AbstractProcess {
	
	@Override
	public String getDescription() {
		return "An algorithm reporting the energy produced per year by the countries contributing to EGIP";
	}
	
	DefaultCategoryDataset datasetMWE = new DefaultCategoryDataset();
	DefaultCategoryDataset datasetMWT = new DefaultCategoryDataset();
	DefaultCategoryDataset datasetTJY = new DefaultCategoryDataset();
	

	@Override
	void initDatasets() {
	
	}

	@Override
	void fulfillDataset(String f_name, String f_area, double f_f1990_mwe, double f_f1995_mwt, double f_f1995_h_tjy, double f_f1995_mwe, double f_f2000_mwt, double f_f2000_h_tjy, double f_f2000_mwe, double f_f2005_mwt, double f_f2005_h_tjy, double f_f2005_mwe, double f_f2010_mwt, double f_f2010_h_tjy, double f_f2010_mwe, double f_f2013_mwe,int startYear, int endYear) {
		
		if (startYear <= 1990 && endYear >= 1990)
			datasetMWE.addValue(f_f1990_mwe, "1990", f_name);
		if (startYear <= 1995 && endYear >= 1995) {
			datasetMWE.addValue(f_f1995_mwe, "1995", f_name);
			datasetMWT.addValue(f_f1995_mwt, "1995",f_name );
			datasetTJY.addValue(f_f1995_h_tjy, "1995",f_name );
		}
		if (startYear <= 2000 && endYear >= 2000) {
			datasetMWE.addValue(f_f2000_mwe,  "2000",f_name);
			datasetMWT.addValue(f_f2000_mwt, "2000",f_name );
			datasetTJY.addValue(f_f2000_h_tjy, "2000",f_name );
		}
		if (startYear <= 2005 && endYear >= 2005) {
			datasetMWE.addValue(f_f2005_mwe, "2005",f_name);
			datasetMWT.addValue(f_f2005_mwt, "2005",f_name );
			datasetTJY.addValue(f_f2005_h_tjy, "2005",f_name );
		}
		if (startYear <= 2010 && endYear >= 2010) {
			datasetMWE.addValue(f_f2010_mwe,"2010",f_name);
			datasetMWT.addValue(f_f2010_mwt, "2010",f_name );
			datasetTJY.addValue(f_f2010_h_tjy, "2010",f_name );
		}
		if (startYear <= 2013 && endYear >= 2013)
			datasetMWE.addValue(f_f2013_mwe,"2013",f_name );
		}

	@Override
	JFreeChart createChartForMWE() {
		if (datasetMWE==null) return null;
		return HistogramGraph.createStaticChart(datasetMWE);
	}

	@Override
	JFreeChart createChartForMWT() {
		if (datasetMWT==null) return null;
		return HistogramGraph.createStaticChart(datasetMWT);
	}

	@Override
	JFreeChart createChartForTJY() {
		if (datasetTJY==null) return null;
		return HistogramGraph.createStaticChart(datasetTJY);
	}

	@Override
	void renderChartForMWE() {
		HistogramGraph tsg = new HistogramGraph("MWE");
		tsg.render(datasetMWE);
	}

	@Override
	void renderChartForMWT() {
		HistogramGraph tsg2 = new HistogramGraph("MWT");
		tsg2.render(datasetMWT);
	}

	@Override
	void renderChartForTJY() {
		HistogramGraph tsg3 = new HistogramGraph("TJY");
		tsg3.render(datasetTJY);
	}

}
