package org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="prod")
public class Prod extends AbstractConfiguration {

	private final String TYPE = "prod";
	

	@Override
	public String getXMLModel ()
	{
		return super.getXML(TYPE);
	}


	@Override
	public String getType() {
		return TYPE;
	}

}
