package org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="stage")
public class Stage extends AbstractConfiguration {

	private final String TYPE = "stage";
	

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
