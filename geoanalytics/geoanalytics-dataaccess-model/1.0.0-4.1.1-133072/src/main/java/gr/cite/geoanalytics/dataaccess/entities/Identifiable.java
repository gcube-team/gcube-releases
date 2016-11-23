package gr.cite.geoanalytics.dataaccess.entities;

import java.util.UUID;

public interface Identifiable
{
	public UUID getId();
	public void setId(UUID id);
}
