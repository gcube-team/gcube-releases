package org.gcube.portlets.user.trendylyzer_portlet.client;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.Algorithm;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmClassification;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.JobItem;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TrendyLyzerPortletServiceAsync {
	/**
	 * @param operator
	 * @return
	 */

	void getAlgorithmsClassifications(
			AsyncCallback<List<AlgorithmClassification>> asyncCallback);

	void getParameters(Algorithm algorithm,
			AsyncCallback<List<Parameter>> asyncCallback);

	void getTableItems(
			List<String> templates,
			AsyncCallback<List<org.gcube.portlets.user.trendylyzer_portlet.client.form.TableItemSimple>> callback);

	void getComputationStatus(String computationId,
			AsyncCallback<ComputationStatus> asyncCallback);

	void getResourceByJobId(String computationId,
			AsyncCallback<Resource> asyncCallback);

	void resubmit(JobItem jobItem, AsyncCallback<String> asyncCallback);

	void startComputation(Algorithm operator, String computationTitle,
			String computationDescription, AsyncCallback<String> asyncCallback);

	void saveImages(String computationId, Map<String, String> mapImages,
			AsyncCallback<String> asyncCallback);

	void removeComputation(String id, AsyncCallback<Void> asyncCallback);

	void getParametersMapByJobId(String id,
			AsyncCallback<Map<String, String>> asyncCallback);

	void getListJobs(AsyncCallback<ListLoadResult<JobItem>> callback);




}
