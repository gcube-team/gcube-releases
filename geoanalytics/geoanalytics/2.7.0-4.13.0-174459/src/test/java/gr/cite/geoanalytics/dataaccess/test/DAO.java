package gr.cite.geoanalytics.dataaccess.test;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDataDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:WEB-INF/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DAO {

	private PrincipalDao principalDao = null;
	private PrincipalDataDao PrincipalDataDao = null;
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public void setPrincipalDataDao(PrincipalDataDao principalDataDao) {
		PrincipalDataDao = principalDataDao;
	}
	
	@Test
	public void test() {
		principalDao.systemPrincipal();
		
		Principal principal = new Principal();
		PrincipalData principalData = new PrincipalData();
		
		principalData.setFullName("John Dao Smith");
		principalData.setInitials("JS");
		principalData.setEmail("j.smith@example.com");
		principalData.setCreationDate(Calendar.getInstance().getTime());
		principalData.setLastUpdate(Calendar.getInstance().getTime());
		principalData.setExpirationDate(new Date(2014 - 1900, 3, 29));
		principal.setName("John.Smith");
		principal.setCreator(principal);
		principal.setPrincipalData(principalData);
		
		Tenant tenant = new Tenant();
		tenant.setLastUpdate(Calendar.getInstance().getTime());
		tenant.setName("Customer one");
		tenant.setCode("A1");
		tenant.setEmail("cusOne@example.com");
		tenant.setCreationDate(Calendar.getInstance().getTime());
		tenant.setCreator(principal);

		PrincipalDataDao.create(principalData);
		principalDao.create(principal);
		principalDao.findActivePrincipals();
		
	}
}