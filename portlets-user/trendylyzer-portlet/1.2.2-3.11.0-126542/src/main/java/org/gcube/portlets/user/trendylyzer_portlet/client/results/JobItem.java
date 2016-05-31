/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.results;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.Algorithm;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmCategory;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmClassification;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.FileResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ImagesResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.MapResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ObjectResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.TableResource;



import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * @author ceras
 *
 */
public class JobItem extends BaseModel {
	
	private static final long serialVersionUID = 7948340340190844208L;
	
	// force to implement these objects in javascript
	protected TableResource tableRes;
	protected FileResource fileRes;
	protected MapResource mapRes;
	protected ImagesResource imgRes;
	protected ObjectResource objRes;
	protected ComputationStatus compst;

	private Map<String, String> parametersMap;
	
	/**
	 * 
	 */
	public JobItem() {
	}
	
	public JobItem(String id, String name, String description, String categoryId, String operatorId, String infrastructure, Date creationDate, Date endDate, ComputationStatus status, Resource resource) {
		set("id", id);
		set("name", name);
		set("description", description);
		set("categoryId", categoryId);
		set("operatorId", operatorId);
		set("infrastructure", infrastructure);
		set("creationDate", creationDate);
		set("endDate", endDate);
		set("status", status);
		set("resource", resource);
	}

	public AlgorithmCategory getOperatorCategory() {
		AlgorithmClassification classification = TrendyLyzer_portlet.getDefaultAlgorithmClassification();
		if (classification==null)
			return null;
		else
			return classification.getCategoryById((String)get("categoryId"));
	}
	
	public Algorithm getOperator() {
		if (get("operator")==null) {
			AlgorithmClassification classification = TrendyLyzer_portlet.getDefaultAlgorithmClassification();
			if (classification!=null)
				set("operator", classification.getAlgorithmById(getOperatorId()));
		}
		return get("operator");
	}

	/**
	 * @return
	 */
	public String getOperatorId() {
		return get("operatorId");
	}
	
	public ComputationStatus getStatus() {
		return (ComputationStatus)get("status");
	}

	/**
	 * @return
	 */
	public String getName() {
		return get("name");
	}
	
	public String getId() {
		return get("id");
	}
	
	public Date getCreationDate() {
		return (Date)get("creationDate");
	}
	
	public Date getEndDate() {
		return (Date)get("endDate");
	}
	
	public Resource getResource() {
		return get("resource");
	}

	public void setParametersMap(Map<String, String> parametersMap) {
		this.parametersMap = parametersMap;
	}
	
	/**
	 * @return the parametersMap
	 */
	public Map<String, String> getParametersMap() {
		return parametersMap;
	}
	
	public void setEndDate(Date endDate) {
		set("endDate", endDate);
	}
		
}
