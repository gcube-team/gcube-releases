package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.Configuration;
import org.gcube.common.core.resources.service.Configuration.DynamicConfiguration;
import org.gcube.common.core.resources.service.Configuration.StaticConfiguration;
import org.gcube.common.core.resources.service.Configuration.StaticConfiguration.Config;
import org.gcube.common.core.resources.service.Configuration.StaticConfiguration.Template;
import org.gcube.common.core.resources.service.Configuration.StaticConfiguration.Template.TemplateParam;
import org.gcube.common.core.resources.service.Configuration.StaticConfiguration.Template.TemplateParam.TemplateParamValue;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KConfiguration {
	public static Configuration load(KXmlParser parser) throws Exception {
		Configuration config=new Configuration();;
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					String tag = parser.getName(); //remember position and name of tag
					if (tag.equals("Static")) config.setStaticConfig(KStaticConfiguration.load(parser));
					if (tag.equals("Dynamic")) config.setDynamicConfig(KDynamicConfiguration.load(parser));
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Configuration")) break loop;
					break;
				case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Configuration");
			}
		}
		return config;
	
	}
	
	public static void store(Configuration component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"Configuration");
			KStaticConfiguration.store(component.getStaticConfig(), serializer);
			KDynamicConfiguration.store(component.getDynamicConfig(), serializer);
		serializer.endTag(NS,"Configuration");
	}
	
	static class KStaticConfiguration {
		public static StaticConfiguration load(KXmlParser parser) throws Exception {
			StaticConfiguration staticConfig=new StaticConfiguration();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : //remember position and name of tag
						if (parser.getName().equals("Config")) staticConfig.getConfigurations().add(KConfig.load(parser));
						if (parser.getName().equals("Template")) staticConfig.setTemplate(KTemplate.load(parser));
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Static")) break loop;
						break;
					case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Static (Configuration)");
				}
			}
			return staticConfig;
		
		}
		public static void store(StaticConfiguration component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS, "Static");
			if (component.getConfigurations().size()!=0) serializer.startTag(NS,"Configs");for (Config c : component.getConfigurations()) KConfig.store(c, serializer);serializer.endTag(NS,"Configs");
				KTemplate.store(component.getTemplate(), serializer);
			serializer.endTag(NS, "Static");
		}
		
		
		static class KConfig {
			public static Config load(KXmlParser parser) throws Exception {
				Config config=new Config();
				loop: while (true) {
					switch (parser.next()){			
						case KXmlParser.START_TAG :
							config.setDefault(Boolean.valueOf(parser.getAttributeValue(NS, "default")));
							if (parser.getName().equals("File")) config.setFile(parser.nextText());
							if (parser.getName().equals("Description")) config.setDescription(parser.nextText());
							if (parser.getName().equals("Label")) config.setLabel(parser.nextText());
						break;
						case KXmlParser.END_TAG:
							if (parser.getName().equals("Config")) break loop; 
							break;
						case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Config (Static Configuration)");
					}
				}
				return config;
			
			}
			public static void store(Config component, KXmlSerializer serializer) throws Exception {
				if (component==null)  return;
				serializer.startTag(NS,"Config");
				serializer.attribute(NS, "default", component.isDefault()+"");
				if (component.getFile()!=null) serializer.startTag(NS, "File").text(component.getFile()).endTag(NS,"File");
				if (component.getDescription()!=null) serializer.startTag(NS, "Description").text(component.getDescription()).endTag(NS,"Description");
				if (component.getLabel()!=null) serializer.startTag(NS, "Label").text(component.getLabel()).endTag(NS,"Label");
				serializer.endTag(NS,"Config");
			}
		}
		
		
		static class KTemplate {
			public static Template load(KXmlParser parser) throws Exception {
				Template template=new Template();
				loop: while (true) {
					switch (parser.next()){			
						case KXmlParser.START_TAG : 
							if (parser.getName().equals("Param")) template.getParameters().add(KTemplateParam.load(parser));
						break;
						case KXmlParser.END_TAG:
							if (parser.getName().equals("Template")) break loop;
							break;
						case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Template (Static Configuration)");
					}
				}
			return template;
			
			}
			public static void store(Template component, KXmlSerializer serializer) throws Exception {
				if (component==null) return;
				if (component.getParameters().size()!=0) return;
				serializer.startTag(NS,"Template");
					serializer.startTag(NS, "Params");
					for (TemplateParam p : component.getParameters()) KTemplateParam.store(p, serializer);serializer.endTag(NS, "Params");	
				serializer.endTag(NS,"Template");
			}
		}
		
		static class KTemplateParam {
			public static TemplateParam load(KXmlParser parser) throws Exception {
				TemplateParam param=new TemplateParam();
				loop: while (true) {
					switch (parser.next()){			
						case KXmlParser.START_TAG : 
							if (parser.getName().equals("Name")) param.setName(parser.nextText());
							if (parser.getName().equals("Description")) param.setDescription(parser.nextText());
							if (parser.getName().equals("Value")) param.getValues().add(KTemplParamValue.load(parser));
						break;
						case KXmlParser.END_TAG:
							if (parser.getName().equals("Param")) break loop;
							break;
						case KXmlParser.END_DOCUMENT :throw new Exception("Parsing failed at Param (Static Configuration)");
					}
				}
			return param;
			
			}
			public static void store(TemplateParam component, KXmlSerializer serializer) throws Exception {
				if (component==null) return;
				serializer.startTag(NS, "Param");
					if (component.getName()!=null) serializer.startTag(NS,"Name").text(component.getName()).endTag(NS,"Name");
					if (component.getDescription()!=null) serializer.startTag(NS,"Description").text(component.getDescription()).endTag(NS,"Description");
					if (component.getValues().size()!=0) serializer.startTag(NS,"AllowedValues");for (TemplateParamValue v : component.getValues()) KTemplParamValue.store(v, serializer);	serializer.endTag(NS,"AllowedValues");
				serializer.endTag(NS,"Param");
			}
			
			
			static class KTemplParamValue {
				public static TemplateParamValue load(KXmlParser parser) throws Exception {
					TemplateParamValue paramValue=new TemplateParamValue();
					loop: while (true) {
						switch (parser.next()){			
							case KXmlParser.START_TAG : 
								if (parser.getName().equals("Description")) paramValue.setDescription(parser.nextText());										
								if (parser.getName().equals("Literal")) paramValue.setLiteral(parser.nextText());										
								if (parser.getName().equals("Label")) paramValue.setLabel(parser.nextText());										
								paramValue.setDef(Boolean.valueOf(parser.getAttributeValue(NS, "default")));
							break;
							case KXmlParser.END_TAG:
								if (parser.getName().equals("Value")) break loop;
								break;
							case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Value (Static Configuration)");
						}
					}
				return paramValue;
				}
			
				public static void store(TemplateParamValue component, KXmlSerializer serializer) throws Exception {
					if (component==null) return;
					serializer.startTag(NS, "Value");
						serializer.attribute(NS, "default", component.isDef()+"");
						if (component.getDescription()!=null) serializer.startTag(NS, "Description").text(component.getDescription()).endTag(NS,"Description");
						if (component.getLiteral()!=null) serializer.startTag(NS, "Literal").text(component.getLiteral()).endTag(NS,"Literal");
						if (component.getLabel()!=null) serializer.startTag(NS, "Label").text(component.getLabel()).endTag(NS,"Label");
					serializer.endTag(NS, "Value");
				}
			}
		}
			
	}
	
	static class KDynamicConfiguration {
		public static DynamicConfiguration load(KXmlParser parser) throws Exception {
			DynamicConfiguration dynamicConfig=new DynamicConfiguration();
			// TODO
			return dynamicConfig;
		
		}
		public static void store(DynamicConfiguration component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS, "Dynamic");
			// TODO
			serializer.endTag(NS, "Dynamic");
		}
	}
}
