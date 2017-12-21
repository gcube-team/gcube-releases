package gr.cite.gaap.utilities;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;

public class TaxonomyUtils 
{
	private static DocumentBuilderFactory dbf = null;
	private static DocumentBuilder db = null;
	private static Object dbLock = new Object();
	
	private static Document parseDocument(String data, DocumentBuilder db) throws Exception
	{
		if(db == TaxonomyUtils.db)
		{
			synchronized(dbLock)
			{
				return db.parse(new ByteArrayInputStream(data.getBytes("UTF-8")));
			}
		}else
			return db.parse(new ByteArrayInputStream(data.getBytes("UTF-8")));
	}
	
	public static boolean isEditable(GeocodeSystem t) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return false;
		
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return isEditable(t, db);
	}
	
	public static boolean isEditable(GeocodeSystem t, DocumentBuilder db) throws Exception
	{
		if(t.getExtraData() != null && !t.getExtraData().isEmpty())
		{
			Document d = db.parse(new ByteArrayInputStream(t.getExtraData().getBytes("UTF-8")));
			String editable = d.getDocumentElement().getAttribute("editable");
			if(editable != null && !editable.isEmpty())
			{
				if(Boolean.parseBoolean(editable) == false)
					return false;
			}
		}else return false;
		return true;
	}
	
	public static String storeLocalizedName(GeocodeSystem t, String locale, String name) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			t.setExtraData("<extraData></extraData>");
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return storeLocalizedName(t, locale, name, db);
	}
	public static String storeLocalizedName(GeocodeSystem t, String locale, String name, DocumentBuilder db) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			t.setExtraData("<extraData></extraData>");
		Document d = db.parse(new ByteArrayInputStream(t.getExtraData().getBytes("UTF-8")));
		NodeList names = d.getElementsByTagName("name");
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
			Element n = d.createElement("name");
			Attr l = d.createAttribute("locale");
			l.setValue(locale);
			n.appendChild(l);
			Node c = d.createTextNode(name);
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
	
	public static String retrieveLocalizedName(GeocodeSystem t, String locale) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return null;
		
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return retrieveLocalizedName(t, locale, db);
	}
	
	public static String retrieveLocalizedName(GeocodeSystem t, String locale, DocumentBuilder db) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return null;
		Document d = parseDocument(t.getExtraData(), db);
		NodeList names = d.getElementsByTagName("name");
		for(int i=0; i<names.getLength(); i++)
		{
			if(((Element)names.item(i)).getAttribute("locale").equals(locale))
				return names.item(i).getFirstChild().getNodeValue();
		}
		return null;
	}
	
	public static boolean isOrdered(GeocodeSystem t) throws Exception
	{
		return getOrder(t) != null;
	}
	
	public static boolean isOrdered(GeocodeSystem t, DocumentBuilder db) throws Exception
	{
		return getOrder(t, db) != null;
	}
	
	public static Integer getOrder(GeocodeSystem t) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return null;
		
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return getOrder(t, db);
	}
	
	public static Integer getOrder(GeocodeSystem t, DocumentBuilder db) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return null;
		Document d = parseDocument(t.getExtraData(), db);
		NodeList nl = d.getDocumentElement().getElementsByTagName("order");
		if(nl.getLength() == 0)
			return null;
		return Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
	}
	
	public static boolean isGeographic(GeocodeSystem t) throws Exception
	{

		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return false;
		
		synchronized(dbLock)
		{
			if(db == null)
			{
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
			}
		}
		return isGeographic(t, db);
	}
	
	public static boolean isGeographic(GeocodeSystem t, DocumentBuilder bd) throws Exception
	{
		if(t.getExtraData() == null || t.getExtraData().isEmpty())
			return false;
		Document d = parseDocument(t.getExtraData(), db);
		
		String geographicTaxonomy = d.getDocumentElement().getAttribute("geographic");
		if(geographicTaxonomy == null || geographicTaxonomy.trim().isEmpty())
			return false;
		return Boolean.parseBoolean(geographicTaxonomy.trim());
	}
}
