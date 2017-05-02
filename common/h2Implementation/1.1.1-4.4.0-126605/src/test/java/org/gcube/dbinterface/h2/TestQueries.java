package org.gcube.dbinterface.h2;

import java.util.Iterator;

import org.gcube.common.dbinterface.persistence.ObjectPersistency;
import org.gcube.common.dbinterface.pool.DBSession;


public class TestQueries {
	
	public static void main(String args[]) throws Exception{
		
		long start = System.currentTimeMillis();
		try {
				DBSession.initialize("org.gcube.dbinterface.h2", "sa", "", "mem:h2Test");
		} catch (Exception e) {
			e.printStackTrace();
				return;
		}
		
		System.out.println("the time spent is : "+(System.currentTimeMillis()-start));
		ObjectPersistency<ExtenderTest> extender= ObjectPersistency.get(ExtenderTest.class);
		
		ExtenderTest test= new ExtenderTest();
		test.setTopolino(1);
		test.setAltro("1");
		extender.insert(test);
		
		ExtenderTest test2= new ExtenderTest();
		test2.setTopolino(2);
		test2.setAltro("2");
		extender.insert(test2);
		
		ObjectPersistency<PersistenceTest> testPers= ObjectPersistency.get(PersistenceTest.class);
		
		PersistenceTest pt = new PersistenceTest();
		pt.setMinny("minny");
		pt.setPippo("pippo");
		pt.setPluto(2);
		pt.setTest(test);
		
		testPers.insert(pt);
		
		System.out.println("the time spent is : "+(System.currentTimeMillis()-start));
		
		Iterator<PersistenceTest> testit= testPers.getAll().iterator();
		while (testit.hasNext()){
			PersistenceTest ptt= testit.next();
			System.out.println(ptt.getMinny()+ptt.getPluto()+ptt.getTest().getAltro());
		}
		
		test.setAltro("cambiato");
		extender.update(test);
		testPers.update(pt);
		
		System.out.println("the time spent is : "+(System.currentTimeMillis()-start));
		
		testit= testPers.getAll().iterator();
		while (testit.hasNext()){
			PersistenceTest ptt= testit.next();
			System.out.println(ptt.getMinny()+ptt.getPluto()+ptt.getTest().getAltro());
		}
		
		System.out.println("the time spent is : "+(System.currentTimeMillis()-start));
		
		extender.deleteByKey(test.getTopolino());
		
		testit= testPers.getAll().iterator();
		while (testit.hasNext()){
			PersistenceTest ptt= testit.next();
			System.out.println(ptt.getMinny()+ptt.getPluto()+ptt.getTest().getAltro());
		}
		
		
		
		/*
		ObjectPersistency<ImportPersistenceItem> persistenceImport= new ObjectPersistency<ImportPersistenceItem>("testImportName", ImportPersistenceItem.class);
		persistenceImport.drop();
		
		persistenceImport= new ObjectPersistency<ImportPersistenceItem>("testImportName", ImportPersistenceItem.class);
		
		ImportPersistenceItem tmpObj = new ImportPersistenceItem(new Date(System.currentTimeMillis()), 5, "id1");
		
		persistenceImport.insert(tmpObj);
		
		
		Iterator<ImportPersistenceItem> it = persistenceImport.getAll();
		while (it.hasNext()){
			System.out.println(it.next().getPhase().toString());
		}
		
		tmpObj.setPhase(PHASE.P);
		System.out.println("----------------");
		persistenceImport.update(tmpObj);
		
		it = persistenceImport.getAll();
		while (it.hasNext()){
			System.out.println(it.next().getPhase().toString());
		}
		
		//DBSession session=DBSession.connect();
		/*
		ObjectPersistency<PersistenceTest> pers=  new ObjectPersistency<PersistenceTest>("persistenceTest", PersistenceTest.class);
		pers.drop();
		pers=  new ObjectPersistency<PersistenceTest>("persistenceTest", PersistenceTest.class);
		System.out.println("object created");
		pers.deleteByValue("pippo", "id1");
		
		PersistenceTest persTest= new PersistenceTest();
		persTest.setMinny("p");
		persTest.setPippo("id1");
		persTest.setPluto(5);
		pers.insert(persTest);
		
		PersistenceTest persTest2= new PersistenceTest();
		persTest2.setMinny("ioioio");
		persTest2.setPippo("id2");
		persTest2.setPluto(12);
		pers.insert(persTest2);
		
		Iterator<PersistenceTest> it=pers.getAll();
		while (it.hasNext()){
			System.out.println("obj retrieved");
			PersistenceTest p= it.next();
			System.out.println(p.getMinny());
			System.out.println(p.getPippo());
			System.out.println(p.getPluto());
		}
		//session.release();
		//System.out.println(result.s);
	*/
	}
	
}
