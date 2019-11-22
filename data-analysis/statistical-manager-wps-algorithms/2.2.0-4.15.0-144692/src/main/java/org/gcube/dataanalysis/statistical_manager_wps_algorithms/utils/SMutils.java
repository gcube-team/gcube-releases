package org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ImageResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ObjectResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.TableResource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.output.OutputManagement;
import org.gcube.dataanalysis.statistical_manager_wps_algorithms.output.StringOutputManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMutils {

	private static final Logger logger = LoggerFactory.getLogger(SMutils.class);
	private static SClient sClient = null;
	private static String token="8be939c2-829c-4b1d-9e1c-1d37b3bfa58c";

	public static SClient getSClient() throws Exception {
		if (sClient == null) {
			DataMinerService dataMinerService = new DataMinerService();
			sClient = dataMinerService.getClient(token);
		}
		return sClient;
	}

	public static List<Parameter> getParameters(String algorithmId)
			throws Exception {
		List<Parameter> parameters = null;
		getSClient();
		Operator operator = sClient.getOperatorById(algorithmId);

		if (operator == null) {
			logger.error("Operator not found");
			throw new Exception("Operator " + algorithmId + " not found.");
		} else {
			logger.debug("Operator Name: " + operator.getName() + " ("
					+ operator.getId() + ")");
			logger.debug("Operator: " + operator);

			parameters = operator.getOperatorParameters();
		}
		return parameters;
	}

	public static ArrayList<String> getAlgorithmsId() throws Exception {
		ArrayList<String> algorithmsId = new ArrayList<>();
		getSClient();
		List<OperatorsClassification> operators = sClient
				.getOperatorsClassifications();
		for (OperatorsClassification oc : operators) {
			for (Operator op : oc.getOperators()) {
				algorithmsId.add(op.getId());
			}

		}
		return algorithmsId;
	}

	public static ArrayList<OutputManagement> getOutputResourceByComputationId(
			ComputationId computationId) throws Exception {
		getSClient();
		OutputData outputData = sClient
				.getOutputDataByComputationId(computationId);

		return getMapFromMapResource(outputData.getResource());

	}

	private static ArrayList<OutputManagement> getMapFromMapResource(
			Resource resource) throws Exception {

		ArrayList<OutputManagement> list = new ArrayList<OutputManagement>();

		switch (resource.getResourceType()) {
		case FILE:
			FileResource fileRes = (FileResource) resource;
			OutputManagement<String> file = new StringOutputManagement();
			file.addInput(fileRes.getResourceId(), fileRes.getUrl());
			list.add(file);
			break;
		case OBJECT:
			ObjectResource objRes = (ObjectResource) resource;
			OutputManagement<String> object = new StringOutputManagement();
			object.addInput(objRes.getResourceId(), objRes.getValue());
			list.add(object);
			break;
		case TABULAR:
			TableResource tableRes = (TableResource) resource;
			OutputManagement<String> tabular = new StringOutputManagement();
			tabular.addInput(tableRes.getResourceId(), tableRes.getResourceId());
			list.add(tabular);
			break;
		case IMAGE:
			ImageResource imageRes = (ImageResource) resource;
			OutputManagement<String> output = new StringOutputManagement();
			output.addInput(imageRes.getResourceId(), imageRes.getLink());
			list.add(output);
			break;
		case MAP:
			MapResource mapResource = (MapResource) resource;
			for (String key : mapResource.getMap().keySet()) {
				Resource res = mapResource.getMap().get(key);
				list.addAll(getMapFromMapResource(res));
			}
			break;
		default:
			break;

		}
		return list;

	}

	public static String getAlgorithmDescription(String algorithmId) throws Exception {
		String description="";
		getSClient();
		Operator operator = sClient.getOperatorById(algorithmId);
		if(operator!=null){
			description=operator.getDescription();
		}
		return description;
	}
	

	public static InputStream getStorageClientInputStream(String url)
			throws Exception {

		// logger.trace("url :" + url);
		URL u = new URL(null, url, new URLStreamHandler() {

			@Override
			protected URLConnection openConnection(URL u) throws IOException {

				return new SMPURLConnection(u);
			}
		});
		return u.openConnection().getInputStream();

		// String [] urlParam=url.split("\\?");

	}

}
