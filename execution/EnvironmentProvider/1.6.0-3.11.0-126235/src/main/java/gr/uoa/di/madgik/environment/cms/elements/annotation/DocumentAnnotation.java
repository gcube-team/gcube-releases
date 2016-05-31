package gr.uoa.di.madgik.environment.cms.elements.annotation;

import gr.uoa.di.madgik.environment.cms.elements.DocumentProperty;
import gr.uoa.di.madgik.environment.cms.elements.document.Document;
import gr.uoa.di.madgik.environment.cms.elements.part.DocumentPart;
import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class DocumentAnnotation 
{
	public String id;
	public String name;
	public int order;
	public byte[] content;
	public String contentLocator;
	public String uri;
	private InputStream inboundContentStream;
	public String schemaName;
	public String schemaUri;
	public String creationTime;
	public String lastUpdate;
	public Document document;
	public String language;
	public long length;
	public String mimeType;
	public String type;
	public DocumentAnnotation previous;
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
