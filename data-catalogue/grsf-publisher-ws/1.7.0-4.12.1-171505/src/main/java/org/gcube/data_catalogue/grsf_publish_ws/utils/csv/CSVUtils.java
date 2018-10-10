package org.gcube.data_catalogue.grsf_publish_ws.utils.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.TimeSeriesBean;
import org.gcube.datacatalogue.common.Constants;
import org.slf4j.LoggerFactory;


/**
 * Convert lists to csv format helpers
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CSVUtils {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CSVUtils.class);
	private static final String CSV_SEPARATOR = ",";
	private static final String UPLOAD_LOCATION_LOCAL = System.getProperty("java.io.tmpdir");
	private static final String GRSF_SUB_PATH = "GRSF_TIME_SERIES";
	public static final String CSV_EXTENSION = ".csv";

	/**
	 * Write a time series to a csv file, and returns the file reference.<br>
	 * Please give the timeSeries already sorted per year
	 * @param timeSeries
	 * @param relevantSources 
	 * @param <T>
	 * @param <T1>
	 */
	public static <T, T1> File listToCSV(List<TimeSeriesBean<T, T1>> timeSeries, String[] relevantSources){

		if(timeSeries == null || timeSeries.isEmpty()){
			logger.warn("The time series provided is null or empty ... " + timeSeries );
			return null;
		}else

			try{
				String fileName = UPLOAD_LOCATION_LOCAL + File.separator + GRSF_SUB_PATH + File.separator + "time_series_" + System.nanoTime() + CSV_EXTENSION;
				File file = new File(fileName);
				file.getParentFile().mkdirs();
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fo, "UTF-8"));

				Set<String> sources = new HashSet<String>(3);

				// discover how the header will look like 
				boolean isUnitPresent = false;
				boolean isValuePresent = false;
				boolean isSourcePresent = false;
				boolean isAssessmentPresent = false;
				boolean isDataOwnerPresent = false;

				for (TimeSeriesBean<T, T1> timeSeriesBean : timeSeries) {
					if(timeSeriesBean.isSourcePresent())
						isSourcePresent = true;
					if(timeSeriesBean.isAssessmentPresent())
						isAssessmentPresent = true;
					if(timeSeriesBean.isValuePresent())
						isValuePresent = true;
					if(timeSeriesBean.isUnitPresent())
						isUnitPresent = true;
					if(timeSeriesBean.isDataOwnerPresent())
						isDataOwnerPresent = true;
					if(isSourcePresent & isAssessmentPresent & isValuePresent & isUnitPresent & isDataOwnerPresent)
						break;
				}

				StringBuffer headerLine = new StringBuffer();
				headerLine.append(Constants.TIME_SERIES_YEAR_FIELD);

				if(isValuePresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(Constants.TIME_SERIES_VALUE_FIELD);
				}

				if(isUnitPresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(Constants.TIME_SERIES_UNIT_FIELD);
				}

				if(isSourcePresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(Constants.TIME_SERIES_DB_SOURCE_FIELD);
				}

				if(isDataOwnerPresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(Constants.TIME_SERIES_DATA_OWNER_FIELD);
				}

				if(isAssessmentPresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(Constants.TIME_SERIES_ASSESSMENT_FIELD);
				}

				bw.write(headerLine.toString());
				bw.newLine();
				bw.flush();

				// now iterate over the rows.. they are already sorted in ascending order
				for (TimeSeriesBean<T, T1> bean : timeSeries)
				{
					StringBuffer oneLine = new StringBuffer();
					oneLine.append(bean.getYear() > 0 ? bean.getYear() : "");

					if(isValuePresent){
						oneLine.append(CSV_SEPARATOR);
						oneLine.append(bean.getValue() != null? bean.getValue().toString().contains(",") ? "\"" + bean.getValue() + "\"" : bean.getValue() : "");
					}

					if(isUnitPresent){
						oneLine.append(CSV_SEPARATOR);
						oneLine.append(bean.getUnit() != null? bean.getUnit() : "");
					}

					if(isSourcePresent){
						oneLine.append(CSV_SEPARATOR);
						oneLine.append(bean.getDatabaseSource() != null? bean.getDatabaseSource() : "");
						if(bean.getDatabaseSource() != null && !bean.getDatabaseSource().isEmpty())
							sources.add(bean.getDatabaseSource());
					}

					if(isDataOwnerPresent){
						oneLine.append(CSV_SEPARATOR);
						oneLine.append(bean.getDataOwner() != null? bean.getDataOwner() : "");
					}

					if(isAssessmentPresent){
						oneLine.append(CSV_SEPARATOR);
						oneLine.append(bean.getAssessment() != null? bean.getAssessment() : "");
					}

					bw.write(oneLine.toString());
					bw.newLine();
					bw.flush();
				}

				bw.close();

				// Add names of the sources to the file's name
				if(sources.isEmpty()){
					relevantSources[0] = "";
				}else{
					for (String source : sources) {
						relevantSources[0] += source + "_";
					}
					relevantSources[0] = relevantSources[0].substring(0, relevantSources[0].length() - 1);
				}

				// on exit delete it...
				file.deleteOnExit();

				// file created
				logger.debug("CSV file created correctly on this machine!");

				return file;
			}
		catch(Exception e){
			logger.error("Failed to create csv file for time series", e);
			return null;
		}
	}


}