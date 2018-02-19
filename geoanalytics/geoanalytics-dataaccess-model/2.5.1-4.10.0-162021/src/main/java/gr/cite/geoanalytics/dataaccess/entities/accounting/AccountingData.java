package gr.cite.geoanalytics.dataaccess.entities.accounting;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "referenceData")
public class AccountingData
{
	private Accounting.AccountingType type;
	private UUID entityId;
	private String entityType;
	private long timestamp;
	
	public Accounting.AccountingType getType()
	{
		return type;
	}
	
	@XmlElement
	public void setType(Accounting.AccountingType type)
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
}
