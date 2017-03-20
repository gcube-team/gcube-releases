package gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a data source node of the plan that will be the input 
 * to the workflow layer
 * 
 * @author vasilis verroios - DI NKUA
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class DataSourceNode extends PlanNode implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * the cql query string that must be answered by the data source
	 */
	private String cqlInput;
	
	/**
	 * identifiers(provided by the registry) of all the possible instances
	 * that can answer this node of the plan.
	 */
	protected Set<String> instanceIds = new HashSet<String>();
	
	/**
	 * Default constructor
	 * @param instanceIds - identifiers(provided by the registry) of all the 
	 * possible data source instances that can answer this node of the plan.
	 * @param functionalArgs - all the arguments needed from the corresponding 
	 * data source(for determining the functional behavior) in order to execute 
	 * this node of the plan.
	 * @param cqlInput - the cql query string that must be answered by the 
	 * data source
	 * @param projections - projections that are provided by this node.
	 */
	public DataSourceNode(Set<String> instanceIds,
			HashMap<String, String> functionalArgs, String cqlInput, Set<String> projections) 
	{
		super(functionalArgs, projections);
		this.setCqlInput(cqlInput);
		this.setInstanceIds(instanceIds);
	}

	/**
	 * setter for the cql query string that must be answered by the data source
	 * @param cqlInput
	 */
	public void setCqlInput(String cqlInput) 
	{
		this.cqlInput = cqlInput;
	}

	/**
	 * getter for the cql query string that must be answered by the data source
	 * @return the cql string
	 */
	public String getCqlInput() 
	{
		return cqlInput;
	}

	/**
	 * getter for the instanceIds field which defines the identifiers
	 * (provided by the registry) of all the possible instances
	 * that can answer this node of the plan.
	 * @return the instanceIds
	 */
	public Set<String> getInstanceIds() 
	{
		return instanceIds;
	}

	/**
	 * setter for the instanceIds field which defines the identifiers
	 * (provided by the registry) of all the possible instances
	 * that can answer this node of the plan.
	 * @param instanceIds 
	 */
	public void setInstanceIds(Set<String> instanceIds) 
	{
		this.instanceIds = instanceIds;
	}
	
	/**
	 * adds an new instanceId in the instanceIds field
	 * @param instanceId 
	 */
	public boolean addInstanceId(String instanceId) 
	{
		return this.instanceIds.add(instanceId);
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(this == obj) return true;
		if (obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		DataSourceNode other = (DataSourceNode)obj;
		if(!cqlInput.equals(other.cqlInput)) return false;
		if(!instanceIds.equals(other.instanceIds)) return false;
		if(!projections.equals(other.projections)) return false;
		if(!functionalArgs.equals(other.functionalArgs)) return false;
		return true;
	}
	
	@Override
	public int hashCode() 
	{
		int hash = 7;
		hash = 31*hash+(cqlInput == null ? 0 : cqlInput.hashCode());
		hash = 31*hash+(instanceIds == null ? 0 : instanceIds.hashCode());
		hash = 31*hash+(functionalArgs == null ? 0 : functionalArgs.hashCode());
		hash = 31*hash+(projections == null ? 0 : projections.hashCode());
		return hash;
	}

	@Override
	public String myToString() {
		return "DataSourceNode [cqlInput=" + cqlInput + ", instanceIds="
				+ instanceIds + ", functionalArgs=" + functionalArgs
				+ ", projections=" + projections + "]";
	}
	
	@Override
	public String toString() 
	{
		StringBuilder result = new StringBuilder();
		result.append("<DataSource>\n");
		result.append("		<Instances>");
		for(String inst : instanceIds)
			result.append(inst + ", ");
		result.append("		</Instances>\n");
		result.append("		<CQL>");
		result.append(cqlInput.replaceAll("\"", "&quot;"));
		result.append("		</CQL>\n");
		result.append("</DataSource>\n");
		
		return result.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DataSourceNode cloned = new DataSourceNode(new HashSet<String>(this.instanceIds), new HashMap<String, String>(this.functionalArgs), this.cqlInput, new HashSet<String>());
		return cloned;
	}
	
	
}
