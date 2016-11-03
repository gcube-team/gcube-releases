package gr.uoa.di.madgik.environment.cms.elements.alternative;

import gr.uoa.di.madgik.environment.cms.elements.DocumentProperty;
import gr.uoa.di.madgik.environment.cms.elements.document.Document;
import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public abstract class DocumentAlternative 
{
	public String id;
	public String name;
	private InputStream inboundContentStream;
	public byte[] content;
	public String contentLocator;
	//public String uri;
	public String schemaName;
	public String schemaUri;
	public String creationTime;
	public String lastUpdate;
	public Document document;
	public String language;
	public long length;
	public String mimeType;
	public String type;
	public Map<String, DocumentProperty> properties;
	
	public abstract InputStream ResolveContent() throws Exception;
	
	public InputStream GetContentStream() throws EnvironmentContentManagementSystemException {
		try
		{
			if(inboundContentStream != null) return inboundContentStream;
			
			if (contentLocator != null) return ResolveContent();
			else if (content != null)
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
