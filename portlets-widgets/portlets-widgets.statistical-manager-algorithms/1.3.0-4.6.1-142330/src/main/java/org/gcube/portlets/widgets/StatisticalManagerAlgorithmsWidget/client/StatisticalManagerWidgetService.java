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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("statman")
public interface StatisticalManagerWidgetService extends RemoteService {
	
	// get all operators and categories
	List<OperatorsClassification> getOperatorsClassifications(ArrayList<String> listOfAlg) throws Exception;
	
	// get parameters for a given operator
	List<Parameter> getParameters(Operator operator) throws Exception;
	
	
	// get a status for a computation

	List<TableItemSimple> getTableItems(List<String> templates,
			Collection<TableItemSimple> callerDefinedTables);

	List<TableItemSimple> getFileItems(List<String> templates);


	
	
	Map<String, String> getParametersMapByJobId(String jobId) throws Exception;

	
	
	
	// get a status for an import
	
	void removeComputation(String id) throws Exception;
	
	Map<String, Resource> getMapFromMapResource(MapResource mapResource) throws Exception;
	
	Map<String, String> getImagesInfoFromImagesResource(ImagesResource imgsRes) throws Exception;
	
	String saveImages(String computationId, Map<String, String> mapImages) throws Exception;

	void removeResource(String id) throws Exception;

	void removeImport(String id) throws Exception;



	
}
