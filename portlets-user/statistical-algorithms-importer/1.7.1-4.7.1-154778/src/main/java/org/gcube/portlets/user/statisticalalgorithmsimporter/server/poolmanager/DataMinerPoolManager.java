package org.gcube.portlets.user.statisticalalgorithmsimporter.server.poolmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.InformationSystemUtils;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.info.InfoData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DataMinerPoolManager {
	private static Logger logger = LoggerFactory.getLogger(DataMinerPoolManager.class);
	private String serverUrl;
	private ServiceCredentials serviceCredentials;

	public DataMinerPoolManager(ServiceCredentials serviceCredentials) throws StatAlgoImporterServiceException {
		this.serviceCredentials = serviceCredentials;
		serverUrl = InformationSystemUtils.retrieveDataMinerPoolManager(serviceCredentials.getScope());
	}

	public String deployAlgorithm(InfoData infoData, ItemDescription codeJarAdminCopy)
			throws StatAlgoImporterServiceException {
		String operationId = sendRequest(infoData, codeJarAdminCopy);
		return operationId;
	}

	public String getDeployOperationLogs(String operationId) throws StatAlgoImporterServiceException {
		String logs = retrieveDeployOperationLogs(operationId);
		return logs;
	}

	public String getDeployOperationLogsLink(String operationId) throws StatAlgoImporterServiceException {
		String logsLink = retrieveDeployOperationLogsLink(operationId);
		return logsLink;
	}

	public String getDeployOperationStatus(String operationId) throws StatAlgoImporterServiceException {
		String deployStatus = retrieveDeployOperationStatus(operationId);
		return deployStatus;
	}

	private String sendRequest(InfoData infoData, ItemDescription codeJarAdminCopy)
			throws StatAlgoImporterServiceException {
		logger.info("Send request to DataMinerPoolManager: " + serverUrl);
		/*
		 * http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-2.0.0-
		 * SNAPSHOT/api/algorithm/stage?
		 * gcube-token=708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548
		 * &algorithmPackageURL=http://data.d4science.org/
		 * dENQTTMxdjNZcGRpK0NHd2pvU0owMFFzN0VWemw3Zy9HbWJQNStIS0N6Yz0
		 * &category=ICHTHYOP_MODEL
		 */

		try {
			String requestUrl = serverUrl + "/algorithm/stage?gcube-token=" + serviceCredentials.getToken()
					+ "&algorithmPackageURL=" + codeJarAdminCopy.getPublicLink() + "&algorithm_type=transducerers"
					+ "&category=" + infoData.getAlgorithmCategory() + "&targetVRE=" + serviceCredentials.getScope();
			logger.debug("DataMinerPoolManager request=" + requestUrl);

			// String authString = user + ":" + password;
			// logger.debug("auth string: " + authString);
			// byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			// String encoded = new String(authEncBytes);
			// logger.debug("Base64 encoded auth string: " + encoded);

			URL urlObj = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");

			// connection.setRequestProperty("Authorization", "Basic " +
			// encoded);
			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer operationId = new StringBuffer();
			logger.info("DataMinerPoolManager response: ");
			while ((line = reader.readLine()) != null) {
				logger.info(line);
				operationId.append(line);
			}

			return operationId.toString();
		} catch (MalformedURLException e) {
			logger.error("DataMinerPoolManager URL seems to be invalid: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager URL seems to be invalid: " + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.error("DataMinerPoolManager error occured in request: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager error occured in request: " + e.getLocalizedMessage(), e);

		} catch (Throwable e) {
			logger.error("DataMinerPoolManager error occured: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException("DataMinerPoolManager error occured: " + e.getLocalizedMessage(),
					e);

		}
	}

	private String retrieveDeployOperationStatus(String operationId) throws StatAlgoImporterServiceException {
		logger.info("Send monitor request to DataMinerPoolManager: " + serverUrl);
		/*
		 *
		 * http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-2.0.0-
		 * SNAPSHOT/api/monitor? gcube-token=.... &logUrl=opId
		 * 
		 */

		try {
			String requestUrl = serverUrl + "/monitor?gcube-token=" + serviceCredentials.getToken() + "&logUrl="
					+ operationId;
			logger.debug("DataMinerPoolManager monitor request=" + requestUrl);

			URL urlObj = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");

			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer operationStatus = new StringBuffer();
			logger.info("DataMinerPoolManager response: ");
			while ((line = reader.readLine()) != null) {
				logger.info(line);
				operationStatus.append(line);
			}

			return operationStatus.toString();
		} catch (MalformedURLException e) {
			logger.error("DataMinerPoolManager monitor URL seems to be invalid: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager monitor URL seems to be invalid: " + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.error("DataMinerPoolManager error occured in monitor request: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager error occured in monitor request: " + e.getLocalizedMessage(), e);

		} catch (Throwable e) {
			logger.error("DataMinerPoolManager monitor error occured: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager monitor error occured: " + e.getLocalizedMessage(), e);

		}

	}

	private String retrieveDeployOperationLogs(String operationId) throws StatAlgoImporterServiceException {
		logger.info("Send logs info request to DataMinerPoolManager: " + serverUrl);
		/*
		 * http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-2.0.0-
		 * SNAPSHOT/api/log?
		 * gcube-token=708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548
		 * &logUrl=426c8e35-a624-4710-b612-c90929c32c27
		 */

		try {
			String requestUrl = serverUrl + "/log?gcube-token=" + serviceCredentials.getToken() + "&logUrl="
					+ operationId;
			logger.debug("DataMinerPoolManager logs request=" + requestUrl);

			URL urlObj = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");

			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer operationStatus = new StringBuffer();
			logger.info("DataMinerPoolManager response: ");
			while ((line = reader.readLine()) != null) {
				logger.info(line);
				operationStatus.append(line);
			}

			return operationStatus.toString();
		} catch (MalformedURLException e) {
			logger.error("DataMinerPoolManager problem retrieving operation logs URL seems to be invalid: "
					+ e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager problem retrieving operation logs URL seems to be invalid: "
							+ e.getLocalizedMessage(),
					e);
		} catch (IOException e) {
			logger.error("DataMinerPoolManager problem retrieving operation logs: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager problem retrieving operation logs: " + e.getLocalizedMessage(), e);

		} catch (Throwable e) {
			logger.error("DataMinerPoolManager error occured retrieving operation logs: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(
					"DataMinerPoolManager error occured retrieving operation logs: " + e.getLocalizedMessage(), e);

		}

	}

	private String retrieveDeployOperationLogsLink(String operationId) throws StatAlgoImporterServiceException {
		logger.info(
				"Retrieve deploy operation logs link for: [operationId=" + operationId + ", server=" + serverUrl + "]");

		String requestUrl = serverUrl + "/log?gcube-token=" + serviceCredentials.getToken() + "&logUrl=" + operationId;
		logger.debug("DataMinerPoolManager monitor request=" + requestUrl);

		return requestUrl;

	}

}
