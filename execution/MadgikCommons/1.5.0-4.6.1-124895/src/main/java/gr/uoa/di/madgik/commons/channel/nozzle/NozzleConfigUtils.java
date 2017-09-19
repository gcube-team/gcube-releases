package gr.uoa.di.madgik.commons.channel.nozzle;

import gr.uoa.di.madgik.commons.channel.proxy.local.LocalNozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Element;

public class NozzleConfigUtils
{
	public static INozzleConfig GetNozzleConfig(Element element) throws Exception
	{
		if(!XMLUtils.AttributeExists(element, "type")) throw new Exception("Invalid serialization");
		INozzleConfig conf=null;
		switch(INozzleConfig.ConfigType.valueOf(XMLUtils.GetAttribute(element, "type")))
		{
			case Local:
			{
				conf=new LocalNozzleConfig();
				break;
			}
			case TCP:
			{
				conf=new TCPServerNozzleConfig();
				break;
			}
			default:
			{
				throw new Exception("Unrecognized nozzle config type");
			}
		}
		conf.FromXML(element);
		return conf;
	}
}
