package gr.uoa.di.madgik.workflow.adaptor.datatransformation.plan.elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a data source node of the plan that will be the input to the
 * workflow layer
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class DataSourceElement extends PlanElement {

	/**
	 * identifiers(provided by the registry) of all the possible instances that
	 * can answer this node of the plan.
	 */
	protected Set<String> instanceIds = new HashSet<String>();

	/**
	 * Default constructor
	 * 
	 * @param instanceIds
	 *            - identifiers(provided by the registry) of all the possible
	 *            data source instances that can answer this node of the plan.
	 * @param functionalArgs
	 *            - all the arguments needed from the corresponding data
	 *            source(for determining the functional behavior) in order to
	 *            execute this node of the plan.
	 * @param cqlInput
	 *            - the cql query string that must be answered by the data
	 *            source
	 * @param projections
	 *            - projections that are provided by this node.
	 */
	public DataSourceElement(Set<String> instanceIds, HashMap<String, String> functionalArgs, Set<String> projections) {
		super(functionalArgs);
		this.setInstanceIds(instanceIds);
	}

	/**
	 * getter for the instanceIds field which defines the identifiers (provided
	 * by the registry) of all the possible instances that can answer this node
	 * of the plan.
	 * 
	 * @return the instanceIds
	 */
	public Set<String> getInstanceIds() {
		return instanceIds;
	}

	/**
	 * setter for the instanceIds field which defines the identifiers (provided
	 * by the registry) of all the possible instances that can answer this node
	 * of the plan.
	 * 
	 * @param instanceIds
	 */
	public void setInstanceIds(Set<String> instanceIds) {
		this.instanceIds = instanceIds;
	}

	/**
	 * adds an new instanceId in the instanceIds field
	 * 
	 * @param instanceId
	 */
	public boolean addInstanceId(String instanceId) {
		return this.instanceIds.add(instanceId);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<DataSource>\n");
		result.append("		<Instances>");
		for (String inst : instanceIds)
			result.append(inst + ", ");
		result.append("		</Instances>\n");
		result.append("</DataSource>\n");

		return result.toString();
	}
}