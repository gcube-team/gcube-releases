package gr.uoa.di.madgik.commons.channel.proxy;

import gr.uoa.di.madgik.commons.channel.proxy.local.LocalChannelLocator;
import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPChannelLocator;
import gr.uoa.di.madgik.commons.utils.URIUtils;
import java.net.URI;

/**
 * A factory for creating ChannelLocator objects.
 */
public class ChannelLocatorFactory
{
	
	/**
	 * Creates instances of {@link IChannelLocator} objects based on the provided serialization 
	 * 
	 * @param XMLserialization the xml serialization as created by {@link IChannelLocator#ToXML()}
	 * 
	 * @return the created channel locator
	 * @throws Exception The instantiation could not be performed
	 */
	public static IChannelLocator GetLocator(URI locator) throws Exception
	{
		try
		{
			IChannelLocator loc = null;
			IChannelLocator.LocatorType type = URIUtils.GetLocatorType(locator);
//			Document doc = XMLUtils.Deserialize(XMLserialization);
//			if (!XMLUtils.AttributeExists(doc.getDocumentElement(), "type"))
//			{
//				throw new Exception("Provided serialization not valid");
//			}
//			IChannelLocator.LocatorType type = IChannelLocator.LocatorType.valueOf(XMLUtils.GetAttribute(doc.getDocumentElement(), "type"));
			switch (type)
			{
				case Local:
				{
					loc = new LocalChannelLocator();
					break;
				}
				case TCP:
				{
					loc = new TCPChannelLocator();
					break;
				}
				default:
				{
					throw new Exception("Unrecognized channel locator type");
				}
			}
			loc.FromURI(locator);
			return loc;
		} catch (Exception ex)
		{
			throw new Exception("Could not instantiate locator based on provided serialization", ex);
		}
	}
}
