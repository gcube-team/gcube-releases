package org.gcube.dataanalysis.ecoengine.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.AliasToEntityOrderedMapResultTransformer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class GenericTests {
	public static void main(String[] args) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
		String query = "select * from absence_data_baskingshark";
		Session ss = dbconnection.getCurrentSession();
		ss.beginTransaction();
		Query qr = null;
		qr = ss.createSQLQuery(query);
		qr.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);
		List<Object> result = qr.list();
		ss.getTransaction().commit();
		//   List<Map<String, Object>> aliasToValueMapList = qr.list();
		//   // to get column names
		//   Object[] ColNames = (Object[]) aliasToValueMapList.get(0).keySet()
//		     .toArray();
		for (Object row : result){
//			System.out.println((LinkedHashMap<String, Object>) row);
			ArrayList<String> listKeys = new ArrayList<String>(((LinkedHashMap<String, Object>) row).keySet());
			ArrayList<Object> listvalues = new ArrayList<Object>(((LinkedHashMap<String, Object>) row).values());
			System.out.println(listvalues);
		}
	}
}
