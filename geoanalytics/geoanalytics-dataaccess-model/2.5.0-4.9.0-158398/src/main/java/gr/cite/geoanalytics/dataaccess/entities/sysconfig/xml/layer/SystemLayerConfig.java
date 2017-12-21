package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlSeeAlso(LayerConfig.class)
public class SystemLayerConfig
{
	private List<LayerConfig> layerConfigs = new ArrayList<LayerConfig>();
	
	@XmlTransient private Map<String, LayerConfig> lookup = new HashMap<String, LayerConfig>();
	
	public List<LayerConfig> getLayerConfigs()
	{
		return layerConfigs;
	}

	@XmlElementWrapper(name="layers")
	public void setLayerConfigs(List<LayerConfig> layerConfigs)
	{
		this.layerConfigs = layerConfigs;
		
		lookup = new HashMap<String, LayerConfig>();
		for(LayerConfig cfg : layerConfigs)
		{
			lookup.put(cfg.getLayerId(), cfg);
		}
	}
	
	public LayerConfig getLayerConfig(String termId)
	{
		LayerConfig lcfg = lookup.get(termId);
		if(lcfg == null) return null;
		return new LayerConfig(lcfg);
	}
	
	public void removeLayerConfig(String termId)
	{
		LayerConfig tmp = new LayerConfig();
		tmp.setLayerId(termId);
		
		lookup.remove(termId);
		layerConfigs.remove(tmp);
	}
	
	public void setLayerConfig(LayerConfig cfg)
	{
		lookup.put(cfg.getLayerId(), cfg);
		int pos = layerConfigs.indexOf(cfg);
		if(pos != -1) layerConfigs.remove(pos);
		layerConfigs.add(pos != -1 ? pos : layerConfigs.size(), cfg);
	}

}
