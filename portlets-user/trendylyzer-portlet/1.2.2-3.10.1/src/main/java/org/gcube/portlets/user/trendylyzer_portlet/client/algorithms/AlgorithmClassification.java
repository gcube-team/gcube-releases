package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;



public class AlgorithmClassification implements IsSerializable{

	private String name;
	private List<AlgorithmCategory>algorithmCategories = new ArrayList<AlgorithmCategory>();
	private List<Algorithm> algorithms = new ArrayList<Algorithm>();
	
	public AlgorithmClassification() {
		super();
	}
	
	/**
	 * 
	 */
	public AlgorithmClassification(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * @param operatorCategories
	 * @param operators
	 */
	public AlgorithmClassification(String name, List<AlgorithmCategory> operatorCategories,
			List<Algorithm> algorithms) {
		this(name);
		this.algorithmCategories = operatorCategories;
		this.algorithms = algorithms;
	}

	/**
	 * @return the operatorCategories
	 */
	public List<AlgorithmCategory> getOperatorCategories() {
		return algorithmCategories;
	}

	/**
	 * @param operatorCategories the operatorCategories to set
	 */
	public void setAlgorithmCategories(List<AlgorithmCategory> algorithmCategories) {
		this.algorithmCategories = algorithmCategories;
	}

	/**
	 * @return the operators
	 */
	public List<Algorithm> getAlgorithms() {
		return algorithms;
	}

	/**
	 * @param operators the operators to set
	 */
	public void setAlgorithms(List<Algorithm> algorithms) {
		this.algorithms = algorithms;
	}
	
	public Algorithm getAlgorithmById(String id) {
		if (id==null)
			return null;
		Algorithm algorithm = null;
		for (Algorithm alg: algorithms)
			if (alg.getId().contentEquals(id)) {
				algorithm = alg;
				break;
			}
		return algorithm;
	}
	
	public AlgorithmCategory getCategoryById(String id) {
		AlgorithmCategory category = null;
		for (AlgorithmCategory cat: algorithmCategories)
			if (cat.getId().contentEquals(id)) {
				category = cat;
				break;
			}
		return category;
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

}
