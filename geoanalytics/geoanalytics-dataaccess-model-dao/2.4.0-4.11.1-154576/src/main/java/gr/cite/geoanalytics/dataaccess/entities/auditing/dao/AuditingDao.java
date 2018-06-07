package gr.cite.geoanalytics.dataaccess.entities.auditing.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

public interface AuditingDao extends Dao<Auditing, UUID>
{
	public List<Auditing> findByType(Auditing.AuditingType type);
	public List<Auditing> findByTypeOrdered(Auditing.AuditingType type);
	public long countByType(Auditing.AuditingType type);
	public Auditing findByTypeAndCreator(Auditing.AuditingType type, Principal creator);
	public Auditing findByTypeAndUser(Auditing.AuditingType type, Principal u);
	public Auditing findMostRecentByType(Auditing.AuditingType type);
	public Auditing findLastDataUpdate();
}
