package org.gcube.data.analysis.tabulardata.model.resources;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SDMXResource extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8695994802487301542L;

	private final String WS_ROOT="ws/rest/";
	
	public enum TYPE {
		DATA_STRUCTURE ("DataStructure"),
		CODE_LIST ("CodeList");
		
		private String type;
		
		TYPE (String type)
		{
			this.type = type;
		}
		
		@Override
		public String toString ()
		{
			return this.type;
		}
		
		public String toStringUrl ()
		{
			return this.type.toLowerCase();
		}
	}
	

	private String 	version,
					agency,
					primaryMeasure,
					name;
	
	private URL registryURL;
	
	private TYPE type;

	public  SDMXResource ()
	{

	}

	
	public SDMXResource (URL registryURL,String name, String version, String agency, String primaryMeasure, TYPE type )
	{
		this.registryURL = registryURL;
		this.name = name;
		this.version = version;
		this.agency = agency;
		this.primaryMeasure = primaryMeasure;
		this.type = type;
	}

	public SDMXResource (URL registryURL,String name, String version, String agency, TYPE type )
	{
		this (registryURL,name, version, agency,null, type);
	}

	@Override
	public String getStringValue() 	
	{
		String registryUrlString = this.registryURL.toString();
		StringBuilder resourceURL = new StringBuilder(registryUrlString);
		
		if (!registryUrlString.endsWith("/")) resourceURL.append("/");
		
		resourceURL.append(WS_ROOT).append(this.type.toStringUrl()).append("/").append(this.agency).append("/").append(this.name).append("/").append(this.version);

		
		return resourceURL.toString();
	}
	
	public URL getResourceURL ()
	{
		try {
			return new URL(getStringValue());
		} catch (MalformedURLException e) {
			return null;
		}
	}


	public String getVersion() {
		return version;
	}

	public String getAgency() {
		return agency;
	}

	public String getPrimaryMeasure() {
		return primaryMeasure;
	}

	public String getName() {
		return name;
	}

	public TYPE getType() {
		return type;
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return this.getClass();
	}
	
	public URL getRegistryURL ()
	{
		return this.registryURL;
	}

	@Override
	public boolean equals(Object sdmxResource) 
	{
		if (sdmxResource instanceof SDMXResource)
		{
			SDMXResource sdmx = (SDMXResource) sdmxResource;
			
			return (checkEqual(this.registryURL, sdmx.getRegistryURL()) &&
					checkEqual(this.name, sdmx.getName()) &&
					checkEqual(this.agency, sdmx.getAgency()) &&
					checkEqual(this.primaryMeasure, sdmx.getPrimaryMeasure()) &&
					checkEqual(this.type, sdmx.getType()) &&
					checkEqual(this.version, sdmx.getVersion()));
		}
		else return super.equals(sdmxResource);
		

		
	}
	
	private boolean checkEqual (Object object1, Object object2)
	{
		return ((object1 == null && object2 == null) || (object1!= null && (object1.equals(object2))));
	}
	
	@Override
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder ("SDMX Resource [");
		toStringBuilder.append(this.type).append(", ").append(this.name).append(", ").append(this.version).append(", agency ").append(this.agency);
		
		if (primaryMeasure != null) toStringBuilder.append(", primary measure ").append(primaryMeasure);
		
		toStringBuilder.append(", Registry URL ").append(this.registryURL).append("]");
		return toStringBuilder.toString();
	}


	
}
