package gr.cite.geoanalytics.dataaccess.entities.user.dao;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.user.UserOld;

import java.util.List;
import java.util.UUID;


public interface UserDaoOld  extends Dao<UserOld, UUID> {
	/*public List<User> findActiveUsers();
	public List<String> listSystemNames();
	public List<String> listSystemNamesByCustomer(Tenant c);
	public List<String> listSystemNamesByCustomerOfActive(Tenant c);
	public List<String> listSystemNamesOfActive();
	public long countActiveUsers();
	public User findBySystemName(String name);
	public List<String> searchByName(List<String> names);
	public List<String> searchByNameAndCustomer(List<String> names, Tenant c);
	public User systemUser();*/
}
