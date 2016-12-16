package org.gcube.spatial.data.geonetwork.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {

	public static String getElementContent(Element element){


		NodeList children = element.getChildNodes();
		String result = "";
		for (int i = 0; i < children.getLength(); i++)
		{
			if (children.item(i).getNodeType() == Node.TEXT_NODE || 
					children.item(i).getNodeType() == Node.CDATA_SECTION_NODE)
			{
				result += children.item(i).getNodeValue();
			}
			else if( children.item(i).getNodeType() == Node.COMMENT_NODE )
			{
				// Ignore comment nodes
			}
		}
		return result.trim();
	}
	
}
