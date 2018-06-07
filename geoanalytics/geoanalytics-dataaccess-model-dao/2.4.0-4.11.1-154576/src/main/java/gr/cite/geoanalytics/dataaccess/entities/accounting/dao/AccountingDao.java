package gr.cite.geoanalytics.dataaccess.entities.accounting.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;

public interface AccountingDao extends Dao<Accounting, UUID>
{
	public List<Accounting> findByType(Accounting.AccountingType type);
	public List<Accounting> findByTypeAndCreator(Accounting.AccountingType type, Principal creator);
	
	public List<Accounting> validAccounting();
	
	public List<Accounting> findByCustomer(Tenant c);
	public List<Accounting> findByUser(Principal principal);
	
	public List<Accounting> findValidByCustomer(Tenant c);
	public List<Accounting> findValidByUser(Principal principal);
	
	public float aggregateByCustomer(Tenant c, Date from, Date to);
	public float aggregateByUser(Principal principal, Date from, Date to);
}
