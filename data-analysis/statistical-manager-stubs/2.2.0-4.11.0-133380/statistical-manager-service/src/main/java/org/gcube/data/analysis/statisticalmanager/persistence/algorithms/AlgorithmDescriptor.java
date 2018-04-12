package org.gcube.data.analysis.statisticalmanager.persistence.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.statisticalmanager.experimentspace.AlgorithmCategory;
import org.gcube.data.analysis.statisticalmanager.stubs.SMAlgorithm;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;

public class AlgorithmDescriptor{

	private String name;
	private AlgorithmCategory category;
	private Map<String,StatisticalType> parameters;
	private String description;
	private StatisticalType output=null;
	private Boolean dinamycallyLoaded=false;
	private HashSet<String> userPerspectiveCategory=new HashSet<>();
	
	
	public AlgorithmDescriptor(String name, AlgorithmCategory category,
			List<StatisticalType> parameters, String description, StatisticalType output) {
		super();
		this.name = name;
		this.category = category;
		this.parameters = new LinkedHashMap<String, StatisticalType>();
		for(StatisticalType parameter:parameters)
			this.parameters.put(parameter.getName(), parameter);
		this.description = description;
		this.output=output;
	}

	public AlgorithmDescriptor(String name,AlgorithmCategory category){
		super();
		this.name=name;
		this.category=category;
		this.dinamycallyLoaded=true;
	}


	public AlgorithmDescriptor(AlgorithmDescriptor toCopy){
		super();
		this.name=new String(toCopy.getName());
		this.category=toCopy.getCategory();
		this.parameters=new HashMap<>(toCopy.getParameters());
		this.description=new String(toCopy.getDescription());
		this.output=(toCopy.hasDynamicOutput()?null:new StatisticalType(toCopy.getOutput().getName(), toCopy.getOutput().getDescription()));
		this.userPerspectiveCategory=toCopy.getUserPerspectiveCategory();
	}

	public boolean hasDynamicOutput(){
		return output==null ;
	}

	/**
	 * @return the output
	 */
	public StatisticalType getOutput() {
		return output;
	}

	public Boolean isDinamycallyLoaded() {
		return dinamycallyLoaded;
	}

	public void setDinamycallyLoaded(Boolean dinamycallyLoaded) {
		this.dinamycallyLoaded = dinamycallyLoaded;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(StatisticalType output) {
		this.output = output;
	}

public HashSet<String> getUserPerspectiveCategory() {
	return userPerspectiveCategory;
}

public void setUserPerspectiveCategory(HashSet<String> userPerspectiveCategory) {
	this.userPerspectiveCategory = userPerspectiveCategory;
}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the category
	 */
	public AlgorithmCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(AlgorithmCategory category) {
		this.category = category;
	}



	/**
	 * @return the parameters
	 */
	public Map<String, StatisticalType> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, StatisticalType> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AlgorithmDescriptor other = (AlgorithmDescriptor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	

	public SMAlgorithm asSMAlgorithm(){
		return new SMAlgorithm(category.name(), description, name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlgorithmDescriptor [name=");
		builder.append(name);
		builder.append(", category=");
		builder.append(category);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append(", description=");
		builder.append(description);
		builder.append(", output=");
		builder.append(output);
		builder.append(", dinamycallyLoaded=");
		builder.append(dinamycallyLoaded);
		builder.append(", userPerspectiveCategory=");
		builder.append(userPerspectiveCategory);
		builder.append("]");
		return builder.toString();
	}

	public static ArrayList<SMAlgorithm> asList(Collection<AlgorithmDescriptor> toTranslate){
		ArrayList<SMAlgorithm> toReturn=new ArrayList<>();
		for(AlgorithmDescriptor descriptor:toTranslate)
			toReturn.add(descriptor.asSMAlgorithm());
		return toReturn;
	}

	public void update(AlgorithmDescriptor updated){
		this.setDescription(updated.getDescription());
		if(updated.getParameters()!=null)this.setParameters(updated.getParameters());
		if(updated.getOutput()!=null)this.setOutput(updated.getOutput());		
	}

}
