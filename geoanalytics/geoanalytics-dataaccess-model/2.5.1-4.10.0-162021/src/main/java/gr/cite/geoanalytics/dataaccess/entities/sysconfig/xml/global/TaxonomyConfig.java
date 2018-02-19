package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

public class TaxonomyConfig
{
	public enum Type 
	{
		LAYERTAXONOMY, 
		GEOGRAPHYTAXONOMY, 
		ALTGEOGRAPHYTAXONOMY, 
		LANDUSETAXONOMY,
		POITAXONOMΥ,
		SITETAXONOMY,
		
		PLANNINGTAXONOMY,
		LEGALTAXONOMY,
		EVALUATIONTAXONOMY,
		ACTIONSTAXONOMY,
		PROJECTINFOCATEGORYTAXONOMY,
		
		BLOCKTAXONOMY,
		SDTAXONOMY,
		MSDTAXONOMY,
		GPSTAXONOMY,
		FEKGPSTAXONOMY,
		PETAXONOMY,
		
		OWNERSHIPTAXONOMY,
		OWNERSHIPRIGHTSTAXONOMY,
		BENEFICIARYTAXONOMY,
		OWNERSHIPTITLESTAXONOMY,
		NOTARYTAXONOMY,
		LEASINGTAXONOMY,
		
		
		SITECATEGORYTAXONOMY,
		ADDRESSTAXONOMY,
		LOCATIONTAXONOMY,
		AREATAXONOMY,
		SAOTAXONOMY,
		SETAXONOMY
	};

	private String id;
	private String type;
	private String referrer;
	
	public TaxonomyConfig() { }
	
	public TaxonomyConfig(TaxonomyConfig other)
	{
		this.id = other.id;
		this.type = other.type;
		this.referrer = other.referrer;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	@XmlElement
	public void setType(String type) throws Exception
	{
		Set<String> cachedTypes = SystemGlobalConfig.getCachedTaxonomyConfigTypes();
		if(cachedTypes != null && !cachedTypes.contains(type))
			throw new Exception("Invalid type");
		this.type = type;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	@XmlElement
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getReferrer()
	{
		return this.referrer;
	}
	
	@XmlElement(required = false)
	public void setReferrer(String referrer)
	{
		this.referrer = referrer;
	}
	
}

