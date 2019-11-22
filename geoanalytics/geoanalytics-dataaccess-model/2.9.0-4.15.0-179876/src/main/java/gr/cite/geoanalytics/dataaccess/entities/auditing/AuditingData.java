package gr.cite.geoanalytics.dataaccess.entities.auditing;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "auditingData")
public class AuditingData
{
	private Auditing.AuditingType type;
	private UUID entityId;
	private String entityType;
	private String data;
	private long timestamp;
	
	public Auditing.AuditingType getType()
	{
		return type;
	}
	
	@XmlElement
	public void setType(Auditing.AuditingType type)
	{
		this.type = type;
	}
	
	public UUID getEntityId()
	{
		return entityId;
	}
	
	@XmlElement
	public void setEntityId(UUID entityId)
	{
		this.entityId = entityId;
	}
	
	public String getEntityType()
	{
		return entityType;
	}
	
	@XmlElement(required = false)
	public void setEntityType(String entityType)
	{
		this.entityType = entityType;
	}
	
	
	public long getTimestamp()
	{
		return timestamp;
	}
	
	@XmlElement
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getData()
	{
		return data;
	}

	@XmlElement
	public void setData(String data)
	{
		this.data = data;
	}
	
	
}
