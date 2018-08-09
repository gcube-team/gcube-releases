/**
 * 
 */
package org.gcube.data.neo;

import static org.gcube.data.TestFixture.*;
import static org.gcube.data.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.trees.generators.TemplateFactory.*;
import static org.gcube.data.trees.io.XMLBindings.*;
import static org.gcube.data.trees.patterns.Patterns.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Iterator;

import org.gcube.data.tr.Store;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TreeTemplate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class ReadTest {

	static Store store;
	
	@BeforeClass
	public static void start() throws Exception {
		
		//we want them identical or bad luck may fail some tests (e.g. trees with no metadata)
		TreeTemplate template = aTreeLike(TEST_TREE,1.0); 
		
		store = newTestStoreWith(template.generate(10));
	}
	
	@AfterClass
	public static void stop() {
		if (store!=null)
			store.delete();
	}
	
	@Test
	public void roundTrip() throws Exception {
		
		long cardinality = store.cardinality();
		
		Tree added = store.add(TEST_TREE);
		
		assertNotNull(added);
		
		String id = added.id();
		
		assertNotNull(id);
		
		assertEquals(cardinality+1,store.cardinality());
		
		Tree retrieved = store.get(id,tree());
		
		assertEquals(id, retrieved.id());
		
		assertEquals(TEST_TREE.attributes(), retrieved.attributes());
	}
	
	@Test
	public void readAll() throws Exception {
		
		Iterator<Tree> iterator = store.get(tree());

		long count=0;
		
		while(iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertEquals(store.cardinality(),count);
		
	}
	
	@Test
	public void readSome() throws Exception {
		
		//add some noise
		TreeTemplate template = aTree().wide(8).deep(2).withValuesOf(1).build();
		
		long cardinality = store.cardinality();
		
		store.add(convert(template.generate(30)));
		
		Iterator<Tree> iterator = store.get(tree(atleast(METADATA,any())));

		int count=0;
		
		while(iterator.hasNext()) {
			Tree next = iterator.next();
			System.out.println(next);
			toStream(next,new StringWriter());
			count++;
		}
		
		assertEquals(cardinality, count);
		
	}
	
}
