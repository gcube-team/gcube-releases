package org.gcube.application.framework.core.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Result")
public class CardinalitiesResultBean {

	@XmlElement(name = "CollectionID")
	String collectionID;

	@XmlElement(name = "Cardinality")
	String cardinality;

}
