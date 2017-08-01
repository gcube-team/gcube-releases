package org.gcube.common.resources.kxml.mcollection;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Calendar;

import org.gcube.common.core.resources.GCUBEMCollection;
import org.gcube.common.resources.kxml.GCUBEResourceImpl;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.utils.KBoolean;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KGCUBEMCollection extends GCUBEMCollection implements GCUBEResourceImpl {
		
	
	public synchronized void load(Reader reader) throws Exception {
		KGCUBEResource.load(this,reader);
	}
	
	public synchronized void store(Writer writer) throws Exception {
		KGCUBEResource.store(this,writer);
	}
	
	public InputStream getSchemaResource() {
			return KGCUBEMCollection.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"metadatacollection.xsd");
	}
	
	public void load(KXmlParser parser) throws Exception {
		
		// the top-level implementation object is the only one we do not generate
		// hence we need to erase its current state before parsiing.
		// single-valued fields will be generated anew, but collection-valued fields
		// must be manually cleared before new elements are added to them by parsing

		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Description")) this.setDescription(parser.nextText());
					if (parser.getName().equals("Name")) this.setName(parser.nextText());
					if (parser.getName().equals("IsUserCollection")) this.setUserCollection(KBoolean.load(parser));
					if (parser.getName().equals("IsIndexable"))this.setIndexable(KBoolean.load(parser));
					if (parser.getName().equals("IsEditable"))  this.setEditable(KBoolean.load(parser));
					
					if (parser.getName().equals("CreationTime")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
							this.setCreationTime(cal);
							}
					if (parser.getName().equals("Creator")) this.setCreator(parser.nextText());
					if (parser.getName().equals("NumberOfMembers")) this.setNumberOfMembers(Integer.parseInt(parser.nextText()));
					
					if (parser.getName().equals("LastUpdateTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
						this.setLastUpdateTime(cal);
						}
					if (parser.getName().equals("PreviousUpdateTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
						this.setPreviousUpdateTime(cal);
						}
					if (parser.getName().equals("LastModifier")) this.setLastModifier(parser.nextText());
					if (parser.getName().equals("OID")) this.setOID(parser.nextText());
					
					if (parser.getName().equals("RelatedCollection")) this.setRelCollection(KRelatedCollection.load(parser));
					if (parser.getName().equals("MetadataFormat")) this.setMetaFormat(KMetadataFormat.load (parser));
					if (parser.getName().equals("GeneratedBy")) this.setGenerateBy(KGeneratedBy.load (parser));
					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Profile"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Profile");
			}
		}
	} 
	
	public void store(KXmlSerializer serializer) throws Exception {
		
		
		//when storing we cannot assume that single-valued fields are already initialised
		// (e.g. there might not have been a previous load).
		serializer.startTag(NS,"Profile");
		if (this.getDescription()!=null) serializer.startTag(NS,"Description").text(this.getDescription()).endTag(NS,"Description");
		if (this.getName()!=null) serializer.startTag(NS,"Name").text(this.getName()).endTag(NS,"Name");
		serializer.startTag(NS,"IsUserCollection");KBoolean.store(this.isUserCollection(), serializer);serializer.endTag(NS,"IsUserCollection");
		serializer.startTag(NS,"IsIndexable");KBoolean.store(this.isIndexable(), serializer);serializer.endTag(NS,"IsIndexable");
		serializer.startTag(NS,"IsEditable");KBoolean.store(this.isEditable(), serializer);serializer.endTag(NS,"IsEditable");
		if (this.getCreationTime()!=null) serializer.startTag(NS,"CreationTime").text(KGCUBEResource.toXMLDateAndTime(this.getCreationTime().getTime())).endTag(NS,"CreationTime");
		if (this.getCreator()!=null) serializer.startTag(NS,"Creator").text(this.getCreator()).endTag(NS,"Creator");
		serializer.startTag(NS,"NumberOfMembers").text( String.valueOf((this.getNumberOfMembers()))).endTag(NS,"NumberOfMembers");
		if (this.getLastUpdateTime()!=null) serializer.startTag(NS,"LastUpdateTime").text(KGCUBEResource.toXMLDateAndTime(this.getLastUpdateTime().getTime())).endTag(NS,"LastUpdateTime");
		if (this.getPreviousUpdateTime()!=null) serializer.startTag(NS,"PreviousUpdateTime").text(KGCUBEResource.toXMLDateAndTime(this.getPreviousUpdateTime().getTime())).endTag(NS,"PreviousUpdateTime");
		if (this.getLastModifier()!=null) serializer.startTag(NS,"LastModifier").text(this.getLastModifier()).endTag(NS,"LastModifier");
		if (this.getOID()!=null) serializer.startTag(NS,"OID").text(this.getOID()).endTag(NS,"OID");
		if (this.getRelCollection()!=null) KRelatedCollection.store(this.getRelCollection(),serializer);
		if (this.getMetaFormat()!=null) KMetadataFormat.store(this.getMetaFormat(),serializer);
		if (this.getGenerateBy()!=null) KGeneratedBy.store(this.getGenerateBy(),serializer);
		serializer.endTag(NS,"Profile");
	}

	
	 static class  KRelatedCollection {
		
		public static RelatedCollection load(KXmlParser parser) throws Exception {
			RelatedCollection e = new RelatedCollection();
			
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					
					if (parser.getName().equals("CollectionID")) e.setCollectionID(parser.nextText());
					if (parser.getName().equals("SecondaryRole")) e.setSecondaryRole(parser.nextText());
					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("RelatedCollection"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at RelatedCollection");
			}
		}
			return e;
	} 
	
	public static void store(RelatedCollection col,KXmlSerializer serializer) throws Exception {
		if (col == null) return ;
		serializer.startTag(NS,"RelatedCollection");
			if (col.getCollectionID()!= null) serializer.startTag(NS, "CollectionID").text(col.getCollectionID()).endTag(NS, "CollectionID");
			if (col.getSecondaryRole()!= null) serializer.startTag(NS, "SecondaryRole").text(col.getSecondaryRole()).endTag(NS, "SecondaryRole");
		serializer.endTag(NS,"RelatedCollection");
	}

	}
	
	 static class  KMetadataFormat {
			
			public static MetadataFormat load(KXmlParser parser) throws Exception {
				MetadataFormat e = new MetadataFormat();
				
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						
						if (parser.getName().equals("Language")) e.setLanguage(parser.nextText());
						if (parser.getName().equals("Name")) e.setName(parser.nextText());
						if (parser.getName().equals("SchemaURI")) e.setSchemaURI(new URI(parser.nextText()));
						
						break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("MetadataFormat"))	break loop;
						break;
					case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at MetadataFormat");
				}
			}
				return e;
		} 
		
		public static void store(MetadataFormat col,KXmlSerializer serializer) throws Exception {
			if (col == null) return ;
			serializer.startTag(NS,"MetadataFormat");
				if (col.getSchemaURI()!= null) serializer.startTag(NS, "SchemaURI").text(col.getSchemaURI().toURL().toString()).endTag(NS, "SchemaURI");
				if (col.getLanguage()!= null) serializer.startTag(NS, "Language").text(col.getLanguage()).endTag(NS, "Language");
				if (col.getName()!= null) serializer.startTag(NS, "Name").text(col.getName()).endTag(NS, "Name");
			serializer.endTag(NS,"MetadataFormat");
		}

		}
	 
	 static class  KGeneratedBy {
			
			public static GeneratedBy load(KXmlParser parser) throws Exception {
				GeneratedBy e = new GeneratedBy();
				
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						
						if (parser.getName().equals("CollectionID")) e.setCollectionID(parser.nextText());
						if (parser.getName().equals("SourceSchemaURI")) e.setSourceSchemaURI(new URI(parser.nextText()));
						
						break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("GeneratedBy"))	break loop;
						break;
					case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at GeneratedBy");
				}
			}
				return e;
		} 
		
		public static void store(GeneratedBy col,KXmlSerializer serializer) throws Exception {
			if (col == null) return ;
			serializer.startTag(NS,"GeneratedBy");
				if (col.getCollectionID()!= null) serializer.startTag(NS, "CollectionID").text(col.getCollectionID()).endTag(NS, "CollectionID");
				if (col.getSourceSchemaURI()!= null) serializer.startTag(NS, "SourceSchemaURI").text(col.getSourceSchemaURI().toURL().toString()).endTag(NS, "SourceSchemaURI");
			serializer.endTag(NS,"GeneratedBy");
		}

		}
}
