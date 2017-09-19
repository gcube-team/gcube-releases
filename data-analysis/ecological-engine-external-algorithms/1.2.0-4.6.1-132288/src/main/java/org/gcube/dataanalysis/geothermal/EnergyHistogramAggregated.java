package org.gcube.dataanalysis.geothermal;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;

public class EnergyHistogramAggregated extends EnergyHistogramPerYear {

	public static enum operators {
		AVG, SUM
	};

	static String aggregationParam = "Aggregation";

	@Override
	public String getDescription() {
		return "An algorithm reporting the aggregated energy in a time range produced by the countries contributing to EGIP";
	}

	@Override
	public List<StatisticalType> getInputParameters() {

		List<StatisticalType> inputs = super.getInputParameters();
		IOHelper.addEnumerateInput(inputs, operators.values(), aggregationParam, "Aggregation Function", operators.AVG.toString());

		return inputs;
	}

	@Override
	void fulfillDataset(String f_name, String f_area, double f_f1990_mwe, double f_f1995_mwt, double f_f1995_h_tjy, double f_f1995_mwe, double f_f2000_mwt, double f_f2000_h_tjy, double f_f2000_mwe, double f_f2005_mwt, double f_f2005_h_tjy, double f_f2005_mwe, double f_f2010_mwt, double f_f2010_h_tjy, double f_f2010_mwe, double f_f2013_mwe, int startYear, int endYear) {

		double aggregatedMWE = 0;
		double counterMWE = 0;
		double aggregatedMWT = 0;
		double counterMWT = 0;
		double aggregatedTJY = 0;
		double counterTJY = 0;

		if (startYear <= 1990 && endYear >= 1990) {
			aggregatedMWE += f_f1990_mwe;
			counterMWE++;
		}

		if (startYear <= 1995 && endYear >= 1995) {
			aggregatedMWE += f_f1995_mwe;
			aggregatedMWT += f_f1995_mwt;
			aggregatedTJY += f_f1995_h_tjy;
			counterMWE++;
			counterMWT++;
			counterTJY++;
		}
		if (startYear <= 2000 && endYear >= 2000) {
			aggregatedMWE += f_f2000_mwe;
			aggregatedMWT += f_f2000_mwt;
			aggregatedTJY += f_f2000_h_tjy;
			counterMWE++;
			counterMWT++;
			counterTJY++;
		}
		if (startYear <= 2005 && endYear >= 2005) {
			aggregatedMWE += f_f2005_mwe;
			aggregatedMWT += f_f2005_mwt;
			aggregatedTJY += f_f2005_h_tjy;
			counterMWE++;
			counterMWT++;
			counterTJY++;
		}
		if (startYear <= 2010 && endYear >= 2010) {
			aggregatedMWE += f_f2010_mwe;
			aggregatedMWT += f_f2010_mwt;
			aggregatedTJY += f_f2010_h_tjy;
			counterMWE++;
			counterMWT++;
			counterTJY++;
		}
		if (startYear <= 2013 && endYear >= 2013)
			aggregatedMWE += f_f2013_mwe;
		counterMWE++;

		String aggregation = config.getParam(aggregationParam);
		operators e = operators.AVG;
		if (aggregation != null)
			try {
				e = operators.valueOf(aggregation);
			} catch (Exception e1) {
			}

		switch (e) {
		case AVG:
			aggregatedMWE = aggregatedMWE / (double) counterMWE;
			aggregatedMWT = aggregatedMWT / (double) counterMWT;
			aggregatedTJY = aggregatedTJY / (double) counterTJY;
			datasetMWE.addValue(aggregatedMWE,f_name,e.toString()+"_MWe");
			datasetMWT.addValue(aggregatedMWT, f_name, e.toString()+"_MWt");
			datasetTJY.addValue(aggregatedTJY, f_name ,e.toString()+"_H_TJy");
			break;
		default:
			aggregatedTJY = aggregatedTJY / (double) counterTJY;
			datasetMWE = null;
			datasetMWT = null;
			datasetTJY.addValue(aggregatedTJY, f_name ,e.toString()+"_H_TJy");
			break;
		}
		
	}

}
