package org.gcube.common.resources.gcore;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.common.resources.gcore.common.AnyMixedWrapper;
import org.gcube.common.resources.gcore.common.AnyWrapper;
import org.gcube.common.resources.gcore.common.BooleanWrapper;
import org.gcube.common.resources.gcore.common.FileList;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;
import org.w3c.dom.Element;

/**
 * Describes software resources, including services, service plugins, and libraries.
 * 
 */
@XmlRootElement(name = "Resource")
public class Software extends Resource {

	private static String CURRENT_VERSION = "1.3.0";
	
	public Software() {
		this.type(Type.SOFTWARE);
		this.version(CURRENT_VERSION);
	}

	@XmlElementRef
	private Profile profile;

	public Profile profile() {
		return profile;
	};

	public Profile newProfile() {
		profile = new Profile();
		return profile;
	}

	@XmlRootElement(name = "Profile")
	@XmlType(propOrder = { "description", "clazz", "name", "version", "configuration", "dependencies", "packages",
			"specificData" })
	public static class Profile {

		@XmlElement(name = "Description")
		private String description;

		@XmlElement(name = "Class")
		private String clazz;

		@XmlElement(name = "Name")
		private String name;

		// we bind it and serialise it, but this is otherwise fixed
		@XmlElement(name = "Version")
		final String version = "1.0.0";

		@XmlElementRef
		private Configuration configuration;

		@XmlElementWrapper(name = "Dependencies")
		@XmlElementRef
		private Set<Dependency> dependencies = new LinkedHashSet<Dependency>();

		@XmlElementWrapper(name = "Packages")
		@XmlElementRefs({ @XmlElementRef(type = ServicePackage.class), @XmlElementRef(type = PluginPackage.class),
				@XmlElementRef(type = GenericPackage.class) })
		private Set<SoftwarePackage<?>> packages = new LinkedHashSet<SoftwarePackage<?>>();

		@XmlElement(name = "SpecificData")
		private SpecificData specificData;

		public String description() {
			return description;
		}

		public Profile description(String description) {
			this.description = description;
			return this;
		}

		public String softwareClass() {
			return clazz;
		}

		public Profile softwareClass(String clazz) {
			this.clazz = clazz;
			return this;
		}

		public String softwareName() {
			return name;
		}

		public Profile softwareName(String name) {
			this.name = name;
			return this;
		}

		public Configuration configuration() {
			return configuration;
		}

		public boolean hasConfiguration() {
			return configuration != null;
		}

		public Configuration newConfiguration() {
			return configuration = new Configuration();
		}

		@SuppressWarnings({"rawtypes","unchecked"})
		public  Group<SoftwarePackage<?>> packages() {
			return new Group<SoftwarePackage<?>>(packages,(Class)SoftwarePackage.class); //wrap if needed
		}

		public Map<String, SoftwarePackage<?>> softwarePackageMap(){
			Map<String, SoftwarePackage<?>> map=new HashMap<String, SoftwarePackage<?>>();
			for (SoftwarePackage<?> sp: packages){
				if(sp.name()!=null)
			       map.put(sp.name(),sp);
			}
			return map;
		}
		
		
		public Element specificData() {
			return specificData==null?null:specificData.root();
		}

		public boolean hasSpecificData() {
			return specificData != null;
		}

		public Element newSpecificData() {
			specificData = new SpecificData();
			return specificData.root();
		}

		public Group<Dependency> dependencies() {
			return new Group<Dependency>(dependencies, Dependency.class);
		}

		@Override
		public String toString() {
			return "Profile [description=" + description + ", clazz=" + clazz + ", name=" + name + ", configuration="
					+ configuration + ", dependency=" + dependencies + ", packages=" + packages + ", specificData="
					+ specificData + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
			result = prime * result + ((dependencies == null) ? 0 : dependencies.hashCode());
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((packages == null) ? 0 : packages.hashCode());
			result = prime * result + ((specificData == null) ? 0 : specificData.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}

		void beforeMarshal(Marshaller marshaller) {
			if (dependencies!=null && dependencies.isEmpty())
				dependencies = null;
			if (packages!=null && packages.isEmpty())
				packages = null;
		}

		// after serialisation, we reinitialise them
		void afterMarshal(Marshaller marshaller) {
			if (dependencies == null)
				dependencies = new LinkedHashSet<Dependency>();
			if (packages == null)
				packages = new LinkedHashSet<SoftwarePackage<?>>();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Profile other = (Profile) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (configuration == null) {
				if (other.configuration != null)
					return false;
			} else if (!configuration.equals(other.configuration))
				return false;
			if (dependencies == null) {
				if (other.dependencies != null)
					return false;
			} else if (!dependencies.equals(other.dependencies))
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (packages == null) {
				if (other.packages != null)
					return false;
			} else if (!packages.equals(other.packages))
				return false;
			if (specificData == null) {
				if (other.specificData != null)
					return false;
			} else if (!specificData.equals(other.specificData))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}

		@XmlRootElement(name = "SpecificData")
		public static class SpecificData extends AnyMixedWrapper {
		}

		@XmlRootElement(name = "Configuration")
		@XmlType(propOrder = { "staticConfigurations" })
		public static class Configuration {

			@XmlElementRef
			private StaticConfigurations staticConfigurations;

			public StaticConfigurations staticConfiguration() {
				return staticConfigurations;
			}

			public boolean hasStaticConfigurations() {
				return staticConfigurations != null;
			}

			public StaticConfigurations newStaticConfiguration() {
				return staticConfigurations = new StaticConfigurations();
			}

			@Override
			public String toString() {
				return " [static=" + staticConfigurations + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((staticConfigurations == null) ? 0 : staticConfigurations.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Configuration other = (Configuration) obj;
				if (staticConfigurations == null) {
					if (other.staticConfigurations != null)
						return false;
				} else if (!staticConfigurations.equals(other.staticConfigurations))
					return false;
				return true;
			}

			@XmlRootElement(name = "Static")
			@XmlType(propOrder = { "configurations", "template" })
			public static class StaticConfigurations {

				@XmlElementWrapper(name = "Configs")
				@XmlElementRef
				private List<StaticConfiguration> configurations = new ArrayList<StaticConfiguration>();

				@XmlElementRef
				private Template template;

				public Group<StaticConfiguration> configurations() {
					return new Group<StaticConfiguration>(configurations, StaticConfiguration.class);
				}

				public Template template() {
					return template;
				}

				public boolean hasTemplate() {
					return template != null;
				}

				public Template newTemplate() {
					template = new Template();
					return template;
				}

				@Override
				public String toString() {
					return "[configurations=" + configurations + ", template=" + template + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((configurations == null) ? 0 : configurations.hashCode());
					result = prime * result + ((template == null) ? 0 : template.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					StaticConfigurations other = (StaticConfigurations) obj;
					if (configurations == null) {
						if (other.configurations != null)
							return false;
					} else if (!configurations.equals(other.configurations))
						return false;
					if (template == null) {
						if (other.template != null)
							return false;
					} else if (!template.equals(other.template))
						return false;
					return true;
				}

				@XmlRootElement(name = "Config")
				@XmlType(propOrder = { "file", "description", "label" })
				public static class StaticConfiguration {

					@XmlElement(name = "File")
					private String file;

					@XmlElement(name = "Description")
					private String description;

					@XmlElement(name = "Label")
					private String label;

					@XmlAttribute(name = "default")
					private boolean default_;

					public boolean isDefault() {
						return default_;
					}

					public StaticConfiguration isDefault(boolean value) {
						this.default_=value;
						return this;
					} 
					
					public String file() {
						return file;
					}

					public StaticConfiguration file(String file) {
						this.file = file;
						return this;
					}

					public String description() {
						return description;
					}

					public StaticConfiguration description(String description) {
						this.description = description;
						return this;
					}

					public String label() {
						return label;
					}

					public StaticConfiguration label(String label) {
						this.label = label;
						return this;
					}

					@Override
					public String toString() {
						return "Configuration [file=" + file + ", description=" + description + ", label=" + label
								+ "]";
					}

					@Override
					public int hashCode() {
						final int prime = 31;
						int result = 1;
						result = prime * result + (default_ ? 1231 : 1237);
						result = prime * result + ((description == null) ? 0 : description.hashCode());
						result = prime * result + ((file == null) ? 0 : file.hashCode());
						result = prime * result + ((label == null) ? 0 : label.hashCode());
						return result;
					}

					@Override
					public boolean equals(Object obj) {
						if (this == obj)
							return true;
						if (obj == null)
							return false;
						if (getClass() != obj.getClass())
							return false;
						StaticConfiguration other = (StaticConfiguration) obj;
						if (default_ != other.default_)
							return false;
						if (description == null) {
							if (other.description != null)
								return false;
						} else if (!description.equals(other.description))
							return false;
						if (file == null) {
							if (other.file != null)
								return false;
						} else if (!file.equals(other.file))
							return false;
						if (label == null) {
							if (other.label != null)
								return false;
						} else if (!label.equals(other.label))
							return false;
						return true;
					}

				}

				@XmlRootElement(name = "Template")
				public static class Template {

					@XmlElementWrapper(name = "Params")
					@XmlElementRef
					private List<ConfigurationParameter> params = new ArrayList<ConfigurationParameter>();

					public Group<ConfigurationParameter> params() {
						return new Group<ConfigurationParameter>(params, ConfigurationParameter.class);
					}
					
					public Map<String, ConfigurationParameter> paramsMap(){
						Map<String, ConfigurationParameter> map=new HashMap<String, ConfigurationParameter>();
						for (ConfigurationParameter cp: params){
							if(cp.name()!=null)
						       map.put(cp.name(),cp);
						}
						return map;
					}

					@Override
					public String toString() {
						return "Template [params=" + params + "]";
					}

					@Override
					public int hashCode() {
						final int prime = 31;
						int result = 1;
						result = prime * result + ((params == null) ? 0 : params.hashCode());
						return result;
					}

					@Override
					public boolean equals(Object obj) {
						if (this == obj)
							return true;
						if (obj == null)
							return false;
						if (getClass() != obj.getClass())
							return false;
						Template other = (Template) obj;
						if (params == null) {
							if (other.params != null)
								return false;
						} else if (!params.equals(other.params))
							return false;
						return true;
					}

					@XmlRootElement(name = "Param")
					public static class ConfigurationParameter {

						@XmlElement(name = "Name")
						private String name;

						@XmlElement(name = "Description")
						private String description;

						@XmlElementWrapper(name = "AllowedValues")
						@XmlElementRef
						private List<AllowedValue> allowedValues = new ArrayList<AllowedValue>();

						public String name() {
							return name;
						}

						public ConfigurationParameter name(String name) {
							this.name = name;
							return this;
						}

						public String description() {
							return description;
						}

						public ConfigurationParameter description(String description) {
							this.description = description;
							return this;
						}

						public Group<AllowedValue> allowedValues() {
							return new Group<AllowedValue>(allowedValues, AllowedValue.class);
						}

						@Override
						public String toString() {
							return "Parameter [name=" + name + ", description=" + description + ", allowedValues="
									+ allowedValues + "]";
						}

						@Override
						public int hashCode() {
							final int prime = 31;
							int result = 1;
							result = prime * result + ((allowedValues == null) ? 0 : allowedValues.hashCode());
							result = prime * result + ((description == null) ? 0 : description.hashCode());
							result = prime * result + ((name == null) ? 0 : name.hashCode());
							return result;
						}

						@Override
						public boolean equals(Object obj) {
							if (this == obj)
								return true;
							if (obj == null)
								return false;
							if (getClass() != obj.getClass())
								return false;
							ConfigurationParameter other = (ConfigurationParameter) obj;
							if (allowedValues == null) {
								if (other.allowedValues != null)
									return false;
							} else if (!allowedValues.equals(other.allowedValues))
								return false;
							if (description == null) {
								if (other.description != null)
									return false;
							} else if (!description.equals(other.description))
								return false;
							if (name == null) {
								if (other.name != null)
									return false;
							} else if (!name.equals(other.name))
								return false;
							return true;
						}

						@XmlRootElement(name = "Value")
						public static class AllowedValue {

							@XmlElement(name = "Description")
							private String description;

							@XmlElement(name = "Literal")
							private String literal;

							@XmlElement(name = "Label")
							private String label;

							@XmlAttribute(name = "default")
							private boolean isDefault;

							public String description() {
								return description;
							}

							public AllowedValue description(String desc) {
								this.description = desc;
								return this;
							}

							public String literal() {
								return literal;
							}

							public AllowedValue literal(String lit) {
								this.literal = lit;
								return this;
							}

							public String label() {
								return label;
							}

							public AllowedValue label(String label) {
								this.label = label;
								return this;
							}

							public boolean isDefault() {
								return isDefault;
							}

							public AllowedValue isDefault(boolean def) {
								this.isDefault = def;
								return this;
							}

							@Override
							public String toString() {
								return " [description=" + description + ", literal=" + literal + ", label=" + label
										+ ", isDefault=" + isDefault + "]";
							}

							@Override
							public int hashCode() {
								final int prime = 31;
								int result = 1;
								result = prime * result + ((description == null) ? 0 : description.hashCode());
								result = prime * result + (isDefault ? 1231 : 1237);
								result = prime * result + ((label == null) ? 0 : label.hashCode());
								result = prime * result + ((literal == null) ? 0 : literal.hashCode());
								return result;
							}

							@Override
							public boolean equals(Object obj) {
								if (this == obj)
									return true;
								if (obj == null)
									return false;
								if (getClass() != obj.getClass())
									return false;
								AllowedValue other = (AllowedValue) obj;
								if (description == null) {
									if (other.description != null)
										return false;
								} else if (!description.equals(other.description))
									return false;
								if (isDefault != other.isDefault)
									return false;
								if (label == null) {
									if (other.label != null)
										return false;
								} else if (!label.equals(other.label))
									return false;
								if (literal == null) {
									if (other.literal != null)
										return false;
								} else if (!literal.equals(other.literal))
									return false;
								return true;
							}

						}

					}
				}

			}

		}

		@XmlRootElement(name = "Dependency")
		@XmlType(propOrder = { "clazz", "name", "version" })
		public static class Dependency {

			@XmlElement(name = "Class")
			private String clazz;

			@XmlElement(name = "Name")
			private String name;

			@XmlElement(name = "Version")
			private String version;

			public String serviceClass() {
				return clazz;
			}
			
			public Dependency serviceClass(String serviceClass) {
				this.clazz=serviceClass;
				return this;
			}

			public String serviceName() {
				return name;
			}

			public Dependency serviceName(String name) {
				this.name = name;
				return this;
			}

			public String version() {
				return version;
			}

			public Dependency version(String version) {
				this.version = version;
				return this;
			}

			@Override
			public String toString() {
				return "[clazz=" + clazz + ", name=" + name + ", version=" + version + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				result = prime * result + ((version == null) ? 0 : version.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Dependency other = (Dependency) obj;
				if (clazz == null) {
					if (other.clazz != null)
						return false;
				} else if (!clazz.equals(other.clazz))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (version == null) {
					if (other.version != null)
						return false;
				} else if (!version.equals(other.version))
					return false;
				return true;
			}

		}

		@XmlRootElement(name = "Software")
		@XmlType(propOrder = { "type", "entryPoints", "files", "uri" })
		public static class GenericPackage extends SoftwarePackage<GenericPackage> {

			@XmlElement(name = "EntryPoint")
			private Set<String> entryPoints = new LinkedHashSet<String>();

			@XmlElement(name = "URI")
			private URI uri;

			@XmlElementWrapper(name = "Files")
			@XmlElement(name = "File")
			private Set<String> files = new LinkedHashSet<String>();


			@SuppressWarnings("unused")
			private void beforeMarshal(Marshaller marshaller) {
				super.beforeMarshal(marshaller);
				if (files.isEmpty())
					files = null;
			}

			// after serialisation, we reinitialise them
			@SuppressWarnings("unused")
			private void afterMarshal(Marshaller marshaller) {
				super.afterMarshal(marshaller);
				if (files == null)
					files = new LinkedHashSet<String>();
			}
			
			@XmlElement(name = "Type")
			private Type type;

			@Override
			protected GenericPackage _this() {
				return this;
			}
			
			public Collection<String> entryPoints() {
				return new Group<String>(entryPoints,String.class);
			}

			public Collection<String> files() {
				return new Group<String>(files,String.class);
			}

			public Type type() {
				return type;
			}

			public GenericPackage type(Type type) {
				this.type = type;
				return this;
			}

			public URI uri() {
				return uri;
			}

			public GenericPackage uri(URI uri) {
				this.uri = uri;
				return this;
			}

			@Override
			public String toString() {
				return "Software [entryPoint=" + entryPoints + ", uri=" + uri + ", files=" + files + ", type=" + type
						+ "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = super.hashCode();
				result = prime * result + ((entryPoints == null) ? 0 : entryPoints.hashCode());
				result = prime * result + ((files == null) ? 0 : files.hashCode());
				result = prime * result + ((type == null) ? 0 : type.hashCode());
				result = prime * result + ((uri == null) ? 0 : uri.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!super.equals(obj))
					return false;
				if (getClass() != obj.getClass())
					return false;
				GenericPackage other = (GenericPackage) obj;
				if (entryPoints == null) {
					if (other.entryPoints != null)
						return false;
				} else if (!entryPoints.equals(other.entryPoints))
					return false;
				if (files == null) {
					if (other.files != null)
						return false;
				} else if (!files.equals(other.files))
					return false;
				if (type != other.type)
					return false;
				if (uri == null) {
					if (other.uri != null)
						return false;
				} else if (!uri.equals(other.uri))
					return false;
				return true;
			}

			@XmlType(name = "Type")
			@XmlEnum
			public enum Type {

				library, application, plugin, webapplication;

				public String value() {
					return name();
				}

				public static Type fromValue(String v) {
					return valueOf(v);
				}

			}

		}

		@XmlRootElement(name = "Main")
		@XmlType(propOrder = { "archive", "functions", "portTypes" })
		public static class ServicePackage extends SoftwarePackage<ServicePackage> {

			@XmlElement(name="GARArchive")
			private String archive;

			@XmlElementWrapper(name = "ServiceEquivalenceFunctions")
			@XmlElementRef
			private Set<Function> functions = new LinkedHashSet<Function>();

			@XmlElementRef
			private Set<PortType> portTypes = new LinkedHashSet<PortType>();

			protected ServicePackage _this() {
				return this;
			};

			@SuppressWarnings("unused")
			private void beforeMarshal(Marshaller marshaller) {
				super.beforeMarshal(marshaller);
				if (functions.isEmpty())
					functions=null;
			}

			// after serialisation, we reinitialise them
			@SuppressWarnings("unused")
			private void afterMarshal(Marshaller marshaller) {
				super.afterMarshal(marshaller);
				if (functions == null)
					functions = new LinkedHashSet<Function>();
			}
			
			public String archive() {
				return archive;
			}

			public ServicePackage archive(String archive) {
				this.archive = archive;
				return this;
			}

			public Group<Function> functions() {
				return new Group<Function>(functions, Function.class);
			}

			public Group<PortType> portTypes() {
				return new Group<PortType>(portTypes, PortType.class);
			}

			public Map<String, PortType> portTypeMap(){
				Map<String, PortType> map=new HashMap<String, PortType>();
				for (PortType p: portTypes){
					if(p.name()!=null)
				       map.put(p.name(),p);
				}
				return map;
			}
			
			
			@Override
			public String toString() {
				return "Main [garArchive=" + archive + ", function=" + functions + ", portTypes=" + portTypes + "]";
			}

			@XmlRootElement(name = "PortType")
			public static class PortType {

				@XmlElement(name = "Name")
				private String name;

				@XmlElementRef
				private Security security;

				@XmlElementRef
				private Wsdl wsdl;

				public String name() {
					return name;
				}

				public PortType name(String name) {
					this.name = name;
					return this;
				}

				public Security security() {
					return security;
				}

				public Security newSecurity() {
					security = new Security();
					return security;
				}

				public Element wsdl() {
					return wsdl==null?null:wsdl.root();
				}

				public Element newWsdl() {
					return (this.wsdl=new Wsdl()).root();
				}
				
				

				@Override
				public String toString() {
					return " [name=" + name + ", security=" + security + ", wsdl=" + wsdl + "]";
				}

				@XmlRootElement(name = "Security")
				public static class Security {

					@XmlElement(name = "Descriptor")
					private SecurityDescriptor descriptor;

					@XmlElementWrapper(name = "Operations")
					@XmlElement(name = "Operation")
					private List<Operation> operations;

					public Element descriptor() {
						return descriptor==null?null:descriptor.root();
					}

					public Element newDescriptor() {
						return (this.descriptor = new SecurityDescriptor()).root();
					}

					public Group<Operation> operations() {
						return new Group<Operation>(operations, Operation.class);
					}
					
					public Map<String, Operation> portTypeMap(){
						Map<String, Operation> map=new HashMap<String, Operation>();
						for (Operation o: operations){
							if(o.name()!=null)
						       map.put(o.name(),o);
						}
						return map;
					}

					@XmlRootElement(name = "Descriptor")
					public static class SecurityDescriptor extends AnyWrapper{}
					
					@XmlRootElement(name = "Operation")
					public static class Operation {

						@XmlElement(name = "id")
						private String id;

						@XmlAttribute(name = "name")
						private String name;

						@XmlElement(name = "description")
						private String description;

						@XmlElementRef
						private Roles roles;

						public String id() {
							return id;
						}

						public Operation id(String id) {
							this.id = id;
							return this;
						}

						public String name() {
							return name;
						}

						public Operation name(String name) {
							this.name = name;
							return this;
						}

						public String description() {
							return description;
						}

						public Operation description(String desc) {
							this.description = desc;
							return this;
						}

						public Roles roles() {
							return roles;
						}

						public Roles newRoles() {
							roles = new Roles();
							return roles;
						}

						@Override
						public String toString() {
							return " [id=" + id + ", name=" + name + ", description=" + description + ", roles="
									+ roles + "]";
						}

						@XmlRootElement(name = "Roles")
						public static class Roles {

							@XmlElement(name = "Role")
							private List<Role> roles = new ArrayList<Role>();

							public Group<Role> role() {
								return new Group<Role>(roles, Role.class);
							}

							@Override
							public String toString() {
								return " [role=" + roles + "]";
							}

							@XmlRootElement(name = "Role")
							public static class Role {

								@XmlAttribute(name = "value")
								private String value;

								public String value() {
									return value;
								}

								public Role value(String value) {
									this.value = value;
									return this;
								}

								@Override
								public String toString() {
									return " [value=" + value + "]";
								}

							}

						}
					}

				}

			}

			@XmlRootElement(name = "Function")
			@XmlType(propOrder = { "name", "formalParameters", "body" })
			public static class Function {

				@XmlElement(name = "Name")
				private String name;

				@XmlElementWrapper(name = "FormalParameters")
				@XmlElement(name = "Name")
				private List<String> formalParameters = new ArrayList<String>();

				@XmlElementRef
				private Body body;

				public String name() {
					return name;
				}

				public Function name(String name) {
					this.name = name;
					return this;
				}

				public List<String> formalParameters() {
					return formalParameters;
				}

				public Group<String> functions() {
					return new Group<String>(formalParameters, String.class);
				}

				public Element body() {
					return body==null?null:body.root();
				}
				
				public String bodyAsString() {
					return body==null?null:body.asString();
				}

				public Element newBody() {
					return (body = new Body()).root();
				}
				
				
				public Function newBody(String text) {
					body = new Body();
					body.setString(text);
					return this;
				}

				@Override
				public String toString() {
					return " [name=" + name + ", formalParametersName=" + formalParameters + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((body == null) ? 0 : body.hashCode());
					result = prime * result + ((formalParameters == null) ? 0 : formalParameters.hashCode());
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					Function other = (Function) obj;
					if (body == null) {
						if (other.body != null)
							return false;
					} else if (!body.equals(other.body))
						return false;
					if (formalParameters == null) {
						if (other.formalParameters != null)
							return false;
					} else if (!formalParameters.equals(other.formalParameters))
						return false;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					return true;
				}

				@XmlRootElement(name="Body")
				public static class Body extends AnyMixedWrapper{}
			}

		}

		@XmlRootElement(name = "WSDL")
		public static class Wsdl extends AnyWrapper {}
		
		@XmlRootElement(name = "Plugin")
		@XmlType(propOrder = { "target","entryPoint","files"})
		public static class PluginPackage extends SoftwarePackage<PluginPackage> {

			@XmlElementRef
			private TargetService target;

			
			@XmlElementWrapper(name = "Files")
			@XmlElement(name = "File")
			private Set<String> files = new LinkedHashSet<String>();
			
			@XmlElement(name = "EntryPoint")
			private String entryPoint;
			
			@Override
			protected PluginPackage _this() {
				return this;
			}

			@SuppressWarnings("unused")
			private void beforeMarshal(Marshaller marshaller) {
				super.beforeMarshal(marshaller);
				if (files.isEmpty())
					files = null;
			}

			// after serialisation, we reinitialise them
			@SuppressWarnings("unused")
			private void afterMarshal(Marshaller marshaller) {
				super.afterMarshal(marshaller);
				if (files == null)
					files = new LinkedHashSet<String>();
			}

			
			public TargetService targetService() {
				return target;
			}

			public TargetService newTargetService() {
				target = new TargetService();
				return target;
			}

			public String entryPoint() {
				return entryPoint;
			}
			
			public PluginPackage entryPoint(String entry) {
				this.entryPoint=entry;
				return this;
			}
			
			public Collection<String> files() {
				return new Group<String>(files,String.class);
			}
			
			@Override
			public String toString() {
				return "Plugin [service=" + target + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = super.hashCode();
				result = prime * result + ((target == null) ? 0 : target.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!super.equals(obj))
					return false;
				if (getClass() != obj.getClass())
					return false;
				PluginPackage other = (PluginPackage) obj;
				if (target == null) {
					if (other.target != null)
						return false;
				} else if (!target.equals(other.target))
					return false;
				return true;
			}

			@XmlRootElement(name = "TargetService")
			public static class TargetService {

				@XmlElementRef
				private Service service;

				@XmlElement(name = "Package")
				private String package_;

				@XmlElement(name = "Version")
				private String version;

				public Service service() {
					return service;
				}

				public Service newService() {
					service = new Service();
					return service;
				}

				public String servicePackage() {
					return package_;
				}

				public TargetService servicePackage(String pack) {
					this.package_ = pack;
					return this;
				}

				public String version() {
					return version;
				}

				public TargetService version(String version) {
					this.version = version;
					return this;
				}

				@Override
				public String toString() {
					return " [service=" + service + ", package_=" + package_ + ", version=" + version + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((package_ == null) ? 0 : package_.hashCode());
					result = prime * result + ((service == null) ? 0 : service.hashCode());
					result = prime * result + ((version == null) ? 0 : version.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					TargetService other = (TargetService) obj;
					if (package_ == null) {
						if (other.package_ != null)
							return false;
					} else if (!package_.equals(other.package_))
						return false;
					if (service == null) {
						if (other.service != null)
							return false;
					} else if (!service.equals(other.service))
						return false;
					if (version == null) {
						if (other.version != null)
							return false;
					} else if (!version.equals(other.version))
						return false;
					return true;
				}

				@XmlRootElement(name = "Service")
				public static class Service {

					@XmlElement(name = "Class")
					private String clazz;

					@XmlElement(name = "Name")
					private String name;

					@XmlElement(name = "Version")
					private String version;

					public String serviceClass() {
						return clazz;
					}

					public Service serviceClass(String clazz) {
						this.clazz = clazz;
						return this;
					}

					public String serviceName() {
						return name;
					}

					public Service serviceName(String name) {
						this.name = name;
						return this;
					}

					public String version() {
						return version;
					}

					public Service version(String version) {
						this.version = version;
						return this;
					}

					@Override
					public String toString() {
						return " [class=" + clazz + ", name=" + name + ", version=" + version + "]";
					}

					@Override
					public int hashCode() {
						final int prime = 31;
						int result = 1;
						result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
						result = prime * result + ((name == null) ? 0 : name.hashCode());
						result = prime * result + ((version == null) ? 0 : version.hashCode());
						return result;
					}

					@Override
					public boolean equals(Object obj) {
						if (this == obj)
							return true;
						if (obj == null)
							return false;
						if (getClass() != obj.getClass())
							return false;
						Service other = (Service) obj;
						if (clazz == null) {
							if (other.clazz != null)
								return false;
						} else if (!clazz.equals(other.clazz))
							return false;
						if (name == null) {
							if (other.name != null)
								return false;
						} else if (!name.equals(other.name))
							return false;
						if (version == null) {
							if (other.version != null)
								return false;
						} else if (!version.equals(other.version))
							return false;
						return true;
					}

				}

			}

		}

		@XmlType(propOrder = { "description", "name", "version", "coordinates", "targetPlatform", "multiversion",
				"mandatory", "shareable", "ghnRequirements", "installScripts", "uninstallScripts", "rebootScripts" , "dependencies", "specificData" })
		public abstract static class SoftwarePackage<T extends SoftwarePackage<T>> {

			@XmlElement(name = "Description")
			private String description;

			@XmlElement(name = "Name")
			private String name;

			@XmlElement(name = "Version")
			private String version;

			@XmlElementRef
			private MavenCoordinates coordinates;

			@XmlElementRef
			private TargetPlatform targetPlatform;

			@XmlElement(name = "MultiVersion")
			private BooleanWrapper multiversion = new BooleanWrapper(true);

			@XmlElement(name = "Mandatory")
			private LevelWrapper mandatory;

			@XmlElement(name = "Shareable")
			private LevelWrapper shareable;

			@XmlElementWrapper(name = "GHNRequirements")
			@XmlElementRef
			private Set<Requirement> ghnRequirements = new LinkedHashSet<Requirement>();

			@XmlElementRef
			private InstallScripts installScripts = new InstallScripts();

			@XmlElementRef
			private UninstallScripts uninstallScripts = new UninstallScripts();

			@XmlElementRef
			private RebootScripts rebootScripts = new RebootScripts();
		
			@XmlElementWrapper(name = "Dependencies")
			@XmlElementRef
			private Set<PackageDependency> dependencies = new LinkedHashSet<PackageDependency>();

			@XmlElement(name = "SpecificData")
			private SpecificData specificData;

			
			protected abstract T _this();
			
			@SuppressWarnings("all")
			private void beforeMarshal(Marshaller marshaller) {
				if (ghnRequirements.isEmpty())
					ghnRequirements=null;
				if (installScripts.files.isEmpty())
					installScripts=null;
				if (uninstallScripts.files.isEmpty())
					uninstallScripts=null;
				if (rebootScripts.files.isEmpty())
					rebootScripts=null;
				if (dependencies.isEmpty())
					dependencies = null;
			}

			// after serialisation, we reinitialise them
			@SuppressWarnings("all")
			private void afterMarshal(Marshaller marshaller) {
				if (ghnRequirements == null)
					ghnRequirements = new LinkedHashSet<Requirement>();
				if (installScripts == null)
					installScripts = new InstallScripts();
				if (uninstallScripts == null)
					uninstallScripts = new UninstallScripts();
				if (rebootScripts == null)
					rebootScripts = new RebootScripts();
				if (dependencies == null)
					dependencies = new LinkedHashSet<PackageDependency>();
			}

			
			public String description() {
				return description;
			}

			public T description(String description) {
				this.description = description;
				return _this();
			}

			public String name() {
				return name;
			}

			public T name(String name) {
				this.name = name;
				return _this();
			}

			public String version() {
				return version;
			}

			public T version(String version) {
				this.version = version;
				return _this();
			}

			public MavenCoordinates coordinates() {
				return coordinates;
			}

			public boolean hasCoordinates() {
				return coordinates != null;
			}

			public MavenCoordinates newCoordinates() {
				coordinates = new MavenCoordinates();
				return coordinates;
			}

			public TargetPlatform targetPlatform() {
				return targetPlatform;
			}

			public boolean hasTargetPlatform() {
				return targetPlatform != null;
			}

			public TargetPlatform newTargetPlatform() {
				targetPlatform = new TargetPlatform();
				return targetPlatform;
			}

			public boolean isMultiVersion() {
				return multiversion.value;
			}
			
			public T multiVersion(boolean value) {
				multiversion.value=value;
				return _this();
			}

			public Level mandatory() {
				return mandatory.level;
			}

			public LevelWrapper newMandatory() {
				mandatory = new LevelWrapper(Level.NONE);
				return mandatory;
			}

			public Level shareable() {
				return shareable.level;
			}

			public boolean hasShareable() {
				return shareable != null;
			}

			public LevelWrapper newShareable() {
				shareable = new LevelWrapper(Level.VO);
				return shareable;
			}

			public Group<Requirement> ghnRequirements() {
				return new Group<Requirement>(ghnRequirements, Requirement.class);
			}

			
			public Map<String, Requirement> ghnRequirementsMap(){
				Map<String, Requirement> map=new HashMap<String, Requirement>();
				for (Requirement r: ghnRequirements){
					if(r.key()!=null)
				       map.put(r.key(),r);
				}
				return map;
			}
			
			

			public Collection<String> installScripts() {
				return new Group<String>(installScripts.files,String.class);
			}

			public Collection<String> uninstallScripts() {
				return new Group<String>(uninstallScripts.files,String.class);
			}

			public Collection<String> rebootScripts() {
				return new Group<String>(rebootScripts.files,String.class);
			}

			public boolean hasRebootScripts() {
				return rebootScripts != null;
			}

			@XmlRootElement(name = "InstallScripts")
			public static class InstallScripts extends FileList {
			}

			@XmlRootElement(name = "UninstallScripts")
			public static class UninstallScripts extends FileList {
			}

			@XmlRootElement(name = "RebootScripts")
			public static class RebootScripts extends FileList {
			}

			public Group<PackageDependency> dependencies() {
				return new Group<PackageDependency>(dependencies, PackageDependency.class);
			}
			
			public Element specificData() {
				return specificData==null?null:specificData.root();
			}
			
			public String specificDataAsString() {
				return specificData==null?null:specificData.asString();
			}

			public boolean hasSpecificData() {
				return specificData != null;
			}

			public Element newSpecificData() {
				specificData = new SpecificData();
				return specificData.root();
			}
			
			public T newSpecificData(String text) {
				specificData = new SpecificData();
				specificData.setString(text);
				return _this();
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result	+ ((coordinates == null) ? 0 : coordinates.hashCode());
				result = prime * result	+ ((dependencies == null) ? 0 : dependencies.hashCode());
				result = prime * result + ((description == null) ? 0 : description.hashCode());
				result = prime * result	+ ((ghnRequirements == null) ? 0 : ghnRequirements.hashCode());
				result = prime * result	+ ((installScripts == null) ? 0 : installScripts.hashCode());
				result = prime * result	+ ((mandatory == null) ? 0 : mandatory.hashCode());
				result = prime * result	+ ((multiversion == null) ? 0 : multiversion.hashCode());
				result = prime * result	+ ((name == null) ? 0 : name.hashCode());
				result = prime * result	+ ((rebootScripts == null) ? 0 : rebootScripts.hashCode());
				result = prime * result	+ ((shareable == null) ? 0 : shareable.hashCode());
				result = prime * result	+ ((specificData == null) ? 0 : specificData.hashCode());
				result = prime * result	+ ((targetPlatform == null) ? 0 : targetPlatform.hashCode());
				result = prime * result	+ ((uninstallScripts == null) ? 0 : uninstallScripts.hashCode());
				result = prime * result	+ ((version == null) ? 0 : version.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				SoftwarePackage<?> other = (SoftwarePackage<?>) obj;
				if (coordinates == null) {
					if (other.coordinates != null)
						return false;
				} else if (!coordinates.equals(other.coordinates))
					return false;
				if (dependencies == null) {
					if (other.dependencies != null)
						return false;
				} else if (!dependencies.equals(other.dependencies))
					return false;
				if (description == null) {
					if (other.description != null)
						return false;
				} else if (!description.equals(other.description))
					return false;
				if (ghnRequirements == null) {
					if (other.ghnRequirements != null)
						return false;
				} else if (!ghnRequirements.equals(other.ghnRequirements))
					return false;
				if (installScripts == null) {
					if (other.installScripts != null)
						return false;
				} else if (!installScripts.equals(other.installScripts))
					return false;
				if (mandatory == null) {
					if (other.mandatory != null)
						return false;
				} else if (!mandatory.equals(other.mandatory))
					return false;
				if (multiversion == null) {
					if (other.multiversion != null)
						return false;
				} else if (!multiversion.equals(other.multiversion))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (rebootScripts == null) {
					if (other.rebootScripts != null)
						return false;
				} else if (!rebootScripts.equals(other.rebootScripts))
					return false;
				if (shareable == null) {
					if (other.shareable != null)
						return false;
				} else if (!shareable.equals(other.shareable))
					return false;
				if (specificData == null) {
					if (other.specificData != null)
						return false;
				} else if (!specificData.equals(other.specificData))
					return false;
				if (targetPlatform == null) {
					if (other.targetPlatform != null)
						return false;
				} else if (!targetPlatform.equals(other.targetPlatform))
					return false;
				if (uninstallScripts == null) {
					if (other.uninstallScripts != null)
						return false;
				} else if (!uninstallScripts.equals(other.uninstallScripts))
					return false;
				if (version == null) {
					if (other.version != null)
						return false;
				} else if (!version.equals(other.version))
					return false;
				return true;
			}

			@Override
			public String toString() {
				return "[description=" + description
						+ ", name=" + name + ", version=" + version
						+ ", coordinates=" + coordinates + ", targetPlatform="
						+ targetPlatform + ", multiversion=" + multiversion
						+ ", mandatory=" + mandatory + ", shareable="
						+ shareable + ", ghnRequirements=" + ghnRequirements
						+ ", installScripts=" + installScripts
						+ ", uninstallScripts=" + uninstallScripts
						+ ", rebootScripts=" + rebootScripts 
						+ ", dependencies=" + dependencies + "]" + ", specificData="
						+ specificData + "]";
			}

			@XmlRootElement(name = "MavenCoordinates")
			@XmlType(propOrder = { "groupId", "artifactId", "version", "classifier" })
			public static class MavenCoordinates {

				@XmlElement(name = "groupId")
				private String groupId;

				@XmlElement(name = "artifactId")
				private String artifactId;

				@XmlElement(name = "version")
				private String version;

				@XmlElement(name = "classifier")
				private String classifier;

				public String groupId() {
					return groupId;
				}

				public MavenCoordinates groupId(String gid) {
					this.groupId = gid;
					return this;
				}

				public String artifactId() {
					return artifactId;
				}

				public MavenCoordinates artifactId(String aId) {
					this.artifactId = aId;
					return this;
				}

				public String version() {
					return version;
				}

				public MavenCoordinates version(String version) {
					this.version = version;
					return this;
				}

				public String classifier() {
					return classifier;
				}

				public MavenCoordinates classifier(String classifier) {
					this.classifier = classifier;
					return this;
				}

				@Override
				public String toString() {
					return " [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
							+ ", classifier=" + classifier + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
					result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
					result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
					result = prime * result + ((version == null) ? 0 : version.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					MavenCoordinates other = (MavenCoordinates) obj;
					if (artifactId == null) {
						if (other.artifactId != null)
							return false;
					} else if (!artifactId.equals(other.artifactId))
						return false;
					if (classifier == null) {
						if (other.classifier != null)
							return false;
					} else if (!classifier.equals(other.classifier))
						return false;
					if (groupId == null) {
						if (other.groupId != null)
							return false;
					} else if (!groupId.equals(other.groupId))
						return false;
					if (version == null) {
						if (other.version != null)
							return false;
					} else if (!version.equals(other.version))
						return false;
					return true;
				}

			}

			
			@XmlRootElement(name = "Dependency")
			@XmlType(propOrder = { "service", "package_", "version", "scope", "optional" })
			public static class PackageDependency {

				@XmlElement(name = "Package")
				private String package_;

				@XmlElement(name = "Version")
				private String version;
				
				@XmlElementRef
				private DependencyService service;
				
				@XmlElement(name = "Scope")
				private LevelWrapper scope;

				@XmlElement(name = "Optional")
				private boolean optional;

				public boolean isOptional() {
					return optional;
				}

				public String dependencyPackage() {
					return package_;
				}
				
				public PackageDependency dependencyPackage(String dependencyPackage) {
					this.package_=dependencyPackage;
					return this;
				}

				public String version() {
					return version;
				}

				public PackageDependency version(String version) {
					this.version = version;
					return this;
				}
				
				public DependencyService service(){
					return service;
				}

				public DependencyService newService(){
					service=new DependencyService();
					return service;
				}
				
				public Level scope() {
					return scope.level;
				}

				public boolean hasScope() {
					return scope != null;
				}

				public LevelWrapper newScope() {
					scope = new LevelWrapper(Level.VO);
					return scope;
				}
				
				

				
				@Override
				public String toString() {
					return " [package_=" + package_ + ", version="
							+ version + ", service=" + service + ", scope="
							+ scope + ", optional=" + optional + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + (optional ? 1231 : 1237);
					result = prime * result
							+ ((package_ == null) ? 0 : package_.hashCode());
					result = prime * result
							+ ((scope == null) ? 0 : scope.hashCode());
					result = prime * result
							+ ((service == null) ? 0 : service.hashCode());
					result = prime * result
							+ ((version == null) ? 0 : version.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					PackageDependency other = (PackageDependency) obj;
					if (optional != other.optional)
						return false;
					if (package_ == null) {
						if (other.package_ != null)
							return false;
					} else if (!package_.equals(other.package_))
						return false;
					if (scope == null) {
						if (other.scope != null)
							return false;
					} else if (!scope.equals(other.scope))
						return false;
					if (service == null) {
						if (other.service != null)
							return false;
					} else if (!service.equals(other.service))
						return false;
					if (version == null) {
						if (other.version != null)
							return false;
					} else if (!version.equals(other.version))
						return false;
					return true;
				}


				@XmlRootElement(name="Service")
				public static class DependencyService{
					
					@XmlElement(name = "Class")
					private String clazz;

					@XmlElement(name = "Name")
					private String name;

					// we bind it and serialise it, but this is otherwise fixed
					@XmlElement(name = "Version")
					private String version;
					

					public String packageClass() {
						return clazz;
					}

					public DependencyService packageClass(String clazz) {
						this.clazz = clazz;
						return this;
					}

					public String packageName() {
						return name;
					}

					public DependencyService packageName(String name) {
						this.name = name;
						return this;
					}
					
					public String version() {
						return version;
					}

					public DependencyService version(String version) {
						this.version = version;
						return this;
					}


					@Override
					public String toString() {
						return " [clazz=" + clazz + ", name=" + name
								+ ", version=" + version + "]";
					}

					@Override
					public int hashCode() {
						final int prime = 31;
						int result = 1;
						result = prime * result
								+ ((clazz == null) ? 0 : clazz.hashCode());
						result = prime * result
								+ ((name == null) ? 0 : name.hashCode());
						result = prime * result
								+ ((version == null) ? 0 : version.hashCode());
						return result;
					}

					@Override
					public boolean equals(Object obj) {
						if (this == obj)
							return true;
						if (obj == null)
							return false;
						if (getClass() != obj.getClass())
							return false;
						DependencyService other = (DependencyService) obj;
						if (clazz == null) {
							if (other.clazz != null)
								return false;
						} else if (!clazz.equals(other.clazz))
							return false;
						if (name == null) {
							if (other.name != null)
								return false;
						} else if (!name.equals(other.name))
							return false;
						if (version == null) {
							if (other.version != null)
								return false;
						} else if (!version.equals(other.version))
							return false;
						return true;
					}

				}
				

			}

			
			@XmlRootElement(name = "TargetPlatform")
			public static class TargetPlatform extends Platform {
			}

			public static class LevelWrapper {

				@XmlAttribute(name = "level")
				private Level level;

				LevelWrapper() {
				}

				LevelWrapper(Level level) {
					this.level = level;
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((level == null) ? 0 : level.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					LevelWrapper other = (LevelWrapper) obj;
					if (level != other.level)
						return false;
					return true;
				}

			}

			@XmlType(name = "Scopes")
			@XmlEnum
			public enum Level {

				NONE, GHN, VRE, VO;

				public String value() {
					return name();
				}

				public static Level fromValue(String v) {
					return valueOf(v);
				}

			}

			@XmlRootElement(name = "Requirement")
			@XmlType(propOrder = { "category", "key", "requirement", "value", "operator" })
			public static class Requirement {

				@XmlAttribute(name = "category")
				private String category;

				@XmlAttribute(name = "key")
				private String key;

				@XmlAttribute(name = "requirement")
				private String requirement;

				@XmlAttribute(name = "value")
				private String value;

				@XmlAttribute(name = "operator")
				private OpType operator;

				public String category() {
					return category;
				}

				public Requirement category(String cat) {
					this.category = cat;
					return this;
				}

				public String key() {
					return key;
				}

				public Requirement key(String key) {
					this.key = key;
					return this;
				}

				public String requirement() {
					return requirement;
				}

				public Requirement requirement(String requirement) {
					this.requirement = requirement;
					return this;
				}

				public String value() {
					return value;
				}

				public Requirement value(String value) {
					this.value = value;
					return this;
				}

				public OpType operator() {
					return operator;
				}

				public Requirement operator(OpType operator) {
					this.operator = operator;
					return this;
				}

				@Override
				public String toString() {
					return "Requirement [category=" + category + ", key=" + key + ", requirement=" + requirement
							+ ", value=" + value + ", operator=" + operator + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((category == null) ? 0 : category.hashCode());
					result = prime * result + ((key == null) ? 0 : key.hashCode());
					result = prime * result + ((operator == null) ? 0 : operator.hashCode());
					result = prime * result + ((requirement == null) ? 0 : requirement.hashCode());
					result = prime * result + ((value == null) ? 0 : value.hashCode());
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					Requirement other = (Requirement) obj;
					if (category == null) {
						if (other.category != null)
							return false;
					} else if (!category.equals(other.category))
						return false;
					if (key == null) {
						if (other.key != null)
							return false;
					} else if (!key.equals(other.key))
						return false;
					if (operator != other.operator)
						return false;
					if (requirement == null) {
						if (other.requirement != null)
							return false;
					} else if (!requirement.equals(other.requirement))
						return false;
					if (value == null) {
						if (other.value != null)
							return false;
					} else if (!value.equals(other.value))
						return false;
					return true;
				}

				@XmlType(name = "OpType")
				@XmlEnum
				public enum OpType {

					eq, exist, ge, gt, le, lt, ne, contains;

					public String value() {
						return name();
					}

					public static OpType fromValue(String v) {
						return valueOf(v);
					}

				}

			}

		}

	}
}
