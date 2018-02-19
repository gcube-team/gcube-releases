package gr.cite.regional.data.collection.dataaccess.test;

import java.util.List;

import javax.persistence.TypedQuery;

import gr.cite.regional.data.collection.dataaccess.entities.Annotation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class HibernateTest {

	@Test
	public void queryAnnotation() {
		SessionFactory factory = HibernateUtils.getSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			// All the action with DB via Hibernate
			// must be located in one transaction.
			// Start Transaction.            
			session.getTransaction().begin();

			// Create an HQL statement, query the object.
			// Equivalent to the SQL statement:
			// Select e.* from EMPLOYEE e order by e.EMP_NAME, e.EMP_NO
			String sql = "from " + Annotation.class.getName();

			// Create Query object.
			TypedQuery<Annotation> query = session.createQuery(sql);

			// Execute query.
			List<Annotation> annotations = query.getResultList();

			for (Annotation a : annotations) {
				System.out.println("fjeoi");
			}

			// Commit data.
			session.getTransaction().commit();
			
			System.out.println("**********************************************Done**********************************************");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			// Rollback in case of an error occurred.
			session.getTransaction().rollback();
		}
	}

}