package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.sun.xml.txw2.annotation.XmlElement;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfigData;

@XmlRootElement
@XmlSeeAlso({Theme.class, LayerStyle.class})
public class SystemPresentationConfig implements SysConfigData
{
	@XmlTransient public static final String DEFAULT_THEME = "default";
	@XmlTransient public static final String DEFAULT_STYLE = "line";
	
	private List<Theme> themes = new ArrayList<Theme>();
	private List<LayerStyle> layerStyles = new ArrayList<LayerStyle>();
	
	@XmlTransient private Map<String, Theme> themeLookup = new HashMap<String, Theme>();
	@XmlTransient private Map<String, LayerStyle> styleLookup = new HashMap<String, LayerStyle>();
	
	public List<Theme> getThemes() 
	{
		return themes;
	}
	
	@XmlElement
	public void setThemes(List<Theme> themes) throws Exception 
	{
		this.themes = themes;
		
		themeLookup = new HashMap<String, Theme>();
		for(Theme t : themes)
		{
			if(t.getTitle() == null) throw new Exception("No title");
			themeLookup.put(t.getTitle(), t);
		}
	}
	
	public Theme getTheme(String name) throws Exception
	{
		if(name == null) throw new Exception("No theme title");
		return themeLookup.get(name);
	}
	
	public void addTheme(Theme theme) throws Exception
	{
		if(theme.getTitle() == null) throw new Exception("No title");
		if(theme.getGeoStyle() == null)
		{
			GeoStyle gs = new GeoStyle();
			gs.setTermStyles(new ArrayList<TermStyle>());
			theme.setGeoStyle(gs);
		}
		if(themeLookup.containsKey(theme.getTitle())) removeTheme(theme.getTitle());
		themeLookup.put(theme.getTitle(), theme);
		themes.add(theme);
		
	}
	
	public void setupDefaultTheme() throws Exception
	{
		if(themeLookup.get(DEFAULT_THEME) != null) throw new Exception("Default theme already exists");
		Theme def = new Theme();
		def.setTitle(DEFAULT_THEME);
		GeoStyle gs = new GeoStyle();
		gs.setTermStyles(new ArrayList<TermStyle>());
		def.setGeoStyle(gs);
		themes.add(def);
	}
	
	public void removeTheme(String name) throws Exception
	{
		if(name == null) throw new Exception("No title");
		Theme t = themeLookup.get(name);
		if(t != null)
			themeLookup.remove(t);
		themes.remove(t);
	}
	
	public List<LayerStyle> getLayerStyles() 
	{
		return layerStyles;
	}
	
	@XmlElement
	public void setLayerStyles(List<LayerStyle> layerStyles) throws Exception 
	{
		this.layerStyles = layerStyles;
		
		styleLookup = new HashMap<String, LayerStyle>();
		for(LayerStyle s : layerStyles)
		{
			if(s.getName() == null) throw new Exception("No style name");
			styleLookup.put(s.getName(), s);
		}
	}
	
	public LayerStyle getLayerStyle(String name) throws Exception
	{
		if(name == null) throw new Exception("No style name");
		return styleLookup.get(name);
	}
	
	public void removeLayerStyle(String name) throws Exception
	{
		if(name == null) throw new Exception("No style name provided");
		for(Theme t : themes)
		{
			for(TermStyle ts : t.getGeoStyle().getTermStyles())
			{
				if(ts.getStyle().equals(name))
					throw new Exception("Cannot delete referenced layer style. Reference by: " + ts.getId());
			}
		}
		LayerStyle ls = styleLookup.get(name);
		if(ls != null)
			styleLookup.remove(ls);
		layerStyles.remove(ls);
	}
	
	public void addLayerStyle(LayerStyle style) throws Exception
	{
		if(style == null) throw new Exception("No style provided");
		if(style.getName() == null) throw new Exception("No style name provided");
		if(styleLookup.containsKey(style.getName())) removeLayerStyle(style.getName());
		styleLookup.put(style.getName(), style);
		layerStyles.add(style);
	}
	
	public void updateLayerStyle(LayerStyle style) throws Exception
	{
		if(style == null) throw new Exception("No style provided");
		if(style.getName() == null) throw new Exception("No style name provided");
		if(!styleLookup.containsKey(style.getName())) throw new Exception("Style not found");
		styleLookup.put(style.getName(), style);
		layerStyles.remove(style);
		layerStyles.add(style);
	}
	
	public String getTermStyle(String theme, String termId) throws Exception
	{
		if(theme == null) throw new Exception("No theme title");
		if(termId == null) throw new Exception("No term id");
		
		Theme t = themeLookup.get(theme);
		if(t == null) return null;
		
		TermStyle ts = t.getGeoStyle().getTermStyle(termId);
		if(ts == null) return null;
		
		return ts.getStyle();
	}
	
	public String getTermStyle(String termId) throws Exception
	{
		return getTermStyle(DEFAULT_THEME, termId);
	}
	
	public void assignTermStyle(String theme, String termId, String styleRef) throws Exception
	{
		if(theme == null) throw new Exception("No theme title");
		if(termId == null) throw new Exception("No term id");
		if(styleRef == null) throw new Exception("No style name");
		
		if(styleLookup.get(styleRef) == null) throw new Exception("Invalid style reference: " + styleRef);
		
		if(!themeLookup.containsKey(theme))
		{
			Theme t = new Theme();
			t.setTitle(theme);
			GeoStyle g = new GeoStyle();
			t.setGeoStyle(g);
			themeLookup.put(t.getTitle(), t);
		}
		
		Theme t = themeLookup.get(theme);
		TermStyle ts = new TermStyle();
		ts.setId(termId);
		ts.setStyle(styleRef);
		t.getGeoStyle().addTermStyle(ts);
		
	}
	
	public void assignTermStyle(String termId, String styleRef) throws Exception
	{
		assignTermStyle(DEFAULT_THEME, termId, styleRef);
	}
	
	public void removeTermStyle(String theme, String termId) throws Exception
	{
		if(theme == null) throw new Exception("No theme title");
		if(termId == null) throw new Exception("No term id");
		Theme t = themeLookup.get(theme);
		if(t == null) return;
		t.getGeoStyle().removeTermStyle(termId);
	}
	
	public void removeTermStyles(String termId) throws Exception
	{
		for(Theme theme : themes)
			removeTermStyle(theme.getTitle(), termId);
	}
	
	public void removeTermStyle(String termId) throws Exception
	{
		if(termId == null) throw new Exception("No term id");
		removeTermStyle(DEFAULT_THEME, termId);
	}
}
