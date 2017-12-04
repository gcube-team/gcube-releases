package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorsClassification;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.ImagesResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.MapResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.Resource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatisticalManagerWidgetServiceAsync {


	void getFileItems(List<String> templates,
			AsyncCallback<List<TableItemSimple>> callback);

	void getImagesInfoFromImagesResource(ImagesResource imgsRes,
			AsyncCallback<Map<String, String>> callback);


	void getMapFromMapResource(MapResource mapResource,
			AsyncCallback<Map<String, Resource>> callback);

	void getOperatorsClassifications(ArrayList<String> listOfAlg,
			AsyncCallback<List<OperatorsClassification>> callback);

	void getParameters(Operator operator,
			AsyncCallback<List<Parameter>> callback);

	void getParametersMapByJobId(String jobId,
			AsyncCallback<Map<String, String>> callback);


	void getTableItems(List<String> templates,Collection<TableItemSimple> callerDefinedTables,
			AsyncCallback<List<TableItemSimple>> callback);

	void removeComputation(String id, AsyncCallback<Void> callback);

	void removeImport(String id, AsyncCallback<Void> callback);

	void removeResource(String id, AsyncCallback<Void> callback);

//	void resubmit(JobItem jobItem, AsyncCallback<String> callback);

	void saveImages(String computationId, Map<String, String> mapImages,
			AsyncCallback<String> callback);

//	void startComputation(Operator op, String computationTitle,
//			String computationDescription,String targetId, AsyncCallback<String> callback);

}
