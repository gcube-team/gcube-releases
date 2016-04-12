package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;



public class Algorithm implements Serializable{

		
		private String id;
		private String name;
		private String briefDescription;
		private String description;	
		private AlgorithmCategory category;
		private List<Parameter> AlgorithmParameters = new ArrayList<Parameter>();
		private boolean hasImage = false;
		
		/**
		 * 
		 */
		public Algorithm() {
			super();
		}
		
		
		public Algorithm(Algorithm alg) {
			super();
			this.id = alg.id;
			this.name=alg.name;
			for(Parameter p:alg.AlgorithmParameters)
				this.AlgorithmParameters.add(p);
			this.hasImage=alg.hasImage;
			this.briefDescription = alg.briefDescription;
			this.description = alg.description;
			this.category = alg.category;
		}
		
		
		/**
		 * @param id
		 * @param briefDescription
		 * @param description
		 * @param category
		 */
		public Algorithm(String id, String briefDescription, String description, AlgorithmCategory category) {
			super();
			this.id = id;
			setNameFromId();
			this.briefDescription = briefDescription;
			this.description = description;
			this.category = category;
		}
		
		
		/**
		 * @param id
		 * @param briefDescription
		 * @param description
		 * @param category
		 * @param hasImage
		 */
		public Algorithm(String id, String briefDescription, String description,
				AlgorithmCategory category, boolean hasImage) {
			super();
			this.id = id;
			setNameFromId();
			this.briefDescription = briefDescription;
			this.description = description;
			this.category = category;
			this.hasImage = hasImage;
		}

		/**
		 * @param id
		 * @param briefDescription
		 * @param description
		 * @param category
		 */
		public Algorithm(String id, String name, String briefDescription, String description,
				AlgorithmCategory category) {
			super();
			this.id = id;
			this.name = name;
			this.briefDescription = briefDescription;
			this.description = description;
			this.category = category;
		}
		
		/**
		 * @param id
		 * @param briefDescription
		 * @param description
		 * @param category
		 * @param AlgorithmParameters
		 */
		public Algorithm(String id, String briefDescription, String description,
				AlgorithmCategory category,
				List<Parameter> AlgorithmParameters) {
			super();
			this.id = id;
			setNameFromId();
			this.briefDescription = briefDescription;
			this.description = description;
			this.category = category;
			this.AlgorithmParameters = AlgorithmParameters;
		}
		
		/**
		 * @param id
		 * @param briefDescription
		 * @param description
		 * @param category
		 * @param AlgorithmParameters
		 */
		public Algorithm(String id, String name, String briefDescription, String description,
				AlgorithmCategory category,
				List<Parameter> AlgorithmParameters) {
			super();
			this.id = id;
			this.name = name;
			this.briefDescription = briefDescription;
			this.description = description;
			this.category = category;
			this.AlgorithmParameters = AlgorithmParameters;
		}
		
		

		/**
		 * @param id
		 * @param name
		 * @param briefDescription
		 * @param description
		 * @param category
		 * @param AlgorithmParameters
		 * @param hasImage
		 */
		public Algorithm(String id, String name, String briefDescription,
				String description, AlgorithmCategory category,
				List<Parameter> AlgorithmParameters, boolean hasImage) {
			super();
			this.id = id;
			this.name = name;
			this.briefDescription = briefDescription;
			this.description = description;
			this.category = category;
			this.AlgorithmParameters = AlgorithmParameters;
			this.hasImage = hasImage;
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
		 * @return the AlgorithmParameters
		 */
		public List<Parameter> getAlgorithmParameters() {
			return AlgorithmParameters;
		}

		/**
		 * @param AlgorithmParameters the AlgorithmParameters to set
		 */
		public void setAlgorithmParameters(List<Parameter> AlgorithmParameters) {
			this.AlgorithmParameters = AlgorithmParameters;
		}

		public void addAlgorithmParameter(Parameter AlgorithmParameter) {
			this.AlgorithmParameters.add(AlgorithmParameter);
		}
		
		/**
		 * 
		 */
		private void setNameFromId() {
			if (id!=null)
				this.name = getCapitalWords(id);
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
		
		public Algorithm clone() {
			return new Algorithm(id, name, briefDescription, description, category, new ArrayList<Parameter>(AlgorithmParameters), hasImage);
		}
		public static String getCapitalWords(String string) {
			String ris = "";
				
			boolean precUnderscore = true;
			for (int i=0; i<string.length(); i++) {
				char c = string.charAt(i);
				
				if (c == '_') {
					precUnderscore = true;
					ris += " ";
				} else {
					ris += (precUnderscore ? Character.toUpperCase(c) : Character.toLowerCase(c));
					if (precUnderscore == true)
						precUnderscore = false;
				}
			}
			return ris;
		}

}
