package gr.uoa.di.madgik.commons.infra;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gr.uoa.di.madgik.commons.utils.XMLUtils;

public class HostingNodeTopology 
{
	
	private static Map<String, Node> nodes = new HashMap<String, Node>();
	private static Logger logger = null;
	
	static
	{
		try
		{
			logger = Logger.getLogger(HostingNodeTopology.class.getName());
			URL resourceNT = Thread.currentThread().getContextClassLoader().getResource("nodeTopology.xml");
			if(resourceNT != null)
			{
				final char[] buffer = new char[1024];
				StringBuilder nt = new StringBuilder();
				Reader in = new InputStreamReader(resourceNT.openStream(), "UTF-8");
				int read = 0;
				try
				{
					do
					{
						read = in.read(buffer, 0, buffer.length);
						if(read > 0) nt.append(buffer, 0 , read);
					}while(read >= 0);
				}
				finally
				{
					in.close();
				}
				HostingNodeTopology.fromXML(nt.toString());
				
			}
		}catch(Exception e)
		{
			logger.log(Level.WARNING, "Error while reading network topology configuration", e);
		}
		
	}
	
	private static class Node
	{
		public final String domain;
		public final String rack;
		public final String hostname;
		
		public Node(String domain, String rack, String hostname)
		{
			this.domain = domain;
			this.rack = rack;
			this.hostname = hostname;
		}
	}
	
	public static void fromXML(String xml) throws Exception
	{
		Document d = XMLUtils.Deserialize(xml);

		List<Element> domains = XMLUtils.GetChildElementsWithName(d.getDocumentElement(), "domain");
		for(Element domain : domains)
		{
			String domainName = XMLUtils.GetAttribute(domain, "name");
			String rackId = null;
			List<Element> racks = XMLUtils.GetChildElementsWithName(domain, "rack");
			if(racks.size() > 0)
			{
				for(Element rack : racks)
				{
					rackId = UUID.randomUUID().toString();
					List<Element> nodeElements = XMLUtils.GetChildElementsWithName(rack, "node");
					for(Element node : nodeElements)
					{
						String hostName = XMLUtils.GetChildText(node);
						nodes.put(hostName, new Node(domainName, rackId, hostName));
					}
				}
			}
			else
			{
				List<Element> nodeElements = XMLUtils.GetChildElementsWithName(domain, "node");
				for(Element node : nodeElements)
				{
					String hostName = XMLUtils.GetChildText(node);
					nodes.put(hostName, new Node(domainName, rackId, hostName));
				}
			}
		}
	}
	
	public static boolean containsNode(String hostname)
	{
		return nodes.containsKey(hostname);
	}
	
	public static boolean sameNode(String hostName1, String hostName2) throws Exception
	{
		if(!containsNode(hostName1) || !containsNode(hostName2)) throw new Exception("No information on given hosts (" + hostName1 + "," + hostName2 + ")");
		return nodes.get(hostName1).hostname.equals(nodes.get(hostName2).hostname);
	}
	
	public static boolean sameRack(String hostName1, String hostName2) throws Exception
	{
		if(!containsNode(hostName1) || !containsNode(hostName2)) throw new Exception("No information on given hosts (" + hostName1 + "," + hostName2 + ")");
		return nodes.get(hostName1).rack.equals(nodes.get(hostName2).rack);
	}
	
	public static boolean sameDomain(String hostName1, String hostName2) throws Exception
	{
		if(!containsNode(hostName1) || !containsNode(hostName2)) throw new Exception("No information on given hosts (" + hostName1 + "," + hostName2 + ")");
		return nodes.get(hostName1).domain.equals(nodes.get(hostName2).domain);
	}
}
