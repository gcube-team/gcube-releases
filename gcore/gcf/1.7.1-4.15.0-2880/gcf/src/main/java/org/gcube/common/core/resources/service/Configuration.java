package org.gcube.common.core.resources.service;

import java.util.ArrayList;
import java.util.List;

public class Configuration {	
		
	private StaticConfiguration staticConfig;
	private DynamicConfiguration dynamicConfig;
  

	public StaticConfiguration getStaticConfig() {return staticConfig;}
	public void setStaticConfig(StaticConfiguration staticConfig) {this.staticConfig = staticConfig;}
	public DynamicConfiguration getDynamicConfig() {return dynamicConfig;}
	public void setDynamicConfig(DynamicConfiguration dynamicConfig) {this.dynamicConfig = dynamicConfig;}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final Configuration other = (Configuration) obj;
		
		if (staticConfig == null) {
			if (other.staticConfig != null)
				return false;
		} else if (! staticConfig.equals(other.staticConfig))
			return false;
		
		
		return true;
	}
	
	public static class DynamicConfiguration {
		// TODO
	}
	
	public static class StaticConfiguration {
			  
		List<Config> configurations = new ArrayList<Config>();
		Template template;

		public List<Config> getConfigurations() {return configurations;}
		public void setConfigurations(List<Config> configurations) {this.configurations = configurations;}
		public Template getTemplate() {return template;}
		public void setTemplate(Template template) {this.template = template;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final StaticConfiguration other = (StaticConfiguration) obj;
			
			if (template == null) {
				if (other.template != null)
					return false;
			} else if (! template.equals(other.template))
				return false;
			
			if (configurations == null) {
				if (other.configurations != null)
					return false;
			} else if (! configurations.equals(other.configurations))
				return false;
			
			
			return true;
		}
		
		public static class Config {
		
			private String file;
			private String descr;
			private String label;
			private boolean def;
			
			public String getFile() {return file;}
			public void setFile(String file) {this.file = file;}
			public String getDescription() {return descr;}
			public void setDescription(String descr) {this.descr = descr;}
			public String getLabel() {return label;}
			public void setLabel(String label) {this.label = label;}
			public boolean isDefault() {return this.def;}
			public void setDefault(boolean def) {this.def = def;}
		
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Config other = (Config) obj;
				
				if (def != other.def) return false;
				
				if (file == null) {
					if (other.file != null)
						return false;
				} else if (! file.equals(other.file))
					return false;
				
				if (label == null) {
					if (other.label != null)
						return false;
				} else if (! label.equals(other.label))
					return false;
				
				if (descr == null) {
					if (other.descr != null)
						return false;
				} else if (! descr.equals(other.descr))
					return false;
				
				
				return true;
			}
		}
		
		public static class Template {
			
			List<TemplateParam> parameters = new ArrayList<TemplateParam>();

			public List<TemplateParam> getParameters() {return parameters;}
			public void setParameters(List<TemplateParam> parameters) {this.parameters = parameters;}
			
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Template other = (Template) obj;
				
				if (parameters == null) {
					if (other.parameters != null)
						return false;
				} else if (! parameters.equals(other.parameters))
					return false;
				
				
				return true;
			}
			
			public static class TemplateParam {
				
				private String name;
				private String description;
				private List<TemplateParamValue> values = new ArrayList<TemplateParamValue>();

				public String getName() {return name;}
				public void setName(String name) {this.name = name;}
				public String getDescription() {return description;}
				public void setDescription(String description) {this.description = description;}
				public List<TemplateParamValue> getValues() {return values;}
				public void setValues(List<TemplateParamValue> values) {this.values = values;}
				
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					
					final TemplateParam other = (TemplateParam) obj;
					
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (! name.equals(other.name))
						return false;
					
					if (values == null) {
						if (other.values != null)
							return false;
					} else if (! values.equals(other.values))
						return false;
					
					if (description == null) {
						if (other.description != null)
							return false;
					} else if (! description.equals(other.description))
						return false;
					
					
					return true;
				}
				
				public static class TemplateParamValue {
					private String description;
					private String literal;
					private String label;
					private boolean def;
					
					public String getDescription() {return description;}
					public void setDescription(String description) {this.description = description;}
					public String getLiteral() {return literal;}
					public void setLiteral(String literal) {this.literal = literal;}
					public String getLabel() {return label;}
					public void setLabel(String label) {this.label = label;}
					public boolean isDef() {return def;}
					public void setDef(boolean def) {this.def = def;}
				
					public boolean equals(Object obj) {
						if (this == obj)
							return true;
						if (obj == null)
							return false;
						if (getClass() != obj.getClass())
							return false;
						
						final TemplateParamValue other = (TemplateParamValue) obj;
						
						if (literal == null) {
							if (other.literal != null)
								return false;
						} else if (! literal.equals(other.literal))
							return false;
						
						if (def != other.def) return false;
						
						if (label == null) {
							if (other.label != null)
								return false;
						} else if (! label.equals(other.label))
							return false;
						
						if (description == null) {
							if (other.description != null)
								return false;
						} else if (! description.equals(other.description))
							return false;
						
						
						return true;
					}
				
				}

			}
					
		}

	}
	  
	
}
