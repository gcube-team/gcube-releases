package org.gcube.dataanalysis.geothermal;

import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;

public class EnergyTrends extends AbstractProcess {

	@Override
	public String getDescription() {
		return "An algorithm reporting the energy trends for the countries contributing to EGIP";
	}
	
	TimeSeriesCollection datasetMWE = new TimeSeriesCollection();
	TimeSeriesCollection datasetMWT = new TimeSeriesCollection();
	TimeSeriesCollection datasetTJY = new TimeSeriesCollection();

	@Override
	void initDatasets() {

	}

	@Override
	void fulfillDataset(String f_name, String f_area, double f_f1990_mwe, double f_f1995_mwt, double f_f1995_h_tjy, double f_f1995_mwe, double f_f2000_mwt, double f_f2000_h_tjy, double f_f2000_mwe, double f_f2005_mwt, double f_f2005_h_tjy, double f_f2005_mwe, double f_f2010_mwt, double f_f2010_h_tjy, double f_f2010_mwe, double f_f2013_mwe, int startYear, int endYear) {

		final TimeSeries seriesMWE = new TimeSeries(f_name);
		final TimeSeries seriesMWT = new TimeSeries(f_name);
		final TimeSeries seriesTJY = new TimeSeries(f_name);

		if (startYear <= 1990 && endYear >= 1990)
			seriesMWE.add(new Year(1990), f_f1990_mwe);

		if (startYear <= 1995 && endYear >= 1995) {
			seriesMWE.add(new Year(1995), f_f1995_mwe);
			seriesMWT.add(new Year(1995), f_f1995_mwt);
			seriesTJY.add(new Year(1995), f_f2000_h_tjy);
		}

		if (startYear <= 2000 && endYear >= 2000) {
			seriesMWE.add(new Year(2000), f_f2000_mwe);
			seriesMWT.add(new Year(2000), f_f2000_mwt);
			seriesTJY.add(new Year(2000), f_f2000_h_tjy);
		}

		if (startYear <= 2005 && endYear >= 2005) {
			seriesMWE.add(new Year(2005), f_f2005_mwe);
			seriesMWT.add(new Year(2005), f_f2005_mwt);
			seriesTJY.add(new Year(2005), f_f2005_h_tjy);
		}
		if (startYear <= 2010 && endYear >= 2010) {
			seriesMWE.add(new Year(2010), f_f2010_mwe);
			seriesMWT.add(new Year(2010), f_f2010_mwt);
			seriesTJY.add(new Year(2010), f_f2010_h_tjy);
		}
		if (startYear <= 2013 && endYear >= 2013) {
			seriesMWE.add(new Year(2013), f_f2013_mwe);
		}

		datasetMWE.addSeries(seriesMWE);
		datasetMWT.addSeries(seriesMWT);
		datasetTJY.addSeries(seriesTJY);
	}

	@Override
	JFreeChart createChartForMWE() {

		return TimeSeriesGraph.createStaticChart(datasetMWE, "yyyy");
	}

	@Override
	JFreeChart createChartForMWT() {
		return TimeSeriesGraph.createStaticChart(datasetMWT, "yyyy");
	}

	@Override
	JFreeChart createChartForTJY() {
		return TimeSeriesGraph.createStaticChart(datasetTJY, "yyyy");
	}

	@Override
	void renderChartForMWE() {
		TimeSeriesGraph tsg = new TimeSeriesGraph("MWE");
		tsg.timeseriesformat = "yyyy";
		tsg.render(datasetMWE);
	}

	@Override
	void renderChartForMWT() {
		TimeSeriesGraph tsg2 = new TimeSeriesGraph("MWT");
		tsg2.timeseriesformat = "yyyy";
		tsg2.render(datasetMWT);
	}

	@Override
	void renderChartForTJY() {
		TimeSeriesGraph tsg3 = new TimeSeriesGraph("TJY");
		tsg3.timeseriesformat = "yyyy";
		tsg3.render(datasetTJY);
	}

}
