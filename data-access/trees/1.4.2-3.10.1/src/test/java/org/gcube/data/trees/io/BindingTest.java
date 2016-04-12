package org.gcube.data.trees.io;

import static junit.framework.Assert.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.io.XMLBindings.*;

import java.io.StringReader;
import java.io.StringWriter;

import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Nodes.Attribute;
import org.gcube.data.trees.data.Tree;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

public class BindingTest {
	
	String customNS = "http://acme/org";
	String customNS2 = "http://acme2/org";
	
	Tree tree; 


	@Before
	public void setup() {

		String id="id";
		String sourceId="sourceId";
		
		Attribute noNSAttr = a("a","one");
		Attribute nsAttr = a(q(customNS,"b"),"one");
		Attribute prefixedAttr = a(q("custom",customNS2,"c"),"one");
		
		Node inner = n("nodeid", e("e","hello"));
		Node leaf = l("leafid",5);
		
		tree = attr(
					t(sourceId,id,
							e(q(customNS,"e1"),leaf),
							e(q(customNS2,"e2"),inner)),
				noNSAttr,nsAttr,prefixedAttr);
	}
	
	@Test
	public void convertStream() throws Exception {
		
		StringWriter w = new StringWriter();
		toStream(tree,w);
		Tree expected = fromStream(new StringReader(w.toString()));
		assertEquals(expected, tree);
					
	}
	
	@Test
	public void convertElement() throws Exception {
		
		Element e = toElement(tree);
		Tree expected = fromElement(e);
		assertEquals(expected, tree);
					
	}
}
