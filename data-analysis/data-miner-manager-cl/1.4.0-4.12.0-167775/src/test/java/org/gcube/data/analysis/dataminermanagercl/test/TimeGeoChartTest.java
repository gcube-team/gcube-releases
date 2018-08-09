package org.gcube.data.analysis.dataminermanagercl.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitor;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ImageResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.TableResource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ColumnListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ColumnParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TimeGeoChartTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(TimeGeoChartTest.class);

	public void testExecute() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test TimeGeoChart");
			try {
				DataMinerService dataMinerService = new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				Operator timeGeoChartOperator = sClient
						.getOperatorById(OperatorId.TIME_GEO_CHART.toString());

				if (timeGeoChartOperator == null) {
					logger.error("Operator not found");
				} else {
					logger.debug("Operator Name: " + timeGeoChartOperator.getName()
							+ " (" + timeGeoChartOperator.getId() + ")");
					logger.debug("Operator: " + timeGeoChartOperator);
					List<Parameter> parameters = sClient
							.getInputParameters(timeGeoChartOperator);
					logger.debug("Parameters: " + parameters);
					for (Parameter parameter : parameters) {
						logger.debug("Parameter:[Name=" + parameter.getName()
								+ ", Typology=" + parameter.getTypology() + "]");
					}
					
					createRequest(timeGeoChartOperator);
					logger.debug("Start Computation");
					ComputationId computationId = sClient
							.startComputation(timeGeoChartOperator);
					logger.debug("Started ComputationId: " + computationId);
					monitoringComputation(computationId, sClient);
				}

				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success", true);
		}
	}

	private void monitoringComputation(final ComputationId computationId,
			final SClient sClient) {

		DMMonitorListener listener = new DMMonitorListener() {

			@Override
			public void running(double percentage) {
				logger.debug("Operation Running: " + percentage);

			}

			@Override
			public void failed(String message, Exception exception) {
				logger.error("Operation Failed");
				logger.error(message, exception);

			}

			@Override
			public void complete(double percentage) {
				logger.debug("Operation Completed");
				logger.debug("Perc: " + percentage);
				retrieveOutput(computationId, sClient);

			}

			@Override
			public void cancelled() {
				logger.debug("Operation Cancelled");
			}

			@Override
			public void accepted() {
				logger.debug("Operation Accepted");

			}
		};

		DMMonitor dmMonitor = new DMMonitor(computationId, sClient);
		dmMonitor.add(listener);
		dmMonitor.start();

	}

	private void retrieveOutput(ComputationId computationId, SClient sClient) {
		try {
			OutputData output = sClient
					.getOutputDataByComputationId(computationId);
			logger.debug("Output: " + output);
			Resource resource = output.getResource();
			if (resource.isMap()) {
				MapResource mapResource = (MapResource) resource;
				for (String key : mapResource.getMap().keySet()) {
					

					Resource res = mapResource.getMap().get(key);
					switch (res.getResourceType()) {
					case FILE:
						FileResource fileResource = (FileResource) res;
						String fileName=retrieveFileName(fileResource.getUrl());
						logger.debug("Entry: " + key + " = "
								+ mapResource.getMap().get(key)+", FileName="+fileName);
						break;
					case IMAGE:
						ImageResource imageResource = (ImageResource) res;
						String imageName=retrieveFileName(imageResource.getLink());
						logger.debug("Entry: " + key + " = "
								+ mapResource.getMap().get(key)+", ImageName="+imageName);
						break;
					case MAP:
						logger.debug("Entry: " + key + " = "
								+ mapResource.getMap().get(key));
						break;
					case OBJECT:
						logger.debug("Entry: " + key + " = "
								+ mapResource.getMap().get(key));
						break;
					case TABULAR:
						TableResource tableResource = (TableResource) res;
						String tableName=retrieveFileName(tableResource.getResourceId());
						logger.debug("Entry: " + key + " = "
								+ mapResource.getMap().get(key)+", TableName="+tableName);
						break;
					default:
						logger.debug("Entry: " + key + " = "
								+ mapResource.getMap().get(key));
						break;

					}

				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param operator operator
	 */
	private void createRequest(Operator operator) {
		logger.debug("Create Request");

		TabularParameter inputTable = new TabularParameter();
		inputTable.setName("InputTable");
		inputTable
				.setValue("http://data.d4science.org/dCt1K2V4TmZkOUNnbSs4U053dE9SZXNjYnJGTWYyZWlHbWJQNStIS0N6Yz0");
		
		ColumnParameter longitude= new ColumnParameter();
		longitude.setName("Longitude");
		longitude.setValue("decimalLongitude");
		longitude.setReferredTabularParameterName("InputTable");
		
		ColumnParameter latitude= new ColumnParameter();
		latitude.setName("Latitude");
		latitude.setValue("decimalLatitude");
		latitude.setReferredTabularParameterName("InputTable");
		
		ColumnListParameter quantities = new ColumnListParameter();
		quantities.setName("Quantities");
		quantities.setValue("Quantities");
		quantities.setReferredTabularParameterName("InputTable");
		
		ColumnParameter time= new ColumnParameter();
		time.setName("Time");
		time.setValue("eventDate");
		time.setReferredTabularParameterName("InputTable");
		

		List<Parameter> parameters = new ArrayList<>();
		parameters.add(inputTable);
		parameters.add(longitude);
		parameters.add(latitude);
		parameters.add(quantities);
		parameters.add(time);
		
		logger.debug("Parameters set: " + parameters);
		operator.setOperatorParameters(parameters);

	}

	private String retrieveFileName(String url) {
		String fileName = "output";

		try {

			URL urlObj;
			urlObj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) urlObj
					.openConnection();
			connection.setRequestMethod("GET");
			String contentDisposition = connection
					.getHeaderField("Content-Disposition");
			Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
			Matcher regexMatcher = regex.matcher(contentDisposition);
			if (regexMatcher.find()) {
				fileName = regexMatcher.group();
			}

			if (fileName == null || fileName.isEmpty()) {
				fileName = "output";
			}

			return fileName;
		} catch (Throwable e) {
			logger.error(
					"Error retrieving file name: " + e.getLocalizedMessage(), e);
			return fileName;
		}

	}

}
