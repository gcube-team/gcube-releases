package org.gcube.data.oai.tmplugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.gcube.data.oai.tmplugin.utils.Utils;
import org.gcube.data.tmf.api.Environment;
import org.gcube.data.tmf.api.PluginLifecycle;
import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.api.SourceBinder;

public class OAIPlugin implements PluginLifecycle{

	protected static final String REQUEST1 = "<wrapRepositoryRequest>" +
			"<name>collection name</name>" +
			"<url required=\"true\">http://ijict.org/index.php/ijoat/oai</url>" +
			"<description>collection description</description>" +
			"<metadataFormat required=\"true\">oai_dc</metadataFormat>" +
			"<sets repeatable=\"true\">ijoat:TN</sets>" +
			"<titleXPath>//*[local-name()='title']</titleXPath>" +			
			"<contentXPath>//*[local-name()='identifier' and contains(.,'://')]</contentXPath>" +
			"<alternativesXPath repeatable=\"true\">//*[local-name()='relation' and contains(.,'://')]</alternativesXPath>" +
			"</wrapRepositoryRequest>";

	protected static final String REQUEST2 = "<wrapSetsRequest>" +
			"<name>collection name</name>" +
			"<url required=\"true\">http://ijict.org/index.php/ijoat/oai</url>" +
			"<description>collection description</description>" +
			"<metadataFormat required=\"true\">oai_dc</metadataFormat>" +
			"<sets repeatable=\"true\">ijoat:TN</sets>" +					
			"<titleXPath>//*[local-name()='title']</titleXPath>" +			
			"<contentXPath>//*[local-name()='identifier' and contains(.,'://')]</contentXPath>" +
			"<alternativesXPath repeatable=\"true\">//*[local-name()='relation' and contains(.,'://')]</alternativesXPath>" +	
			"</wrapSetsRequest>";
	
	
	@Override
	public String name() {
		return "oai-tm-plugin";
	}

	@Override
	public String description() {
		return "A Tree Manager plugin for OAI Data Sources";
	}

	@Override
	public List<Property> properties() {
		return Arrays.asList(
				new Property("An example request WrapRepositoryRequest", "requestSample", REQUEST1), 
				new Property("An example request WrapSetsRequest", "requestSample", REQUEST2));
	}

	@Override
	public SourceBinder binder() {
		return new OAIBinder();
	}

	@Override
	public List<String> requestSchemas() {
		
		List<String> schemas = new ArrayList<String>();
		
		String sampleDataSchema1 = Utils.toSchema(WrapRepositoryRequest.class);		
		String sampleDataSchema2 = Utils.toSchema(WrapSetsRequest.class);
		
		schemas.add(sampleDataSchema1);
		schemas.add(sampleDataSchema2);
		
		return schemas;
	}

	@Override
	public boolean isAnchored() {
		return false;
	}

	@Override
	public void start(Environment environment) throws Exception {
	}

	@Override
	public void stop(Environment environment) {

	}


}
