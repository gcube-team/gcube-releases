package org.gcube.data_catalogue.grsf_publish_ws.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.gcube.data_catalogue.grsf_publish_ws.json.input.TimeSeriesBean;
import org.slf4j.LoggerFactory;


/**
 * Convert lists to csv format helpers
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CSVHelpers {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CSVHelpers.class);
	private static final String CSV_SEPARATOR = ",";
	private static final String UPLOAD_LOCATION_LOCAL = System.getProperty("java.io.tmpdir");
	private static final String GRSF_SUB_PATH = "GRSF_TIME_SERIES";
	public static final String CSV_EXTENSION = ".csv";

	/**
	 * Write a time series to a csv file, and returns the file reference.<br>
	 * Please give the timeSeries already sorted per year
	 * @param timeSeries
	 * @param <T>
	 * @param <T1>
	 */
	public static <T, T1> File listToCSV(List<TimeSeriesBean<T, T1>> timeSeries){

		if(timeSeries == null || timeSeries.isEmpty()){
			logger.warn("The time series provided is null or empty ... " + timeSeries );
			return null;
		}else

			try{
				String fileName = UPLOAD_LOCATION_LOCAL + File.separator + GRSF_SUB_PATH + File.separator + "time_series_" + System.currentTimeMillis() + CSV_EXTENSION;
				File file = new File(fileName);
				file.getParentFile().mkdirs();
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fo, "UTF-8"));

				// discover how the header will look like 
				boolean isUnitPresent = timeSeries.get(0).isUnitPresent();
				boolean isValuePresent = true;
				boolean isSourcePresent = true;
				boolean isAssessmentPresent = true;

				StringBuffer headerLine = new StringBuffer();
				headerLine.append(TimeSeriesBean.YEAR_FIELD);

				if(isValuePresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(TimeSeriesBean.VALUE_FIELD);
				}

				if(isUnitPresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(TimeSeriesBean.UNIT_FIELD);
				}

				if(isSourcePresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(TimeSeriesBean.SOURCE_FIELD);
				}
				
				if(isAssessmentPresent){
					headerLine.append(CSV_SEPARATOR);
					headerLine.append(TimeSeriesBean.ASSESSMENT_FIELD);
				}

				bw.write(headerLine.toString());
				bw.newLine();
				bw.flush();

				// now iterate over the rows.. they are already sorted in ascending order
				for (TimeSeriesBean<T, T1> bean : timeSeries)
				{
					StringBuffer oneLine = new StringBuffer();
					oneLine.append(bean.getYear());

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
						oneLine.append(bean.getSource() != null? bean.getSource() : "");
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

				// file created
				logger.debug("CSV file created correctly on this machine!");

				// on exit delete it...
				file.deleteOnExit();
				return file;
			}
		catch(Exception e){
			logger.error("Failed to create csv file for time series", e);
			return null;
		}
	}

}