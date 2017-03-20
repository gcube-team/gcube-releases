package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfigData;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.sun.xml.txw2.annotation.XmlElement;

@XmlRootElement
@XmlSeeAlso(TaxonomyConfig.class)
public class SystemGlobalConfig implements SysConfigData
{
	private boolean systemOnline;
	private Set<String> taxonomyConfigTypes = null;
	private static Set<String> cachedTaxonomyConfigTypes = null;
	private List<TaxonomyConfig> taxonomyConfig;

	public boolean isSystemOnline()
	{
		return systemOnline;
	}
	
	@XmlElement
	public void setSystemOnline(boolean systemOnline)
	{
		this.systemOnline = systemOnline;
	}
	
	public List<TaxonomyConfig> getTaxonomyConfig()
	{
		if(this.taxonomyConfig == null) return null;
		List<TaxonomyConfig> ret = new ArrayList<TaxonomyConfig>();
		for(TaxonomyConfig tcfg : this.taxonomyConfig)
			ret.add(new TaxonomyConfig(tcfg));
		return ret;
	}
	
	@XmlElementWrapper(name = "taxonomyConfigs")
	public void setTaxonomyConfig(List<TaxonomyConfig> taxonomyConfig)
	{
		this.taxonomyConfig = taxonomyConfig;
	}
	
	public void setTaxonomyConfig(TaxonomyConfig taxonomyConfig) throws Exception
	{
		TaxonomyConfig existing = getByTaxonomyId(taxonomyConfig.getId());
		if(existing != null && !existing.getType().equals(taxonomyConfig.getType()))
			throw new Exception("Non-unique taxonomy mapping for " + taxonomyConfig.getType());
		List<TaxonomyConfig> toDelete = new ArrayList<TaxonomyConfig>();
		for(TaxonomyConfig cfg : this.taxonomyConfig)
		{
			if(cfg.getType().equals(taxonomyConfig.getType()))
			{
				toDelete.add(cfg);
				//break;
			}
		}
		
		this.taxonomyConfig.removeAll(toDelete);
		
		this.taxonomyConfig.add(taxonomyConfig);
	}
	
	public void removeTaxonomyConfig(TaxonomyConfig taxonomyConfig)
	{
		List<TaxonomyConfig> toDelete = new ArrayList<TaxonomyConfig>();
		for(TaxonomyConfig cfg : this.taxonomyConfig)
		{
			if(cfg.getType().equals(taxonomyConfig.getType()))
			{
				toDelete.add(cfg);
				//break;
			}
		}
		
		this.taxonomyConfig.removeAll(toDelete);
	}
	
	public List<String> getByTaxonomyTag(String t)
	{
		List<String> taxonomies = new ArrayList<String>();
		for(TaxonomyConfig c : taxonomyConfig)
		{
			if(c.getType().equals(t)) taxonomies.add(c.getId());
		}
		return taxonomies;
	}
	
	public TaxonomyConfig getByTaxonomyId(String id)
	{
		for(TaxonomyConfig c : taxonomyConfig)
		{
			if(c.getId().equals(id))
				return new TaxonomyConfig(c);
		}
		return null;
	}
	
	public Map<String, String> getTaxonomyMappings()
	{
		Map<String, String> mappings = new HashMap<String, String>();
		for(TaxonomyConfig c : taxonomyConfig)
		{
			if(c.getType().equals(TaxonomyConfig.Type.ALTGEOGRAPHYTAXONOMY))
				mappings.put(c.getReferrer(), c.getId());
		}
		return mappings;
	}
	
	public static Set<String> getCachedTaxonomyConfigTypes()
	{
		if(cachedTaxonomyConfigTypes == null) return null;
		return new HashSet<String>(cachedTaxonomyConfigTypes);
	}
	
	public Set<String> getTaxonomyConfigTypes()
	{
		if(cachedTaxonomyConfigTypes != null)
			return new HashSet<String>(cachedTaxonomyConfigTypes);
		
		Set<String> types = new HashSet<String>();
		for(Type t : Type.values())
			types.add(t.toString());
		for(TaxonomyConfig cfg : taxonomyConfig)
			types.add(cfg.getType());
		cachedTaxonomyConfigTypes = new HashSet<String>(types);
		return types;
	}
	
	public void addTaxonomyConfigType(String type)
	{
		if(!taxonomyConfigTypes.contains(type))
		{
			taxonomyConfigTypes.add(type);
			cachedTaxonomyConfigTypes.add(type);
		}
	}
}
