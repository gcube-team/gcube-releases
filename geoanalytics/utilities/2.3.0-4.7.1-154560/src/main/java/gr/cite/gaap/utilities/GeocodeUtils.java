package gr.cite.gaap.utilities;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.context.i18n.LocaleContextHolder;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;

public class GeocodeUtils 
{
	private static DocumentBuilderFactory dbf = null;
	private static DocumentBuilder db = null;
	private static Object dbLock = new Object();
	
	private static Document parseDocument(String data, DocumentBuilder db) throws Exception
	{
		if(db == GeocodeUtils.db)
		{
			synchronized(dbLock)
			{
				return db.parse(new ByteArrayInputStream(data.getBytes("UTF-8")));
			}
		}else
			return db.parse(new ByteArrayInputStream(data.getBytes("UTF-8")));
	}
	
	public static String storeLocalizedValue(Geocode tt, String locale, String value) throws Exception
	{
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return storeLocalizedValue(tt, locale, value, db);
	}
	
	public static String storeLocalizedValue(Geocode tt, String locale, String value, DocumentBuilder db) throws Exception
	{
		if(tt.getExtraData() == null || tt.getExtraData().isEmpty())
			tt.setExtraData("<extraData></extraData>");
		
		Document d = parseDocument(tt.getExtraData(), db);
		NodeList names = d.getElementsByTagName("value");
		boolean found = false;
		for(int i=0; i<names.getLength(); i++)
		{
			if(((Element)names.item(i)).getAttribute("locale").equals(locale))
			{
				found = true;
				break;
			}
		}
		if(!found)
		{
			Element n = d.createElement("value");
			Attr l = d.createAttribute("locale");
			l.setValue(locale);
			n.appendChild(l);
			Node c = d.createTextNode(value);
			n.appendChild(c);
			d.appendChild(n);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer tr = tf.newTransformer();
			StringWriter sw = new StringWriter();  
			tr.transform(new DOMSource(d), new StreamResult(sw));  
			sw.toString();
			return sw.toString();
		}
		return null;
	}
	
	
	public static boolean isUserCreated(Geocode tt) throws Exception
	{
		if(tt.getExtraData() == null || tt.getExtraData().isEmpty())
			return false;
		
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return isUserCreated(tt, db);
	}
	
	public static boolean isUserCreated(Geocode tt, DocumentBuilder db) throws Exception
	{
		if(tt.getExtraData() == null || tt.getExtraData().isEmpty())
			return false;
		Document d = parseDocument(tt.getExtraData(), db);
		
		String userCreatedAttr = d.getDocumentElement().getAttribute("userCreated"); 
		if(userCreatedAttr == null || userCreatedAttr.trim().isEmpty())
			return false;
		
		return Boolean.parseBoolean(userCreatedAttr.trim());
	}
	
	public static String getUserCreatedTermValue(Geocode tt) throws Exception
	{
		if(tt.getExtraData() == null || tt.getExtraData().isEmpty())
			return null;
		
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return getUserCreatedTermValue(tt, db);
	}
	
	public static String getUserCreatedTermValue(Geocode tt, DocumentBuilder db) throws Exception
	{
		if(tt.getExtraData() == null || tt.getExtraData().isEmpty())
			return null;
		
		Document d = parseDocument(tt.getExtraData(), db);
		String userCreatedAttr = d.getDocumentElement().getAttribute("userCreated"); 
		if(userCreatedAttr != null && !userCreatedAttr.isEmpty() && Boolean.parseBoolean(userCreatedAttr) == true)
		{
			NodeList nl = d.getDocumentElement().getElementsByTagName("value");
			for(int i=0; i < nl.getLength(); i++)
			{
				Element el = (Element)nl.item(i);
				String localeAttr = el.getAttribute("locale");
				if(localeAttr != null && !localeAttr.isEmpty())
				{
					if(localeAttr.equals(LocaleContextHolder.getLocale().getLanguage()))
						return el.getFirstChild().getNodeValue();
				}
			}
			if(nl.getLength() == 0)
				return nl.item(0).getFirstChild().getNodeValue();
			
		}
		return "";
	}
	
	
}

