package org.gcube.data.tml.proxies;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.data.tml.Constants;

/**
 * Builds queries for Tree Manager services that give read or write access to data sources.
 * 
 * @author Fabio Simeoni
 *
 */
public class SourceQueryBuilder {

	private final StatefulQuery query;
	
	private String id;
	private String name;
	private List<QName> types = new ArrayList<QName>();
	
	public SourceQueryBuilder(StatefulQuery query) {
		query.addNamespace("tm",URI.create(Constants.namespace));
		this.query=query;
	}
	/**
	 * Sets a source identifier on the query.
	 * @param id the identifier
	 * @return this builder
	 */
	public SourceQueryBuilder withId(String id) {
		this.id=id;
		return this;
	}
	
	/**
	 * Sets a source name on the query.
	 * @param name the name
	 * @return this builder
	 */
	public SourceQueryBuilder withName(String name) {
		this.name=name;
		return this;
	}
	
	/**
	 * Sets a source type on the query.
	 * @param name the type
	 * @return this builder
	 */
	public SourceQueryBuilder withType(QName type) {
		this.types.add(type);
		return this;
	}
	
	/**
	 * Returns the query.
	 * @return the query.
	 */
	public StatefulQuery build() {
	
		if (name!=null)
			query.addCondition("$resource/Data/tm:Name/text() eq '"+name+"'");
		
		if (id!=null)
			query.addCondition("$resource/Data/tm:SourceId/text() eq '"+id+"'");
		
		if (!types.isEmpty()) {
			query.addVariable("$type", "$resource/Data/tm:Type");
			for (QName type: types)
				query.addCondition("resolve-QName($type/text(),$type) eq QName('"+type.getNamespaceURI()+"','"+type.getLocalPart()+"')");
		}
		
		return query;
		
		
	}

}
