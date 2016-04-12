package org.gcube.portlets.user.trendylyzer_portlet.client;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.Algorithm;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmClassification;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource;
import org.gcube.portlets.user.trendylyzer_portlet.client.form.TableItemSimple;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.JobItem;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("trendylyzer")
public interface TrendyLyzerPortletService extends RemoteService {



	List<AlgorithmClassification> getAlgorithmsClassifications() throws Exception;

	List<Parameter> getParameters(Algorithm algorithm) throws Exception;

	List<TableItemSimple> getTableItems(List<String> templates);

	ComputationStatus getComputationStatus(String computationId) throws Exception;

	Resource getResourceByJobId(String computationId);

	String resubmit(JobItem jobItem) throws Exception;

	String startComputation(Algorithm operator, String computationTitle,
			String computationDescription) throws Exception;

	String saveImages(String computationId, Map<String, String> mapImages) throws Exception;

	void removeComputation(String id) throws Exception;

	ListLoadResult<JobItem> getListJobs() throws Exception;

	Map<String, String> getParametersMapByJobId(String id);

	


}
