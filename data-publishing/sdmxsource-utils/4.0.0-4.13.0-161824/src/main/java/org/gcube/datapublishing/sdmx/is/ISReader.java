package org.gcube.datapublishing.sdmx.is;

import java.util.List;

import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ISReader <T> {


	
	private Logger logger;
	private SimpleQuery query;
	
	public ISReader() {
		this.logger = LoggerFactory.getLogger(ISReader.class);
	}
	
	protected void newQuery (Class<?> queryClass)
	{
		this.query = ICFactory.queryFor(queryClass);
	}
	
	protected void addCondition (String element, String name)
	{
		this.logger.debug("Adding condition "+element + " name "+name);
		String categoryCondition = generateQuery(element,name);
		this.logger.debug("Produced condition: "+categoryCondition );
		this.query.addCondition(categoryCondition);
	}
	
	protected void setResults (String results)
	{
		this.logger.debug("Setting results "+results);
		this.query.setResult(results);
	}
	
	protected List<T> submit (Class<T> resultType)
	{
		DiscoveryClient<T>  client = ICFactory.clientFor(resultType);
		List<T> response = client.submit(query);
		return response;
	}
	


	
	
	private String generateQuery(String element, String name)
	{	
		StringBuilder builder = new StringBuilder();
		builder.append("$resource/Profile/");
		builder.append(element).append("/text() eq '");
		builder.append(name);
		builder.append("'");
		return builder.toString();
		
	}



}
