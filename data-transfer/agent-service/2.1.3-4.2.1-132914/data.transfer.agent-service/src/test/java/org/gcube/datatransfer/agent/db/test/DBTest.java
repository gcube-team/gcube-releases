package org.gcube.datatransfer.agent.db.test;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.FetchGroup;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.datastore.DataStoreCache;
import javax.jdo.listener.InstanceLifecycleListener;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.TypeMetadata;

import junit.framework.Assert;

import org.gcube.datatransfer.agent.impl.jdo.Transfer;
import org.junit.Before;
import org.junit.Test;

public class DBTest {

	private static PersistenceManagerFactory persistenceFactory;

	private static PersistenceManager persistenceManager;

	@Before
	public void setUp(){

		Properties prop = new Properties();
		URL properties = null;

		properties = Thread.currentThread().getContextClassLoader().getResource("db.properties");

		try {
			prop.load(properties.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

		persistenceFactory = JDOHelper.getPersistenceManagerFactory(prop);
		persistenceManager = persistenceFactory.getPersistenceManagerProxy();

	}

	@Test
	public void testStoreDB(){


		try
		{
			persistenceManager.currentTransaction().begin();
			Transfer tr = new Transfer();
			tr.setStatus("DONE");
			tr.setSubmitter("Andrea");
			persistenceManager.makePersistent(tr);
			persistenceManager.currentTransaction().commit();
		}catch (Exception e)
		{
			e.printStackTrace();
			Assert.assertNotNull(e);
		}

		finally
		{
			if (persistenceManager.currentTransaction().isActive()) 
				persistenceManager.currentTransaction().rollback();
			persistenceManager.close();
		}
	}

	@Test
	public void testGetAllObjects(){

		try
		{
			persistenceManager.currentTransaction().begin();
			Extent<?> ex = persistenceManager.getExtent(Transfer.class, true);
			Iterator<?> iter = ex.iterator();

			while (iter.hasNext())
			{
				Object o=iter.next();
				System.out.println(((Transfer)o).getId());
				Assert.assertNotNull(((Transfer)o).getId());
			}
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
			
			e.printStackTrace();
		}
	}

	
	@Test
	public void querBySubmitter(){

		try
		{
			persistenceManager.currentTransaction().begin();
			
			Query query = persistenceManager.newQuery(Transfer.class);
			query.setFilter("submitter == \"Andrea\"");

			List<Transfer> list = (List<Transfer>)query.execute();
			
			for(Transfer t : list){
				Assert.assertNotNull(t.getStatus());
				System.out.println(t.getStatus() );
			}
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void querById(){

		try
		{
			persistenceManager.currentTransaction().begin();
			
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,new Long(0));
			Assert.assertNotNull(t.getId());
			System.out.println(t.getId());
			
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateById(){

		try
		{
			persistenceManager.currentTransaction().begin();
			
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,new Long(0));
			Assert.assertNotNull(t.getId());
			System.out.println(t.getId());
			t.setStatus("FAILED");
			persistenceManager.makePersistent(t);
			
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}


}
