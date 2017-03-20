package org.gcube.data.spd.model.service.types;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchRequest {

	private String pluginName;
    private List<SearchCondition> properties;
    private String resultType;
    private String word;
	        
    protected SearchRequest() {
		super();
	}
	    
    public SearchRequest(String pluginName,
			List<SearchCondition> properties, String resultType, String word) {
		super();
		this.pluginName = pluginName;
		this.properties = properties;
		this.resultType = resultType;
		this.word = word;
	}

	public String getPluginName() {
		return pluginName;
	}
	public List<SearchCondition> getProperties() {
		return properties;
	}
	public String getResultType() {
		return resultType;
	}
	public String getWord() {
		return word;
	}
    
    
}
