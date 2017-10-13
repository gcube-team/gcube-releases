package org.gcube.data;

import static java.util.Collections.*;
import static org.gcube.data.streams.dsl.Streams.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Iterator;

import org.gcube.common.mycontainer.Gar;
import org.gcube.data.streams.Stream;
import org.gcube.data.tm.context.TReaderContext;
import org.gcube.data.tm.state.SourceHome;
import org.gcube.data.tm.state.SourceResource;
import org.gcube.data.tr.Store;
import org.gcube.data.tr.TreeSource;
import org.gcube.data.tr.neo.NeoStore;
import org.gcube.data.trees.data.Tree;

public class TestUtils {

	public static String test_scope = "/gcube/devsec";
	
	public static URL binder_url() {
		
		try {
			return new URL("http://localhost:9999");
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Gar serviceGar() {
		return new Gar(new File("src/test/resources/tree-manager-service.gar"));
	}
	
	public static TreeSource source(String id) throws Exception {
		SourceHome home = (SourceHome) TReaderContext.getContext().getLocalHome();
		SourceResource resource = (SourceResource) home.find(id);
		return (TreeSource) resource.source();
	}

	public static Store store(String id) throws Exception {
		return source(id).store();
	}
	
	public static byte[] serialise(Object t) throws Exception {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(t);
		oos.close();
		bos.close();
		
		return bos.toByteArray();
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialise(byte[] bytes) throws Exception {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (T) ois.readObject();
		
	}
	
	public static void roundtrip(Object o) throws Exception {
		
		deserialise(serialise(o));
		
	}
	
	public static NeoStore newTestStore() {
		
		try {
			NeoStore store = new NeoStore("test");
			File tempLocation = File.createTempFile("pref", "suff").getParentFile();
			store.start(tempLocation);
			return store;
		}
		catch(Exception e) {
			throw new RuntimeException("cannot start test container",e);
		}
	}

	public static NeoStore newTestStoreWith(Tree tree) {
		
		return newTestStoreWith(singleton(tree).iterator());
		
	}
	
	public static NeoStore newTestStoreWith(Iterator<Tree> trees) {
		
		try {
			NeoStore store = newTestStore();
			
			Stream<Tree> outcomes = store.add(convert(trees));
			
			//consume outcomes to pull results
			while (outcomes.hasNext())
				outcomes.next();
			
			return store;
		}
		catch(Exception e) {
			throw new RuntimeException("cannot start test container",e);
		}
	}
}
