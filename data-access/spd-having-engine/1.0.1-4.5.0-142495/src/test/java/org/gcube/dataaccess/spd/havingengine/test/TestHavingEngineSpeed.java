/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.gcube.dataaccess.spd.havingengine.HavingStatement;
import org.gcube.dataaccess.spd.havingengine.HavingStatementFactory;
import org.gcube.dataaccess.spd.havingengine.exl.HavingStatementFactoryEXL;
import org.gcube.dataaccess.spd.havingengine.test.model.Person;
import org.gcube.dataaccess.spd.havingengine.test.model.Product;
import org.gcube.dataaccess.spd.havingengine.test.model.Product.Type;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestHavingEngineSpeed {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		List<Person> persons = new ArrayList<Person>();
		
		for (int i = 0; i<100000; i++)	persons.add(new Person("name"+i, "surname"+i, i, Calendar.getInstance(), new ArrayList<Product>(Arrays.asList(new Product(Type.OCCURRENCE, 11+i)))));
		
		long start = System.currentTimeMillis();
		HavingStatementFactory factory = new HavingStatementFactoryEXL();
		HavingStatement<Person> personFilter = factory.compile("age<31 || xpath(\"/Person/surname = 'Marioli'\")");
		long time = System.currentTimeMillis()-start;
		System.out.println("setup in "+time);
		
		start = System.currentTimeMillis();
		long accepted = 0;
		for (Person person:persons) {
			boolean accept = personFilter.accept(person);
			if (accept) accepted++;
		}
		time = System.currentTimeMillis()-start;
		System.out.println("accepted "+accepted+" in "+time+" over "+persons.size()+" elements");
	}

}
