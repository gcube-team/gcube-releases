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
public class TestHavingEngine {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("Federico", "De Faveri", 31, Calendar.getInstance(), new ArrayList<Product>(Arrays.asList(new Product(Type.TAXON, 12), new Product(Type.OCCURRENCE, 13)))));
		persons.add(new Person("Valentina", "Marioli", 27, Calendar.getInstance(), new ArrayList<Product>(Arrays.asList(new Product(Type.OCCURRENCE, 13)))));
		persons.add(new Person("Roberto", "Cirillo", 31, Calendar.getInstance(), new ArrayList<Product>(Arrays.asList(new Product(Type.OCCURRENCE, 0)))));
		persons.add(new Person("Lucio", "Lelii", 33, Calendar.getInstance(), new ArrayList<Product>(Arrays.asList(new Product(Type.TAXON, 0)))));
		
		HavingStatementFactory factory = new HavingStatementFactoryEXL();
		HavingStatement<Person> personFilter = factory.compile("xpath(\"//product[type='TAXON' and counter>0]\")");
		
		for (Person person:persons) {
			System.out.println("Evaluating "+person);
			boolean accept = personFilter.accept(person);
			System.out.println(accept?"ACCEPTED":"NOT ACCEPTED");
			System.out.println();
		}

	}

}
