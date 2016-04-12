package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.ArrayList;
import java.util.List;


import com.google.gwt.user.client.rpc.IsSerializable;

public class AlgorithmCategory implements IsSerializable{
	private String id;
	private String name; 
	private String briefDescription;
	private String description;	
	private List<Algorithm> algorithms = new ArrayList<Algorithm>();
	private boolean hasImage = false;
	
	/**
	 * 
	 */
	public AlgorithmCategory() {
		super();
	}
	
	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 */
	public AlgorithmCategory(String id, String briefDescription, String description) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
	}

	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param b
	 */
	public AlgorithmCategory(String id, String briefDescription, String description,	boolean hasImage) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.hasImage = hasImage;
	}


	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param operators
	 */
	public AlgorithmCategory(String id, String briefDescription,
			String description, List<Algorithm> operators) {
		super();
		this.id = id;
		setNameFromId();
		this.briefDescription = briefDescription;
		this.description = description;
		this.algorithms = operators;
	}

	
	/**
	 * @param id
	 * @param briefDescription
	 * @param description
	 * @param operators
	 */
	public AlgorithmCategory(String id, String name, String briefDescription,
			String description, List<Algorithm> operators) {
		super();
		this.id = id;
		this.name = name;
		this.briefDescription = briefDescription;
		this.description = description;
		this.algorithms = operators;
	}

	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the briefDescription
	 */
	public String getBriefDescription() {
		return briefDescription;
	}

	/**
	 * @param briefDescription the briefDescription to set
	 */
	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
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
	
	public void addAlgorithm(Algorithm algorithm) {
		this.algorithms.add(algorithm);
	}
	
	/**
	 * 
	 */
	private void setNameFromId() {
		if (id!=null) {
			String name = "";
			
			boolean precUnderscore = true;
			for (int i=0; i<id.length(); i++) {
				char c = id.charAt(i);
				
				if (c == '_') {
					precUnderscore = true;
					name += " ";
				} else {
					name += (precUnderscore ? Character.toUpperCase(c) : Character.toLowerCase(c));
					if (precUnderscore == true)
						precUnderscore = false;
				}					
			}
			this.name = name;
		}
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public boolean hasImage() {
		return hasImage;
	}
	
	/**
	 * @param hasImage the hasImage to set
	 */
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	
	public AlgorithmCategory clone() {
		AlgorithmCategory op = new AlgorithmCategory(id, name, briefDescription, description, new ArrayList<Algorithm>(algorithms));
		op.setHasImage(hasImage);
		return op;
	}
}
