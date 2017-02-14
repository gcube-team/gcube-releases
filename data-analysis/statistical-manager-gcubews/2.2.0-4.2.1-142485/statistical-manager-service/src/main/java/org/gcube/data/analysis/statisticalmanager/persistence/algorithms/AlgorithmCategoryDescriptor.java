package org.gcube.data.analysis.statisticalmanager.persistence.algorithms;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.data.analysis.statisticalmanager.experimentspace.AlgorithmCategory;

public class AlgorithmCategoryDescriptor {

	private AlgorithmCategory category;
	private Map<String,AlgorithmDescriptor> algorithms;
	
	public AlgorithmCategoryDescriptor (AlgorithmCategory category,Collection<AlgorithmDescriptor> algorithms){
		this.category=category;
		this.algorithms=new LinkedHashMap<String, AlgorithmDescriptor>();
		for(AlgorithmDescriptor desc:algorithms)
			this.algorithms.put(desc.getName(), desc);
	}
	
	public Map<String, AlgorithmDescriptor> getAlgorithms() {
		return algorithms;
	}
	
	public AlgorithmCategory getCategory() {
		return category;
	}

	public boolean containsAlgorithm(String algorithmName){
		return algorithms.containsKey(algorithmName);
	}
	
	public AlgorithmDescriptor getAlgorithmDescriptor(String algorithmName){
		return algorithms.get(algorithmName);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgorithmCategoryDescriptor other = (AlgorithmCategoryDescriptor) obj;
		if (category != other.category)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlgorithmCategoryDescriptor [category=");
		builder.append(category);
		builder.append(", algorithms=");
		builder.append(algorithms);
		builder.append("]");
		return builder.toString();
	}

}
