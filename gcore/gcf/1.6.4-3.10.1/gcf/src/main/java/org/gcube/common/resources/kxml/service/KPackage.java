package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.Dependency;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Package.GHNRequirement;
import org.gcube.common.core.resources.service.Package.MavenCoordinate;
import org.gcube.common.core.resources.service.Package.ScopeLevel;
import org.gcube.common.core.resources.service.Package.GHNRequirement.Category;
import org.gcube.common.core.resources.service.Package.GHNRequirement.OpType;
import org.gcube.common.resources.kxml.common.KPlatform;
import org.gcube.common.resources.kxml.utils.KAny;
import org.gcube.common.resources.kxml.utils.KBoolean;
import org.gcube.common.resources.kxml.utils.KStringList;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * @author Manuele Simi (ISTI-CNR)
 */
public class KPackage {
	public static void load(Package p, KXmlParser parser) throws Exception {	
			if (parser.getName().equals("Description")) p.setDescription(parser.nextText().trim());
			if (parser.getName().equals("Name")) p.setName(parser.nextText().trim());
			if (parser.getName().equals("Version")) p.setVersion(parser.nextText().trim());
			if (parser.getName().equals("MavenCoordinates")) loadCoordinates(p,parser);
			if (parser.getName().equals("TargetPlatform")) p.setTargetPlatform(KPlatform.load(parser, "TargetPlatform"));
			if (parser.getName().equals("MultiVersion")) p.setMultiVersion(KBoolean.load(parser));
			if (parser.getName().equals("Mandatory")) p.setMandatoryLevel(ScopeLevel.valueOf(parser.getAttributeValue(NS, "level")));
			if (parser.getName().equals("Shareable")) p.setSharingLevel(ScopeLevel.valueOf(parser.getAttributeValue(NS, "level")));
			if (parser.getName().equals("Requirement")) p.getGHNRequirements().add(KGHNRequirement.load(parser));
			if (parser.getName().equals("InstallScripts")) p.setInstallScripts(KStringList.load("InstallScripts",parser));
			if (parser.getName().equals("UninstallScripts")) p.setUninstallScripts(KStringList.load("UninstallScripts",parser));
			if (parser.getName().equals("RebootScripts")) p.setRebootScripts(KStringList.load("RebootScripts",parser));
			if (parser.getName().equals("Dependency")) p.getDependencies().add(KDependency.load(parser));
			if (parser.getName().equals("SpecificData")) p.setSpecificData(KAny.load("SpecificData",parser));
	}

	private static void loadCoordinates(Package p, KXmlParser parser) throws Exception {
		String groupId = null, artifactId = null,version =null, classifier=null;
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
									if (parser.getName().equals(MavenCoordinate.groupId.name())) 
										groupId = parser.nextText().trim();
									if (parser.getName().equals(MavenCoordinate.artifactId.name())) 
										artifactId = parser.nextText().trim();
									if (parser.getName().equals(MavenCoordinate.version.name())) 
										version = parser.nextText().trim();
									if (parser.getName().equals(MavenCoordinate.classifier.name())) 
										classifier = parser.nextText().trim();
								break;
								case KXmlParser.END_TAG:
									if (parser.getName().equals("MavenCoordinates")) break loop;
								break;
							case KXmlParser.END_DOCUMENT :
								throw new Exception("Parsing failed at MavenCoordinates");				
			}
		}	
		p.setMavenCoordinates(groupId, artifactId, version, classifier);

		
	}

	public static void store(Package component, KXmlSerializer serializer) throws Exception {
		if (component.getDescription()!=null) serializer.startTag(NS,"Description").text(component.getDescription().trim()).endTag(NS,"Description");
		if (component.getName()!=null) serializer.startTag(NS,"Name").text(component.getName().trim()).endTag(NS,"Name");
		if (component.getVersion()!=null) serializer.startTag(NS,"Version").text(component.getVersion().trim()).endTag(NS,"Version");
		if (component.getMavenCoordinate(MavenCoordinate.groupId) != null) {
			serializer.startTag(NS,"MavenCoordinates");
			serializer.startTag(NS, MavenCoordinate.groupId.name()).text(component.getMavenCoordinate(MavenCoordinate.groupId)).endTag(NS,MavenCoordinate.groupId.name());
			serializer.startTag(NS, MavenCoordinate.artifactId.name()).text(component.getMavenCoordinate(MavenCoordinate.artifactId)).endTag(NS,MavenCoordinate.artifactId.name());
			serializer.startTag(NS, MavenCoordinate.version.name()).text(component.getMavenCoordinate(MavenCoordinate.version)).endTag(NS,MavenCoordinate.version.name());
			if (component.getMavenCoordinate(MavenCoordinate.classifier) != null) 
				serializer.startTag(NS, MavenCoordinate.classifier.name()).text(component.getMavenCoordinate(MavenCoordinate.classifier)).endTag(NS,MavenCoordinate.classifier.name());
			serializer.endTag(NS,"MavenCoordinates");
		}

		if (component.getTargetPlatform()!=null) KPlatform.store(component.getTargetPlatform(), serializer, "TargetPlatform");
		if (component.getMultiVersion()!=null) {serializer.startTag(NS,"MultiVersion");KBoolean.store(component.getMultiVersion(),serializer);serializer.endTag(NS,"MultiVersion");}
		if (component.getMandatoryLevel()!=null) serializer.startTag(NS,"Mandatory").attribute(NS, "level",component.getMandatoryLevel().toString()).endTag(NS,"Mandatory");
		if (component.getSharingLevel()!=null) serializer.startTag(NS,"Shareable").attribute(NS, "level",component.getSharingLevel().toString()).endTag(NS,"Shareable");
		if (component.getGHNRequirements().size()!=0){ serializer.startTag(NS,"GHNRequirements");for (GHNRequirement r : component.getGHNRequirements()) KGHNRequirement.store(r,serializer);serializer.endTag(NS,"GHNRequirements");}
		if (component.getInstallScripts().size()!=0) KStringList.store("InstallScripts","File", component.getInstallScripts(), serializer);
		if (component.getUninstallScripts().size()!=0) KStringList.store("UninstallScripts","File", component.getUninstallScripts(), serializer);
		if (component.getRebootScripts().size()!=0) KStringList.store("RebootScripts","File", component.getRebootScripts(), serializer);
		if (component.getDependencies().size()!=0)  {serializer.startTag(NS,"Dependencies");for (Dependency d : component.getDependencies()) KDependency.store(d, serializer);serializer.endTag(NS,"Dependencies");}
		KAny.store("SpecificData",component.getSpecificData(), serializer);
		
	}
	
		
	public static class KGHNRequirement {
		
		public static GHNRequirement load(KXmlParser parser) throws Exception {
			
			GHNRequirement r = new GHNRequirement();
			String categoryName = parser.getAttributeValue(NS, "category").trim();
			if (Category.hasValue(categoryName))
				r.setCategory(Category.valueOf(categoryName));
			else 
				r.setCategory(categoryName);
			if (parser.getAttributeValue(NS, "key") != null)
					r.setKey(parser.getAttributeValue(NS, "key").trim());
			r.setValue(parser.getAttributeValue(NS, "value").trim());
			if (parser.getAttributeValue(NS, "requirement") != null)
				r.setRequirement(parser.getAttributeValue(NS, "requirement").trim());
			r.setOperator(OpType.fromValue(parser.getAttributeValue(NS, "operator").trim()));			
			return r; 
		}
		
		public static void store(GHNRequirement component, KXmlSerializer serializer) throws Exception {
			if (component.getCategory() != null) {
				serializer.startTag(NS,"Requirement");
				serializer.attribute(NS,"category",component.getCategory().toString());
				if (component.getKey() != null)
					serializer.attribute(NS,"key",component.getKey().trim());
				if (component.getRequirement() != null)
					serializer.attribute(NS,"requirement",component.getRequirement().trim());
				if (component.getValue() != null)
					serializer.attribute(NS,"value",component.getValue().trim());			
				serializer.attribute(NS, "operator", component.getOperator().value().trim());
				serializer.endTag(NS,"Requirement");
			}
		}
		
	}
	
}
