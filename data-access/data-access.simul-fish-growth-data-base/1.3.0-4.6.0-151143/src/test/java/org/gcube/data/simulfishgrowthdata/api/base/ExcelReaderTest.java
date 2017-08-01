package org.gcube.data.simulfishgrowthdata.api.base;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.data.simulfishgrowthdata.util.ExcelReader;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Session;

import gr.i2s.fishgrowth.model.Site;
import junit.framework.TestCase;

public class ExcelReaderTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		String dbEndpointName = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";

		HibernateUtil.configGently(dbEndpointName, scope);
	}

	public void testImportSample() throws Exception {
		Long id = 495L;

		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("proper_sampling.xlsx").getFile());
			assertNotNull("Xls file exist", file);
			String filename = file.getAbsolutePath();

			ExcelReader.instance(ExcelReader.KIND_SAMPLE).importLocal(session, id, filename);

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testImportWeightCategories() throws Exception {
		Long id = 495L;

		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("proper_weight_categories.xlsx").getFile());
			assertNotNull("Xls file exist", file);
			String filename = file.getAbsolutePath();

			ExcelReader.instance(ExcelReader.KIND_LIMITS).importLocal(session, id, filename);

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

}
