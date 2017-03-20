package gr.uoa.di.madgik.hive.plan;

import gr.uoa.di.madgik.hive.utils.XMLBeautifier;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Represents a data source node of the plan that will be the input to the
 * workflow layer
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class DataSourceNode extends PlanNode {
	/**
	 * the source that will be used as a data source
	 */
	protected String source;

	/**
	 * Default constructor
	 * 
	 * @param source
	 *            of the data source instances that can answer this node of the
	 *            plan
	 * @param functionalArgs
	 *            - all the arguments needed from the corresponding data
	 *            source(for determining the functional behavior) in order to
	 *            execute this node of the plan.
	 */
	public DataSourceNode(String source, HashMap<String, String> functionalArgs) {
		super(functionalArgs);
		this.setInstanceIds(source);
	}

	/**
	 * getter of the source of the data source
	 * 
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * setter for the source of the data source
	 * 
	 * @param source
	 *            the source
	 */
	public void setInstanceIds(String source) {
		this.source = source;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSourceNode other = (DataSourceNode) obj;
		if (!source.equals(other.source))
			return false;
		if (!functionalArgs.equals(other.functionalArgs))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (source == null ? 0 : source.hashCode());
		hash = 31 * hash + (functionalArgs == null ? 0 : functionalArgs.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<DataSource>");
		result.append("<Source>");
		result.append(source);
		result.append("</Source>");
		result.append("<Indications>");
		for (Entry<String, String> entry : functionalArgs.entrySet())
			result.append(entry.getKey() + "-" + (entry.getValue().length() < 256? entry.getValue() : entry.getValue().substring(0, 255) + "...").replace("&", "&amp;") + ", ");
		if (!functionalArgs.isEmpty())
			result.delete(result.length()-2, result.length());
		result.append("</Indications>");
		result.append("</DataSource>");

		return XMLBeautifier.prettyPrintXml(result.toString());
	}
}
