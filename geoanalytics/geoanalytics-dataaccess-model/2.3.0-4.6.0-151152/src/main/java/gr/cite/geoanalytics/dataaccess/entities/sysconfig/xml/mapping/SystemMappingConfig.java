package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlSeeAlso(AttributeMappingConfig.class)
public class SystemMappingConfig
{
	private List<AttributeMappingConfig> mappingConfigs = new ArrayList<AttributeMappingConfig>();
	
	@XmlTransient private Map<String, Map<String, Map<String, AttributeMappingConfig>>> lookup = new HashMap<String, Map<String, Map<String, AttributeMappingConfig>>>(); //attribute name -> attribute value -> layer id ->config
	@XmlTransient private Map<String, Map<String, AttributeMappingConfig>> idLookup = new HashMap<String, Map<String, AttributeMappingConfig>>(); //taxonomy or taxonomy term id -> layer id -> config
	
	public List<AttributeMappingConfig> getMappingConfigs()
	{
		return mappingConfigs;
	}

	@XmlElementWrapper(name="mappings")
	public void setMappingConfigs(List<AttributeMappingConfig> mappingConfigs)
	{
		this.mappingConfigs = mappingConfigs;
		
		lookup = new HashMap<String, Map<String, Map<String, AttributeMappingConfig>>>();
		for(AttributeMappingConfig cfg : mappingConfigs)
		{
			if(lookup.get(cfg.getAttributeName()) == null)
				lookup.put(cfg.getAttributeName(), new HashMap<String, Map<String, AttributeMappingConfig>>());
			if(lookup.get(cfg.getAttributeName()).get(cfg.getAttributeValue() == null ? "" : cfg.getAttributeValue()) == null)
				lookup.get(cfg.getAttributeName()).put(cfg.getAttributeValue() == null ? "" : cfg.getAttributeValue(), new HashMap<String, AttributeMappingConfig>());
			lookup.get(cfg.getAttributeName()).get(cfg.getAttributeValue() == null ? "" : cfg.getAttributeValue()).put(cfg.getLayerTermId() != null ? cfg.getLayerTermId() : "", cfg);
		}
		
		idLookup = new HashMap<String, Map<String, AttributeMappingConfig>>();
		for(AttributeMappingConfig cfg : mappingConfigs)
		{
			if(idLookup.get(cfg.getTermId()) == null)
				idLookup.put(cfg.getTermId(), new HashMap<String, AttributeMappingConfig>());
			idLookup.get(cfg.getTermId()).put(cfg.getLayerTermId() != null ? cfg.getLayerTermId() : "", cfg);
		}
	}
	
	public List<AttributeMappingConfig> getMappingConfig(String attributeName)
	{
		List<AttributeMappingConfig> res = new ArrayList<AttributeMappingConfig>();
		Map<String, Map<String, AttributeMappingConfig>> mcfg = lookup.get(attributeName);
		if(mcfg == null) return null;
		for(Map<String, AttributeMappingConfig> c : mcfg.values())
		{
			for(AttributeMappingConfig cv : c.values())
				res.add(new AttributeMappingConfig(cv));
		}
		return res;
	}
	
	public List<AttributeMappingConfig> getMappingConfigForLayer(String attributeName, String layerTermId)
	{
		List<AttributeMappingConfig> res = new ArrayList<AttributeMappingConfig>();
		Map<String, Map<String, AttributeMappingConfig>> mcfg = lookup.get(attributeName);
		if(mcfg == null) return null;
		for(Map<String, AttributeMappingConfig> c : mcfg.values())
		{
			AttributeMappingConfig cfg = c.get(layerTermId);
			if(cfg != null)
				res.add(new AttributeMappingConfig(cfg));
		}
		return res;
	}
	
	public List<AttributeMappingConfig> getMappingConfigForId(String termId)
	{
		List<AttributeMappingConfig> mappings = new ArrayList<AttributeMappingConfig>();
		Map<String, AttributeMappingConfig> cfg = idLookup.get(termId);
		if(cfg == null) return mappings;
		for(AttributeMappingConfig cv : cfg.values())
			mappings.add(new AttributeMappingConfig(cv));
		return mappings;
	}
	
	public AttributeMappingConfig getMappingConfigForIdAndLayer(String termId, String layerTermId)
	{
		Map<String, AttributeMappingConfig> c = idLookup.get(termId);
		if(c == null) return null;
		AttributeMappingConfig cv = c.get(layerTermId);
		if(cv == null) return null;
		return new AttributeMappingConfig(cv);
	}
	
	public List<AttributeMappingConfig> getMappingConfigForLayer(String layerTermId)
	{
		List<AttributeMappingConfig> attrs = new ArrayList<AttributeMappingConfig>();
		for(Map<String, Map<String, AttributeMappingConfig>> cfg : lookup.values())
		{
			for(Map<String, AttributeMappingConfig> l : cfg.values())
			{
				AttributeMappingConfig mcfg = l.get(layerTermId);
				if(mcfg != null)
					attrs.add(new AttributeMappingConfig(mcfg));
			}
		}
		return attrs;
	}
	
	public List<AttributeMappingConfig> getMappingConfig(String attributeName, String attributeValue)
	{
		if(lookup.get(attributeName) == null) return null;
		Map<String, Map<String, AttributeMappingConfig>> val = lookup.get(attributeName);
		if(val == null) return null;
		Map<String, AttributeMappingConfig> cfgs = val.get(attributeValue != null ? attributeValue : "");
		if(cfgs == null) return null;
		List<AttributeMappingConfig> ret = new ArrayList<AttributeMappingConfig>();
		for(AttributeMappingConfig cv : cfgs.values())
			ret.add(new AttributeMappingConfig(cv));
		return ret;
	}
	
	public AttributeMappingConfig getMappingConfigForLayer(String attributeName, String attributeValue, String layerTermId)
	{
		if(lookup.get(attributeName) == null) return null;
		Map<String, Map<String, AttributeMappingConfig>> val = lookup.get(attributeName);
		if(val == null) return null;
		Map<String, AttributeMappingConfig> cfgs = val.get(attributeValue != null ? attributeValue : "");
		if(cfgs == null) return null;
		return new AttributeMappingConfig(cfgs.get(layerTermId));
	}
	
	public void removeMappingConfig(String attributeName)
	{
		Map<String, Map<String, AttributeMappingConfig>> mcfgs = lookup.get(attributeName);
		if(mcfgs == null) return;
		
		lookup.remove(attributeName);
		
		for(Map.Entry<String, Map<String, AttributeMappingConfig>> mcfg : mcfgs.entrySet())
		{
			for(Map.Entry<String, AttributeMappingConfig> cfg : mcfg.getValue().entrySet())
			{
				idLookup.remove(cfg.getValue().getTermId());
				mappingConfigs.remove(cfg.getValue());
			}
		}
	}
	
	public void removeMappingConfigForLayer(String attributeName, String layerTermId)
	{
		Map<String, Map<String, AttributeMappingConfig>> mcfgs = lookup.get(attributeName);
		if(mcfgs == null) return;
		
		List<String> toDelete = new ArrayList<String>();
		for(Map.Entry<String, Map<String, AttributeMappingConfig>> mcfg : mcfgs.entrySet())
		{
			mcfg.getValue().remove(layerTermId);
			if(mcfg.getValue().isEmpty())
				toDelete.add(mcfg.getKey());
			
			for(Map.Entry<String, AttributeMappingConfig> cfg : mcfg.getValue().entrySet())
			{
				idLookup.get(cfg.getValue().getTermId()).remove(layerTermId);
				if(idLookup.get(cfg.getValue().getTermId()).isEmpty())
					idLookup.remove(cfg.getValue().getTermId());
				mappingConfigs.remove(cfg.getValue());
			}
		}
		for(String key : toDelete)
		{
			mcfgs.remove(key);
			if(mcfgs.isEmpty())
				lookup.remove(attributeName);
		}
	}
	
	public void removeMappingConfigForLayer(String layerTermId)
	{
		List<String> toDeleteLookup = new ArrayList<String>();
		for(Map.Entry<String, Map<String, Map<String, AttributeMappingConfig>>> mcfgs : lookup.entrySet())
		{
			List<String> toDelete = new ArrayList<String>();
			for(Map.Entry<String, Map<String, AttributeMappingConfig>> mcfg : mcfgs.getValue().entrySet())
			{
				mcfg.getValue().remove(layerTermId);
				if(mcfg.getValue().isEmpty())
					toDelete.add(mcfg.getKey());
				
				for(Map.Entry<String, AttributeMappingConfig> cfg : mcfg.getValue().entrySet())
				{
					if(cfg.getKey().equals(layerTermId))
					{
						idLookup.get(cfg.getValue().getTermId()).remove(layerTermId);
						if(idLookup.get(cfg.getValue().getTermId()).isEmpty())
							idLookup.remove(cfg.getValue().getTermId());
						mappingConfigs.remove(cfg.getValue());
					}
				}
			}
			for(String key : toDelete)
			{
				mcfgs.getValue().remove(key);
				if(mcfgs.getValue().isEmpty())
					toDeleteLookup.add(mcfgs.getKey());
			}
		}
		for(String key : toDeleteLookup)
			lookup.remove(key);
	}
	
	public void removeMappingConfig(String attributeName, String attributeValue)
	{
		Map<String, Map<String, AttributeMappingConfig>> l = lookup.get(attributeName);
		if(l == null) return;
		Map<String, AttributeMappingConfig> tmp = l.get(attributeValue);
		if(tmp == null) return;
		l.remove(attributeValue);
		if(l.isEmpty()) 
			lookup.remove(attributeName);
		
		for(AttributeMappingConfig mcfg : tmp.values())
		{
			idLookup.remove(mcfg.getTermId());
			mappingConfigs.remove(mcfg);
		}
	}
	
	public void removeMappingConfigForLayer(String attributeName, String attributeValue, String layerTermId)
	{
		attributeValue = attributeValue != null ? attributeValue : "";
		Map<String, Map<String, AttributeMappingConfig>> l = lookup.get(attributeName);
		if(l == null) return;
		Map<String, AttributeMappingConfig> tmp = l.get(attributeValue);
		if(tmp == null) return;
		
		AttributeMappingConfig mcfg = tmp.get(layerTermId);
		if(mcfg == null) return;
		
		tmp.remove(layerTermId);
		if(tmp.isEmpty())
			l.remove(attributeValue);
		if(l.isEmpty())
			lookup.remove(attributeName);
		
		idLookup.get(mcfg.getTermId()).remove(layerTermId);
		if(idLookup.get(mcfg.getTermId()).isEmpty())
			idLookup.remove(mcfg.getTermId());
		
		mappingConfigs.remove(mcfg);
	}
	
	public void setMappingConfig(AttributeMappingConfig cfg)
	{
		if(lookup.get(cfg.getAttributeName()) == null)
			lookup.put(cfg.getAttributeName(), new HashMap<String, Map<String, AttributeMappingConfig>>());
		Map<String, Map<String, AttributeMappingConfig>> l = lookup.get(cfg.getAttributeName());
		if(l.get(cfg.getAttributeValue() != null ? cfg.getAttributeValue() : "") == null)
			l.put(cfg.getAttributeValue() != null ? cfg.getAttributeValue() : "", new HashMap<String, AttributeMappingConfig>());
		l.get(cfg.getAttributeValue() != null ? cfg.getAttributeValue() : "").put(cfg.getLayerTermId() != null ? cfg.getLayerTermId() : "", cfg);
		
		if(idLookup.get(cfg.getTermId()) == null)
			idLookup.put(cfg.getTermId(), new HashMap<String, AttributeMappingConfig>());
		idLookup.get(cfg.getTermId()).put(cfg.getLayerTermId() != null ? cfg.getLayerTermId() : "", cfg);
		
		int pos = mappingConfigs.indexOf(cfg);
		if(pos != -1) mappingConfigs.remove(pos);
		
		mappingConfigs.add(pos != -1 ? pos : mappingConfigs.size(), cfg);
	}

}
