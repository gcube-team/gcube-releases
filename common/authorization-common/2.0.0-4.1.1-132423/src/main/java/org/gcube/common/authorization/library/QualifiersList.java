package org.gcube.common.authorization.library;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.common.authorization.library.utils.MapAdapter;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class QualifiersList {

	
	@XmlJavaTypeAdapter(MapAdapter.class)
	Map<String, String> qualifierTokenMap= new HashMap<String, String>();

	
	@SuppressWarnings("unused")
	private QualifiersList(){}
	
	public QualifiersList(Map<String, String> qualifierTokenMap) {
		this.qualifierTokenMap = qualifierTokenMap;
	}

	public Map<String, String> getQualifiers() {
		return qualifierTokenMap;
	}

	@Override
	public String toString() {
		return "QualifiersList [qualifierTokenMap=" + qualifierTokenMap + "]";
	}

	
	
}
