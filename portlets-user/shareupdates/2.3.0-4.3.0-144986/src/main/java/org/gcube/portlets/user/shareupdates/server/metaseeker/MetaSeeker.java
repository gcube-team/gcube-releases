package org.gcube.portlets.user.shareupdates.server.metaseeker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;

import org.gcube.portlets.user.shareupdates.server.opengraph.OpenGraph;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Massimiliano Assante ISTI-CNR
 * @version 1.0 Nov 2012
 * 
 * This class parses the meta elements in the head of an html page and constructs a hashmap with the content attr value associated to the attr name
 * e.g.
 * <meta name="description" content="my description">
 * 
 * ex. #getContent("description") returns 'my description'
 *
 */
public class MetaSeeker {
	private static Logger _log = LoggerFactory.getLogger(MetaSeeker.class);
	
	private String pageUrl;

	private Hashtable<String, ArrayList<MetaElement>> metaAttributes;
	private String baseType;
	private boolean isImported; // determine if the object is a new incarnation or representation of a web page
	private boolean hasChanged; // track if object has been changed

	public final static String[] REQUIRED_META = new String[]{"title", "type", "image", "url" };

	public final static Hashtable<String, String[]> BASE_TYPES = new Hashtable<String, String[]>();
	static
	{
		BASE_TYPES.put("activity", new String[] {"activity", "sport"});
		BASE_TYPES.put("business", new String[] {"bar", "company", "cafe", "hotel", "restaurant"});
		BASE_TYPES.put("group", new String[] {"cause", "sports_league", "sports_team"});
		BASE_TYPES.put("organization", new String[] {"band", "government", "non_profit", "school", "university"});
		BASE_TYPES.put("person", new String[] {"actor", "athlete", "author", "director", "musician", "politician", "profile", "public_figure"});
		BASE_TYPES.put("place", new String[] {"city", "country", "landmark", "state_province"});
		BASE_TYPES.put("product", new String[] {"album", "book", "drink", "food", "game", "movie", "product", "song", "tv_show"});
		BASE_TYPES.put("website", new String[] {"blog", "website", "article"});
	}

	/**
	 * Create an open graph representation for generating your own Open Graph object
	 */
	public MetaSeeker()	{
		metaAttributes = new Hashtable<String, ArrayList<MetaElement>>();
		hasChanged = false;
		isImported = false;
	}

	/**
	 * Fetch the metas representation from a web site
	 * @param url The address to the web page to fetch the meta
	 * @throws java.io.IOException If a network error occurs, the HTML parser will throw an IO Exception
	 */
	public MetaSeeker(URLConnection connection, URL httpURL) throws java.io.IOException, Exception {
		this();
		isImported = true;
		Charset charset = OpenGraph.getConnectionCharset(connection);
		BufferedReader dis  = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
		String inputLine;
		StringBuffer headContents = new StringBuffer();

		// Loop through each line, looking for the closing head element
		while ((inputLine = dis.readLine()) != null) {
			if (inputLine.contains("</head>"))	{
				inputLine = inputLine.substring(0, inputLine.indexOf("</head>") + 7);
				inputLine = inputLine.concat("<body></body></html>");
				headContents.append(inputLine + "\r\n");
				break;
			}
			headContents.append(inputLine + "\r\n");
		}

		String headContentsStr = headContents.toString();
		HtmlCleaner cleaner = new HtmlCleaner();
		// parse the string HTML
		TagNode pageData = cleaner.clean(headContentsStr);
		// open only the meta tags
		TagNode[] metaData = pageData.getElementsByName("meta", true);
		_log.trace("meta length " + metaData.length);
		for (TagNode metaElement : metaData)	{
			String target = null;
			_log.trace("meta found ");
			if (metaElement.hasAttribute("name")) { 
				target = "name";
				setProperty(metaElement.getAttributeByName(target), metaElement.getAttributeByName("content"));
				_log.trace(metaElement.getAttributeByName("content"));
			}
		}
		pageUrl = httpURL.toExternalForm();
	}
	/**
	 * Get the basic type of the Open graph page as per the specification
	 * @return Base type as defined by specification, null otherwise
	 */
	public String getBaseType()
	{
		return baseType;
	}

	/**
	 * Get a value of a given Open Graph property
	 * @param property The Open graph property key
	 * @return Returns the value of the first property defined, null otherwise
	 */
	public String getContent(String property)
	{
		if (metaAttributes.containsKey(property) && metaAttributes.get(property).size() > 0)
			return metaAttributes.get(property).get(0).getContent();
		else
			return null;
	}

	/**
	 * Get all the defined properties of the Open Graph object
	 * @return An array of all currently defined properties
	 */
	public MetaElement[] getProperties()
	{
		ArrayList<MetaElement> allElements = new ArrayList<MetaElement>();
		for (ArrayList<MetaElement> collection : metaAttributes.values())
			allElements.addAll(collection);

		return (MetaElement[]) allElements.toArray(new MetaElement[allElements.size()]);
	}

	/**
	 * Get all the defined properties of the Open Graph object
	 * @param property The property to focus on
	 * @return An array of all currently defined properties
	 */
	public MetaElement[] getProperties(String property)
	{
		if (metaAttributes.containsKey(property))
		{
			ArrayList target = metaAttributes.get(property);
			return (MetaElement[]) target.toArray(new MetaElement[target.size()]);
		}
		else
			return null;
	}

	/**
	 * Get the original URL the Open Graph page was obtained from
	 * @return The address to the Open Graph object page
	 */
	public String getOriginalUrl()
	{
		return pageUrl;
	}




	/**
	 * Set the meta property to a specific value
	 * @param property The meta where XXXX is the property you wish to set
	 * @param content The value or contents of the property to be set
	 */
	public void setProperty(String property, String content)
	{

		MetaElement element = new MetaElement(property, content);
		if (!metaAttributes.containsKey(property))
			metaAttributes.put(property, new ArrayList<MetaElement>());

		metaAttributes.get(property).add(element);
	}

	/**
	 * Removed a defined property
	 * @param property The og:XXXX where XXXX is the property you wish to remove
	 */
	public void removeProperty(String property)
	{
		metaAttributes.remove(property);
	}

	/**
	 * Obtain the underlying HashTable
	 * @return The underlying structure as a Hashtable
	 */
	public Hashtable<String, ArrayList<MetaElement>> exposeTable() {
		return metaAttributes;
	}

	/**
	 * Test if the Open Graph object was initially a representation of a web page
	 * @return True if the object is from a web page, false otherwise
	 */
	public boolean isFromWeb()
	{
		return isImported;
	}

	/**
	 * Test if the object has been modified by setters/deleters.
	 * This is only relevant if this object initially represented a web page
	 * @return True True if the object has been modified, false otherwise
	 */
	public boolean hasChanged()
	{
		return hasChanged;
	}
}
