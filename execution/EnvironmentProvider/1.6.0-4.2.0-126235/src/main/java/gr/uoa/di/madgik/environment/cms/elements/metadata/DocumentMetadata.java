package gr.uoa.di.madgik.environment.cms.elements.metadata;

import gr.uoa.di.madgik.environment.cms.elements.DocumentProperty;
import gr.uoa.di.madgik.environment.cms.elements.document.Document;
import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public abstract class DocumentMetadata 
{
	public String id;
	public String name;
	public int order;
	public byte[] content;
	public String contentLocator;
	//public String uri;
	private InputStream inboundContentStream;
	public String creationTime;
	public String lastUpdate;
	public Document document;
	public long length;
	public String language;
	public String mimeType;
	public String schemaName;
	public String schemaUri;
	public String type;
	public Map<String, DocumentProperty> properties;
	
	public abstract InputStream ResolveContent() throws Exception;
	
	public InputStream GetContentStream() throws EnvironmentContentManagementSystemException {
		try
		{
			if(inboundContentStream != null) return inboundContentStream;
			
			if (contentLocator != null)
			{
				if(contentLocator == null) return null;
				return new URI(contentLocator).toURL().openStream();
			}else if (content != null)
			{
				ByteArrayInputStream ins = new ByteArrayInputStream(content);
				return ins;
			}else
				return null;
		}catch(Exception e)
		{
			throw new EnvironmentContentManagementSystemException("Could not get content stream for document", e);
		}
	}
	
	public void SetInboundContentStream(InputStream is)
	{
		inboundContentStream = is;
	}
}
