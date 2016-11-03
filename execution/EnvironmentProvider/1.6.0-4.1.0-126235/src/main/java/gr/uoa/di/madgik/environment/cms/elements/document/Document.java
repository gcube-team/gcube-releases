package gr.uoa.di.madgik.environment.cms.elements.document;

import gr.uoa.di.madgik.environment.cms.elements.DocumentProperty;
import gr.uoa.di.madgik.environment.cms.elements.alternative.DocumentAlternative;
import gr.uoa.di.madgik.environment.cms.elements.annotation.DocumentAnnotation;
import gr.uoa.di.madgik.environment.cms.elements.metadata.DocumentMetadata;
import gr.uoa.di.madgik.environment.cms.elements.part.DocumentPart;
import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public abstract class Document 
{
	public String id;
	public String name;
	//public String uri;
	public String collectionId;
	
	public String language;
	public String mimeType;
	public String schemaName;
	public String schemaURI;
	public String type;

	public byte[] content;
	public String contentLocator;
	private InputStream inboundContentStream;
	
	public List<DocumentPart> parts = null;
	public List<DocumentAlternative> alternatives = null;
	public List<DocumentMetadata> metadata = null;
	public List<DocumentAnnotation> annotations = null;
	public List<DocumentProperty> properties = null;
	
	public abstract InputStream ResolveContent() throws Exception;
	
	public InputStream GetContentStream() throws EnvironmentContentManagementSystemException 
	{
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
