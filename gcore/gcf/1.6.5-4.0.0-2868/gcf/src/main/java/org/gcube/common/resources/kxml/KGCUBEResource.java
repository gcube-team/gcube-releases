package org.gcube.common.resources.kxml;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.kxml.utils.KStringList;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Shared parser for all {@link KGCUBEResource KGCUBEResources}.
 * The parser can be shared for serialisation and deserialisation of different resource across multiple threads. 
 * 
 * @author Fabio Simeoni (University of Strathclyde), Luca Frosini (ISTI-CNR)
 *
 */
public class KGCUBEResource {

	
	public static final String NS = null;
	
	static final DateFormat dateAndTime=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static final String SCHEMA_RESOURCE_LOCATION="/org/gcube/common/resources/kxml/schemas/";
	
	protected static final GCUBELog logger = new GCUBELog(KGCUBEResource.class,"Resource Parser");
	
	protected static Map<Class<? extends GCUBEResourceImpl>,Schema> schemaMap = new HashMap<Class<? extends GCUBEResourceImpl>,Schema>();
	protected static final SchemaFactory sfactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	protected static DOMImplementationLS domImplementation;

	
	/**
	 * Deserialises a {@link GCUBEResourceImpl} which implements a {@link GCUBEResource} from a
	 * serialisation. 
	 * @param <T> the type of the {@link GCUBEResourceImpl}.
	 * @param component the {@link GCUBEResourceImpl}.
	 * @param reader the {@link java.io.Reader} which gives access to the {@link GCUBEResourceImpl}.
	 * @throws Exception if the {@link GCUBEResourceImpl} could not be deserialised. 
	 */
	public static <T extends GCUBEResource & GCUBEResourceImpl> void load(T component, Reader reader) throws Exception {
		
			try {
				//first copy reader source into a string so as to reuse with multiple passes
				Writer writer = new StringWriter();
				char[] chars = new char[1000];int count;
				while ((count=reader.read(chars))!=-1) writer.write(chars,0,count);
					
				//then validate
				validate(new BufferedReader(new StringReader(writer.toString())), getSchema(component));
				
				//then parse
				KXmlParser parser = new KXmlParser(); // use a new parser for the task
				parser.setInput(new BufferedReader(new StringReader(writer.toString()))); //set its input
				//parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
				
				loop: while (true) {
					switch (parser.next()){			
						case KXmlParser.START_TAG :
							if ((parser.getName().equals("Resource")) && (parser.getAttributeValue(NS, "version")!=null)) 
								component.setResourceVersion(parser.getAttributeValue(NS, "version"));
							if (parser.getName().equals("ID")) {
								String id= parser.nextText();
								if (id!=null && id.length()>0)component.setID(id);
							}							
							//no resource type, already set at creation time.
							if (parser.getName().equals("Scopes")) {
								List<GCUBEScope> scopes = new ArrayList<GCUBEScope>(); 
									for (String s : KStringList.load("Scopes",parser)) scopes.add(GCUBEScope.getScope(s));
								component.addScope(scopes.toArray(new GCUBEScope[0]));
							}
							if (parser.getName().equals("Profile"))	component.load(parser);//job done, exit
							break;
						case KXmlParser.END_DOCUMENT :	break loop;
					}
				}
			}
			catch(Exception e) {
				throw new Exception("Could not load resource from its serialisation",e);
			}
	}

	/**
	 * Serialises a {@link GCUBEResourceImpl} which implements a {@link GCUBEResource}.
	 * @param <T> the type of the {@link GCUBEResourceImpl}.
	 * @param component the {@link GCUBEResourceImpl}.
	 * @param writer the {@link java.io.Writer} which gives access to the serialisation.
	 * @throws Exception if the {@link GCUBEResourceImpl} could not be serialised. 
	 */
	public static <T extends GCUBEResource & GCUBEResourceImpl> void store(T component, Writer writer) throws Exception {
		
		StringWriter tempWriter = new StringWriter(); // serialises to a temporary writer first
		KXmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(tempWriter);
	
		try {
			serializer.startDocument("UTF-8", true);
			serializer.startTag(NS,"Resource");
			serializer.attribute(NS, "version", component.getResourceVersion());
			if (component.getID()!=null) serializer.startTag(NS,"ID").text(component.getID()).endTag(NS,"ID");
			if (component.getType()!=null) serializer.startTag(NS,"Type").text(component.getType()).endTag(NS,"Type");
			if (component.getScopes().size()!=0) {
				KStringList.store("Scopes","Scope", new ArrayList<String>(component.getScopes().keySet()),serializer);
			}
			component.store(serializer);//propagate
			serializer.endTag(NS,"Resource");
			serializer.endDocument();
			
			//System.out.println(tempWriter.toString());
			validate(new BufferedReader(new StringReader(tempWriter.toString())),getSchema(component));//validate serialisation
			
			//clone onto original writer
			BufferedReader serialisationReader = new BufferedReader(new StringReader(tempWriter.toString()));
			String line;
			while ((line=serialisationReader.readLine())!=null) writer.write(line);			
			serialisationReader.close();
		}
		catch (Exception e) {
			throw new Exception("The resource does not have a valid serialisation",e);
		}
		finally {
			writer.close();
		}
	}
	
	protected synchronized static Schema getSchema(GCUBEResourceImpl component) throws Exception {
		
		 
		//factory.setResourceResolver( new MyLSResourceResolver());
		sfactory.setResourceResolver(new SchemaResolver());
		Schema schema = schemaMap.get(component.getClass());
		if (schema==null) {
			
			if (domImplementation==null) domImplementation = 
				(DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
			
			
	
			synchronized(sfactory) {//one thread at the time please
				schema = sfactory.newSchema(new StreamSource(component.getSchemaResource()));
			    schemaMap.put(component.getClass(),schema);
			}
		    
		}
		return schema;
		
	}
	
	//helper
	static class BooleanWrapper {
		boolean valid=true;
		Exception exception;
	}
	
	protected static class SchemaResolver implements LSResourceResolver {

		public LSInput resolveResource(String type, String namespaceURI,String publicId, String systemId, String baseURI) {
			LSInput source = null;
			if (systemId.equals("CommonTypeDefinitions.xsd")) {
				source = domImplementation.createLSInput();
				source.setByteStream(KGCUBEResource.class.getResourceAsStream(KGCUBEResource.SCHEMA_RESOURCE_LOCATION+"CommonTypeDefinitions.xsd"));
			}
			return source;
		}
	};
	
	//validator
	protected static void validate(Reader reader, Schema schema) throws Exception {
	 
		//force internal 1.5 implementation to avoid clashes with old deployed xerces impls
		SAXParserFactory factory = new com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl();
		factory.setSchema(schema);
		factory.setNamespaceAware(true);
	
		XMLReader xmlReader = factory.newSAXParser().getXMLReader();
		
		final BooleanWrapper wrapper = new BooleanWrapper();
		
		ErrorHandler handler = new DefaultHandler() {
	         public void error(SAXParseException e) {fatalError(e);}
		      public void fatalError(SAXParseException e) {
		         wrapper.valid=false;
		         wrapper.exception=new Exception("Line:" + e.getLineNumber() + ":" + e.getMessage() + "\n");
		     }
		}; 
	
		xmlReader.setErrorHandler(handler);	
		xmlReader.parse(new InputSource(reader));
		if (!wrapper.valid) throw wrapper.exception;
		
	}

	private static DateFormat getDateAndTime() {
		return KGCUBEResource.dateAndTime;
	}
	
	/**
	 * Transforms the input {@link Date} in a valid string representation for the dateAndTime XML Schema data type 
	 * @param date the {@link Date} object to tranform
	 * @return the {@link String} object
	 */
	public static synchronized String toXMLDateAndTime(Date date) {
		String formatted = getDateAndTime().format(date);
		StringBuilder toXS = new StringBuilder();
		toXS.append(formatted.substring(0, formatted.length()-2));
		toXS.append(":");
		toXS.append(formatted.substring(formatted.length()-2, formatted.length()));
		return toXS.toString();
		
	}
	
	/**
	 * Transforms the input date in a {@link Date} object
	 * @param date the string representation of the date 
	 * @return the {@link Date} object
	 * @throws ParseException if the input date is not in an valid format
	 */
	public static synchronized Date fromXMLDateAndTime(String date) throws ParseException {		
		
		//the test is for backward compatibility, to read the old profiles that have no time zone in the dateAndTime fields
		Pattern p = Pattern.compile("^.*T\\d{2}:\\d{2}:\\d{2}$"); //ends with 'T'HH:mm:ss		
		if (p.matcher(date).matches()) {
			 return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);				
		} else {
			StringBuilder toDate = new StringBuilder();
			toDate.append(date.substring(0, date.length()-3));		
			toDate.append(date.substring(date.length()-2, date.length()));
			return  getDateAndTime().parse(toDate.toString());		
		}
		
			
	}
}



