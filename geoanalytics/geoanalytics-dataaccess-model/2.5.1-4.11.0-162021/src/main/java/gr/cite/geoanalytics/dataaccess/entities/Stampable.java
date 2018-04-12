package gr.cite.geoanalytics.dataaccess.entities;

import java.util.Date;

public interface Stampable
{
	public Date getCreationDate();
	public void setCreationDate(Date creationDate);

	public Date getLastUpdate();
	public void setLastUpdate(Date lastUpdate);

}
