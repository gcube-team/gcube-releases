/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.shared.FileContentType;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 30, 2019
 */
public class TestManageCorrelationOutput {

	static String csvFile = "correlation_matrix_1.csv600998991800871270";
	static String csvFilePATH = "/tmp/correlation_matrix_1.csv600998991800871270";
	static String csvLegend = "correlation_matrix_legend_1.csv8236429741829427362";
	static String csvLegendPath = "/tmp/correlation_matrix_legend_1.csv8236429741829427362";

	public static void main(String[] args) {

		OutputFile outputFile = new OutputFile();
		outputFile.setDataType(FileContentType.CSV);
		outputFile.setName(csvFile);
		outputFile.setServerLocation(csvFilePATH);


		OutputFile outputLegend = new OutputFile();
		outputLegend.setDataType(FileContentType.CSV);
		outputLegend.setName(csvLegend);
		outputLegend.setServerLocation(csvLegendPath);


		OutputFile output3 = new OutputFile();
		output3.setDataType(FileContentType.IMAGE);
		output3.setName("fakeFile");
		output3.setServerLocation("/tmp/dropbox-antifreeze-V90v2S");


		//new PerformFishAnalyticsServiceImpl().manageOutputsForPerformFishAnalysis(Arrays.asList(outputFile, outputLegend, output3));

//		String id, String code, String name, String description,
//		ArrayList<KPI> listKPI, PopulationType populationType, int deepIndex
//
		List<KPI> listKPI = new ArrayList<KPI>();
		KPI kpi1 = new KPI();
		kpi1.setCode("P3");
		kpi1.setName("Mortalities - total %");
		listKPI.add(kpi1);

		KPI kpi2 = new KPI();
		kpi2.setCode("P4");
		kpi2.setName("Specific Growth Rate (SGR) %");

		listKPI.add(kpi2);

		System.out.println(getCodeKPIForName("Specific Growth Rate (SGR) %",listKPI));

	}

	/**
	 * Gets the code kpi for name.
	 *
	 * @param name the name
	 * @return the code kpi for name
	 */
	public static String getCodeKPIForName(String name, List<KPI> selectedKPIs){

		System.out.println("Selected KPIs: "+selectedKPIs);
		String purgedName = name.replaceAll("\\%", "").trim();
		for (KPI kpi : selectedKPIs) {
			String purgedKPIName = kpi.getName().replaceAll("\\%", "").trim();
			if(purgedKPIName.compareToIgnoreCase(purgedName)==0)
				return kpi.getCode();
		}

		return null;
	}

}
