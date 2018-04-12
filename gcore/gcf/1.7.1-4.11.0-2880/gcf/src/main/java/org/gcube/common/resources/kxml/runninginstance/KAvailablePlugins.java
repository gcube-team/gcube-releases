package org.gcube.common.resources.kxml.runninginstance;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.HashSet;
import java.util.Set;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

import org.gcube.common.core.resources.runninginstance.AvailablePlugins;
import org.gcube.common.core.resources.runninginstance.AvailablePlugins.AvailablePlugin;

/**
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class KAvailablePlugins {

	public static AvailablePlugins load(KXmlParser parser) throws Exception {		
		AvailablePlugins plugins = new AvailablePlugins();
		Set<AvailablePlugin> pluginsSet = new HashSet<AvailablePlugin>();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG:
					String tag = parser.getName(); 
					if (tag.equals("Plugin")) pluginsSet.add(loadPlugin(plugins.new AvailablePlugin(), parser));					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("AvailablePlugins")) break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at AvailablePlugins");
			}
		}		
		plugins.setPlugins(pluginsSet);
		return plugins;		
	}

	private static AvailablePlugin loadPlugin(AvailablePlugin plugin, KXmlParser parser) throws Exception {
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					String tag = parser.getName(); 		
					if (tag.equals("Service")) loadService(parser, plugin);
					if (tag.equals("Package"))	plugin.setPluginPackage(parser.nextText().trim());
					if (tag.equals("Version")) plugin.setPluginVersion(parser.nextText().trim());
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Plugin")) break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Plugin");
			}
		}
		
		return plugin;
	}	

	private static void loadService(KXmlParser parser, AvailablePlugin plugin) throws Exception {
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					String tag = parser.getName();
					if (tag.equals("Class")) plugin.setClazz(parser.nextText().trim());
					if (tag.equals("Name"))	plugin.setName(parser.nextText().trim());
					if (tag.equals("Version")) plugin.setVersion(parser.nextText().trim());
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Service"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Service");
			}
		}
		
	}

	
	public static void store(AvailablePlugins plugins, KXmlSerializer serializer) throws Exception {
		if ((plugins != null) && (plugins.getPlugins() != null) && (plugins.getPlugins().size() > 0)) {
			serializer.startTag(NS,"AvailablePlugins");
			for (AvailablePlugin plugin : plugins.getPlugins()) storePlugin(plugin, serializer);			
			serializer.endTag(NS,"AvailablePlugins");
		}
		
	}

	private static void storePlugin(AvailablePlugin plugin, KXmlSerializer serializer) throws Exception  {
		if (plugin != null) {
			serializer.startTag(NS,"Plugin");
			serializer.startTag(NS,"Service");
			if (plugin.getClazz()!=null) serializer.startTag(NS,"Class").text(plugin.getClazz().trim()).endTag(NS,"Class");
			if (plugin.getName()!=null) serializer.startTag(NS,"Name").text(plugin.getName().trim()).endTag(NS,"Name");
			if (plugin.getVersion()!=null) serializer.startTag(NS,"Version").text(plugin.getVersion().trim()).endTag(NS,"Version");
			serializer.endTag(NS,"Service");
			if (plugin.getPluginPackage()!=null) serializer.startTag(NS,"Package").text(plugin.getPluginPackage().trim()).endTag(NS,"Package");
			if (plugin.getPluginVersion()!=null) serializer.startTag(NS,"Version").text(plugin.getPluginVersion().trim()).endTag(NS,"Version");
			serializer.endTag(NS,"Plugin");
		}
		
	}
	
}
